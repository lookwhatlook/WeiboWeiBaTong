package org.zarroboogs.util.net;

import org.zarroboogs.utils.net.BroserContent;

import android.os.AsyncTask;

public class FetchWeiBoAsyncTask extends AsyncTask<Void, Integer, String> {

	public static interface OnFetchDoneListener {
		public void onFetchDone(String isSuccess);
	}

	public OnFetchDoneListener mInSuccessListener;

	public FetchWeiBoAsyncTask(OnFetchDoneListener listener) {
		// TODO Auto-generated constructor stub
		this.mInSuccessListener = listener;
	}

	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		BroserContent mBroserContent = BroserContent.getInstance();
		HttpGetHelper mGetHelper = new HttpGetHelper();
		return mGetHelper.getWeiboWeiba(mBroserContent);
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		this.mInSuccessListener.onFetchDone(result);
	}

}
