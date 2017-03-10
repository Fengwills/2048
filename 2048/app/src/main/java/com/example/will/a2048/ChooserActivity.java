package com.example.will.a2048;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.will.a2048.R;

public class ChooserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        MainActivity.applyFont(this, findViewById(R.id.chooser_container), "font/Comfortaa-Bold.ttf");
        findViewById(R.id.three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.setLines(3);
                Intent intent = new Intent();
                intent.setClass(ChooserActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
        findViewById(R.id.four).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.setLines(4);
                Intent intent = new Intent();
                intent.setClass(ChooserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.five).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.setLines(5);
                Intent intent = new Intent();
                intent.setClass(ChooserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
