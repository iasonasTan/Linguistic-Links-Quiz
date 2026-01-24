package com.app.ll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.app.ll.page.AbstractPage;
import com.app.ll.page.QuizFragment;
import com.google.android.material.color.DynamicColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MainActivity extends AppCompatActivity {
    public static final String ACTION_CHANGE_PAGE = "ll.action.changePage";
    public static final String PAGE_NAME_EXTRA    = "ll.extra.pageName";

    private final List<AbstractPage> mPages = new ArrayList<>();

    private final BroadcastReceiver mRequestReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String action = Objects.requireNonNull(intent.getAction());
            // noinspection all
            switch(action) {
                case ACTION_CHANGE_PAGE:
                    String pageName = Objects.requireNonNull(intent.getStringExtra(PAGE_NAME_EXTRA));
                    setPage(pageName);
                    break;
            }
        }

        private void setPage(String pageName) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frag_container, getFragmentByName(pageName))
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

        // initialize fragment
        mPages.add(new QuizFragment());

        // init layout
        if(savedInstanceState==null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frag_container, getFragmentByName(QuizFragment.NAME))
                    .commit();
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