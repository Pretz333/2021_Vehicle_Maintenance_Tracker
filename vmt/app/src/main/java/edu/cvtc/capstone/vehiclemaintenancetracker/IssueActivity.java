package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import java.util.List;
import java.util.Locale;

public class IssueActivity extends AppCompatActivity {

    public static final String EXTRA_ISSUE_ID = "edu.cvtc.capstone.vehiclemaintenancetracker.EXTRA_ISSUE_ID";
    @SuppressLint("StaticFieldLeak")
    public static Context context;

    // The request code of the child activity when an issue
    // is created.
    public static final int REQUEST_ADD_ISSUE = 420;

    // Member variables
    private int vehicleId;
    Toolbar toolbar;

    // A custom preference util used to set/get
    // a key-value pair. In this case, the amount
    // of issues a given vehicle has.
    PreferenceUtil preferenceUtil;

    // The adapter used by the RecyclerView.
    // It's used to notify the adapter when
    // the dataset has changed.
    IssueRecyclerAdapter issueRecyclerAdapter;

    // An array of logs used to populate the
    // RecyclerView
    ArrayList<Issue> issueArrayList;

    // Database helper
    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        context = this;

        // Initialize the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the custom preference util
        preferenceUtil = new PreferenceUtil(this);

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
        context = this;
        populateRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        context = null;
    }

    // Add a few fake issue logs to the logArrayList
    // to be used in the RecyclerView for demo
    private void populateRecyclerView() {

        // Only display issues if the vehicleId is valid!
        if (vehicleId != -1) {
            // We're good to go. Let's do some database stuff.

            // Grab all the issues from the database and throw them
            // into the issueArrayList
            issueArrayList = dbHelper.getAllIssuesByVehicleId(vehicleId, false);

            // Prepare the RecyclerView
            prepRecyclerView();

            // Using the custom preference util, set the key as the vehicleID
            // and the amount of issues this vehicle has equal to the amount of
            // currently open issues.
            preferenceUtil.setIssueCountByVehicleId(vehicleId, issueArrayList.size());

            // If there aren't any issues, notify the user!
            // TODO: Use a textView like William did, not this.
            //  But this works for now.
            if (issueArrayList.size() < 1) {
                Snackbar.make(toolbar, "You don't have any issues for this vehicle. To create one, tap the plus icon in the toolbar.",
                        Snackbar.LENGTH_LONG)
                        .show();
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
        issueRecyclerAdapter = new IssueRecyclerAdapter(issueArrayList);
        recyclerView.setAdapter(issueRecyclerAdapter);
    }

    public static void setIssueStatusToComplete(int issueId){
        if(context != null) {
            DBHelper dbHelper = new DBHelper(context);
            Issue issue = dbHelper.getIssueByIssueId(issueId);
            if (issue != null) {
                issue.setStatusId(dbHelper.getClosedIssueStatusId());
                dbHelper.updateIssue(issue);

                /*

                //Makes a toast and displays what the issue status is after the attempt to set it to closed
                List<IssueStatus> issueStatuses = dbHelper.getPossibleIssueStatuses();
                issue = dbHelper.getIssueByIssueId(issueId);
                String description = "None";
                for (int i = 0; i < issueStatuses.size(); i++) {
                    if (issueStatuses.get(i).getId() == issue.getStatusId()) {
                        description = issueStatuses.get(i).getDescription();
                        break;
                    }
                }
                Toast.makeText(context, "Set to: " + description, Toast.LENGTH_SHORT).show();

                 */

                if (context instanceof IssueActivity) {
                    ((IssueActivity) context).populateRecyclerView();
                }

            }
        }
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
                startActivityForResult(intent, REQUEST_ADD_ISSUE);
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

        private int issueId;

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

        // Event handler for the buttons
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.card_issueActivity_buttonEdit :
                    // Create the target intent
                    Intent intent = new Intent(context, IssueSettingsActivity.class);

                    // Extras include the ID of the issue
                    intent.putExtra(IssueActivity.EXTRA_ISSUE_ID, issueId);

                    // Start the activity
                    context.startActivity(intent);
                    Snackbar.make(title, "Edit button tapped on RecyclerView element: " + getLayoutPosition(), Snackbar.LENGTH_SHORT).show();
                    break;

                case R.id.card_issueActivity_buttonComplete :
                    IssueActivity.setIssueStatusToComplete(issueId);
                    //((View)v.getParent()).setVisibility(View.GONE); //TODO: Fix this
                    break;

                default:
                    // ...
            }
        }

        // One-hitter method for setting the data for all
        // the TextViews
        public void setDataByObject(Issue issue) {
            issueId = issue.getId();
            title.setText(issue.getTitle());
            description.setText(issue.getDescription());
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
