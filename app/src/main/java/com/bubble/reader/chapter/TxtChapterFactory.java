package com.bubble.reader.chapter;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.bean.TxtChapter;
import com.bubble.reader.chapter.listener.OnChapterRequestListener;
import com.bubble.reader.chapter.listener.OnChapterResultListener;
import com.bubble.reader.utils.BookUtils;
import com.bubble.reader.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Bubble
 * @date 2020/7/13
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc Txt文件章节工厂
 */
public class TxtChapterFactory extends ChapterFactory<TxtChapter> implements OnChapterRequestListener<TxtChapter> {
    private static final String TAG = TxtChapterFactory.class.getSimpleName();
    /**
     * 要读取得txt文件
     */
    private File mBookFile;
    /**
     * 随机流
     */
    private RandomAccessFile mRandomFile;
    /**
     * 文件
     */
    private MappedByteBuffer mMapFile;
    /**
     * 文件编码
     */
    private String mEncoding;
    /**
     * 开始下标
     */
    private int mStartIndex = 0;
    /**
     * 文件长度
     */
    private int mFileLength;
    /**
     * 章节编号
     */
    private int mChapterNo = 0;
    /***
     * 用来回收资源的时候 停止请求
     */
    private DisposableObserver<TxtChapter> mDisposableObserver = new DisposableObserver<TxtChapter>() {
        @Override
        public void onNext(TxtChapter chapter) {
            if (!mInitialized) {
                // 第一次初始化 回调初始化
                mInitialized = true;
                mCurrentChapter = chapter;
                mOnChapterListener.onInitialized();
            } else {
                mOnChapterListener.onChapterLoaded();
            }
        }

        @Override
        public void onError(Throwable e) {
            mOnChapterListener.onError(e);
        }

        @Override
        public void onComplete() {
            mOnChapterListener.onComplete();
        }
    };

    public void setBookFile(File file) {
        mBookFile = file;
    }

    public TxtChapterFactory() {
        /***
         * 章节获取监听 这里直接用自己作为章节获取的实现类 只需要在自己里面实现逻辑即可
         */
        mOnChapterRequestListener = this;
    }

    @Override
    public String getEncoding() {
        return mEncoding;
    }

    @Override
    public void initData() {
        super.initData();
        openBook();
    }

    @Override
    public void recycle() {
        super.recycle();
        // 取消子线程读取目录的操作
        mDisposableObserver.dispose();
    }

    /*****************************************章节获取*****************************************/
    @Override
    public void onRequest(boolean isPrepare, int currentIndex, OnChapterResultListener<TxtChapter> listener) {
        TxtChapter chapter = parseChapter(mCurrentChapter.getChapterStart());
        if (chapter == null) {
            listener.onGetChapterFailure(isPrepare, "解析失败");
        } else {
            listener.onGetChapterSuccess(isPrepare, chapter);
        }
    }

