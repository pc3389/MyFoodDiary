package bo.young.myfoodrecords.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.example.myfoodrecords.R;

import bo.young.myfoodrecords.activities.MapsActivity;
import bo.young.myfoodrecords.activities.PrivatePlaceActivity;
import bo.young.myfoodrecords.model.PlaceModel;
import bo.young.myfoodrecords.utils.Constants;
import bo.young.myfoodrecords.utils.RealmHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;

public class PrivatePlaceAdapter extends RecyclerView.Adapter<PrivatePlaceAdapter.PlaceViewHolder> {

    private List<PlaceModel> placeModelList;
    private Context context;

    private Activity mActivity;
    private PlaceModel placeModel;
    public AlertDialog dialog;
    public AlertDialog deleteDialog;


    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        private TextView placeNameTextView;
        private TextView placeAddressTextView;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeNameTextView = itemView.findViewById(R.id.item_place_name_tv);
            placeAddressTextView = itemView.findViewById(R.id.item_place_address_tv);
        }
    }

    public PrivatePlaceAdapter(List<PlaceModel> placeModelList, Context context, Activity mActivity) {
        this.placeModelList = placeModelList;
        this.context = context;
        this.mActivity = mActivity;
    }


    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PrivatePlaceAdapter.PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        placeModel = placeModelList.get(position);
        holder.placeNameTextView.setText(placeModel.getPlaceName());
        holder.placeAddressTextView.setText(placeModel.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeModel = placeModelList.get(position);
                setupDialog();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (placeModelList == null) {
            return 0;
        } else {
            return placeModelList.size();
        }
    }

    /**
     * Setups the dialog showing the list (Set, Edit, Delete)
     * each items has ClickEvents accordingly
     */
    public void setupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.location_button));
        String[] placeArray = {context.getResources().getString(R.string.select), context.getResources().getString(R.string.edit), context.getResources().getString(R.string.delete)};
        builder.setItems(placeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // Select the place detail and pass the placeId
                    case 0: {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.PUT_PLACE_ID, placeModel.getId());
                        mActivity.setResult(Constants.RESULT_PRIVATE_PLACE, intent);
                        mActivity.finish();
                        break;
                    }
                    //Edit the place detail in MapsActivity
                    case 1: {
                        Intent intent = new Intent(context, MapsActivity.class);
                        intent.putExtra(Constants.PRIVATE_PLACE_KEY, placeModel.getId());
                        intent.putExtra(Constants.KEY_REQUEST_CODE, PrivatePlaceActivity.REQUSET_PRIVATE_PLACE);
                        context.startActivity(intent);
                        break;
                    }
                    //Delete the data from Realm Database
                    case 2: {
                        setupDeleteDialog();
                        break;
                    }
                }
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    /**
     * Opens the dialog asking if the user still wants to delete the file
     * Implemented to prevent the mis-click, and to make sure the user really wants to delete the data
     */
    public void setupDeleteDialog() {
        Realm realm = Realm.getDefaultInstance();
        RealmHelper helper = new RealmHelper(realm);
        int placeId = placeModel.getId();

        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
        deleteBuilder.setTitle(context.getString(R.string.delete))
                .setMessage(context.getString(R.string.delete_description))

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        helper.deletePlace(placeId);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert);
        deleteDialog = deleteBuilder.create();
        deleteDialog.show();
    }
}