package com.codingcrew.memoir.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.codingcrew.memoir.activities.JournalDetailActivity;
import com.codingcrew.memoir.models.Journal;
import com.codingcrew.memoir.networking.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {
    public static final String PREF_KEY = "PREF_DATA";
    public static final String AUTH_KEY = "token";
    public static final String SINGLE_KEY_INTENT = "Intent for single key";
    public static final int SELECT_PHOTO = 1;
    public static final String[] EMOTES = {"\uD83D\uDE43", "\uD83E\uDD73", "\uD83D\uDE22", "\uD83D\uDE33", "\uD83D\uDE21"};
    public static final String[] OPTIONS_NOTEBOOK = {"Share", "Edit", "Delete"};
    public static final String EDIT = "EDIT_OPTION_ACTIVE";
    public static String AUTH_KEY_USER = null;

    public static void share_intent(Context context, Journal journal) {
        Intent msgIntent = new Intent();
        msgIntent.setAction(Intent.ACTION_SEND);
        String msg = journal.getTitle() + " " + journal.getEmotion() + "\n" + journal.getDescription();
        msgIntent.putExtra(Intent.EXTRA_TEXT, msg);
        msgIntent.setType("text/plain");
        context.startActivity(msgIntent);
    }

    public static void share_intent_task(Context context, com.codingcrew.memoir.models.Task task) {
        Intent msgIntent = new Intent();
        msgIntent.setAction(Intent.ACTION_SEND);
        String msg = task.getTitle() + " " + task.getDescription() + "\n" + task.getDate();
        msgIntent.putExtra(Intent.EXTRA_TEXT, msg);
        msgIntent.setType("text/plain");
        context.startActivity(msgIntent);
    }

    public static void delete(Activity context, Journal journal, String auth_token) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete \"" + journal.getTitle() + "\"")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<ResponseBody> call = RetrofitClient.getInstance().getAPIClient().deleteJournal(journal.getId(), auth_token);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "Deletion Successful", Toast.LENGTH_SHORT).show();

                                    if (journal.getImage().equals("none")) {

                                    } else {

                                        FirebaseStorage.getInstance().getReference().child("journals/" + journal.getImage()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    if (context.getClass() == JournalDetailActivity.class) {
                                        context.finish();
                                    } else {
//                                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public static void delete_task(Activity applicationContext, com.codingcrew.memoir.models.Task task, String auth_token) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(applicationContext)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete \"" + task.getTitle() + "\"")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<ResponseBody> call = RetrofitClient.getInstance().getAPIClient().deleteTask(task.getId(), auth_token);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(applicationContext, "Deletion Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("TAG", "onResponse: " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}
