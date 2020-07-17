package com.bubble.breader.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * @author Bubble
 * @date 2020/7/4
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 页面加载类
 */
public class BookUtils {
    private static Pattern sPattern1 = Pattern.compile("(☆)(\\s*.*\\s*)(\n|\r|)*");
    private static Pattern sPattern2 = Pattern.compile("(.*第)([一二三四五六七八九十百千万1234567890]*)[章节卷集部篇回](\\s{1,}.*)(\n|\r|)*");
    private static Pattern sPattern3 = Pattern.compile("(Chapter)(\\s*.*\\s*)(\n|\r|)*");

    /**
     * 检查是否是章节名称
     *
     * @param str
     * @return
     */
    public static boolean checkArticle(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (sPattern1.matcher(str).find()) {
            return true;
        }
        if (sPattern2.matcher(str).matches()) {
            return true;
        }
        return sPattern3.matcher(str).find();
    }

    /**
     * 检查两个字符串是否相等
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean checkEqual(String s1, String s2) {
        if (TextUtils.isEmpty(s1) && TextUtils.isEmpty(s2)) {
            return true;
        }
        if (!TextUtils.isEmpty(s1) && s1.equals(s2)) {
            return true;
        }
        return false;
    }
}
