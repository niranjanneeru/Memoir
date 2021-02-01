package com.codingcrew.memoir.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codingcrew.memoir.R;
import com.codingcrew.memoir.models.Journal;
import com.codingcrew.memoir.networking.RetrofitClient;
import com.codingcrew.memoir.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codingcrew.memoir.utils.Utils.AUTH_KEY;
import static com.codingcrew.memoir.utils.Utils.EMOTES;
import static com.codingcrew.memoir.utils.Utils.PREF_KEY;
import static com.codingcrew.memoir.utils.Utils.SELECT_PHOTO;
import static com.codingcrew.memoir.utils.Utils.SINGLE_KEY_INTENT;

public class JournalDetailActivity extends AppCompatActivity {


    ProgressBar progressBar;
    ImageView image;
    Journal journal;
    ImageView selected_image, picker_image, picker_emotion, delete_item, share_item, back;
    SharedPreferences preferences;
    String auth_token;

    BottomSheetDialog bottomSheetDialog;

    TextInputEditText titleET, descET;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri uri;
    Button save, cancel;
    TextInputLayout titleLayout, descLayout;
    private TextView title, desc, date, emotion;
    private boolean imageChanged = false;
    private FloatingActionButton edit;


    private void make_visible() {
        progressBar.setVisibility(View.GONE);
        picker_image.setVisibility(View.VISIBLE);
        picker_emotion.setVisibility(View.VISIBLE);

        titleLayout.setVisibility(View.VISIBLE);
        descLayout.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        auth_token = preferences.getString(AUTH_KEY, null);


        back = findViewById(R.id.back_nav);

        edit = findViewById(R.id.edit_journal);
        delete_item = findViewById(R.id.action_delete);
        share_item = findViewById(R.id.action_share);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        share_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.share_intent(JournalDetailActivity.this, journal);
            }
        });
        delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.delete(JournalDetailActivity.this, journal, auth_token);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_visible();
                bottomSheetDialog.show();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.new_journal_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(false);


        Intent intent = getIntent();
        if (intent != null) {
            journal = (Journal) intent.getSerializableExtra(SINGLE_KEY_INTENT);
        }

        image = findViewById(R.id.image);
        selected_image = bottomSheetDialog.findViewById(R.id.selected_image);
        picker_image = bottomSheetDialog.findViewById(R.id.picker);
        picker_emotion = bottomSheetDialog.findViewById(R.id.emotion_picker);
        title = findViewById(R.id.item_title);
        desc = findViewById(R.id.item_desc);
        date = findViewById(R.id.item_date);
        emotion = findViewById(R.id.emotion);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("journals");


        titleLayout = bottomSheetDialog.findViewById(R.id.journal_title);
        descLayout = bottomSheetDialog.findViewById(R.id.description_journal_layout);

        titleET = bottomSheetDialog.findViewById(R.id.journal_input);
        descET = bottomSheetDialog.findViewById(R.id.description_journal);

        save = bottomSheetDialog.findViewById(R.id.save);
        cancel = bottomSheetDialog.findViewById(R.id.cancel);

        progressBar = bottomSheetDialog.findViewById(R.id.progressBar_journal);

        if (journal != null) {
            load_data(journal);
        }


        titleET.setText(journal.getTitle());
        descET.setText(journal.getDescription());

        final String[] image = new String[1];
        final String[] emotion = new String[1];
        emotion[0] = journal.getEmotion();
        image[0] = journal.getImage();


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
                String id = journal.getId();
                String date = journal.getDate();

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
                    Toast.makeText(getApplicationContext(), "Emoji is not selected", Toast.LENGTH_SHORT).show();
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

                if (imageChanged) {
                    StorageReference deleteRef = storageReference.child(image[0]);
                    deleteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(JournalDetailActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    image[0] = UUID.randomUUID().toString();
                    StorageReference journalRef = storageReference.child(image[0]);

                    journalRef.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Snackbar.make(findViewById(android.R.id.content), "Uploaded", Snackbar.LENGTH_LONG).show();

                                    Journal data = new Journal(id, title, desc, "dummy", emotion[0], image[0], date, journal.getCategory());
                                    Call<Journal> call = RetrofitClient.getInstance().getAPIClient().changeJournalDiary(id, auth_token, data);
                                    call.enqueue(new Callback<Journal>() {
                                        @Override
                                        public void onResponse(Call<Journal> call, Response<Journal> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(JournalDetailActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                                load_data(data);
                                                bottomSheetDialog.cancel();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Journal> call, Throwable t) {

                                        }
                                    });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(JournalDetailActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Journal data = new Journal(id, title, desc, "dummy", emotion[0], image[0], date, journal.getCategory());
                    Call<Journal> call = RetrofitClient.getInstance().getAPIClient().changeJournalDiary(id, auth_token, data);
                    call.enqueue(new Callback<Journal>() {
                        @Override
                        public void onResponse(Call<Journal> call, Response<Journal> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(JournalDetailActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                load_data(data);
                                bottomSheetDialog.cancel();
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<Journal> call, Throwable t) {

                        }
                    });
                }
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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(JournalDetailActivity.this)
                        .setTitle("Pick An Emotion")
                        .setIcon(R.drawable.ic_emoji)
                        .setItems(EMOTES, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                emotion[0] = EMOTES[which];
                            }
                        });

                builder.show();

            }
        });

        String data = intent.getStringExtra(Utils.EDIT);
        if (data != null) {
            make_visible();
            bottomSheetDialog.show();
        }
    }

    private void load_data(Journal id) {
        picker_emotion.setImageResource(R.drawable.ic_emo_sel);
        picker_image.setImageResource(R.drawable.ic_cam_sel);
        title.setText(id.getTitle());
        desc.setText(id.getDescription());
        date.setText(id.getDate().substring(0, 10));
        emotion.setText(id.getEmotion());
        storageReference.child(id.getImage()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Glide.with(JournalDetailActivity.this).asBitmap().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).load(bytes).into(image);
                    Glide.with(JournalDetailActivity.this).asBitmap().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).load(bytes).into(selected_image);
                    selected_image.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                selected_image.setImageBitmap(bitmap);
                imageChanged = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}