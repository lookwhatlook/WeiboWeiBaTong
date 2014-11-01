package org.zarroboogs.weibo.ui.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.zarroboogs.weibo.bean.CommentTimeLineData;
import org.zarroboogs.weibo.database.CommentToMeTimeLineDBTask;

/**
 * User: qii Date: 13-4-10
 */
public class CommentsToMeDBLoader extends AsyncTaskLoader<CommentTimeLineData> {

	private String accountId;
	private CommentTimeLineData result;

	public CommentsToMeDBLoader(Context context, String accountId) {
		super(context);
		this.accountId = accountId;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if (result == null) {
			forceLoad();
		} else {
			deliverResult(result);
		}
	}

	public CommentTimeLineData loadInBackground() {
		result = CommentToMeTimeLineDBTask.getCommentLineMsgList(accountId);
		return result;
	}

}