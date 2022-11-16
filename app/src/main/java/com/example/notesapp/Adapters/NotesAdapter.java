package com.example.notesapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notesapp.Models.Notes;
import com.example.notesapp.R;
import com.example.notesapp.RecyclerViewInterface;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> implements Filterable {
    Context context;
    public List<Notes> list;
    List<Notes> filteredList;
    private final RecyclerViewInterface recyclerViewInterface;
    public NotesAdapter(Context context, List<Notes> list, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes, parent, false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.notes.setText(list.get(position).getNote());
        holder.date.setText(list.get(position).getDate());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public int getID(int position){
        return list.get(position).getID();
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder{
        CardView notes_container;
        TextView title, notes, date;
        public NotesViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            notes_container = itemView.findViewById(R.id.notes_container);
            title = itemView.findViewById(R.id.title);
            notes = itemView.findViewById(R.id.notes);
            date = itemView.findViewById(R.id.date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        recyclerViewInterface.LongItemClick(position);
                    }
                    return false;
                }
            });
        }
    }
    @Override
    public Filter getFilter(){
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Notes> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(list);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Notes item : list) {
                    //if (item.getTitle().toLowerCase().contains(filterPattern) || item.getNote().toLowerCase().contains(filterPattern)) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}

