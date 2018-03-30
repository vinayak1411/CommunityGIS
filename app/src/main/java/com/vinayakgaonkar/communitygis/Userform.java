package com.vinayakgaonkar.communitygis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import java.util.Locale;
import java.util.Map;

public class Userform extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    Bitmap photo;
    Double latitude,longitude;
    EditText address,comment;
    Button getloc,submit,clk_photo;
    ImageView imageView;
    Spinner amenity_type_spinner,amenity_category_spinner;
    RatingBar ratingBar;
    TextView rating_text;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    ProgressDialog progressDialog;
    private static final String ROOT_URL = "https://pratiksrane27.000webhostapp.com/Android_Data/";
    public static final String URL_UserData = ROOT_URL+"InsertData.php";
    GPSTracker gps;
    List<Address> addressList;
    FirebaseAuth mAuth;
    ImageView profilepic;
    TextView uemail;
    TextView uname;
    Boolean cameraclick = false;
    Boolean getlocclick = false;
    Boolean comment_check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userform);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Give Feedback");
        haveNetworkConnection();

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Userform.this);

        clk_photo = (Button)findViewById(R.id.capture);
        clk_photo.setFocusable(true);

        getloc =(Button)findViewById(R.id.fetch_address);
        getloc.setFocusable(true);

        submit = (Button)findViewById(R.id.submit);
        address =(EditText)findViewById(R.id.address);
        address.setInputType(InputType.TYPE_NULL);
        comment = (EditText)findViewById(R.id.comment_value);

        if(TextUtils.isEmpty(comment.getText())){
            comment.setError("Field Required");
        }
        else{
            comment_check = true;
        }
        imageView = (ImageView) findViewById(R.id.imageview);
        amenity_category_spinner = (Spinner) findViewById(R.id.spinner4);
        amenity_type_spinner = (Spinner) findViewById(R.id.spinner3);

        try {
            //rating bar
            ListenerForRatingBar();
            //disable button if user has no camera

            if (!hascamera()) {
                clk_photo.setEnabled(false);
            }

            //spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.amenity_type, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            amenity_type_spinner.setAdapter(adapter);

            amenity_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    String Select_category = String.valueOf(amenity_type_spinner.getSelectedItem());

                    switch (Select_category) {

                        case "Road":
                            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(Userform.this, R.array.Category_Road, android.R.layout.simple_spinner_item);
                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            amenity_category_spinner.setAdapter(adapter2);
                            break;
                        case "School":
                            ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(Userform.this, R.array.Category_School, android.R.layout.simple_spinner_item);
                            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            amenity_category_spinner.setAdapter(adapter3);
                            break;
                        case "Hospital":
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Userform.this, R.array.Category_Hospital, android.R.layout.simple_spinner_item);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            amenity_category_spinner.setAdapter(adapter4);
                            break;
                        case "Public Toilets":
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Userform.this, R.array.Category_PublicToilets, android.R.layout.simple_spinner_item);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            amenity_category_spinner.setAdapter(adapter5);
                            break;
                        case "Play Ground":
                            ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(Userform.this, R.array.Category_PlayGround, android.R.layout.simple_spinner_item);
                            adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            amenity_category_spinner.setAdapter(adapter6);
                            break;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //navigation
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            //nagivartion code ends

            //location
            try {
                if (ActivityCompat.checkSelfPermission(this, mPermission)
                        != MockPackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{mPermission},
                            REQUEST_CODE_PERMISSION);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            getloc.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    getlocclick = true;

                    gps = new GPSTracker(Userform.this);

                    if (gps.canGetLocation()) {

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        try {
                            Geocoder geocoder = new Geocoder(Userform.this, Locale.getDefault());
                            if (geocoder.isPresent()) {
                                try {
                                    addressList = geocoder.getFromLocation(latitude, longitude, 1);
                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                }
                                if (addressList != null) {
                                    String geocodeaddress = addressList.get(0).getAddressLine(0);
                                    address.setText(geocodeaddress);
                                } else {
                                    address.setText("latitude:" + latitude + ",longitude:" + longitude);
                                }
                            }
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "geocoder IO exception ,Internet Connection Required ", Toast.LENGTH_LONG).show();
                        } catch (NullPointerException npegc) {
                            Toast.makeText(getApplicationContext(), "gecoder null pointer exception ", Toast.LENGTH_LONG).show();

                        }


                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                                + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
                    } else {

                        gps.showSettingsAlert();
                    }

                }
            });


            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cameraclick == true && getlocclick == true && comment_check == true) {
                        inserData();
                    }
                    else {
                        Toast.makeText(Userform.this,"All fields are required",Toast.LENGTH_LONG).show();
                        getloc.requestFocus();
                        getloc.setText("click me!");
                        clk_photo.requestFocus();
                        clk_photo.setText("click me!");
                        comment.setText("Add Your Comment");
                    }
                }
            });



        }
        catch (NullPointerException npe){
            Toast.makeText(getApplicationContext(),"null pointer exception occured",Toast.LENGTH_SHORT).show();
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


            Glide.with(Userform.this)
                    .load(user.getPhotoUrl())
                    .apply(new RequestOptions()
                            .override(100, 100)
                            .centerCrop())
                    .into(profilepic);

        }
    }


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

    //to insert data into database
    private void inserData(){
        FirebaseUser user = mAuth.getCurrentUser();
        final String email =user.getEmail().toString();

        //image
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        final String image_str = Base64.encodeToString(imageBytes,Base64.DEFAULT);

        final String address_str,latitude_str,longitude_str,amenity_type_str,amenity_category_str,comment_str,rating_str;
        address_str = address.getText().toString();
        latitude_str= String.valueOf(latitude);
        longitude_str= String.valueOf(longitude);
        amenity_type_str = String.valueOf(amenity_type_spinner.getSelectedItem());
        amenity_category_str = String.valueOf(amenity_category_spinner.getSelectedItem());
        comment_str = comment.getText().toString();
        rating_str = rating_text.getText().toString();

        progressDialog.setMessage("Uploading User Feedback...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URL_UserData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            boolean success = jsonObject.getBoolean("success");

                            if(success){
                                Toast.makeText(Userform.this,"Feedback Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                Intent after = new Intent(Userform.this,Aftersubmit.class);
                                startActivity(after);
                                finish();
                            }else{
                                Toast.makeText(Userform.this,"Feedback Already Exists : You can give Feedback About certain Amenity only once", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Userform.this,error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<>();
                params = new HashMap<>();
                params.put("email",email);
                params.put("latitude",latitude_str);
                params.put("longitude",longitude_str);
                params.put("address",address_str);
                params.put("amenity_type",amenity_type_str);
                params.put("amenity_category",amenity_category_str);
                params.put("amenity_comment",comment_str);
                params.put("imagestring",image_str);
                params.put("ratings",rating_str);
                return params;
            }
        };


        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }//insertdata

    //check if user has camera
    private boolean hascamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    //camera code
    public void launchcamera(View view) {
        cameraclick = true;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    //if you want to return image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //get the photo
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");
            imageView.setImageBitmap(photo);

        }
    }

    //rating bar
    public void ListenerForRatingBar() {
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        rating_text = (TextView) findViewById(R.id.rating_value);

        ratingBar.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        rating_text.setText(String.valueOf(v));
                    }
                }
        );

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
            Intent use = new Intent(Userform.this,WebviewActivity.class);
            startActivity(use);
            finish();

        } else if (id == R.id.nav_userfeedback) {
            Intent profile = new Intent(Userform.this,UserFeedback.class);
            startActivity(profile);
            finish();

        }else if (id ==R.id.nav_profile){
            Intent userfeedback = new Intent(Userform.this,Profile.class);
            startActivity(userfeedback);
            finish();
        }


        else if (id == R.id.nav_use) {
            Intent home = new Intent(Userform.this,Howtouse.class);
            startActivity(home);
            finish();

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent main = new Intent(Userform.this,GoogleSignIn.class);
            startActivity(main);
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
