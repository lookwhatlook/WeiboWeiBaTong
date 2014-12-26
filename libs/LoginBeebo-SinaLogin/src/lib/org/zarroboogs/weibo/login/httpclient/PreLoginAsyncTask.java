package lib.org.zarroboogs.weibo.login.httpclient;

import lib.org.zarroboogs.weibo.login.javabean.PreLoginResult;
import android.os.AsyncTask;

public class PreLoginAsyncTask extends AsyncTask<String, Integer, PreLoginResult> {

	public static interface OnPreLongInListener {
		public void onDoLogInFinish(PreLoginResult preLonginBean);
	}

	public OnPreLongInListener mInSuccessListener;

	public PreLoginAsyncTask(OnPreLongInListener listener) {
		this.mInSuccessListener = listener;
	}

	@Override
	protected PreLoginResult doInBackground(String... params) {
		SinaPreLogin sinaPreLogin = new SinaPreLogin();
		// params[0]:未加密的用户名          params[1]:未加密的密码
		return sinaPreLogin.preLogin(params[0], params[1]);
	}

	@Override
	protected void onPostExecute(PreLoginResult preLonginBean) {
		super.onPostExecute(preLonginBean);
		mInSuccessListener.onDoLogInFinish(preLonginBean);
	}

}
