package com.bubble.bubblereader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bubble.reader.chapter.TxtChapterFactory;
import com.bubble.reader.chapter.listener.OnChapterRequestListener;
import com.bubble.reader.chapter.listener.OnChapterResultListener;
import com.bubble.reader.page.DefaultPageCreator;
import com.bubble.reader.page.listener.OfflinePageListener;
import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.impl.HorizontalMoveDrawHelper;

import java.io.File;

public class HorizontalMoveReadActivity extends AppCompatActivity implements OnChapterRequestListener {

    PageView mReadView;

    private DefaultPageCreator mPageCreator;


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

        mPageCreator = new DefaultPageCreator.Builder(mReadView)
                .chapterFactory(factory)
                .build();
        mReadView.setDrawHelper(new HorizontalMoveDrawHelper(mReadView));
        mReadView.setPageCreator(mPageCreator);
        mPageCreator.addPageListener(new OfflinePageListener() {
            @Override
            public void onFileNotFound() {
            }

            @Override
            public void onError(String message) {
                super.onError(message);
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
            }
        });
    }

    @Override
    public void onRequest(boolean isPrepare, int currentIndex, OnChapterResultListener listener) {
    }
}
