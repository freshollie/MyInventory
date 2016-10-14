package uk.ac.coventry.bello.myvehicle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.VectorEnabledTintResources;
import android.view.View;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyVehicleActivity";
    private EditText editTextMake;
    private EditText editTextYear;
    private Vehicle mVehicle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextMake = (EditText)findViewById(R.id.makeInput);
                editTextYear = (EditText)findViewById(R.id.yearInput);

                String make = editTextMake.getText().toString();
                String strYear = editTextYear.getText().toString();

                if (strYear.matches("")){
                    mVehicle = new Vehicle(make);
                } else {
                    int intYear = Integer.parseInt(strYear);
                    mVehicle = new Vehicle(make, intYear);
                }

                Toast.makeText(getApplicationContext(), mVehicle.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "User clicked " + Vehicle.counter + " times.");
                Log.d(TAG, "User message is \"" + mVehicle.getMessage() + "\".");
            }
        });
    }
}
