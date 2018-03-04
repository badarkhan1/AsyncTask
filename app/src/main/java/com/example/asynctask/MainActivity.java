package com.example.asynctask;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    Button btn;
    ProgressBar progressBar;
    EditText eturl;
    ListView listView;
    String[] imgurls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btndownload);
        btn.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        eturl = (EditText) findViewById(R.id.eturl);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        imgurls = getResources().getStringArray(R.array.imgurls);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btndownload:
                downloadImage();
                break;
            default:
                break;
        }
    }

    private void downloadImage() {
        if(eturl.getText().toString().equals("")){
            Toast.makeText(this, "Select an image to download", Toast.LENGTH_SHORT).show();
            return;
        }
        new DownloadImageTask().execute(eturl.getText().toString());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        eturl.setText(imgurls[i]);
    }

    private class DownloadImageTask extends AsyncTask<String,Integer,Boolean>{

        private static final String TAG = "MTAG";

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean success = false;
            URL downloadURL;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            File file;
            int count = 0;
            int progress;
            int total;

            try{
                downloadURL = new URL(strings[0]);
                connection = (HttpURLConnection) downloadURL.openConnection();
                total = connection.getContentLength();
                inputStream = connection.getInputStream();
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+
                        Uri.parse(strings[0]).getLastPathSegment());
                outputStream = new FileOutputStream(file);

                // Writing Data To The Destination
                int read = -1;
                byte[] buffer = new byte[1024];
                while ((read = inputStream.read(buffer))!= -1){
                    outputStream.write(buffer,0,read);
                    count += read;
                    progress = ((int)(((double)count /total)*100));
                    publishProgress(progress);
                    Log.d(TAG, "doInBackground: " + "count: " + count + " total:" + total + " percentage: " + progress);
                }
                success = true;
            }catch (MalformedURLException e){
                Log.d(TAG, "doInBackground: " + e + " ");
            }catch (IOException e){
                Log.d(TAG, "doInBackground: " + e + "");
            }finally {
                if(connection != null){
                    connection.disconnect();
                }
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.d(TAG, "doInBackground: " + e + " ");
                    }
                }
                if (outputStream != null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        Log.d(TAG, "doInBackground: " + e + " ");
                    }
                }
            }

            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.GONE);
        }
    }
}















