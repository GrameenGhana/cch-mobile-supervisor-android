package org.grameenfoundation.cch.supervisor.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.task.SynchronizationListener;
import org.grameenfoundation.cch.supervisor.task.SynchronizationManager;
import org.grameenfoundation.cch.supervisor.util.ConnectionUtils;

public abstract class BaseActivity extends ActionBarActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private Context mContext = null;
    private Handler handler = null;
    private ProgressDialog progressDialog = null;

    private long activityStartTime;

    protected User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

        if (!ConnectionUtils.isLoggedIn(this)) {
            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
            finish();
        } else {
            handler = new Handler();
            createProgressBar();
            user = Supervisor.getUser();
        }
    }

    public abstract void refresh();

    protected abstract String getPageTag();

    protected void createProgressBar() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setTitle("Updating");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(false);
                progressDialog.setMessage("Please Wait...");
                progressDialog.setIcon(R.drawable.ic_refresh);
                progressDialog.setProgressNumberFormat(null);

                if (SynchronizationManager.getInstance().isSynchronizing()) {
                    progressDialog.show();
                }
            }
        });
    }

    protected void startSynchronization() {
        if (ConnectionUtils.isNetworkConnected(this)) {
            SynchronizationManager.getInstance().registerListener(new SynchronizationListener() {
                @Override
                public void synchronizationStart() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.show();
                        }
                    });
                }

                @Override
                public void synchronizationUpdate(final Integer step, final Integer max, final String message, Boolean reset) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage(message);
                            progressDialog.setMax(max);
                            progressDialog.setProgress(step);
                            progressDialog.setIndeterminate(false);
                            if (!progressDialog.isShowing()) {
                                progressDialog.show();
                            }
                        }
                    });
                }

                @Override
                public void synchronizationUpdate(final String message, Boolean indeterminate) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage(message);
                            progressDialog.setIndeterminate(true);
                            if (!progressDialog.isShowing()) {
                                progressDialog.show();
                            }
                        }
                    });
                }

                @Override
                public void synchronizationComplete() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            refresh();
                        }
                    });

                    SynchronizationManager.getInstance().unRegisterListener(this);
                }

                @Override
                public void onSynchronizationError(final Throwable throwable) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                            AlertDialog alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
                            alertDialog.setMessage(throwable.getMessage());
                            alertDialog.setIcon(android.R.drawable.stat_sys_warning);

                            alertDialog.setTitle("Error");
                            alertDialog.setCancelable(true);
                            alertDialog.show();
                        }
                    });

                    SynchronizationManager.getInstance().unRegisterListener(this);
                }
            });
            SynchronizationManager.getInstance().start();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("No Internet connection");
            builder.setMessage("Cannot update data at the moment. Try again when the device is connected.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            });
            builder.show();
        }
    }

    protected void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_confirm);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Supervisor.Db.onLogout();
                Intent i = new Intent(BaseActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        activityStartTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        Long endTime = System.currentTimeMillis();
        Supervisor.Db.insertLog(getPageTag(), String.valueOf(activityStartTime), endTime.toString());
   }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)  {
            finish();
        }
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_sync:
                startSynchronization();
                return true;
            case R.id.menu_logout:
                logout();
                return true;
        }
        return true;
    }
}
