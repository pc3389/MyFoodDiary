package android.example.myfoodrecords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.example.myfoodrecords.activities.DetailActivity;
import android.example.myfoodrecords.activities.EditorActivity;
import android.example.myfoodrecords.activities.MapsActivity;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.model.PlaceModel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PrivatePlaceAdapter extends RecyclerView.Adapter<PrivatePlaceAdapter.PlaceViewHolder> {

    private List<PlaceModel> placeModelList;
    private Context context;

    public static final String PRIVATE_PLACE_KEY = "PrivatePlace";
    public static final String PUT_PLACE_ID = "placeKey";
    public static final int RESULT_PRIVATE_PLACE = 1;
    private Activity mActivity;


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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new PrivatePlaceAdapter.PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        final PlaceModel placeModel = placeModelList.get(position);
        holder.placeNameTextView.setText(placeModel.getPlaceName());
        holder.placeAddressTextView.setText(placeModel.getAddress());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra(PRIVATE_PLACE_KEY, placeModel.getId());
                context.startActivity(intent);
                //TODO do the things in MapsActivity
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(PUT_PLACE_ID, placeModel.getId());
                mActivity.setResult(RESULT_PRIVATE_PLACE, intent);
                mActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeModelList.size();
    }




}