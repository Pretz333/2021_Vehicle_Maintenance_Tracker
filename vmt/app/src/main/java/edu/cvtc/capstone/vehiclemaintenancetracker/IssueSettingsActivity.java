package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class IssueSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    // This is exactly like the VehicleSettingsActivity, but for Issues.
    // It is used to update/change a issue and create new ones.
    public static final String TAG = "IssueSettingsActivity_CLASS";

    DBHelper dbHelper = new DBHelper(IssueSettingsActivity.this);
    Issue issue = null;
    int vehicleId;

    Toolbar toolbar;
    EditText mTitle, mDescription;
    TextInputLayout eTitle, eDescription, ePriority;
    AutoCompleteTextView mPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_settings);

        // Grab the vehicleId and issueId from the received intent
        Intent intent = getIntent();
        vehicleId = intent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);
        int issueId = intent.getIntExtra(IssueActivity.EXTRA_ISSUE_ID, -1);

        // Get a reference to the view objects.
        toolbar = findViewById(R.id.issueSettings_toolbar);
        mTitle = findViewById(R.id.issueSettings_editTextTitle);
        mDescription = findViewById(R.id.issueSettings_editTextDescription);
        mPriority = findViewById(R.id.issueSettings_editTextPrioritySpinner);
        eTitle = findViewById(R.id.issueSettings_textInputTitle);
        eDescription = findViewById(R.id.issueSettings_textInputDescription);
        ePriority = findViewById(R.id.issueSettings_textInputPriority);

        // Set support for the toolbar
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(v -> IssueSettingsActivity.super.finish());

        // Setting up the onClick listener for the save button
        findViewById(R.id.issueSettings_buttonSave).setOnClickListener(this);

        // Initialize an adapter for the spinner and make the spinner use the adapter, a
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_spinner,
                getResources().getStringArray(R.array.issueSettingsActivity_spinnerPriority));
        mPriority.setAdapter(adapter);

        // If a valid issueId was passed to this activity, we want to pre-populate the fields
        if (issueId != -1) {
            // Since a issueId was passed, we also want to grab the issue from the DB for later modification
            issue = dbHelper.getIssueByIssueId(issueId);
            populateFieldsByObject(issue);

            // Set the vehicleId to the issue's vehicleId
            vehicleId = issue.getVehicleId();

            // Since the issue was already in the database, the close button should be visible.
            // We also want to change what it says depending on if this issue is closed or open
            Button closeButton = findViewById(R.id.issueSettings_buttonClose);
            if (issue.getStatusId() == dbHelper.getOpenIssueStatusId()) {
                closeButton.setText(getResources().getString(R.string.issueSettings_buttonClose));
            } else {
                closeButton.setText(getResources().getString(R.string.issueSettings_buttonReopen));
            }
            closeButton.setVisibility(View.VISIBLE);
            closeButton.setOnClickListener(this);

            // And we want to change the title to say "Edit Issue" instead of "Add Issue"
            TextView title = findViewById(R.id.issueSettings_textViewTitle);
            title.setText(getResources().getString(R.string.issueSettings_titleDisplayEdit));
        }
    }

    private void populateFieldsByObject(Issue issue) {
        mTitle.setText(issue.getTitle());
        mDescription.setText(issue.getDescription());

        // Fill the spinner with the text of the selected priority.
        // It's important to note that the filter argument must be set to false.
        // otherwise it will discard the list set by the adapter in favour of
        // the text that was set. Wacky? yes. Fixed? also yes
        mPriority.setText(priorityIntToString(issue.getPriority()), false);
    }

    // Event handler for button clicks
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.issueSettings_buttonSave) {
            // Check if all fields are valid
            if (areFieldsValid()) {
                // If we made a new log from the id passed, insert it, otherwise update it

                // areFieldsValid() should catch this first check, so this means the log was unable
                // to be created as it didn't have the minimum properties needed to be functional
                if (issue == null) {
                    Snackbar.make(v, "The issue failed to be created", Snackbar.LENGTH_SHORT).show();
                } else if (issue.getId() == -1) {
                    dbHelper.insertIssue(issue);
                    IssueSettingsActivity.super.finish();
                } else {
                    dbHelper.updateIssue(issue);
                    IssueSettingsActivity.super.finish();
                }
            } else {
                // The user made a typo/didn't fill out a field, lets alert them!
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);
                alert.setTitle(getResources().getString(R.string.vehicleSettingsActivity_errorValidationTitle));
                alert.setMessage(getResources().getString(R.string.vehicleSettingsActivity_errorValidationMessage));
                alert.setPositiveButton(getResources().getString(R.string.vehicleSettingsActivity_errorValidationButtonNeutral), null);
                alert.show();
            }
        } else if (v.getId() == R.id.issueSettings_buttonClose) {
            // Get the open status id
            int openStatusId = dbHelper.getOpenIssueStatusId();

            // If it was open, set it to closed. If it was closed, set it to open
            if (issue.getStatusId() == openStatusId) {
                issue.setStatusId(dbHelper.getClosedIssueStatusId());
            } else {
                issue.setStatusId(openStatusId);
            }

            // Update the DB and close go back to the IssueActivity
            dbHelper.updateIssue(issue);
            IssueSettingsActivity.super.finish();
        }
    }

    // This is used to convert a String value, such as "High Priority," to an int for the database.
    private int priorityStringToInt(String string) {
        // Convert the priority of the editText from letters to a single digit
        switch (string) {
            case "High Priority":
                return 0;
            case "Medium Priority":
                return 1;
            case "Low Priority":
                return 2;
            default:
                return -1;
        }
    }

    // This is used to convert a int value, to its user-friend definition like "High Priority"
    private String priorityIntToString(int priority) {
        switch (priority) {
            case 0:
                return "High Priority";
            case 1:
                return "Medium Priority";
            case 2:
                return "Low Priority";
            default:
                return "None Selected";
        }
    }

    // This checks every editText field to see if it's empty or contains invalid data.
    // If it catches any issues, it will highlight the editText container field and return false.
    private boolean areFieldsValid() {
        // Whether the values from the fields can be inserted into the database without conflict.
        boolean retVal = true;

        if (vehicleId == -1) {
            // Then we don't know which vehicle to attach this issue to. This should never happen
            return false;
            // TODO: Display something to the user
        }

        // If we're creating a new Issue, we need to create the Issue object
        if (issue == null) {
            // A new issue is not fixed, and thus will be open.
            // Try to get the open status id and ensure it properly fetched
            int openIssueId = dbHelper.getOpenIssueStatusId();
            if (openIssueId != -1) {
                issue = new Issue(mTitle.getText().toString().trim(), vehicleId, openIssueId);
            }
        }

        // Convert the EditText fields to strings
        String checkTitle = mTitle.getText().toString().trim();
        String checkDescription = mDescription.getText().toString().trim();
        String checkPriority = mPriority.getText().toString().trim();

        if (checkTitle.isEmpty()) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkTitle)) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eTitle.setError(null);
            issue.setTitle(checkTitle);
        }

        if (checkDescription.isEmpty()) {
            retVal = false;
            eDescription.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isTextSafe(checkDescription)) {
            retVal = false;
            eDescription.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eDescription.setError(null);
            issue.setDescription(checkDescription);
        }

        if (checkPriority.isEmpty()) {
            retVal = false;
            ePriority.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else {
            ePriority.setError(null);
            issue.setPriority(priorityStringToInt(checkPriority));
        }

        return retVal;
    }

}
