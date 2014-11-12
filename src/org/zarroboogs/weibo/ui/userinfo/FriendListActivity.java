package org.zarroboogs.weibo.ui.userinfo;

import org.zarroboogs.weibo.Constances;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.support.utils.GlobalContext;
import org.zarroboogs.weibo.ui.interfaces.AbstractAppActivity;
import org.zarroboogs.weibo.ui.main.MainTimeLineActivity;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * User: Jiang Qi Date: 12-8-16
 */
public class FriendListActivity extends AbstractAppActivity {

	private UserBean bean;

	public UserBean getUser() {
		return bean;
	}

	public static Intent newIntent(String token, UserBean userBean) {
		Intent intent = new Intent(GlobalContext.getInstance(), FriendListActivity.class);
		intent.putExtra(Constances.TOKEN, token);
		intent.putExtra("user", userBean);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(getString(R.string.following_list));
		getActionBar().setIcon(R.drawable.ic_ab_friendship);
		bean = (UserBean) getIntent().getParcelableExtra("user");
		if (getSupportFragmentManager().findFragmentByTag(FriendsListFragment.class.getName()) == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, FriendsListFragment.newInstance(bean), FriendsListFragment.class.getName()).commit();
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
}
