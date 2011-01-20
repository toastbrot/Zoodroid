package com.unicorntoast.android.zoodroid.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.unicorntoast.android.zoodroid.R;
import com.unicorntoast.android.zoodroid.exception.ZoodroidException;
import com.unicorntoast.android.zoodroid.utils.EncodingUtil;

/*

curl -vj "http://zootool.com/api/users/validate/?username=toastbrot&apikey=f032677916334bb10869ccc940dfcd00&login=true" --user "toastbrot:dab03e052126b883bbf12645b385c24232192a8e" --digest

 */
public class ZooToolApi {
	private static final String API_KEY = "f032677916334bb10869ccc940dfcd00";

	private static final String VALIDATE_CALL = "http://zootool.com/api/users/validate/?username=%s&apikey=%s&login=true";
	
	private String username;
	private String password;
	private String userPwd;

	private DefaultHttpClient httpClient = new DefaultHttpClient();
	private Credentials credentials = null;
	
	private Context ctx;
	
	public ZooToolApi(Context ctx) {
		this.ctx = ctx;
	}

	public void login(String username, String password) {
		this.username = username.toLowerCase();
		this.password = password;
		this.userPwd = null;
		this.credentials = null;
		
		new LoginTask().execute(this.username, this.password);
	}
	
	private class LoginTask extends AsyncTask<String, Integer, String> {
		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.progressDialog = ProgressDialog.show(ZooToolApi.this.ctx, "Logging you in", "Just a matter of seconds ;)");
		}

		@Override
		protected String doInBackground(String... params) {
			
			try {
				MessageDigest sha = MessageDigest.getInstance("SHA-12");
				ZooToolApi.this.userPwd = username+":"+EncodingUtil.byteArrayToHex(sha.digest(params[1].getBytes()));
			} catch (NoSuchAlgorithmException e) {
				progressDialog.cancel();
				throw new ZoodroidException(R.string.exception_sha1,e);
			}

			credentials = new UsernamePasswordCredentials(userPwd);
			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope("zootool.com", 80, null, AuthPolicy.DIGEST),
					credentials
				);
			
			HttpGet request = new HttpGet(String.format(VALIDATE_CALL, params[0], API_KEY));
			
			try {
				System.out.println("executing request" + request.getRequestLine());
				HttpResponse response = httpClient.execute(request);
				HttpEntity entity = response.getEntity();
				
		        System.out.println("----------------------------------------");
		        System.out.println(response.getStatusLine());
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		        
		            BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
		            StringBuffer buffer = new StringBuffer();
		            
		            String line = r.readLine();
		            while(line!=null) {
		            	line = r.readLine();
		            	buffer.append(line).append("\n");
		            }
		            entity.consumeContent();
		            return buffer.toString();
		        }
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			
			return null;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
		@Override
		protected void onPostExecute(String result) {
			progressDialog.cancel();
		}
	}
	
	public void destroy() {
		credentials = null;
		httpClient.getConnectionManager().shutdown(); 
	}
}
