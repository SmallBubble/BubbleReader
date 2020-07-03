package com.bubble.bubblereader;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        String str = null;
        try {
            str = new String("视频给出了答案".getBytes(), "GBK");
            byte[] bytes = str.getBytes();
            System.out.println(Arrays.toString(bytes));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        assertEquals(4, 2 + 2);
    }
}