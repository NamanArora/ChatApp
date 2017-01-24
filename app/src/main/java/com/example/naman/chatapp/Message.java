package com.example.naman.chatapp;

/**
 * Created by Naman on 21-01-2017.
 */

public class Message {

    public String sender;
    public String message;

    Message()
    {

    }
    Message(String sender, String message)
    {
        this.sender = sender;
        this.message = message;
    }
}
