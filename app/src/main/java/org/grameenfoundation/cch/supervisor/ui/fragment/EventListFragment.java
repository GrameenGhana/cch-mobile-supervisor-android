package org.grameenfoundation.cch.supervisor.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.nhaarman.listviewanimations.appearance.StickyListHeadersAdapterDecorator;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.util.StickyListHeadersListViewWrapper;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.model.Event;
import org.grameenfoundation.cch.supervisor.repository.ModelRepository;
import org.grameenfoundation.cch.supervisor.ui.adaptor.MyStickyListHeadersEventAdapter;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class EventListFragment extends Fragment implements View.OnClickListener {

	private static final String ARG_POSITION = "position";
    private static final String ARG_TYPE = "type";
    private static final String ARG_TYPE_ID = "id";

    private CheckBox mPending;
    private CheckBox mInComplete;
    private CheckBox mComplete;
	private StickyListHeadersListView listView;

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
        listView = (StickyListHeadersListView) rootView.findViewById(R.id.fragment_event_list);
        //listView.setBackgroundResource(R.drawable.list_bg);

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

        // Create list of regions with districts
        AlphaInAnimationAdapter animationAdapter;
        MyStickyListHeadersEventAdapter adapter = new MyStickyListHeadersEventAdapter(Supervisor.mAppContext, e);

        animationAdapter = new AlphaInAnimationAdapter(adapter);
        StickyListHeadersAdapterDecorator stickyListHeadersAdapterDecorator =
                new StickyListHeadersAdapterDecorator(animationAdapter);
        stickyListHeadersAdapterDecorator
                .setListViewWrapper(new StickyListHeadersListViewWrapper(listView));

        assert animationAdapter.getViewAnimator() != null;
        animationAdapter.getViewAnimator().setInitialDelayMillis(500);

        assert stickyListHeadersAdapterDecorator.getViewAnimator() != null;
        stickyListHeadersAdapterDecorator.getViewAnimator().setInitialDelayMillis(500);

        listView.setAdapter(stickyListHeadersAdapterDecorator);

		ViewCompat.setElevation(rootView, 50);

		return rootView;
	}

    @Override
    public void onClick(View v) {
        createView(v);
    }
}