package com.bubble.reader.widget.draw.impl;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.base.PageDrawHelper;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 仿真翻页
 */
public class SimulationDrawHelper extends PageDrawHelper {

    public SimulationDrawHelper(PageView pageView) {
        super(pageView);
    }


    /*===============================继承父类的方法=================================*/
    @Override
    protected void initData() {

    }

    @Override
    public void onTouchEvent(PageView view, MotionEvent event) {

    }

    @Override
    public void onDrawPage(Canvas canvas) {

    }


    /*===============================本类自己的方法=================================*/
    /**
     * A点为触摸点
     */
    private PointF mPointA;
    private PointF mPointB;
    private PointF mPointC;
    private PointF mPointD;
    private PointF mPointE;
    /**
     * F 点为直角点 右上角和右下角
     */
    private PointF mPointF;
    private PointF mPointG;
    private PointF mPointH;
    private PointF mPointI;
    private PointF mPointJ;
    private PointF mPointK;

    private Path mPath;

    /**
     * 创建当前页的路径 （正面）
     */
    private void createAreaFrontPath() {
        mPath.reset();
        // 左上角奇点
        mPath.lineTo(0, 0);
        // 移动到左下角
        mPath.lineTo(0, mPageHeight);
        // 移动到C点
        mPath.lineTo(mPointC.x, mPageHeight);
        // 从C点画贝塞尔曲线 以E点为控制点
        mPath.quadTo(mPointE.x, mPointE.y, mPointB.x, mPointB.y);
        //移动到A点
        mPath.lineTo(mPointA.x, mPointA.y);
        //移动到K点
        mPath.lineTo(mPointK.x, mPointK.y);
        // 从C点画贝塞尔曲线 以E点为控制点
        mPath.quadTo(mPointH.x, mPointH.y, mPointJ.x, mPointJ.y);
        // 右上角
        mPath.lineTo(mPageWidth, 0);
        // 闭合区域
        mPath.close();
    }

    /**
     * 当前页背面
     */
    private void createAreaBackPath() {

    }

    private void calcPoints() {
        mPointG.set((mPointA.x + mPointF.x) / 2, (mPointA.y + mPointF.y) / 2);

        mPointE.x = mPointG.x - (mPointF.y - mPointG.y) * (mPointF.y - mPointG.y) / (mPointF.x - mPointG.x);
        mPointE.y = mPointF.y;

        mPointH.x = mPointF.x;
        mPointH.y = mPointG.y - (mPointF.x - mPointG.x) * (mPointF.x - mPointG.x) / (mPointF.y - mPointG.y);

        mPointC.x = mPointE.x - (mPointF.x - mPointE.x) / 2;
        mPointC.y = mPointF.y;

        mPointJ.x = mPointF.x;
        mPointJ.y = mPointH.y - (mPointF.y - mPointH.y) / 2;

        mPointB = getIntersectionPoint(mPointA, mPointE, mPointC, mPointJ);
        mPointK = getIntersectionPoint(mPointA, mPointH, mPointC, mPointJ);

        mPointD.x = (mPointC.x + 2 * mPointE.x + mPointB.x) / 4;
        mPointD.y = (2 * mPointE.y + mPointC.y + mPointB.y) / 4;

        mPointI.x = (mPointJ.x + 2 * mPointH.x + mPointK.x) / 4;
        mPointI.y = (2 * mPointH.y + mPointJ.y + mPointK.y) / 4;
    }

    /**
     * 计算两线段相交点坐标
     *
     * @param lineOneMyPointOne
     * @param lineOneMyPointTwo
     * @param lineTwoMyPointOne
     * @param lineTwoMyPointTwo
     * @return 返回该点
     */
    private PointF getIntersectionPoint(PointF lineOneMyPointOne, PointF lineOneMyPointTwo, PointF lineTwoMyPointOne, PointF lineTwoMyPointTwo) {
        float x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = lineOneMyPointOne.x;
        y1 = lineOneMyPointOne.y;
        x2 = lineOneMyPointTwo.x;
        y2 = lineOneMyPointTwo.y;
        x3 = lineTwoMyPointOne.x;
        y3 = lineTwoMyPointOne.y;
        x4 = lineTwoMyPointTwo.x;
        y4 = lineTwoMyPointTwo.y;

        float pointX = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float pointY = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));

        return new PointF(pointX, pointY);
    }

}
