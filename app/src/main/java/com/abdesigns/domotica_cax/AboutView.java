package com.abdesigns.domotica_cax;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class AboutView extends AppCompatActivity {
    TextView infoTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        infoTextView = (TextView)findViewById(R.id.infoTextView);

        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mailto = "mailto:racifuina@me.com";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Soporte domótica CAX");
                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {

                }
            }
        });
        BetterLinkMovementMethod.linkify(Linkify.ALL, infoTextView);
    }
}
