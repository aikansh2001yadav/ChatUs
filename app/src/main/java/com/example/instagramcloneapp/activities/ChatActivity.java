package com.example.instagramcloneapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramcloneapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;



public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Storing receiver name of current chat
     */
    private String receiverName;
    /**
     * Stores a reference of chatEditText used to type message
     */
    private TextInputEditText chatEditText;
    /**
     * List adapter to be set as an adapter to chatListView
     */
    private ArrayAdapter<String> listAdapter;
    /**
     * Stores a reference of chat list view
     */
    private ListView chatListView;
    /**
     * Stores chats between the current parse user and receiver
     */
    private ArrayList<String> chatsList;

    /**
     * Stores subscriptionHandling which is used to handle events on live queries
     */
    private SubscriptionHandling<ParseObject> subscriptionHandling;

    /**
     * Stores a reference of progressBar used to show progress
     */
    private ProgressBar chatProgressBar;

    /**
     * Stores object ids of each message from parse server
     */
    private ArrayList<String> objectIdsMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setTitle("Chat");
        //Initialising views and initialising listAdapter and chatsList
        receiverName = getIntent().getStringExtra("Receiver");
        chatEditText = findViewById(R.id.chatEditText);
        chatListView = findViewById(R.id.chatListView);
        chatProgressBar = findViewById(R.id.chatProgressBar);

        chatsList = new ArrayList<>();
        objectIdsMessage = new ArrayList<>();


        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chatsList);
        chatListView.setAdapter(listAdapter);
        //Setting on item long click listener
        chatListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Creating alertDialog builder
                new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("Delete Message")
                        .setMessage("Do you want to delete current message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Deleting the selected message from parse server
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Chat");
                                query.whereEqualTo("Sender", ParseUser.getCurrentUser().getUsername());
                                query.whereEqualTo("objectId", objectIdsMessage.get(position));
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if(e == null){
                                            if(!objects.isEmpty()){
                                                for(ParseObject parseObject:objects){
                                                    try {
                                                        chatProgressBar.setVisibility(View.VISIBLE);
                                                        parseObject.delete();
                                                        Toast.makeText(ChatActivity.this, "Message deleted", Toast.LENGTH_SHORT).show();
                                                    } catch (ParseException parseException) {
                                                        parseException.printStackTrace();
                                                    }
                                                }
                                            }else{
                                                Toast.makeText(ChatActivity.this, "Can't delete", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            showAlert("Error", "Something went wrong");
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
        findViewById(R.id.sendButton).setOnClickListener(this);

        // Back4App's Parse setup
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server("wss://instagramcloneapp.b4a.io/").build()
        );
        // Init Live Query Client
        ParseLiveQueryClient parseLiveQueryClient = null;

        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://instagramcloneapp.b4a.io/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Message - Live Query
        if (parseLiveQueryClient != null) {
            ParseQuery<ParseObject> subQuery1 = ParseQuery.getQuery("Chat");
            subQuery1.whereEqualTo("Sender", ParseUser.getCurrentUser().getUsername());
            subQuery1.whereEqualTo("Receiver", receiverName);

            ParseQuery<ParseObject> subQuery2 = ParseQuery.getQuery("Chat");
            subQuery2.whereEqualTo("Sender", receiverName);
            subQuery2.whereEqualTo("Receiver", ParseUser.getCurrentUser().getUsername());

            ArrayList<ParseQuery<ParseObject>> queryArrayList = new ArrayList<>();
            queryArrayList.add(subQuery1);
            queryArrayList.add(subQuery2);

            ParseQuery<ParseObject> parseQuery = ParseQuery.or(queryArrayList);
            parseQuery.orderByAscending("createdAt");
            subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

            //Setting on handle subscriber on subscriptionHandling
            subscriptionHandling.handleSubscribe(new SubscriptionHandling.HandleSubscribeCallback<ParseObject>() {
                @Override
                public void onSubscribe(ParseQuery<ParseObject> query) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Getting various messages from parse server
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e == null){
                                        chatsList.clear();
                                        for(ParseObject parseObject:objects){
                                            objectIdsMessage.add(parseObject.getObjectId());
                                            chatsList.add(parseObject.getString("Sender") + ": " + parseObject.getString("Message"));
                                        }
                                        listAdapter.notifyDataSetChanged();
                                        chatProgressBar.setVisibility(View.GONE);
                                    }else{
                                        showAlert("Error", "Something went wrong");
                                    }
                                }
                            });
                        }
                    });
                }
            });

            //Setting on handle event on subscriptionHandling
            subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
                @Override
                public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                    //Updating listView
                    if(event == SubscriptionHandling.Event.CREATE){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                objectIdsMessage.add(object.getObjectId());
                                chatsList.add(object.getString("Sender") + ": " + object.getString("Message"));
                                listAdapter.notifyDataSetChanged();
                                chatProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                    if(event == SubscriptionHandling.Event.DELETE){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                int position = objectIdsMessage.indexOf(object.getObjectId());
                                objectIdsMessage.remove(position);
                                chatsList.remove(position);
                                listAdapter.notifyDataSetChanged();
                                chatProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        chatProgressBar.setVisibility(View.VISIBLE);
        //Storing message in the parse server
        String message = chatEditText.getText().toString();
        chatEditText.setText("");
        closeKeyboard();
        ParseObject chat = new ParseObject("Chat");
        chat.put("Sender", ParseUser.getCurrentUser().getUsername());
        chat.put("Receiver", receiverName);
        chat.put("Message", message);
        chat.saveInBackground();
    }
    /**
     * Showing alert
     * @param title
     * @param message
     */
    private void showAlert(String title, String message) {
        new AlertDialog.Builder(ChatActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Closing keyboard
     */
    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
}