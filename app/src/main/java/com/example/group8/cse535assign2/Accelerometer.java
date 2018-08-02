package com.example.group8.cse535assign2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.widget.Toast;

public abstract class Accelerometer extends Service implements SensorEventListener {

    private SensorManager accelManager;
    private Sensor senseAccel;
    float valuesX[] = new float[128];
    float valuesY[] = new float[128];
    float valuesZ[] = new float[128];


    int index = 0;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

     Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        index++;
            valuesX[index] = sensorEvent.values[0];
            valuesY[index] = sensorEvent.values[1];
            valuesZ[index] = sensorEvent.values[2];
        if(index >= 127){
            index = 0;
            accelManager.unregisterListener(this);

            accelManager.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
