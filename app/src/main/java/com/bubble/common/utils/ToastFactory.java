package com.bubble.common.utils;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bubble.bubblereader.R;
import com.bubble.common.base.BaseApplication;

/**
 * @author WeiJiaxiang
 * @date 2020/7/8
 * @Desc
 */
public class ToastFactory {
    private static Toast sToast;

    private class ToastHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private class ToastRunnable implements Runnable {
        @Override
        public void run() {

        }
    }

    /**
     * 显示自适应Toast
     */
    public static void showWarpToast(String message) {

        View toastView = LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.layout_toast, null, false);
        TextView tvContent = toastView.findViewById(R.id.tvContent);
        tvContent.setText(message);
        sToast.setView(toastView);
    }


}
