package org.zarroboogs.weibo.ui.topic;

import org.zarroboogs.weibo.Constances;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.support.utils.GlobalContext;
import org.zarroboogs.weibo.ui.interfaces.AbstractAppActivity;
import org.zarroboogs.weibo.ui.main.MainTimeLineActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * User: qii Date: 12-11-18
 */
public class UserTopicListActivity extends AbstractAppActivity {

	public static Intent newIntent(UserBean userBean, ArrayList<String> topicList) {
		Intent intent = new Intent(GlobalContext.getInstance(), UserTopicListActivity.class);
		intent.putExtra(Constances.USERBEAN, userBean);
		intent.putStringArrayListExtra("topicList", topicList);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		UserBean userBean = (UserBean) getIntent().getParcelableExtra(Constances.USERBEAN);
		ArrayList<String> topicList = getIntent().getStringArrayListExtra("topicList");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(getString(R.string.topic));

		if (savedInstanceState == null) {
			UserTopicListFragment fragment;
			if (topicList != null) {
				fragment = new UserTopicListFragment(userBean, topicList);
			} else {
				fragment = new UserTopicListFragment(userBean);
			}
			getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
		}
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
		return false;
	}
}
