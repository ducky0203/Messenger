package com.example.chatfirebase.Model;

public class Request {
    private String name, status, img_avatar;

    public Request() {
    }

    public Request(String name, String status, String img_avatar) {
        this.name = name;
        this.status = status;
        this.img_avatar = img_avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImg_avatar() {
        return img_avatar;
    }

    public void setImg_avatar(String img_avatar) {
        this.img_avatar = img_avatar;
    }
}
