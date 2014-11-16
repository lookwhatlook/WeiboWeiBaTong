package org.zarroboogs.weibo.ui.maintimeline;

import org.zarroboogs.weibo.Constances;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.MentionTimeLineData;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.bean.MessageReCmtCountBean;
import org.zarroboogs.weibo.bean.TimeLinePosition;
import org.zarroboogs.weibo.bean.UnreadBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.ClearUnreadDao;
import org.zarroboogs.weibo.dao.TimeLineReCmtCountDao;
import org.zarroboogs.weibo.database.MentionWeiboTimeLineDBTask;
import org.zarroboogs.weibo.net.WeiboException;
import org.zarroboogs.weibo.othercomponent.unreadnotification.NotificationServiceHelper;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.ui.adapter.StatusListAdapter;
import org.zarroboogs.weibo.ui.basefragment.AbstractMessageTimeLineFragment;
import org.zarroboogs.weibo.ui.browser.BrowserWeiboMsgActivity;
import org.zarroboogs.weibo.ui.loader.MentionsWeiboMsgLoader;
import org.zarroboogs.weibo.ui.loader.MentionsWeiboTimeDBLoader;
import org.zarroboogs.weibo.ui.main.MainTimeLineActivity;
import org.zarroboogs.weibo.ui.main.MentionsTimeLine;
import org.zarroboogs.weibo.widget.TopTipsView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * User: qii Date: 12-7-29
 */
@SuppressLint("ValidFragment")
public class MentionsWeiboTimeLineFragment extends AbstractMessageTimeLineFragment<MessageListBean> {

	private AccountBean accountBean;

	private UserBean userBean;

	private String token;

	private UnreadBean unreadBean;

	private TimeLinePosition timeLinePosition;

	private MessageListBean bean = new MessageListBean();

	private final int POSITION_IN_PARENT_FRAGMENT = 0;

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public MessageListBean getList() {
		return bean;
	}

	public MentionsWeiboTimeLineFragment() {

	}

	public MentionsWeiboTimeLineFragment(AccountBean accountBean, UserBean userBean, String token) {
		this.accountBean = accountBean;
		this.userBean = userBean;
		this.token = token;
	}

