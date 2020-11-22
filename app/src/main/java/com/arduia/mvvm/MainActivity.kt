package com.arduia.mvvm

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.arduia.mvvm.databinding.ActivityMainBinding
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupViewModel()
    }

    private fun setupView(){
        binding.btnRotate.setOnClickListener {
            viewModel.rotate()
        }

        binding.btnToast.setOnClickListener {
            viewModel.showMessage(getString(R.string.sample_msg))
        }

        binding.cbChecked.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) viewModel.setAvailableOn()
            else viewModel.setAvailableOff()
        }
    }

    private fun setupViewModel(){

        viewModel.isAvailable.observe(this){
            binding.btnToast.isEnabled = it
            binding.btnRotate.isEnabled = it
            binding.cbChecked.isChecked = it
        }

        viewModel.onRotate.observe(this, EventObserver{
            inverseCurrentScreenOrientation()
        })

        viewModel.onToastShow.observe(this, EventObserver{ msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })

        viewModel.nullInitialTest.observe(this, Observer {
            if(it== null) throw NullPointerException("npe")
        })
    }



    private fun inverseCurrentScreenOrientation(){
        requestedOrientation = when(getOrientation() == 2){
            true -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun getOrientation() = resources.configuration.orientation
}