package fr.julienvermet.bugdroid.bugs.viewpager;

import java.text.SimpleDateFormat;

import fr.julienvermet.bugdroid.bugs.Bug;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter implements ViewPagerIndicator.PageInfoProvider{

	static SimpleDateFormat readableDateFormat = new SimpleDateFormat("yyyy - MM/dd");
	public static String[] list = new String[]{"Attachments", "Details", "Comments", "Ccs"};
	Bug bug;

	public PageAdapter(FragmentManager fm, Bug bug) {
		super(fm);
		this.bug=bug;
	}

	@Override
	public String getTitle(int pos){
		return list[pos];
	}

	@Override
	public Fragment getItem(int pos) {

		Fragment f = null;

		if (pos == 0)
			f = AttachmentFragment.newInstance(bug.getAttachments());
		else if (pos == 1)
			f = BugFragment.newInstance(bug);
		else if (pos == 2)
			f = CommentFragment.newInstance(bug);
		else if (pos == 3)
			f = CcsFragment.newInstance(bug.getCcs());

		return f;

	}

	@Override
	public int getCount() {
		return list.length;
	}
}