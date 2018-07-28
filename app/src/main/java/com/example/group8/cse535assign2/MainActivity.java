package com.example.group8.cse535assign2;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Random;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.os.Environment;
/**
 * group 8
 */

public class MainActivity extends AppCompatActivity {
    private double x = 0;
    private double y =0;
    float[] values = new float[20];
    boolean ifRun = false;
    Random random;
    Thread thread;

    SQLiteDatabase db;

    EditText patientName;
    EditText patientID;
    EditText age;
    EditText sex;

    String patientIDText;
    String ageText;
    String sexText;
    String patientNameText;


    private LineGraphSeries<DataPoint> series;
    GraphView graph;


    String path;

    @ Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         * build a graph
         */
        graph = (GraphView) findViewById(R.id.graph);
        series = new  LineGraphSeries<DataPoint>();

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(2500);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(50);
        viewport.setScrollable(true);
        Button startButton = (Button) findViewById(R.id.start);

        /**
         * create a start button
         */
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ifRun == false) {

                    random = new Random();
                    ifRun = true;
                    graph.addSeries(series);

                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (ifRun) {

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        series.appendData(new DataPoint(x++, random.nextDouble() * 2500d), true, 100);
                                    }
                                });

                                try {
                                    Thread.sleep(400);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    });
                    thread.start();
                    Toast.makeText(MainActivity.this, "run", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, path , Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * create a stop button
         */
        Button stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ifRun = false;
                graph.removeSeries(series);
                Toast.makeText(MainActivity.this,  "stop", Toast.LENGTH_SHORT).show();
            }
        });

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
                Toast.makeText(MainActivity.this, "" + sexText, Toast.LENGTH_SHORT).show();
            }
        });


        //name = ((EditText) findViewById(R.id.name)).getText().toString();
        //age = ((EditText) findViewById(R.id.age)).getText().toString();
        //id = ((EditText) findViewById(R.id.id)).getText().toString();


        Button createDB = (Button) findViewById(R.id.createDB);
        createDB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)  {
                try{

                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/database";
                    File pathFile = new File(path);
                    File file = new File(path+"/mydata.db");


                        if(!pathFile.exists()){
                            pathFile.mkdirs();
                        }
                        if(!file.exists()){
                            file.createNewFile();
                        }


                    db = SQLiteDatabase.openOrCreateDatabase(file, null);
                    db.beginTransaction();


                    try {

                        //perform your database operations here ...
                        db.execSQL("create table tblPat ("
                                + " recID integer PRIMARY KEY autoincrement, "
                                + " name text, "
                                + " id text, "
                                + " age text, "
                                + " sex text ); " );

                        db.setTransactionSuccessful(); //commit your changes


                    }
                    catch (SQLiteException e) {
                        //report problem
                    }
                    finally {
                        db.endTransaction();
                    }
                }catch (SQLException e){

                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        patientID = (EditText) findViewById(R.id.id);
        age = (EditText) findViewById(R.id.age);
        patientName = (EditText) findViewById(R.id.name);
        //sex = (EditText) findViewById(R.id.sex);


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


        Button add = (Button) findViewById(R.id.addDB);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(patientNameText != null) {
                    if(sexText != null) {

                        if (patientIDText != null) {


                            if (ageText != null) {
                                //db.beginTransaction();
                                try {
                                    //perform your database operations here ...
                                    db.execSQL("insert into tblPat(name, id, age, sex) values ('" + patientNameText + ",'" + patientIDText + "', '" + ageText + "' ,'" + sexText + "');");
                                    //db.setTransactionSuccessful(); //commit your changes
                                } catch (SQLiteException e) {
                                    //report problem
                                } finally {
                                    //db.endTransaction();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Please enter a Age", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Please enter a patient ID", Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(MainActivity.this, "Please enter a patient sex", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Please enter a patient name", Toast.LENGTH_LONG).show();
                    }
                }

        });





    }

}
