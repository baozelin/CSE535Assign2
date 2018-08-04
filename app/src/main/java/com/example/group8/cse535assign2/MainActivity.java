package com.example.group8.cse535assign2;


import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import android.database.sqlite.SQLiteDatabase;

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


    public EditText patientName;
    EditText patientID;
    EditText age;
    EditText sex;

    String patientIDText;
    String ageText;
    String sexText;
    String patientNameText;


    private LineGraphSeries<DataPoint> series;

    LinearLayout graphLayout;
    GraphView graph;

    GraphView graph2;

    String path;
    File dbfile;
    SQLiteDatabase db;

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
        setContentView(R.layout.activity_main);



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

        //dbfile = new File(Environment.getExternalStorageDirectory()+"/mydata");
        dbfile = new File(this.getExternalFilesDir(null) + "/mydata");


        if(!dbfile.exists() && !dbfile.isDirectory()){
            dbfile.mkdirs();
        }

        db = SQLiteDatabase.openOrCreateDatabase(dbfile + "/group8.db", null);


        /**
         * build a graph
         */
        graphLayout = (LinearLayout) findViewById(R.id.graphlayout);
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
                graph.removeAllSeries();
                Toast.makeText(MainActivity.this,  "stop", Toast.LENGTH_SHORT).show();

            }
        });


        /**
         * page2 button
         */

        Button pageButton = (Button) findViewById(R.id.page2);
        pageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this,  "move to page2", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);

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


        /**
         * create db button
         */


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

                dialog = ProgressDialog.show(MainActivity.this, "", "Uploading file...", true);

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
                request.setDestinationInExternalFilesDir(MainActivity.this,downloadFilePath,downloadFileName);
                Long reference =downloadManager.enqueue(request);

                Toast.makeText(MainActivity.this, "Downloading file to: "+downloadFilePath,
                        Toast.LENGTH_SHORT).show();

            }
        });

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
                    Toast.makeText(MainActivity.this, "Source File not exist :", Toast.LENGTH_LONG).show();
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

                            Toast.makeText(MainActivity.this, "File Upload Complete.",
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
                        Toast.makeText(MainActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
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
