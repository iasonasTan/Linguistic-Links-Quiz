package com.app.ll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.ll.page.AbstractPage;
import com.app.ll.page.QuizFragment;
import com.app.ll.page.TableFragment;
import com.google.android.material.color.DynamicColors;

import java.util.List;
import java.util.Objects;

public final class MainActivity extends AppCompatActivity {
    public static final String ACTION_CHANGE_PAGE = "ll.action.changePage";
    public static final String PAGE_NAME_EXTRA    = "ll.extra.pageName";
    public static final String USE_ANIMATION_EXTRA= "ll.extra.useAnimation";

    private Vibrator mVibrator;
    private VibrationEffect mSwitchPageEffect;
    private List<AbstractPage> mPages;

    private final BroadcastReceiver mRequestReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String action = Objects.requireNonNull(intent.getAction());
            // noinspection all : one-case switch statement
            switch(action) {
                case ACTION_CHANGE_PAGE:
                    mVibrator.vibrate(mSwitchPageEffect);
                    String pageName = Objects.requireNonNull(intent.getStringExtra(PAGE_NAME_EXTRA));
                    boolean animation = intent.getBooleanExtra(USE_ANIMATION_EXTRA, true);
                    setPage(pageName, animation);
                    break;
            }
        }

        private void setPage(String pageName, boolean animation) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(animation)
                transaction.setCustomAnimations(R.anim.to_left, R.anim.to_right);
            for (AbstractPage page : mPages)
                transaction.hide(page);
            transaction
                    .show(getFragmentByName(pageName))
                    .commit();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // config
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        DynamicColors.applyToActivityIfAvailable(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // register receivers
        ContextCompat.registerReceiver(this, mRequestReceiver, createIntentFilter(), ContextCompat.RECEIVER_NOT_EXPORTED);

        // initialize utils
        mVibrator = getSystemService(Vibrator.class);
        mSwitchPageEffect = VibrationEffect.createOneShot(135, VibrationEffect.DEFAULT_AMPLITUDE);

        // initialize layout
        mPages = List.of(
                new QuizFragment(),
                new TableFragment()
        );
        if(savedInstanceState==null) {
            for(AbstractPage page: mPages) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frag_container, page)
                        .commit();
            }
        }

        sendBroadcast(new Intent(ACTION_CHANGE_PAGE)
                .setPackage(getPackageName())
                .putExtra(USE_ANIMATION_EXTRA, false)
                .putExtra(PAGE_NAME_EXTRA, QuizFragment.NAME));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRequestReceiver);
    }

    // Requires Updates
    private IntentFilter createIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHANGE_PAGE);
        return filter;
    }

    private Fragment getFragmentByName(String name) {
        for (AbstractPage page : mPages) {
            if(page.getName().equals(name))
                return page;
        }
        throw new IllegalArgumentException("Cannot find fragment with name '"+name+"'");
    }
}