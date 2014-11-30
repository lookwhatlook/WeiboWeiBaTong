package org.zarroboogs.weibo.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
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
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		super.onCreate(savedInstanceState);
		forceShowActionBarOverflowMenu();
		GlobalContext.getInstance().setActivity(this);
		TimeLineBitmapDownloader.refreshThemePictureBackground();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(0xFF01579b);
		}


	}

	@TargetApi(19) 
	public void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
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
