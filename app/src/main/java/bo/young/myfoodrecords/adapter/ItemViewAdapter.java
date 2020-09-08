package bo.young.myfoodrecords.adapter;

import android.content.Context;
import android.content.Intent;
import android.example.myfoodrecords.R;

import bo.young.myfoodrecords.activities.DetailActivity;
import bo.young.myfoodrecords.model.Food;
import bo.young.myfoodrecords.utils.Constants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemViewAdapter extends RecyclerView.Adapter<ItemViewAdapter.ItemViewHolder> {

    private List<Food> foodList;
    private Context context;



    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodImageView;
        private TextView foodNameTextView;
        private TextView foodRatingTextView;
        private TextView foodDateTextView;
        private TextView foodTypeTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImageView =itemView.findViewById(R.id.food_pic_iv);
            foodNameTextView = itemView.findViewById(R.id.food_name_tv);
            foodRatingTextView = itemView.findViewById(R.id.food_rating_tv);
            foodDateTextView = itemView.findViewById(R.id.food_date_tv);
            foodTypeTextView = itemView.findViewById(R.id.food_type_tv);
        }
    }

    public ItemViewAdapter(List<Food> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final Food food = foodList.get(position);

        holder.foodNameTextView.setText(food.getName());
        if(!food.getFoodType().equals(Constants.NO_TYPE)) {
            holder.foodTypeTextView.setText(food.getFoodType());
        }
        holder.foodDateTextView.setText(food.getDate());
        holder.foodRatingTextView.setText(String.valueOf(food.getRating()));
        if(food.getPhotoPath() != null) {
            Glide.with(context)
                    .load(food.getPhotoPath())
                    .into(holder.foodImageView);
        } else {
            Glide.with(context)
                    .load(R.mipmap.ic_no_food)
                    .into(holder.foodImageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Constants.KEY_ITEM_FOOD_ID, food.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (foodList == null) {
            return 0;
        } else {
            return foodList.size();
        }
    }




}
