package com.nextdoor.stfandroid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class STFAnnotateActivity extends ActionBarActivity {
    private final int STROKE_WIDTH = 5;
    private final String PAINT_COLOR = "#F22613";
    private final int CORRECTION = 100;
    private final String SEND_BUTTON = "Send";
    private final String CANCEL_BUTTON = "Cancel";

    private ImageView annotationView;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private Canvas canvas;
    private Paint paint;
    private Bitmap overlay;
    private Bitmap screenshot;
    private Matrix matrix;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stfannotate);
        annotationView = (ImageView) findViewById(R.id.screenshot);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(10);
        annotationView.setDrawingCacheEnabled(true);
        annotationView.setOnTouchListener(getAnnotationListener());
        screenshot = STFAnnotator.getScreenshot();
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
                    .setPositiveButton(SEND_BUTTON, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Bitmap image = STFAnnotator.mergeAnnotation(screenshot, overlay);
                            STFItem stfItem = new STFItem(bitmapToBase64(image), getFeedback(), "", getEmailAddr());
                            STFManager.enqueue(stfItem);
                            STFManager.updateQueue();
                            STFAnnotator.deleteScreenshot();
                            STFAnnotateActivity.this.finish();
                        }
                    })
                    .setNegativeButton(CANCEL_BUTTON, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            dialog = builder.create();
            dialog.show();
            ImageView header = (ImageView) dialog.findViewById(R.id.header);
            header.setBackgroundColor(Color.parseColor(STFConfig.APP_COLOR));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor(STFConfig.APP_COLOR));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor(STFConfig.APP_COLOR));
            dialog.setCanceledOnTouchOutside(false);
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnTouchListener getAnnotationListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY() + CORRECTION;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        upX = event.getX();
                        upY = event.getY() + CORRECTION;
                        canvas.drawLine(downX, downY, upX, upY, paint);
                        annotationView.invalidate();
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
        };
    }

    private void setupCanvas() {
        overlay = Bitmap.createBitmap(screenshot.getWidth(), screenshot.getHeight(), screenshot.getConfig());
        canvas = new Canvas(overlay);
        paint = new Paint();
        paint.setColor(Color.parseColor(PAINT_COLOR));
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        matrix = new Matrix();
        canvas.drawBitmap(screenshot, matrix, paint);
        annotationView.setImageBitmap(overlay);
    }

    private String getFeedback() {
        EditText feedback = (EditText) dialog.findViewById(R.id.feedback);
        return feedback.getText().toString();
    }

    private String getEmailAddr() {
        EditText emailAddr = (EditText) dialog.findViewById(R.id.email);
        return emailAddr.getText().toString();
    }

    private String bitmapToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private boolean validateEmailAddr() {
        EditText emailAddr = (EditText) dialog.findViewById(R.id.email);
        if (emailAddr.getText().toString().trim().equals("")) {
            emailAddr.setError("Email address required.");
            Toast.makeText(this, emailAddr.getError(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
