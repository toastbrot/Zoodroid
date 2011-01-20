package com.unicorntoast.android.zoodroid.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
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
	
	public void tryLogin() {
		
	}
	
	private void callHttp(String url) {
		
	}
	
	private class HttpCallResult {
		public int statusCode;
		public String body;
		
		public HttpCallResult(int statusCode, String body) {
			this.statusCode = statusCode;
			this.body = body;
		}
	}
	
	private class HttpCallTask extends AsyncTask<String, Void, HttpCallResult> {
		private boolean throwNotAuthorized;
		
		public HttpCallTask() {
			this(true);
		}
		
		public HttpCallTask(boolean throwNotAuthorized) {
			this.throwNotAuthorized = throwNotAuthorized;
		}
		@Override
		protected HttpCallResult doInBackground(String... params) {
			HttpGet request = new HttpGet(params[0]);
			
			HttpResponse response;
			
			int statusCode = -1;
			try {
				response = httpClient.execute(request);
				
				statusCode = response.getStatusLine().getStatusCode();
				if(statusCode==401 && throwNotAuthorized) {
					throw new ZoodroidException(R.string.exception_not_authorized, null);
				} else if(statusCode!=200) {
					throw new ZoodroidException(R.string.exception_not_authorized, null, "HTTP Status: "+statusCode);
				}
			} catch (ClientProtocolException e) {
				throw new ZoodroidException(R.string.exception_protocol, e);
			} catch (IOException e) {
				throw new ZoodroidException(R.string.exception_connection, e);
			}
			
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				try {
					BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuffer buffer = new StringBuffer();
		
					String line = r.readLine();
					while (line != null) {
						line = r.readLine();
						buffer.append(line).append("\n");
					}
					entity.consumeContent();
					return new HttpCallResult(statusCode, buffer.toString());
				} catch(IOException e) {
					throw new ZoodroidException(R.string.exception_response, e);
				}
			}
	
			return null;
		}
		
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
				MessageDigest sha = MessageDigest.getInstance("SHA-1");
				ZooToolApi.this.userPwd = params[0]+":"+EncodingUtil.byteArrayToHex(sha.digest(params[1].getBytes()));
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
			
			
			HttpResponse response;
			
			try {
				response = httpClient.execute(request);
				
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode==408) {
					throw new ZoodroidException(R.string.exception_not_authorized, null);
				} else if(statusCode!=200) {
					throw new ZoodroidException(R.string.exception_not_authorized, null, "HTTP Status: "+statusCode);
				}
			} catch (ClientProtocolException e) {
				throw new ZoodroidException(R.string.exception_protocol, e);
			} catch (IOException e) {
				throw new ZoodroidException(R.string.exception_connection, e);
			}
			
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				try {
					BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuffer buffer = new StringBuffer();
		
					String line = r.readLine();
					while (line != null) {
						line = r.readLine();
						buffer.append(line).append("\n");
					}
					entity.consumeContent();
					return buffer.toString();
				} catch(IOException e) {
					throw new ZoodroidException(R.string.exception_response, e);
				}
			}
	
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			progressDialog.cancel();
			System.out.println(result);
		}
	}
	
	public void destroy() {
		credentials = null;
		httpClient.getConnectionManager().shutdown(); 
	}
}
