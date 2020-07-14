package com.bubble.reader.utils;

import android.graphics.Paint;
import android.text.TextUtils;

import com.bubble.reader.bean.PageBean;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int mParagraphSpace;
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
        mParagraphSpace = paragraphLine;

        mPaint.setTextSize(mFontSize);
    }


    public PageFactory height(int height) {
        mHeight = height;
        return getInstance();
    }

    public PageFactory width(int width) {
        mWidth = width;
        return getInstance();
    }

    public PageFactory fontSize(int fontSize) {
        mFontSize = fontSize;
        return getInstance();
    }

    public PageFactory lineSpace(int lineSpace) {
        mLineSpace = lineSpace;
        return getInstance();
    }

    public PageFactory paragraphSpace(int paragraphLine) {
        mParagraphSpace = paragraphLine;
        return getInstance();
    }

    public PageFactory setEncoding(String encoding) {
        mEncoding = encoding;
        return getInstance();
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
    public List<PageBean> createPages(String chapterName, int chapterNo, String content) {
        List<PageBean> pages = new ArrayList<>();
        int pageCount = 0;
        int pageNum = 0;
        try {
            byte[] bytes = content.getBytes(mEncoding);
            int length = bytes.length;
            int start = 0;
            int contentHeight = 0;
            PageBean pageBean = new PageBean();
            while (start < length) {
                byte[] paragraph = getParagraph(bytes, start);
                String paragraphStr = "";
                paragraphStr = new String(paragraph, mEncoding);
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
                /**
                 * 超过高度并且 内容不为空
                 */
                if (checkHeight(contentHeight) && !pageBean.getContent().isEmpty()) {
                    // 也数量和页号加1
                    pageCount++;
                    pageNum++;
                    // 因为读取了一页 高度置为0
                    contentHeight = 0;
                    // 设置页面信息
                    pageBean.setChapterName(chapterName);
                    pageBean.setPageNum(pageNum);
                    pageBean.setChapterNo(chapterNo);
                    pages.add(pageBean);
                    // 重新創建一個新的頁面
                    pageBean = new PageBean();
                }

                // 判断段落是否剩余文本
                if (TextUtils.isEmpty(paragraphStr)) {
                    // 没有剩余 刚好到一页结尾
                    start += paragraph.length;
                } else {
                    // 有剩余 需要加到下一页
                    start += paragraphStr.getBytes(mEncoding).length;
                }
            }
            /**
             * 超过高度并且 内容不为空
             */
            if (!pageBean.getContent().isEmpty()) {
                // 也数量和页号加1
                pageCount++;
                pageNum++;
                // 设置页面信息
                pageBean.setChapterName(chapterName);
                pageBean.setPageNum(pageNum);
                pageBean.setChapterNo(chapterNo);
                pages.add(pageBean);
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 章节所有的页面都生成出来以后 统一设置 页面数量
        if (!pages.isEmpty()) {
            for (PageBean bean : pages) {
                bean.setPageCount(pageCount);
            }
        }
        // 返回章节生成的所有页面
        return pages;
    }

    public Map<String, PageBean> convertToMap(List<PageBean> pages) {
        if (pages == null) {
            return null;
        }
        Map<String, PageBean> mapPages = new HashMap<>();
        for (PageBean bean : pages) {
            //以页号作为key存取页面
            String key = getKey(bean.getChapterName(), bean.getChapterNo(), bean.getPageNum());
            mapPages.put(key, bean);
        }
        return mapPages;
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

    public String getKey(String chapterName, int chapterNo, int pageNum) {
        return chapterName + "_" + chapterNo + "_" + pageNum;
    }

}
