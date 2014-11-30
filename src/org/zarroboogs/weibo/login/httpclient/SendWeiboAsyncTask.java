package org.zarroboogs.weibo.login.httpclient;

import org.zarroboogs.utils.net.BroserContent;

import android.os.AsyncTask;

public class SendWeiboAsyncTask extends AsyncTask<String, Integer, Boolean> {

	public static interface OnSendListener {
		public void onSend(Boolean hb);
	}

	public OnSendListener mInSuccessListener;

	public SendWeiboAsyncTask(OnSendListener listener) {
		this.mInSuccessListener = listener;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		
		SinaPreLogin sinaPreLogin = new SinaPreLogin();
		boolean isSend = sinaPreLogin.sendWeibo(BroserContent.getInstance(), 
				"http://widget.weibo.com/public/aj_addMblog.php",
				params[0], params[1], null, params[2]);
		return isSend;
	}

	@Override
	protected void onPostExecute(Boolean hb) {
		super.onPostExecute(hb);
		mInSuccessListener.onSend(hb);
	}

}
