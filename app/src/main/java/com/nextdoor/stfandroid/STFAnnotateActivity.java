package com.nextdoor.stfandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class STFAnnotateActivity extends ActionBarActivity {
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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(10);
        screenshotView.setDrawingCacheEnabled(true);
        screenshotView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY() + 100;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        upX = event.getX();
                        upY = event.getY() + 100;
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
        screenshot = STFAnnotator.getScreenshot(getIntent().getStringExtra(STFSession.TAG));
        setupCanvas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stfannotate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.action_send) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_feedback, null))
                    .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Bitmap result = STFAnnotator.mergeAnnotation(screenshot, overlay);
                            STFAnnotateActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            View customTitle = getLayoutInflater().inflate(R.layout.dialog_feedback_title, null);
            builder.setCustomTitle(customTitle);
            builder.setInverseBackgroundForced(true);
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.stf_blue));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.stf_blue));
            dialog.setCanceledOnTouchOutside(false);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupCanvas() {
        overlay = Bitmap.createBitmap(screenshot.getWidth(), screenshot.getHeight(), screenshot.getConfig());
        canvas = new Canvas(overlay);
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.stf_blue));
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        matrix = new Matrix();
        canvas.drawBitmap(screenshot, matrix, paint);
        screenshotView.setImageBitmap(overlay);
    }
}
