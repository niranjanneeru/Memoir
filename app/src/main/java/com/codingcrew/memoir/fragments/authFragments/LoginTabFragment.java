package com.codingcrew.memoir.fragments.authFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.codingcrew.memoir.R;
import com.codingcrew.memoir.activities.MainActivity;
import com.codingcrew.memoir.models.LoginUser;
import com.codingcrew.memoir.networking.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codingcrew.memoir.utils.Utils.AUTH_KEY;
import static com.codingcrew.memoir.utils.Utils.PREF_KEY;

public class LoginTabFragment extends Fragment {


    float v = 0;
    private TextInputLayout username, password;
    private Button login, forgotPassword;
    private SharedPreferences preferences;
    private ProgressBar loading;
    private TextInputEditText usernameET, passwordET;

    public LoginTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login_tab, container, false);
        username = root.findViewById(R.id.usernameField);
        password = root.findViewById(R.id.passwordField);
        login = root.findViewById(R.id.login);
        forgotPassword = root.findViewById(R.id.forgot_password);
        usernameET = root.findViewById(R.id.usernameInput);
        passwordET = root.findViewById(R.id.passwordInput);
        preferences = getActivity().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        forgotPassword.setVisibility(View.VISIBLE);
        loading = root.findViewById(R.id.progressBar);
        loading.setVisibility(View.GONE);
        username.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);


        username.setTranslationX(800);
        password.setTranslationX(800);
        login.setTranslationX(800);
        forgotPassword.setTranslationX(800);

        username.setAlpha(v);
        password.setAlpha(v);
        login.setAlpha(v);
        forgotPassword.setAlpha(v);

        username.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        forgotPassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameInput = usernameET.getText().toString();
                String passwordInput = passwordET.getText().toString();

                if (usernameInput.trim().equals("")) {
                    username.setError("Empty Credentials");
                    username.requestFocus();
                    return;
                } else {
                    username.setError(null);
                }
                if (passwordInput.trim().equals("")) {
                    password.setError("Empty Credentials");
                    password.requestFocus();
                    return;
                } else {
                    password.setError(null);
                }

                loading.setVisibility(View.VISIBLE);
                username.setVisibility(View.GONE);
                forgotPassword.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                login.setVisibility(View.GONE);


                Call<ResponseBody> call = RetrofitClient.getInstance().getAPIClient().login(new LoginUser(usernameInput, passwordInput));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String s = response.body().string();
                                JsonParser parser = new JsonParser();
                                JsonObject obj = (JsonObject) parser.parse(s);
                                String authKey = "token " + obj.get("auth_token").toString().replaceAll("\"", "");
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(AUTH_KEY, authKey);
                                editor.apply();
                                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                                intent.putExtra(AUTH_KEY, authKey);
                                startActivity(intent);
                                getActivity().finish();
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            loading.setVisibility(View.GONE);
                            forgotPassword.setVisibility(View.VISIBLE);
                            password.setVisibility(View.VISIBLE);
                            username.setVisibility(View.VISIBLE);
                            login.setVisibility(View.VISIBLE);
                            try {
                                String s = response.errorBody().string();
                                if (s.contains("Unable to log in with provided credentials.")) {
                                    username.setError("New Here? Move to Sign Up");
                                    password.setError("Invalid Credentials");
                                    return;
                                } else {
                                    username.setError(null);
                                    password.setError(null);
                                }
                                Toast.makeText(getContext(), "Something Happened Wrong", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loading.setVisibility(View.GONE);
                        forgotPassword.setVisibility(View.VISIBLE);
                        password.setVisibility(View.VISIBLE);
                        username.setVisibility(View.VISIBLE);
                        login.setVisibility(View.VISIBLE);
                        t.printStackTrace();
                        Toast.makeText(getContext(), "Something Happened Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return root;
    }
}