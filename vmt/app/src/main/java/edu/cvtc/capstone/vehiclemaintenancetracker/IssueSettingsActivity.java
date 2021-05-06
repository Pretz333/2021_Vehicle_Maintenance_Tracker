package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class IssueSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "IssueSettingsActivity_CLASS";
    
    DBHelper dbHelper = new DBHelper(IssueSettingsActivity.this);
    Issue issue = null;
    int vehicleId = -1;

    // View references
    Toolbar toolbar;
    EditText mTitle, mDescription;
    TextInputLayout eTitle, eDescription, ePriority; // Containers of the EditText fields (Used for error messages)
    // The spinner for selecting a priority
    AutoCompleteTextView mPriority; //TODO, a
    //Spinner mPriority; //TODO, j

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_settings);
      
        // Grab the vehicleId from the received intent
        Intent intent = getIntent();
        vehicleId = intent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);
        int issueId = intent.getIntExtra(IssueActivity.EXTRA_ISSUE_ID, -1);

        // Get a reference to the view objects.
        toolbar = findViewById(R.id.issueSettings_toolbar);
        mTitle = findViewById(R.id.issueSettings_editTextTitle);
        mDescription = findViewById(R.id.issueSettings_editTextDescription);
        mPriority = findViewById(R.id.issueSettings_editTextPrioritySpinner);
        //mPriority = findViewById(R.id.issueSettings_spinnerPriority); //TODO: j's priority handler
        eTitle = findViewById(R.id.issueSettings_textInputTitle);
        eDescription = findViewById(R.id.issueSettings_textInputDescription);
        ePriority = findViewById(R.id.issueSettings_textInputPriority);
        Button saveButton = findViewById(R.id.issueSettings_buttonSave);
        Button closeButton = findViewById(R.id.issueSettings_buttonClose);

        // Set support for the toolbar
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(v -> IssueSettingsActivity.super.finish());

        // Setting up onClick listeners
        saveButton.setOnClickListener(this);

        // Initialize an adapter for the spinner and make the spinner use the adapter, a
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_spinner, getResources()
                .getStringArray(R.array.issueSettingsActivity_spinnerPriority));
        mPriority.setAdapter(adapter);
      
        // If a valid issueId was passed to this activity, we want to pre-populate the fields
        if (issueId != -1) {
            //Since a issueId was passed, we also want to grab the issue from the db for later modification
            issue = dbHelper.getIssueByIssueId(issueId);
            populateFieldsByObject(issue);

            // Since the issue was already in the database, the close button should be visible. //TODO: Delete button, not close?
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
        mPriority.setSelection(issue.getPriority()); //TODO: may cause errors as this was written using a spinner and we're not anymore
    }

    // Event handler for button clicks
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.issueSettings_buttonSave) {
            // Verify that all fields are valid
            if (hasMinimumRequirements()) {
                // Update the values
                updateIssueWithValues();

                // If the issue exists, update it. Otherwise, insert the new issue.
                if (issue == null) {
                    // The issue does not have the minimum requirements.
                    Snackbar.make(v, "The issue must have a title", Snackbar.LENGTH_SHORT).show();
                } else if (issue.getId() == -1) {
                    // Since it passed verification, lets toss all the editText
                    // data into the class-level issue object and insert it
                    putFieldsIntoObject();
                    dbHelper.insertIssue(issue);
                    Snackbar.make(v, "Successfully added the issue!", Snackbar.LENGTH_SHORT).show();

                    // Close the activity
                    IssueSettingsActivity.super.finish();
                } else {
                    putFieldsIntoObject();
                    dbHelper.updateIssue(issue);
                    Snackbar.make(v, "Successfully updated the issue!", Snackbar.LENGTH_SHORT).show();

                    // Close the activity
                    IssueSettingsActivity.super.finish();
                }


            } else {
                // Something went wrong, lets alert the user!
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);
                alert.setTitle(getResources().getString(R.string.vehicleSettingsActivity_errorValidationTitle));
                alert.setMessage(getResources().getString(R.string.vehicleSettingsActivity_errorValidationMessage));
                alert.setPositiveButton(getResources().getString(R.string.vehicleSettingsActivity_errorValidationButtonNeutral), null);
                alert.show();
            }

        }
    }

    // This takes all the fields required to construct an issueObject.
    //
    // If the issueObject is null, we are creating an issue. 
    // If it's not null, we are editing an object.
    private void putFieldsIntoObject() {
        if (issue == null) {
            issue = new Issue(); //TODO: Don't
        }

        issue.setTitle(mTitle.getText().toString());
        issue.setDescription(mDescription.getText().toString());
        issue.setPriority(priorityStringToInt(mPriority.getText().toString()));
        issue.setVehicleId(vehicleId);
    }

    // Used by the Spinner. It converts a String value, such as 
    // "High Priority," to an int for the database. TODO: A's, not friendly with database changes
    private int priorityStringToInt(String string) {
        // Convert the priority of the editText
        // from letters to a single digit
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

    // This checks every editText field if its empty, or contains invalid data.
    //
    // If it catches any issues, it will highlight the editText container field and return false.
    private boolean hasMinimumRequirements() {
        // Whether the values from the fields can be inserted into the database without conflict.
        boolean retVal = true;

        if(vehicleId == -1){
            retVal = false;
            //TODO: Display something to the user
        }

        // Convert the EditText fields to strings
        String checkTitle = mTitle.getText().toString();
        String checkDescription = mDescription.getText().toString();
        String checkPriority = mPriority.getText().toString();

        // Check if each field is not blank, null, or contains invalid data.
        // If it does, retVal will be false.

        // Check if the title's empty
        if (isStringEmpty(checkTitle)) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkTitle)) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eTitle.setError(null);
        }

        // Again...
        if (isStringEmpty(checkDescription)) {
            retVal = false;
            eDescription.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isTextSafe(checkDescription)) {
            retVal = false;
            eDescription.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eDescription.setError(null);
        }

        // And again...
        if (isStringEmpty(checkPriority)) {
            retVal = false;
            ePriority.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringLettersOrDigitsOnly(checkPriority)) { //TODO: This isn't verified in A's version
            retVal = false;
            ePriority.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            ePriority.setError(null);
        }

        return retVal;
    }

    // Simplified function to check if a string is empty
    private boolean isStringEmpty(String string) {
        return (string.isEmpty() || string.equals("") || string.equals(" "));
    }

    private void updateIssueWithValues() {
        // Check if they have the minimum
        if ((vehicleId != -1 || issue.getVehicleId() != -1) && !isStringEmpty(mTitle.getText().toString())) {

            // Create a new issue or update an existing issue
            if (issue == null) {
                issue = new Issue(mTitle.getText().toString(), vehicleId, -1); //TODO: Update the statusId
            } else if (!isStringEmpty(mTitle.getText().toString())) {
                issue.setTitle(mTitle.getText().toString());
            }

            // Set the remaining properties if the issue exists
            if (issue != null && issue.getTitle() != null) {
                if(!isStringEmpty(mDescription.getText().toString())){
                    issue.setDescription(mDescription.getText().toString());
                }

                if(!isStringEmpty(mPriority.getText().toString())){
                    issue.setPriority(priorityStringToInt(mPriority.getText().toString()));
                }
            }
        }
    }

}
