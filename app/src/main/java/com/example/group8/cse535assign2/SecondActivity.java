package com.example.group8.cse535assign2;


import android.content.Intent;
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

public class SecondActivity extends AppCompatActivity {
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


    @ Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


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


        dbfile = new File(this.getExternalFilesDir(null) + "/mydata");


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
        viewportX.setMaxY(2500);
        viewportX.setXAxisBoundsManual(true);
        viewportX.setMinX(0);
        viewportX.setMaxX(50);
        viewportX.setScrollable(true);

        graphY = (GraphView) findViewById(R.id.graphY);
        Viewport viewportY = graphY.getViewport();
        viewportY.setYAxisBoundsManual(true);
        viewportY.setMinY(0);
        viewportY.setMaxY(2500);
        viewportY.setXAxisBoundsManual(true);
        viewportY.setMinX(0);
        viewportY.setMaxX(50);
        viewportY.setScrollable(true);

        graphZ = (GraphView) findViewById(R.id.graphZ);
        Viewport viewportZ = graphZ.getViewport();
        viewportZ.setYAxisBoundsManual(true);
        viewportZ.setMinY(0);
        viewportZ.setMaxY(2500);
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
                                double xy = random.nextDouble() * 2500d;
                                double yy = random.nextDouble() * 2500d;
                                double zy = random.nextDouble() * 2500d;

                                seriesX.appendData(new DataPoint(x++, xy), true, 100);
                                seriesY.appendData(new DataPoint(x++, yy), true, 100);
                                seriesZ.appendData(new DataPoint(x++, zy), true, 100);

                                Timestamp ts=new Timestamp(new Date().getTime());

                                try {
                                    //perform your database operations here ...
                                    db.execSQL("insert into " + table_name + " (timestamp,x,y,z) values ('" + ts + "','" + xy + "', '" + yy + "','" + zy + "' );");
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


}
