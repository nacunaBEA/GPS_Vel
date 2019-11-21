// IGPSInterface.aidl
package com.example.myapplication;

// Declare any non-default types here with import statements

interface IGPSInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
      double onGetVelocity(in double latitude,in double longitude);

      boolean activeLoop(in boolean start);


}
