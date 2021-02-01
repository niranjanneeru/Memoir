package com.codingcrew.memoir.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.codingcrew.memoir.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.codingcrew.memoir.utils.Utils.AUTH_KEY;
import static com.codingcrew.memoir.utils.Utils.PREF_KEY;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
                preferences.edit().remove(AUTH_KEY).apply();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!isConnected(this)) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        BottomNavigationView view = findViewById(R.id.nav_bar_bottom);
        NavController navController = Navigation.findNavController(this, R.id.nav_fragment);

        NavigationUI.setupWithNavController(view, navController);

    }

    private boolean isConnected(MainActivity mainActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiConn != null && wifiConn.isConnected() || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }
    }
}