	@Override
	public void onResume() {
		super.onResume();
		setListViewPositionFromPositionsCache();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(newBroadcastReceiver, new IntentFilter(AppEventAction.NEW_MSG_BROADCAST));
		setActionBarTabCount(newMsgTipBar.getValues().size());
		getNewMsgTipBar().setOnChangeListener(new TopTipsView.OnChangeListener() {
			@Override
			public void onChange(int count) {
				((MainTimeLineActivity) getActivity()).setMentionsWeiboCount(count);
				setActionBarTabCount(count);
			}
		});
		checkUnreadInfo();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!getActivity().isChangingConfigurations()) {
			saveTimeLinePositionToDB();
		}
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(newBroadcastReceiver);

	}

	private void saveTimeLinePositionToDB() {
		timeLinePosition = Utility.getCurrentPositionFromListView(getListView());
		timeLinePosition.newMsgIds = newMsgTipBar.getValues();
		MentionWeiboTimeLineDBTask.asyncUpdatePosition(timeLinePosition, accountBean.getUid());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setRetainInstance(false);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		newMsgTipBar.setType(TopTipsView.Type.ALWAYS);

	}

	@Override
	protected void onListViewScrollStop() {
		super.onListViewScrollStop();
		timeLinePosition = Utility.getCurrentPositionFromListView(getListView());
	}

	@Override
	protected void buildListAdapter() {
		StatusListAdapter adapter = new StatusListAdapter(this, getList().getItemList(), getListView(), true, false);
		adapter.setTopTipBar(newMsgTipBar);
		timeLineAdapter = adapter;
		getListView().setAdapter(timeLineAdapter);
	}

	private void checkUnreadInfo() {
		Loader loader = getLoaderManager().getLoader(DB_CACHE_LOADER_ID);
		if (loader != null) {
			return;
		}
		Intent intent = getActivity().getIntent();
		AccountBean intentAccount = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
		MessageListBean mentionsWeibo = intent.getParcelableExtra(BundleArgsConstants.MENTIONS_WEIBO_EXTRA);
		UnreadBean unreadBeanFromNotification = intent.getParcelableExtra(BundleArgsConstants.UNREAD_EXTRA);

		if (accountBean.equals(intentAccount) && mentionsWeibo != null) {
			addUnreadMessage(mentionsWeibo);
			clearUnreadMentions(unreadBeanFromNotification);
			MessageListBean nullObject = null;
			intent.putExtra(BundleArgsConstants.MENTIONS_WEIBO_EXTRA, nullObject);
			getActivity().setIntent(intent);
		}
	}

	private void setActionBarTabCount(int count) {
		MentionsTimeLine parent = (MentionsTimeLine) getParentFragment();
		ActionBar.Tab tab = parent.getWeiboTab();
		if (tab == null) {
			return;
		}
		String tabTag = (String) tab.getTag();
		if (MentionsWeiboTimeLineFragment.class.getName().equals(tabTag)) {
			View customView = tab.getCustomView();
			TextView countTV = (TextView) customView.findViewById(R.id.tv_home_count);
			countTV.setText(String.valueOf(count));
			if (count > 0) {
				countTV.setVisibility(View.VISIBLE);
			} else {
				countTV.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void newMsgLoaderSuccessCallback(MessageListBean newValue, Bundle loaderArgs) {
		if (getActivity() != null && newValue.getSize() > 0) {
			addNewDataAndRememberPosition(newValue);
		}
		unreadBean = null;
		NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NotificationServiceHelper.getMentionsWeiboNotificationId(GlobalContext.getInstance().getAccountBean()));

	}

	private void addNewDataAndRememberPosition(final MessageListBean newValue) {
		int initSize = getList().getSize();
		if (getActivity() != null && newValue.getSize() > 0) {
			final boolean jumpToTop = getList().getSize() == 0;

			getList().addNewData(newValue);
			if (!jumpToTop) {
				int index = getListView().getFirstVisiblePosition();
				getAdapter().notifyDataSetChanged();
				int finalSize = getList().getSize();
				final int positionAfterRefresh = index + finalSize - initSize + getListView().getHeaderViewsCount();
				// use 1 px to show newMsgTipBar
				Utility.setListViewSelectionFromTop(getListView(), positionAfterRefresh, 1, new Runnable() {

					@Override
					public void run() {
						newMsgTipBar.setValue(newValue, jumpToTop);
					}
				});

			} else {
				newMsgTipBar.setValue(newValue, jumpToTop);
				newMsgTipBar.clearAndReset();
				getAdapter().notifyDataSetChanged();
				getListView().setSelection(0);
			}
			MentionWeiboTimeLineDBTask.asyncReplace(getList(), accountBean.getUid());
			saveTimeLinePositionToDB();
		}
	}

	protected void middleMsgLoaderSuccessCallback(int position, MessageListBean newValue, boolean towardsBottom) {

		if (newValue != null) {
			int size = newValue.getSize();

			if (getActivity() != null && newValue.getSize() > 0) {
				getList().addMiddleData(position, newValue, towardsBottom);

				if (towardsBottom) {
					getAdapter().notifyDataSetChanged();
				} else {

					View v = Utility.getListViewItemViewFromPosition(getListView(), position + 1 + 1);
					int top = (v == null) ? 0 : v.getTop();
					getAdapter().notifyDataSetChanged();
					int ss = position + 1 + size - 1;
					getListView().setSelectionFromTop(ss, top);
				}
			}
		}
	}

	@Override
	protected void oldMsgLoaderSuccessCallback(MessageListBean newValue) {
		if (newValue != null && newValue.getSize() > 1) {
			getList().addOldData(newValue);
			MentionWeiboTimeLineDBTask.asyncReplace(getList(), accountBean.getUid());
		} else {
			Toast.makeText(getActivity(), getString(R.string.older_message_empty), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(Constances.ACCOUNT, accountBean);
		outState.putParcelable(Constances.USERBEAN, userBean);
		outState.putString(Constances.TOKEN, token);

		if (getActivity().isChangingConfigurations()) {
			outState.putParcelable(Constances.BEAN, bean);
			outState.putParcelable("unreadBean", unreadBean);
			outState.putSerializable("timeLinePosition", timeLinePosition);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		switch (getCurrentState(savedInstanceState)) {
		case FIRST_TIME_START:
			getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
			break;
		case ACTIVITY_DESTROY_AND_CREATE:
			userBean = (UserBean) savedInstanceState.getParcelable(Constances.USERBEAN);
			accountBean = (AccountBean) savedInstanceState.getParcelable(Constances.ACCOUNT);
			token = savedInstanceState.getString(Constances.TOKEN);
			unreadBean = (UnreadBean) savedInstanceState.getParcelable("unreadBean");
			timeLinePosition = (TimeLinePosition) savedInstanceState.getSerializable("timeLinePosition");

			Loader<MentionTimeLineData> loader = getLoaderManager().getLoader(DB_CACHE_LOADER_ID);
			if (loader != null) {
				getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
			}

			MessageListBean savedBean = (MessageListBean) savedInstanceState.getParcelable(Constances.BEAN);
			if (savedBean != null && savedBean.getSize() > 0) {
				getList().replaceData(savedBean);
				timeLineAdapter.notifyDataSetChanged();
				refreshLayout(getList());
			} else {
				getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
			}

			break;
		}
	}

	@Override
	protected void listViewItemClick(AdapterView parent, View view, int position, long id) {
		startActivityForResult(BrowserWeiboMsgActivity.newIntent(GlobalContext.getInstance().getAccountBean(), bean.getItemList().get(position), GlobalContext
				.getInstance().getSpecialToken()), MainTimeLineActivity.REQUEST_CODE_UPDATE_MENTIONS_WEIBO_TIMELINE_COMMENT_REPOST_COUNT);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// use Up instead of Back to reach this fragment
		if (data == null) {
			return;
		}
		final MessageBean msg = (MessageBean) data.getParcelableExtra("msg");
		if (msg != null) {
			for (int i = 0; i < getList().getSize(); i++) {
				if (msg.equals(getList().getItem(i))) {
					MessageBean ori = getList().getItem(i);
					if (ori.getComments_count() != msg.getComments_count() || ori.getReposts_count() != msg.getReposts_count()) {
						ori.setReposts_count(msg.getReposts_count());
						ori.setComments_count(msg.getComments_count());
						MentionWeiboTimeLineDBTask.asyncReplace(getList(), accountBean.getUid());
						getAdapter().notifyDataSetChanged();
					}
					break;
				}
			}
		}
	}

	private void setListViewPositionFromPositionsCache() {
		Utility.setListViewSelectionFromTop(getListView(), timeLinePosition != null ? timeLinePosition.position : 0,
				timeLinePosition != null ? timeLinePosition.top : 0, new Runnable() {
					@Override
					public void run() {
						setListViewUnreadTipBar(timeLinePosition);

					}
				});

	}

	private void setListViewUnreadTipBar(TimeLinePosition p) {
		if (p != null && p.newMsgIds != null) {
			newMsgTipBar.setValue(p.newMsgIds);
			setActionBarTabCount(newMsgTipBar.getValues().size());
			((MainTimeLineActivity) getActivity()).setMentionsWeiboCount(newMsgTipBar.getValues().size());
		}
	}

	private LoaderManager.LoaderCallbacks<MentionTimeLineData> dbCallback = new LoaderManager.LoaderCallbacks<MentionTimeLineData>() {
		@Override
		public Loader<MentionTimeLineData> onCreateLoader(int id, Bundle args) {
			getPullToRefreshListView().setVisibility(View.INVISIBLE);
			return new MentionsWeiboTimeDBLoader(getActivity(), GlobalContext.getInstance().getCurrentAccountId());
		}

		@Override
		public void onLoadFinished(Loader<MentionTimeLineData> loader, MentionTimeLineData result) {
			getPullToRefreshListView().setVisibility(View.VISIBLE);

			if (result != null) {
				getList().replaceData(result.msgList);
				timeLinePosition = result.position;
			}

			getAdapter().notifyDataSetChanged();
			setListViewPositionFromPositionsCache();
			refreshLayout(bean);

			/**
			 * when this account first open app,if he don't have any data in
			 * database,fetch data from server automally
			 */

			if (bean.getSize() == 0) {
				mPullToRefreshListView.setRefreshing();
				loadNewMsg();
			} else {
				new RefreshReCmtCountTask().executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
			}

			getLoaderManager().destroyLoader(loader.getId());

			checkUnreadInfo();

		}

		@Override
		public void onLoaderReset(Loader<MentionTimeLineData> loader) {

		}
	};

	protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateNewMsgLoader(int id, Bundle args) {
		String accountId = accountBean.getUid();
		String token = accountBean.getAccess_token();
		String sinceId = null;
		if (getList().getItemList().size() > 0) {
			sinceId = getList().getItemList().get(0).getId();
		}
		return new MentionsWeiboMsgLoader(getActivity(), accountId, token, sinceId, null);
	}

	protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateMiddleMsgLoader(int id, Bundle args, String middleBeginId, String middleEndId,
			String middleEndTag, int middlePosition) {
		String accountId = accountBean.getUid();
		String token = accountBean.getAccess_token();
		return new MentionsWeiboMsgLoader(getActivity(), accountId, token, middleBeginId, middleEndId);
	}

	protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateOldMsgLoader(int id, Bundle args) {
		String accountId = accountBean.getUid();
		String token = accountBean.getAccess_token();
		String maxId = null;
		if (getList().getItemList().size() > 0) {
			maxId = getList().getItemList().get(getList().getItemList().size() - 1).getId();
		}
		return new MentionsWeiboMsgLoader(getActivity(), accountId, token, null, maxId);
	}

	private BroadcastReceiver newBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			AccountBean intentAccount = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
			final UnreadBean unreadBean = intent.getParcelableExtra(BundleArgsConstants.UNREAD_EXTRA);
			if (intentAccount == null || !accountBean.equals(intentAccount)) {
				return;
			}
			MessageListBean data = intent.getParcelableExtra(BundleArgsConstants.MENTIONS_WEIBO_EXTRA);
			addUnreadMessage(data);
			clearUnreadMentions(unreadBean);
		}
	};

	private void addUnreadMessage(MessageListBean data) {
		if (data != null && data.getSize() > 0) {
			MessageBean last = data.getItem(data.getSize() - 1);
			boolean dup = getList().getItemList().contains(last);
			if (!dup) {
				addNewDataAndRememberPosition(data);
			}
		}
	}

	private class RefreshReCmtCountTask extends MyAsyncTask<Void, List<MessageReCmtCountBean>, List<MessageReCmtCountBean>> {

		List<String> msgIds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			msgIds = new ArrayList<String>();
			List<MessageBean> msgList = getList().getItemList();
			for (MessageBean msg : msgList) {
				if (msg != null) {
					msgIds.add(msg.getId());
				}
			}
		}

		@Override
		protected List<MessageReCmtCountBean> doInBackground(Void... params) {
			try {
				return new TimeLineReCmtCountDao(GlobalContext.getInstance().getSpecialToken(), msgIds).get();
			} catch (WeiboException e) {
				cancel(true);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<MessageReCmtCountBean> value) {
			super.onPostExecute(value);
			if (getActivity() == null || value == null) {
				return;
			}

			for (int i = 0; i < value.size(); i++) {
				MessageBean msg = getList().getItem(i);
				MessageReCmtCountBean count = value.get(i);
				if (msg != null && msg.getId().equals(count.getId())) {
					msg.setReposts_count(count.getReposts());
					msg.setComments_count(count.getComments());
				}
			}
			MentionWeiboTimeLineDBTask.asyncReplace(getList(), accountBean.getUid());
			getAdapter().notifyDataSetChanged();

		}

	}

	private void clearUnreadMentions(final UnreadBean data) {
		new MyAsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					new ClearUnreadDao(GlobalContext.getInstance().getAccountBean().getAccess_token()).clearMentionStatusUnread(data, GlobalContext
							.getInstance().getAccountBean().getUid());
				} catch (WeiboException ignored) {

				}
				return null;
			}
		}.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
	}
}
