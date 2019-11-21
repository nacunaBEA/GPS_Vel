package com.example.myapplication;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Velocity";




    private static final int LOCATION_TIME = 1000;
    private static final float LOCATION_DISTANCE = 0;
    private double longitude= 0 ;
    private double latitude = 0 ;
    private TelephonyManager mTelephonyManager = null;
    private PhoneStateListener phoneStateListener = null;




    private LocationManager locationManager;
    private LocationListener locationListener;




//    private GPSBackgroundThread gpsBackgroundThread;
    private TextView tv_velocity;
    private Button bt_start;
    private boolean gpsRutineFlag = false;
    private  Handler mhandler = new Handler();

    IGPSInterface mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_velocity = findViewById(R.id.tv_velocity);
        bt_start  =findViewById(R.id.bt_start);


        locationListener = new BEALocationListener();
            permissions();





    }
    public void permissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                 requestPermissions(new String[]{ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        else{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mTelephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);



            Intent intentService = new Intent(this,GPSThreadService.class);
            bindService(intentService,mConnection,Context.BIND_AUTO_CREATE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, true);
            if(provider != null) {
                Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_TIME, LOCATION_DISTANCE, locationListener);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                permissions();
                break;
            default:
                break;
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = IGPSInterface.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    int count = 0;
    public void StartGPSServiceRutine(View view) {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    gpsRutineFlag =true;

                    while (gpsRutineFlag) {

                        tv_velocity.post(new Runnable() {
                            @Override
                            public void run() {
                                double distance =0;
                                try {
                                     distance = mService.onGetVelocity(latitude, longitude);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
//hola
                                tv_velocity.setText("Segundo -> " + ++count + "\tvelocidad -> " + String.valueOf(distance));
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();

    }











    public void StopGPSServiceRutine(View view)
    {
         gpsRutineFlag=false;
        Log.d(TAG,"Se detuvo el hilo para obtener la velocidad");
//        mCallback.activeLoop(true);
    }



    public class BEALocationListener implements LocationListener{



        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            latitude = Math.toRadians(location.getLatitude());
            longitude = Math.toRadians(location.getLongitude());
            Toast.makeText(getApplicationContext(),"Location changed", Toast.LENGTH_LONG).show();
//            mCallback.onSetCoordinates(latitude,longitude);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }











}


