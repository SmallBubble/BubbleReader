package com.bubble.bubblereader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bubble.breader.chapter.TxtChapterFactory;
import com.bubble.breader.page.BubblePageCreator;
import com.bubble.breader.widget.PageView;
import com.bubble.breader.widget.draw.impl.LoadingDrawHelper;
import com.bubble.breader.widget.draw.impl.VerticalScrollDrawHelperV2;

import java.io.File;

public class VerticalScrollReadActivity extends AppCompatActivity {

    PageView mReadView;

    private BubblePageCreator mPageCreator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        mReadView = findViewById(R.id.readView);
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            initRead();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            initRead();
        }

    }

    private void initRead() {
        File directory = Environment.getExternalStorageDirectory();
        TxtChapterFactory factory = new TxtChapterFactory.Builder()
                .file(directory.getAbsoluteFile() + "/test.txt")
                .build();
        mPageCreator = new BubblePageCreator.Builder(mReadView)
                .chapterFactory(factory)
                .build();
        mReadView.setDrawHelper(new VerticalScrollDrawHelperV2(mReadView));
        mReadView.setPageCreator(mPageCreator);
        mReadView.setLoadingDrawHelper(new LoadingDrawHelper(mReadView, 10) {
            @Override
            protected void onDraw(Canvas canvas, int pageWidth, int pageHeight) {
                super.onDraw(canvas, pageWidth, pageHeight);
            }
        });
//        mPageCreator.addPageListener(new OfflinePageListener() {
//            @Override
//            public void onFileNotFound() {
//            }
//
//            @Override
//            public void onError(String message) {
//                super.onError(message);
//            }
//
//            @Override
//            public void onSuccess() {
//                super.onSuccess();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPageCreator.recycle();
    }
}