    /**
     * 打开书籍
     */
    private void openBook() {
        Observable.create(new ObservableOnSubscribe<TxtChapter>() {
            @Override
            public void subscribe(ObservableEmitter<TxtChapter> emitter) throws Exception {
                openBook(emitter, mBookFile);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDisposableObserver);
    }

    /**
     * 打开书籍
     *
     * @param emitter
     * @param file    文件
     */
    private void openBook(ObservableEmitter<TxtChapter> emitter, File file) {
        if (!file.exists()) {
            // 文件不存在
            return;
        }
        try {
            mRandomFile = new RandomAccessFile(mBookFile, "r");
            mEncoding = FileUtils.getFileEncoding(mBookFile);
            mMapFile = mRandomFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, mBookFile.length());
            mFileLength = mMapFile.limit();
            parseChapter(emitter);
            // 解析完成
            emitter.onComplete();
            BubbleLog.e(TAG, "解析成功" + Thread.currentThread().getName());
        } catch (Exception e) {
            emitter.onError(e);
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

    private TxtChapter parseChapter(int start) {
        // 没有到书籍结尾 直到找到下一章节 这里只要找出end 当前章节的结束位置
        String paragraphStr = "";
        while (start < mFileLength) {
            byte[] paragraph = readNextParagraph(start);
            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (BookUtils.checkArticle(paragraphStr)) {
                // 找到一个章节
                String chapterName = getChapterName();
                String content = getContent(mStartIndex);
                mChapterNo++;
                TxtChapter chapter = new TxtChapter();
                chapter.setBookEnd(mFileLength == start);
                chapter.setBookStart(mStartIndex == 0);
                chapter.setChapterStart(mStartIndex);
                chapter.setChapterEnd(start);
                chapter.setChapterName(chapterName);
                chapter.setChapterContent(content);
                chapter.setChapterNo(mChapterNo);
                BubbleLog.e(TAG, chapter.getChapterName() + "     " + chapter.getChapterStart() + "   " + chapter.getChapterEnd());
                return chapter;
            }
            start += paragraph.length;
        }
        return null;
    }

    /**
     * 解析章节
     *
     * @param emitter
     */
    private void parseChapter(ObservableEmitter<TxtChapter> emitter) {
        int start = mStartIndex;
        // 没有到书籍结尾 直到找到下一章节 这里只要找出end 当前章节的结束位置
        String paragraphStr = "";
        while (start < mFileLength) {
            byte[] paragraph = readNextParagraph(start);
            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (BookUtils.checkArticle(paragraphStr)) {
                // 找到一个章节
                String chapterName = getChapterName();
                String content = getContent(mStartIndex);
                mChapterNo++;
                TxtChapter chapter = new TxtChapter();
                chapter.setBookEnd(mFileLength == start);
                chapter.setBookStart(mStartIndex == 0);
                chapter.setChapterStart(mStartIndex);
                chapter.setChapterEnd(start);
                chapter.setChapterName(chapterName);
                chapter.setChapterContent(content);
                chapter.setChapterNo(mChapterNo);
                //通知
                emitter.onNext(chapter);
                // 添加进map 存起来 下次直接读取
                mChapters.put(chapterName + mChapterNo, chapter);

//                BubbleLog.e(TAG, chapter.getChapterName() + Thread.currentThread().getName());
                mStartIndex = start;
            }
            start += paragraph.length;
        }
    }

    /**
     * 获取章节内容
     *
     * @param end 结束
     * @return
     */
    private String getContent(int end) {
        StringBuilder sb = new StringBuilder();
        String paragraphStr = "";
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
            sb.append(paragraphStr);
            end += paragraph.length;
        }
        return sb.toString();
    }

    /**
     * 获取章节名称
     *
     * @return
     */
    private String getChapterName() {
        byte[] paragraph = readNextParagraph(mStartIndex);
        String chapterName = "";
        try {
            chapterName = new String(paragraph, getEncoding());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mStartIndex += paragraph.length;
        return chapterName;
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
        byte b = 0;
        int index = start;
        while (index < mFileLength) {
            // 文件没有结束
            b = mMapFile.get(index);
            // 往后移一位
            index++;
            if (lastByte == 0 && b == 0) {
                // 连续两个空格
                break;
            }
            // 换行符 读取段落完成
            if (b == 10) {
                break;
            }
            // 记录当前字节
            lastByte = b;
        }
        // 如果超过文件大小 结尾使用文件长度
        index = Math.min(index, mFileLength);
        int len = Math.max(index - start, 0);
        //读取找到的段落
        byte[] paragraph = new byte[len];
        for (int i = 0; i < len; i++) {
            paragraph[i] = mMapFile.get(start + i);
        }
        return paragraph;
    }

    public static class Builder extends ChapterFactory.Builder<Builder> {
        private File mBookFile;

        public Builder() {
            super();
        }

        @Override
        public <C extends ChapterFactory> C build() {
            TxtChapterFactory factory = new TxtChapterFactory();
            factory.setBookFile(mBookFile);
            return (C) factory;
        }

        public Builder file(File file) {
            mBookFile = file;
            return this;
        }

        public Builder file(String path) {
            return file(new File(path));
        }

    }
}