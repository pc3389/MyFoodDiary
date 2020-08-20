package android.example.myfoodrecords.adapter;

import android.content.Context;
import android.content.Intent;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.activities.SummaryDetailActivity;
import android.example.myfoodrecords.model.PlaceModel;
import android.example.myfoodrecords.model.SummaryItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder> {

    public static final String KEY_SUMMARY_NAME = "summaryName";
    public static final String KEY_FOOD_OR_PLACE = "foodOrPlace";

    private List<SummaryItem> summaryItemList;
    private Context context;
    public static String foodOrPlace = "food";

    public static class SummaryViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView countTextView;
        private TextView ratingTextView;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_summary_name);
            countTextView = itemView.findViewById(R.id.item_summary_number);
            ratingTextView = itemView.findViewById(R.id.item_summary_rating);
        }
    }

    public SummaryAdapter(List<SummaryItem> summaryItemList, Context context) {
        this.summaryItemList = summaryItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_summary, parent, false);
        return new SummaryAdapter.SummaryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        holder.nameTextView.setText(summaryItemList.get(position).getName());
        holder.countTextView.setText(String.valueOf(summaryItemList.get(position).getCount()));
        holder.ratingTextView.setText(String.valueOf(summaryItemList.get(position).getRating()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SummaryDetailActivity.class);
                intent.putExtra(KEY_SUMMARY_NAME, summaryItemList.get(position).getName());
                intent.putExtra(KEY_FOOD_OR_PLACE,foodOrPlace);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return summaryItemList.size();
    }



}
