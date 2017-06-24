package com.example.downloadimagedemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.example.downloadimagedemo.data.TokenIDData;
import com.example.downloadimagedemo.data.UploadImageData;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static final String URL_TO_POST = "CHANGE TO UPLOAD URL";
    public static final int PICK_PHOTO_FOR_AVATAR = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button downBtn = (Button) findViewById(R.id.download_btn);

        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),DownloadImageActivity.class);
                startActivity(intent);
            }
        });

        Button upBtn = (Button) findViewById(R.id.upload_btn);

        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                byte[] imageBytes = new byte[inputStream.available()];
                inputStream.read(imageBytes);

                // Encodes the array of bytes to Base64
                String base64 = Base64.encodeToString(imageBytes,Base64.NO_WRAP);

                //Log.e("Main",base64);

                ContentResolver cr = this.getContentResolver();
                String mime = cr.getType(data.getData());

                // Adds the Mime Type to the beginning of the Base64
                String base64ToUpload = "data:" + mime + ";base64," + base64;

                //Log.e("Main", base64ToUpload);

                //Log.e("Main", mime);

                UploadImageData upload = new UploadImageData(base64ToUpload,mime,"USER TOKEN");

                // Start upload task
                new UploadImageTask(upload,URL_TO_POST).execute();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    private void onPostSuccess() {
        Toast.makeText(this,"Upload success",Toast.LENGTH_SHORT).show();
    }

    public class UploadImageTask extends AsyncTask<Void,Void,HttpStatus> {

        UploadImageData data;
        String url;

        public UploadImageTask(UploadImageData data, String url) {
            this.data = data;
            this.url = url;
        }

        @Override
        protected HttpStatus doInBackground(Void... voids) {
            try {
                // Set the Content-Type header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);

                Log.e("Main", data.toString());

                HttpEntity<UploadImageData> requestEntity = new HttpEntity<>(data, requestHeaders);

                // Create a new RestTemplate instance
                @SuppressWarnings("deprecation")
                RestTemplate restTemplate = new RestTemplate(true);


                // Add the Jackson and String message converters
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
                ResponseEntity<String> responseEntity = restTemplate.exchange(url , HttpMethod.POST, requestEntity, String.class);

                return responseEntity.getStatusCode();

            } catch (HttpClientErrorException e) {
                Log.e("HTTP ERROR", e.getResponseBodyAsString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(HttpStatus httpStatus) {
            super.onPostExecute(httpStatus);
            if (httpStatus == HttpStatus.OK) {
                onPostSuccess();
            }

        }
    }

}
