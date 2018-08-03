package com.example.group8.cse535assign2;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.IOException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.os.Environment;
/**
 * group 8
 */

public class SecondActivity extends AppCompatActivity implements SensorEventListener {
    private double x = 0;
    private double y =0;
    float[] values = new float[20];
    boolean ifRun = false;
    Random random;
    Thread thread;


    EditText patientName;
    EditText patientID;
    EditText age;
    EditText sex;

    String patientIDText;
    String ageText;
    String sexText;
    String patientNameText;



    private LineGraphSeries<DataPoint> seriesX;
    private LineGraphSeries<DataPoint> seriesY;
    private LineGraphSeries<DataPoint> seriesZ;

    GraphView graphX;
    GraphView graphY;
    GraphView graphZ;


    // GraphView graph2;

    String path;
    File dbfile;
    SQLiteDatabase db;
    String table_name;




    /**
     *
     * sensor
     */

    private SensorManager accelManager;
    private Sensor senseAccel;
    float valuesX ;
    float valuesY ;
    float valuesZ ;
    int index = 0;



    @ Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        /**
         * sensor
         */
        accelManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManager.registerListener(this, senseAccel,  SensorManager.SENSOR_DELAY_NORMAL);




        /**
         * component
         *
         */
        patientID = (EditText) findViewById(R.id.id);
        age = (EditText) findViewById(R.id.age);
        patientName = (EditText) findViewById(R.id.name);

        /**
         * database
         */
        path = Environment.getExternalStorageDirectory().getAbsolutePath();


        dbfile = new File(this.getExternalFilesDir(null) + "/CSE535_ASSIGNMENT2");


        if(!dbfile.exists() && !dbfile.isDirectory()){
            dbfile.mkdirs();
        }

        db = SQLiteDatabase.openOrCreateDatabase(dbfile + "/group8.db", null);


        /**
         * build a graph
         */

        graphX = (GraphView) findViewById(R.id.graphX);
        graphY = (GraphView) findViewById(R.id.graphY);
        graphZ = (GraphView) findViewById(R.id.graphZ);
        //graphLayout.addView(graph);

        seriesX = new  LineGraphSeries<DataPoint>();
        seriesY = new  LineGraphSeries<DataPoint>();
        seriesZ = new  LineGraphSeries<DataPoint>();



        Button startButton = (Button) findViewById(R.id.start);

        graphX = (GraphView) findViewById(R.id.graphX);
        Viewport viewportX = graphX.getViewport();
        viewportX.setYAxisBoundsManual(true);
        viewportX.setMinY(0);
        viewportX.setMaxY(40);
        viewportX.setXAxisBoundsManual(true);
        viewportX.setMinX(0);
        viewportX.setMaxX(50);
        viewportX.setScrollable(true);

        graphY = (GraphView) findViewById(R.id.graphY);
        Viewport viewportY = graphY.getViewport();
        viewportY.setYAxisBoundsManual(true);
        viewportY.setMinY(0);
        viewportY.setMaxY(40);
        viewportY.setXAxisBoundsManual(true);
        viewportY.setMinX(0);
        viewportY.setMaxX(50);
        viewportY.setScrollable(true);

        graphZ = (GraphView) findViewById(R.id.graphZ);
        Viewport viewportZ = graphZ.getViewport();
        viewportZ.setYAxisBoundsManual(true);
        viewportZ.setMinY(0);
        viewportZ.setMaxY(40);
        viewportZ.setXAxisBoundsManual(true);
        viewportZ.setMinX(0);
        viewportZ.setMaxX(50);
        viewportZ.setScrollable(true);





