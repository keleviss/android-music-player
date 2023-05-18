package com.viss.mymusicplayerfirst;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder>{

    ArrayList<Song> songsList;
    Context context;

    public SongListAdapter(ArrayList<Song> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate song row item layout
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new SongListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song songData = songsList.get(position);
        holder.titleTextView.setText(songData.getTitle());
        String duration = formatDuration(songData.getDuration());
        holder.durationTextView.setText(duration);

        if (MyMediaPlayer.currentIndex == position) {
            holder.titleTextView.setTextColor(Color.parseColor("#FF00E9FE"));
        } else {
            holder.titleTextView.setTextColor(Color.parseColor("#FFFFFF"));
        }

        holder.itemView.setOnClickListener(v -> {
                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = holder.getAdapterPosition();
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putExtra("LIST", songsList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView iconImageView;
        TextView durationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleView);
            iconImageView = itemView.findViewById(R.id.artworkView);
            durationTextView = itemView.findViewById(R.id.durationView);
            titleTextView.setSelected(true);

            Typeface mediumFont = ResourcesCompat.getFont(itemView.getContext(), R.font.gothammedium);
            Typeface lightFont = ResourcesCompat.getFont(itemView.getContext(), R.font.gothamlight);
            titleTextView.setTypeface(mediumFont);
            durationTextView.setTypeface(lightFont);
        }
    }

    @SuppressLint("DefaultLocale")
    public String formatDuration (String duration) {
        long millis = Long.parseLong(duration);

        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(millis);
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        if (minutes > 60) {
            int hours = minutes / 60;
            minutes %= 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return String.format("%02d:%02d", minutes, seconds);
    }

}
