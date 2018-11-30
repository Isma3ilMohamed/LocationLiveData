package com.thedevwolf.locationlivedata

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log


import androidx.lifecycle.Observer


import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog= ProgressDialog(this)



        btn.cornerRadius = 150
        btn.setOnClickListener {
            getLocationUpdates()
        }

    }


    @SuppressLint("LogNotTimber")
    private fun getLocationUpdates() {
       /* progressDialog.setTitle("Gathering location")
        progressDialog.setMessage("please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()*/

        CurrentLocation.getInstance(this).observe(this, Observer { location ->

            if (location!=null){
                //progressDialog.dismiss()
                Toast.makeText(this, "Location is : ${location.latitude} : ${location.longitude}", Toast.LENGTH_SHORT)
                .show()
            }

            //Log.d(javaClass.simpleName, "City Changed " + currentAddress.getAddressElement(1,location))

        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    getLocationUpdates()
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(this, " location enabled refused by user", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

