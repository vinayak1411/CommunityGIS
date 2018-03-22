package com.vinayakgaonkar.communitygis;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by vinayak on 16-03-2018.
 */

public class FeedbackAdapter extends  RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {


    private Context mCtx;
    private List<Feedback> FeedbackList;

    public FeedbackAdapter(Context mCtx, List<Feedback> feedbackList) {
        this.mCtx = mCtx;
        FeedbackList = feedbackList;
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout,null);//null because no view group parent
        FeedbackViewHolder holder = new FeedbackViewHolder(view);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedbackViewHolder holder, int position) {
        Feedback feedback = FeedbackList.get(position);
        holder.address.setText(feedback.getAddress());
        holder.amenity_type.setText(feedback.getAmenity_type());
        holder.amenity_category.setText(feedback.getAmenity_category());
        holder.comment.setText(feedback.getComment());
        holder.ratings.setText(String.valueOf(feedback.getRating()));
        Glide.with(mCtx)
                .load(feedback.getImage_url())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return FeedbackList.size();
    }

    class FeedbackViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView address,amenity_type,amenity_category,ratings,comment;


        public FeedbackViewHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.List_Address);
            amenity_type = itemView.findViewById(R.id.TV_Amenity_Type);
            amenity_category = itemView.findViewById(R.id.TV_Amenity_Category);
            ratings = itemView.findViewById(R.id.TV_Ratings);
            comment = itemView.findViewById(R.id.TV_Comment);
            imageView = itemView.findViewById(R.id.List_imageView);
        }
    }
}
