package org.zarroboogs.weibo.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lib.org.zarroboogs.weibo.login.httpclient.AfterPreLoginAsyncTask;
import lib.org.zarroboogs.weibo.login.httpclient.AfterPreLoginAsyncTask.OnAfterPreLongInListener;
import lib.org.zarroboogs.weibo.login.httpclient.LoginAsyncTask;
import lib.org.zarroboogs.weibo.login.httpclient.LoginAsyncTask.OnLogInListener;
import lib.org.zarroboogs.weibo.login.httpclient.PreLoginAsyncTask;
import lib.org.zarroboogs.weibo.login.httpclient.PreLoginAsyncTask.OnPreLongInListener;
import lib.org.zarroboogs.weibo.login.httpclient.RealLibrary;
import lib.org.zarroboogs.weibo.login.httpclient.SendWeiboAsyncTask;
import lib.org.zarroboogs.weibo.login.httpclient.SendWeiboAsyncTask.OnSendListener;
import lib.org.zarroboogs.weibo.login.javabean.DoorImageAsyncTask;
import lib.org.zarroboogs.weibo.login.javabean.DoorImageAsyncTask.OnDoorOpenListener;
import lib.org.zarroboogs.weibo.login.javabean.HasloginBean;
import lib.org.zarroboogs.weibo.login.javabean.LoginResultHelper;
import lib.org.zarroboogs.weibo.login.javabean.PreLoginResult;

