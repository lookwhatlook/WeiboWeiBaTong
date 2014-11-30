package lib.org.zarroboogs.weibo.login.httpclient;

import lib.org.zarroboogs.weibo.login.javabean.LoginResultHelper;
import lib.org.zarroboogs.weibo.login.javabean.PreLoginResult;
import android.os.AsyncTask;

public class AfterPreLoginAsyncTask extends AsyncTask<String, Integer, LoginResultHelper> {

	private PreLoginResult mPreLonginBean;
	public void setPreLonginBean(PreLoginResult mPreLonginBean) {
		this.mPreLonginBean = mPreLonginBean;
	};
	public static interface OnAfterPreLongInListener {
		public void onDoLogInFinish(LoginResultHelper preLonginBean);
	}

	public OnAfterPreLongInListener mInSuccessListener;

	public AfterPreLoginAsyncTask(OnAfterPreLongInListener listener) {
		this.mInSuccessListener = listener;
	}

	@Override
	protected LoginResultHelper doInBackground(String... params) {
		SinaPreLogin sinaPreLogin = new SinaPreLogin();
		// params[0]:未加密的用户名          params[1]:未加密的密码
		return sinaPreLogin.doLoginAfterPreLogin(params[0], params[1],params[2], mPreLonginBean);
	}

	@Override
	protected void onPostExecute(LoginResultHelper preLonginBean) {
		super.onPostExecute(preLonginBean);
		mInSuccessListener.onDoLogInFinish(preLonginBean);
	}

}
