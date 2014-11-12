package org.zarroboogs.weibo.ui.browser;

import org.zarroboogs.weibo.Constances;
import org.zarroboogs.weibo.bean.CommentBean;
import org.zarroboogs.weibo.ui.interfaces.AbstractAppActivity;
import org.zarroboogs.weibo.ui.main.MainTimeLineActivity;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * User: qii Date: 12-9-19
 */
public class BrowserCommentActivity extends AbstractAppActivity {

	private String token;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(Constances.TOKEN, token);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		CommentBean bean = (CommentBean) intent.getParcelableExtra("comment");
		token = intent.getStringExtra(Constances.TOKEN);

		if (getFragmentManager().findFragmentByTag(BrowserCommentActivity.class.getName()) == null) {
			getFragmentManager().beginTransaction().replace(android.R.id.content, new BrowserCommentFragment(bean), BrowserCommentFragment.class.getName())
					.commit();
		}

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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = MainTimeLineActivity.newIntent();
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
