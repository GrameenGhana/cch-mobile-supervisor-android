package org.grameenfoundation.cch.supervisor.ui.adaptor;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.activity.BaseActivity;
import org.grameenfoundation.cch.supervisor.model.Course;
import org.grameenfoundation.cch.supervisor.repository.ModelRepository;

import java.util.ArrayList;
import java.util.HashMap;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MyStickyListHeadersCourseAdapter extends ArrayAdapter<String> implements StickyListHeadersAdapter, OnClickListener {

    public static final String TAG = MyStickyListHeadersCourseAdapter.class.getSimpleName();

	private final Context mContext;
    private final BaseActivity mActivity;
	private LayoutInflater mInflater;
	private ArrayList<Course> mCoursesList;

    private HashMap<String,String> catCount = new HashMap(3);
	private int cntInprogress = 0;
    private int cntEligible = 0;
    private int cntPassed = 0;

	public MyStickyListHeadersCourseAdapter(final BaseActivity act, ArrayList<Course> coursesList) {
        mActivity = act;
		mContext = act.getApplicationContext();
		mCoursesList = coursesList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Log.v(TAG, "Size of courses: " + mCoursesList.size());

		for (int i = 0; i < mCoursesList.size(); i++) {
            if (mCoursesList.get(i).isInprogress()) { cntInprogress++; }
            if (mCoursesList.get(i).isEligible()) { cntEligible++; }
            if (mCoursesList.get(i).isPassed()) { cntPassed++; }
			add("Row number " + i);
		}
        catCount.put("In Progress", String.valueOf(cntInprogress));
        catCount.put("Eligible", String.valueOf(cntEligible));
        catCount.put("Passed", String.valueOf(cntPassed));
	}

    private static class ViewHolder {
        public/* Roboto */TextView name;
        public/* Roboto */TextView ksa;
        public/* Roboto */TextView stats;
        public/* Material */TextView iconPlay;
    }

    private static class HeaderViewHolder {
        public/* Roboto */TextView header;
        public/* Material */TextView icon;
    }


	@Override
	public long getItemId(final int position) {
		return getItem(position).hashCode();
	}

	@Override
	public long getHeaderId(final int position) {
		return mCoursesList.get(position).getCategoryId();
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.list_item_sticky_header_first_line);
			holder.ksa = (TextView) convertView.findViewById(R.id.list_item_sticky_header_second_line);
			holder.stats = (TextView) convertView.findViewById(R.id.list_item_sticky_header_third_line);
			holder.iconPlay = (TextView) convertView.findViewById(R.id.list_item_sticky_header_media_icon_play);
            holder.stats.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        holder.name.setText(mCoursesList.get(position).statsText());
        holder.ksa.setText(mCoursesList.get(position).title);
        holder.stats.setText(mCoursesList.get(position).lastAccessed());

        if (mCoursesList.get(position).isInprogress()) {
            holder.iconPlay.setVisibility(View.INVISIBLE);
        } else {
            holder.iconPlay.setVisibility(View.VISIBLE);
            holder.iconPlay.setOnClickListener(this);
            holder.iconPlay.setTag(position);
        }

		return convertView;
	}

	@Override
	public View getHeaderView(final int position, final View convertView, final ViewGroup parent) {
		View view = (View) convertView;

		final HeaderViewHolder holder;

		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.list_header, parent, false);
			holder = new HeaderViewHolder();
			holder.header = (TextView) view.findViewById(R.id.list_header_media_header);
			holder.icon = (TextView) view.findViewById(R.id.list_header_media_icon);
			//holder.icon.setOnClickListener(this);
			view.setTag(holder);
		} else {
			holder = (HeaderViewHolder) view.getTag();
		}

		holder.icon.setTag(position);
		holder.header.setText(mCoursesList.get(position).getCategory() + " ("+catCount.get(mCoursesList.get(position).getCategory()) + ")");

		return view;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();

		switch (v.getId()) {
		    case R.id.list_item_sticky_header_media_icon_play:
                Course course = mCoursesList.get(position);
                if (course.isEligible()) {
                    course.setKSAPassed();
                } else {
                    course.setKSAEligible();
                }
                ModelRepository.updateCourseStatus(course);
                mActivity.refresh();
			    break;
		}
	}
}