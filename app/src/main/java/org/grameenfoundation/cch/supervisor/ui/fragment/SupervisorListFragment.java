package org.grameenfoundation.cch.supervisor.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nhaarman.listviewanimations.appearance.StickyListHeadersAdapterDecorator;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.util.StickyListHeadersListViewWrapper;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.model.Nurse;
import org.grameenfoundation.cch.supervisor.repository.ModelRepository;
import org.grameenfoundation.cch.supervisor.ui.adaptor.MyStickyListHeadersNurseAdapter;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SupervisorListFragment extends Fragment  {

	private static final String ARG_POSITION = "position";
    private static final String ARG_TYPE = "type";
    private static final String ARG_TYPE_ID = "id";

	private StickyListHeadersListView listView;

	private int position;
    private static String eventType;
    private static String eventTypeId;

	public static SupervisorListFragment newInstance(int position, String type, String id) {
		SupervisorListFragment f = new SupervisorListFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        listView = (StickyListHeadersListView) rootView.findViewById(R.id.fragment_list);
        //listView.setBackgroundResource(R.drawable.list_bg);

        ArrayList<Nurse> n = (eventType.equalsIgnoreCase("facility"))
                ? ModelRepository.getFacilitySupervisors(eventTypeId, false, false, false)
                : ModelRepository.getDistrictSupervisors(eventTypeId, false, false, false);

        // Create list of regions with districts
        AlphaInAnimationAdapter animationAdapter;
        MyStickyListHeadersNurseAdapter adapter = new MyStickyListHeadersNurseAdapter(Supervisor.mAppContext, n);

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
}