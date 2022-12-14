package org.iii.testjjsdk;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.iii.testjjsdk.databinding.ActivityMapsBinding;
import org.json.JSONObject;
//MAP
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
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
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
    Button buttongps;
    String result="";
    String id="";
    String latitude;
    String longitude;



    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    Double la=0.0,lo=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
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
        buttongps = findViewById(R.id.buttongps);

        Intent intent = getIntent();
        id=intent.getStringExtra("member_id");

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
        btnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPositionNetwork();
            }
        });
        buttongps.setOnClickListener(new View.OnClickListener() {
            @Override
            // ????????????
            public void onClick(View view) {
                // ?????????????????????????????????
                // ???????????????
                Thread thread = new Thread(MapsActivity);
                thread.start(); // ????????????
                Intent intent = new Intent();
                intent.putExtra("member_id",id);
                intent.setClass(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
    private Runnable MapsActivity = new Runnable(){
        public void run()
        {
            try {
                String param = "txtViewLatNetwork="+latitude+ "&txtViewLongNetwork="+longitude+"&member="+id;
                URL url = new URL("http://59.126.170.89/GPS.php/");
                // ???????????? HTTP ???????????????????????????????????????????????????
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // ?????? Google ???????????? HttpURLConnection ??????
                connection.setRequestMethod("POST");
                // ????????????????????? POST
                connection.setDoOutput(true); // ????????????
                connection.setDoInput(true); // ????????????-
                connection.setUseCaches(false); // ???????????????
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "Keep-Alive");// ???????????????
                connection.setRequestProperty("Charset", "UTF-8");
                connection.connect(); // ????????????
                OutputStream out = connection.getOutputStream();
                out.write(param.getBytes());
                out.flush();
                out.close();
                int responseCode =
                        connection.getResponseCode();
                // ???????????????????????????
                if(responseCode == HttpURLConnection.HTTP_OK){
                    // ?????? HTTP ??????????????? OK ???????????? Error
                    InputStream inputStream = connection.getInputStream();
                    // ??????????????????
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    // ???????????????????????????
                    String box = ""; // ?????????????????????
                    String line = null; // ????????????????????????
                    while((line = bufReader.readLine()) != null) {
                        Log.e("123","777");
                        result += line + "\n";
                        // ???????????????????????????????????????????????????
                    }
                    inputStream.close(); // ??????????????????
                    JSONObject j = new JSONObject(result);
                    String key = j.getString("success");
                    if(key.equals("1"))
                    {
                        result="????????????";
                        Intent intent = new Intent();
                        intent.putExtra("member_id",id);
                        intent.setClass(MapsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                // ??????????????????????????????????????????
                // ????????????????????????????????????
                // ?????? Json ????????????????????????????????????

            } catch(Exception e) {
                result = e.toString(); // ?????????????????????????????????
            }

            // ???????????????????????????????????????
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapsActivity.this, result, Toast.LENGTH_SHORT).show(); // ??????????????????
                    result = "";
                }
            });
        }
    };

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
                latitude=Double.toString(location.getLatitude());
                longitude=Double.toString(location.getLongitude());
                txtViewLatNetwork.setText(Double.toString(location.getLatitude()));
                txtViewLongNetwork.setText(Double.toString(location.getLongitude()));
                txtViewAltNetwork.setText(Double.toString(location.getAltitude()));
                la=location.getLatitude();
                lo=location.getLongitude();


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
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
    public void onMapReady(GoogleMap map) {
        getPositionNetwork();
        mMap = map;
        if (la==0.0) {
            SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity.this);


        }



        // Add a marker in Sydney and move the camera
        mMap.clear();
        LatLng sydney = new LatLng(la,lo);
        mMap.addMarker(new MarkerOptions().position(sydney).title("????????????"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



//        // Zoom in, animating the camera.
//        map.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);






    }



}