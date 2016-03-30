package org.grameenfoundation.cch.supervisor.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.StickyListHeadersAdapterDecorator;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.util.StickyListHeadersListViewWrapper;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.repository.ModelRepository;
import org.grameenfoundation.cch.supervisor.ui.adaptor.MyStickyListHeadersDistrictAdapter;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class StartupActivity extends BaseActivity {

    private static final String TAG = StartupActivity.class.getSimpleName();
    private static final String PAGE_TAG = "Main page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }

    @Override
    protected void onPostCreate(Bundle si) {
        super.onPostCreate(si);

        if (Supervisor.Db.needDataUpdate()) {
            startSynchronization();
        } else {
            refresh();
        }
    }

    public void refresh() {
        // Set layout details
        ImageView mImage = (ImageView) findViewById(R.id.main_avatar_image);
        TextView mName = (TextView) findViewById(R.id.main_first_line);
        TextView mRole = (TextView) findViewById(R.id.main_second_line);
        StickyListHeadersListView listView = (StickyListHeadersListView) findViewById(R.id.activity_main_list);

        // Set up top bar
        mImage.setImageResource(R.drawable.ic_launcher);
        mName.setText(user.getDisplayName());
        mRole.setText(user.getRole());
        ((TextView) findViewById(R.id.main_third_line)).setText("");

        // Create list of regions with districts
        listView.setBackgroundResource(R.drawable.list_bg);
        MyStickyListHeadersDistrictAdapter adapter = new MyStickyListHeadersDistrictAdapter(this, ModelRepository.getDistricts());

        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(adapter);
        StickyListHeadersAdapterDecorator stickyListHeadersAdapterDecorator = new StickyListHeadersAdapterDecorator(animationAdapter);
        stickyListHeadersAdapterDecorator.setListViewWrapper(new StickyListHeadersListViewWrapper(listView));

        assert animationAdapter.getViewAnimator() != null;
        animationAdapter.getViewAnimator().setInitialDelayMillis(500);

        assert stickyListHeadersAdapterDecorator.getViewAnimator() != null;
        stickyListHeadersAdapterDecorator.getViewAnimator().setInitialDelayMillis(500);

        listView.setAdapter(stickyListHeadersAdapterDecorator);
    }

    protected String getPageTag() { return PAGE_TAG; }
}
