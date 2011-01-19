package com.unicorntoast.android.zoodroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ZoodroidActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = getIntent();
        String webPageUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.d("ABLA", webPageUrl+"");
        intent.getExtras();
        
    }
}