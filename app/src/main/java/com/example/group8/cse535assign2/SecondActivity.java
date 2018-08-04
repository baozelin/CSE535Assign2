package com.example.group8.cse535assign2;


import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    SQLiteDatabase downloadDB;
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


    /** Variables for upload* */
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    String upLoadServerUri = "http://impact.asu.edu/CSE535Spring18Folder/UploadToServer.php";
    String downLoadServerUri = "http://impact.asu.edu/CSE535Spring18Folder/UploadToServer.php";
    String uploadFilePath =null;
    String uploadFileName = null;
    String downloadFilePath =null;
    String downloadFileName = null;
    DownloadManager downloadManager;



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

        graphX = (GraphView) findViewById(R.id.graphX);
        Viewport viewportX = graphX.getViewport();
        viewportX.setYAxisBoundsManual(true);
        viewportX.setMinY(-20);
        viewportX.setMaxY(20);
        viewportX.setXAxisBoundsManual(true);
        viewportX.setMinX(0);
        viewportX.setMaxX(10);
        viewportX.setScrollable(true);

        graphY = (GraphView) findViewById(R.id.graphY);
        Viewport viewportY = graphY.getViewport();
        viewportY.setYAxisBoundsManual(true);
        viewportY.setMinY(-20);
        viewportY.setMaxY(20);
        viewportY.setXAxisBoundsManual(true);
        viewportY.setMinX(0);
        viewportY.setMaxX(10);
        viewportY.setScrollable(true);

        graphZ = (GraphView) findViewById(R.id.graphZ);
        Viewport viewportZ = graphZ.getViewport();
        viewportZ.setYAxisBoundsManual(true);
        viewportZ.setMinY(-20);
        viewportZ.setMaxY(20);
        viewportZ.setXAxisBoundsManual(true);
        viewportZ.setMinX(0);
        viewportZ.setMaxX(10);
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
                patientID = (EditText) findViewById(R.id.id);
                age = (EditText) findViewById(R.id.age);
                patientName = (EditText) findViewById(R.id.name);
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

                                    Toast.makeText(SecondActivity.this, "CREATE TABLE: "+table_name, Toast.LENGTH_LONG).show();
                                } catch (SQLiteException e) {
                                    e.getStackTrace();
                                    Toast.makeText(SecondActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                } finally {

                                    db.endTransaction();
                                    //drawGraph();
                                }
                            } else {
                                Toast.makeText(SecondActivity.this, "Please select patient sex", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(SecondActivity.this, "Please input Age", Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(SecondActivity.this, "Please input patient Id", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(SecondActivity.this, "Please input patient name", Toast.LENGTH_LONG).show();
                }
            }

        });


        /**
         * start button
         */
        Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawGraph();

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

        /**Create upload button**/
        Button uploadButton;
        uploadButton = (Button)findViewById(R.id.uploadDB);

        uploadFilePath = this.getExternalFilesDir(null) + "/CSE535_ASSIGNMENT2";
        uploadFileName = "/group8.db";
        /**Php script**/
        upLoadServerUri = "http://impact.asu.edu/CSE535Spring18Folder/UploadToServer.php";

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(SecondActivity.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    public void run() {

                        uploadDB(uploadFilePath + "" + uploadFileName);

                    }
                }).start();
            }
        });

        /**Create download button**/
        Button downloadButton;
        downloadButton = (Button)findViewById(R.id.downloadDB);
        downloadFilePath = "/CSE535_ASSIGNMENT2_DOWN";
        downloadFileName = "group8.db";
        /**Php script**/
        downLoadServerUri = "http://impact.asu.edu/CSE535Spring18Folder/group8.db";

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(downLoadServerUri);
                Uri dUri = Uri.parse(downloadFilePath);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(SecondActivity.this,downloadFilePath,downloadFileName);
                Long reference =downloadManager.enqueue(request);

                Toast.makeText(SecondActivity.this, "Downloading file to: "+downloadFilePath,
                        Toast.LENGTH_SHORT).show();
                try {
                    thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                drawFromDB();

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


                                seriesX.appendData(new DataPoint(x, valuesX), true, 100);
                                seriesY.appendData(new DataPoint(x, valuesY), true, 100);
                                seriesZ.appendData(new DataPoint(x, valuesZ), true, 100);
                                x += 1;
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
        patientID = (EditText) findViewById(R.id.id);
        age = (EditText) findViewById(R.id.age);
        patientName = (EditText) findViewById(R.id.name);
        table_name = patientNameText + "_" + patientIDText + "_" + ageText + "_" + sexText;
        Toast.makeText(SecondActivity.this, "Draw data of: "+table_name,
                Toast.LENGTH_SHORT).show();
        path = Environment.getExternalStorageDirectory().getAbsolutePath();

        dbfile = new File(this.getExternalFilesDir(null) + "/CSE535_ASSIGNMENT2_DOWN");

        if(!dbfile.exists() && !dbfile.isDirectory()){
            dbfile.mkdirs();
        }

        downloadDB = SQLiteDatabase.openOrCreateDatabase(dbfile + "/group8.db", null);

        String getData = "SELECT  * FROM " + table_name + " ORDER BY TIMESTAMP desc";
        Cursor cs = null;


        try {

            cs = downloadDB.rawQuery(getData, null);

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

                count++;
            } while (cs.moveToNext() && count < 10);
        }


        seriesX = new  LineGraphSeries<DataPoint>();
        seriesY = new  LineGraphSeries<DataPoint>();
        seriesZ = new  LineGraphSeries<DataPoint>();

        graphX.addSeries(seriesX);
        graphY.addSeries(seriesY);
        graphZ.addSeries(seriesZ);

        for(int i = 0; i < count; i++){
            //x++;
            seriesX.appendData(new DataPoint(i, dataX[9 - i]), true, 100);
            seriesY.appendData(new DataPoint(i, dataY[9 - i]), true, 100);
            seriesZ.appendData(new DataPoint(i, dataZ[9 - i]), true, 100);
        }

    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @SuppressLint("LongLogTag")
    public void uploadDB(String sourceFileUri){
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(SecondActivity.this, "Source File not exist :", Toast.LENGTH_LONG).show();
                }
            });

            return;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response2 is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +"http://impact.asu.edu/CSE535Spring18Folder/"
                                    +uploadFileName;

                            Toast.makeText(SecondActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SecondActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SecondActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return;

        }
    }






}