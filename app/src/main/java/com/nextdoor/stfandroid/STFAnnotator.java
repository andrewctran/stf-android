package com.nextdoor.stfandroid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class STFAnnotator {
    public static final String TAG = "STFAnnotator";

    public static Bitmap takeScreenshot(View rootView) {
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public static Bitmap getScreenshot(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        File screenshotFile = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(screenshotFile.getAbsolutePath(), options);
        return bitmap;
    }

    public static String saveScreenshot(Bitmap screenshot) {
        String externalPath = Environment.getExternalStorageDirectory() + "/";
        String imagePath = externalPath + "STFScreenshot";
        File imageFile = new File(imagePath);
        FileOutputStream fos;
        if (imageFile.exists()) {
            imageFile.delete();
        }
        try {
            fos = new FileOutputStream(imageFile);
            screenshot.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return imagePath;
    }
}