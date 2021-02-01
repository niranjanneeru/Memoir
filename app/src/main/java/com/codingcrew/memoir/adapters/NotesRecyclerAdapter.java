package com.codingcrew.memoir.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingcrew.memoir.R;
import com.codingcrew.memoir.models.Journal;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Journal> notes;
    private OnNotesClickListener onNotesClickListener;
    private OnNotesLongClickListener onNotesLongClickListener;

    public NotesRecyclerAdapter(Context context, OnNotesClickListener onNotesClickListener, OnNotesLongClickListener onNotesLongClickListener) {
        this.context = context;
        this.notes = new ArrayList<>();
        this.onNotesClickListener = onNotesClickListener;
        this.onNotesLongClickListener = onNotesLongClickListener;
    }

    public void setNotes(ArrayList<Journal> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.journal_notes, parent, false), onNotesClickListener, onNotesLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(notes.get(position).getTitle());
        holder.desc.setText(notes.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public interface OnNotesClickListener {
        void onNoteClick(int position);
    }

    public interface OnNotesLongClickListener {
        boolean onNoteLongClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        OnNotesClickListener onNotesClickListener;
        OnNotesLongClickListener onNotesLongClickListener;
        private TextView title, desc;

        public ViewHolder(@NonNull View itemView, NotesRecyclerAdapter.OnNotesClickListener onJournalClickListener, NotesRecyclerAdapter.OnNotesLongClickListener onJournalLongClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title_notes);
            desc = itemView.findViewById(R.id.title_desc);
            this.onNotesClickListener = onJournalClickListener;
            this.onNotesLongClickListener = onJournalLongClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("TAG", "onClick: ");
            onNotesClickListener.onNoteClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return onNotesLongClickListener.onNoteLongClick(getAdapterPosition());
        }
    }
}
