package com.example.richard.glassapp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.sonyericsson.extras.liveware.aef.registration.Registration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static MainActivity object = null;

    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    long totalSize = 0;

    //needed for TTS
    TextToSpeech t1;

    //Speech-to-Text
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private String txtInput = "";

    private AudioManager mAudioManager;
    private ComponentName mReceiverComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        object = this;

        setContentView(R.layout.activity_upload);

        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);

        //TextToSpeech initialization
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        /**
         * Capture speech recording button click event
         */
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        /*
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.addAction("android.intent.action.MEDIA_BUTTON");
        HeadPhonesReceiver r = new HeadPhonesReceiver();
        registerReceiver(r, filter);
        */

        mAudioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mReceiverComponent = new ComponentName(this,HeadPhonesReceiver.class);
        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);

        if (AppSampleExtensionService.object == null) {
            Intent intent = new Intent(Registration.Intents
                    .EXTENSION_REGISTER_REQUEST_INTENT);
            Context context = getApplicationContext();
            intent.setClass(context, AppSampleExtensionService.class);
            context.startService(intent);
        }
    }

    public void setTxtInput(String txtInput){
        this.txtInput = txtInput;
    }

    public void previewMedia(Bitmap picture){
        imgPreview.setVisibility(View.VISIBLE);
        vidPreview.setVisibility(View.GONE);

        imgPreview.setImageBitmap(picture);
    }

    public void setTxtSpeechInput(String result){
        txtSpeechInput.setText(result);
    }

    public final class UploadTask extends AsyncTask<Void, Integer, String> {
        private Bitmap bitmap = null;
        private String fileName = null;

        public UploadTask(Bitmap bitmap, String fileName){
            Log.d(Constants.LOG_TAG, "new upload task created");
            this.bitmap = bitmap;
            this.fileName = fileName;

            Log.d(Constants.LOG_TAG, "file name = " + fileName);
            Log.d(Constants.LOG_TAG, "inputText = " + txtInput);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        private String uploadFile(){
            Log.d(Constants.LOG_TAG, "upload started");
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            //Constants.getCameraData.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            byte [] ba = bao.toByteArray();

            Log.d(Constants.LOG_TAG, "converted image");

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.FILE_UPLOAD_URL);

            MultipartEntity entity = new MultipartEntity();
            entity.addPart("image", new ByteArrayBody(ba, fileName));
            try {
                entity.addPart("request",new StringBody(txtInput));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d(Constants.LOG_TAG, "failed to add request message");
            }

            Log.d(Constants.LOG_TAG, "created body");

            httppost.setEntity(entity);
            Log.d(Constants.LOG_TAG, "request sent");
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to execute request";
            }
            HttpEntity r_entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            String responseString = null;
            if (statusCode == 200) {
                // Server response
                try {
                    responseString = EntityUtils.toString(r_entity);
                } catch (IOException e) {
                    e.printStackTrace();
                    responseString = "Server failed :(";
                } catch (Exception e){
                    e.toString();
                    responseString = "Server failed :(";
                }
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }
            Log.d(Constants.LOG_TAG, "resulted response: " + responseString);

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            //Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            GlassAppCameraControl.clearRequest();
            showAlert(result);

            //parsing response from the server
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String networkResponse = null;
            try {
                networkResponse = jsonObj.getString("answer");
            } catch (JSONException e) {
                e.printStackTrace();
                networkResponse = "fail :(";
            } catch (Exception e){
                e.printStackTrace();
                networkResponse = "fail :(";
            }

            /*
            String lang = networkResponse.substring(0,3);
            networkResponse = networkResponse.substring(3,networkResponse.length()-1);
            Log.w("language of the response",lang);
            */
            Log.w("networkResponse",networkResponse);

            //Play the response
            playText(networkResponse);

            txtInput = "";
            txtSpeechInput.setText(txtInput);

            super.onPostExecute(result);
        }
    }

    public void runUploading(Bitmap bitmap, String fileName){
        new MainActivity.UploadTask(bitmap, fileName).execute();
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        t1.stop();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void playText(String txt){
        Toast.makeText(getApplicationContext(), txt,Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(txt);
        } else {
            ttsUnder20(txt);
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    txtInput = result.get(0);
                }
                break;
            }

        }
    }

    //functions for speaking
    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public static class HeadPhonesReceiver extends BroadcastReceiver {
        public HeadPhonesReceiver(){
            super();
            Log.d(Constants.LOG_TAG, "HeadPhonesReceiver created");
        }
        @Override public void onReceive(Context context, Intent intent) {
            Log.d(Constants.LOG_TAG, "broadcast received");
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return;
            }
            int action = event.getAction();
            if (action == KeyEvent.ACTION_DOWN) {
                // do something
                Log.d(Constants.LOG_TAG, "button down");
                MainActivity.object.promptSpeechInput();

                abortBroadcast();
            }
        }
    }
}
