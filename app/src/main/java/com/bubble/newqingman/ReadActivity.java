package com.bubble.newqingman;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bubble.reader.creator.PageCreator;
import com.bubble.reader.page.offline.OfflinePageCreator;
import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.HSDrawHelper;

import java.io.File;

public class ReadActivity extends AppCompatActivity {

    PageView mReadView;

    private PageCreator mPageCreator;


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
        mPageCreator = new OfflinePageCreator.Builder(mReadView)
                .file(directory.getAbsoluteFile() + "/test.txt")
                .build();
        mReadView.setDrawHelper(new HSDrawHelper(mReadView));
        mReadView.setPageCreator(mPageCreator);
    }
}
