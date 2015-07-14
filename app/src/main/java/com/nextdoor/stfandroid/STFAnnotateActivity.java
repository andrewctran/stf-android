package com.nextdoor.stfandroid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class STFAnnotateActivity extends Activity {
    private FrameLayout panel;
    private ImageView screenshotView;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private Canvas canvas;
    private Paint paint;
    private Bitmap overlay;
    private Bitmap screenshot;
    private Matrix matrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stfannotate);
        screenshotView = (ImageView) findViewById(R.id.screenshot);
        screenshotView.setDrawingCacheEnabled(true);
        screenshotView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        upX = event.getX();
                        upY = event.getY();
                        canvas.drawLine(downX, downY, upX, upY, paint);
                        screenshotView.invalidate();
                        downX = upX;
                        downY = upY;
                        break;

                    case MotionEvent.ACTION_UP:
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    default:
                        break;
                }

                return true;
            }
        });
        panel = (FrameLayout) findViewById(R.id.panel);
        screenshot = STFAnnotator.getScreenshot(getIntent().getStringExtra(STFManager.TAG));
        setupCanvas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stfannotate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupCanvas() {
        overlay = Bitmap.createBitmap(screenshot.getWidth(), screenshot.getHeight(), screenshot.getConfig());
        canvas = new Canvas(overlay);
        paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        matrix = new Matrix();
        canvas.drawBitmap(screenshot, matrix, paint);
        screenshotView.setImageBitmap(overlay);
    }
}
