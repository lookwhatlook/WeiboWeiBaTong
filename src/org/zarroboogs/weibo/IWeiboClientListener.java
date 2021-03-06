package org.zarroboogs.weibo;

import android.os.Bundle;

public interface IWeiboClientListener {

	/**
	 * Called when a dialog completes. Executed by the thread that initiated the
	 * dialog.
	 * 
	 * @param values
	 *            Key-value string pairs extracted from the response.
	 */
	public void onComplete(Bundle values);

	/**
	 * Called when a Weibo responds to a dialog with an error. Executed by the
	 * thread that initiated the dialog.
	 */
	public void onWeiboException(WeiboException e);

	/**
	 * Called when a dialog is canceled by the user. Executed by the thread that
	 * initiated the dialog.
	 */
	public void onCancel();

}
