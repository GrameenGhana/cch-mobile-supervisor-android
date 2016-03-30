package org.grameenfoundation.cch.supervisor.ui.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.ArrayAdapter;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.model.Event;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MyStickyListHeadersEventAdapter extends ArrayAdapter<String> implements StickyListHeadersAdapter, OnClickListener {

    public static final String TAG = MyStickyListHeadersEventAdapter.class.getSimpleName();

	private final Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Event> mEventsList;

	public MyStickyListHeadersEventAdapter(final Context ctx, ArrayList<Event> eventsList) {
		mContext = ctx;
		mEventsList = eventsList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Log.v(TAG, "Size of events: " + mEventsList.size());

		for (int i = 0; i < mEventsList.size(); i++) {
			add("Row number " + i);
		}
	}

    private static class ViewHolder {
        public/* Roboto */TextView title;
        public/* Roboto */TextView schedule;
        public/* Roboto */TextView justification;
        public/* Roboto */TextView comment;
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
		return mEventsList.get(position).getCategoryId();
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_event, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.list_item_event_title);
			holder.justification = (TextView) convertView.findViewById(R.id.list_item_event_justification);
            holder.comment   = (TextView) convertView.findViewById(R.id.list_item_event_comments);
			holder.schedule = (TextView) convertView.findViewById(R.id.list_item_event_nurse);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        holder.title.setText(mEventsList.get(position).title);
        holder.justification.setText(mEventsList.get(position).justification);
        holder.comment.setText(mEventsList.get(position).comments);
        holder.schedule.setText(mEventsList.get(position).scheduleText(true));

		return convertView;
	}

	@Override
	public View getHeaderView(final int position, final View convertView, final ViewGroup parent) {
		View view = (View) convertView;

		final HeaderViewHolder holder;

		if (view == null) {
			view = (View) LayoutInflater.from(mContext).inflate(R.layout.list_header_dark, parent, false);
			holder = new HeaderViewHolder();
			holder.header = (TextView) view.findViewById(R.id.list_header_left_text);
			holder.icon = (TextView) view.findViewById(R.id.list_header_right_text);
			//holder.icon.setOnClickListener(this);
			view.setTag(holder);
		} else {
			holder = (HeaderViewHolder) view.getTag();
		}
		holder.icon.setTag(position);

		holder.icon.setText(mContext.getResources().getString(R.string.material_icon_clock));
		holder.header.setText(mEventsList.get(position).getCategory());

		return view;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();

		switch (v.getId()) {
		    case R.id.list_item_sticky_header_media_icon_play:
				Toast.makeText(mContext, "Go to Event: " + (mEventsList.get(position).title), Toast.LENGTH_SHORT).show();
                //Intent i = new Intent(mContext, ViewEventActivity.class);
                //i.putExtra("event", mEventsList.get(position));
                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //mContext.startActivity(i);
			    break;
		}
	}
}