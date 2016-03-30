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
import org.grameenfoundation.cch.supervisor.repository.ModelRepository;
import org.grameenfoundation.cch.supervisor.ui.adaptor.MyStickyListHeadersFacilityAdapter;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class FacilityListFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private StickyListHeadersListView listView;

    private int position;

    public static FacilityListFragment newInstance(int position) {
        FacilityListFragment f = new FacilityListFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        listView = (StickyListHeadersListView) rootView.findViewById(R.id.fragment_list);
        listView.setBackgroundResource(R.drawable.list_bg);

        // Create list of regions with districts
        AlphaInAnimationAdapter animationAdapter;
        MyStickyListHeadersFacilityAdapter adapter = new MyStickyListHeadersFacilityAdapter(Supervisor.mAppContext,
                ModelRepository.getFacility());

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