package com.unicorntoast.android.zoodroid.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPreferences {
	private static final String PREFERENCES_NAME = "ZoodroidPrefs";
	private static final String KEY_USERNAME = "zd_username";
	private static final String KEY_PASSWORD = "zd_password";
	
	private SharedPreferences preferences;
	
	public UserPreferences(Context ctx) {
		preferences = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveCredentials(String username, String password) {
		Editor edit = preferences.edit();
		edit.putString(KEY_USERNAME, username);
		edit.putString(KEY_PASSWORD, password);
		edit.commit();
	}
	
	public String getUsername() {
		return preferences.getString(KEY_USERNAME, null);
	}
	
	public String getPassword() {
		return preferences.getString(KEY_PASSWORD, null);
	}
}
