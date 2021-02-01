package com.codingcrew.memoir.fragments.mainFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.codingcrew.memoir.R;
import com.codingcrew.memoir.activities.MainActivity;
import com.codingcrew.memoir.activities.SplashActivity;
import com.codingcrew.memoir.adapters.TaskRecyclerAdapter;
import com.codingcrew.memoir.models.Task;
import com.codingcrew.memoir.networking.RetrofitClient;
import com.codingcrew.memoir.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codingcrew.memoir.utils.Utils.AUTH_KEY;
import static com.codingcrew.memoir.utils.Utils.OPTIONS_NOTEBOOK;
import static com.codingcrew.memoir.utils.Utils.PREF_KEY;

public class TodoFragment extends Fragment implements TaskRecyclerAdapter.OnTaskClickListener, TaskRecyclerAdapter.OnTasksLongClickListener {

    RecyclerView taskRecyclerView;
    LottieAnimationView animationView;
    TextView empty_text;
    BottomSheetDialog bottomSheetDialog;
    TaskRecyclerAdapter adapter;
    ArrayList<Task> tasks;
    Button save, cancel;
    TextInputEditText titleET, descET;
    TextInputLayout titleLayout, descLayout;
    ProgressBar progressBar;
    SharedPreferences preferences;
    private String auth_token;
    private ProgressBar loader;
    private FloatingActionButton new_notes;

    public TodoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_todo, container, false);


        taskRecyclerView = root.findViewById(R.id.notesRecyclerView);
        loader = root.findViewById(R.id.progressBar);
        loader.setVisibility(View.VISIBLE);
        empty_text = root.findViewById(R.id.empty_text);
        animationView = root.findViewById(R.id.empty);
        preferences = getActivity().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);


        bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.new_notes_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        titleLayout = bottomSheetDialog.findViewById(R.id.journal_title);
        descLayout = bottomSheetDialog.findViewById(R.id.description_journal_layout);

        titleET = bottomSheetDialog.findViewById(R.id.journal_input);
        descET = bottomSheetDialog.findViewById(R.id.description_journal);

        save = bottomSheetDialog.findViewById(R.id.save);
        cancel = bottomSheetDialog.findViewById(R.id.cancel);

        progressBar = bottomSheetDialog.findViewById(R.id.progressBar_journal);


        if (!isConnected((MainActivity) this.getActivity())) {
            Intent intent = new Intent(getContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        new_notes = root.findViewById(R.id.new_notes);

        new_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleET.setText("");
                descET.setText("");
                progressBar.setVisibility(View.GONE);
                titleLayout.setVisibility(View.VISIBLE);
                descLayout.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = titleET.getText().toString();
                        String desc = descET.getText().toString();
                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        if (title.trim().equals("")) {
                            titleLayout.setError("Empty Title");
                            titleLayout.requestFocus();
                            return;
                        } else {
                            titleLayout.setError(null);
                        }

                        if (desc.trim().equals("")) {
                            descLayout.setError("Empty Description");
                            descLayout.requestFocus();
                            return;
                        } else {
                            descLayout.setError(null);
                        }

                        progressBar.setVisibility(View.VISIBLE);

                        titleLayout.setVisibility(View.INVISIBLE);
                        descLayout.setVisibility(View.INVISIBLE);
                        save.setVisibility(View.INVISIBLE);
                        cancel.setVisibility(View.INVISIBLE);


                        Call<ResponseBody> call = RetrofitClient.getInstance().getAPIClient().pushTask(auth_token, new Task("1", title, desc, "username", date, date));
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    bottomSheetDialog.cancel();
                                    netWorkCall();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                });

                bottomSheetDialog.show();
            }
        });


        adapter = new TaskRecyclerAdapter(getActivity().getApplicationContext(), this, this);
        taskRecyclerView.setAdapter(adapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth_token = preferences.getString(AUTH_KEY, null);
        if (auth_token != null) {
            netWorkCall();
        }


        return root;
    }


    private void netWorkCall() {
        loader.setVisibility(View.VISIBLE);
        Call<ArrayList<Task>> call = RetrofitClient.getInstance().getAPIClient().getTask(auth_token);
        call.enqueue(new Callback<ArrayList<Task>>() {
            @Override
            public void onResponse(Call<ArrayList<Task>> call, Response<ArrayList<Task>> response) {
                if (response.isSuccessful()) {
                    tasks = response.body();
                    adapter.setTasks(tasks);
                    adapter.notifyDataSetChanged();
                    loader.setVisibility(View.GONE);
                    if (tasks.size() == 0) {
                        taskRecyclerView.setVisibility(View.GONE);
                        animationView.setVisibility(View.VISIBLE);
                        empty_text.setVisibility(View.VISIBLE);
                        animationView.setRepeatCount(LottieDrawable.INFINITE);
                    } else {
                        taskRecyclerView.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.GONE);
                        empty_text.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Task>> call, Throwable t) {

            }
        });
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

    @Override
    public void onTaskClick(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("Todo Options")
                .setItems(OPTIONS_NOTEBOOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Utils.share_intent_task(getContext(), tasks.get(position));
                                break;
                            case 1:
                                progressBar.setVisibility(View.GONE);
                                titleLayout.setVisibility(View.VISIBLE);
                                descLayout.setVisibility(View.VISIBLE);
                                save.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.VISIBLE);
                                titleET.setText(tasks.get(position).getTitle());
                                descET.setText(tasks.get(position).getDescription());

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bottomSheetDialog.cancel();
                                    }
                                });

                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String title = titleET.getText().toString();
                                        String desc = descET.getText().toString();

                                        if (title.trim().equals("")) {
                                            titleLayout.setError("Empty Title");
                                            titleLayout.requestFocus();
                                            return;
                                        } else {
                                            titleLayout.setError(null);
                                            tasks.get(position).setTitle(title);
                                        }

                                        if (desc.trim().equals("")) {
                                            descLayout.setError("Empty Description");
                                            descLayout.requestFocus();
                                            return;
                                        } else {
                                            descLayout.setError(null);
                                            tasks.get(position).setDescription(desc);
                                        }

                                        progressBar.setVisibility(View.VISIBLE);

                                        titleLayout.setVisibility(View.INVISIBLE);
                                        descLayout.setVisibility(View.INVISIBLE);
                                        save.setVisibility(View.INVISIBLE);
                                        cancel.setVisibility(View.INVISIBLE);

                                        Call<Task> call = RetrofitClient.getInstance().getAPIClient().changeTask(tasks.get(position).getId(), auth_token, tasks.get(position));
                                        call.enqueue(new Callback<Task>() {
                                            @Override
                                            public void onResponse(Call<Task> call, Response<Task> response) {
                                                if (response.isSuccessful()) {
                                                    bottomSheetDialog.cancel();
                                                    netWorkCall();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Task> call, Throwable t) {

                                            }
                                        });
                                    }
                                });

                                bottomSheetDialog.show();
                                break;
                            case 2:
                                Utils.delete_task(getActivity(), tasks.get(position), auth_token);
                                netWorkCall();
                                break;
                        }
                    }
                });
        builder.show();
    }

    @Override
    public boolean onTaskLongClick(int position) {
        onTaskClick(position);
        return true;
    }
}