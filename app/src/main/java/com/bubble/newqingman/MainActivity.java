package com.bubble.newqingman;

import android.content.Intent;
import android.view.View;

import com.bubble.common.base.BaseActivity;

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
        findViewById(R.id.btnRead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReadActivity.class));
            }
        });
    }

    @Override
    protected void initView() {
    }
}
