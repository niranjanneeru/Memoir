package com.codingcrew.memoir.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.codingcrew.memoir.R;
import com.codingcrew.memoir.adapters.LoginAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import static com.codingcrew.memoir.utils.Utils.AUTH_KEY;
import static com.codingcrew.memoir.utils.Utils.PREF_KEY;

public class LoginActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton fabGoogle, fabFb, fabTwt;
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        fabGoogle = findViewById(R.id.fabGoogle);
        fabFb = findViewById(R.id.fabFb);
        fabTwt = findViewById(R.id.fabTwt);



        if (!isConnected(this)) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("SignUp"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        fabGoogle.setTranslationY(300);
        fabFb.setTranslationY(300);
        fabTwt.setTranslationY(300);
        tabLayout.setTranslationY(300);

        fabGoogle.setAlpha(v);
        fabFb.setAlpha(v);
        fabTwt.setAlpha(v);
        tabLayout.setAlpha(v);

        fabGoogle.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        fabFb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        fabTwt.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
    }

    private boolean isConnected(LoginActivity loginActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiConn != null && wifiConn.isConnected() || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }
    }
}