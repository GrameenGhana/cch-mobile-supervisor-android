package org.grameenfoundation.cch.supervisor.ui.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.model.Event;
import org.grameenfoundation.cch.supervisor.ui.view.AnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AnimatedExpandableListEventAdapter extends AnimatedExpandableListAdapter implements View.OnClickListener {

    public static final String TAG = AnimatedExpandableListEventAdapter.class.getSimpleName();

    private final Context mContext;
	private List<GroupItem> mItems;
    private LayoutInflater mInflater;

    public AnimatedExpandableListEventAdapter(final Context ctx, ArrayList<Event> mEventsList) {
        mContext = ctx;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = new ArrayList<>();

        for (String header : getHeaders()) {
             GroupItem item = new GroupItem();
             for (int i = 0; i < mEventsList.size(); i++) {
                  if (mEventsList.get(i).getCategory().equals(header)) {
                      ChildItem child = new ChildItem();
                      child.title = mEventsList.get(i).title;
                      child.schedule = mEventsList.get(i).scheduleText(true);
                      child.comment = mEventsList.get(i).comments;
                      child.justification = mEventsList.get(i).justification;
                      item.items.add(child);
                  }
             }
             item.title = header + " (" + item.items.size() + ")";
             mItems.add(item);
        }
    }

	public void setData(List<GroupItem> items) {
		this.mItems = items;
	}

    private ArrayList<String> getHeaders() {
        ArrayList<String> x = new ArrayList<>();
        x.add("Past Last Month");
        x.add("Past This Month");
        x.add("Yesterday");
        x.add("Today");
        x.add("Tomorrow");
        x.add("Future");
        x.add("In the Past");
        x.add("Unknown Period");
        return x;
    }

    @Override
	public ChildItem getChild(int groupPosition, int childPosition) {
		return mItems.get(groupPosition).items.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		ChildHolder holder;
		ChildItem item = getChild(groupPosition, childPosition);

		if (convertView == null) {
			holder = new ChildHolder();
            convertView = mInflater.inflate(R.layout.list_item_event, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.list_item_event_title);
            holder.justification = (TextView) convertView.findViewById(R.id.list_item_event_justification);
            holder.comment   = (TextView) convertView.findViewById(R.id.list_item_event_comments);
            holder.schedule = (TextView) convertView.findViewById(R.id.list_item_event_nurse);
            convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}

		holder.title.setText(item.title);
        holder.justification.setText(item.justification);
        holder.comment.setText(item.comment);
        holder.schedule.setText(item.schedule);

		return convertView;
	}

	@Override
	public int getRealChildrenCount(int groupPosition) {
		return mItems.get(groupPosition).items.size();
	}

	@Override
	public GroupItem getGroup(int groupPosition) {
		return mItems.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mItems.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupHolder holder;
		GroupItem item = getGroup(groupPosition);

		if (convertView == null) {
			holder = new GroupHolder();
            convertView =  LayoutInflater.from(mContext).inflate(R.layout.list_header_dark, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.list_header_left_text);
            holder.icon = (TextView) convertView.findViewById(R.id.list_header_right_text);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}
        holder.icon.setTag(groupPosition);

		holder.title.setText(item.title);
        holder.icon.setText(mContext.getResources().getString(R.string.material_icon_clock));

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

    private static class GroupItem {
        String title;
        List<ChildItem> items = new ArrayList<>();
    }

    private static class GroupHolder {
        public/* Roboto */TextView title;
        public/* Material */TextView icon;
    }

    private static class ChildItem {
        String title;
        String schedule;
        String justification;
        String comment;
    }

    private static class ChildHolder {
        public/* Roboto */TextView title;
        public/* Roboto */TextView schedule;
        public/* Roboto */TextView justification;
        public/* Roboto */TextView comment;
        public/* Material */TextView iconPlay;
    }

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();

		switch (v.getId()) {
		    case R.id.list_item_sticky_header_media_icon_play:
				Toast.makeText(mContext, "Go to Event: ", Toast.LENGTH_SHORT).show();
                //Intent i = new Intent(mContext, ViewEventActivity.class);
                //i.putExtra("event", mEventsList.get(position));
                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //mContext.startActivity(i);
			    break;
		}
	}
}