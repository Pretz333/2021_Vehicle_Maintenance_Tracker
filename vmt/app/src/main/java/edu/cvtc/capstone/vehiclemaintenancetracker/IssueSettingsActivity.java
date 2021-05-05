package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

public class IssueSettingsActivity extends AppCompatActivity {
    public static final String TAG = "IssueSettingsActivity_CLASS";

    //Class variables
    DBHelper dbHelper = new DBHelper(IssueSettingsActivity.this);
    Issue issue = null;
    int vehicleId;
    Toolbar toolbar;

    private EditText mTitle, mDescription;
    private Spinner mPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_settings);

        // Get a reference to the member objects
        mTitle = findViewById(R.id.issueSettings_editTextTitle);
        mDescription = findViewById(R.id.issueSettings_editTextDescription);
        mPriority = findViewById(R.id.issueSettings_spinnerPriority);
        Button saveButton = findViewById(R.id.issueSettings_buttonSave);
        Button closeButton = findViewById(R.id.issueSettings_buttonClose);

        // Get the issueId from the intent
        Intent intent = getIntent();
        int issueId = intent.getIntExtra(IssueActivity.EXTRA_ISSUE_ID, -1);
        vehicleId = intent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // If a valid issueId was passed to this activity, we want to pre-populate the fields
        if (issueId != -1) {
            //Since a issueId was passed, we also want to grab the issue from the db for later modification
            issue = dbHelper.getIssueByIssueId(issueId);
            populateFieldsByObject(issue);

            // Since the issue was already in the database, the close button should be visible.
            closeButton.setVisibility(View.VISIBLE);

            //And we want to change the title to say "Edit Issue" instead of "Add Issue"
            TextView title = findViewById(R.id.issueSettings_textViewTitle);
            title.setText(getResources().getString(R.string.issueSettings_titleDisplayEdit));
        }
    }

    private void populateFieldsByObject(Issue issue) {
        mTitle.setText(issue.getTitle());
        mDescription.setText(issue.getDescription());

        // Set the spinner to the id of the selected priority
        mPriority.setSelection(issue.getPriority());
    }

    private void updateIssueWithValues() {

    }
}