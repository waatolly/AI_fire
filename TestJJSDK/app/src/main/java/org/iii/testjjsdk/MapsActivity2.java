package org.iii.testjjsdk;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.iii.testjjsdk.databinding.ActivityMaps2Binding;
//MAP
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//懸浮
import android.app.Application;
import android.view.Gravity;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;




import java.util.List;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private TextView txtViewLatGPS;
    private TextView txtViewLongGPS;
    private TextView txtViewAltGPS;

    private TextView txtViewLatNetwork;
    private TextView txtViewLongNetwork;
    private TextView txtViewAltNetwork;

    private LocationManager mLocationManagerGPS;
    private LocationListener mLocationListenerGPS;

    private LocationManager mLocationManagerNetwork;
    private LocationListener mLocationListenerNetwork;

    private Button btnGPS;
    private Button btnNetwork;
    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    Double la=24.76061758646203,lo=120.95341865344;
    String str="求救";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //        GPS
        txtViewLatGPS = findViewById(R.id.txtViewLatGPS);
        txtViewLongGPS = findViewById(R.id.txtViewLonGPS);
        txtViewAltGPS = findViewById(R.id.txtViewAltGPS);

        txtViewLatNetwork = findViewById(R.id.txtViewLatNetwork);
        txtViewLongNetwork = findViewById(R.id.txtViewLonNetwork);
        txtViewAltNetwork = findViewById(R.id.txtViewAltNetwork);

        btnGPS = findViewById(R.id.btnGPSLoc);
        btnNetwork = findViewById(R.id.btnNetworkLoc);

        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPositionGPS();
            }
        });

        btnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getPositionNetwork();}

        });



    }



    private void getPositionGPS() {
        mLocationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerGPS = new LocationListener() {
            public void onLocationChanged(Location location) {
                txtViewLatGPS.setText(Double.toString(location.getLatitude()));
                txtViewLongGPS.setText(Double.toString(location.getLongitude()));
                txtViewAltGPS.setText(Double.toString(location.getAltitude()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
                showAlert(R.string.GPS_disabled);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                btnGPS.setEnabled(false);
                mLocationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, mLocationListenerGPS);
            }
        }
    }

    private void getPositionNetwork() {
        mLocationManagerNetwork = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                txtViewLatNetwork.setText(Double.toString(location.getLatitude()));
                txtViewLongNetwork.setText(Double.toString(location.getLongitude()));
                txtViewAltNetwork.setText(Double.toString(location.getAltitude()));
                la=location.getLatitude();
                lo=location.getLongitude();
                str =str.replace("求救","現在地");



            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
                showAlert(R.string.Network_disabled);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                btnNetwork.setEnabled(false);
                mLocationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, mLocationListenerNetwork);
            }
        }
    }

    private void showAlert(int messageId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageId).setCancelable(false).setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }).show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_watch_permissions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())));
                }
            }).setNegativeButton(R.string.btn_close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!btnGPS.isEnabled()) {
            btnGPS.setEnabled(true);
        }

        if (!btnNetwork.isEnabled()) {
            btnNetwork.setEnabled(true);
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        getPositionNetwork();
        mMap = googleMap;



        if (la==24.76061758646203) {

            SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity2.this);




        }


        // Add a marker in Sydney and move the camera
//        mMap.clear();
        LatLng sydney = new LatLng(la, lo);
        mMap.addMarker(new MarkerOptions().position(sydney).title( str ));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }



}