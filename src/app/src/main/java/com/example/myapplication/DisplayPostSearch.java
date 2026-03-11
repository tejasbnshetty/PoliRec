package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class DisplayPostSearch extends AppCompatActivity {

    private LinearLayout vehicleContainer;
    private TextView userInfoHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post_search);

        //map UI
        vehicleContainer = findViewById(R.id.vehicle_container);
        userInfoHeader = findViewById(R.id.user_info_header);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name") + "'s Vehicles";
        userInfoHeader.setText(name);

        //user info from extra of intent - only for user
        HashMap<String, Object> vehicleInfo = (HashMap<String, Object>) intent.getSerializableExtra("vehicleInfo");

        if (vehicleInfo != null) {
            displaySingleVehicleCard(vehicleInfo);
            return;
        }

        // If no vehicleInfo, fall back to admin multi-vehicle flow
        HashMap<String, Object> userInfo = (HashMap<String, Object>) intent.getSerializableExtra("userInfo");
        if (userInfo != null) {
            displayVehicleCards(userInfo);
        } else {
            showErrorMessage();
        }
    }

    //display cards based on intent info, by inflating vehicle acrds
    private void displayVehicleCards(HashMap<String, Object> userInfo) {
        if (userInfo.containsKey("vehicles")) {
            HashMap<String, Object> vehicles = (HashMap<String, Object>) userInfo.get("vehicles");
            for (String regoNum : vehicles.keySet()) {
                HashMap<String, Object> vehicle = (HashMap<String, Object>) vehicles.get(regoNum);
                displayCard(vehicle);
            }
        } else {
            showNoVehiclesMessage();
        }
    }

    //just for search by rego - user log in
    private void displaySingleVehicleCard(HashMap<String, Object> vehicleInfo) {
        displayCard(vehicleInfo);
    }

    //Reusable function to inflate and show one vehicle card
    private void displayCard(HashMap<String, Object> vehicle) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View vehicleCard = inflater.inflate(R.layout.vehicle_card, vehicleContainer, false);

        //map Vehicle Card UI
        TextView regoNumber = vehicleCard.findViewById(R.id.rego_number);
        TextView vehicleMake = vehicleCard.findViewById(R.id.vehicle_make);
        TextView vehicleType = vehicleCard.findViewById(R.id.vehicle_type);
        TextView registeredOn = vehicleCard.findViewById(R.id.registered_on);
        TextView registrationStatus = vehicleCard.findViewById(R.id.registration_status_textview);
        TextView vehicleModel = vehicleCard.findViewById(R.id.vehicle_model);
        TextView vehicleFuel = vehicleCard.findViewById(R.id.vehicle_fuel);
        TextView vehicleTransmission = vehicleCard.findViewById(R.id.vehicle_transmission);

        //data for card
        regoNumber.setText("Rego: " + vehicle.get("rego_num"));
        vehicleMake.setText("Make: " + vehicle.get("make"));
        vehicleType.setText("Type: " + vehicle.get("type"));
        registeredOn.setText("Registered On: " + vehicle.get("registered_on"));
        vehicleModel.setText("Model: "+ vehicle.get("model"));
        vehicleFuel.setText("Fuel: "+vehicle.get("fuel"));
        vehicleTransmission.setText("Transmission: "+vehicle.get("transmission"));

        //calculate the days left for renewal
        String registeredOnString = vehicle.get("registered_on").toString();
        int registrationPeriodInMonths = 12;//assumed as 12 months for all vehicles

        if (vehicle.containsKey("registration_period")) {
            registrationPeriodInMonths = Integer.parseInt(vehicle.get("registration_period").toString());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            Date registeredDate = sdf.parse(registeredOnString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(registeredDate);
            cal.add(Calendar.MONTH, registrationPeriodInMonths);
            Date expiryDate = cal.getTime();

            Date today = new Date();
            long diffInMillies = expiryDate.getTime() - today.getTime();
            long daysLeft = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (daysLeft >= 0) {
                registrationStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                registrationStatus.setText("Registration expires in " + daysLeft + " days.");//if not expired yet
            } else {
                registrationStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                registrationStatus.setText("Registration expired " + (-daysLeft) + " days ago.");//if already expired
            }

        } catch (ParseException e) {
            e.printStackTrace();
            registrationStatus.setText("Registration date invalid.");//error in date format(unlikely)
        }

        vehicleContainer.addView(vehicleCard);
    }


    //methods error handling for no vehicles(unlikely) or error in obtaining data
    private void showNoVehiclesMessage() {
        TextView noVehicleText = new TextView(this);
        noVehicleText.setText("No vehicles found.");
        noVehicleText.setTextSize(18);
        noVehicleText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        vehicleContainer.addView(noVehicleText);
    }

    private void showErrorMessage() {
        userInfoHeader.setText("Error: No user data received.");
        TextView errorText = new TextView(this);
        errorText.setText("Something went wrong loading the user info.");
        errorText.setTextSize(18);
        errorText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        vehicleContainer.addView(errorText);
    }
}