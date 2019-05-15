package com.example.wordchen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.wordchen.flower.R;

/**
 * Created by WORDCHEN on 2018/1/25.
 */

public class TouristMainActivity extends Activity {
    private Button button_nfc,button_erweima,button_person,button_setting;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tourist_main);
        button_nfc=findViewById(R.id.nfc_read);
        button_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nfcIntent = new Intent(TouristMainActivity.this, NFCReaderActivity.class);
                startActivity(nfcIntent);

            }
        });
        button_erweima=findViewById(R.id.erweima_read);
        button_erweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent erweimaIntent = new Intent(TouristMainActivity.this, ErweimaActivity.class);
                startActivity(erweimaIntent);

            }
        });
        button_person=findViewById(R.id.person);
        button_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent erweimaIntent = new Intent(TouristMainActivity.this, TouristActivity.class);
                startActivity(erweimaIntent);

            }
        });
//        button_setting=findViewById(R.id.erweima_read);
//        button_setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent erweimaIntent = new Intent(TouristMainActivity.this, ErweimaActivity.class);
//                startActivity(erweimaIntent);
//
//            }
//        });
    }
}

