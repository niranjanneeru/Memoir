package com.codingcrew.memoir.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codingcrew.memoir.R;
import com.codingcrew.memoir.models.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {

    FirebaseStorage storage;
    StorageReference storageReference;
    private Context context;
    private ArrayList<Journal> journals;
    private OnJournalClickListener onJournalClickListener;
    private OnJournalLongClickListener onJournalLongClickListener;

    public JournalRecyclerAdapter(Context context, OnJournalClickListener onJournalClickListener, OnJournalLongClickListener onJournalLongClickListener) {
        this.context = context;
        journals = new ArrayList<>();
        this.onJournalClickListener = onJournalClickListener;
        this.onJournalLongClickListener = onJournalLongClickListener;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("journals");
    }

    public void setJournals(ArrayList<Journal> journals) {
        this.journals = journals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.journal_item, parent, false), onJournalClickListener, onJournalLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(journals.get(position).getTitle());

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.placeholder);

        Glide.with(context).asBitmap().load(R.drawable.placeholder).apply(requestOptions).into(holder.image);


        storageReference.child(journals.get(position).getImage()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Glide.with(context).asBitmap().load(bytes).apply(requestOptions).into(holder.image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });

    }

    @Override
    public int getItemCount() {
        return journals.size();
    }


    public interface OnJournalClickListener {
        void onJournalClick(int position);

        void onJournalItemClick(int adapterPosition);
    }

    public interface OnJournalLongClickListener {
        boolean onJournalLongClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        OnJournalClickListener onJournalClickListener;
        OnJournalLongClickListener onJournalLongClickListener;
        private TextView title, desc;
        private ImageView image;

        public ViewHolder(@NonNull View itemView, OnJournalClickListener onJournalClickListener, OnJournalLongClickListener onJournalLongClickListener) {
            super(itemView);


            title = itemView.findViewById(R.id.name_item_journal);
            image = itemView.findViewById(R.id.image_item_journal);
            this.onJournalClickListener = onJournalClickListener;
            this.onJournalLongClickListener = onJournalLongClickListener;
            image.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            onJournalClickListener.onJournalClick(getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {
            return onJournalLongClickListener.onJournalLongClick(getAdapterPosition());
        }
    }
}
