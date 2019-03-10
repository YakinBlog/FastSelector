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
    public static final int TYPE_CREATE = 3;

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int dataIndex = position - 1;
        if (getItemViewType(position) == TYPE_CREATE) {
            holder.image.setBackgroundResource(R.color.placeholder);
            holder.image.setImageResource(R.drawable.ic_camera);
            holder.image.setScaleType(ImageView.ScaleType.CENTER);
            if(listener != null) {
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onCreateClick();
                    }
                });
            }
            holder.delete.setVisibility(View.INVISIBLE);
        } else if (getItemViewType(position) == TYPE_SELECT) {
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
                    if (dataIndex != RecyclerView.NO_POSITION) {
                        list.remove(dataIndex);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                    }
                }
            });
            final MediaModel media = list.get(dataIndex);
            holder.panel.setVisibility(View.VISIBLE);
            if(MimeTypeUtil.isVideo(media.getMimeType())) {
                holder.panel.setText(DateUtils.timeParse(media.getDuration()));
                Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.video_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.panel.setCompoundDrawables(drawable, null, null, null);
            } else if (MimeTypeUtil.isAudio(media.getMimeType())) {
                holder.panel.setText(DateUtils.timeParse(media.getDuration()));
                Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.audio_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.panel.setCompoundDrawables(drawable, null, null, null);
            } else if (MimeTypeUtil.isImage(media.getMimeType())){
                holder.panel.setText("裁剪");
                Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.edit_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.panel.setCompoundDrawables(drawable, null, null, null);
                if(listener != null) {
                    holder.panel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onCropClick(media, holder.itemView);
                        }
                    });
                }
            }

            if (MimeTypeUtil.isAudio(media.getMimeType())) {
                holder.image.setBackgroundResource(R.color.placeholder);
                holder.image.setImageResource(R.drawable.ic_audio);
                holder.image.setScaleType(ImageView.ScaleType.CENTER);
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
                        listener.onBrowseClick(media, holder.itemView);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 2;
    }

    private boolean isSelectItem(int position) {
        int size = list.size() == 0 ? 1 : list.size() + 1;
        return position == size;
    }

    private boolean isCreateItem(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isCreateItem(position)) {
            return TYPE_CREATE;
        } else if (isSelectItem(position)) {
            return TYPE_SELECT;
        } else {
            return TYPE_BROWSER;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageView delete;
        TextView panel;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            delete = view.findViewById(R.id.delete);
            panel = view.findViewById(R.id.panel);
        }
    }

    private onItemClickListener listener;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public interface onItemClickListener {

        void onSelectClick();
        void onCreateClick();
        void onCropClick(MediaModel media, View view);
        void onBrowseClick(MediaModel media, View view);
    }
}
