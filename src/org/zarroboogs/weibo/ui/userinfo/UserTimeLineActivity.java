package org.zarroboogs.weibo.ui.userinfo;

import org.zarroboogs.weibo.Constances;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.support.utils.GlobalContext;
import org.zarroboogs.weibo.ui.interfaces.AbstractAppActivity;
import org.zarroboogs.weibo.ui.main.MainTimeLineActivity;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * User: qii Date: 13-6-21
 */
public class UserTimeLineActivity extends AbstractAppActivity {

	public static Intent newIntent(String token, UserBean userBean) {
		Intent intent = new Intent(GlobalContext.getInstance(), UserTimeLineActivity.class);
		intent.putExtra(Constances.TOKEN, token);
		intent.putExtra("user", userBean);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		String token = getIntent().getStringExtra(Constances.TOKEN);
		UserBean bean = getIntent().getParcelableExtra("user");
		getActionBar().setTitle(bean.getScreen_name());
		if (getSupportFragmentManager().findFragmentByTag(StatusesByIdTimeLineFragment.class.getName()) == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, StatusesByIdTimeLineFragment.newInstance(bean, token), StatusesByIdTimeLineFragment.class.getName())
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
