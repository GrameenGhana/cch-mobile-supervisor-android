package org.grameenfoundation.cch.supervisor.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListView;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.model.Event;
import org.grameenfoundation.cch.supervisor.repository.ModelRepository;
import org.grameenfoundation.cch.supervisor.ui.adaptor.AnimatedExpandableListEventAdapter;
import org.grameenfoundation.cch.supervisor.ui.view.AnimatedExpandableListView;

import java.util.ArrayList;

public class EventListFragment extends Fragment implements View.OnClickListener {

	private static final String ARG_POSITION = "position";
    private static final String ARG_TYPE = "type";
    private static final String ARG_TYPE_ID = "id";

    private CheckBox mPending;
    private CheckBox mInComplete;
    private CheckBox mComplete;
    private AnimatedExpandableListView listView;

	private int position;
    private static String eventType;
    private static String eventTypeId;

	public static EventListFragment newInstance(int position, String type, String id) {
		EventListFragment f = new EventListFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
        b.putString(ARG_TYPE_ID, id);
        b.putString(ARG_TYPE, type);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
        eventType = getArguments().getString(ARG_TYPE);
        eventTypeId = getArguments().getString(ARG_TYPE_ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_event, container, false);
        listView = (AnimatedExpandableListView) rootView.findViewById(R.id.fragment_event_list);

        mPending = (CheckBox) rootView.findViewById(R.id.pending);
        mInComplete = (CheckBox) rootView.findViewById(R.id.incomplete);
        mComplete = (CheckBox) rootView.findViewById(R.id.completed);

        mPending.setOnClickListener(this);
        mInComplete.setOnClickListener(this);
        mComplete.setOnClickListener(this);

        return createView(rootView);
    }

    protected View createView(View rootView) {

        ArrayList<Event> e = (eventType.equalsIgnoreCase("facility"))
                        ? ModelRepository.getFacilityEvents(eventTypeId, mPending.isChecked(), mInComplete.isChecked(), mComplete.isChecked())
                        : ModelRepository.getNurseEvents(eventTypeId, mPending.isChecked(), mInComplete.isChecked(), mComplete.isChecked());

        AnimatedExpandableListView.AnimatedExpandableListAdapter adapter = new AnimatedExpandableListEventAdapter(Supervisor.mAppContext, e);
        listView.setAdapter(adapter);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });

        // Set indicator (arrow) to the right
        Resources r = getResources();
        DisplayMetrics metrics = Supervisor.mAppContext.getResources().getDisplayMetrics();

        int width = metrics.widthPixels;
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            listView.setIndicatorBounds(width - px, width);
        } else {
            listView.setIndicatorBoundsRelative(width - px, width);
        }

		ViewCompat.setElevation(rootView, 50);

		return rootView;
	}

    @Override
    public void onClick(View v) {
        createView(v);
    }
}