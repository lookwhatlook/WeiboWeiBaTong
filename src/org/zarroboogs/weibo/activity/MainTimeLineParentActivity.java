package org.zarroboogs.weibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.slidingmenu.lib.app.SlidingFragmentActivity;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;

import java.lang.reflect.Field;

/**
 * User: qii Date: 13-1-22
 */
public class MainTimeLineParentActivity extends SlidingFragmentActivity {

	private int theme = 0;

	@Override
	protected void onResume() {
		super.onResume();
		GlobalContext.getInstance().setCurrentRunningActivity(this);

		if (theme == SettingUtils.getAppTheme()) {

		} else {
			reload();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (GlobalContext.getInstance().getCurrentRunningActivity() == this) {
			GlobalContext.getInstance().setCurrentRunningActivity(null);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("theme", theme);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			theme = SettingUtils.getAppTheme();
		} else {
			theme = savedInstanceState.getInt("theme");
		}
		setTheme(theme);
		super.onCreate(savedInstanceState);
		forceShowActionBarOverflowMenu();
		GlobalContext.getInstance().setActivity(this);
		TimeLineBitmapDownloader.refreshThemePictureBackground();
		
	}

	private void forceShowActionBarOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ignored) {

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}



	public void reload() {

		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();

		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	protected void dealWithException(WeiboException e) {
		Toast.makeText(this, e.getError(), Toast.LENGTH_SHORT).show();
	}
}
