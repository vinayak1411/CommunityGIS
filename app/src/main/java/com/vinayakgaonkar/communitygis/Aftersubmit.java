package com.vinayakgaonkar.communitygis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Aftersubmit extends AppCompatActivity {

    Button map , form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aftersubmit);
        setTitle("Thank You!");
        map = (Button)findViewById(R.id.BacktoMap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(Aftersubmit.this,WebviewActivity.class);
                startActivity(map);
                finish();
            }
        });



        form = ( Button)findViewById(R.id.AnotherReview);
        form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent form = new Intent(Aftersubmit.this,Userform.class);
                startActivity(form);
                finish();
            }
        });
    }//oncreate
}
