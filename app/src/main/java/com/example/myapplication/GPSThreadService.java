package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class GPSThreadService extends Service {

    private double longitudeBack = 0;
    private double latitudeBack = 0;

    private double r = 6378.1;
    private double D = 0;
    private double a = 0;
    private double ALat = 0;
    private double ALon = 0;
    private double c = 0;


    public GPSThreadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return IService;
    }
    IGPSInterface.Stub IService = new IGPSInterface.Stub() {
        @Override
        public double onGetVelocity(double latitude, double longitude) throws RemoteException {

             ALat = (latitude - latitudeBack);


             ALon = (longitude - longitudeBack);


            a = Math.sin(ALat / 2) * Math.sin(ALat / 2) + Math.cos(latitudeBack) * Math.cos(latitude) * Math.sin(ALon / 2) * Math.sin(ALon / 2);


            if (a < 1) {
                c = 2 * Math.asin(Math.sqrt(a));
            } else {
                c = 2 * Math.asin(Math.sqrt(1));

            }
            if((r * c * 1000)/3600>0) {
                D = r * c * 1000;
            }
            else
                D=0;
            longitudeBack =longitude;
            latitudeBack = latitude;

            return D;
        }

        @Override
        public boolean activeLoop(boolean start) throws RemoteException {
            return false;
        }
    };

}
