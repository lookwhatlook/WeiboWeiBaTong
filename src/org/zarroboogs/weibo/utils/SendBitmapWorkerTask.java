package org.zarroboogs.weibo.utils;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class SendBitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

	public static interface OnCacheDoneListener {
		public void onCacheDone(String newFile);
	}

	private OnCacheDoneListener mCacheDoneListener;

	private String data = null;
	private File cacheDir;
	private Context mContext;

	public SendBitmapWorkerTask(Context context, OnCacheDoneListener listener) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		mContext = context;
		this.mCacheDoneListener = listener;
		cacheDir = mContext.getExternalCacheDir();
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(String... params) {
		data = params[0];
		return decodeSampledBitmapFromFile(data, 1024);
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		String newFile = BitmapUtils.saveBitmapToFile(cacheDir, "WEI-" + makeMD5(data), bitmap, ".jpg");
		if (mCacheDoneListener != null) {
			mCacheDoneListener.onCacheDone(newFile);
		}
	}

	public static String makeMD5(String password) {
		MessageDigest md;
		try {
			// 生成一个MD5加密计算摘要
			md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(password.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			String pwd = new BigInteger(1, md.digest()).toString(16);
			return pwd;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 如果失败要返回null
		return null;
	}

	public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (width > reqWidth) {
			inSampleSize = Math.round((float) width / (float) reqWidth);
		}
		return inSampleSize;
	}
}
