package com.video_trim.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.video_trim.K4LVideoTrimmer;
import com.video_trim.R;
import com.video_trim.interfaces.OnK4LVideoListener;
import com.video_trim.interfaces.OnTrimVideoListener;

import java.io.File;

public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener {

    private K4LVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    private String ROOT = File.separator + "NAMASTEY";
    private String SD_CARD_PATH = Environment.getExternalStorageDirectory().getPath();
    private String FILE_PATH = SD_CARD_PATH + ROOT;
    private String FILE_PATH_VIDEO = "tempvideo.mp4";
    String path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        Intent extraIntent = getIntent();


        if (extraIntent != null) {
            path = extraIntent.getStringExtra("EXTRA_VIDEO_PATH");
        }

        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));

//        mVideoTrimmer.setDestinationPath();
        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {
            mVideoTrimmer.setMaxDuration(60);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnK4LVideoListener(this);

            String  folder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

            mVideoTrimmer.setDestinationPath(folder);
//            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoURI(Uri.fromFile(new File(path)));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {
        mProgressDialog.show();
    }

    @Override
    public void getResult(final Uri uri) {
        mProgressDialog.cancel();

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(TrimmerActivity.this, getString(R.string.video_saved_at, uri.getPath()), Toast.LENGTH_SHORT).show();
//            }
//        });
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
//        intent.setDataAndType(uri, "video/mp4");
//        startActivity(intent);

        Intent intent = new Intent();
        intent.putExtra("videoPath",uri);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
