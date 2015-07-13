package com.nextdoor.stfandroid;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
    private STFManager stfManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stfManager = new STFManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        stfManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stfManager.onPause();
    }
}