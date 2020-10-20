package bo.young.myfoodrecords.adapter

import android.content.Context
import android.content.Intent
import android.example.myfoodrecords.R
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bo.young.myfoodrecords.model.YelpRestaurants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_restaurant.view.*

class RestaurantsAdapter(val context: Context, val restaurants: List<YelpRestaurants>):
        RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(restaurant: YelpRestaurants) {
            itemView.restaurant_name_tv.text = restaurant.name
            itemView.restaurant_ratingBar.rating = restaurant.rating.toFloat()
            val numReview: String = "${restaurant.numReview} Reviews"
            itemView.restaurant_numReview_tv.text = numReview
            itemView.restaurant_address_tv.text = restaurant.location.address
            itemView.restaurant_category_tv.text = restaurant.categories[0].title
            itemView.restaurant_distance_tv.text = restaurant.displayDistanceInKm()
            itemView.restaurant_price_tv.text = restaurant.price
            Glide.with(context).load(restaurant.imageUrl).apply(RequestOptions().transform(
                    CenterCrop(), RoundedCorners(20)
            )).into(itemView.restaurant_iv)
            itemView.setOnClickListener {
                val uri = Uri.parse(restaurant.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.bind(restaurant)
    }

    override fun getItemCount() = restaurants.size

}
