package org.zarroboogs.weibo.ui.loader;

import android.content.Context;

import org.zarroboogs.weibo.bean.data.SearchStatusListBean;
import org.zarroboogs.weibo.dao.SearchDao;
import org.zarroboogs.weibo.net.WeiboException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: qii Date: 13-5-12
 */
public class SearchStatusLoader extends AbstractAsyncNetRequestTaskLoader<SearchStatusListBean> {

	private static Lock lock = new ReentrantLock();

	private String token;
	private String searchWord;
	private String page;

	public SearchStatusLoader(Context context, String token, String searchWord, String page) {
		super(context);
		this.token = token;
		this.searchWord = searchWord;
		this.page = page;
	}

	public SearchStatusListBean loadData() throws WeiboException {
		SearchDao dao = new SearchDao(token, searchWord);
		dao.setPage(page);

		SearchStatusListBean result = null;
		lock.lock();

		try {
			result = dao.getStatusList();
		} finally {
			lock.unlock();
		}

		return result;
	}

}
