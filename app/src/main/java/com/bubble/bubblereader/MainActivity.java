package com.bubble.bubblereader;

import android.content.Intent;
import android.view.View;

import com.bubble.common.base.BaseActivity;
import com.bubble.common.net.RequestCallBack;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DefaultObserver;

/**
 * @author Bubble
 * @date 2020/6/19
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * <p>
 * Desc：MainActivity
 */
public class MainActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initListener() {
        findViewById(R.id.btnRead1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HorizontalMoveReadActivity.class));
            }
        });
        findViewById(R.id.btnRead2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HorizontalScrollReadActivity.class));
            }
        });
        findViewById(R.id.btnRead3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VerticalScrollReadActivity.class));
            }
        });
        findViewById(R.id.btnRead4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HorizontalMoveReadActivity.class));
            }
        });
    }

    @Override
    protected void initView() {
        RequestCallBack<String> manager = new RequestCallBack<String>() {
            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailure(int code, String message) {

            }
        };

//        manager.addCallBack(new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(String data) {
//
//            }
//
//            @Override
//            public void onFailure(int code, String message) {
//
//            }
//        });


        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("");
            }
        })
                .compose(apply())
                .subscribe(new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(String data) {

                    }

                    @Override
                    public void onFailure(int code, String message) {

                    }
                });
    }
}
