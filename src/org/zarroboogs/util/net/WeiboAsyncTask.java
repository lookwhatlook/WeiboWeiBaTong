package org.zarroboogs.util.net;

import org.zarroboogs.utils.net.BroserContent;
import org.zarroboogs.weibo.login.httpclient.SinaPreLogin;

import android.content.Context;
import android.os.AsyncTask;

public class WeiboAsyncTask extends AsyncTask<Context, Integer, Boolean> {

	private String pid = "";
	private String textContent = "";
	private String mCookie = "";
	private String app_src = "3o33sO";

	public interface OnSendFinished {
		public void onSendFinished(boolean isSuccess);
	}

	private OnSendFinished mFinished;

	public WeiboAsyncTask(String cookie, String appSrc, String pid, String textContent) {
		// TODO Auto-generated constructor stub
		this.pid = pid;
		this.textContent = textContent;
		this.mCookie = cookie;
		this.app_src = appSrc;
	}

	public void setOnSendFinishedListener(OnSendFinished onSendFinished) {
		this.mFinished = onSendFinished;
	}

	@Override
	protected Boolean doInBackground(Context... params) {
		// TODO Auto-generated method stub
		BroserContent mBroserContent = BroserContent.getInstance();
		HttpPostHelper mPostHelper = new HttpPostHelper();
		SinaPreLogin sinaPreLogin = new SinaPreLogin();
		return sinaPreLogin.sendWeibo(mBroserContent, "http://widget.weibo.com/public/aj_addMblog.php", app_src, textContent, mCookie, pid);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (mFinished != null) {
			mFinished.onSendFinished(result);
		}
	}
}
