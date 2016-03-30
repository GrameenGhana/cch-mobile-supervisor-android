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
import org.grameenfoundation.cch.supervisor.model.Nurse;
import org.grameenfoundation.cch.supervisor.repository.ModelRepository;
import org.grameenfoundation.cch.supervisor.ui.adaptor.MyStickyListHeadersNurseAdapter;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class NurseListFragment extends Fragment implements View.OnClickListener {

	private static final String ARG_POSITION = "position";
    private static final String ARG_TYPE = "type";
    private static final String ARG_TYPE_ID = "id";

    private CheckBox mInProgress;
    private CheckBox mEligible;
    private CheckBox mPassed;
	private StickyListHeadersListView listView;

	private int position;
    private static String eventType;
    private static String eventTypeId;

    public static NurseListFragment newInstance(int position, String type, String id) {
		NurseListFragment f = new NurseListFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_list_nurse, container, false);
        listView = (StickyListHeadersListView) rootView.findViewById(R.id.fragment_nurse_list);
        listView.setBackgroundResource(R.drawable.list_bg);

        mInProgress = (CheckBox) rootView.findViewById(R.id.inProgress);
        mEligible = (CheckBox) rootView.findViewById(R.id.eligible);
        mPassed = (CheckBox) rootView.findViewById(R.id.passed);

        mInProgress.setOnClickListener(this);
        mEligible.setOnClickListener(this);
        mPassed.setOnClickListener(this);

        return createView(rootView);
    }

    protected View createView(View rootView) {

        ArrayList<Nurse> n = (eventType.equalsIgnoreCase("facility"))
                ? ModelRepository.getFacilityNurses(eventTypeId, mInProgress.isChecked(), mEligible.isChecked(), mPassed.isChecked())
                : ModelRepository.getDistrictNurses(eventTypeId, mInProgress.isChecked(), mEligible.isChecked(), mPassed.isChecked());

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

    @Override
    public void onClick(View v) {
        createView(v);
    }

}