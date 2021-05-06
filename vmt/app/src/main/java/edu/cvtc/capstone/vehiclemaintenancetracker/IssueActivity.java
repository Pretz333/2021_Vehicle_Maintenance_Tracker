package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class IssueActivity extends AppCompatActivity {

    public static final String EXTRA_ISSUE_ID = "edu.cvtc.capstone.vehiclemaintenancetracker.EXTRA_ISSUE_ID";

    // Member variables
    private int vehicleId;
    Toolbar toolbar;

    // An array of logs used to populate the
    // RecyclerView
    ArrayList<Issue> issueArrayList;

    // Database helper
    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        // Initialize the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for
        // reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueActivity.super.finish();
            }
        });

        // Grab the vehicleId from the received intent
        vehicleId = getIntent().getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // Only pull logs of the vehicleId is valid.
        // In other words, we can't get logs from a
        // vehicle that doesn't exist
        if (vehicleId != -1) {
            // Initialize the log list
            issueArrayList = new ArrayList<>();

            // Generate logs as demo-data
            populateRecyclerView();

        } else {
            // Not a valid id
            Snackbar.make(toolbar, String.format("The vehicle id of %s is not valid", vehicleId), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateRecyclerView();
    }

    // Add a few fake issue logs to the logArrayList
    // to be used in the RecyclerView for demo
    private void populateRecyclerView() {

        // Only display issues if the vehicleId is valid!
        if (vehicleId != -1) {
            // We're good to go. Let's do some database stuff.

            // Grab all the issues from the database and throw them
            // into the issueArrayList
            issueArrayList = dbHelper.getAllIssuesByVehicleId(vehicleId);

            // Prepare the RecyclerView
            prepRecyclerView();

            // If there aren't any issues, notify the user!
            // TODO: Use a textView like William did, not this.
            //  But this works for now.
            if (issueArrayList.size() < 1) {
                Snackbar.make(toolbar, "You don't have any issues for this vehicle. To create one, tap the plus icon in the toolbar.", Snackbar.LENGTH_INDEFINITE).show();
            }
        } else {
            // Not a valid id
            Snackbar.make(toolbar, String.format("The vehicle id of %s is not valid", vehicleId), Snackbar.LENGTH_INDEFINITE).show();
        }

    }

    // Prepare the RecyclerView and its Adapter with data
    private void prepRecyclerView() {
        // Find the RecyclerView view
        RecyclerView recyclerView = findViewById(R.id.recyclerView_issueActivity);

        // Create a LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and make the RecyclerView use it
        IssueRecyclerAdapter IssueRecyclerAdapter = new IssueRecyclerAdapter(issueArrayList);
        recyclerView.setAdapter(IssueRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_addALog:
                Intent intent = new Intent(IssueActivity.this, IssueSettingsActivity.class);
                intent.putExtra(IssueActivity.EXTRA_ISSUE_ID, -1);
                intent.putExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, vehicleId);
                startActivity(intent);
                break;
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
class IssueRecyclerAdapter extends RecyclerView.Adapter<IssueRecyclerAdapter.ViewHolder> {

    // An array holding issue logs
    final private ArrayList<Issue> issueArrayList;

    // Date formatter for the date TextView
    static SimpleDateFormat simpleDateFormat;

    // Constructor accepting an array
    public IssueRecyclerAdapter(ArrayList<Issue> issueArrayList) {
        this.issueArrayList = issueArrayList;
        simpleDateFormat = new SimpleDateFormat("MMM d, y", Locale.ENGLISH);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member variables
        Context context;

        final TextView title;
        final TextView description;
        final TextView date;
        final TextView priority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            title = itemView.findViewById(R.id.card_issueActivity_title);
            description = itemView.findViewById(R.id.card_issueActivity_description);
            date = itemView.findViewById(R.id.card_issueActivity_date);
            priority = itemView.findViewById(R.id.card_issueActivity_priority);

            Button buttonEdit = itemView.findViewById(R.id.card_issueActivity_buttonEdit);
            Button buttonComplete = itemView.findViewById(R.id.card_issueActivity_buttonComplete);

            buttonEdit.setOnClickListener(this);
            buttonComplete.setOnClickListener(this);
        }

        // Event handler for the edit button
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.card_issueActivity_buttonEdit :
                        Snackbar.make(title, "Edit button tapped on RecyclerView element: " + getLayoutPosition(), Snackbar.LENGTH_SHORT).show();
                    break;

                case R.id.card_issueActivity_buttonComplete :
                        Snackbar.make(title, "Complete button tapped on RecyclerView element: " + getLayoutPosition(), Snackbar.LENGTH_SHORT).show();
                    break;

                default:
                    // ...
            }
        }

        // One-hitter method for setting the data for all
        // the TextViews
        public void setDataByObject(Issue issue) {
            title.setText(issue.getTitle());
            description.setText(issue.getDescription());
            //date.setText(simpleDateFormat.format(issue.getDate()));
            priority.setText(issue.getPriorityAsString());
        }
    }

    @NonNull
    @Override
    public IssueRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_issue, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueRecyclerAdapter.ViewHolder holder, int position) {
        // Grab an issue object at a given position
        Issue issue = issueArrayList.get(position);
        // Set the view holder data based upon the object
        holder.setDataByObject(issue);
    }

    @Override
    public int getItemCount() {
        return issueArrayList.size();
    }
}


