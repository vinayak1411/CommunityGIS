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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Howtouse extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth mAuth;
    ImageView profilepic;
    TextView uemail;
    TextView uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howtouse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("How To Use");
        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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


            Glide.with(Howtouse.this)
                    .load(user.getPhotoUrl())
                    .apply(new RequestOptions()
                            .override(100, 100)
                            .centerCrop())
                    .into(profilepic);

        }
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

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent home = new Intent(Howtouse.this,WebviewActivity.class);
            startActivity(home);
            finish();
        } else if (id == R.id.nav_feedback) {

            Intent feedback = new Intent(Howtouse.this,Userform.class);
            startActivity(feedback);
            finish();

        } else if (id ==R.id.nav_userfeedback){
            Intent userfeedback = new Intent(Howtouse.this,UserFeedback.class);
            startActivity(userfeedback);
            finish();
        }

        else if (id == R.id.nav_profile) {

            Intent profile = new Intent(Howtouse.this,Profile.class);
            startActivity(profile);
            finish();

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent main = new Intent(Howtouse.this,GoogleSignIn.class);
            startActivity(main);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
