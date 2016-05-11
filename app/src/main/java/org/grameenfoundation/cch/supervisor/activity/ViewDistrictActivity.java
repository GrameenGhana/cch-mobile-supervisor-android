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
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.model.District;
import org.grameenfoundation.cch.supervisor.ui.fragment.FacilityListFragment;
import org.grameenfoundation.cch.supervisor.ui.fragment.NurseListFragment;
import org.grameenfoundation.cch.supervisor.ui.fragment.SupervisorListFragment;
import org.grameenfoundation.cch.supervisor.ui.view.PagerSlidingTabStrip;

import java.util.ArrayList;

public class ViewDistrictActivity extends BaseActivity {

    private static final String TAG = ViewDistrictActivity.class.getSimpleName();
    private static final String PAGE_TAG = "District page";

    private District district;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_model);

        Intent i = getIntent();
        district = (District) i.getSerializableExtra("district");

        refresh();
    }

    protected String getPageTag() { return PAGE_TAG; }

    public void refresh() {
        TextView mFirstLine = (TextView) findViewById(R.id.main_first_line);
        TextView mSecondLine = (TextView) findViewById(R.id.main_second_line);
        TextView mThirdLine = (TextView) findViewById(R.id.main_third_line);

        try {
            mFirstLine.setText(district.name);
            mSecondLine.setText(district.region);
            mThirdLine.setText(district.statsText());

            PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.activity_view_tabs);
            ViewPager pager = (ViewPager) findViewById(R.id.activity_view_pager);

            MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter);
            tabs.setViewPager(pager);
            final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            pager.setPageMargin(pageMargin);
            pager.setCurrentItem(0);

            tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
                @Override
                public void onTabReselected(int position) {
                    Toast.makeText(ViewDistrictActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
                }
            });
        } catch(NullPointerException ex) {
            Log.d(TAG, ex.getLocalizedMessage());
        }
	}

    public class MyPagerAdapter extends FragmentPagerAdapter {

		private final ArrayList<String> tabNames = new ArrayList<String>() {
			{
				add("Facilities");
				add("Nurses");
                add("Supervisors");
			}
		};

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
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
				return FacilityListFragment.newInstance(position);
			} else if (position == 1) {
				return NurseListFragment.newInstance(position, "District", String.valueOf(district.districtId));
            } else if (position == 2) {
                return SupervisorListFragment.newInstance(position, "District", String.valueOf(district.districtId));
			} else {
				return FacilityListFragment.newInstance(position);
			}
		}
	}
}