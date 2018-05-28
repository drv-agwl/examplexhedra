package xhedra.test2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class reply extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        imageView = findViewById(R.id.imageView);
        Intent intent = getIntent();
        if(intent.hasExtra("URL")){
            Picasso.get().load(intent.getStringExtra("URL")).into(imageView);
        }

    }
}
