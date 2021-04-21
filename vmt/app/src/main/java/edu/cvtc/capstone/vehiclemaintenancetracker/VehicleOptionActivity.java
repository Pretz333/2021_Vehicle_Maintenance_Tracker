package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class VehicleOptionActivity extends AppCompatActivity {

    // Intent extras used by the MainActivity's RecyclerView
    public static final String EXTRA_VEHICLE_ID = "edu.cvtc.capstone.vehiclemaintenancetracker.EXTRA_VEHICLE_ID";

    // The list that contains item objects used in the
    // RecyclerView's menu
    ArrayList<OptionItemObject> optionItemObjectArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_option);

        // Grab the intent data
        Intent receivedIntent = getIntent();

        // Grab the ID with a provided default value
        int vehicleIdFromRecycler = receivedIntent.getIntExtra(EXTRA_VEHICLE_ID, -1);

        // Get the user hint TextView and set it
        // Short-hand because I'm that lazy at the moment... - Alexander
        ((TextView) findViewById(R.id.textView_optionActivity_vehicleHint))
                .setText(getResources()
                        .getString(R.string.optionActivity_userHint)
                        .concat(" Vehicle/RecyclerView Item ID Of: " + vehicleIdFromRecycler));


        // Initialize the option menu list
        optionItemObjectArrayList = new ArrayList<>();

        // Generate each option item to be used in the RecyclerView
        prepListObjects();

        // Initialize the RecyclerView
        prepRecyclerView();

    }

    // Create the list items to be used in
    // the RecyclerView
    private void prepListObjects() {
        // Create objects
        OptionItemObject itemMaintenance = new OptionItemObject("Maintenance", "Add, remove and edit maintenance logs", R.drawable.card_options_maintenance);
        OptionItemObject itemIssue = new OptionItemObject("Issue Tracker", "See what issues you need to resolve", R.drawable.card_options_issue);
        OptionItemObject itemSystems = new OptionItemObject("Systems", "I really don't know", R.drawable.card_options_systems);
        OptionItemObject itemVehicleSettings = new OptionItemObject("Vehicle Settings", "Modify this vehicle profile or delete it", R.drawable.card_options_vehicle_settings);

        // Add them to the array list
        optionItemObjectArrayList.add(itemMaintenance);
        optionItemObjectArrayList.add(itemIssue);
        optionItemObjectArrayList.add(itemSystems);
        optionItemObjectArrayList.add(itemVehicleSettings);
    }

    // Prepare the RecyclerView and its Adapter with data
    private void prepRecyclerView() {
        // Find the RecyclerView view
        RecyclerView recyclerView = findViewById(R.id.recyclerView_optionActivity);

        // Create a LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and make the RecyclerView use it
        OptionRecyclerAdapter optionRecyclerAdapter = new OptionRecyclerAdapter(optionItemObjectArrayList);
        recyclerView.setAdapter(optionRecyclerAdapter);
    }

}


// Custom class representing each option menu item (Did not track in pivotal)
class OptionItemObject {
    // Member variables
    private String title;
    private String subtitle;
    private int colorResource;

    // No-param constructor
    public OptionItemObject() {
        // ...
    }

    // Overloaded Constructor
    public OptionItemObject(String title, String subtitle, int colorResource) {
        this.title = title;
        this.subtitle = subtitle;
        this.colorResource = colorResource;
    }

    // Getters and setters
    //
    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setColor(int colorResource) {
        this.colorResource = colorResource;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getColorResource() {
        return colorResource;
    }


}

// RecyclerView for this activity. What's sad is we can't copy and paste the code from
// the previous recycler view because of how much different the layouts are. Well, that's
// alright... - Alexander
class OptionRecyclerAdapter extends RecyclerView.Adapter<OptionRecyclerAdapter.ViewHolder> {
    // DataSet for the list items
    ArrayList<OptionItemObject> dataset;

    public OptionRecyclerAdapter(ArrayList<OptionItemObject> dataset) {
        this.dataset = dataset;
    }

    // Provide a reference to the type of views that the
    // RecyclerView adapter will interact with.
    // Heads up, it's quite a bit!
    // NOTE: ViewHolder is a child class, not a function!
    // hopefully that saves some confusion.
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // Context of super class
        Context context;

        // View members of layout
        final TextView title;
        final TextView subtitle;
        final ImageView color;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Context used mainly for OnClickListener
            context = itemView.getContext();

            // Find the view members
            title = itemView.findViewById(R.id.listItem_optionActivity_title);
            subtitle = itemView.findViewById(R.id.listItem_optionActivity_subTitle);
            color = itemView.findViewById(R.id.listItem_optionActivity_Icon);

            // Onclick Listener for card
            itemView.setOnClickListener(this);
        }

        // One-hitter method for setting the values for all views
        // which is called by the parent class's onBindViewHolder method.
        public void setData(String title, String subtitle, int colorResource) {
            this.title.setText(title);
            this.subtitle.setText(subtitle);
            this.color.setImageResource(colorResource);
        }

        // Click listener for the card. This should take
        // the user to the corresponding activity
        // (Activities are not created yet)
        @Override
        public void onClick(View v) {
            // Get the layout position
            int layoutPosition = getLayoutPosition();

            // Simple toast message
            Toast.makeText(context, "You tapped an item at position: " + layoutPosition, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_options, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Grab the corresponding option object
        OptionItemObject optionItem = dataset.get(position);
        // Set the data in the view holder
        holder.setData(optionItem.getTitle(), optionItem.getSubtitle(), optionItem.getColorResource());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
