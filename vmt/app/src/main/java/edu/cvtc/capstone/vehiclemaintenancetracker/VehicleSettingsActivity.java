package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class VehicleSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    // This activity is used to change the details of a vehicle or create a new one

    public static final String TAG = "VEHICLESETTINGS_CLASS";
    DBHelper dbHelper = new DBHelper(VehicleSettingsActivity.this);
    PreferenceUtil preferenceUtil;
    Vehicle vehicle = null;
    int vehicleId;

    // View references to all the editText fields
    EditText mNickname, mMake, mModel, mYear, mPlate, mDatePurchased, mColor, mMileage, mValue, mVIN;
    TextInputLayout eNickname, eMake, eModel, eYear, ePlate, eColor, eVIN, eDatePurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_settings);

        // Initialize the preference util
        preferenceUtil = new PreferenceUtil(this);

        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> VehicleSettingsActivity.super.finish());

        // Get references to all the editText fields
        mNickname = findViewById(R.id.vehicleSettings_editTextNickname);
        mMake = findViewById(R.id.vehicleSettings_editTextMake);
        mModel = findViewById(R.id.vehicleSettings_editTextModel);
        mYear = findViewById(R.id.vehicleSettings_editTextYear);
        mPlate = findViewById(R.id.vehicleSettings_editTextPlate);
        mDatePurchased = findViewById(R.id.vehicleSettings_editTextDatePurchased);
        mColor = findViewById(R.id.vehicleSettings_editTextColor);
        mMileage = findViewById(R.id.vehicleSettings_editTextMileage);
        mValue = findViewById(R.id.vehicleSettings_editTextValue);
        mVIN = findViewById(R.id.vehicleSettings_editTextVin);
        Button buttonDelete = findViewById(R.id.vehicleSettings_buttonDelete);

        // These view references are for the container that holds the
        // edit text. The purposes of these are only to provide the
        // material design error message with better highlighting.
        eNickname = findViewById(R.id.vehicleSettings_textInputNickname);
        eMake = findViewById(R.id.vehicleSettings_textInputMake);
        eModel = findViewById(R.id.vehicleSettings_textInputModel);
        eYear = findViewById(R.id.vehicleSettings_textInputYear);
        ePlate = findViewById(R.id.vehicleSettings_textInputPlate);
        eColor = findViewById(R.id.vehicleSettings_textInputColor);
        eVIN = findViewById(R.id.vehicleSettings_textInputVin);
        eDatePurchased = findViewById(R.id.vehicleSettings_textInputDate);

        // Initialize button onClick listeners for this activity
        findViewById(R.id.vehicleSettings_buttonSave).setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        // Get the vehicleID from the intent
        vehicleId = getIntent().getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // If a valid VehicleID was passed to this activity, we want to pre-populate the fields
        if (vehicleId != -1) {
            // Since a vehicleID was passed, we also want to grab the vehicle from the db for later modification
            vehicle = dbHelper.getVehicleById(vehicleId);
            populateFieldsByObject(vehicle);

            // This vehicle is already in the database, so the delete button should be visible.
            buttonDelete.setVisibility(View.VISIBLE);
        }

    }

    // Populate all the fields by a Vehicle object
    private void populateFieldsByObject(Vehicle vehicle) {
        // Set the fields data from the given vehicle object
        mNickname.setText(vehicle.getName());
        mMake.setText(vehicle.getMake());
        mModel.setText(vehicle.getModel());
        mYear.setText(vehicle.getYear());
        mPlate.setText(vehicle.getLicensePlate());
        mColor.setText(vehicle.getColor());

        if (vehicle.getMileage() != 0) {
            mMileage.setText(String.valueOf(vehicle.getMileage()));
        }

        if (vehicle.getValue() != 0) {
            mValue.setText(String.valueOf(vehicle.getValue()));
        }

        mVIN.setText(vehicle.getVIN());

        if (vehicle.getPurchaseDate() != null) {
            // Format the date for easier reading because no one wants to read a LONG date.
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/y", Locale.ENGLISH);
            mDatePurchased.setText(simpleDateFormat.format(vehicle.getPurchaseDate()));
        }
    }

    // This checks every editText field to see if it's empty or contains invalid data.
    // If it catches any issues, it will highlight the editText container field and return false.
    private boolean hasMinimumRequirements() {
        // Whether the values from the fields can be inserted into the database without conflict.
        boolean retVal = true;

        // Convert the EditText fields to strings
        String checkNickname = mNickname.getText().toString().trim();
        String checkMake = mMake.getText().toString().trim();
        String checkModel = mModel.getText().toString().trim();
        String checkYear = mYear.getText().toString().trim();
        String checkPlate = mPlate.getText().toString().trim();
        String checkColor = mColor.getText().toString().trim();
        String checkVIN = mVIN.getText().toString().trim();
        String checkDatePurchased = mDatePurchased.getText().toString().trim();

        // Create a new vehicle if needed
        if (vehicle == null) {
            vehicle = new Vehicle(checkNickname);
        }

        // Note: if the vehicle was created but fails verification, it will create a new vehicle
        // that does nothing and goes nowhere. Since it can only make the one (it'll modify it
        // after it's created) and it won't be inserted into the database, it's not a concern.
        // It'll be caught by the garbage collector eventually.

        if (checkNickname.isEmpty()) {
            retVal = false;
            eNickname.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkNickname)) {
            retVal = false;
            eNickname.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eNickname.setError(null);
            vehicle.setName(checkNickname);
        }

        if (checkMake.isEmpty()) {
            retVal = false;
            eMake.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkMake)) {
            retVal = false;
            eMake.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eMake.setError(null);
            vehicle.setMake(checkMake);
        }

        if (checkModel.isEmpty()) {
            retVal = false;
            eModel.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkModel)) {
            retVal = false;
            eModel.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eModel.setError(null);
            vehicle.setModel(checkModel);
        }

        if (checkYear.isEmpty()) {
            retVal = false;
            eYear.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isYearValid(checkYear)) {
            retVal = false;
            eYear.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidYear));
        } else {
            eYear.setError(null);
            vehicle.setYear(checkYear);
        }

        if (checkPlate.isEmpty()) {
            retVal = false;
            ePlate.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkPlate)) {
            retVal = false;
            ePlate.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            ePlate.setError(null);
            vehicle.setLicensePlate(checkPlate);
        }

        if (checkColor.isEmpty()) {
            retVal = false;
            eColor.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringLettersOnly(checkColor)) {
            retVal = false;
            eColor.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageOnlyLetters));
        } else {
            eColor.setError(null);
            vehicle.setColor(checkColor);
        }

        if (!mMileage.getText().toString().isEmpty()) {
            vehicle.setMileage(Integer.parseInt(mMileage.getText().toString()));
        } else {
            vehicle.setMileage(0);
        }

        if (!mValue.getText().toString().isEmpty()) {
            vehicle.setValue(Double.parseDouble(mValue.getText().toString()));
        } else {
            vehicle.setValue(0.00);
        }

        // The year is attempted to be set before this statement is called
        if (!VerifyUtil.isVINValid(checkVIN, vehicle.getYear())) {
            retVal = false;
            eVIN.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidVIN));
        } else {
            eVIN.setError(null);
            vehicle.setVIN(checkVIN);
        }

        if (VerifyUtil.parseStringToDate(checkDatePurchased) != null) {
            vehicle.setPurchaseDate(VerifyUtil.parseStringToDate(checkDatePurchased));
            eDatePurchased.setError(null);
        } else if (!checkDatePurchased.isEmpty()) {
            eDatePurchased.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageDifferentValue));
            retVal = false;
        }

        return retVal;
    }

    // Listener for view events.
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vehicleSettings_buttonSave) {
            // Check if all fields are valid
            if (hasMinimumRequirements()) {
                // If we made a new vehicle from the id passed, insert it, otherwise update it

                // hasMinimums() should catch this first check, so this means the vehicle was unable
                // to be created as it didn't have the minimum properties needed to be functional
                if (vehicle == null) {
                    Snackbar.make(v, "Vehicle must have a nickname", Snackbar.LENGTH_SHORT).show();
                } else if (vehicle.getId() == -1) {
                    dbHelper.insertVehicle(vehicle);
                    VehicleSettingsActivity.super.finish();
                } else {
                    dbHelper.updateVehicle(vehicle);
                    VehicleSettingsActivity.super.finish();
                }
            } else {
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);
                alert.setTitle(getResources().getString(R.string.vehicleSettingsActivity_errorValidationTitle));
                alert.setMessage(getResources().getString(R.string.vehicleSettingsActivity_errorValidationMessage));
                alert.setPositiveButton(getResources().getString(R.string.vehicleSettingsActivity_errorValidationButtonNeutral), null);
                alert.show();
            }
        } else if (v.getId() == R.id.vehicleSettings_buttonDelete) {
            // Display the alert dialog to confirm the vehicle delete.
            AlertDialog.Builder builder = new AlertDialog.Builder(VehicleSettingsActivity.this);
            View view = VehicleSettingsActivity.this.getLayoutInflater().inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            AlertDialog alert = builder.create();

            // Set the message of the alert dialog
            ((TextView) view.findViewById(R.id.alertDialog_message)).setText(R.string.alertDialog_messageDeleteVehicle);

            // Confirm button click handler
            view.findViewById(R.id.alertDialog_buttonYes).setOnClickListener(v1 -> {
                // Delete vehicle from the database
                dbHelper.deleteVehicle(vehicle);

                // Delete the issueCounter key-value pair associated with this vehicle
                preferenceUtil.deleteIssueCountByVehicleId(vehicleId);

                // Close the alert dialog box
                alert.cancel();

                // Display a toast that the vehicle was deleted
                Toast.makeText(getApplicationContext(), "The vehicle was deleted.", Toast.LENGTH_SHORT).show();
                VehicleSettingsActivity.super.finish();
            });

            // Cancel button click handler
            view.findViewById(R.id.alertDialog_buttonNo).setOnClickListener(v12 -> {
                // Close the alert dialog
                alert.cancel();
            });

            // Display the alert dialog
            alert.show();
        }
    }

}