import org.apache.commons.codec.binary.Base64;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.BlackMagicLoginTask;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.support.utils.Utility;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.umeng.analytics.MobclickAgent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class WeiboLoginActivity extends AbstractAppActivity {

	EditText doorimg;
	ImageView mDoorImg;
	
	JsEvaluator mJsEvaluator;
	PreLoginResult mPreLonginBean;
	HasloginBean mHasloginBean;
	String rsaPwd = "";
	
	
	String userName = "";
	String passWord = "";
	


	private EditText username;

	private EditText password;

	private String appkey;

	private String appSecret;

	private BlackMagicLoginTask loginTask;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_blackmagicactivity_layout);
		mJsEvaluator = new JsEvaluator(getApplicationContext());
		
		mDoorImg = (ImageView) findViewById(R.id.imagedoor);
		
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		doorimg = (EditText) findViewById(R.id.doorimg);
		
	};
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Utility.cancelTasks(loginTask);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu_blackmagicactivity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_login:
			if (username.getText().toString().length() == 0) {
				username.setError(getString(R.string.email_cant_be_empty));
				return true;
			}

			if (password.getText().toString().length() == 0) {
				password.setError(getString(R.string.password_cant_be_empty));
				return true;
			}
			if (Utility.isTaskStopped(loginTask)) {

				String[] array = getResources().getStringArray(R.array.tail_value);
				String value = array[0];
				appkey = value.substring(0, value.indexOf(","));
				appSecret = value.substring(value.indexOf(",") + 1);

				Log.d("APPKEY", "key:" + appkey + "  secret:" + appSecret);
				loginTask = new BlackMagicLoginTask(WeiboLoginActivity.this, 
						username.getText().toString(), password.getText().toString(), appkey, appSecret);
				loginTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1000) {
				afterPreLogin(rsaPwd);
			}
			if (msg.what == 1001) {
				mHasloginBean = (HasloginBean) msg.obj;
				sendWeibo(mHasloginBean);
			}
			if (msg.what == 1002) {
				DoorImageAsyncTask doorImageAsyncTask = new DoorImageAsyncTask();
				doorImageAsyncTask.setOnDoorOpenListener(new OnDoorOpenListener() {
					
					@Override
					public void onDoorOpen(android.graphics.Bitmap result) {
						// TODO Auto-generated method stub
						mDoorImg.setImageBitmap(result);
					}
				});
				doorImageAsyncTask.execute(mPreLonginBean.getPcid());
			}
		}
	};
	
	private void preLogin() {
		PreLoginAsyncTask loginAsyncTask = new PreLoginAsyncTask(new OnPreLongInListener() {
			
			@Override
			public void onDoLogInFinish(PreLoginResult preLonginBean) {
				
				mPreLonginBean = preLonginBean;
				encodePassword(preLonginBean);
			}
		});
		loginAsyncTask.execute(userName,passWord);
	}
	private void afterPreLogin(String rasPassWord) {
		AfterPreLoginAsyncTask afterPreLoginAsyncTask = new AfterPreLoginAsyncTask(new OnAfterPreLongInListener() {
			
			@Override
			public void onDoLogInFinish(LoginResultHelper preLonginBean) {
				// TODO Auto-generated method stub
				Log.d("onDoLogInFinish", "" + preLonginBean.isLogin());
				if (preLonginBean.isLogin()) {
					login(preLonginBean);
				}else {
					Log.d("LogIn_Failed", "[" + preLonginBean.getErrorReason() + "]");
					mHandler.sendEmptyMessage(1002);
				}
			}

		});
		afterPreLoginAsyncTask.setPreLonginBean(mPreLonginBean);
		afterPreLoginAsyncTask.execute(encodeAccount(userName), rasPassWord, doorimg.getText().toString());
	}
	private void encodePassword(PreLoginResult preLonginBean) {
		RealLibrary realLibrary = new RealLibrary(getApplicationContext());
		String js = realLibrary.getRsaJs();
		
		String pwd = "\"" + passWord + "\"";
		String servertime = "\"" + preLonginBean.getServertime() + "\"";
		String nonce = "\"" + preLonginBean.getNonce() + "\"";
		String pubkey = "\"" + preLonginBean.getPubkey() + "\"";
		String call = " var rsaPassWord = getRsaPassWord(" + pwd +", " + servertime + ", " + nonce +", " + pubkey+ "); rsaPassWord; ";
		
		mJsEvaluator.evaluate(js + call, new JsCallback() {
			
			@Override
			public void onResult(String value) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
				Message msg = new Message();
				rsaPwd = value;
				msg.what = 1000;
				mHandler.sendMessage(msg);
			}
		});
	}
	
	private void login(LoginResultHelper preLonginBean) {
		LoginAsyncTask loginAsyncTask = new LoginAsyncTask(new OnLogInListener() {
			
			@Override
			public void onLonIn(HasloginBean hb) {
				Message message = new Message();
				message.obj = hb;
				message.what =  1001;
				mHandler.sendMessage(message);
			}
		});
		loginAsyncTask.execute(preLonginBean);
	}
	private void sendWeibo(HasloginBean hasloginBean) {
		if (hasloginBean.isResult()) {
			Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
			SendWeiboAsyncTask sendWeiboAsyncTask = new SendWeiboAsyncTask(new OnSendListener() {
				
				@Override
				public void onSend(Boolean hb) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "" + hb, Toast.LENGTH_SHORT).show();
				}
			});
			sendWeiboAsyncTask.execute();
		}else {
			Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
		}
	}
    private String encodeAccount(String account) {
        String encodedString;
		try {
			encodedString = new String(Base64.encodeBase64(URLEncoder.encode(account, "UTF-8").getBytes()));
	        String userName = encodedString.replace('+','-').replace('/','_');
	        return userName;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
    
	public static class ProgressFragment extends DialogFragment {

		private MyAsyncTask<?, ?, ?> asyncTask = null;

		public static ProgressFragment newInstance() {
			ProgressFragment frag = new ProgressFragment();
			frag.setRetainInstance(true);
			Bundle args = new Bundle();
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage(getString(R.string.logining));
			dialog.setIndeterminate(false);
			dialog.setCancelable(true);

			return dialog;
		}

		@Override
		public void onCancel(DialogInterface dialog) {

			if (asyncTask != null) {
				asyncTask.cancel(true);
			}

			super.onCancel(dialog);
		}

		public void setAsyncTask(MyAsyncTask<?, ?, ?> task) {
			asyncTask = task;
		}
	}
}
