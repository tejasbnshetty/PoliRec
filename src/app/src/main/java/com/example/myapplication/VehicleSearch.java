package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.myapplication.data.model.AVLTreeManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VehicleSearch extends AppCompatActivity {

    private EditText searchInput;
    private Button searchButton;
    private LinearLayout resultsContainer;

    TextView searchHint, searchHint2, searchingText, countTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_search);
        //UI Connection
        searchInput = findViewById(R.id.advanced_search_input);
        searchButton = findViewById(R.id.advanced_search_button);
        resultsContainer = findViewById(R.id.results_container);
        searchHint = findViewById(R.id.search_hint_text);
        searchHint2 = findViewById(R.id.search_hint_text2);
        searchingText = findViewById(R.id.searching_text);
        countTextView = findViewById(R.id.search_result_count);


        //listener
        searchButton.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String input = searchInput.getText().toString().trim();

        if(input.equalsIgnoreCase("") || input == null) {
            searchInput.setError("Nothing has been entered");//error if empty. catch it before entering search. Saves time
            searchingText.setVisibility(View.GONE);
            countTextView.setVisibility(View.GONE);
        }
        else{
            Map<String, String> filters = tokenizeInput(input);
            if (filters == null) {
                searchingText.setVisibility(View.GONE);
                countTextView.setVisibility(View.GONE);
                return; // no search
            }

            SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
            String username = prefs.getString("USERNAME", "");
            boolean isAdmin = prefs.getBoolean("IS_ADMIN", false);

            List<HashMap<String, Object>> results = AVLTreeManager.getInstance()
                    .searchByFilters(isAdmin ? null : username, filters); //reach for all the entries

            int numberOfEntries = results != null ? results.size() : 0;

            //UI feedback
            searchingText.setVisibility(View.VISIBLE);
            countTextView.setVisibility(View.GONE);

            resultsContainer.removeAllViews();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                List<View> cards = new ArrayList<>();
                for (HashMap<String, Object> vehicle : results) {
                    View card = inflateVehicleCard(vehicle); // Still runs on main thread — may need partial offloading
                    cards.add(card);
                }

                handler.post(() -> {
                    for (View card : cards) {
                        resultsContainer.addView(card);//add details when found
                    }
                    if (numberOfEntries > 0) {
                        countTextView.setText("Results: " + numberOfEntries);//number of entries
                        countTextView.setVisibility(View.VISIBLE);
                    } else {
                        searchInput.setError("No matching vehicles found");
                    }
                    searchingText.setVisibility(View.GONE);
                });
            });
        }
    }

    private static final Set<String> VALID_KEYS = new HashSet<>(Arrays.asList("make", "model", "type", "fuel", "transmission"));

    //Token and grammar for the search(not case sensitive)
    private Map<String, String> tokenizeInput(String input) {
        Map<String, String> filters = new HashMap<>();

        // Regular expression: key:value or key:"multi word value"
        Pattern pattern = Pattern.compile("(\\w+):\"([^\"]+)\"|(\\w+):(\\S+)");
        Matcher matcher = pattern.matcher(input);


        boolean foundValid = false;

        while (matcher.find()) {
            String key = matcher.group(1) != null ? matcher.group(1).toLowerCase() : matcher.group(3).toLowerCase();
            String value = matcher.group(2) != null ? matcher.group(2).toLowerCase() : matcher.group(4).toLowerCase();

            if (VALID_KEYS.contains(key)) {
                filters.put(key, value);
                foundValid = true;
            } else {
                searchInput.setError("Unknown filter: " + key);
            }
        }

        if (!foundValid) {
            searchInput.setError("Please use valid filters like make:Toyota or model:Corolla");
            return null;
        }

        return filters;

    }


    private View inflateVehicleCard(HashMap<String, Object> vehicle) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View vehicleCard = inflater.inflate(R.layout.vehicle_card, resultsContainer, false);

        //vehicle card mapping
        TextView regoNumber = vehicleCard.findViewById(R.id.rego_number);
        TextView vehicleMake = vehicleCard.findViewById(R.id.vehicle_make);
        TextView vehicleType = vehicleCard.findViewById(R.id.vehicle_type);
        TextView registeredOn = vehicleCard.findViewById(R.id.registered_on);
        TextView registrationStatus = vehicleCard.findViewById(R.id.registration_status_textview);
        TextView model = vehicleCard.findViewById(R.id.vehicle_model);
        TextView vehicleFuel = vehicleCard.findViewById(R.id.vehicle_fuel);
        TextView vehicleTransmission = vehicleCard.findViewById(R.id.vehicle_transmission);

        regoNumber.setText("Rego: " + vehicle.get("rego_num"));
        vehicleMake.setText("Make: " + vehicle.get("make"));
        vehicleType.setText("Type: " + vehicle.get("type"));
        registeredOn.setText("Registered On: " + vehicle.get("registered_on"));
        model.setText("Model: "+ vehicle.get("model"));
        vehicleFuel.setText("Fuel: "+vehicle.get("fuel"));
        vehicleTransmission.setText("Transmission: "+vehicle.get("transmission"));

        //calculate if expired or not
        String registeredOnString = vehicle.get("registered_on").toString();
        int registrationPeriodInMonths = 12;//assumed to be 12 months of registration for all vehicles

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
                registrationStatus.setText("Registration expires in " + daysLeft + " days.");//not expired
            } else {
                registrationStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                registrationStatus.setText("Registration expired " + (-daysLeft) + " days ago.");//expired
            }

        } catch (ParseException e) {
            e.printStackTrace();
            registrationStatus.setText("Registration date invalid.");
        }

        return vehicleCard;
    }
}
