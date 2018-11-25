package com.example.noobkenneth.cody;


import android.graphics.Bitmap;

public class Outfit {
    private Bitmap image;
    private String textthing;

    Outfit(){
    }

    Outfit(Bitmap image, String text){
        this.image = image;
        this.textthing = text;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


    public String getTextthing() {
        return textthing;
    }

    public void setTextthing(String textthing) {
        this.textthing = textthing;
    }
}

