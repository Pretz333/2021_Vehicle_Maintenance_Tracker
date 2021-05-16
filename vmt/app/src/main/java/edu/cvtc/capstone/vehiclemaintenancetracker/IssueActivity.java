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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class IssueActivity extends AppCompatActivity {
    public static final String TAG = "IssueActivity_CLASS";
    public static final String EXTRA_ISSUE_ID = "edu.cvtc.capstone.vehiclemaintenancetracker.EXTRA_ISSUE_ID";
    public static boolean viewingClosed = false;

    // Member variables
    DBHelper dbHelper = new DBHelper(this);
    int vehicleId;
    Toolbar toolbar;
    TextView noIssues;
    RecyclerView recyclerView;
    PreferenceUtil preferenceUtil;

    // An array of logs used to populate the RecyclerView
    ArrayList<Issue> issueArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        // Initialize the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> IssueActivity.super.finish());

        // Initialize the custom preference util
        preferenceUtil = new PreferenceUtil(this);

        // Grab the vehicleId from the received intent
        vehicleId = getIntent().getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // Find the noIssues TextView
        noIssues = findViewById(R.id.noIssuesTextView);

        // Populate the RecyclerView
        populateRecyclerView(viewingClosed);

    }

    @Override
    protected void onResume() {
        super.onResume();
        populateRecyclerView(viewingClosed);
    }

    // Populate the issue list
    private void populateRecyclerView(boolean viewingClosed) {
        // Only pull logs if the vehicleId is valid exists (-1 means it does not exist)
        if (vehicleId != -1) {

            // Grab all the issues from the database and throw them into the issueArrayList
            issueArrayList = dbHelper.getAllIssuesByVehicleId(vehicleId, viewingClosed);

            // Prepare the RecyclerView
            prepRecyclerView();

            // Using the custom preference util, set the key as the vehicleID and the amount
            // of issues this vehicle has equal to the amount of currently open issues.
            // Only do this if viewing the open issues, otherwise it will say the vehicle
            // has X amount of issues, where X is the number of closed/fixed issues
            if (!viewingClosed) {
                preferenceUtil.setIssueCountByVehicleId(vehicleId, issueArrayList.size());
            }

            // Only display the logs if there are logs, otherwise display the "you have none" text
            if (issueArrayList.size() < 1) {
                noIssues.setVisibility(View.VISIBLE);

                if (viewingClosed) {
                    noIssues.setText(getResources().getString(R.string.no_closed_issues));
                } else {
                    noIssues.setText(getResources().getString(R.string.no_open_issues));
                }
            } else {
                noIssues.setVisibility(View.GONE);
            }
        } else {
            Snackbar.make(toolbar, "The vehicle ID is not valid, please try again.", Snackbar.LENGTH_INDEFINITE).show();
        }

    }

    // Prepare the RecyclerView and its Adapter with data
    private void prepRecyclerView() {
        // Find the RecyclerView view
        recyclerView = findViewById(R.id.recyclerView_issueActivity);

        // Create a LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and make the RecyclerView use it
        recyclerView.setAdapter(new IssueRecyclerAdapter(issueArrayList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_issue, menu);

        // Get a reference to the search view
        SearchView searchView = (SearchView) menu.findItem(R.id.menuItem_search).getActionView();
        searchView.setSearchableInfo(((SearchManager) getSystemService(Context.SEARCH_SERVICE))
                .getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Create a new array list to filter the data
                ArrayList<Issue> filteredList;

                filteredList = dbHelper.getAllIssuesBySearchTerm(query, vehicleId, viewingClosed);

                // If the filtered list is empty, display a message.
                if (filteredList.isEmpty()) {
                    Toast.makeText(IssueActivity.this, "No issues found.", Toast.LENGTH_SHORT).show();
                }

                // Pass the list to the adapter.
                recyclerView.setAdapter(new IssueRecyclerAdapter(filteredList));
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
        int id = item.getItemId();

        if (id == R.id.menuItem_add) {
            // Go to IssueSettingsActivity and send an id of -1 so it knows we're creating a new issue
            Intent intent = new Intent(IssueActivity.this, IssueSettingsActivity.class);
            intent.putExtra(IssueActivity.EXTRA_ISSUE_ID, -1);
            intent.putExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, vehicleId);
            startActivity(intent);
        } else if (id == R.id.menuItem_filter) {
            // Swap to viewing the opposite status of what we are now
            viewingClosed = !viewingClosed;
            populateRecyclerView(viewingClosed);
        }

        return super.onOptionsItemSelected(item);
    }

}

class IssueRecyclerAdapter extends RecyclerView.Adapter<IssueRecyclerAdapter.ViewHolder> {

    // An array holding issue logs
    final private ArrayList<Issue> issueArrayList;

    // Constructor
    public IssueRecyclerAdapter(ArrayList<Issue> issueArrayList) {
        this.issueArrayList = issueArrayList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        Issue issue;

        final TextView title;
        final TextView description;
        final TextView priority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            title = itemView.findViewById(R.id.card_issueActivity_title);
            description = itemView.findViewById(R.id.card_issueActivity_description);
            priority = itemView.findViewById(R.id.card_issueActivity_priority);
            Button buttonComplete = itemView.findViewById(R.id.card_issueActivity_buttonComplete);

            itemView.findViewById(R.id.card_issueActivity_buttonEdit).setOnClickListener(v -> {
                // Go to LogSettingsActivity and pass the log's ID so it knows which log we're editing
                Intent intent = new Intent(context, IssueSettingsActivity.class);
                intent.putExtra(IssueActivity.EXTRA_ISSUE_ID, issue.getId());
                context.startActivity(intent);
            });

            buttonComplete.setOnClickListener(v -> {
                if (context != null) {
                    // Set up a DBHelper
                    DBHelper dbHelper = new DBHelper(context);

                    // Update the status with the fetched status.
                    issue.setStatusId((!IssueActivity.viewingClosed) ? dbHelper.getClosedIssueStatusId() : dbHelper.getOpenIssueStatusId());

                    // Update the DB with the new issue status
                    dbHelper.updateIssue(issue);

                    // Update the RecyclerView
                    removeAt(getAdapterPosition());
                }
            });

            if (IssueActivity.viewingClosed) {
                buttonComplete.setText(R.string.card_issue_buttonReopen);
            }
        }

        // One-hitter method for setting the data for all the TextViews
        public void setDataByObject(Issue issue) {
            this.issue = issue;
            title.setText(issue.getTitle());
            description.setText(issue.getDescription());
            priority.setText(issue.getPriorityAsString());
        }
    }

    // Removes an element from the list at the specified position
    // TODO: Removing items causes wierd visual effects unless the whole screen is full
    public void removeAt(int position) {
        issueArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, issueArrayList.size());
    }

    @NonNull
    @Override
    public IssueRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_issue, parent, false));
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
