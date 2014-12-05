package org.zarroboogs.weibo.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;

import java.lang.reflect.Field;

/**
 * User: Jiang Qi Date: 12-7-31
 */
public class AbstractAppActivity extends FragmentActivity {

	protected int theme = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			theme = SettingUtils.getAppTheme();
		} else {
			theme = savedInstanceState.getInt("theme");
		}
		setTheme(theme);

		super.onCreate(savedInstanceState);
		forceShowActionBarOverflowMenu();
		GlobalContext.getInstance().setActivity(this);
		
	}


	@Override
	protected void onResume() {
		super.onResume();
		GlobalContext.getInstance().setCurrentRunningActivity(this);

		if (theme != SettingUtils.getAppTheme()) {
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




	private void reload() {

		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();

		overridePendingTransition(0, 0);
		startActivity(intent);
		TimeLineBitmapDownloader.refreshThemePictureBackground();
	}

	public TimeLineBitmapDownloader getBitmapDownloader() {
		return TimeLineBitmapDownloader.getInstance();
	}

	protected void dealWithException(WeiboException e) {
		Toast.makeText(this, e.getError(), Toast.LENGTH_SHORT).show();
	}
}
