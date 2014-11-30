package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.fragment.DMConversationListFragment;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * User: qii Date: 12-11-10
 */
public class DMActivity extends AbstractAppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		UserBean bean = (UserBean) getIntent().getParcelableExtra("user");

		setTitle(bean.getScreen_name());
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, DMConversationListFragment.newInstance(bean), DMConversationListFragment.class.getName()).commit();
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
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			intent = MainTimeLineActivity.newIntent();
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		DMConversationListFragment fragment = (DMConversationListFragment) getSupportFragmentManager().findFragmentByTag(
				DMConversationListFragment.class.getName());
		if (fragment != null) {
			if (!fragment.isSmileyPanelClosed()) {
				fragment.closeSmileyPanel();
			} else {
				super.onBackPressed();
			}
		} else {
			super.onBackPressed();
		}
	}
}
