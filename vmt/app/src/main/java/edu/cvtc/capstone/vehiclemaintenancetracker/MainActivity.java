package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MAINACTIVITY_CLASS";

    // An array containing each vehicle object for use by the RecyclerView.
    private ArrayList<Vehicle> vehicleArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the toolbar
        setSupportActionBar(findViewById(R.id.toolbar));

        populateRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateRecyclerView();
    }

    private void populateRecyclerView() {
        // Get all saved vehicles
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        vehicleArrayList = dbHelper.getAllVehicles();

        // Fill the RecyclerView and its Adapter with some data.
        prepRecyclerView();

        // Display or hide the "You have no vehicles" message
        if (vehicleArrayList.isEmpty()) {
            findViewById(R.id.noVehiclesTextView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.noVehiclesTextView).setVisibility(View.INVISIBLE);
        }
    }

    // Initializes the RecyclerView and using the data within the vehicleArrayList DataSet.
    private void prepRecyclerView() {

        // Find the RecyclerView view
        RecyclerView recyclerView = findViewById(R.id.recyclerView_mainActivity);

        // Create a LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and make the RecyclerView use it
        recyclerView.setAdapter(new VehicleRecyclerAdapter(vehicleArrayList));

    }

    // Rename the night mode toggle in the options menu based on if it is in night or light mode
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vehicle_menu, menu);
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            menu.findItem(R.id.night_mode).setTitle(R.string.day_mode);
        } else {
            menu.findItem(R.id.night_mode).setTitle(R.string.night_mode);
        }
        return true;
    }

    // Has the night/light mode toggle and the add a vehicle button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.night_mode) {
            // Change the theme and the text of the menu item depending on which mode we're on
            int nightMode = AppCompatDelegate.getDefaultNightMode();
            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                item.setTitle(R.string.night_mode);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                item.setTitle(R.string.day_mode);
            }
            recreate();
        } else if (item.getItemId() == R.id.addAVehicle) {
            // Go to the vehicle settings activity and send an id
            // of -1 so it knows we're creating a new vehicle
            Intent intent = new Intent(MainActivity.this, VehicleSettingsActivity.class);
            intent.putExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);
            startActivity(intent);
        }
        return true;
    }

}

// A RecyclerView adapter for each vehicle displayed. Although we could use a more simple
// approach such as ArrayAdapter using a ListView, the layout for displaying the vehicles
// is costly and contains lots of children. So for efficiency, we will use RecyclerView.

class VehicleRecyclerAdapter extends RecyclerView.Adapter<VehicleRecyclerAdapter.ViewHolder> {

    // The DataSet that contains the vehicle objects
    public ArrayList<Vehicle> vehicleDataSet;

    // Default constructor to receive vehicle data
    public VehicleRecyclerAdapter(ArrayList<Vehicle> vehicleDataSet) {
        this.vehicleDataSet = vehicleDataSet;
    }

    // Provide a reference to the type of views that the RecyclerView adapter will interact with.
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context; // Context of super class
        private PreferenceUtil preferenceUtil;
        private int vehicleID;

        // TextView members of the card_vehicle.xml layout file.
        private final TextView nickname;
        private final TextView makeAndModel;
        private final TextView colorAndYear;
        private final TextView plateNumber;
        private final TextView issueDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Context used mainly for OnClickListener
            context = itemView.getContext();

            // TODO: Maybe this shouldn't be re-created multiple times, just sayin' - Alex
            // Initialize the preference util
            preferenceUtil = new PreferenceUtil(context);

            // Find the views within the itemView layout and set them to the member variables above.
            nickname = itemView.findViewById(R.id.card_mainActivity_nickname);
            makeAndModel = itemView.findViewById(R.id.card_mainActivity_vehicleMakeAndModel);
            colorAndYear = itemView.findViewById(R.id.card_mainActivity_vehicleColorAndYear);
            plateNumber = itemView.findViewById(R.id.card_mainActivity_vehiclePlateNumber);
            issueDescription = itemView.findViewById(R.id.card_mainActivity_issueDescription);

            // onClick Listener for button
            itemView.findViewById(R.id.card_mainActivity_dummyButton).setOnClickListener(this);
        }

        // Click listener for the card
        @Override
        public void onClick(View v) {
            // Go to VehicleOptionActivity and pass the vehicle ID
            Intent intent = new Intent(context, VehicleOptionActivity.class);
            intent.putExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, vehicleID);
            context.startActivity(intent);
        }

        // This is used to set the values for all views
        // This is called by the parent class's onBindViewHolder method.
        public void setData(int vehicleID, String nickname, String makeAndModel, String colorAndYear, String plateNumber) {

            this.vehicleID = vehicleID;
            this.nickname.setText(nickname);
            this.makeAndModel.setText(makeAndModel);
            this.colorAndYear.setText(colorAndYear);
            this.plateNumber.setText(plateNumber);

            // Default issue string
            String issueString = context.getResources().getString(R.string.card_mainActivity_recyclerView_noIssues);

            // Get the amount of issues this specific vehicle has
            int issueCount = preferenceUtil.getIssueCountByVehicleId(vehicleID);

            // If this vehicle has any issues, set it. If not, display the default issue string.
            if (issueCount == 1) {
                this.issueDescription.setText(String.format("%s %s", issueCount, context.getResources().getString(R.string.card_mainActivity_recyclerView_withIssue)));
            } else if (issueCount > 1) {
                this.issueDescription.setText(String.format("%s %s", issueCount, context.getResources().getString(R.string.card_mainActivity_recyclerView_withIssues)));
            } else {
                this.issueDescription.setText(issueString);
            }
        }

    }

    @NonNull
    @Override
    public VehicleRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view layout and attach a ViewHolder to it
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_vehicle, parent, false));
    }

    // Set each view with the correct data
    @Override
    public void onBindViewHolder(@NonNull VehicleRecyclerAdapter.ViewHolder holder, int position) {
        // Grab the vehicle at the specified position
        Vehicle v = vehicleDataSet.get(position);

        // Set the view holder data based on the vehicle above
        // Note: Make & Model, along with Color & Year are a SINGLE TextView view
        // This was done to save stress on the RecyclerView when populating,
        // due to how many views we already have for the vehicle card.
        holder.setData(v.getId(), v.getName(), v.getMake() + " " + v.getModel(),
                v.getColor() + ", " + v.getYear(), v.getLicensePlate());
    }

    @Override
    public int getItemCount() {
        return vehicleDataSet.size();
    }
}