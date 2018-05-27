package xhedra.test2;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button loc_en;
    Button loc_hi;
    Button loc_mr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loc_mr = findViewById(R.id.mr);
        loc_hi = findViewById(R.id.hi);
        loc_en = findViewById(R.id.en);
        final String loc = Locale.getDefault().toString();

        loc_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale(loc);
            }
        });

        loc_hi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("hi");
            }
        });

        loc_mr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("mr");
            }
        });

    }
    protected void setLocale(String loc){
        Locale myLocale = new Locale(loc);
        Resources res = getResources();
        DisplayMetrics myDM = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, myDM);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();


    }
}
