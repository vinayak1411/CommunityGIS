package com.vinayakgaonkar.communitygis;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);
        setTitle("Your Feedbacks");
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            feedbackList = new ArrayList<>();
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadFeedback();


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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

                            for (int i = 0; i < array.length(); i++) {
                                try{
                                //getting product object from json array

                                JSONObject feedbackObject = array.getJSONObject(i);
                                int id = feedbackObject.getInt("id");
                                String address = feedbackObject.getString("address");
                                String amenity_type = feedbackObject.getString("amenity_type");
                                String amenity_category = feedbackObject.getString("amenity_category");
                                String amenity_comment = feedbackObject.getString("amenity_comment");
                                String ratings = feedbackObject.getString("ratings");
                                String image_url = feedbackObject.getString("image_url");
                                Feedback feedback = new Feedback(id, address, amenity_type,amenity_category, amenity_comment, image_url, ratings);

                                feedbackList.add(feedback);


                                     adapter = new FeedbackAdapter(UserFeedback.this, feedbackList);
                                    recyclerView.setAdapter(adapter);
                                }catch(Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
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

        if (id == R.id.nav_profile) {
            Intent profile = new Intent(UserFeedback.this,Profile.class);
            startActivity(profile);
        } else if (id == R.id.nav_feedback) {
            Intent form = new Intent(UserFeedback.this,Userform.class);
            startActivity(form);

        } else if (id == R.id.nav_use) {
            Intent use = new Intent(UserFeedback.this,Howtouse.class);
            startActivity(use);

        } else if (id == R.id.nav_home) {
            Intent home = new Intent(UserFeedback.this,WebviewActivity.class);
            startActivity(home);

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent main = new Intent(UserFeedback.this,GoogleSignIn.class);
            startActivity(main);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
