package com.example.instagramcloneapp.model;

public class ChatStatus {
    /**
     * Stores name of the sender
     */
    private String sender;
    /**
     * Stores whether you allow or deny its request to chat
     */
    private boolean chatStatus;

    /**
     * Constructor initialises sender name and chatStatus
     * @param sender
     * @param chatStatus
     */
    public ChatStatus(String sender, boolean chatStatus){
        this.sender = sender;
        this.chatStatus = chatStatus;
    }

    /**
     * @return name of sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @return chatStatus of sender
     */
    public boolean isChatStatus() {
        return chatStatus;
    }
}
