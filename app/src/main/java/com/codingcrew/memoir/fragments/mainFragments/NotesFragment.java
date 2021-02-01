package com.codingcrew.memoir.fragments.mainFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.codingcrew.memoir.R;
import com.codingcrew.memoir.activities.MainActivity;
import com.codingcrew.memoir.activities.SplashActivity;
import com.codingcrew.memoir.adapters.NotesRecyclerAdapter;
import com.codingcrew.memoir.models.Journal;
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

public class NotesFragment extends Fragment implements NotesRecyclerAdapter.OnNotesClickListener, NotesRecyclerAdapter.OnNotesLongClickListener {


    RecyclerView notesRecyclerView;
    LottieAnimationView animationView;
    TextView empty_text;
    BottomSheetDialog bottomSheetDialog;
    NotesRecyclerAdapter adapter;
    ArrayList<Journal> notes;
    Button save, cancel;
    TextInputEditText titleET, descET;
    TextInputLayout titleLayout, descLayout;
    ProgressBar progressBar;
    SharedPreferences preferences;
    private String auth_token;
    private ProgressBar loader;
    private FloatingActionButton new_notes;

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        notesRecyclerView = root.findViewById(R.id.notesRecyclerView);
        loader = root.findViewById(R.id.progressBar);
        loader.setVisibility(View.VISIBLE);
        empty_text = root.findViewById(R.id.empty_text);
        animationView = root.findViewById(R.id.empty);


        bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.new_notes_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        titleLayout = bottomSheetDialog.findViewById(R.id.journal_title);
        descLayout = bottomSheetDialog.findViewById(R.id.description_journal_layout);

        titleET = bottomSheetDialog.findViewById(R.id.journal_input);
        descET = bottomSheetDialog.findViewById(R.id.description_journal);

        save = bottomSheetDialog.findViewById(R.id.save);
        cancel = bottomSheetDialog.findViewById(R.id.cancel);

        preferences = getActivity().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);


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


                        Call<ResponseBody> call = RetrofitClient.getInstance().getAPIClient().postJournalOrDiary(auth_token, new Journal("1", title, desc, "dummy", "dummy", "none", date, "d"));
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

        adapter = new NotesRecyclerAdapter(getActivity().getApplicationContext(), this, this);
        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));

        auth_token = preferences.getString(AUTH_KEY, null);
        if (auth_token != null) {
            Log.d("TAG", "onCreateView: " + auth_token);
            netWorkCall();
        }

        return root;
    }

    private void netWorkCall() {
        loader.setVisibility(View.VISIBLE);
        Call<ArrayList<Journal>> notesCall = RetrofitClient.getInstance().getAPIClient().getNotes(auth_token);
        notesCall.enqueue(new Callback<ArrayList<Journal>>() {
            @Override
            public void onResponse(Call<ArrayList<Journal>> call, Response<ArrayList<Journal>> response) {
                if (response.isSuccessful()) {
                    notes = response.body();
                    adapter.setNotes(notes);
                    adapter.notifyDataSetChanged();
                    loader.setVisibility(View.GONE);
                    if (notes.size() == 0) {
                        notesRecyclerView.setVisibility(View.GONE);
                        animationView.setVisibility(View.VISIBLE);
                        empty_text.setVisibility(View.VISIBLE);
                        animationView.setRepeatCount(LottieDrawable.INFINITE);
                    } else {
                        notesRecyclerView.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.GONE);
                        empty_text.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Journal>> call, Throwable t) {
                Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
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
    public void onNoteClick(int position) {
        onNoteLongClick(position);
    }

    @Override
    public boolean onNoteLongClick(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("Notebook Options")
                .setItems(OPTIONS_NOTEBOOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Utils.share_intent(getContext(), notes.get(position));
                                break;
                            case 1:
                                progressBar.setVisibility(View.GONE);
                                titleLayout.setVisibility(View.VISIBLE);
                                descLayout.setVisibility(View.VISIBLE);
                                save.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.VISIBLE);
                                titleET.setText(notes.get(position).getTitle());
                                descET.setText(notes.get(position).getDescription());

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
                                            notes.get(position).setTitle(title);
                                        }

                                        if (desc.trim().equals("")) {
                                            descLayout.setError("Empty Description");
                                            descLayout.requestFocus();
                                            return;
                                        } else {
                                            descLayout.setError(null);
                                            notes.get(position).setDescription(desc);
                                        }

                                        progressBar.setVisibility(View.VISIBLE);

                                        titleLayout.setVisibility(View.INVISIBLE);
                                        descLayout.setVisibility(View.INVISIBLE);
                                        save.setVisibility(View.INVISIBLE);
                                        cancel.setVisibility(View.INVISIBLE);


                                        Call<Journal> call = RetrofitClient.getInstance().getAPIClient().changeJournalDiary(notes.get(position).getId(), auth_token, notes.get(position));
                                        call.enqueue(new Callback<Journal>() {
                                            @Override
                                            public void onResponse(Call<Journal> call, Response<Journal> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                                    bottomSheetDialog.cancel();
                                                    netWorkCall();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Journal> call, Throwable t) {
                                                Toast.makeText(getContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });

                                bottomSheetDialog.show();
                                break;
                            case 2:
                                Utils.delete(getActivity(), notes.get(position), auth_token);
                                netWorkCall();
                                break;
                        }
                        Log.d("TAG", "onClick: " + which);
                    }
                });
        builder.show();
        return true;
    }
}