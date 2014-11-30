package org.zarroboogs.util.net;

import org.zarroboogs.utils.NetEaseUtils;
import org.zarroboogs.utils.net.BroserContent;

import android.os.AsyncTask;

public class LoginAsyncTask extends AsyncTask<String, Integer, Boolean> {

	public static interface OnLogInSuccessListener {
		public void onDoLogInFinish(boolean isSuccess);
	}

	public OnLogInSuccessListener mInSuccessListener;

	public LoginAsyncTask(OnLogInSuccessListener listener) {
		// TODO Auto-generated constructor stub
		this.mInSuccessListener = listener;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		BroserContent mBroserContent = BroserContent.getInstance();

		HttpPostHelper mPostHelper = new HttpPostHelper();

		String userName = params[0];
		String passWord = NetEaseUtils.makeMD5(params[1].trim());
		String logInUrl = "http://reg.163.com/logins.jsp";

		boolean isSuccess = mPostHelper.loginNetEase(mBroserContent, logInUrl, userName, passWord);
		System.out.println("LogIn Success! ? : " + isSuccess);

		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		mInSuccessListener.onDoLogInFinish(result);
	}

}
