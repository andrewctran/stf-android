package com.nextdoor.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Image handling for the bug/feedback reporter.
 */
public class STFAnnotator {
    public static final String TAG = "STFAnnotator";
    public static final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/STFScreenshot";

    /**
     * Takes a screenshot of the current display.
     * @param rootView The root of the app's view hierarchy.
     * @return Screenshot in Bitmap format
     */
    public static Bitmap takeScreenshot(View rootView) {
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    /**
     * Grabs the latest screenshot from disk.
     * @return Screenshot in Bitmap format
     */
    public static Bitmap getScreenshot() {
        String path = Environment.getExternalStorageDirectory() + "/STFScreenshot";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        File screenshotFile = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(screenshotFile.getAbsolutePath(), options);
        return bitmap;
    }

    /**
     * Saves the screenshot to disk, overriding any existing screenshot.
     * @param screenshot
     * @return The image path to the screenshot on disk.
     */
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

    public static void deleteScreenshot() {
        String externalPath = Environment.getExternalStorageDirectory() + "/";
        String imagePath = externalPath + "STFScreenshot";
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }
    /**
     * Overlay user annotations on the screenshot.
     * @param screenshot
     * @param annotation
     * @return The composite bitmap combining both screenshot and annotations.
     */
    public static Bitmap mergeAnnotation(Bitmap screenshot, Bitmap annotation) {
        Bitmap overlay = Bitmap.createBitmap(screenshot.getWidth(), screenshot.getHeight(), screenshot.getConfig());
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(screenshot, new Matrix(), null);
        canvas.drawBitmap(annotation, 0, 0, null);
        return overlay;
    }
}