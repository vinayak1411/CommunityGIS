package com.vinayakgaonkar.communitygis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private static final String URL_Profile = "https://pratiksrane27.000webhostapp.com/Android_Data/profileInsert.php";
    EditText etname,etphno,etaadhar,etemail;
    RadioGroup radioGroup;
    RadioButton gender,male,female,trans ;
    Button Save;
    ImageView displaypic;
    Boolean sharedprefboolean= false;
    ImageView profilepic;
    TextView uemail;
    TextView uname;
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
        male = (RadioButton)findViewById(R.id.Radio_male);
        female = (RadioButton)findViewById(R.id.Radio_female);
        trans = (RadioButton)findViewById(R.id.Radio_Trans);
        displaypic = (ImageView)findViewById(R.id.Profile_pic);
        etname =(EditText)findViewById(R.id.Name);
        etname.setEnabled(false);
        etphno = (EditText)findViewById(R.id.Contact);
        etaadhar = (EditText)findViewById(R.id.AdharNo);
        etemail = (EditText)findViewById(R.id.Email);
        etemail.setEnabled(false);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        Glide.with(Profile.this)
                .load(user.getPhotoUrl())
                .into(displaypic);
        etname.setText(user.getDisplayName());
        etemail.setText(user.getEmail());
        Save = (Button)findViewById(R.id.Save_btn);
        loadpref();

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( TextUtils.isEmpty(etphno.getText())){
                    etphno.setError( "Empty" );

                } else if(etphno.getTextSize()<10){
                    etphno.setError( "Invalid");

                }

                //code to verify wether user has entered valid aadhar card number
                if( TextUtils.isEmpty(etaadhar.getText())&& etaadhar.getTextSize()<12){
                    etaadhar.setText( "12 digit req" );

                }else {
                    String aadharnum = etaadhar.getText().toString();
                    Boolean checkaadhar = ValidateAadhar.validateVerhoeff(aadharnum);
                    if (checkaadhar==false){
                        etaadhar.setText( "Invalid " );
                    }
                }
                progressDialog.setMessage("Saving Your Data...");
                progressDialog.show();
                insertdata();

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        profilepic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_imageview);
        uname = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        uemail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_useremail);

        loaduserinfo();
    }//oncreate



    public void loaduserinfo(){

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            uname.setText(user.getDisplayName());
            uemail.setText(user.getEmail());


            Glide.with(Profile.this)
                    .load(user.getPhotoUrl())
                    .apply(new RequestOptions()
                            .override(100, 100)
                            .centerCrop())
                    .into(profilepic);

        }
    }

    public void rbclick(View view){
        int selectedId=radioGroup.getCheckedRadioButtonId();
        gender=(RadioButton)findViewById(selectedId);
        Gender_str=gender.getText().toString();
    }

    public void savepref(String contact,String aadharnum,String gender_pref,Boolean spbool) {
        SharedPreferences sharedPref = getSharedPreferences("profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("contactno", contact);
        editor.putString("aadharnum", aadharnum);
        editor.putString("gender_sp", gender_pref);
        editor.putBoolean("checkexist", spbool);
        editor.apply();
    }

    public void loadpref(){
        SharedPreferences sharedPref = getSharedPreferences("profile", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("checkexist",false)==true) {
            etphno.setText(sharedPref.getString("contactno", ""));
            etaadhar.setText(sharedPref.getString("aadharnum", ""));
            String selectedgender = sharedPref.getString("gender_sp","");
            try {
                if (Objects.equals(selectedgender, "Male")) {
                    male.setChecked(true);
                } else if (Objects.equals(selectedgender, "Female")) {
                    female.setChecked(true);
                } else if (Objects.equals(selectedgender, "Transgender")) {
                    trans.setChecked(true);
                }
            }catch (Exception excp){
                Toast.makeText(Profile.this,"Inside Loadpref:"+excp.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }
    private void insertdata(){

        final String gender_str,aadhar,phno;
        FirebaseUser user = mAuth.getCurrentUser();
        final String name =user.getDisplayName().toString();
        final String email =user.getEmail().toString();
        gender_str =Gender_str;
        sharedprefboolean = true;
        phno = etphno.getText().toString();
        aadhar = etaadhar.getText().toString();
        savepref(phno,aadhar,Gender_str,sharedprefboolean);

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
                                progressDialog.hide();
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
                        Toast.makeText(Profile.this,"please select Gender again", Toast.LENGTH_LONG).show();
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

    }//insertdata

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

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent home = new Intent(Profile.this,WebviewActivity.class);
            startActivity(home);

        } else if (id == R.id.nav_feedback) {
            Intent feedback = new Intent(Profile.this,Userform.class);
            startActivity(feedback);
            finish();

        } else if (id == R.id.nav_userfeedback) {
            Intent your = new Intent(Profile.this,UserFeedback.class);
            startActivity(your);
            finish();

        } else if (id == R.id.nav_use) {
            Intent use = new Intent(Profile.this,Howtouse.class);
            startActivity(use);
            finish();

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent logout = new Intent(Profile.this,GoogleSignIn.class);
            startActivity(logout);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
