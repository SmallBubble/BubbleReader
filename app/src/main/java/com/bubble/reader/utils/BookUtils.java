package com.bubble.reader.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * packger：com.bubble.reader.utils
 * auther：Bubble
 * date：2020/6/21
 * email：jiaxiang6595@foxmail.com
 * Desc：
 */
public class BookUtils {
    /**
     * 检查是否是章节名称
     *
     * @param str
     * @return
     */
    public static boolean checkArticle(String str) {
        if (TextUtils.isEmpty(str)) return false;
        Pattern p = Pattern.compile("(☆)(\\s*.*\\s*)(\n|\r|)*");
        if (p.matcher(str).find()) return true;
        p = Pattern.compile("(.*第)([一二三四五六七八九十百千万1234567890]*)[章节卷集部篇回](\\s{1,}.*)(\n|\r|)*");
        if (p.matcher(str).matches()) return true;
        p = Pattern.compile("(Chapter)(\\s*.*\\s*)(\n|\r|)*");
        return p.matcher(str).find();
    }
}
