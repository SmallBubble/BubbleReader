package com.bubble.reader.page.offline;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextPaint;
import android.text.TextUtils;

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
import java.util.List;

/**
 * packger：com.bubble.reader.page.offline
 * auther：Bubble
 * date：2020/6/21
 * email：1337986595@qq.com
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
    private int mFontSize = Dp2PxUtil.dip2px(64);
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
    private int mParagraphSpace = Dp2PxUtil.dip2px(30);

    private int mBackgroundColor = Color.BLUE;

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

    /**
     * 是否整章作为一页
     */
    private boolean mChapterPage;

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
            mVisiblePage = getNextPageContent(0, mChapterPage);
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

    public boolean isChapterPage() {
        return mChapterPage;
    }

    public void setChapterPage(boolean chapterPage) {
        mChapterPage = chapterPage;
    }

    @Override
    public void onCancel() {
        super.onCancel();
        mVisiblePage = mCancelPage;
        BubbleLog.e(TAG, "mCancel ====  drawStatic");
    }

    @Override
    public boolean onNextPage(int scroll) {
        if (mChapterPage) {
            if (mVisiblePage.isBookEnd()) return false;
            BubbleLog.e(TAG, "獲取下一頁");
            // 设置取消页为当前可见的页 这时候往下一页方向滑动 也就是说  当滑动距离不足以翻到下一页的时候  会显示未滑动之前的页面
            drawPage(mReadView.getCurrentPage(), mVisiblePage);
            // 获取新的一页内容 并放到可见页上 这时候假设我们不会取消翻页
            mVisiblePage = getNextPageContent((int) mVisiblePage.getPageEnd(), mChapterPage);
            // 绘制可见页内容到原来不可见页上 因为此时我们已经滑动 此时： 可见——>不可见  不可见——>可见
            drawPage(mReadView.getNextPage(), mVisiblePage);
            return true;
        } else {
            mCancelPage = mVisiblePage;
            //如果已经是书籍末尾 就结束了  没有下一页内容了
            if (mVisiblePage.isBookEnd()) return false;
            // 设置取消页为当前可见的页 这时候往下一页方向滑动 也就是说  当滑动距离不足以翻到下一页的时候  会显示未滑动之前的页面
            drawPage(mReadView.getCurrentPage(), mVisiblePage);
            // 获取新的一页内容 并放到可见页上 这时候假设我们不会取消翻页
            mVisiblePage = getNextPageContent((int) mVisiblePage.getPageEnd());
            // 绘制可见页内容到原来不可见页上 因为此时我们已经滑动 此时： 可见——>不可见  不可见——>可见
            drawPage(mReadView.getNextPage(), mVisiblePage);
            return true;
        }
    }

    @Override
    public boolean onPrePage(int scroll) {
        if (mVisiblePage.isBookStart()) return false;
        if (mChapterPage) {
            if (mVisiblePage.getChapterPage() != 0) return false;
            drawPage(mReadView.getCurrentPage(), mVisiblePage);
            mVisiblePage = getPrePageContent((int) mVisiblePage.getPageStart());
            drawPage(mReadView.getNextPage(), mVisiblePage);
            return true;
        } else {
            mCancelPage = mVisiblePage;
            drawPage(mReadView.getCurrentPage(), mVisiblePage);
            mVisiblePage = getPrePageContent((int) mVisiblePage.getPageStart());
            drawPage(mReadView.getNextPage(), mVisiblePage);
            return true;
        }
    }

    private void drawStatic() {
        drawPage(mReadView.getCurrentPage(), mVisiblePage);
        drawPage(mReadView.getNextPage(), mVisiblePage);
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
        String paragraphStr = "";
        /*********************************找出章节名称和下标*********************************/

        // 当没有到开头位置 段落不是章节 就一直往前读 知道读到章节名称
        while (start > 0 && !BookUtils.checkArticle(paragraphStr)) {
            byte[] paragraph = readPreParagraph(start);
            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            start -= paragraph.length;
        }
        BubbleLog.e(TAG, paragraphStr);
        int pageNum = 0;
        /*********************************找出章节名称和下标*********************************/

        /*********************************下面才是正式找出页面内容*********************************/
        int pageEnd = start = Math.max(start, 0);
        while (pageEnd != end) {
            page = getNextPageContent(pageEnd, mChapterPage);
            pageEnd = (int) page.getPageEnd();
            pageNum++;
        }
        /*********************************上面才是正式找出页面内容*********************************/
        if (!TextUtils.isEmpty(paragraphStr)) {
            // 找到章节了 设置章节名称
            page.setChapterName(paragraphStr);
        }
        if (start == 0) {
            page.setBookStart(true);
        }
        page.setChapterPage(pageNum);
        return page;
    }

    /**
     * 获取上一个段落内容
     *
     * @param end 结束位置 往前读
     * @return
     */
    private byte[] readPreParagraph(int end) {
        int index = end;

        while (index >= 0) {
            byte b = mMapFile.get(index);
            if (b == 10) {
                // 遇到换行符 结束
                break;
            }
            index--;
        }
        // 这里是段落内容的总长度（不包括换行符 eg： aaaaa\n   这里的长度 为5）
        int len = end - index;
        if (len == 0) {
            return readPreParagraph(index - 1);
        }
        //加上\n 的长度
        len++;
        byte[] paragraphByte = new byte[len];

        for (int i = 0; i < len; i++) {
            paragraphByte[i] = mMapFile.get(index + i + 1);
        }
        return paragraphByte;
    }


    /**
     * 获取下一页内容
     *
     * @param start       开始读取的位置
     * @param chapterPage 是否整张作为一页
     * @return
     */
    private PageBean getNextPageContent(int start, boolean chapterPage) {
        if (true) {
            //不是整章作为一页 返回分页获取内容
            return getNextPageContent(start);
        }

        BubbleLog.e(TAG, "========================\n\n===================================");
        // 创建一个页面
        PageBean page = new PageBean();
        page.setPageStart(start);
        // 结束位置
        int end = start;
        int y = mPadding;
        int contentHeight = mPadding;
//        BubbleLog.e(TAG, "mFontSize  ==  " + mFontSize + "  mLineSpace  ===   " + mLineSpace + "   mParagraphSpace ==  " + mParagraphSpace + "  mPageHeight === " + mPageHeight);
        // 当开始位置不超过文件长度 并且新增一行的高度不超过页面高度时 获取页面内容
        while (end < mFileLength) {
            // 读取一个段落
            byte[] paragraph = readNextParagraph(end);
            String paragraphStr = null;

            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (BookUtils.checkArticle(paragraphStr)) {
                // 第一段 是章节名称 就设置为页面的章节名称 和章节页码 为1
                if (end == start) {
                    page.setChapterName(paragraphStr);
                    // 设置章节页码
                    page.setChapterPage(1);
                } else {
                    // 页数加1
                    page.setPageCount(page.getPageCount() + 1);
//                    page.setChapterPage(page.getChapterPage() + 1);
//                    否则结束这个页面（因为到了另外一章）
                    break;
                }
            }
            if (!TextUtils.isEmpty(paragraphStr)) {
                //去掉空字符
                paragraphStr = paragraphStr.replaceAll("[\r\n]", "");
                // 分割段落 到页码中
                while (paragraphStr.length() > 0) {
                    // 获取一行的文字个数
                    int size = mPaint.breakText(paragraphStr, true, mContentWidth, mMeasureLineWidth);

                    String line = paragraphStr.substring(0, size);
                    // 添加到页码
                    page.getContent().add(line);
                    // 记录当前高度
                    contentHeight += mLineSpace + mFontSize;
//                    BubbleLog.e(TAG, "换行  contentHeight  ==   " + contentHeight + "   " + line);

                    // 当前行和剩余段落长度一样 说明这行就是段落结尾 并且 当前内容的高度+段间距的高度 不能超过总高度
                    if (line.length() == paragraphStr.length() && mContentHeight > contentHeight + mParagraphSpace) {
                        page.getContent().add("");
                        contentHeight += mParagraphSpace;

//                        BubbleLog.e(TAG, "换段落  contentHeight  ==   " + contentHeight + "   ");
                    }
                    // 获取剩下的内容
                    paragraphStr = paragraphStr.substring(size);
                    // 如果再加上一行就超过总高度 就不要加上了 结束这个页面
                    if (contentHeight + mLineSpace + mFontSize > mContentHeight) {
                        // 页数加1
                        page.setPageCount(page.getPageCount() + 1);
                        page.setChapterPage(page.getChapterPage() + 1);
                        if (line.length() == paragraphStr.length()) {
                            page.getContent().add("");
                            contentHeight += mParagraphSpace;
                        } else {
                            contentHeight += mLineSpace;
                        }
                        y += contentHeight;
                        contentHeight = contentHeight - mContentHeight;
                        break;
                    }
                }
            }
            if (!TextUtils.isEmpty(paragraphStr)) {
                try {
                    // 这里要注意一定要按文本的编码获取段落剩余内容的长度 否则下标会出错
                    end += paragraph.length - paragraphStr.getBytes(getEncoding()).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                end += paragraph.length;
            }
        }
        y += contentHeight;
        BubbleLog.e(TAG, "换段落  getContent" + y);
        if (page.getPageCount() == 1) {
            page.setChapterPage(1);
        }
//        if (page.getChapterPage() > 1) {// 如果当前页面不是章节首页，设置+1 页
//            page.setChapterPage(page.getChapterPage() + 1);
//        }
        page.setBookStart(start == 0);
        page.setBookEnd(end == mFileLength);
        page.setPageEnd(end);
        return page;
    }


    /**
     * 获取下一页内容
     *
     * @param start 开始读取的位置
     * @return
     */
    private PageBean getNextPageContent(int start) {
        BubbleLog.e(TAG, "========================\n\n===================================");
        // 创建一个页面
        PageBean page = new PageBean();
        page.setPageStart(start);
        // 结束位置
        int end = start;
        int contentHeight = mPadding;
//        BubbleLog.e(TAG, "mFontSize  ==  " + mFontSize + "  mLineSpace  ===   " + mLineSpace + "   mParagraphSpace ==  " + mParagraphSpace + "  mPageHeight === " + mPageHeight);
        // 当开始位置不超过文件长度 并且新增一行的高度不超过页面高度时 获取页面内容
        while (end < mFileLength && contentHeight + mFontSize + mLineSpace < mContentHeight) {
            // 读取一个段落
            byte[] paragraph = readNextParagraph(end);
            String paragraphStr = null;

            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (BookUtils.checkArticle(paragraphStr)) {
                // 第一段 是章节名称 就设置为页面的章节名称 和章节页码 为1
                if (end == start) {
                    page.setChapterName(paragraphStr);
                    // 设置章节页码
                    page.setChapterPage(1);
                } else {
//                    否则结束这个页面（因为到了另外一章）
                    break;
                }
            }
            if (!TextUtils.isEmpty(paragraphStr)) {
                //去掉空字符
                paragraphStr = paragraphStr.replaceAll("[\r\n]", "");
                // 分割段落 到页码中
                while (paragraphStr.length() > 0) {
                    // 获取一行的文字个数
                    int size = mPaint.breakText(paragraphStr, true, mContentWidth, mMeasureLineWidth);

                    String line = paragraphStr.substring(0, size);
                    // 添加到页码
                    page.getContent().add(line);
                    // 记录当前高度
                    contentHeight += mLineSpace + mFontSize;
//                    BubbleLog.e(TAG, "换行  contentHeight  ==   " + contentHeight + "   " + line);

                    // 当前行和剩余段落长度一样 说明这行就是段落结尾 并且 当前内容的高度+段间距的高度 不能超过总高度
                    if (line.length() == paragraphStr.length() && mContentHeight > contentHeight + mParagraphSpace) {
                        page.getContent().add("");
                        contentHeight += mParagraphSpace;
//                        BubbleLog.e(TAG, "换段落  contentHeight  ==   " + contentHeight + "   ");
                    }

                    // 获取剩下的内容
                    paragraphStr = paragraphStr.substring(size);
                    // 如果再加上一行就超过总高度 就不要加上了 结束这个页面
                    if (contentHeight + mLineSpace + mFontSize > mContentHeight) {
                        break;
                    }
                }
            }
            if (!TextUtils.isEmpty(paragraphStr)) {
                try {
                    // 这里要注意一定要按文本的编码获取段落剩余内容的长度 否则下标会出错
                    end += paragraph.length - paragraphStr.getBytes(getEncoding()).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                end += paragraph.length;
            }
        }
        if (page.getChapterPage() > 1) {// 如果当前页面不是章节首页，设置+1 页
            page.setChapterPage(page.getChapterPage() + 1);
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
//        BubbleLog.e(TAG, "========================\n\n===================================");
//        if (mChapterPage && pageBitmap.getType() == 1) {
//            // 当前页（已经划出屏幕）才需要回收 原来的下一页（目前的当前页不需要回收 因为内容不变）
//            // 回收原页面
//            pageBitmap.getBitmap().recycle();
//            // 创建新页面 根据章节占据页面高度*页数 获取新页面高度
//            int height = pageBean.getPageCount() * mPageHeight;
//            BubbleLog.e(TAG, "height" + height + "  getPageCount" + pageBean.getPageCount());
//            pageBitmap.setBitmap(Bitmap.createBitmap(mPageWidth, height, Bitmap.Config.ARGB_8888));
//        }
        BubbleLog.e(TAG, "contentHeight" + pageBitmap.getBitmap().getHeight());
        pageBitmap.setPageBean(pageBean);
        Canvas canvas = new Canvas(pageBitmap.getBitmap());

//        canvas.drawColor(mBackgroundColor);
        // 清除原来内容
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (pageBitmap.getType() == 1) {
            canvas.drawColor(Color.YELLOW);
        } else {
            canvas.drawColor(Color.GREEN);
        }

        int y = mFontSize + mPadding;
        for (int i = 0; i < pageBean.getContent().size(); i++) {
            String line = pageBean.getContent().get(i);
            if (line.length() > 0) {
//                BubbleLog.e(TAG, "换行 " + y + "    " + line);
                canvas.drawText(line, mPadding, y, mPaint);
                y += mFontSize + mLineSpace;
            } else {
                y += mParagraphSpace;
//                BubbleLog.e(TAG, "换段落 " + y + "   " + line);
            }
        }
//        BubbleLog.e(TAG, "换段落 drawPage" + y);
//        mPaint.setColor(Color.GREEN);
//        mPaint.setAlpha(20);
//        canvas.drawRect(new RectF(mPadding, mPadding, mContentWidth + mPadding, mContentHeight + mPadding), mPaint);
    }
}
