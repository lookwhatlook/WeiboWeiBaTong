package org.zarroboogs.weibo.ui.preference;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.ui.interfaces.AbstractAppActivity;

/**
 * User: qii Date: 12-10-24
 */
public class WaterMarkPrefActivity extends AbstractAppActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(getString(R.string.notification));

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().replace(android.R.id.content, new WaterMarkFragment()).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			intent = new Intent(this, SettingActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		return false;
	}

}
