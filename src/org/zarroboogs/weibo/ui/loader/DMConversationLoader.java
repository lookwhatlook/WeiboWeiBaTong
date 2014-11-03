package org.zarroboogs.weibo.ui.loader;

import android.content.Context;

import org.zarroboogs.weibo.bean.data.DMListBean;
import org.zarroboogs.weibo.dao.DMConversationDao;
import org.zarroboogs.weibo.net.WeiboException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: qii Date: 13-5-15
 */
public class DMConversationLoader extends AbstractAsyncNetRequestTaskLoader<DMListBean> {

	private static Lock lock = new ReentrantLock();

	private String token;
	private String uid;
	private String page;

	public DMConversationLoader(Context context, String token, String uid, String page) {
		super(context);
		this.token = token;
		this.uid = uid;
		this.page = page;
	}

	public DMListBean loadData() throws WeiboException {
		DMConversationDao dao = new DMConversationDao(token);
		dao.setPage(Integer.valueOf(page));
		dao.setUid(uid);

		DMListBean result = null;
		lock.lock();

		try {
			result = dao.getConversationList();
		} finally {
			lock.unlock();
		}

		return result;
	}

}