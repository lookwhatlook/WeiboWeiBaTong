package org.zarroboogs.weibo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.WeibaGson;
import org.zarroboogs.weibo.bean.WeibaTree;
import org.zarroboogs.weibo.bean.WeiboWeiba;
import org.zarroboogs.weibo.database.AccountDBTask;
import org.zarroboogs.weibo.database.AppsrcDatabaseManager;
import org.zarroboogs.weibo.net.ExecuterManager;
import org.zarroboogs.weibo.net.FetchWeiBoAsyncTask;
import org.zarroboogs.weibo.net.LoginWeiboAsyncTask;
import org.zarroboogs.weibo.net.UploadThread;
import org.zarroboogs.weibo.net.UploadThread.WaterMark.POS;
import org.zarroboogs.weibo.net.WeiboAsyncTask;
import org.zarroboogs.weibo.net.FetchWeiBoAsyncTask.OnFetchDoneListener;
import org.zarroboogs.weibo.net.LoginWeiboAsyncTask.LoginCallBack;
import org.zarroboogs.weibo.net.UploadThread.WaterMark;
import org.zarroboogs.weibo.net.WeiboAsyncTask.OnSendFinished;
import org.zarroboogs.weibo.selectphoto.ImgFileListActivity;
import org.zarroboogs.weibo.selectphoto.SendImgData;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.utils.SendBitmapWorkerTask;
import org.zarroboogs.weibo.utils.SendBitmapWorkerTask.OnCacheDoneListener;
import org.zarroboogs.weibo.utils.Utility;
import org.zarroboogs.weibo.utils.WeiBaNetUtils;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshListView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class WeiboMainActivity extends SharedPreferenceActivity implements LoginCallBack, OnClickListener, OnGlobalLayoutListener, OnSendFinished,
		OnItemClickListener, OnSharedPreferenceChangeListener {

	String pidC = "";
	RelativeLayout mEmotionRelativeLayout;
	ExecuterManager manager = new ExecuterManager();
	MyOnUploaded myOnUploaded = new MyOnUploaded();
	List<String> mList = new ArrayList<String>();
	Map<Integer, String> map = new HashMap<Integer, String>();
	InputMethodManager imm = null;
	EditText mEditText;
	Handler mHandler = new Handler();
	RelativeLayout mRootView;

	RelativeLayout editTextLayout;
	ImageButton mSelectPhoto;
	ImageButton mSendBtn;
	ImageButton smileButton;

	Button appSrcBtn;
	TableLayout sendImgTL;

	AccountBean mAccountBean;

	private boolean isKeyBoardShowed = false;
	private ScrollView mEditPicScrollView;
	private RelativeLayout mRow001;
	private RelativeLayout mRow002;
	private RelativeLayout mRow003;

	private ImageView mImageView001;
	private ImageView mImageView002;
	private ImageView mImageView003;
	private ImageView mImageView004;
	private ImageView mImageView005;
	private ImageView mImageView006;
	private ImageView mImageView007;
	private ImageView mImageView008;
	private ImageView mImageView009;

	private TextView weiTextCountTV;

	ArrayList<ImageView> mSelectImageViews = new ArrayList<ImageView>();

	ArrayList<ImageView> mEmotionArrayList = new ArrayList<ImageView>();
	ProgressDialog mDialog;

	Toast mEmptyToast;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	PullToRefreshListView listView;
	ChangeWeibaAdapter listAdapter;
	List<WeiboWeiba> listdata = new ArrayList<WeiboWeiba>();
	SlidingMenu menu;

	AppsrcDatabaseManager mDBmanager = null;// new
											// AppsrcDatabaseManager(getApplicationContext());
	String atContent = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getAppSrcSharedPreference().registerOnSharedPreferenceChangeListener(this);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		setContentView(R.layout.activity_main);

		mAccountBean = getIntent().getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
		atContent = getIntent().getStringExtra("content");

		mEmptyToast = Toast.makeText(getApplicationContext(), R.string.text_is_empty, Toast.LENGTH_SHORT);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage(getString(R.string.send_wei_ing));
		mDialog.setCancelable(false);

		mEditPicScrollView = (ScrollView) findViewById(R.id.scrollView1);
		editTextLayout = (RelativeLayout) findViewById(R.id.editTextLayout);

		weiTextCountTV = (TextView) findViewById(R.id.weiTextCountTV);
		sendImgTL = (TableLayout) findViewById(R.id.sendImgTL_ref);
		sendImgTL.setVisibility(View.GONE);

		mRow001 = (RelativeLayout) findViewById(R.id.sendPicRow01);
		mRow002 = (RelativeLayout) findViewById(R.id.sendPicRow02);
		mRow003 = (RelativeLayout) findViewById(R.id.sendPicRow03);

		appSrcBtn = (Button) findViewById(R.id.appSrcBtn);
		appSrcBtn.setText(getWeiba().getText());
		Log.d("MAIN_", "" + sendImgTL.getChildCount());
		mImageView001 = (ImageView) findViewById(R.id.IVRow101);
		mImageView002 = (ImageView) findViewById(R.id.IVRow102);
		mImageView003 = (ImageView) findViewById(R.id.IVRow103);
		mImageView004 = (ImageView) findViewById(R.id.IVRow201);
		mImageView005 = (ImageView) findViewById(R.id.IVRow202);
		mImageView006 = (ImageView) findViewById(R.id.IVRow203);
		mImageView007 = (ImageView) findViewById(R.id.IVRow301);
		mImageView008 = (ImageView) findViewById(R.id.IVRow302);
		mImageView009 = (ImageView) findViewById(R.id.IVRow303);

		mSelectImageViews.add(mImageView001);
		mSelectImageViews.add(mImageView002);
		mSelectImageViews.add(mImageView003);
		mSelectImageViews.add(mImageView004);
		mSelectImageViews.add(mImageView005);
		mSelectImageViews.add(mImageView006);
		mSelectImageViews.add(mImageView007);
		mSelectImageViews.add(mImageView008);
		mSelectImageViews.add(mImageView009);

		mSelectPhoto = (ImageButton) findViewById(R.id.imageButton1);
		mRootView = (RelativeLayout) findViewById(R.id.container);
		mEditText = (EditText) findViewById(R.id.weiboContentET);
		mEmotionRelativeLayout = (RelativeLayout) findViewById(R.id.smileLayout_ref);
		smileButton = (ImageButton) findViewById(R.id.smileImgButton);
		mSendBtn = (ImageButton) findViewById(R.id.sendWeiBoBtn);

		findAllEmotionImageView((ViewGroup) findViewById(R.id.emotionTL));
		mSelectPhoto.setOnClickListener(this);
		smileButton.setOnClickListener(this);
		mSendBtn.setOnClickListener(this);
		appSrcBtn.setOnClickListener(this);
		mEditPicScrollView.setOnClickListener(this);
		editTextLayout.setOnClickListener(this);
		mEditText.addTextChangedListener(watcher);

		if (!TextUtils.isEmpty(atContent)) {
			mEditText.setText(atContent + " ");
			mEditText.setSelection(mEditText.getEditableText().toString().length());
		}

		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow_slidingmenu);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.activity_main_left_menu);

		mDBmanager = new AppsrcDatabaseManager(getApplicationContext());

		listAdapter = new ChangeWeibaAdapter(this);
		listView = (PullToRefreshListView) menu.findViewById(R.id.left_menu_list_view);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
					listView.setRefreshing();
					fetchWeiBa();
				} else {
					listView.post(new Runnable() {
						public void run() {
							listView.onRefreshComplete();
						}
					});
					Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
					;
				}

			}
		});
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

		menu.setOnOpenListener(new OnOpenListener() {

			@Override
			public void onOpen() {
				List<WeiboWeiba> list = mDBmanager.fetchAllAppsrc();
				if (isKeyBoardShowed) {
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
				}
				if (list.size() == 0) {
					if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
						fetchWeiBa();
					} else {
						Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
						;
					}
				} else {
					listAdapter.setWeibas(list);
				}
			}
		});

	}

	private void fetchWeiBa() {
		showDialogForWeiBo();
		FetchWeiBoAsyncTask mFetchWeiBoAsyncTask = new FetchWeiBoAsyncTask(new OnFetchDoneListener() {

			@Override
			public void onFetchDone(String isSuccess) {
				// TODO Auto-generated method stub

				Gson gson = new Gson();
				WeibaGson weibaGson = gson.fromJson(isSuccess, WeibaGson.class);
				List<WeibaTree> weibaTrees = weibaGson.getData();

				for (WeibaTree weibaTree : weibaTrees) {
					List<WeiboWeiba> weibas = weibaTree.getData();
					for (WeiboWeiba weiba : weibas) {
						if (mDBmanager.searchAppsrcByCode(weiba.getCode()) == null) {
							mDBmanager.insertCategoryTree(0, weiba.getCode(), weiba.getText());
						}
					}
				}

				listView.onRefreshComplete();
				listAdapter.setWeibas(mDBmanager.fetchAllAppsrc());
				hideDialogForWeiBo();
			}
		});
		mFetchWeiBoAsyncTask.execute();
	}

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String charSequence = mEditText.getText().toString();
			int count = Utility.length(charSequence);
			String text = count <= 0 ? getString(R.string.send_weibo) : count + "";
			weiTextCountTV.setText(text);
			if (count > 140) {
				weiTextCountTV.setTextColor(Color.RED);
			} else {
				weiTextCountTV.setTextColor(Color.BLACK);
			}
		}
	};

	public static int calculateWeiboLength(CharSequence c) {

		int len = 0;
		for (int i = 0; i < c.length(); i++) {
			int temp = (int) c.charAt(i);
			if (temp > 0 && temp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		manager.shutDown();
		mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		ImageLoader.getInstance().stop();
		getAppSrcSharedPreference().unregisterOnSharedPreferenceChangeListener(this);
	}

	private void findAllEmotionImageView(ViewGroup vg) {
		int count = vg.getChildCount();
		for (int i = 0; i < count; i++) {
			View v = vg.getChildAt(i);
			if (v instanceof TableRow) {
				findAllEmotionImageView((TableRow) v);
			} else {
				((ImageView) v).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String content = mEditText.getText().toString() + ((ImageView) v).getContentDescription();
						mEditText.setText(content);
						mEditText.setSelection(content.length());
					}
				});
				mEmotionArrayList.add((ImageView) v);
			}
		}
	}

	class MyOnUploaded implements org.zarroboogs.weibo.net.UploadThread.OnUploaded {

		@Override
		public void onUploaded(UploadThread r, String pid) {
			// TODO Auto-generated method stub
			if (pid == "") {
				manager.reAddJobs(r);
			} else {
				manager.reduceJobs();
				map.put(r.getId(), pid);
				if (manager.isAllFinished()) {
					Log.d("WEIBO_", pid);

					pidC = buildUploadPicIds(map);

					String text = mEditText.getText().toString();
					if (TextUtils.isEmpty(text)) {
						text = getString(R.string.default_text_pic_weibo);
					}

					WeiboAsyncTask mAsyncTask = new WeiboAsyncTask(mAccountBean.getCookie(), getWeiba().getCode(), pidC, text);
					mAsyncTask.setOnSendFinishedListener(WeiboMainActivity.this);
					mAsyncTask.execute(getApplicationContext());
				}

			}

		}

		private String buildUploadPicIds(Map<Integer, String> map) {
			String pidC = "";
			for (int i = 0; i < map.size(); i++) {
				if (i == 0) {
					pidC = map.get(i) + ",";
				} else if (i == map.size() - 1) {
					pidC = pidC + map.get(i);
				} else {
					pidC = pidC + map.get(i) + ",";
				}
			}
			return pidC;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == ChangeWeibaActivity.REQUEST) {
			appSrcBtn.setText(getWeiba().getText());
		} else if (resultCode == RESULT_OK && requestCode == ImgFileListActivity.REQUEST_CODE) {
			SendImgData sid = SendImgData.getInstance();
			ArrayList<String> imgs = sid.getSendImgs();
			if (imgs.size() > 0) {
				sendImgTL.setVisibility(View.VISIBLE);
			}
			for (int i = 0; i < imgs.size(); i++) {
				ImageView iv = mSelectImageViews.get(i);
				mImageLoader.displayImage("file://" + imgs.get(i), iv, options);
				iv.setVisibility(View.VISIBLE);
				// BitmapWorkerTask mWorkerTask = new BitmapWorkerTask(iv);
				// mWorkerTask.execute(imgs.get(i));
			}

			int offset = imgs.size() % 3;
			int row = offset == 0 ? imgs.size() / 3 : imgs.size() / 3 + 1;
			if (imgs.size() > 0) {
				switch (row) {
				case 1:
					mRow001.setVisibility(View.VISIBLE);
					break;
				case 2: {
					mRow001.setVisibility(View.VISIBLE);
					mRow002.setVisibility(View.VISIBLE);
					break;
				}

				case 3: {
					mRow001.setVisibility(View.VISIBLE);
					mRow002.setVisibility(View.VISIBLE);
					mRow003.setVisibility(View.VISIBLE);
					break;
				}

				default:
					break;
				}
			}

			for (String s : imgs) {
				Log.d("IMG_", "   " + s + "/////" + mSelectImageViews.size());
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!(mEmotionRelativeLayout.getVisibility() == View.GONE)) {
			mEmotionRelativeLayout.setVisibility(View.GONE);
			return;
		}
		super.onBackPressed();
	}

	public void startLogIn() {
		hideDialogForWeiBo();
		Intent intent = new Intent();
		intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, mAccountBean);
		intent.setClass(WeiboMainActivity.this, WebViewActivity.class);
		startActivity(intent);
	}

	@Override
	public void onDoLogInFinish(boolean isSuccess) {
		if (!isSuccess) {
			startLogIn();
		} else {
			final SendImgData sendImgData = SendImgData.getInstance();

			ArrayList<String> send = sendImgData.getSendImgs();
			final int count = send.size();

			if (count > 0) {
				for (int i = 0; i < send.size(); i++) {
					SendBitmapWorkerTask sendBitmapWorkerTask = new SendBitmapWorkerTask(getApplicationContext(), new OnCacheDoneListener() {
						@Override
						public void onCacheDone(String newFile) {
							// TODO Auto-generated method stub
							Log.d("RESIZE_PIC", "" + newFile);
							sendImgData.addReSizeImg(newFile);
							if (sendImgData.getReSizeImgs().size() == count) {
								uploadPictures(sendImgData.getReSizeImgs());
							}
						}
					});
					sendBitmapWorkerTask.execute(send.get(i));
				}
			} else {
				String text = mEditText.getText().toString();
				if (TextUtils.isEmpty(text)) {
					mEmptyToast.show();
					return;
				}
				// String cookieInDB = mAccountBean.getCookie();
				// Log.d("COOKIE_STORED: ", /*cookieInDB.equals(cookieInSp) +*/
				// "   cookieInSp::" + cookieInSp + " \r\n " + "cookieInDB::" +
				// cookieInDB);
				WeiboAsyncTask mAsyncTask = new WeiboAsyncTask(mAccountBean.getCookieInDB(), getWeiba().getCode(), pidC, text);
				mAsyncTask.setOnSendFinishedListener(this);
				mAsyncTask.execute(getApplicationContext());
			}
		}
	}

	private void uploadPictures(ArrayList<String> send) {
		UserBean userBean = AccountDBTask.getUserBean(mAccountBean.getUid());
		String url = "";
		if (!TextUtils.isEmpty(userBean.getDomain())) {
			url = "weibo.com/" + userBean.getDomain();
		} else {
			url = "weibo.com/u/" + mAccountBean.getUid();
		}
		WaterMark mark = new WaterMark(mAccountBean.getUsernick(), url);
		final int count = send.size();
		for (int i = 0; i < count; i++) {
			UploadThread ut000 = new UploadThread(mark, i, myOnUploaded, mAccountBean.getCookie(), send.get(i));
			manager.addJobs(ut000);
		}
	}

	private boolean checkDataEmpty() {
		if (TextUtils.isEmpty(mEditText.getText().toString()) && SendImgData.getInstance().getSendImgs().size() < 1) {
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.editTextLayout: {
			mEditText.performClick();
			break;
		}
		case R.id.scrollView1: {
			mEditText.performClick();
			break;
		}
		case R.id.appSrcBtn: {
			// if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
			// Intent weibaIntent = new Intent(this, ChangeWeibaActivity.class);
			// startActivityForResult(weibaIntent, ChangeWeibaActivity.REQUEST);
			// }else {
			// Toast.makeText(getApplicationContext(),
			// R.string.net_not_avaliable,
			// Toast.LENGTH_SHORT).show();;
			// }
			if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
				if (isKeyBoardShowed) {
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
				}
				menu.toggle();
			} else {
				Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case R.id.sendWeiBoBtn: {
			if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
				if (checkDataEmpty()) {
					mEmptyToast.show();
				} else {
					showDialogForWeiBo();
					String cookieInDB = mAccountBean.getCookieInDB();
					Log.d("LogIn-CookieInDB", "UID: " + mAccountBean.getUid() + "  Uname:" + mAccountBean.getUname() + "   [" + cookieInDB + "]");
					LoginWeiboAsyncTask mAsyncTask = new LoginWeiboAsyncTask(WeiboMainActivity.this, cookieInDB);
					mAsyncTask.execute(getApplicationContext());
				}
			} else {
				Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
				;
			}

			break;
		}
		case R.id.smileImgButton: {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			mHandler.postDelayed(new Runnable() {
				public void run() {
					if (mEmotionRelativeLayout.getVisibility() == View.GONE) {
						mEmotionRelativeLayout.setVisibility(View.VISIBLE);
					} else {
						mEmotionRelativeLayout.setVisibility(View.GONE);
					}
				}
			}, 100);

			break;
		}
		case R.id.imageButton1: {
			Intent mIntent = new Intent(getApplicationContext(), ImgFileListActivity.class);
			startActivityForResult(mIntent, ImgFileListActivity.REQUEST_CODE);
			break;
		}
		default:
			break;
		}

	}

	@Override
	public void onGlobalLayout() {
		// TODO Auto-generated method stub

		Rect r = new Rect();
		mEmotionRelativeLayout.getWindowVisibleDisplayFrame(r);

		int heightDiff = mEmotionRelativeLayout.getRootView().getHeight() - (r.bottom - r.top);
		if (heightDiff > 100) {
			// if more than 100 pixels, its probably a keyboard...
			Log.d("WEIBO_INPUT", "++++++++");
			mEmotionRelativeLayout.setVisibility(View.GONE);
			isKeyBoardShowed = true;
		} else {
			Log.d("WEIBO_INPUT", "---------");
			isKeyBoardShowed = false;
		}

	}

	private void showDialogForWeiBo() {
		if (!mDialog.isShowing()) {
			mDialog.show();
		}

	}

	private void hideDialogForWeiBo() {
		mDialog.cancel();
		mDialog.hide();
	}

	class WeiBaCacheFile implements FilenameFilter {

		@Override
		public boolean accept(File dir, String filename) {
			// TODO Auto-generated method stub
			return filename.startsWith("WEI-");
		}

	}

	@Override
	public void onSendFinished(boolean isSuccess) {
		// TODO Auto-generated method stub
		hideDialogForWeiBo();
		if (isSuccess) {
			Toast.makeText(getApplicationContext(), R.string.send_wei_success, Toast.LENGTH_SHORT).show();
			mEditText.setText("");
			SendImgData sid = SendImgData.getInstance();
			sid.clearSendImgs();
			sid.clearReSizeImgs();
			sendImgTL.setVisibility(View.GONE);
			for (int i = 0; i < 9; i++) {
				mSelectImageViews.get(i).setVisibility(View.INVISIBLE);
			}

			File[] cacheFiles = getExternalCacheDir().listFiles(new WeiBaCacheFile());
			for (File file : cacheFiles) {
				Log.d("LIST_CAXCHE", " " + file.getName());
				file.delete();
			}
		} else {
			Toast.makeText(getApplicationContext(), R.string.send_wei_failed, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		super.onSharedPreferenceChanged(sharedPreferences, key);
		appSrcBtn.setText(getWeiba().getText());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		WeiboWeiba weiba = ((WeiboWeiba) parent.getItemAtPosition(position));
		Log.d("CLICK", "" + weiba);
		saveWeiba(weiba);
		menu.toggle();
	}
}
