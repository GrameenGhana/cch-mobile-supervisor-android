/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grameenfoundation.cch.supervisor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.model.Nurse;
import org.grameenfoundation.cch.supervisor.ui.fragment.CourseListFragment;
import org.grameenfoundation.cch.supervisor.ui.fragment.EventListFragment;
import org.grameenfoundation.cch.supervisor.ui.view.PagerSlidingTabStrip;

import java.util.ArrayList;

public class ViewNurseActivity extends BaseActivity {

    private static final String TAG = ViewNurseActivity.class.getSimpleName();
    private static final String PAGE_TAG = "Nurse page";

	private MyPagerAdapter adapter;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;

    private TextView mFirstLine;
    private TextView mSecondLine;
    private TextView mThirdLine;

    private Nurse nurse;

	protected String getPageTag() { return PAGE_TAG; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_model);

        Intent i = getIntent();
        nurse = (Nurse) i.getSerializableExtra("nurse");
        refresh();
    }

    public void refresh() {
        mFirstLine = (TextView) findViewById(R.id.main_first_line);
        mSecondLine = (TextView) findViewById(R.id.main_second_line);
        mThirdLine = (TextView) findViewById(R.id.main_third_line);

        mFirstLine.setText(nurse.name);
        mSecondLine.setText(nurse.title);
        mThirdLine.setText(nurse.statsText());

		tabs = (PagerSlidingTabStrip) findViewById(R.id.activity_view_tabs);
		pager = (ViewPager) findViewById(R.id.activity_view_pager);

		adapter = new MyPagerAdapter(this, getSupportFragmentManager());
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setCurrentItem(0);

		tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
			@Override
			public void onTabReselected(int position) {
				Toast.makeText(ViewNurseActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
			}
		});
	}

    public class MyPagerAdapter extends FragmentPagerAdapter {

		private final ArrayList<String> tabNames = new ArrayList<String>() {
			{
				add("Courses");
				add("Events");
			}
		};

        private BaseActivity mActivity;

		public MyPagerAdapter(BaseActivity ba, FragmentManager fm) {
			super(fm);
            mActivity = ba;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabNames.get(position);
		}

		@Override
		public int getCount() {
			return tabNames.size();
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return CourseListFragment.newInstance(mActivity, position, nurse.nurseId);
			} else if (position == 1) {
				return EventListFragment.newInstance(position, "Nurse", nurse.nurseId);
			} else {
				return CourseListFragment.newInstance(mActivity, position, nurse.nurseId);
			}
		}
	}
}