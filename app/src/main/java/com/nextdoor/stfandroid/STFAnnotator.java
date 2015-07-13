package com.nextdoor.stfandroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class STFAnnotator {
    private Context context;
    private Bitmap screenshot;

    public STFAnnotator(Context context) {
        this.context = context;
    }
    public Bitmap takeScreenshot() {
        View rootView = ((Activity) context).findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveScreenshot(Bitmap screenshot) {
        File stfDirectory = new File(Environment.getExternalStorageDirectory(), "/STF");
        stfDirectory.mkdirs();
        String imagePath = "STFScreenshot.png";
        File stfImage = new File(stfDirectory, imagePath);
        if (stfImage.exists()) {
            stfImage.delete();
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

}
