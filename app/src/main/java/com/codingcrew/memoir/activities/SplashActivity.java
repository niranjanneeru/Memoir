package com.codingcrew.memoir.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.codingcrew.memoir.R;
import com.google.android.material.snackbar.Snackbar;

import static com.codingcrew.memoir.utils.Utils.AUTH_KEY;
import static com.codingcrew.memoir.utils.Utils.PREF_KEY;

public class SplashActivity extends AppCompatActivity {
    ImageView logo, splashImage, splash, text;
    LottieAnimationView animationView;
    ConstraintLayout parent;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        logo = findViewById(R.id.logo);
        splashImage = findViewById(R.id.splash);
        animationView = findViewById(R.id.anim);
        splash = findViewById(R.id.splashOriginal);
        parent = findViewById(R.id.parent);
        text = findViewById(R.id.textLogo);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);


        if (!isConnected(this)) {
            showSnackBar();
        } else {
            splash.setVisibility(View.GONE);
            splashImage.animate().translationY(-5500).setDuration(1000).setStartDelay(4000);
            logo.animate().translationY(3000).setDuration(1000).setStartDelay(4000);
            animationView.animate().translationY(3000).setDuration(1000).setStartDelay(4000);
            animationView.setRepeatCount(LottieDrawable.INFINITE);
            text.animate().translationY(3000).setDuration(1000).setStartDelay(4000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String auth_key = preferences.getString(AUTH_KEY, null);
                    if (auth_key != null) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        splashImage.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                }
            }, 7000);
        }
    }

    private void showSnackBar() {
        Snackbar.make(parent, "CHECK NETWORK CONNECTIVITY", Snackbar.LENGTH_INDEFINITE).setAction("CLOSE", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        }).show();
    }

    private boolean isConnected(SplashActivity splashActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) splashActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiConn != null && wifiConn.isConnected() || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }
    }
}