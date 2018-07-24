package com.example.dong.music;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolderSong>{

    private Context mContext;
    private InterfaceSong mInterfaceSong;
    private int mPositionClick = -1;

    public SongAdapter(Context context, InterfaceSong interfaceSong){
        this.mContext = context;
        this.mInterfaceSong = interfaceSong;
    }

    @NonNull
    @Override
    public ViewHolderSong onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_song, viewGroup, false);
        return new ViewHolderSong(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSong viewHolderSong, final int position) {
        ItemSong itemSong = mInterfaceSong.getItemSong(position);
        viewHolderSong.mTextNameSong.setText(itemSong.getNameSong());
        viewHolderSong.mTextNameSinger.setText(itemSong.getNameSinger());
        viewHolderSong.mTextNumber.setText((position+1) + Constants.EMPTY);
        viewHolderSong.mLinearSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterfaceSong.onClick(position);
            }
        });
        if (position == mPositionClick){
            viewHolderSong.mImageSong.setVisibility(View.VISIBLE);
            viewHolderSong.mImageSong.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate));
            viewHolderSong.mTextNumber.setVisibility(View.INVISIBLE);
        }
        else {
            viewHolderSong.mImageSong.setVisibility(View.INVISIBLE);
            viewHolderSong.mTextNumber.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mInterfaceSong.getCount();
    }

    public class ViewHolderSong extends RecyclerView.ViewHolder{

        private TextView mTextNameSong;
        private TextView mTextNameSinger;
        private TextView mTextNumber;
        private ImageView mImageSong;
        private LinearLayout mLinearSong;

        public ViewHolderSong(@NonNull View itemView) {
            super(itemView);
            mTextNameSong = itemView.findViewById(R.id.text_name_song);
            mTextNameSinger = itemView.findViewById(R.id.text_name_singer);
            mTextNumber = itemView.findViewById(R.id.text_number);
            mImageSong = itemView.findViewById(R.id.image_song);
            mLinearSong = itemView.findViewById(R.id.linear_song);
        }
    }

    public interface InterfaceSong{
        int getCount();
        ItemSong getItemSong(int position);
        void onClick(int position);
    }

    public void setCurrentPosition(int position){
        mPositionClick = position;
    }

}
