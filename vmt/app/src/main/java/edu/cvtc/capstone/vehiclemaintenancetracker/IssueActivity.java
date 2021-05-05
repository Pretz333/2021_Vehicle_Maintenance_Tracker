package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.material.snackbar.Snackbar;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class IssueActivity extends AppCompatActivity {

    public static final String EXTRA_ISSUE_ID = "edu.cvtc.capstone.vehiclemaintenancetracker.EXTRA_ISSUE_ID";

    // Member variables
    private int vehicleId;
    Toolbar toolbar;

    // An array of logs used to populate the
    // RecyclerView
    ArrayList<Issue> issueArrayList;

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
            prepDemoData();

            // Prepare the RecyclerView
            prepRecyclerView();

        } else {
            // Not a valid id
            Snackbar.make(toolbar, String.format("The vehicle id of %s is not valid", vehicleId), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    // Add a few fake issue logs to the logArrayList
    // to be used in the RecyclerView for demo
    private void prepDemoData() {

        // Create objects
        // TODO: Replace with actual Database objects
        //Time time = new Time(Calendar.getInstance().getTimeInMillis());

        Issue issue1 = new Issue("No Oil",
                "All the oil leaked out. Currently driving it without oil. Probably should add some in the future",
                0, vehicleId, 0);

        Issue issue2 = new Issue("Buy New Tires",
                "The rear left tire is missing. Although driving with three wheels is fine by me, others are very judgemental",
                0, vehicleId, 0);

        Issue issue3 = new Issue("Air Vents",
                "Exhaust fumes are leaking into the vehicle cabin. Getting terrible headaches before work every day. Should look into that later on. Not a priority",
                0, vehicleId, 0);

        // Add the objects to the list
        issueArrayList.add(issue1);
        issueArrayList.add(issue2);
        issueArrayList.add(issue3);
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
            //priority.setText(issue.getPriority());
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