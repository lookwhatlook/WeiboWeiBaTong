package org.zarroboogs.weibo.net;

import android.content.Context;
import android.os.AsyncTask;

public class RepostWeiboAsyncTask extends AsyncTask<Context, Integer, Boolean> {

	private String mid = "";
	private String textContent = "";
	private String mCookie = "";
	private String app_src = "3o33sO";

	public interface OnRepostFinished {
		public void onSendFinished(boolean isSuccess);
	}

	private OnRepostFinished mFinished;

	public RepostWeiboAsyncTask(String cookie, String appSrc, String mid, String textContent) {
		// TODO Auto-generated constructor stub
		this.mid = mid;
		this.textContent = textContent;
		this.mCookie = cookie;
		this.app_src = appSrc;
	}

	public void setRepostFinishedListener(OnRepostFinished onSendFinished) {
		this.mFinished = onSendFinished;
	}

	@Override
	protected Boolean doInBackground(Context... params) {
		// TODO Auto-generated method stub
		BroserContent mBroserContent = BroserContent.getInstance();
		HttpPostHelper mPostHelper = new HttpPostHelper();
		return mPostHelper.repostWeibo(mBroserContent, "http://widget.weibo.com/public/aj_repost.php", app_src, textContent, mCookie, mid);
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
