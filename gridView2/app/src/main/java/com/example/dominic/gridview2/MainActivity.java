package com.example.dominic.gridview2;

import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    // boilerplate code to imitate home screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toWardrobe(View view)
    {
        Intent intent = new Intent(this, WardrobeActivity.class);
        startActivity(intent);
    }
}