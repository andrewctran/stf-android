package com.nextdoor.stfandroid;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
    private STFSession stfSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stfSession = STFManager.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        stfSession.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stfSession.onPause();
    }
}