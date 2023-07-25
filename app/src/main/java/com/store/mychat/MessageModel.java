package com.store.mychat;

public class MessageModel {
    private String messageId;
    private String senderId ,receiverId;
    private String message;
    private long timestamp;
    private boolean isSeen;
//    private boolean isSeen;

    public MessageModel() {
        // Default constructor required for Firebase
    }

//    public MessageModel(boolean isSeen) {
//        this.isSeen = isSeen;
//    }



    public MessageModel(String messageId, String senderId, String message, long timestamp , String receiverId ) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.receiverId = receiverId;
//        this.isSeen = isSeen;
    }

//    public MessageModel(String messageId, String senderId, String message, long timestamp , String receiverId , boolean isSeen) {
//        this.messageId = messageId;
//        this.senderId = senderId;
//        this.message = message;
//        this.timestamp = timestamp;
//        this.receiverId = receiverId;
//        this.isSeen = isSeen;
//    }

    // Getters and setters for the fields

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

//    public boolean isSeen() {
//        return isSeen;
//    }
//
//    public void setSeen(boolean seen) {
//        isSeen = seen;
//    }
}
