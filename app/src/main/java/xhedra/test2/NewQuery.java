package xhedra.test2;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class NewQuery extends AppCompatActivity {

    private Button record;
    private TextView label;

    private MediaRecorder mRecorder;

    private String mFileName = null;

    private static final String LOG_TAG = "Record_log";

    private StorageReference storage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_query);

        progressDialog = new ProgressDialog(this);

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
}
