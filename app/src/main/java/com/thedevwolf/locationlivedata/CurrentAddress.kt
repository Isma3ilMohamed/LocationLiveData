package com.thedevwolf.locationlivedata

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.util.*
import java.io.IOException



class CurrentAddress(private val mContext:Context, locale:String) {
    var geocoder: Geocoder = Geocoder(mContext, Locale(locale))


    fun getAddressElement(elementCode: Int, location: Location): String? {

        val addressList: List<Address>?
        val address: Address
        var elementString: String? = null

        try {

            addressList = geocoder.getFromLocation(
                location.latitude, location.longitude,
                1
            ) // We only want one address to be returned.

        } catch (e: IOException) {
            // Catch network or other IO problems
            return mContext.getString(R.string.location_not_available)
        } catch (e: IllegalArgumentException) {
            // Catch invalid latitude or longitude values
            return mContext.getString(R.string.invalid_latLong)
        }

        // Handle case where no address is found
        if (addressList == null || addressList.isEmpty()) {
            return mContext.getString(R.string.address_not_found)
        } else {
            // Create the Address object from the address list
            address = addressList[0]
        }

        // Get the specific address element requested by the caller
        when (elementCode) {

            AddressCodes.ADMIN_AREA -> elementString = address.adminArea
            AddressCodes.CITY_NAME -> elementString = address.locality
            AddressCodes.COUNTRY_CODE -> elementString = address.countryCode
            AddressCodes.COUNTRY_NAME -> elementString = address.countryName
            AddressCodes.FEATURE_NAME -> elementString = address.featureName
            AddressCodes.FULL_ADDRESS -> elementString = address.toString()
            AddressCodes.PHONE_NUMBER -> elementString = address.phone
            AddressCodes.POST_CODE -> elementString = address.postalCode
            AddressCodes.PREMISES -> elementString = address.premises
            AddressCodes.STREET_NAME -> elementString = address.thoroughfare
            AddressCodes.SUB_ADMIN_AREA -> elementString = address.subAdminArea
            AddressCodes.SUB_THOROUGHFARE -> elementString = address.subThoroughfare
            else -> elementString = mContext.getString(R.string.geocode_invalid_element)
        }

        return elementString
    }


    object AddressCodes {
        const val ADMIN_AREA = 0
        const val CITY_NAME = 1
        const val COUNTRY_CODE = 2
        const val COUNTRY_NAME = 3
        const val FEATURE_NAME = 4
        const val FULL_ADDRESS = 5
        const val PHONE_NUMBER = 6
        const val POST_CODE = 7
        const val PREMISES = 8
        const val STREET_NAME = 9
        const val SUB_ADMIN_AREA = 10
        const val SUB_THOROUGHFARE = 11
    }
}