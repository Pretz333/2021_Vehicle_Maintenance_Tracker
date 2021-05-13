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

    // The request code of the child activity when an issue is created.
    public static final int REQUEST_ADD_ISSUE = 420;

    // Member variables
    private int vehicleId;
    Toolbar toolbar;
    public static boolean viewingClosed = false;
    TextView noIssues; //Used to display a message when there are no issues to be displayed

    // A custom preference util used to set/get a key-value pair.
    // In this case, the amount of issues a given vehicle has.
    PreferenceUtil preferenceUtil;

    // The adapter used by the RecyclerView.
    // It's used to notify the adapter when the data set has changed.
    IssueRecyclerAdapter issueRecyclerAdapter;

    // An array of logs used to populate the RecyclerView
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

        // Back button, better than the Manifest way for reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> IssueActivity.super.finish());

        // Grab the vehicleId from the received intent
        vehicleId = getIntent().getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // Find the noIssues TextView
        noIssues = findViewById(R.id.noIssuesTextView);

        // Only pull logs of the vehicleId is valid.
        // In other words, we can't get logs from a vehicle that doesn't exist
        if (vehicleId != -1) {
            // Initialize the list
            issueArrayList = new ArrayList<>();

            // Populate the list
            populateRecyclerView(viewingClosed);

        } else {
            // Not a valid id
            Snackbar.make(toolbar, String.format("The vehicle id of %s is not valid", vehicleId), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        context = this;
        populateRecyclerView(viewingClosed);
    }

    @Override
    protected void onPause() {
        super.onPause();
        context = null;
    }

    // Populate the issue list
    private void populateRecyclerView(boolean viewingClosed) {

        // Only display issues if the vehicleId is valid!
        if (vehicleId != -1) {
            // We're good to go. Let's do some database stuff.

            // Grab all the issues from the database and throw them into the issueArrayList
            issueArrayList = dbHelper.getAllIssuesByVehicleId(vehicleId, viewingClosed);

            // Prepare the RecyclerView
            prepRecyclerView();

            // Using the custom preference util, set the key as the vehicleID
            // and the amount of issues this vehicle has equal to the amount of
            // currently open issues.
            if(!viewingClosed) {
                preferenceUtil.setIssueCountByVehicleId(vehicleId, issueArrayList.size());
            }

            // If there aren't any issues, notify the user!
            if (issueArrayList.size() < 1) {
                noIssues.setVisibility(View.VISIBLE);

                if(viewingClosed) {
                    noIssues.setText(getResources().getString(R.string.no_closed_issues));
                } else {
                    noIssues.setText(getResources().getString(R.string.no_open_issues));
                }
            } else {
                noIssues.setVisibility(View.GONE);
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

    public static void setIssueStatus(int issueId, boolean complete){
        if(context != null) {
            DBHelper dbHelper = new DBHelper(context);
            Issue issue = dbHelper.getIssueByIssueId(issueId);
            if (issue != null) {
                if(complete) {
                    issue.setStatusId(dbHelper.getClosedIssueStatusId());
                } else {
                    issue.setStatusId(dbHelper.getOpenIssueStatusId());
                }

                dbHelper.updateIssue(issue);

                /*

                //Makes a toast and displays what the issue status is after the attempt to set it
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
                    ((IssueActivity) context).populateRecyclerView(viewingClosed);
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
                viewingClosed = !viewingClosed;
                populateRecyclerView(viewingClosed);
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

    // Constructor accepting an array
    public IssueRecyclerAdapter(ArrayList<Issue> issueArrayList) {
        this.issueArrayList = issueArrayList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member variables
        Context context;

        private int issueId;

        final TextView title;
        final TextView description;
        final TextView priority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            title = itemView.findViewById(R.id.card_issueActivity_title);
            description = itemView.findViewById(R.id.card_issueActivity_description);
            priority = itemView.findViewById(R.id.card_issueActivity_priority);

            Button buttonEdit = itemView.findViewById(R.id.card_issueActivity_buttonEdit);
            Button buttonComplete = itemView.findViewById(R.id.card_issueActivity_buttonComplete);

            buttonEdit.setOnClickListener(this);
            buttonComplete.setOnClickListener(this);

            if(IssueActivity.viewingClosed) {
                buttonComplete.setText(R.string.card_issue_buttonReopen);
            }
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
                    break;

                case R.id.card_issueActivity_buttonComplete :
                    if(IssueActivity.viewingClosed) {
                        IssueActivity.setIssueStatus(issueId, false);
                    } else {
                        IssueActivity.setIssueStatus(issueId, true);
                    }
                    break;

                default:
                    // ...
            }
        }

        // One-hitter method for setting the data for all the TextViews
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
