package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LogActivity extends AppCompatActivity {
    public static final String TAG = "LogActivity_CLASS";
    public static final String EXTRA_LOG_ID = "edu.cvtc.capstone.vehiclemaintenancetracker.EXTRA_LOG_ID";

    public static int vehicleId;
    DBHelper dbHelper = new DBHelper(LogActivity.this);
    Toolbar toolbar;

    // An array of logs used to populate the RecyclerView
    ArrayList<MaintenanceLog> logArrayList;

    SearchView searchView;
    RecyclerView recyclerView;
    TextView totalsUp;

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

        // Reference the totalsUp (Total time and money) Text view
        totalsUp = findViewById(R.id.logActivity_totalsUp);

        populateRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateRecyclerView();
    }

    private void populateRecyclerView() {
        // Only pull logs if the vehicleId is valid exists (-1 means it does not exist)
        if (vehicleId != -1) {
            // Initialize the log list
            logArrayList = dbHelper.getAllLogsByVehicleId(vehicleId);

            // Calculate the total cost and time
            // for all logs. Then display it in a
            // TextView.
            calculateTotals();

            //Refresh the RecyclerView
            prepRecyclerView();

            // Only display the logs if there are logs, otherwise display the "you have none" text
            if (logArrayList.isEmpty()) {
                findViewById(R.id.noLogsTextView).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.noLogsTextView).setVisibility(View.INVISIBLE);
            }
        } else {
            Snackbar.make(toolbar, "The vehicle ID is not valid, please try again.", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    // Calculate the total amount of money
    // and minutes with every log entry,
    // then display it to the user.
    private void calculateTotals() {
        // Members
        double totalCost = 0.0;
        int totalTimeInMinutes = 0;

        // Looper
        for (MaintenanceLog log : logArrayList) {
            totalCost += log.getCost();
            totalTimeInMinutes += log.getTime();
        }

        // Display the stats
        totalsUp.setText(String.format("You spent $%.2f and %d minutes total in maintenance.", totalCost, totalTimeInMinutes));
    }

    // Prepare the RecyclerView and its Adapter with data
    private void prepRecyclerView() {
        // Find the RecyclerView view
        recyclerView = findViewById(R.id.recyclerView_logActivity);

        // Create a LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and make the RecyclerView use it
        recyclerView.setAdapter(new LogRecyclerAdapter(logArrayList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_log, menu);

        // Get a reference to the search view
        searchView = (SearchView) menu.findItem(R.id.menuItem_search).getActionView();
        searchView.setSearchableInfo(((SearchManager) getSystemService(Context.SEARCH_SERVICE))
                .getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Create a new array list to store the query results
                ArrayList<MaintenanceLog> filteredList;

                // Query
                filteredList = dbHelper.getAllLogsBySearchTerm(query, vehicleId);

                // If the filtered list is empty, display a message.
                if (filteredList.isEmpty()) {
                    Toast.makeText(LogActivity.this, "No maintenance logs found.", Toast.LENGTH_SHORT).show();
                }

                // Pass the list to the adapter to display
                recyclerView.setAdapter(new LogRecyclerAdapter(filteredList));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedItemId = item.getItemId();

        if (selectedItemId == R.id.menuItem_add) {
            // Go to LogSettingsActivity and send an id of -1 so it knows we're creating a new log
            Intent intent = new Intent(LogActivity.this, MaintenanceLogSettingsActivity.class);
            intent.putExtra(LogActivity.EXTRA_LOG_ID, -1);
            intent.putExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, vehicleId);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

class LogRecyclerAdapter extends RecyclerView.Adapter<LogRecyclerAdapter.ViewHolder> {

    // An array holding maintenance logs
    private ArrayList<MaintenanceLog> logArrayList;

    // Date formatter for the date TextView and time TextView
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, y", Locale.ENGLISH);

    // Constructor
    public LogRecyclerAdapter(ArrayList<MaintenanceLog> logArrayList) {
        this.logArrayList = logArrayList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

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

            // Set the Edit button's onClick Listener
            itemView.findViewById(R.id.card_logActivity_buttonEdit).setOnClickListener(
                    v -> {
                        // Go to LogSettingsActivity and pass the log's ID so it knows which log we're editing
                        Intent intent = new Intent(context, MaintenanceLogSettingsActivity.class);
                        intent.putExtra(LogActivity.EXTRA_LOG_ID, logID);
                        intent.putExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, LogActivity.vehicleId);
                        context.startActivity(intent);
                    }
            );
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
            if (log.getTime() == 1) {
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