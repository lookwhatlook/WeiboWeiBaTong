package lib.org.zarroboogs.weibo.login.httpclient;

import lib.org.zarroboogs.weibo.login.javabean.HasloginBean;
import lib.org.zarroboogs.weibo.login.javabean.LoginResultHelper;
import android.os.AsyncTask;

public class LoginAsyncTask extends AsyncTask<LoginResultHelper, Integer, HasloginBean> {

	public static interface OnLogInListener {
		public void onLonIn(HasloginBean hb);
	}

	public OnLogInListener mInSuccessListener;

	public LoginAsyncTask(OnLogInListener listener) {
		this.mInSuccessListener = listener;
	}

	@Override
	protected HasloginBean doInBackground(LoginResultHelper... params) {
		SinaPreLogin sinaPreLogin = new SinaPreLogin();
		// params[0]:未加密的用户名          params[1]:未加密的密码
		return sinaPreLogin.loginUserPage(params[0]);
	}

	@Override
	protected void onPostExecute(HasloginBean hb) {
		super.onPostExecute(hb);
		mInSuccessListener.onLonIn(hb);
	}

}
