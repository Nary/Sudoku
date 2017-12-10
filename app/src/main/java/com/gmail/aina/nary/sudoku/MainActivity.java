package com.gmail.aina.nary.sudoku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.gmail.aina.nary.sudoku.R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAdView = (AdView) findViewById(com.gmail.aina.nary.sudoku.R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
    }

    public void startSession(View view) {
        Intent intent = new Intent(this, Activity_Sudoku.class);
        String difficulty = (String) view.getTag();
        intent.putExtra("Difficulty", difficulty);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        overridePendingTransition(com.gmail.aina.nary.sudoku.R.anim.fade_in, com.gmail.aina.nary.sudoku.R.anim.fade_out);
        //overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }
}
