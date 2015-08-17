package com.nextdoor.library;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Image handling for the bug/feedback reporter.
 */
public class STFAnnotator {
    public static final String STF_FILENAME = "stf_screenshot.jpg";
    public static final String STF_DIR = "stfImages";

    /**
     * Takes a screenshot of the current display.
     * @param rootView The root of the app's view hierarchy.
     * @return Screenshot in Bitmap format
     */
    public static Bitmap takeScreenshot(View rootView) {
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache(true);
        return Bitmap.createBitmap(rootView.getDrawingCache());
    }

    /**
     * Grabs the latest screenshot from disk.
     * @return Screenshot in Bitmap format
     */
    public static Bitmap getScreenshot(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        File screenshotFile = new File(path);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(screenshotFile));
        } catch (FileNotFoundException e) {

        }
        return bitmap;
    }

    /**
     * Saves the screenshot to disk, overriding any existing screenshot.
     * @param screenshot
     * @return The image path to the screenshot on disk.
     */
    public static String saveScreenshot(Context context, Bitmap screenshot) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir(STF_DIR, Context.MODE_PRIVATE);
        File imagePath = new File(directory, STF_FILENAME);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(imagePath);
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath.getAbsolutePath();
    }

    public static void deleteScreenshot() {
        String imagePath = STFSession.imagePath;
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