package com.thedevwolf.locationlivedata

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

abstract class  BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (fullScreen()){
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }


        setContentView(getLayoutRes())
        if (hideInputType()){
            hideInput()
        }

        init(savedInstanceState)
    }

    protected abstract fun init(savedInstanceState: Bundle?)
    protected abstract fun getLayoutRes():Int
    protected abstract fun fullScreen():Boolean
    protected abstract fun hideInputType():Boolean
    private fun hideInput(){
        if (currentFocus!=null){
            val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken,0)
        }
    }

    override fun attachBaseContext(newBase: Context?) {

        //super.attachBaseContext(Utils.changeLang(GlobalPreference.getInstance(newBase!!).getAppLanguage(),newBase))
    }

}