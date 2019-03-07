package com.yakin.fastselector.simple;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.fastselector.utils.MimeTypeUtil;

import java.util.ArrayList;
import java.util.List;

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder> {

    public static final int TYPE_SELECT = 1;
    public static final int TYPE_BROWSER = 2;

    private LayoutInflater inflater;
    private List<MediaModel> list;

    public GridImageAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>();
    }

    public void setMediaList(List<MediaModel> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    public void addMediaList(List<MediaModel> list) {
        this.list.addAll(list);
    }

    public void addMediaItem(MediaModel media) {
        this.list.add(media);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.grid_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SELECT) {
            holder.image.setImageResource(R.drawable.ic_add);
            if(listener != null) {
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onSelectClick();
                    }
                });
            }
            holder.delete.setVisibility(View.INVISIBLE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = holder.getAdapterPosition();
                    if (index != RecyclerView.NO_POSITION) {
                        list.remove(index);
                        notifyItemRemoved(index);
                        notifyItemRangeChanged(index, list.size());
                    }
                }
            });
            final MediaModel media = list.get(position);

            if(MimeTypeUtil.isVideo(media.getMimeType())) {
                holder.duration.setVisibility(View.VISIBLE);
                holder.duration.setText(DateUtils.timeParse(media.getDuration()));
                Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.video_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.duration.setCompoundDrawables(drawable, null, null, null);
            } else if (MimeTypeUtil.isAudio(media.getMimeType())) {
                holder.duration.setVisibility(View.VISIBLE);
                holder.duration.setText(DateUtils.timeParse(media.getDuration()));
                Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.picture_audio);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.duration.setCompoundDrawables(drawable, null, null, null);
            }

            if (MimeTypeUtil.isAudio(media.getMimeType())) {
                holder.image.setImageResource(R.drawable.audio_placeholder);
            } else {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(holder.itemView.getContext())
                        .load(media.getPath())
                        .apply(options)
                        .into(holder.image);
            }
            if (listener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onBrowseClick(media, view);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    private boolean isShowSelectItem(int position) {
        int size = list.size() == 0 ? 0 : list.size();
        return position == size;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowSelectItem(position)) {
            return TYPE_SELECT;
        } else {
            return TYPE_BROWSER;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageView delete;
        TextView duration;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            delete = view.findViewById(R.id.delete);
            duration = view.findViewById(R.id.duration);
        }
    }

    private onItemClickListener listener;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public interface onItemClickListener {

        void onSelectClick();
        void onBrowseClick(MediaModel media, View view);
    }
}
