package app.ronzano.zensino.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.ronzano.zensino.databinding.ActivityMainBinding
import app.ronzano.zensino.ui.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        binding.model = model
        binding.lifecycleOwner = this
        setContentView(binding.root)

    }
}