package org.grameenfoundation.cch.supervisor.ui.adaptor;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.activity.ViewNurseActivity;
import org.grameenfoundation.cch.supervisor.model.Nurse;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MyStickyListHeadersNurseAdapter extends ArrayAdapter<String> implements StickyListHeadersAdapter, OnClickListener {

    public static final String TAG = MyStickyListHeadersNurseAdapter.class.getSimpleName();

	private final Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Nurse> mNursesList;

	public MyStickyListHeadersNurseAdapter(final Context ctx, ArrayList<Nurse> nursesList) {
		mContext = ctx;
		mNursesList = nursesList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Log.v(TAG, "Size of nurses: " + mNursesList.size());

		for (int i = 0; i < mNursesList.size(); i++) {
			add("Row number " + i);
		}
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
		return mNursesList.get(position).facilityId;
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

        holder.name.setText(mNursesList.get(position).name);

        if (mNursesList.get(position).isSupervisor()) {
            holder.ksa.setText(mNursesList.get(position).title);
            holder.stats.setText("");
            holder.iconPlay.setText("");
        } else {
            holder.ksa.setText(mNursesList.get(position).title);
            holder.stats.setText(mNursesList.get(position).KSAText() + "    |    " + mNursesList.get(position).statsText());
            holder.iconPlay.setOnClickListener(this);
        }

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
		holder.header.setText(mNursesList.get(position).primaryFacility);

		return view;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();

		switch (v.getId()) {
		    case R.id.list_item_sticky_header_media_icon_play:
				//Toast.makeText(mContext, "Go to Nurse: " + (mNursesList.get(position).name), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContext, ViewNurseActivity.class);
                i.putExtra("nurse", mNursesList.get(position));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
			    break;
		}
	}
}