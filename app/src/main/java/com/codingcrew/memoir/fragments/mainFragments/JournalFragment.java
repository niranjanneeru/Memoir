package com.codingcrew.memoir.fragments.mainFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.codingcrew.memoir.R;
import com.codingcrew.memoir.activities.JournalDetailActivity;
import com.codingcrew.memoir.activities.MainActivity;
import com.codingcrew.memoir.activities.SplashActivity;
import com.codingcrew.memoir.adapters.JournalRecyclerAdapter;
import com.codingcrew.memoir.models.Journal;
import com.codingcrew.memoir.networking.RetrofitClient;
import com.codingcrew.memoir.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.codingcrew.memoir.utils.Utils.AUTH_KEY;
import static com.codingcrew.memoir.utils.Utils.EMOTES;
import static com.codingcrew.memoir.utils.Utils.OPTIONS_NOTEBOOK;
import static com.codingcrew.memoir.utils.Utils.PREF_KEY;
import static com.codingcrew.memoir.utils.Utils.SELECT_PHOTO;
import static com.codingcrew.memoir.utils.Utils.SINGLE_KEY_INTENT;

public class JournalFragment extends Fragment implements JournalRecyclerAdapter.OnJournalClickListener, JournalRecyclerAdapter.OnJournalLongClickListener {

    RecyclerView journalRecyclerView;
    LottieAnimationView animationView;
    Uri uri;
    TextView empty_text;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageView selected_image, picker_image, picker_emotion;
    JournalRecyclerAdapter adapter;
    ArrayList<Journal> journals;
    SharedPreferences preferences;
    private String auth_token;
    private ProgressBar loader;
    private FloatingActionButton new_journal;

