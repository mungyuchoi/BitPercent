package com.moon.bitpercent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moon.bitpercent.databinding.ActivityMainBinding
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        viewModel.run {
            price.observe(this@MainActivity, { price ->
                val priceDecimal = BigDecimal.valueOf(price)
                if (percent.value == null) {
                    return@observe
                }
                var percentDecimal = BigDecimal.valueOf(percent.value!!)
                percentDecimal = percentDecimal.divide(BigDecimal.TEN)
                percentDecimal = percentDecimal.divide(BigDecimal.TEN)
                percentDecimal = percentDecimal.plus(BigDecimal.ONE)
                binding.editresult.setText((priceDecimal.multiply(percentDecimal)).toString())
            })

            percent.observe(this@MainActivity, { percent ->
                if (price.value == null) {
                    return@observe
                }
                val priceDecimal = BigDecimal.valueOf(price.value!!)
                var percentDecimal = BigDecimal.valueOf(percent)
                percentDecimal = percentDecimal.divide(BigDecimal.TEN)
                percentDecimal = percentDecimal.divide(BigDecimal.TEN)
                percentDecimal = percentDecimal.plus(BigDecimal.ONE)
                binding.editresult.setText((priceDecimal.multiply(percentDecimal)).toString())
            })
        }

        binding.editprice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.price.postValue(binding.editprice.text.toString().toDouble())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.editpercent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.percent.postValue(binding.editpercent.text.toString().toDouble())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }


}

class MainViewModel : ViewModel() {
    var price = MutableLiveData<Double>()
    var percent = MutableLiveData<Double>()

}