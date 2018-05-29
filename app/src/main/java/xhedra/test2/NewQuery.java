package xhedra.test2;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewQuery extends AppCompatActivity {

    private Button record;
    private TextView label;
    private ImageView imgUpld;
    private MediaRecorder mRecorder;

    private String mFileName = null;

    private static final String LOG_TAG = "Record_log";

    private StorageReference storage;

    private ProgressDialog progressDialog;
    private final int PICK_IMAGE_REQUEST = 71;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    String mCurrentImagePath ;
    Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_query);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 29);
        }

        progressDialog = new ProgressDialog(this);
        imgUpld = findViewById(R.id.imageView2);
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
        imgUpld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),"Camera allowed", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 29){
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"MIC allowed", Toast.LENGTH_LONG).show();
                }

        }
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

    private File createNewFile() throws IOException{
        String header = "IMG";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prefix = header + timeStamp;
        String suffix = ".jpg";
        File image = File.createTempFile(prefix,suffix,getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        mCurrentImagePath = image.getPath();
        return image;
    }
    private void uploadImage(){
        StorageReference storageReference = storage.child("images").child("IMG.jpg");
        storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(NewQuery.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewQuery.this, "Could not upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void upldImg(View view){
        uploadImage();
    }
    private void chooseImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createNewFile();
            }catch(IOException ex) {
                ex.printStackTrace();
            }
            if(photoFile != null){
                Uri PhotoUri = FileProvider.getUriForFile(NewQuery.this,"xhedra.test2.fileprovider",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, PhotoUri);
                startActivityForResult(intent,1);
                filePath = Uri.fromFile(new File(mCurrentImagePath));
            }
        }
    }
}
