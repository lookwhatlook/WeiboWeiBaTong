package org.zarroboogs.weibo.fragment;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * User: qii Date: 12-12-30
 */
public class AbstractAppFragment extends FixedOnActivityResultBugFragment {
	/**
	 * when activity is recycled by system, isFirstTimeStartFlag will be reset
	 * to default true, when activity is recreated because a configuration
	 * change for example screen rotate, isFirstTimeStartFlag will stay false
	 */
	private boolean isFirstTimeStartFlag = true;

	protected final int FIRST_TIME_START = 0; // when activity is first time
												// start
	protected final int SCREEN_ROTATE = 1; // when activity is destroyed and
											// recreated because a configuration
											// change, see
											// setRetainInstance(boolean retain)
	protected final int ACTIVITY_DESTROY_AND_CREATE = 2; // when activity is
															// destroyed because
															// memory is too
															// low, recycled by
															// android system

	protected int getCurrentState(Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			isFirstTimeStartFlag = false;
			return ACTIVITY_DESTROY_AND_CREATE;
		}

		if (!isFirstTimeStartFlag) {
			return SCREEN_ROTATE;
		}

		isFirstTimeStartFlag = false;
		return FIRST_TIME_START;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setInsets(getActivity(), container);
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	public static void setInsets(Activity context, View view) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
			return;
		SystemBarTintManager tintManager = new SystemBarTintManager(context);
		SystemBarTintManager.SystemBarConfig config = tintManager
				.getConfig();
		view.setPadding(0, config.getPixelInsetTop(true),
				config.getPixelInsetRight(), config.getPixelInsetBottom());
	}
	
}