    public JournalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_journal, container, false);
        journalRecyclerView = root.findViewById(R.id.journalRecyclerView);
        loader = root.findViewById(R.id.progressBar);
        loader.setVisibility(View.VISIBLE);
        empty_text = root.findViewById(R.id.empty_text);
        animationView = root.findViewById(R.id.empty);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if (!isConnected((MainActivity) this.getActivity())) {
            Intent intent = new Intent(getContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        new_journal = root.findViewById(R.id.new_journal);
        preferences = getActivity().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        adapter = new JournalRecyclerAdapter(getActivity().getApplicationContext(), this, this);
        journalRecyclerView.setAdapter(adapter);
        journals = new ArrayList<>();
        adapter.setJournals(journals);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        journalRecyclerView.setLayoutManager(manager);


        auth_token = preferences.getString(AUTH_KEY, null);
        if (auth_token != null) {
            netWorkCall();
        }


        new_journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.new_journal_sheet);
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                selected_image = bottomSheetDialog.findViewById(R.id.selected_image);
                picker_image = bottomSheetDialog.findViewById(R.id.picker);
                picker_emotion = bottomSheetDialog.findViewById(R.id.emotion_picker);
                TextInputLayout titleLayout, descLayout;
                titleLayout = bottomSheetDialog.findViewById(R.id.journal_title);
                descLayout = bottomSheetDialog.findViewById(R.id.description_journal_layout);
                TextInputEditText titleET, descET;
                titleET = bottomSheetDialog.findViewById(R.id.journal_input);
                descET = bottomSheetDialog.findViewById(R.id.description_journal);
                Button save, cancel;
                save = bottomSheetDialog.findViewById(R.id.save);
                cancel = bottomSheetDialog.findViewById(R.id.cancel);
                ProgressBar progressBar;
                progressBar = bottomSheetDialog.findViewById(R.id.progressBar_journal);

                progressBar.setVisibility(View.GONE);
                selected_image.setVisibility(View.GONE);
                picker_image.setVisibility(View.VISIBLE);
                picker_emotion.setVisibility(View.VISIBLE);

                titleLayout.setVisibility(View.VISIBLE);
                descLayout.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);


                final String[] image = new String[1];
                final String[] emotion = new String[1];
                emotion[0] = null;


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
                        String id = nextId();
                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        image[0] = "dummy";

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

                        if (emotion[0] == null) {
                            Toast.makeText(getContext(), "Emoji is not selected", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (selected_image.getVisibility() == View.GONE) {
                            Toast.makeText(getContext(), "Select Image", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        progressBar.setVisibility(View.VISIBLE);
                        selected_image.setVisibility(View.INVISIBLE);
                        picker_image.setVisibility(View.INVISIBLE);
                        picker_emotion.setVisibility(View.INVISIBLE);

                        titleLayout.setVisibility(View.INVISIBLE);
                        descLayout.setVisibility(View.INVISIBLE);
                        save.setVisibility(View.INVISIBLE);
                        cancel.setVisibility(View.INVISIBLE);


                        image[0] = UUID.randomUUID().toString();
                        StorageReference journalRef = storageReference.child("journals/" + image[0]);

                        journalRef.putFile(uri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Uploaded", Snackbar.LENGTH_LONG).show();
                                        Call<ResponseBody> call = RetrofitClient.getInstance().getAPIClient().postJournalOrDiary(auth_token, new Journal(id, title, desc, "dummy", emotion[0], image[0], date, "j"));
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
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        exception.printStackTrace();
                                        Toast.makeText(getContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

                picker_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, SELECT_PHOTO);
                    }
                });


                picker_emotion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                                .setTitle("Pick An Emotion")
                                .setIcon(R.drawable.ic_emoji)
                                .setItems(EMOTES, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        emotion[0] = EMOTES[which];
                                        picker_emotion.setImageResource(R.drawable.ic_emo_sel);
                                    }
                                });

                        builder.show();

                    }
                });


                bottomSheetDialog.show();
            }
        });

        return root;
    }

    private void netWorkCall() {
        loader.setVisibility(View.VISIBLE);
        Call<ArrayList<Journal>> journalCall = RetrofitClient.getInstance().getAPIClient().getJournals(auth_token);
        journalCall.enqueue(new Callback<ArrayList<Journal>>() {
            @Override
            public void onResponse(Call<ArrayList<Journal>> call, Response<ArrayList<Journal>> response) {
                if (response.isSuccessful()) {
                    journals = response.body();
                    adapter.setJournals(journals);
                    adapter.notifyDataSetChanged();
                    loader.setVisibility(View.GONE);
                    if (journals.size() == 0) {
                        journalRecyclerView.setVisibility(View.GONE);
                        animationView.setVisibility(View.VISIBLE);
                        animationView.setRepeatCount(LottieDrawable.INFINITE);
                        empty_text.setVisibility(View.VISIBLE);
                    } else {
                        journalRecyclerView.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.GONE);
                        empty_text.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Journal>> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }


    private String nextId() {
        int id = -1;
        for (Journal journal : journals
        ) {
            id = Math.max(id, Integer.parseInt(journal.getId()));
        }
        return String.valueOf(id + 1);
    }

    @Override
    public void onJournalClick(int position) {
        Intent intent = new Intent(getContext(), JournalDetailActivity.class);
        intent.putExtra(SINGLE_KEY_INTENT, journals.get(position));
        startActivity(intent);
    }

    @Override
    public void onJournalItemClick(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("Notebook Options")
                .setItems(OPTIONS_NOTEBOOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Utils.share_intent(getContext(), journals.get(position));
                                break;
                            case 1:
                                Intent intent = new Intent(getContext(), JournalDetailActivity.class);
                                intent.putExtra(SINGLE_KEY_INTENT, journals.get(position));
                                intent.putExtra(Utils.EDIT, Utils.EDIT);
                                startActivity(intent);
                                break;
                            case 2:
                                Utils.delete(getActivity(), journals.get(position), auth_token);
                                netWorkCall();
                                break;
                        }
                        Log.d("TAG", "onClick: " + which);
                    }
                });
        builder.show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                selected_image.setImageBitmap(bitmap);
                selected_image.setVisibility(View.VISIBLE);
                picker_image.setImageResource(R.drawable.ic_cam_sel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onJournalLongClick(int position) {
        onJournalItemClick(position);
        return true;
    }
}