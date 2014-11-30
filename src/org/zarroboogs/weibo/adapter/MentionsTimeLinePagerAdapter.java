package org.zarroboogs.weibo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import org.zarroboogs.weibo.fragment.MentionsCommentTimeLineFragment;
import org.zarroboogs.weibo.fragment.MentionsTimeLineFragment;
import org.zarroboogs.weibo.fragment.MentionsWeiboTimeLineFragment;
import org.zarroboogs.weibo.support.lib.AppFragmentPagerAdapter;

/**
 * User: qii Date: 13-3-8
 */
public class MentionsTimeLinePagerAdapter extends AppFragmentPagerAdapter {

	private SparseArray<Fragment> fragmentList;

	public MentionsTimeLinePagerAdapter(MentionsTimeLineFragment fragment, ViewPager viewPager, FragmentManager fm, SparseArray<Fragment> fragmentList) {
		super(fm);
		this.fragmentList = fragmentList;
		fragmentList.append(MentionsTimeLineFragment.MENTIONS_WEIBO_CHILD_POSITION, fragment.getMentionsWeiboTimeLineFragment());
		fragmentList.append(MentionsTimeLineFragment.MENTIONS_COMMENT_CHILD_POSITION, fragment.getMentionsCommentTimeLineFragment());
		FragmentTransaction transaction = fragment.getChildFragmentManager().beginTransaction();
		if (!fragmentList.get(MentionsTimeLineFragment.MENTIONS_WEIBO_CHILD_POSITION).isAdded())
			transaction.add(viewPager.getId(), fragmentList.get(MentionsTimeLineFragment.MENTIONS_WEIBO_CHILD_POSITION), MentionsWeiboTimeLineFragment.class.getName());
		if (!fragmentList.get(MentionsTimeLineFragment.MENTIONS_COMMENT_CHILD_POSITION).isAdded())
			transaction.add(viewPager.getId(), fragmentList.get(MentionsTimeLineFragment.MENTIONS_COMMENT_CHILD_POSITION),
					MentionsCommentTimeLineFragment.class.getName());
		if (!transaction.isEmpty()) {
			transaction.commit();
			fragment.getChildFragmentManager().executePendingTransactions();
		}
	}

	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	protected String getTag(int position) {
		SparseArray<String> tagList = new SparseArray<String>();
		tagList.append(MentionsTimeLineFragment.MENTIONS_WEIBO_CHILD_POSITION, MentionsWeiboTimeLineFragment.class.getName());
		tagList.append(MentionsTimeLineFragment.MENTIONS_COMMENT_CHILD_POSITION, MentionsCommentTimeLineFragment.class.getName());

		return tagList.get(position);
	}

	@Override
	public int getCount() {
		return 2;
	}

}