package com.unicorntoast.android.zoodroid;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.unicorntoast.android.zoodroid.api.ZooToolApi;
import com.unicorntoast.android.zoodroid.data.UserPreferences;
import com.unicorntoast.android.zoodroid.exception.ZoodroidException;
import com.unicorntoast.android.zoodroid.ui.dialog.ErrorDialog;

public class ZoodroidActivity extends Activity {
    
	
	private ZooToolApi api;
	private UserPreferences preferences;
	private EditText inputUsername;
	private EditText inputPassword;
	
	private class ExceptionHandler implements UncaughtExceptionHandler {

		private UncaughtExceptionHandler oldDefaultUncaughtExceptionHandler;
		
		public ExceptionHandler() {
			oldDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		}
		
		public void uncaughtException(Thread thread, final Throwable ex) {
			try {
				runOnUiThread(new Runnable() {
					public void run() {
						String msg = null;
						String more = null;
						if(ex instanceof ZoodroidException) {
							ZoodroidException zde = ZoodroidException.class.cast(ex.getCause());
							msg = ZoodroidActivity.this.getString(zde.getMsgResourceId());
							more = zde.getMore();
						} else if(ex.getCause() instanceof ZoodroidException) {
							ZoodroidException zde = ZoodroidException.class.cast(ex.getCause());
							msg = ZoodroidActivity.this.getString(zde.getMsgResourceId()); 
							more = zde.getMore();
						} else {
							msg = ex.getLocalizedMessage();
						}
						
						if(more!=null) {
							msg += ": "+more;
						}
						
						ErrorDialog.show(ZoodroidActivity.this, msg);
					}
				});
				
			} catch(Exception e) {
				Thread.setDefaultUncaughtExceptionHandler(oldDefaultUncaughtExceptionHandler);
				oldDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
			}
		}
		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
 
        preferences = new UserPreferences(this);
        
     
        inputUsername = EditText.class.cast(findViewById(R.id.input_username));
        inputPassword = EditText.class.cast(findViewById(R.id.input_password));
        Button signInButton = Button.class.cast(findViewById(R.id.signin_button));
        
        inputUsername.setText("toastbrot");
        inputPassword.setText("cc189ac0");
        
        signInButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String username = inputUsername.getText().toString();
				String password = inputPassword.getText().toString();
				if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
					api.login(username, password);	
				}
				
			}
        });
        api = new ZooToolApi(this);
        
        Intent intent = getIntent();
        String webPageUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.d("ABLA", webPageUrl+"");
        intent.getExtras();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	api.destroy();
    }
}