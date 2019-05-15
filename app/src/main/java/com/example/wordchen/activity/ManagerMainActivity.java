package com.example.wordchen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import com.example.wordchen.flower.R;

/**
 * Created by WORDCHEN on 2018/3/4.
 */

public class ManagerMainActivity extends Activity implements View.OnClickListener {
    Button button_display,button_pie,button_manage,button_manager_person,registercard;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_main);
        init();
    }
    private void init() {
        registercard=findViewById(R.id.registercard);
        button_manage=findViewById(R.id.aa);
        button_display=findViewById(R.id.bb);
        button_pie=findViewById(R.id.cc);
        button_manager_person=findViewById(R.id.dd);
        button_manage.setOnClickListener(this);
        button_display.setOnClickListener(this);
        button_pie.setOnClickListener(this);
        button_manager_person.setOnClickListener(this);
        registercard.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registercard:
                Intent it_registerCard=new Intent(ManagerMainActivity.this,WriteTag.class);
                startActivity(it_registerCard);
                break;
            case R.id.aa:
                Intent it_manage = new Intent(ManagerMainActivity.this,AddActivity.class);
                startActivity(it_manage);
                break;
            case R.id.bb:
                Intent it_display = new Intent(ManagerMainActivity.this,DisplayActivity.class);
                startActivity(it_display);
                break;
            case R.id.cc:
                Intent it_pie = new Intent(ManagerMainActivity.this,CountActivity.class);
                startActivity(it_pie);
                break;
            case R.id.dd:
                Intent it_person = new Intent(ManagerMainActivity.this,ManagerActivity.class);
                startActivity(it_person);
                break;
        }
    }
}
