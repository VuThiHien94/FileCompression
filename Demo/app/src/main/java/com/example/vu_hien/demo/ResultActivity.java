package com.example.vu_hien.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity {
        TextView result;
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_result);

            Intent intent = getIntent();
            String message = intent.getStringExtra("message");
            result=(TextView) findViewById(R.id.result);
            result.setTextSize(20);
            result.setText(message);
            Button btnBack= (Button) findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }
}
