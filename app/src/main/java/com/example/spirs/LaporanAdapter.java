package com.example.spirs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LaporanAdapter
        extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Laporan> laporanList;
    private boolean isAdmin;

    public LaporanAdapter(
            Context context,
            ArrayList<Laporan> laporanList,
            boolean isAdmin
    ) {
        this.context = context;
        this.laporanList = laporanList;
        this.isAdmin = isAdmin;
    }

    public void updateData(
            ArrayList<Laporan> newList,
            boolean isAdmin
    ) {
        this.laporanList = newList;
        this.isAdmin = isAdmin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(context)
                .inflate(
                        R.layout.item_laporan,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Laporan laporan = laporanList.get(position);

        holder.tvJudulLaporan.setText(
                "Judul : " + laporan.getJudul()
        );

        holder.tvLokasi.setText(
                "Lokasi : " + laporan.getLokasi()
        );

        holder.tvStatus.setText(
                "Status : " + laporan.getStatus()
        );

        if (isAdmin) {
            holder.btnEdit.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {

            if (!isAdmin) return;

            Intent intent = new Intent(
                    context,
                    UpdateStatusLaporanActivity.class
            );

            intent.putExtra(
                    "laporan_id",
                    laporan.getId()
            );

            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    DetailLaporanActivity.class
            );

            intent.putExtra(
                    "laporan_id",
                    laporan.getId()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvJudulLaporan;
        TextView tvLokasi;
        TextView tvStatus;
        ImageButton btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvJudulLaporan =
                    itemView.findViewById(
                            R.id.tvJudulLaporan
                    );

            tvLokasi =
                    itemView.findViewById(
                            R.id.tvLokasi
                    );

            tvStatus =
                    itemView.findViewById(
                            R.id.tvStatus
                    );

            btnEdit =
                    itemView.findViewById(
                            R.id.btnEdit
                    );
        }
    }
}