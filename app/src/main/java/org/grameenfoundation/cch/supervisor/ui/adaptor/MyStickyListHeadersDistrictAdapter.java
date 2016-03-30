package org.grameenfoundation.cch.supervisor.ui.adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.activity.ViewDistrictActivity;
import org.grameenfoundation.cch.supervisor.model.District;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MyStickyListHeadersDistrictAdapter extends ArrayAdapter<String> implements StickyListHeadersAdapter, OnClickListener {

    public static final String TAG = MyStickyListHeadersDistrictAdapter.class.getSimpleName();

	private final Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<District> mDistrictsList;

	public MyStickyListHeadersDistrictAdapter(final Context ctx, ArrayList<District> districtsList) {
		mContext = ctx;
		mDistrictsList = districtsList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Log.v(TAG, "Size of districts: " + mDistrictsList.size());

		for (int i = 0; i < mDistrictsList.size(); i++) {
			add("Row number " + i);
		}
	}

    private static class ViewHolder {
        public/* Roboto */TextView districtName;
        public/* Roboto */TextView facilityStats;
        public/* Roboto */TextView staffStats;
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
        return mDistrictsList.get(position).regionId;
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
			holder.districtName = (TextView) convertView.findViewById(R.id.list_item_sticky_header_first_line);
			holder.facilityStats = (TextView) convertView.findViewById(R.id.list_item_sticky_header_second_line);
			holder.staffStats = (TextView) convertView.findViewById(R.id.list_item_sticky_header_third_line);
			holder.iconPlay = (TextView) convertView.findViewById(R.id.list_item_sticky_header_media_icon_play);
			holder.iconPlay.setOnClickListener(this);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        holder.districtName.setText(mDistrictsList.get(position).name);
        holder.facilityStats.setText(mDistrictsList.get(position).facilityStatsText());
        holder.staffStats.setText(mDistrictsList.get(position).staffStatsText());
		holder.iconPlay.setTag(position);

		return convertView;
	}

	@Override
	public View getHeaderView(final int position, final View convertView, final ViewGroup parent) {
		View view = (View) convertView;

		final HeaderViewHolder holder;

		if (view == null) {
			view = (View) LayoutInflater.from(mContext).inflate(R.layout.list_header, parent, false);
			holder = new HeaderViewHolder();
			holder.header = (TextView) view.findViewById(R.id.list_header_media_header);
			holder.icon = (TextView) view.findViewById(R.id.list_header_media_icon);
			//holder.icon.setOnClickListener(this);
			view.setTag(holder);
		} else {
			holder = (HeaderViewHolder) view.getTag();
		}

		holder.icon.setTag(position);
		holder.header.setText(mDistrictsList.get(position).region);

		return view;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();

		switch (v.getId()) {
		    case R.id.list_item_sticky_header_media_icon_play:
                Intent i = new Intent(mContext, ViewDistrictActivity.class);
                i.putExtra("district", mDistrictsList.get(position));
                mContext.startActivity(i);
			    break;
		}
	}
}