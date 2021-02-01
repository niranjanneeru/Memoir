package com.codingcrew.memoir.fragments.authFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.codingcrew.memoir.models.User;
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

public class SignUpTabFragment extends Fragment {

    float v = 0;
    ProgressBar loading;
    private TextInputLayout email, password, username;
    private SharedPreferences preferences;
    private TextInputEditText emailET, passwordET, usernameET;
    private Button signup;


    public SignUpTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up_tab, container, false);

        email = root.findViewById(R.id.usernameField);
        password = root.findViewById(R.id.passwordField);
        username = root.findViewById(R.id.nameField);
        signup = root.findViewById(R.id.sign_up);
        emailET = root.findViewById(R.id.usernameInput);
        passwordET = root.findViewById(R.id.passwordInput);
        usernameET = root.findViewById(R.id.nameInput);
        preferences = getActivity().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        loading = root.findViewById(R.id.progressBar);
        loading.setVisibility(View.GONE);
        email.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);
        signup.setVisibility(View.VISIBLE);

        email.setTranslationX(800);
        password.setTranslationX(800);
        username.setTranslationX(800);
        signup.setTranslationX(800);

        email.setAlpha(v);
        password.setAlpha(v);
        username.setAlpha(v);
        signup.setAlpha(v);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        username.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        signup.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = emailET.getText().toString();
                String usernameInput = usernameET.getText().toString();
                String passwordInput = passwordET.getText().toString();

                if (emailInput.trim().equals("")) {
                    email.setError("Empty Credentials");
                    email.requestFocus();
                    return;
                } else {
                    email.setError(null);
                }
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
                if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    email.setError("Invalid Mail Id");
                    email.requestFocus();
                    return;
                } else {
                    email.setError(null);
                }
                if (passwordInput.length() < 8) {
                    password.setError("Minimum 8 Characters");
                    password.requestFocus();
                    return;
                } else {
                    password.setError(null);
                }
                loading.setVisibility(View.VISIBLE);
                email.setVisibility(View.INVISIBLE);
                password.setVisibility(View.INVISIBLE);
                username.setVisibility(View.INVISIBLE);
                signup.setVisibility(View.INVISIBLE);

                User user = new User(emailInput, usernameInput, passwordInput, passwordInput);

                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getAPIClient()
                        .createUser(user);


                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.isSuccessful()) {
                                String s = response.body().toString();
                                call = RetrofitClient.getInstance().getAPIClient()
                                        .login(new LoginUser(usernameInput, passwordInput));
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
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        loading.setVisibility(View.GONE);
                                        email.setVisibility(View.VISIBLE);
                                        password.setVisibility(View.VISIBLE);
                                        username.setVisibility(View.VISIBLE);
                                        signup.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        loading.setVisibility(View.GONE);
                                        email.setVisibility(View.VISIBLE);
                                        password.setVisibility(View.VISIBLE);
                                        username.setVisibility(View.VISIBLE);
                                        signup.setVisibility(View.VISIBLE);
                                        Toast.makeText(getContext(), "Something Happened", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                String s = response.errorBody().string();
                                loading.setVisibility(View.GONE);
                                email.setVisibility(View.VISIBLE);
                                password.setVisibility(View.VISIBLE);
                                username.setVisibility(View.VISIBLE);
                                signup.setVisibility(View.VISIBLE);
                                if (s.contains("A user with that username already exists.")) {
                                    username.setError("Username Taken");
                                    username.requestFocus();
                                    return;
                                }
                                if (s.contains("This password is too common.")) {
                                    password.setError("Too Common Password");
                                    password.requestFocus();
                                    return;
                                }
                                if (s.contains("The password is too similar to the email address.")) {
                                    password.setError("Password is too similar with email address.");
                                    password.requestFocus();
                                    return;
                                }

                                if (s.contains("The password is too similar to the username.")) {
                                    password.setError("Password is too similar with username.");
                                    password.requestFocus();
                                    return;
                                }
                                Log.d("TAG", "onResponse: " + response.code() + s);
                                Toast.makeText(getContext(), "Something Happened Wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        loading.setVisibility(View.GONE);
                        email.setVisibility(View.VISIBLE);
                        password.setVisibility(View.VISIBLE);
                        username.setVisibility(View.VISIBLE);
                        signup.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        return root;
    }
}