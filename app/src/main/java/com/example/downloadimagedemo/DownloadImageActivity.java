package com.example.downloadimagedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.downloadimagedemo.data.TokenIDData;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


public class DownloadImageActivity extends AppCompatActivity {

    public static final String URL = "CHANGE TO DOWNLOAD PICTURE URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_image);

        ImageView image = (ImageView) findViewById(R.id.downloaded_image);

        new DownloadPictureTask(this, URL,image).execute();
    }


    public class DownloadPictureTask extends AsyncTask<Void,Void,Bitmap> {

        private final static String TAG = "DownloadPictureTask";

        private ImageView mImageView;

        private String mUrl;
        private Context mContext;

        TokenIDData token = new TokenIDData("USER TOKEN");

        public DownloadPictureTask(Context context, String url, ImageView imageView) {
            this.mContext = context;
            this.mUrl = url;
            this.mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                // Set the Content-Type header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<TokenIDData> requestEntity = new HttpEntity<>(token, requestHeaders);

                // Create a new RestTemplate instance
                @SuppressWarnings("deprecation")
                RestTemplate restTemplate = new RestTemplate(true);


                // Add the Jackson and String message converters
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
                ResponseEntity<String> responseEntity = restTemplate.exchange(mUrl, HttpMethod.POST, requestEntity, String.class);

                JSONObject res = new JSONObject(responseEntity.getBody());

                // The image comes in Base64
                String base64 = res.getString("file64Base");

                Log.e(TAG,base64);

                // Removes the Mime Type from the beginning
                base64 = base64.substring(base64.indexOf(",") + 1);

                // Converts to an array of bytes
                byte[] decoded = Base64.decode(base64, Base64.DEFAULT);

                // Converts to bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);

                // SAVE FOR CACHING

                return bitmap;

            } catch (HttpClientErrorException e) {
                Log.e("HTTP ERROR", e.getStatusCode().toString());
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                Log.e(TAG,"not null");
                mImageView.setImageBitmap(bitmap);
            } else {
                Log.e(TAG,"null");
            }
        }
    }
}
