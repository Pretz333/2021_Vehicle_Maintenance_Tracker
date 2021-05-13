package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogActivity extends AppCompatActivity {
    public static final String TAG = "LogActivity_CLASS";
    public static final String EXTRA_LOG_ID = "edu.cvtc.capstone.vehiclemaintenancetracker.EXTRA_LOG_ID";

    int vehicleId;
    DBHelper dbHelper = new DBHelper(LogActivity.this);
    Toolbar toolbar;

    // An array of logs used to populate the RecyclerView
    ArrayList<MaintenanceLog> logArrayList;

    LogRecyclerAdapter logRecyclerAdapter;
    SearchView searchView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // Initialize the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> LogActivity.super.finish());

        // Grab the vehicleId from the received intent
        vehicleId = getIntent().getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        populateRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateRecyclerView();
    }

    private void populateRecyclerView(){
        // Only pull logs if the vehicleId is valid.
        // In other words, we don't want to get logs from a vehicle that doesn't exist
        if (vehicleId != -1) {
            // Initialize the log list
            logArrayList = dbHelper.getAllLogsByVehicleId(vehicleId);

            //Refresh the RecyclerView
            prepRecyclerView();

            //Only display the logs if there are logs, otherwise we'll display the "you have none" text
            if(logArrayList.isEmpty()){
                findViewById(R.id.noLogsTextView).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.noLogsTextView).setVisibility(View.INVISIBLE);
            }
        } else {
            // Not a valid id
            Snackbar.make(toolbar, String.format("The vehicle id of %s is not valid", vehicleId), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    // Prepare the RecyclerView and its Adapter with data
    private void prepRecyclerView() {
        // Find the RecyclerView view
        recyclerView = findViewById(R.id.recyclerView_logActivity);

        // Create a LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and make the RecyclerView use it
        logRecyclerAdapter = new LogRecyclerAdapter(logArrayList);
        recyclerView.setAdapter(logRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_log, menu);

        // Get a reference to the search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menuItem_log_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // TODO: Set the text color to white in the search field


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Search: " + query);
                // Call a method to filter the RecyclerView
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void filter(String searchText) {
        // Create a new array list to filter the data
        ArrayList<MaintenanceLog> filteredList;

        filteredList = dbHelper.getAllLogsBySearchTerm(searchText, vehicleId);

        // If the filtered list is empty, display a message.
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No maintenance logs found.", Toast.LENGTH_SHORT).show();
        }

        // Pass the list to the adapter.
        logRecyclerAdapter = new LogRecyclerAdapter(filteredList);
        recyclerView.setAdapter(logRecyclerAdapter);
        logRecyclerAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedItemId = item.getItemId();

        if (selectedItemId == R.id.menuItem_log_filter) {
            Snackbar.make(toolbar, "Filter button tapped", Snackbar.LENGTH_SHORT).show();
        } else if (selectedItemId == R.id.menuItem_log_search) {
            Snackbar.make(toolbar, "Search button tapped", Snackbar.LENGTH_SHORT).show();
        } else if (selectedItemId == R.id.menuItem_addALog) {
            // Go to the log settings activity and send an id of -1 so it knows we're creating a new log
            Intent intent = new Intent(LogActivity.this, MaintenanceLogSettingsActivity.class);
            intent.putExtra(LogActivity.EXTRA_LOG_ID, -1);
            intent.putExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, vehicleId);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

// The adapter for this activity's RecyclerView
class LogRecyclerAdapter extends RecyclerView.Adapter<LogRecyclerAdapter.ViewHolder> {

    // An array holding maintenance logs
    private ArrayList<MaintenanceLog> logArrayList;

    // Date formatter for the date TextView and time TextView
    static SimpleDateFormat simpleDateFormat;

    // Constructor accepting an array
    public LogRecyclerAdapter(ArrayList<MaintenanceLog> logArrayList) {
        this.logArrayList = logArrayList;
        simpleDateFormat = new SimpleDateFormat("MMM d, y", Locale.ENGLISH);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Member variables
        Context context;

        private int logID;

        final TextView title;
        final TextView description;
        final TextView date;
        final TextView cost;
        final TextView time;
        final TextView mileage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            title = itemView.findViewById(R.id.card_logActivity_title);
            description = itemView.findViewById(R.id.card_logActivity_description);
            date = itemView.findViewById(R.id.card_logActivity_date);
            cost = itemView.findViewById(R.id.card_logActivity_cost);
            time = itemView.findViewById(R.id.card_logActivity_time);
            mileage = itemView.findViewById(R.id.card_logActivity_mileage);

            Button buttonEdit = itemView.findViewById(R.id.card_logActivity_buttonEdit);
            buttonEdit.setOnClickListener(this);
        }

        // Event handler for the edit button
        @Override
        public void onClick(View v) {
            // Create the target intent
            Intent intent = new Intent(context, MaintenanceLogSettingsActivity.class);

            // Extras include the ID of the vehicle and the Nickname
            intent.putExtra(LogActivity.EXTRA_LOG_ID, logID);

            // Start the activity
            context.startActivity(intent);
        }

        // One-hitter method for setting the data for all the TextViews
        public void setDataByObject(MaintenanceLog log) {
            this.logID = log.getId();
            title.setText(log.getTitle());
            description.setText(log.getDescription());
            date.setText(simpleDateFormat.format(log.getDate()));
            cost.setText(context.getResources()
                    .getString(R.string.card_log_cost)
                    .concat(String.format(Locale.US, "%.2f", log.getCost())));
            if(log.getTime() == 1){
                time.setText(context.getResources().getString(R.string.card_log_single_time));
            } else {
                time.setText(context.getResources().getString(R.string.card_log_time, String.valueOf(log.getTime())));
            }
            mileage.setText(String.format(context.getResources()
                    .getString(R.string.card_log_mileage) + " %s", log.getMileage()));
        }
    }

    @NonNull
    @Override
    public LogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_log, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LogRecyclerAdapter.ViewHolder holder, int position) {
        // Grab a log object at a given position
        MaintenanceLog maintenanceLog = logArrayList.get(position);
        // Set the view holder data based upon the object
        holder.setDataByObject(maintenanceLog);
    }

    @Override
    public int getItemCount() {
        return logArrayList.size();
    }
}