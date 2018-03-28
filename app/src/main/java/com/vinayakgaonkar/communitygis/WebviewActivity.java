package com.vinayakgaonkar.communitygis;

import android.app.AlertDialog;
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
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WebviewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    WebView webView;
    FirebaseAuth mAuth;
    ImageView profilepic;
    TextView uemail;
    TextView uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Map");
        mAuth = FirebaseAuth.getInstance();


        //webview
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String  url = "http://try.cartoview.net/apps/cartoview_feature_list/491/view/";
        webView.loadUrl(url);



        haveNetworkConnection();
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

    public void loaduserinfo(){

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            uname.setText(user.getDisplayName());
            uemail.setText(user.getEmail());


          Glide.with(WebviewActivity.this)
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

        if (id == R.id.nav_feedback) {
            // Handle the camera action
            Intent feedback = new Intent(WebviewActivity.this,Userform.class);
            startActivity(feedback);

        } else if (id == R.id.nav_userfeedback) {

            Intent howtouse = new Intent(WebviewActivity.this,UserFeedback.class);
            startActivity(howtouse);

        } else if (id ==R.id.nav_profile){
            Intent userfeedback = new Intent(WebviewActivity.this,Profile.class);
            startActivity(userfeedback);

        } else if (id == R.id.nav_use) {

            Intent profile = new Intent(WebviewActivity.this,Howtouse.class);
            startActivity(profile);


        }
        else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent main = new Intent(WebviewActivity.this,GoogleSignIn.class);
            startActivity(main);
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
