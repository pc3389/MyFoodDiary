package android.example.myfoodrecords.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.activities.DetailActivity;
import android.example.myfoodrecords.activities.EditorActivity;
import android.example.myfoodrecords.activities.MapsActivity;
import android.example.myfoodrecords.activities.PrivatePlaceActivity;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.model.PlaceModel;
import android.example.myfoodrecords.utils.RealmHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.realm.Realm;

public class PrivatePlaceAdapter extends RecyclerView.Adapter<PrivatePlaceAdapter.PlaceViewHolder> {

    private List<PlaceModel> placeModelList;
    private Context context;

    public static final String PRIVATE_PLACE_KEY = "PrivatePlace";
    public static final String PUT_PLACE_ID = "placeKey";
    public static final int RESULT_PRIVATE_PLACE = 13;
    private Activity mActivity;
    private PlaceModel placeModel;


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
        return placeModelList.size();
    }

    private void setupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Location");
        String[] placeArray = {context.getResources().getString(R.string.set) , context.getResources().getString(R.string.edit), context.getResources().getString(R.string.delete)};
        builder.setItems(placeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        Intent intent = new Intent();
                        intent.putExtra(PUT_PLACE_ID, placeModel.getId());
                        mActivity.setResult(RESULT_PRIVATE_PLACE, intent);
                        mActivity.finish();
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent(context, MapsActivity.class);
                        intent.putExtra(PRIVATE_PLACE_KEY, placeModel.getId());
                        intent.putExtra(EditorActivity.KEY_REQUEST_CODE, PrivatePlaceActivity.REQUSET_PRIVATE_PLACE);
                        context.startActivity(intent);
                        break;
                    }
                    case 2: {
                        int placeId = placeModel.getId();
                        Realm realm = Realm.getDefaultInstance();
                        RealmHelper helper = new RealmHelper(realm);

                        helper.deletePlace(placeId);
                        break;
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}