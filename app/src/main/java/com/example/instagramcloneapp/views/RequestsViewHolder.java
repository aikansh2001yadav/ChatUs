package com.example.instagramcloneapp.views;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramcloneapp.R;

public class RequestsViewHolder extends RecyclerView.ViewHolder{
    /**
     * Stores a reference of requestTextView
     */
    private TextView requestTextView;
    /**
     * Stores a reference of allowRequestButton used to allow request
     */
    private Button allowRequestButton;
    /**
     * Stores a reference of denyRequestButton used to deny request
     */
    private Button denyRequestButton;

    /**
     * Constructor initialises private fields
     * @param itemView
     */
    public RequestsViewHolder(@NonNull View itemView) {
        super(itemView);
        requestTextView = itemView.findViewById(R.id.requestTextView);
        allowRequestButton = itemView.findViewById(R.id.allowRequestButton);
        denyRequestButton = itemView.findViewById(R.id.denyRequestButton);
    }

    public TextView getRequestTextView(){
        return requestTextView;
    }

    public Button getAllowRequestButton(){
        return allowRequestButton;
    }

    public Button getDenyRequestButton(){
        return denyRequestButton;
    }
}
