package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

public class IssueSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    // View references
    //
    Toolbar toolbar;
    // EditText fields
    EditText mTitle, mDescription;
    // Containers of the EditText fields (Only used for error messages)
    TextInputLayout eTitle, eDescription, ePriority;
    // The spinner for selecting a priority
    AutoCompleteTextView mPriority;

    // Objects and variables ("null", or -1 by default)
    int receivedVehicleId = -1;
    // Issue object (null by default)
    Issue issueObject = null;

    // Helper for database interactions
    DBHelper dbHelper = new DBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_settings);

        // Grab the vehicleId from the received intent
        receivedVehicleId = getIntent().getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // Setting the view references
        //
        // Toolbar
        toolbar = findViewById(R.id.issueSettings_toolbar);
        // EditText fields
        mTitle = findViewById(R.id.issueSettings_editTextTitle);
        mDescription = findViewById(R.id.issueSettings_editTextDescription);
        mPriority = findViewById(R.id.issueSettings_editTextPrioritySpinner);
        // EditText container fields
        eTitle = findViewById(R.id.issueSettings_textInputTitle);
        eDescription = findViewById(R.id.issueSettings_textInputDescription);
        ePriority = findViewById(R.id.issueSettings_textInputPriority);
        // Buttons
        Button buttonSave = findViewById(R.id.issueSettings_buttonSave);

        // Set support for the toolbar
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for
        // reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueSettingsActivity.super.finish();
            }});

        // Setting up onClick listeners
        buttonSave.setOnClickListener(this);

        // Initialize an adapter for the spinner
        // and make the spinner use the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_spinner, getResources().getStringArray(R.array.issueSettingsActivity_spinnerPriority));
        mPriority.setAdapter(adapter);
    }

    // Event handler for button clicks
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.issueSettings_buttonSave) {
            // Verify that all fields are valid
            if (hasMinimumRequirements()) {

                // Since it passed verification, lets
                // toss all the editText data into the
                // global issue object variable
                putFieldsIntoObject();

                // Insert it into the database
                dbHelper.insertIssue(issueObject);

                // Close the activity after insertion
                IssueSettingsActivity.super.finish();
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

    // This takes all the fields required
    // to construct an issueObject.
    //
    // If the issueObject is null, we are
    // creating an issue. If it's not null,
    // we are editing an object.
    private void putFieldsIntoObject() {
        if (issueObject == null) {
            issueObject = new Issue();
        }

        issueObject.setId(-1);
        issueObject.setTitle(mTitle.getText().toString());
        issueObject.setDescription(mDescription.getText().toString());
        issueObject.setPriority(priorityStringToInt(mPriority.getText().toString()));
        issueObject.setVehicleId(receivedVehicleId);
        // TODO: Implement an actual status id in the future.
        issueObject.setStatusId(0);
    }

    // Used by the Spinner. It converts a String
    // value, such as "High Priority," to an int
    // for the database.
    private int priorityStringToInt(String string) {
        // Convert the priority of the editText
        // from letters to a single diget
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

    // This checks every editText field if its
    // empty, or contains invalid data.
    //
    // If it catches any issues, it will highlight
    // the editText container field and return false.
    private boolean hasMinimumRequirements() {
        // Whether the values from the fields
        // can be inserted into the database
        // without conflict.
        boolean retVal = true;

        // Convert the EditText fields to strings
        String checkTitle = mTitle.getText().toString();
        String checkDescription = mDescription.getText().toString();
        String checkPriority = mPriority.getText().toString();

        //TODO: HEY WILLIAM, LOOK HERE!
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // the code below, specifically the verifyUtil else-if statements are related to my discussion post


        // Check if each field is not blank, null,
        // or contains invalid data. If it does,
        // retVal will be false.
        //
        // Check if it's empty
        if (isStringEmpty(checkTitle)) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringLettersOrDigitsOnly(checkTitle)) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eTitle.setError(null);
        }

        // Again...
        if (isStringEmpty(checkDescription)) {
            retVal = false;
            eDescription.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringLettersOrDigitsOnly(checkDescription)) {
            retVal = false;
            eDescription.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eDescription.setError(null);
        }

        // And again...
        if (isStringEmpty(checkPriority)) {
            retVal = false;
            ePriority.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringLettersOrDigitsOnly(checkPriority)) {
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

}













