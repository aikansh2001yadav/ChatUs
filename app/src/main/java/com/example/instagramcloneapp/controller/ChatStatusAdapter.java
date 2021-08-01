package com.example.instagramcloneapp.controller;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramcloneapp.R;
import com.example.instagramcloneapp.model.ChatStatus;
import com.example.instagramcloneapp.views.RequestsViewHolder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatStatusAdapter extends RecyclerView.Adapter<RequestsViewHolder> {

    /**
     * Stores context of RequestActivity
     */
    private Context context;
    /**
     * Stores an arraylist of chat status of each user request
     */
    private ArrayList<ChatStatus> chatStatusArrayList;

    /**
     * Constructor initialises member fields
     * @param context
     * @param chatStatusArrayList
     */
    public ChatStatusAdapter(Context context, ArrayList<ChatStatus> chatStatusArrayList){
        this.context = context;
        this.chatStatusArrayList = chatStatusArrayList;
    }
    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_layout, parent, false);
        return new RequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsViewHolder holder, int position) {
        //setting sender name as text in requestTextView
        holder.getRequestTextView().setText(chatStatusArrayList.get(holder.getAdapterPosition()).getSender() + " wants to talk to you");
        //set text and background color accordingly on the basis of chatStatus
        if(chatStatusArrayList.get(holder.getAdapterPosition()).isChatStatus()){
            holder.getAllowRequestButton().setText("ALLOWED");
            holder.getAllowRequestButton().setBackgroundColor(GREEN);
        }else{
            holder.getDenyRequestButton().setText("DENIED");
            holder.getDenyRequestButton().setBackgroundColor(RED);
        }
        //Setting on click listener on allowButton
        holder.getAllowRequestButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting chat status as true in the parse server for the ChatStatus parseObject relating to appropriate sender
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatStatus");
                query.whereEqualTo("Sender", chatStatusArrayList.get(holder.getAdapterPosition()).getSender());
                query.whereEqualTo("Recipient", ParseUser.getCurrentUser().getUsername());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e == null){
                            for(ParseObject parseObject:objects){
                                parseObject.put("ChatStatus", true);
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            Toast.makeText(context, chatStatusArrayList.get(holder.getAdapterPosition()).getSender() + " is allowed to chat", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(context, "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }else{
                            Toast.makeText(context, "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //Changing text and colors of allowButton
                holder.getAllowRequestButton().setText("ALLOWED");
                holder.getAllowRequestButton().setBackgroundColor(GREEN);
                holder.getDenyRequestButton().setText("DENY");
                holder.getDenyRequestButton().setBackgroundColor(Color.rgb(170, 46, 230));
            }
        });

        //Setting on click listener on denyButton
        holder.getDenyRequestButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting chat status as false in the parse server for the ChatStatus parseObject relating to appropriate sender
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatStatus");
                query.whereEqualTo("Sender", chatStatusArrayList.get(holder.getAdapterPosition()).getSender());
                query.whereEqualTo("Recipient", ParseUser.getCurrentUser().getUsername());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e == null){
                            for(ParseObject parseObject:objects){
                                parseObject.put("ChatStatus", false);
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            Toast.makeText(context, chatStatusArrayList.get(holder.getAdapterPosition()).getSender() + " is denied to chat now." , Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(context, "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }else{
                            Toast.makeText(context, "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //Changing text and color of denyButton
                holder.getDenyRequestButton().setText("DENIED");
                holder.getDenyRequestButton().setBackgroundColor(RED);
                holder.getAllowRequestButton().setText("ALLOW");
                holder.getAllowRequestButton().setBackgroundColor(Color.rgb(170, 46, 230));
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatStatusArrayList.size();
    }
}
