package org.zarroboogs.weibo.net;

import java.util.concurrent.ExecutorService;

import android.text.TextUtils;

public class UploadThread implements Runnable {
	private WaterMark mWaterMark;

	public static class WaterMark {
		public static enum POS{
			BOTTOM_RIGHT,
			BOTTOM_CENTER,
			CENTER
		}
		// &marks=1&markpos=1&logo=1&nick=%40andforce&url=weibo.com/u/2294141594
		boolean isEnaable = true;
		int markpos = 1;
		String nick = "";
		String url = "";

		public WaterMark(boolean isEnable, POS pos, String nick, String url) {
			this.isEnaable = isEnable;
			switch (pos) {
				case BOTTOM_RIGHT:
					this.markpos = 1;
					break;
				case BOTTOM_CENTER: {
					this.markpos = 2;
					break;
				}
				case CENTER: {
					this.markpos = 3;
					break;
				}
	
				default:
					this.markpos = 1;
					break;
			}
			this.markpos = markpos;
			this.nick = nick;
			this.url = url;
		}

		public String buildMark() {
			if (isEnaable) {
				return "&marks=1&markpos=" + markpos + "&logo=1&nick=%40" + nick + "&url=" + url;
			} else {
				return "&marks=0";
			}
		}
	}

	public static interface OnUploaded {
		public void onUploaded(UploadThread r, String pid);
	}

	private OnUploaded mOnUploaded;

	private String mCookie = "";
	private String mPic = "";
	private int counter = 3;
	private int id = -1;

	private BroserContent mBroserContent = BroserContent.getInstance();
	private HttpPostHelper mPostHelper = new HttpPostHelper();

	public UploadThread(WaterMark waterMark, int id, OnUploaded ou, String cookie, String pic) {
		this.mWaterMark = waterMark;
		this.id = id;
		this.mCookie = cookie;
		this.mPic = pic;
		this.mOnUploaded = ou;
	}

	public int getId() {
		return id;
	}

	public void addToThread(ExecutorService es) {
		if (--counter > 0) {
			es.execute(this);
		}
	}

	@Override
	public void run() {
		// mPostHelper...sendWeibo(mBroserContent,
		// "http://widget.weibo.com/public/aj_addMblog.php", "3o33sO",
		// "tttttttt", mPreferences.getString("cookie", ""));
		// http://picupload.service.weibo.com/interface/pic_upload.php?app=miniblog&data=1
		// &url=weibo.com/u/2294141594&
		// markpos=1&logo=1&nick=%40BUGVSAPI&marks=1&mime=image/jpeg&ct=0.714141929987818
		String markUrl = "http://picupload.service.weibo.com/interface/pic_upload.php?" + "app=miniblog&data=1" + mWaterMark.buildMark()
				+ "&mime=image/png&ct=0.2805887470021844";
		String unMark = "http://picupload.service.weibo.com/interface/pic_upload.php?app=" + "miniblog&data=1&mime=image/png&ct=0.2805887470021844";
		String a = mPostHelper.uploadPicToWeibo(mBroserContent, markUrl, mCookie, mPic);
		mOnUploaded.onUploaded(this, a);
	}

}
