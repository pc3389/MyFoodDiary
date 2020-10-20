package bo.young.myfoodrecords.fragments

import android.content.Context
import android.example.myfoodrecords.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import bo.young.myfoodrecords.adapter.RestaurantsAdapter
import bo.young.myfoodrecords.model.YelpRestaurants
import bo.young.myfoodrecords.model.YelpSearchResult
import bo.young.myfoodrecords.utils.YelpService
import kotlinx.android.synthetic.main.fragment_yelp.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "YelpFragment"
private const val BASE_URL = "https://api.yelp.com/v3/"

class YelpFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_yelp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api_key = getString(R.string.yelp_api_key)
        restaurant_button.setOnClickListener{
            search(view, api_key)
        }
        restaurant_search_location_tv.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEND) {
                search(view, api_key)
                true
            } else{
                false
            }
        }
    }

    private fun updateRecyclerView(api_key: String, searchTerm: String, location: String) {
        val restaurants = mutableListOf<YelpRestaurants>()
        val adapter = RestaurantsAdapter(requireContext(), restaurants)

        restaurants_rc.adapter = adapter
        restaurants_rc.layoutManager = LinearLayoutManager(requireContext())

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val yelpService = retrofit.create(YelpService::class.java)
        yelpService.searchRestaurants("Bearer $api_key", searchTerm, location)
                .enqueue(object : Callback<YelpSearchResult> {
                    override fun onResponse(
                            call: Call<YelpSearchResult>,
                            response: Response<YelpSearchResult>
                    ) {
                        Log.i(TAG, "onResponse $response")
                        val body = response.body()
                        if (body == null) {
                            Log.w(TAG, "Did not receive")
                            return
                        }
                        restaurants.addAll(body.restaurants)
                        adapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                        Log.i(TAG, "onResponse $t")
                    }
                })
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun search(view: View, api_key: String) {
        if(restaurant_search_food_name_tv.text.isEmpty() || restaurant_search_location_tv.text.isEmpty()) {
            Toast.makeText(requireContext(), R.string.require_name_and_location, Toast.LENGTH_SHORT).show()
            return
        }
        val searchTerm = restaurant_search_food_name_tv.text.toString()
        val location = restaurant_search_location_tv.text.toString()
        updateRecyclerView(api_key, searchTerm, location)
        hideKeyboard(view)
    }
}