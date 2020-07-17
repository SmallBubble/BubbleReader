package com.bubble.bubblereader;

import com.bubble.breader.BubbleReader;
import com.bubble.bubblereader.constant.AppConstant;
import com.bubble.common.base.BaseApplication;
import com.bubble.common.net.NetManager;

/**
 * @author Bubble
 * @date 2020/6/29
 * @email 1337986595@qq.com
 * @Desc
 */
public class QMApp extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        NetManager.init(AppConstant.BASE_URL);
        BubbleReader.getInstance().init(this);
    }
}
