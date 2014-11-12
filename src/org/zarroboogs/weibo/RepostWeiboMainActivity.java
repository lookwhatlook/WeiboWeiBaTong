package org.zarroboogs.weibo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.net.ExecuterManager;
import org.zarroboogs.weibo.net.LoginWeiboAsyncTask;
import org.zarroboogs.weibo.net.RepostWeiboAsyncTask;
import org.zarroboogs.weibo.net.LoginWeiboAsyncTask.LoginCallBack;
import org.zarroboogs.weibo.net.RepostWeiboAsyncTask.OnRepostFinished;
import org.zarroboogs.weibo.selectphoto.ImgFileListActivity;
import org.zarroboogs.weibo.selectphoto.SendImgData;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.utils.Utility;
import org.zarroboogs.weibo.utils.WeiBaNetUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class RepostWeiboMainActivity extends SharedPreferenceActivity implements LoginCallBack, OnClickListener, OnGlobalLayoutListener, OnRepostFinished {
	private MessageBean msg;
	String pidC = "";
	RelativeLayout mEmotionRelativeLayout;
	ExecuterManager manager = new ExecuterManager();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		setContentView(R.layout.activity_main);

		mAccountBean = getIntent().getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
		Log.d("RpostWeiBo_activity", "AccountBean == null ? : " + (mAccountBean == null));
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

		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		Intent intent = getIntent();
		handleNormalOperation(intent);
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
		intent.setClass(RepostWeiboMainActivity.this, WebViewActivity.class);
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

			String text = mEditText.getText().toString();
			if (TextUtils.isEmpty(text)) {
				mEmptyToast.show();
				return;
			}
			RepostWeiboAsyncTask mAsyncTask = new RepostWeiboAsyncTask(mAccountBean.getCookie(), getWeiba().getCode(), msg.getId(), text);
			mAsyncTask.setRepostFinishedListener(this);
			mAsyncTask.execute(getApplicationContext());

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
			if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
				Intent weibaIntent = new Intent(this, ChangeWeibaActivity.class);
				startActivityForResult(weibaIntent, ChangeWeibaActivity.REQUEST);
			} else {
				Toast.makeText(getApplicationContext(), R.string.net_not_avaliable, Toast.LENGTH_SHORT).show();
				;
			}

			break;
		}
		case R.id.sendWeiBoBtn: {
			if (WeiBaNetUtils.isNetworkAvaliable(getApplicationContext())) {
				if (checkDataEmpty()) {
					mEmptyToast.show();
				} else {
					showDialogForWeiBo();
					LoginWeiboAsyncTask mAsyncTask = new LoginWeiboAsyncTask(RepostWeiboMainActivity.this, mAccountBean.getCookieInDB());
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
		} else {
			Log.d("WEIBO_INPUT", "---------");
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

	private void handleNormalOperation(Intent intent) {

		msg = (MessageBean) intent.getParcelableExtra("msg");

		if (msg.getRetweeted_status() != null) {
			mEditText.setText("//@" + msg.getUser().getScreen_name() + ": " + msg.getText());
			mEditText.setHint("//@" + msg.getRetweeted_status().getUser().getScreen_name() + "：" + msg.getRetweeted_status().getText());
		} else {
			mEditText.setHint("@" + msg.getUser().getScreen_name() + "：" + msg.getText());
		}
		mEditText.setSelection(0);
	}
}
