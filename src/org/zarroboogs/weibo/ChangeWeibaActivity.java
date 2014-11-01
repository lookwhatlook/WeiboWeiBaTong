package org.zarroboogs.weibo;

import java.util.ArrayList;
import java.util.List;

import org.zarroboogs.weibo.bean.WeibaGson;
import org.zarroboogs.weibo.bean.WeibaTree;
import org.zarroboogs.weibo.bean.WeiboWeiba;
import org.zarroboogs.weibo.net.FetchWeiBoAsyncTask;
import org.zarroboogs.weibo.net.FetchWeiBoAsyncTask.OnFetchDoneListener;

import com.google.gson.Gson;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChangeWeibaActivity extends SharedPreferenceActivity implements OnItemClickListener {

	public static final int REQUEST = 0x0002;
	ListView listView;
	ChangeWeibaAdapter listAdapter;
	List<WeiboWeiba> listdata = new ArrayList<WeiboWeiba>();

	ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_weiba_activity_layout);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage(getString(R.string.fetch_new_weiba));
		mDialog.setCancelable(false);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.show();

		listView = (ListView) findViewById(R.id.weibaListView);
		listAdapter = new ChangeWeibaAdapter(this);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

		fetchWeiBa();
	}

	private void showDialogForWeiBo() {
		if (!mDialog.isShowing()) {
			mDialog.show();
		}

	}

	private void hideDialogForWeiBo() {
		mDialog.cancel();
		mDialog.hide();
	}

	private void fetchWeiBa() {
		showDialogForWeiBo();
		FetchWeiBoAsyncTask mFetchWeiBoAsyncTask = new FetchWeiBoAsyncTask(new OnFetchDoneListener() {

			@Override
			public void onFetchDone(String isSuccess) {
				// TODO Auto-generated method stub

				Gson gson = new Gson();
				WeibaGson weibaGson = gson.fromJson(isSuccess, WeibaGson.class);
				List<WeibaTree> weibaTrees = weibaGson.getData();
				for (WeibaTree weibaTree : weibaTrees) {
					List<WeiboWeiba> weibas = weibaTree.getData();
					listdata.addAll(weibas);
					for (WeiboWeiba weiba : weibas) {
						Log.d("FETCH_WEIBA", "Name:" + weiba.getText() + "  Code:" + weiba.getCode());
					}
				}
				listAdapter.setWeibas(listdata);
				hideDialogForWeiBo();
			}
		});
		mFetchWeiBoAsyncTask.execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_OK);
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		WeiboWeiba weiba = ((WeiboWeiba) arg0.getItemAtPosition(arg2));
		Log.d("CLICK", "" + weiba);
		saveWeiba(weiba);
		setResult(RESULT_OK);
		finish();
	}
}
