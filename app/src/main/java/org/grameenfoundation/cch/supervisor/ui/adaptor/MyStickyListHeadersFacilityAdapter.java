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
import org.grameenfoundation.cch.supervisor.activity.ViewFacilityActivity;
import org.grameenfoundation.cch.supervisor.model.Facility;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MyStickyListHeadersFacilityAdapter extends ArrayAdapter<String> implements StickyListHeadersAdapter, OnClickListener {

    public static final String TAG = MyStickyListHeadersFacilityAdapter.class.getSimpleName();

	private final Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Facility> mFacilityList;

	public MyStickyListHeadersFacilityAdapter(final Context ctx, ArrayList<Facility> facilitiesList) {
		mContext = ctx;
		mFacilityList = facilitiesList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Log.v(TAG, "Size of facilities: " + mFacilityList.size());

		for (int i = 0; i < mFacilityList.size(); i++) {
			add("Row number " + i);
		}
	}

    private static class ViewHolder {
        public/* Roboto */TextView firstLine;
        public/* Roboto */TextView secondLine;
        public/* Roboto */TextView thirdLine;
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
        return (mFacilityList.get(position).facilityType.equalsIgnoreCase("HC")) ? 1 : 2;
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
			holder.firstLine = (TextView) convertView.findViewById(R.id.list_item_sticky_header_first_line);
			holder.secondLine = (TextView) convertView.findViewById(R.id.list_item_sticky_header_second_line);
			holder.thirdLine = (TextView) convertView.findViewById(R.id.list_item_sticky_header_third_line);
			holder.iconPlay = (TextView) convertView.findViewById(R.id.list_item_sticky_header_media_icon_play);
			holder.iconPlay.setOnClickListener(this);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        holder.firstLine.setText(mFacilityList.get(position).name);
        holder.secondLine.setText("");
        holder.thirdLine.setText(mFacilityList.get(position).statsText());
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
		holder.header.setText(mFacilityList.get(position).facilityType);

		return view;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();

		switch (v.getId()) {
		    case R.id.list_item_sticky_header_media_icon_play:
				//Toast.makeText(mContext, "Go to Facility: " + (mFacilityList.get(position).name), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContext, ViewFacilityActivity.class);
                i.putExtra("facility", mFacilityList.get(position));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
			    break;
		}
	}
}