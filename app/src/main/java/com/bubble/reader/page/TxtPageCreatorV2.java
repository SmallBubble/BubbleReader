package com.bubble.reader.page;

import android.graphics.Color;
import android.text.TextPaint;

import com.bubble.common.log.BubbleLog;
import com.bubble.common.utils.Dp2PxUtil;
import com.bubble.reader.bean.PageBean;
import com.bubble.reader.bean.PageResult;
import com.bubble.reader.chapter.TxtChapterFactory;
import com.bubble.reader.chapter.listener.OnChapterListener;
import com.bubble.reader.utils.PageFactory;
import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.impl.HorizontalMoveDrawHelper;
import com.bubble.reader.widget.draw.impl.HorizontalScrollDrawHelper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class TxtPageCreatorV2 extends PageCreator {
    private static final String TAG = TxtPageCreatorV2.class.getSimpleName();
    /**
     * 解析出来的页面
     */
    private Map<String, PageBean> mPages = new HashMap<>();

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

    private File mBookFile;
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

    private TxtChapterFactory mChapterFactory;

    public TxtPageCreatorV2(PageView readView) {
        super(readView);
    }

    @Override
    protected void initData() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(Color.RED);
        mPaint.setSubpixelText(true);
        mChapterFactory = new TxtChapterFactory();
        mChapterFactory.setBookFile(mBookFile);
        mChapterFactory.initData();
        mChapterFactory.setOnChapterListener(new OnChapterListener() {
            @Override
            public void onInitialized() {

            }

            @Override
            public void onChapterLoaded() {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
//        openBook(mBookFile);
    }

    /*=======================================建造者=========================================*/
    public static class Builder extends PageCreator.Builder<Builder> {
        private File mFile;

        public Builder(PageView view) {
            super(view);
        }

        @Override
        public <C extends PageCreator> C build() {
            TxtPageCreatorV2 creator = new TxtPageCreatorV2(mReadView);
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

//    /**
//     * 打开书籍
//     *
//     * @param file 文件
//     */
//    private void openBook(File file) {
//        if (!file.exists()) {
//            // 文件不存在
//            return;
//        }
//        try {
//            mRandomFile = new RandomAccessFile(mBookFile, "r");
//            mEncoding = FileUtils.getFileEncoding(mBookFile);
//            mMapFile = mRandomFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, mBookFile.length());
//            mFileLength = mMapFile.limit();
//            mVisiblePage = getNextPageContent(0);
//            drawStatic();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (mRandomFile != null) {
//                    mRandomFile.close();
//                    mRandomFile = null;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /*=======================================对外方法阅读=========================================*/

    /**
     * 获取编码
     *
     * @return
     */
    public String getEncoding() {
        return mChapterFactory.getEncoding();
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
        // 最后一章最后一页
        if (mChapterFactory.isEnd() && mVisiblePage.isBookEnd()) {
            return mPageResult.set(false, false);
        }
        //该章节最后一页 获取下一章内容
        if (mVisiblePage.getPageCount() == mVisiblePage.getPageNum()) {
            mChapterFactory.onLoadChapter(true);
            String content = mChapterFactory.getCurrentContent();

            PageFactory.getInstance()
                    .getPages(mChapterFactory.isEnd()
                            , mChapterFactory.getCurrentName()
                            , mChapterFactory.getCurrentChapterNo()
                            , content);

        } else {
            mCancelPage = mVisiblePage;
            // 直接获取Map里面的
            String key = mChapterFactory.getCurrentName() + mChapterFactory.getCurrentChapterNo() + mVisiblePage.getPageNum();
            mVisiblePage = mPages.get(key);
        }
        return mPageResult.set(true, true);

//
//        if (mChapterPage) {
//            mCancelPage = mVisiblePage;
//            BubbleLog.e(TAG, "獲取下一頁");
//            // 设置取消页为当前可见的页 这时候往下一页方向滑动 也就是说  当滑动距离不足以翻到下一页的时候  会显示未滑动之前的页面
//            drawPage(mReadView.getCurrentPage(), mVisiblePage);
//            // 获取新的一页内容 并放到可见页上 这时候假设我们不会取消翻页
//            mVisiblePage = getNextPageContent(mVisiblePage);
//            // 绘制可见页内容到原来不可见页上 因为此时我们已经滑动 此时： 可见——>不可见  不可见——>可见
//            drawPage(mReadView.getNextPage(), mVisiblePage);
//            return mPageResult.set(true, true);
//        } else {
//            mCancelPage = mVisiblePage;
//            // 设置取消页为当前可见的页 这时候往下一页方向滑动 也就是说  当滑动距离不足以翻到下一页的时候  会显示未滑动之前的页面
//            drawPage(mReadView.getCurrentPage(), mVisiblePage);
//            // 获取新的一页内容 并放到可见页上 这时候假设我们不会取消翻页
//            mVisiblePage = getNextPageContent(mVisiblePage);
//            // 绘制可见页内容到原来不可见页上 因为此时我们已经滑动 此时： 可见——>不可见  不可见——>可见
//            drawPage(mReadView.getNextPage(), mVisiblePage);
//
//            return mPageResult.set(true, true);
//        }
    }

    @Override
    public PageResult onPrePage() {
        // 第一章第一页
        if (mChapterFactory.isStart() && mVisiblePage.isBookStart()) {
            return mPageResult.set(false, false);
        }
        return mPageResult.set(false, false);
    }


    /*=======================================往前阅读=========================================*/

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
     * 获取阅读页
     *
     * @param start 开始读取位置
     * @return
     */
    private PageBean getPage(int start, String content) {
        BubbleLog.e(TAG, "========================\n\n===================================");

        try {
            byte[] bytes = content.getBytes(getEncoding());
            if (bytes == null) {
                return null;
            }
            int length = bytes.length;

            byte lastB = bytes[0];
            // 获取一个段落
            for (int i = start; i < length; i++) {
                byte b = bytes[i];
                if (b == 0 && b == 0) {
                    break;
                }
                if (b == 10) {
                    break;
                }
                lastB = b;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取下一个段落
     *
     * @param start
     * @return
     */
    private byte[] readNextParagraph(String content, int start) {

        try {
            byte[] bytes = content.getBytes(getEncoding());
            int length = bytes.length;
            //上一个字节
            byte lastByte = 0;
            int index = start;
            while (index < length) {
                // 文件没有结束
                byte b = bytes[index];

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
            index = Math.min(index, length - 1);
            int len = index - start;
            if (len == 0) {
                // 空行 继续找
                return readNextParagraph(start + 1);
            }
            //读取找到的段落
            byte[] paragraph = new byte[len];
            for (int i = 0; i < len; i++) {
                paragraph[i] = bytes[start + i];
            }
            return paragraph;


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[];
    }
}
