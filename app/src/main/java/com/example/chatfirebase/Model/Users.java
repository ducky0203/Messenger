package com.example.chatfirebase.Model;

public class Users {
    private String name;
    private String img_avatar;
    private String img_thumnail;
    private String status;

    public Users() {
    }

    public Users(String name, String img_avatar, String img_thumnail, String status) {
        this.name = name;
        this.img_avatar = img_avatar;
        this.img_thumnail = img_thumnail;
        this.status = status;
    }

    public String getImg_thumnail() {
        return img_thumnail;
    }

    public void setImg_thumnail(String img_thumnail) {
        this.img_thumnail = img_thumnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_avatar() {
        return img_avatar;
    }

    public void setImg_avatar(String img_avatar) {
        this.img_avatar = img_avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
