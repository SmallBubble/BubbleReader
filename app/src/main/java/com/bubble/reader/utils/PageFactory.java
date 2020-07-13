package com.bubble.reader.utils;

import android.graphics.Paint;
import android.text.TextUtils;

import com.bubble.reader.bean.PageBean;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Bubble
 * @date 2020/7/13
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc TODO
 */
public class PageFactory {
    private static PageFactory sPageFactory;

    private int mHeight;
    private int mWidth;
    private int mFontSize;
    private int mLineSpace;
    private int mParagraphLine;
    private String mEncoding;

    private Paint mPaint;

    private PageFactory() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * 改变页面配置
     *
     * @param height        高度
     * @param width         宽度
     * @param fontSize      字体大小
     * @param lineSpace     行距
     * @param paragraphLine 段距
     */
    public void changePageConfig(int height, int width, int fontSize, int lineSpace, int paragraphLine) {
        mHeight = height;
        mWidth = width;
        mFontSize = fontSize;
        mLineSpace = lineSpace;
        mParagraphLine = paragraphLine;

        mPaint.setTextSize(mFontSize);
    }

    public static PageFactory getInstance() {
        if (sPageFactory == null) {
            synchronized (PageFactory.class) {
                if (sPageFactory == null) {
                    sPageFactory = new PageFactory();
                }
            }
        }
        return sPageFactory;
    }


    /**
     * 分页
     *
     * @param chapterName 要分页的章节名称
     * @param content     要分页的内容
     * @return
     */
    public Map<String, PageBean> getPages(boolean isEnd, String chapterName, int chapterNo, String content) {
        Map<String, PageBean> pages = new HashMap<>();
        int pageCount = 0;
        String key = "";
        int pageNum = 1;
        try {
            byte[] bytes = content.getBytes(mEncoding);
            int length = bytes.length;
            int start = 0;
            int contentHeight = 0;
            PageBean pageBean = new PageBean();
            while (!checkHeight(contentHeight) && start < length) {
                byte[] paragraph = getParagraph(bytes, start);
                String paragraphStr = "";
                paragraphStr = new String(paragraph);
                // 如果是章节
                if (BookUtils.checkArticle(paragraphStr)) {

                }
                // 段落不为空
                while (!TextUtils.isEmpty(paragraphStr)) {
                    // 获取一行
                    int size = mPaint.breakText(paragraphStr, true, mWidth, null);
                    String line = paragraphStr.substring(0, size);
                    pageBean.getContent().add(line);
                    // 每添加一行 需要加一个字体大小好行间距
                    contentHeight += mLineSpace + mFontSize;

                    // 如果这时候的高度再加上一行就超过页面了 就完成一行了
                    if (checkHeight(contentHeight)) {
                        break;
                    }
                    paragraphStr = paragraphStr.substring(size);

                }


                if (checkHeight(contentHeight)) {
                    pageCount++;
                    pageNum++;
                    contentHeight = 0;
                    pageBean.setChapterStart(start);
                    pageBean.setChapterName(chapterName);
                    pageBean.setPageStart(start);
                    pageBean.setBookEnd(isEnd);
                    pageBean.setPageNum(pageNum);
                    key = chapterName + "_" + chapterNo + "_" + pageNum;
                    pages.put(key, pageBean);

                    // 重新創建一個新的頁面
                    pageBean = new PageBean();
                }

                start += paragraph.length;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (pages.isEmpty()) {
            Set<Map.Entry<String, PageBean>> entries = pages.entrySet();

            for (Map.Entry<String, PageBean> page : entries) {
//                page.getValue().setPageCount();
            }
        }
        return pages;
    }

    private byte[] getParagraph(byte[] bytes, int start) {

        byte lastB = 0;
        int index = start;
        while (index < bytes.length) {
            byte b = bytes[index];
            if (checkLineBreak(b, lastB)) {
                break;
            }
            index++;
            lastB = b;
        }

        byte[] paragraph = new byte[index - start];
        for (int i = 0; i < paragraph.length; i++) {
            paragraph[i] = bytes[i + start];
        }
        return paragraph;
    }

    /**
     * 检查高度是否超过一页
     *
     * @param height
     * @return
     */
    private boolean checkHeight(int height) {
        return height + mFontSize > mHeight;
    }

    /**
     * 检查是否换行
     *
     * @param b
     * @param lastB
     * @return
     */
    private boolean checkLineBreak(byte b, byte lastB) {
        return b == 10 || (lastB == 0 && b == 0);
    }

}
