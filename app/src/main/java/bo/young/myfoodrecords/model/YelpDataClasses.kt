package bo.young.myfoodrecords.model

import com.google.gson.annotations.SerializedName

data class YelpSearchResult(
        @SerializedName("total") val total: Int,
        @SerializedName("businesses") val restaurants: List<YelpRestaurants>
)

data class YelpRestaurants(
        val name: String,
        val rating: Double,
        val price: String,
        @SerializedName("review_count") val numReview: Int,
        @SerializedName("distance") val distanceInMeters: Double,
        @SerializedName("image_url") val imageUrl: String,
        @SerializedName("url") val url: String,
        val categories: List<YelpCategory>,
        val location: YelpLocation
) {
    fun displayDistanceInMile(): String {
        val milesPerMeter = 0.000621371
        val distanceInMiles = "%.2f".format(distanceInMeters * milesPerMeter)
        return "$distanceInMiles mi"
    }
    fun displayDistanceInKm(): String {
        val distanceInMiles = "%.2f".format(distanceInMeters / 1000)
        return "$distanceInMiles km"
    }
}

data class YelpCategory (
        val title: String
)
data class YelpLocation(
        @SerializedName("address1") val address: String
)