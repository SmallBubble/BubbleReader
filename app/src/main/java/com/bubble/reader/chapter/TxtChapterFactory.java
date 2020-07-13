package com.bubble.reader.chapter;

import com.bubble.reader.bean.TxtChapter;
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
public class TxtChapterFactory extends ChapterFactory<TxtChapter> {
    private File mBookFile;
    /**
     * 随机流
     */
    private RandomAccessFile mRandomFile;
    /**
     * 文件
     */
    private MappedByteBuffer mMapFile;

    private String mEncoding;
    private int mStartIndex = 0;

    DisposableObserver<TxtChapter> mDisposableObserver = new DisposableObserver<TxtChapter>() {
        @Override
        public void onNext(TxtChapter t) {
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            mPageListener.onBookFinished();
        }
    };
    private int mFileLength;

    private void loadChapter() {
        Observable.create(new ObservableOnSubscribe<TxtChapter>() {
            @Override
            public void subscribe(ObservableEmitter<TxtChapter> emitter) throws Exception {
                openBook(emitter, mBookFile);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDisposableObserver);
    }

    @Override
    public String getEncoding() {
        return mEncoding;
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

    /**
     * 解析章节
     *
     * @param emitter
     */
    private void parseChapter(ObservableEmitter<TxtChapter> emitter) {
        int start = mStartIndex;
        // 没有到书籍结尾 直到找到下一章节 这里只要找出end 当前章节的结束位置
        while (start < mFileLength) {
            String paragraphStr = "";
            byte[] paragraph = readNextParagraph(start);
            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (BookUtils.checkArticle(paragraphStr)) {
                break;
            }
            start += paragraph.length;
        }
        String chapterName = getChapterName();
        String content = getContent(start);

        TxtChapter chapter = new TxtChapter();
        chapter.setBookEnd(mFileLength == start);
        chapter.setBookStart(mStartIndex == 0);
        chapter.setChapterStart(mStartIndex);
        chapter.setChapterEnd(start);
        chapter.setChapterName(chapterName);
        chapter.setChapterContent(content);
        chapter.setChapterNo(++mChapterNo);

        emitter.onNext(chapter);

        mChapters.put(chapterName + mChapterNo, chapter);

    }

    private int mChapterNo = 1;

    /**
     * 获取章节内容
     *
     * @param end 结束
     * @return
     */
    private String getContent(int end) {
        StringBuilder sb = new StringBuilder();
        while (mStartIndex < mFileLength) {
            String paragraphStr = "";
            byte[] paragraph = readNextParagraph(mStartIndex);
            try {
                paragraphStr = new String(paragraph, getEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (BookUtils.checkArticle(paragraphStr)) {
                break;
            }
            sb.append(paragraphStr);
            mStartIndex += paragraph.length;
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
        if (len == 0) {
            // 空行 继续找
            return readNextParagraph(start + 1);
        }
        //读取找到的段落
        byte[] paragraph = new byte[len];
        for (int i = 0; i < len; i++) {
            paragraph[i] = mMapFile.get(start + i);
        }
        return paragraph;
    }

    @Override
    public void recycle() {
        super.recycle();
        // 取消后台读取目录操作操作
        mDisposableObserver.dispose();
    }
}
