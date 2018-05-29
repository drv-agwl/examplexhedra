package xhedra.test2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class NewQuery extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Button record;
    private TextView label;

    private MediaRecorder mRecorder;

    private String mFileName = null;

    private static final String LOG_TAG = "Record_log";

    private StorageReference storage;

    private ProgressDialog progressDialog;

    private ImageButton image,video,text;

    private TextToSpeech engine;

    private EditText textQuery;
    private TextView videoQuery,imageQuery;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_query);

        progressDialog = new ProgressDialog(this);

        engine = new TextToSpeech(this,this);

        imageQuery = (TextView)findViewById(R.id.imageupload);
        videoQuery = (TextView)findViewById(R.id.videoupload);
        textQuery = (EditText)findViewById(R.id.editText);

        image = (ImageButton)findViewById(R.id.imageu);
        video = (ImageButton)findViewById(R.id.videou);
        text = (ImageButton)findViewById(R.id.textu);

        storage = FirebaseStorage.getInstance().getReference();

        record = (Button)findViewById(R.id.record);
        label = (TextView)findViewById(R.id.textView);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/record.3gp";

        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    startRecording();
                    label.setText("Started Recording....");

                }else if(event.getAction() == MotionEvent.ACTION_UP){

                    stopRecording();
                    label.setText("Recording stopped");
                }

                return false;
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                String text = textQuery.getText().toString();
                speak(text);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                String videotext = videoQuery.getText().toString();
                speak(videotext);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                String imageText = imageQuery.getText().toString();
                speak(imageText);
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        uploadAudio();
    }

    private void uploadAudio() {

        progressDialog.setMessage("Uploading Audio...");
        progressDialog.show();

        StorageReference filepath = storage.child("Audio").child("recording.3gp");
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                label.setText("Uploading Finished");
            }
        });



    }

    @Override
    public void onInit(int i) {
        if(i == TextToSpeech.SUCCESS){
            engine.setLanguage(Locale.getDefault());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speak(String s){
        engine.speak(s,TextToSpeech.QUEUE_FLUSH,null,null);
    }
}
