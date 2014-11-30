package org.zarroboogs.weibo.setting.fragment;

import org.zarroboogs.weibo.R;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_activity_pref);
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