/*
    The issue object that represents a single issue
 */
class Issue {
    public static final String TAG = "ISSUE_CLASS";

    private int id;
    private String title;
    private String description;
    private int priority; //TODO: Make this a FK?
    private int vehicleId;
    private int statusId;

    public Issue() {
        // ...
    }

    //Constructors
    //Minimum
    public Issue(String title, int vehicleId, int statusId) {
        this.id = -1;
        setTitle(title);
        this.priority = 0;
        setVehicleId(vehicleId);
        setStatusId(statusId);
    }

    //Everything but the id and priority
    public Issue(String title, String description, int vehicleId, int statusId) {
        this.id = -1;
        setTitle(title);
        setDescription(description);
        this.priority = 0;
        setVehicleId(vehicleId);
        setStatusId(statusId);
    }

    //Everything but the id
    public Issue(String title, String description, int priority, int vehicleId, int statusId) {
        this.id = -1;
        setTitle(title);
        setDescription(description);
        setPriority(priority);
        setVehicleId(vehicleId);
        setStatusId(statusId);
    }

    //Everything, for use when reading from the database. Since it's in the database, it's already passed verification
    public Issue(int id, String title, String description, int priority, int vehicleId, int statusId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.vehicleId = vehicleId;
        this.statusId = statusId;
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(VerifyUtil.isStringSafe(title)) {
            this.title = title;
        } else {
            Log.w(TAG, "Issue title unsafe");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(VerifyUtil.isTextSafe(description)) {
            this.description = description;
        } else {
            Log.w(TAG, "Issue description unsafe");
        }
    }

    public int getPriority() {
        return priority;
    }

    public String getPriorityAsString() {
        switch(priority) {
            case 0 :
                return "High Priority";
            case 1 :
                return "Medium Priority";
            case 2 :
                return "Low Priority";
            default :
                return "No Priority";
        }
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        //DBHelper dbHelper = new DBHelper(null);
        //if(dbHelper.checkIfVehicleIdExists(vehicleId)) {
        this.vehicleId = vehicleId;
        //} else {
        //   Log.w(TAG, "VehicleId did not exist");
        //}
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        //DBHelper dbHelper = new DBHelper(null);
        //if(dbHelper.checkIfIssueStatusIdExists(statusId)) {
        this.statusId = statusId;
        //} else {
        //    Log.w(TAG, "StatusId did not exist");
        //}
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Issue ID:").append(id)
                .append("\nTitle:").append(title)
                .append("\nDescription:").append(description);
        return builder.toString();
    }
}

/*
    The issueStatus object that represents the status of an issue.
    e.g. complete, not complete, etc...
 */
class IssueStatus {
    public static final String TAG = "ISSUESTATUS_CLASS";

    private int id;
    private String description;

    //Pre-database insert
    public IssueStatus(String description) {
        this.id = -1;
        setDescription(description);
    }

    //For use when reading from the database. Since it's in the database, it's already passed verification
    public IssueStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(VerifyUtil.isStringLettersOnly(description)) {
            this.description = description;
        } else {
            Log.w(TAG, "Description unsafe");
        }
    }
}