package com.bubble.reader.page.offline;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.bubble.common.log.BubbleLog;
import com.bubble.common.utils.Dp2PxUtil;
import com.bubble.reader.creator.PageCreator;
import com.bubble.reader.page.PageBitmap;
import com.bubble.reader.page.bean.PageBean;
import com.bubble.reader.utils.BookUtils;
import com.bubble.reader.utils.FileUtils;
import com.bubble.reader.widget.PageView;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * packger：com.bubble.reader.page.offline
 * auther：Bubble
 * date：2020/6/21
 * email：jiaxiang6595@foxmail.com
 * Desc：
 */
public class OfflinePageCreator extends PageCreator {
    private static final String TAG = OfflinePageCreator.class.getSimpleName();
    /**
     * 文件长度
     */
    private int mFileLength;
    /**
     * 随机流
     */
    private RandomAccessFile mRandomFile;
    /**
     * 文件
     */
    private MappedByteBuffer mMapFile;
    /**
     * 解析出来的页面
     */
    private List<PageBean> mPages = new ArrayList<>();

    /**
     * 字体大小
     */
    private int mFontSize = Dp2PxUtil.dip2px(19);
    ;
    /**
     * 行距
     */
    private int mLineSpace = Dp2PxUtil.dip2px(12);

    /**
     * 画笔 用于测量文字
     */
    private TextPaint mPaint;
    /**
     * 段间距
     */
    private int mParagraphSpace = Dp2PxUtil.dip2px(15);

    private int mBackgroundColor = Color.WHITE;

    private File mBookFile;
    private String mEncoding;
    private float[] mMeasureLineWidth;
    /**
     * 可见页
     */
    private PageBean mVisiblePage;
    /**
     * 不可见页
     */
    private PageBean mInvisiblePage;

    /**
     * 取消页
     */
    private PageBean mCancelPage;

    public OfflinePageCreator(PageView readView) {
        super(readView);
    }

    @Override
    protected void initData() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(Color.RED);
        mPaint.setSubpixelText(true);
        mMeasureLineWidth = new float[2];

