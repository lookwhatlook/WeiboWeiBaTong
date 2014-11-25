package org.zarroboogs.weibo.ui.blackmagic;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.BlackMagicLoginTask;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.ui.interfaces.AbstractAppActivity;

import com.umeng.analytics.MobclickAgent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * User: qii Date: 12-11-9
 */
public class BlackMagicActivity extends AbstractAppActivity {

	private EditText username;

	private EditText password;

	private Spinner spinner;

	private String appkey;

	private String appSecret;

	private BlackMagicLoginTask loginTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_blackmagicactivity_layout);

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle(getString(R.string.hack_login));

		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		spinner = (Spinner) findViewById(R.id.spinner);

		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tail, android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(mSpinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String[] array = getResources().getStringArray(R.array.tail_value);
				String value = array[position];
				appkey = value.substring(0, value.indexOf(","));
				appSecret = value.substring(value.indexOf(",") + 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

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
				loginTask = new BlackMagicLoginTask(this, username.getText().toString(), 
						password.getText().toString(), appkey, appSecret);
				loginTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
