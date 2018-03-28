package com.vinayakgaonkar.communitygis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;

public class UserFeedback extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String URL = "https://pratiksrane27.000webhostapp.com/Android_Data/newfetchdata.php";
    RecyclerView recyclerView;
    FeedbackAdapter adapter;
    List<Feedback> feedbackList;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    ImageView profilepic;
    TextView uemail;
    TextView uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);
        setTitle("Your Feedbacks");
        mAuth = FirebaseAuth.getInstance();
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            progressDialog = new ProgressDialog(UserFeedback.this);
            haveNetworkConnection();
            feedbackList = new ArrayList<>();
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadFeedback();
            progressDialog.setMessage("Loading Your Feebacks Please wait...");
            progressDialog.show();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();


        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        profilepic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_profilepic);
        uname = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        uemail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_useremail);

        loaduserinfo();


    }//oncreate


    public void loaduserinfo(){

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            uname.setText(user.getDisplayName());
            uemail.setText(user.getEmail());


            Glide.with(UserFeedback.this)
                    .load(user.getPhotoUrl())
                    .apply(new RequestOptions()
                            .override(100, 100)
                            .centerCrop())
                    .into(profilepic);
        }
    }


    private void loadFeedback(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            //traversing through all the object
                           if(array.length()!=0) {
                               progressDialog.hide();
                                for (int i = 0; i < array.length(); i++) {
                                    try {
                                        //getting product object from json array

                                        JSONObject feedbackObject = array.getJSONObject(i);
                                        int id = feedbackObject.getInt("id");
                                        String address = feedbackObject.getString("address");
                                        String amenity_type = feedbackObject.getString("amenity_type");
                                        String amenity_category = feedbackObject.getString("amenity_category");
                                        String amenity_comment = feedbackObject.getString("amenity_comment");
                                        String ratings = feedbackObject.getString("ratings");
                                        String image_url = feedbackObject.getString("image_url");
                                        Feedback feedback = new Feedback(id, address, amenity_type, amenity_category, amenity_comment, image_url, ratings);

                                        feedbackList.add(feedback);


                                        adapter = new FeedbackAdapter(UserFeedback.this, feedbackList);
                                        recyclerView.setAdapter(adapter);
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }


                            }
                            else{
                               progressDialog.hide();
                                Toast.makeText(UserFeedback.this,"You Have Not Submitted Any Feedback Yet ",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserFeedback.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }})
        {

            @Override
            protected Map<String, String> getParams()  {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                final String email =user.getEmail().toString();

                Map<String, String> params = new HashMap<>();
                params = new HashMap<>();
                params.put("email",email);
                return params;
            }
        };

        Volley.newRequestQueue(UserFeedback.this).add(stringRequest);
    }//loadFeedback
    public void haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        if (haveConnectedWifi == false && haveConnectedMobile == false){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Connect to Internet or quit")
                    .setCancelable(false)
                    .setPositiveButton("Connect to Internet", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        }
    }//hasnetworkconnection

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
            Intent home = new Intent(UserFeedback.this,WebviewActivity.class);
            startActivity(home);
            finish();
        } else if (id == R.id.nav_feedback) {
            Intent form = new Intent(UserFeedback.this,Userform.class);
            startActivity(form);
            finish();

        } else if (id == R.id.nav_profile) {
            Intent use = new Intent(UserFeedback.this,Profile.class);
            startActivity(use);
            finish();

        } else if (id == R.id.nav_use) {
            Intent home = new Intent(UserFeedback.this,Howtouse.class);
            startActivity(home);
            finish();

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent main = new Intent(UserFeedback.this,GoogleSignIn.class);
            startActivity(main);
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
