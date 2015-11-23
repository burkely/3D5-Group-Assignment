package com.lydia.meet_me;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.location.Location;
import android.location.LocationManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    private String friend_location;
    private String message = null;
    protected Button button;

    public static final String TAG = MapsActivity.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.lydia.meet_me.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        friend_location = intent.getStringExtra(LaunchActivity.EXTRA_MESSAGE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        setUpMapIfNeeded();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
    private void setUpMap(){

        mMap.setMyLocationEnabled(true);
        mGoogleApiClient.connect();
        // Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    public void onSearch()
    {
        //EditText location_tf= (EditText)findViewById(R.id.TFaddress);
        //String location= location_tf.getText().toString();
        String location = friend_location;
        List<Address> addressList=null;
        if(location!=null|| !location.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try{
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address= addressList.get(0);
            LatLng latLng= new LatLng(address.getLatitude(),address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            double lat1= mLastLocation.getLatitude();
            double lon1= mLastLocation.getLongitude();
            double lat2= address.getLatitude();
            double lon2= address.getLongitude();

            System.out.println("lat1="+lat1+"lon1 = "+lon1+"lat2 = "+lat2+"lon2 = "+lon2);

            double dLon = Math.toRadians(lon2 - lon1);

            //convert to radians
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);
            lon1 = Math.toRadians(lon1);

            double Bx = Math.cos(lat2) * Math.cos(dLon);
            double By = Math.cos(lat2) * Math.sin(dLon);
            double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
            double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
            double lat4 = Math.toDegrees(lat3);
            double lon4 = Math.toDegrees(lon3);
            LatLng latLnga= new LatLng(lat4, lon4);
            mMap.addMarker(new MarkerOptions().position(latLnga).title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLnga));

            List<Address> geocodeMatches = null;
            String Address1 = null;
            String Address2 = null;
            String State = null;
            String Zipcode = null;
            String Country = null;

            try {
                geocodeMatches =
                        new Geocoder(this).getFromLocation(lat4, lon4, 1);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Location not recognized, try again.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.show();
                // create alert dialog

            }

            if (!geocodeMatches.isEmpty())
            {
                Address1 = geocodeMatches.get(0).getAddressLine(0);
                Address2 = geocodeMatches.get(0).getAddressLine(1);
                State = geocodeMatches.get(0).getAdminArea();
                Zipcode = geocodeMatches.get(0).getPostalCode();
                Country = geocodeMatches.get(0).getCountryName();
            }

            System.out.println("Address is "+ Address1 + Address2 + State + Zipcode + Country);


            if(Address1 != null){
                message = Address1;
            }
            if(Address2 != null){
                message =  message + " " + Address2;
            }
            if(State != null){
                message =  message + " " +State;
            }
            if(Zipcode != null){
                message = message +" " + Zipcode;
            }
            if(Country != null){
                message = message +" " + Country;
            }

            final Intent intent;
            intent = new Intent(this, display.class);
            intent.putExtra(EXTRA_MESSAGE, message);

            button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    startActivity(intent);
                }
            });




        }

    }

    protected void onResume(){
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded(){
        if (mMap==null){
            mMap=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if(mMap !=null){
                setUpMap();
            }
        }



    }


    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("onConnection");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            LatLng latLnga= new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLnga).title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLnga, 15));
        }
        onSearch();
    }


    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("ConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("ConnectionFailed");
    }
}