        /**
         * build a sex radioGroup
         */
        RadioGroup sexRadio = (RadioGroup) findViewById(R.id.sexRadioGroup);
        sexRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton choice = (RadioButton) findViewById(id);
                sexText = choice.getText().toString();
                Toast.makeText(SecondActivity.this, "" + sexText, Toast.LENGTH_SHORT).show();
            }
        });


        /**
         * create db button
         */

        Button createDB = (Button) findViewById(R.id.createDB);
        createDB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(patientNameText != null) {

                    if(patientIDText != null) {

                        if (ageText != null) {

                            if (sexText != null) {

                                table_name = patientNameText + "_" + patientIDText + "_" + ageText + "_" + sexText;

                                try {
                                    db.beginTransaction();

                                    //perform your database operations here ...
                                    db.execSQL("create table if not exists  " + table_name + " ( TIMESTAMP TIMESTAMP , X FLOAT, Y FLOAT, Z FLOAT) ");

                                    db.setTransactionSuccessful(); //commit your changes

                                    Toast.makeText(SecondActivity.this, "CREATE", Toast.LENGTH_LONG).show();
                                } catch (SQLiteException e) {
                                    e.getStackTrace();
                                    Toast.makeText(SecondActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                } finally {

                                    db.endTransaction();
                                    drawGraph();
                                }
                            } else {
                                Toast.makeText(SecondActivity.this, "Please enter a Age", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(SecondActivity.this, "Please enter a patient ID", Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(SecondActivity.this, "Please enter a patient sex", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(SecondActivity.this, "Please enter a patient name", Toast.LENGTH_LONG).show();
                }
            }

        });


        /**
         * stop button
         */
        Button stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ifRun = false;
                graphX.removeAllSeries();
                graphY.removeAllSeries();
                graphZ.removeAllSeries();
                Toast.makeText(SecondActivity.this,  "stop", Toast.LENGTH_SHORT).show();
                //graphLayout.removeView(graph);


            }
        });



        patientName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                patientNameText = v.getText().toString();
                return false;
            }
        });

        patientID.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                patientIDText = v.getText().toString();
                return false;
            }
        });


        age.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                ageText = v.getText().toString();
                return false;
            }
        });




    }

    public void drawGraph(){
        if (ifRun == false) {

            random = new Random();
            ifRun = true;

            graphX.addSeries(seriesX);
            graphY.addSeries(seriesY);
            graphZ.addSeries(seriesZ);

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (ifRun) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                x += 1;
                                seriesX.appendData(new DataPoint(x, valuesX), true, 100);
                                seriesY.appendData(new DataPoint(x, valuesY), true, 100);
                                seriesZ.appendData(new DataPoint(x, valuesZ), true, 100);

                                Timestamp ts=new Timestamp(new Date().getTime());

                                try {
                                    //perform your database operations here ...
                                    db.execSQL("insert into " + table_name + " (timestamp,x,y,z) values ('" + ts + "','" + valuesX + "', '" + valuesY + "','" + valuesZ + "' );");
                                    //db.setTransactionSuccessful(); //commit your changes
                                } catch (SQLiteException e) {
                                    e.getStackTrace();
                                } finally {
                                    //db.endTransaction();
                                }



                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

            });
            thread.start();
            Toast.makeText(SecondActivity.this, "run", Toast.LENGTH_SHORT).show();
        }


    }

    long lasttime = 0;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        long currtime = System.currentTimeMillis();

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER   && currtime - lasttime >= 1000 ) {
            //index++;
            valuesX = sensorEvent.values[0];
            valuesY = sensorEvent.values[1];
            valuesZ = sensorEvent.values[2];

            lasttime = currtime;

        }
    }

    float dataX[] = new float[10];
    float dataY[] = new float[10];
    float dataZ[] = new float[10];



    public void drawFromDB(){
        String getData = "SELECT  * FROM " + table_name + " ORDER BY created_at desc";
        Cursor cs = null;


        try {

            cs = db.rawQuery(getData, null);

            //db.setTransactionSuccessful();
        } catch (Exception e) {
            e.getStackTrace();
        }

        int count = 0;

        if (cs.moveToFirst()) {
            do {

                dataX[count] = Float.parseFloat(cs.getString(1));
                dataY[count] = Float.parseFloat(cs.getString(2));
                dataZ[count] = Float.parseFloat(cs.getString(3));

                index++;
            } while (cs.moveToNext() && count < 10);
        }

        for(int i = 0; i < 10; i++){
            seriesX.appendData(new DataPoint(i, dataX[9 - i]), true, 100);
            seriesY.appendData(new DataPoint(i, dataY[9 - i]), true, 100);
            seriesZ.appendData(new DataPoint(i, dataZ[9 - i]), true, 100);
        }

    }

















    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }






}