        openBook(mBookFile);
    }

    /**
     * 打开书籍
     *
     * @param file 文件
     */
    private void openBook(File file) {
        if (!file.exists()) {
            // 文件不存在
            return;
        }
        try {
            mRandomFile = new RandomAccessFile(mBookFile, "r");
            mEncoding = FileUtils.getFileEncoding(mBookFile);
            mMapFile = mRandomFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, mBookFile.length());
            mFileLength = mMapFile.limit();
            mPages.add(getNextPageContent(0));
            mPages.add(getNextPageContent((int) mPages.get(0).getPageEnd()));
            drawStatic();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mRandomFile != null) {
                    mRandomFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getEncoding() {
        return mEncoding;
    }

    /**
     * 获取上一页内容
     *
     * @param end 当前可见页的开始位置 获取上一页内容 从该位置往前读
     * @return
     */
    private PageBean getPrePageContent(int end) {
        PageBean page = new PageBean();
        page.setPageEnd(end - 1);


        // 新页的开始位置
        int start = end;

        while (start > 0) {
            byte[] paragraph = readPreParagraph(start);

            start--;

        }

        return page;
    }

    /**
     * 获取上一个段落内容
     *
     * @param end 结束位置 往前读
     * @return
     */
    private byte[] readPreParagraph(int end) {
        byte lastB = 0;
        int index = end;

        while (index >= 0) {
            byte b = mMapFile.get(index);
            if (b == 10) {
                // 遇到换行符 结束
                break;
            }
            index--;
            lastB = b;
        }

        mMapFile.get();

        int len = index - end;
        if (len == 0) {

        }
        byte[] paragraphByte = new byte[len];

        for (int i = 0; i < len; i++) {
            paragraphByte[i] = mMapFile.get(index + i);
        }
        return paragraphByte;
    }

    /**
     * 获取下一页内容
     *
     * @param start 开始读取的位置
     * @return
     */
    private PageBean getNextPageContent(int start) {
        // 创建一个页面
        PageBean page = new PageBean();
        page.setPageStart(start);
        // 结束位置
        int end = start;
        int contentHeight = 0;
        // 当开始位置不超过文件长度 并且新增一行的高度不超过页面高度时 获取页面内容
        while (end < mFileLength && contentHeight + mFontSize < mPageHeight) {
            // 读取一个段落
            byte[] paragraph = readNextParagraph(end);
            String paragraphStr = null;

            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (BookUtils.checkArticle(paragraphStr)) {
                page.setChapterName(paragraphStr);
            }
            if (!TextUtils.isEmpty(paragraphStr)) {
                //去掉空字符
                paragraphStr = paragraphStr.replaceAll("[\r\n]", " ");
                // 分割段落 到页码中
                while (paragraphStr.length() > 0) {
                    // 获取一行的文字个数
                    int size = mPaint.breakText(paragraphStr, true, mPageWidth, mMeasureLineWidth);
                    Log.e(TAG, "mMeasureLineWidth " + Arrays.toString(mMeasureLineWidth));
                    Log.e(TAG, "mPageWidth  size" + size);
                    Log.e(TAG, "mPageWidth  " + mPageWidth);

                    Log.e(TAG, "mPageWidth  " + page.getContent().size() + "  " + (mPageWidth < mMeasureLineWidth[0]));
                    String line = paragraphStr.substring(0, mMeasureLineWidth[0] > mPageWidth ? size - 1 : size);
                    Log.e(TAG, "mPageWidth  width" + mPaint.measureText(line));
                    // 添加到页码
                    page.getContent().add(line);
                    // 记录当前高度
                    contentHeight += mLineSpace + mFontSize;
                    // 获取剩下的内容
                    paragraphStr = paragraphStr.substring(size);
                }
                page.getContent().add("");
                contentHeight += mParagraphSpace;
            }
            end += paragraph.length;
        }
        page.setBookStart(start == 0);
        page.setBookEnd(end == mFileLength);
        page.setPageEnd(end);
        return page;
    }

    /**
     * 读取下一个段落
     *
     * @param start
     * @return
     */
    private byte[] readNextParagraph(int start) {
        //上一个字节
        byte lastByte = 0;
        int index = start;
        while (index < mFileLength) {
            // 文件没有结束
            byte b = mMapFile.get(index);

            if (lastByte == 0 && b == 0) {
                // 连续两个空格
                break;
            }
            // 换行符 读取段落完成
            if (b == 10) {
                break;
            }
            // 往后移一位
            index++;
            // 记录当前字节
            lastByte = b;
        }
        // 如果超过文件大小 结尾使用文件长度
        index = Math.min(index, mFileLength - 1);
        int len = index - start;
        if (len == 0) {// 空行 继续找
            return readNextParagraph(start + 1);
        }
        //读取找到的段落
        byte[] paragraph = new byte[len];
        for (int i = 0; i < len; i++) {
            paragraph[i] = mMapFile.get(start + i);
        }
        return paragraph;
    }


    private void drawPage(PageBitmap pageBitmap, PageBean pageBean) {
        Log.e(TAG, "  ");
        Log.e(TAG, "drawPage  ");
        Log.e(TAG, "drawPage  ");
        Log.e(TAG, "drawPage  ");
        Log.e(TAG, "drawPage  ");
        Log.e(TAG, "  ");
        Canvas canvas = new Canvas(pageBitmap.getBitmap());
        canvas.drawColor(mBackgroundColor);
        int y = mFontSize;
        for (int i = 0; i < pageBean.getContent().size(); i++) {
            String line = pageBean.getContent().get(i);

            int size = mPaint.breakText(line, true, mPageWidth, mMeasureLineWidth);
            Log.e(TAG, "mMeasureLineWidth " + Arrays.toString(mMeasureLineWidth));
            Log.e(TAG, "mPageWidth  " + mPageWidth);
            Log.e(TAG, "mPageWidth size " + size);
            Log.e(TAG, "mPageWidth  " + i + "  " + (mPageWidth < mMeasureLineWidth[0]));
            Log.e(TAG, "mPageWidth  width" + mPaint.measureText(line));
            if (line.trim().length() > 0) {
                mPaint.setColor(Color.RED);
                canvas.drawText(line, mPadding, y, mPaint);
                y += mFontSize + mLineSpace;
                BubbleLog.e(TAG, "换行 " + y);
            } else {
                y += mParagraphSpace;
                BubbleLog.e(TAG, "换段落 " + y);
            }
        }
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(20);
        canvas.drawRect(new RectF(mPadding, 0, mPageWidth + mPadding, mPageHeight), mPaint);
//　　人们说，玄山派内门无名弟
//        　　人们说，人们说那一战玄山
    }

    private int index = 1;

    @Override
    public boolean onNextPage() {
        //如果已经是书籍末尾 就结束了  没有下一页内容了
        if (mVisiblePage.isBookEnd()) return false;
        index++;
        // 设置取消页为当前可见的页 这时候往下一页方向滑动 也就是说  当滑动距离不足以翻到下一页的时候  会显示未滑动之前的页面
        mCancelPage = mVisiblePage;
        drawPage(mReadView.getCurrentPage(), mVisiblePage);
        // 获取新的一页内容 并放到可见页上 这时候假设我们不会取消翻页
        mVisiblePage = getNextPageContent(0);
        // 绘制可见页内容到原来不可见页上 因为此时我们已经滑动 此时： 可见——>不可见  不可见——>可见
        drawPage(mReadView.getNextPage(), mVisiblePage);
        return true;
    }

    @Override
    public boolean onPrePage() {
        if (mVisiblePage.isBookStart()) return false;
        mCancelPage = mVisiblePage;
        drawPage(mReadView.getCurrentPage(), mVisiblePage);
        mVisiblePage = getPrePageContent((int) mVisiblePage.getPageStart());
        drawPage(mReadView.getNextPage(), mVisiblePage);
        return true;
    }

    private void drawStatic() {
        if (mPages.size() > 0) {
            drawPage(mReadView.getCurrentPage(), mPages.get(0));
            drawPage(mReadView.getNextPage(), mPages.get(1));
        }
    }

    public static class Builder extends PageCreator.Builder<Builder> {
        private File mFile;

        public Builder(PageView view) {
            super(view);
        }

        @Override
        public <C extends PageCreator> C build() {
            OfflinePageCreator creator = new OfflinePageCreator(mReadView);
            creator.setBookFile(mFile);
            return (C) creator;
        }

        public Builder file(String path) {
            return file(new File(path));
        }

        public Builder file(File file) {
            mFile = file;
            return this;
        }

    }

    private void setBookFile(File bookFile) {
        mBookFile = bookFile;
    }


}
