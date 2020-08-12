package android.example.myfoodrecords;

import android.content.Context;
import android.content.Intent;
import android.example.myfoodrecords.activities.DetailActivity;
import android.example.myfoodrecords.model.Food;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder> {

    List<Food> foodList;
    Context context;


    public class SummaryViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodImageView;
        private TextView foodNameTextView;
        private TextView foodRatingTextView;
        private TextView foodDateTextView;
        private TextView foodTypeTextView;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImageView =itemView.findViewById(R.id.food_pic_iv);
            foodNameTextView = itemView.findViewById(R.id.food_name_tv);
            foodRatingTextView = itemView.findViewById(R.id.food_rating_tv);
            foodDateTextView = itemView.findViewById(R.id.food_date_tv);
            foodTypeTextView = itemView.findViewById(R.id.food_type_tv);
        }
    }

    public SummaryAdapter(List<Food> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new SummaryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        final Food food = foodList.get(position);

        holder.foodNameTextView.setText(food.getName());
        holder.foodTypeTextView.setText(food.getFoodType());
        holder.foodDateTextView.setText(food.getDate());
        holder.foodRatingTextView.setText(String.valueOf(food.getRating()));
        if(food.getPhotoPath() != null) {
            new PhotoUtil(food.getPhotoPath(), true);
            holder.foodImageView.setImageBitmap(PhotoUtil.setPic());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id", food.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }




}
