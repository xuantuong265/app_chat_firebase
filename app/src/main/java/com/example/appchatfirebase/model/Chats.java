package com.example.appchatfirebase.model;

public class Chats {

    private String sender, receiver;

    public Chats(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public Chats() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
