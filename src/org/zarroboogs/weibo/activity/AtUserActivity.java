package org.zarroboogs.weibo.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import org.zarroboogs.weibo.Constances;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.AtUserFragment;

import com.umeng.analytics.MobclickAgent;

/**
 * User: qii Date: 12-10-8
 */
public class AtUserActivity extends AbstractAppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.at_other);

		String token = getIntent().getStringExtra(Constances.TOKEN);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().replace(android.R.id.content, new AtUserFragment(token)).commit();
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
			finish();
			break;
		}
		return true;
	}
}
