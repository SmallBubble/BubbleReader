package com.bubble.reader.page;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextPaint;
import android.text.TextUtils;

import com.bubble.common.log.BubbleLog;
import com.bubble.common.utils.Dp2PxUtil;
import com.bubble.reader.bean.PageBitmap;
import com.bubble.reader.bean.PageBean;
import com.bubble.reader.bean.PageResult;
import com.bubble.reader.utils.BookUtils;
import com.bubble.reader.utils.FileUtils;
import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.impl.HorizontalMoveDrawHelper;
import com.bubble.reader.widget.draw.impl.HorizontalScrollDrawHelper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class TxtPageCreator extends PageCreator {
    private static final String TAG = TxtPageCreator.class.getSimpleName();
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

    public TxtPageCreator(PageView readView) {
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

    /*=======================================建造者=========================================*/
    public static class Builder extends PageCreator.Builder<Builder> {
        private File mFile;

        public Builder(PageView view) {
            super(view);
        }

        @Override
        public <C extends PageCreator> C build() {
            TxtPageCreator creator = new TxtPageCreator(mReadView);
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

    /*=======================================私有方法阅读=========================================*/

    /**
     * 设置要阅读的文件
     *
     * @param bookFile
     * @version v1 只支持txt
     */
    private void setBookFile(File bookFile) {
        mBookFile = bookFile;
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
            mVisiblePage = getNextPageContent(0);
            drawStatic();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mRandomFile != null) {
                    mRandomFile.close();
                    mRandomFile = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*=======================================对外方法阅读=========================================*/

    /**
     * 获取编码
     *
     * @return
     */
    public String getEncoding() {
        return mEncoding;
    }

    public boolean isChapterPage() {
        return mChapterPage;
    }

    /**
     * 设置
     *
     * @param chapterPage
     */
    public void setChapterPage(boolean chapterPage) {
        mChapterPage = chapterPage;
    }

    /**
     * 取消翻页
     * 回复原来的页面
     * 以下两个帮助类中 会通过{@link PageView#mDrawHelper }中的监听回调 调用该方法
     * <p>
     * {@link HorizontalMoveDrawHelper }
     * <p>
     * {@link HorizontalScrollDrawHelper}
     */
    @Override
    public void onCancel() {
        super.onCancel();
        mVisiblePage = mCancelPage;
        BubbleLog.e(TAG, "mCancel ====  drawStatic");
    }


    @Override
    public PageResult onNextPage() {
        if (mChapterPage) {
            if (mVisiblePage.isBookEnd()) {
                return mPageResult.set(false, false);
            }
            mCancelPage = mVisiblePage;
            BubbleLog.e(TAG, "獲取下一頁");
            // 设置取消页为当前可见的页 这时候往下一页方向滑动 也就是说  当滑动距离不足以翻到下一页的时候  会显示未滑动之前的页面
            drawPage(mReadView.getCurrentPage(), mVisiblePage);
            // 获取新的一页内容 并放到可见页上 这时候假设我们不会取消翻页
            mVisiblePage = getNextPageContent(mVisiblePage);
            // 绘制可见页内容到原来不可见页上 因为此时我们已经滑动 此时： 可见——>不可见  不可见——>可见
            drawPage(mReadView.getNextPage(), mVisiblePage);
            return mPageResult.set(true, true);
        } else {
            mCancelPage = mVisiblePage;
            //如果已经是书籍末尾 就结束了  没有下一页内容了
            if (mVisiblePage.isBookEnd()) {
                return mPageResult.set(false, false);
            }
            // 设置取消页为当前可见的页 这时候往下一页方向滑动 也就是说  当滑动距离不足以翻到下一页的时候  会显示未滑动之前的页面
            drawPage(mReadView.getCurrentPage(), mVisiblePage);
            // 获取新的一页内容 并放到可见页上 这时候假设我们不会取消翻页
            mVisiblePage = getNextPageContent(mVisiblePage);
            // 绘制可见页内容到原来不可见页上 因为此时我们已经滑动 此时： 可见——>不可见  不可见——>可见
            drawPage(mReadView.getNextPage(), mVisiblePage);

            return mPageResult.set(true, true);
        }
    }

    @Override
    public PageResult onPrePage() {
        if (mVisiblePage.isBookStart()) {
            // 如果当前页是书籍开始
            return mPageResult.set(false, false);
        }
        if (mChapterPage) {
            mCancelPage = mVisiblePage;
            drawPage(mReadView.getCurrentPage(), mVisiblePage);
            mVisiblePage = getPrePageContent(mVisiblePage);
            drawPage(mReadView.getNextPage(), mVisiblePage);
            return mPageResult.set(true, true);
        } else {
            mCancelPage = mVisiblePage;
            drawPage(mReadView.getCurrentPage(), mVisiblePage);
            mVisiblePage = getPrePageContent(mVisiblePage);
            drawPage(mReadView.getNextPage(), mVisiblePage);
            return mPageResult.set(true, true);
        }
    }




    /*=======================================往前阅读=========================================*/

    private PageBean getPrePageContent(PageBean pageBean) {
        if (pageBean.getPageNum() == 1) {
            // 章节第一页内容
            PageBean page = getPrePageContent(pageBean.getPageStart());
            page.setChapterEnd(page.getPageEnd());
            return page;
        } else {
            //上一页的页号
            int pageNum = 0;
            // 当前页所在章节开始位置
            int pageStart = pageBean.getChapterStart();
            PageBean page = null;
            // 从章节开始位置往后找 找到当前页的上一页的内容
            while (pageStart != pageBean.getPageStart()) {
                PageBean tempPage = getPage(pageStart);
                pageStart = tempPage.getPageEnd();
                pageNum++;
                if (pageBean.getPageNum() - 1 == pageNum) {
                    page = tempPage;
                    page.copyField(pageBean.getChapterName(), pageBean.getChapterStart(), pageBean.getChapterEnd(), pageBean.getPageCount(), pageNum);
                }
            }
            return page;
        }
    }

    /**
     * 获取上一页内容
     *
     * @param end 当前可见页的开始位置 获取上一页内容 从该位置往前读
     * @return
     */
    private PageBean getPrePageContent(int end) {
        // 新页的开始位置
        int start = end;
        String paragraphStr = "";
        String chapterName = "";
        /*------------------找出章节名称和下标------------------*/
        // 当没有到书籍开头&&段落不是章节，就一直往前读，直到找到前面一章的开头为止。
        while (start > 0 && !BookUtils.checkArticle(paragraphStr)) {
            byte[] paragraph = readPreParagraph(start);
            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            start -= paragraph.length;
        }
        chapterName = paragraphStr;
        // 这个是找到的章节名称
        BubbleLog.e(TAG, paragraphStr);
        /*------------------找出章节名称和下标------------------*/
        /*------------------下面才是正式找出页面内容------------------*/
        // 页号为0 每当找到一页 加一
        int pageCount = 0;

        /* start 为上面找到章节的位置 和0比较 取大的那个
         * pageEnd是 上一页的开始位置 这里默认是当前章节的开始位置（上一个章节最后一页的结束位置）
         */
        int pageEnd = start = Math.max(start, 0);
        /*
         *  循环找出需要的阅读页(就是当前显示的阅读页的上一页 end为当前页的开始位置 )
         *  就是说当我们while获取到的page 结束位置 等于我们传过来的end位置时 page就是 mVisiblePage 的上一页
         */
        PageBean page = new PageBean();
        while (pageEnd != end) {
            PageBean tempPage = getPage(pageEnd);
            pageEnd = tempPage.getPageEnd();
            if (tempPage.getContent().size() <= 0) {
                continue;
            }
            pageCount++;
            tempPage.setPageNum(pageCount);
            page = tempPage;
        }
        // 设置章节开始位置
        page.setChapterStart(start);
        // 设置页号
        page.setPageCount(pageCount);
        /*------------------上面才是正式找出页面内容------------------*/
        // 设置章节名称
        page.setChapterName(chapterName);
        // 如果下标为0 设置为书籍开始页
        page.setBookStart(start == 0);
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


    /*=======================================往后阅读=========================================*/

    /**
     * 获取下一页内容  根据当前可见页来获取下一页内容
     *
     * @param pageBean 当前可见页
     * @return 返回下一页内容
     */
    private PageBean getNextPageContent(PageBean pageBean) {
        if (pageBean.getPageCount() == pageBean.getPageNum()) {
            // 如果是当前章节的最后一页
            // 需要重新获取下一章的内容
            PageBean page = getNextPageContent(pageBean.getChapterEnd());
            // 设置章节开始位置
            page.setChapterStart(pageBean.getChapterEnd());
            return page;
        } else {
            // 不是最后一页 直接获取阅读页
            PageBean page = getPage(pageBean.getPageEnd());
            // 因为是同一章节的 这四个属性（页号+1）是一样的 直接复制给新的page
            page.copyField(pageBean.getChapterName(), pageBean.getChapterStart(), pageBean.getChapterEnd(), pageBean.getPageCount(), pageBean.getPageNum() + 1);
            return page;
        }
    }

    /**
     * 获取下一页内容和该页所在的章节信息（章节名称 章节页数）
     * <p>
     * 获取下一页内容 (一个章节只需要调用一次即可)
     * <p>
     * 这里不需要传章节开始位置过来 因为必然是章节开始才会调用此方法（start即是章节开始位置 也是 此章节第一页的开始位置）
     *
     * @param start 开始读取的位置
     * @return 返回从 start 开始的下一页阅读页
     */
    private PageBean getNextPageContent(int start) {
        // 新页的开始位置
        int end = start;
        String paragraphStr = "";
        /*------------------找出章节名称和下标------------------*/
        //先获取本章的章节名称
        String chapterName = "";
        byte[] chapter = readNextParagraph(end);
        try {
            chapterName = new String(chapter, getEncoding());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        end += chapter.length;

        // 没有到书籍结尾 直到找到下一章节 这里只要找出end 当前章节的结束位置
        while (end < mFileLength) {
            byte[] paragraph = readNextParagraph(end);
            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (BookUtils.checkArticle(paragraphStr)) {
                break;
            }
            end += paragraph.length;
        }
        // 这个是下一个章节名称
        BubbleLog.e(TAG, paragraphStr);
        /*------------------找出章节名称和下标------------------*/
        /*------------------下面才是正式找出页面内容------------------*/
        // 当前章节的页数
        int pageCount = 0;
        /* end 为上面找到章节的位置 和文件长度比较 取小的那个
         * pageEnd 是 上一页的开始位置 这里默认是当前章节的开始位置（上一个章节最后一页的结束位置）
         */
        end = Math.min(end, mFileLength);
        /*
         *  循环找出需要的阅读页(就是当前显示的阅读页的上一页 end为当前页的开始位置 )
         *  就是说当我们while获取到的page 结束位置 等于我们传过来的end位置时 page就是 mVisiblePage 的上一页
         */
        PageBean page = new PageBean();
        int pageEnd = start;
        // 这里主要是获取页数
        while (pageEnd != end) {
            PageBean tempPage = getPage(pageEnd);
            pageEnd = tempPage.getPageEnd();
            if (tempPage.getContent().size() <= 0) {
                continue;
            }
            pageCount++;
            tempPage.setPageNum(pageCount);
            // 如果是我们要找的章节
            if (tempPage.getPageStart() == start) {
                page = tempPage;
            }
        }
        // 设置章节结尾位置
        page.setChapterEnd(end);
        // 设置页号
        page.setPageCount(pageCount);
        /*------------------上面才是正式找出页面内容------------------*/
        // 设置章节名称
        page.setChapterName(chapterName);
        // 如果下标为0 设置为书籍开始页
        page.setBookEnd(end == mFileLength);
        return page;
    }

    /**
     * 获取阅读页
     *
     * @param start 开始读取位置
     * @return
     */
    private PageBean getPage(int start) {
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
                    page.setPageNum(1);
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
        if (page.getPageNum() > 1) {// 如果当前页面不是章节首页，设置+1 页
            page.setPageNum(page.getPageNum() + 1);
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

    /*=======================================绘制内容=========================================*/

    /**
     * 绘制静止状态下的页面
     */
    private void drawStatic() {
        drawPage(mReadView.getCurrentPage(), mVisiblePage);
        drawPage(mReadView.getNextPage(), mVisiblePage);
    }

    /**
     * 绘制页面
     *
     * @param pageBitmap 要绘制的bitmap 通过{@link PageBitmap#getBitmap()}获取
     * @param pageBean   要绘制的内容
     */
    private void drawPage(PageBitmap pageBitmap, PageBean pageBean) {
//        BubbleLog.e(TAG, "========================\n\n===================================");
        BubbleLog.e(TAG, "contentHeight" + pageBitmap.getBitmap().getHeight());
        pageBitmap.setPageBean(pageBean);
        Canvas canvas = new Canvas(pageBitmap.getBitmap());
        // 清除原来内容
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

//        if (pageBitmap.getType() == 1) {
//            canvas.drawColor(Color.YELLOW);
//        } else {
//            canvas.drawColor(Color.GREEN);
//        }

//        canvas.drawColor(mBackgroundColor);

        int y = mFontSize + mPadding;
        for (int i = 0; i < pageBean.getContent().size(); i++) {
            String line = pageBean.getContent().get(i);
            if (line.length() > 0) {
                canvas.drawText(line, mPadding, y, mPaint);
                y += mFontSize + mLineSpace;
            } else {
                y += mParagraphSpace;
            }
        }
    }
}
