package com.bubble.reader.page.helper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;

import com.bubble.reader.page.bean.PageBean;

/**
 * @author Bubble
 * @date 2020/7/11
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class BottomTipsHelper extends PageHelper {

    private Context mContext;

    public void draw(Canvas canvas, PageBean pageBean) {
        drawBattery(canvas);
    }

    /**
     * 画电池
     *
     * @param canvas
     */
    private void drawBattery(Canvas canvas) {
        Intent intent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (intent == null) {

        }

    }

    @Override
    void onDraw(Canvas canvas, PageBean pageBean) {

    }
}
