package com.example.edutekzilaproject;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements SensorEventListener {

    //Declaration of all Variables...
    private SensorManager sensorManager;
    private TextView xAccelerometer , yAccelerometer;
    private ImageView smallCircle;
    private Button recordData;

    //Declaration and Initiations of all Variables for dataBase...
    private FirebaseFirestore db ;
    private CollectionReference coordinate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization of all variables...
        xAccelerometer = findViewById(R.id.xAccelerometer);
        yAccelerometer = findViewById(R.id.yAccelerometer);
        smallCircle = findViewById(R.id.small_circle);
        recordData = findViewById(R.id.recordData);

        db = FirebaseFirestore.getInstance();
        coordinate = db.collection("Coordinates");

        // Assigning SensorManager to it's corresponding variables...
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Setting onClickListener on recordData variable...
        recordData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Assigning the intent from MainActivity to DataBaseRecord Activity...
                Intent switchActivity = new Intent( MainActivity.this , DataBaseRecord.class);

                //starting the activity...
                startActivity(switchActivity);
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        //Checking for the Accelerometer in the device...
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            //Getting the Values of x and y co-ordinates from sensor...
            float[] values = event.values;
            float x = values[0];
            float y = values[1];

            //Assigning the of co-ordinates to its corresponding TextView...
            yAccelerometer.setText("Y-axis="+y+"");
            xAccelerometer.setText("X-axis="+x+"");

            // Getting the current co-ordinates of the bubble...
            final float getx=smallCircle.getX();
            final float gety=smallCircle.getY();

            //changing the co-ordinates of bubble according to its current value and the which we get from sensor...
            smallCircle.setX(getx-x);
            smallCircle.setY(gety-y);

            //Storing the values of Coordinate in the Map...
            Map<String, Object> value = new HashMap<>();
            value.put("X-Axis", x);
            value.put("Y-Axis", y);

//           Adding that map values in the FireBase DataBase...
            coordinate
                    .add(value)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        else
        {
            // If device don't have accelerometer then showing the toast message to the user...
            Toast.makeText(this, "Oops Accelerometer Not Found...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();

        // Informing the sensorManager that we are using Accelerometer...
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Removing the sensorManager from it's sensor
        sensorManager.unregisterListener(this);
    }

}