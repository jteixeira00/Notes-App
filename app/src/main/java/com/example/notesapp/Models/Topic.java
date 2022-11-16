package com.example.notesapp.Models;

public class Topic {
    public String topicName;
    public int qos;
    public Topic(String name, int qos){
        this.topicName = name;
        this.qos = qos;
    }
}
