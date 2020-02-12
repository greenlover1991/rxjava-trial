package com.sgo.hotgists.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sgo.hotgists.R;
import com.sgo.hotgists.models.Gist;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a given Gist listing
 */
public class GistListingAdapter extends RecyclerView.Adapter<GistListingAdapter.GistViewHolder> {

  private final ArrayList<Gist> data;

  GistListingAdapter() {
    this.data = new ArrayList<>();
  }

  void setData(List<Gist> data) {
    this.data.clear();
    this.data.addAll(data);
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public GistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new GistViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_gist, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull GistViewHolder holder, int position) {
    Gist gist = data.get(position);
    holder.gistId.setText(gist.getId());
    holder.gistUrl.setText(gist.getUrl());
    holder.filename.setText(gist.getFilenamesAsCsv());
    holder.iconFavorite.setImageResource(gist.isFavorite() ? R.drawable.baseline_favorite_black_24 : R.drawable.baseline_favorite_border_black_24);

    if (gist.getCount() > 0) {
      final Context context = holder.gistOwner.getContext();
      final Resources res = context.getResources();

      Glide.with(context)
          .load(gist.getOwnerAvatarUrl())
          .centerCrop()
          .into(holder.gistOwnerAvatar);
      holder.gistOwner.setText(gist.getOwnerLogin());
      holder.gistCount.setText(res.getString(R.string.lbl_gists_count, gist.getCount()));

      holder.gistOwnerRow.setVisibility(View.VISIBLE);
    } else {
      holder.gistOwnerRow.setVisibility(View.GONE);
    }
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  public Gist getItem(int position) {
    return data.get(position);
  }

  // TODO clarification: if the user+count should be a different item
  // create a different viewholder
  static class GistViewHolder extends RecyclerView.ViewHolder {

    private final TextView gistId;
    private final TextView gistUrl;
    private final TextView filename;
    private final ImageView iconFavorite;

    private final View gistOwnerRow;
    private final ImageView gistOwnerAvatar;
    private final TextView gistOwner;
    private final TextView gistCount;

    GistViewHolder(@NonNull View itemView) {
      super(itemView);
      this.gistId = itemView.findViewById(R.id.gistId);
      this.gistUrl = itemView.findViewById(R.id.gistUrl);
      this.filename = itemView.findViewById(R.id.filename);
      this.iconFavorite = itemView.findViewById(R.id.iconFavorite);
      this.gistOwnerRow = itemView.findViewById(R.id.gistOwnerRow);
      this.gistOwnerAvatar = itemView.findViewById(R.id.gistOwnerAvatar);
      this.gistOwner = itemView.findViewById(R.id.gistOwner);
      this.gistCount = itemView.findViewById(R.id.gistCount);

    }

  }
}
