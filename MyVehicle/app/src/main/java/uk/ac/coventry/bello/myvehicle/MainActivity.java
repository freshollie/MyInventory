package uk.ac.coventry.bello.myvehicle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyVehicleActivity";
    private EditText editTextMake;
    private EditText editTextYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate");
    }

    public void onButtonClick(View view){
        Log.d(TAG, "test");
    }
}
