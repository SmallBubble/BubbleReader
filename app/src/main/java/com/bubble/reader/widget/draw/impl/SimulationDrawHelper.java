package com.bubble.reader.widget.draw.impl;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.base.DrawHelper;

/**
 * packger：com.bubble.reader.widget.draw
 * author：Bubble
 * date：2020/6/21
 * email：1337986595@qq.com
 * Desc：仿真翻页
 */
public class SimulationDrawHelper extends DrawHelper {

    public SimulationDrawHelper(PageView pageView) {
        super(pageView);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onTouchEvent(PageView view, MotionEvent event) {

    }

    @Override
    public void onDrawPage(Canvas canvas) {

    }

    @Override
    public void onDrawStatic(Canvas canvas) {

    }
}
