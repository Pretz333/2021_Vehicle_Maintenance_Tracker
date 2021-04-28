package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogActivity extends AppCompatActivity {

    // Member variables
    private int vehicleId;
    Toolbar toolbar;

    // An array of logs used to populate the
    // RecyclerView
    ArrayList<MaintenanceLog> logArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // Initialize the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for
        // reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogActivity.super.finish();
            }
        });

        // Grab the vehicleId from the received intent
        vehicleId = getIntent().getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // Only pull logs of the vehicleId is valid.
        // In other words, we can't get logs from a
        // vehicle that doesn't exist
        if (vehicleId != -1) {
            // Initialize the log list
            logArrayList = new ArrayList<>();

            // Generate logs as demo-data
            prepDemoData();

            // Prepare the RecyclerView
            prepRecyclerView();

        } else {
            // Not a valid id
            Snackbar.make(toolbar, String.format("The vehicle id of %s is not valid", vehicleId), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    // Add a few fake maintenance logs to the logArrayList
    // to be used in the RecyclerView for demo
    private void prepDemoData() {

        // Create objects
        // TODO: Replace with actual Database objects
        Time time = new Time(Calendar.getInstance().getTimeInMillis());

        MaintenanceLog log1 = new MaintenanceLog(0,
                "Replaced Something",
                "Im keeping this log here to remind myself that I replaced something but I dont know what it is",
                new Date(1619130376711L),
                48.00,
                time,
                180000,
                -1,
                -1
        );

        MaintenanceLog log2 = new MaintenanceLog(1,
                "Updated Lights",
                "Ive updated the headlight bulbs for whatever reason",
                new Date(1619130376711L),
                24.99,
                time,
                180000,
                -1,
                -1
        );

        MaintenanceLog log3 = new MaintenanceLog(2,
                "Tires Rotated",
                "All of the tires were rotated and it drives smoother now",
                new Date(1619130376711L),
                00.00,
                time,
                180000,
                -1,
                -1
        );

        // Add the objects to the list
        logArrayList.add(log1);
        logArrayList.add(log2);
        logArrayList.add(log3);
    }

    // Prepare the RecyclerView and its Adapter with data
    private void prepRecyclerView() {
        // Find the RecyclerView view
        RecyclerView recyclerView = findViewById(R.id.recyclerView_logActivity);

        // Create a LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and make the RecyclerView use it
        LogRecyclerAdapter logRecyclerAdapter = new LogRecyclerAdapter(logArrayList);
        recyclerView.setAdapter(logRecyclerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_log_filter:
                Snackbar.make(toolbar, "Filter button tapped", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.menuItem_log_search:
                Snackbar.make(toolbar, "Search button tapped", Snackbar.LENGTH_SHORT).show();
                break;

            default:
                // ...
        }
        return super.onOptionsItemSelected(item);
    }
}

// The adapter for this activity's RecyclerView
class LogRecyclerAdapter extends RecyclerView.Adapter<LogRecyclerAdapter.ViewHolder> {

    // An array holding maintenance logs
    final private ArrayList<MaintenanceLog> logArrayList;

    // Date formatter for the date TextView
    // and time TextView
    static SimpleDateFormat simpleDateFormat;
    static SimpleDateFormat simpleTimeFormat;

    // Constructor accepting an array
    public LogRecyclerAdapter(ArrayList<MaintenanceLog> logArrayList) {
        this.logArrayList = logArrayList;
        simpleDateFormat = new SimpleDateFormat("MMM d, y", Locale.ENGLISH);
        simpleTimeFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member variables
        Context context;

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
            Snackbar.make(title, "Edit button tapped on RecyclerView element: " + getLayoutPosition(), Snackbar.LENGTH_SHORT).show();
        }

        // One-hitter method for setting the data for all
        // the TextViews
        public void setDataByObject(MaintenanceLog log) {
            title.setText(log.getTitle());
            description.setText(log.getDescription());
            date.setText(simpleDateFormat.format(log.getDate()));
            cost.setText(context.getResources()
                    .getString(R.string.card_log_cost)
                    .concat(String.valueOf(log.getCost())));
            time.setText(context.getResources()
                    .getString(R.string.card_log_time)
                    .concat(" ") // Empty space
                    .concat(simpleTimeFormat.format(log.getTime())));
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