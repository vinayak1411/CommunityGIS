package com.vinayakgaonkar.communitygis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private static final String URL_Profile = "https://pratiksrane27.000webhostapp.com/Android_Data/profileInsert.php";
    EditText etname,etphno,etaadhar,etemail;
    RadioGroup radioGroup;
    RadioButton  gender ;
    Button Save;
    ImageView displaypic;
    private String Gender_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(Profile.this);
        displaypic = (ImageView)findViewById(R.id.Profile_pic);
        etname =(EditText)findViewById(R.id.Name);
        etphno = (EditText)findViewById(R.id.Contact);
        etaadhar = (EditText)findViewById(R.id.AdharNo);

        if( TextUtils.isEmpty(etphno.getText())&& etphno.getTextSize()<10){
            etphno.setError( "Please Provide a Valid Contact Number" );
        }

        //code to verify wether user has entered valid aadhar card number
        if( TextUtils.isEmpty(etaadhar.getText())&& etaadhar.getTextSize()<12){
            etaadhar.setError( "Please Provide a Valid Aadhar Number" );
        }else {
            String aadharnum = etaadhar.getText().toString();
            Boolean checkaadhar = ValidateAadhar.validateVerhoeff(aadharnum);
            if (checkaadhar==false){
                etaadhar.setError("please enter valid aadhar card number");
            }
        }


        etemail = (EditText)findViewById(R.id.Email);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        Glide.with(Profile.this)
                .load(user.getPhotoUrl())
                .into(displaypic);
        etname.setText(user.getDisplayName());
        etemail.setText(user.getEmail());
        Save = (Button)findViewById(R.id.Save_btn);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertdata();
            }
        });

    }//oncreate

    public void rbclick(View view){
        int selectedId=radioGroup.getCheckedRadioButtonId();
        gender=(RadioButton)findViewById(selectedId);
        Gender_str=gender.getText().toString();
    }

    private void insertdata(){

        final String gender_str,aadhar,phno;
        FirebaseUser user = mAuth.getCurrentUser();
        final String name =user.getDisplayName().toString();
        final String email =user.getEmail().toString();
        gender_str =Gender_str;
        aadhar = etaadhar.getText().toString();
        phno = etphno.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URL_Profile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            boolean success = jsonObject.getBoolean("success");

                            if(success){
                                Toast.makeText(Profile.this,"Profile Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(Profile.this,"error occured while saving data", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<>();
                params = new HashMap<>();
                params.put("name",name);
                params.put("email",email);
                params.put("phno",phno);
                params.put("aadhar",aadhar);
                params.put("gender",gender_str);

                return params;
            }
        };


        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feedback) {
            // Handle the camera action
            Intent feedback = new Intent(Profile.this,Userform.class);
            startActivity(feedback);

        } else if (id == R.id.nav_home) {
            Intent home = new Intent(Profile.this,WebviewActivity.class);
            startActivity(home);

        }else if (id ==R.id.nav_userfeedback){
            Intent userfeedback = new Intent(Profile.this,UserFeedback.class);
            startActivity(userfeedback);
        }

        else if (id == R.id.nav_use) {
            Intent use = new Intent(Profile.this,Howtouse.class);
            startActivity(use);

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent main = new Intent(Profile.this,GoogleSignIn.class);
            startActivity(main);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
