package com.app.ll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ll.page.AbstractPage;
import com.app.ll.page.QuizFragment;
import com.app.ll.page.SettingsFragment;
import com.app.ll.page.TableFragment;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Objects;

public final class MainActivity extends AppCompatActivity {
    private static Vibrator sVibrator;
    private VibrationEffect mSwitchPageEffect;
    private List<AbstractPage> mPages;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        @SuppressWarnings("all") // Switch has too fiew case labels
        public void onReceive(Context context, Intent intent) {
            String action = Objects.requireNonNull(intent.getAction());
            switch(action) {
                case SettingsManager.ACTION_LOAD_SETTINGS:
                    loadSettings();
                    break;
            }
        }
    };

    private void loadSettings() {
        SharedPreferences preferences = getSharedPreferences(SettingsManager.SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean enableVibrations = preferences.getBoolean(SettingsManager.ENABLE_VIBRATIONS_NAME, true);
        sVibrator = enableVibrations ? getSystemService(Vibrator.class) : null;
        mSwitchPageEffect = enableVibrations ? VibrationEffect.createOneShot(135, VibrationEffect.DEFAULT_AMPLITUDE) : null;
    }

    public static void vibrate(VibrationEffect effect) {
        if(sVibrator!=null)
            sVibrator.vibrate(effect);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unusable code
        // TODO Remove unregister receiver logic
        //unregisterReceiver(mReceiver);
        //mPages.forEach(AbstractPage::unregisterReceivers);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // config
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        DynamicColors.applyToActivityIfAvailable(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.quiz,
                R.string.all_links
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // initialize utils
        IntentFilter filter = new IntentFilter(SettingsManager.ACTION_LOAD_SETTINGS);
        ContextCompat.registerReceiver(this, mReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);

        loadSettings();

        NavigationView view = findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            String name = null;
            boolean effects = true;
            if(id==R.id.nav_home) {
                name = QuizFragment.NAME;
            } else if (id==R.id.nav_list) {
                name = TableFragment.NAME;
            } else if (id==R.id.nav_settings) {
                name = SettingsFragment.NAME;
            }
            if(name != null) {
                setPage(name, effects);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // initialize layout
        mPages = List.of(
                new QuizFragment(),
                new TableFragment(),
                new SettingsFragment()
        );
        if(savedInstanceState==null) {
            for(AbstractPage page: mPages) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, page)
                        .commit();
            }
        }
        setPage(QuizFragment.NAME, false);
    }

    private void setPage(String pageName, boolean effects) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragmentToShow = getFragmentByName(pageName);
        if(!fragmentToShow.isVisible()) {
            for (AbstractPage page : mPages)
                transaction.hide(page);
            if(effects) {
                vibrate(mSwitchPageEffect);
                transaction.setCustomAnimations(R.anim.to_left, R.anim.to_right);
            }
            transaction
                    .show(fragmentToShow)
                    .commit();
        }
    }

    private Fragment getFragmentByName(String name) {
        for (AbstractPage page : mPages) {
            if(page.getName().equals(name))
                return page;
        }
        throw new IllegalArgumentException("Cannot find fragment with name '"+name+"'");
    }
}