package com.moon.bitpercent

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moon.bitpercent.data.Bit
import com.moon.bitpercent.databinding.ActivityMainBinding
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var isPlus = true

    @RequiresApi(Build.VERSION_CODES.O)
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

                percentDecimal = if (isPlus) {
                    percentDecimal.plus(BigDecimal.ONE)
                } else {
                    BigDecimal.ONE.minus(percentDecimal)
                }
                Log.i(TAG, "isPlus:$isPlus percentDecimal:$percentDecimal")
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

                percentDecimal = if (isPlus) {
                    percentDecimal.plus(BigDecimal.ONE)
                } else {
                    BigDecimal.ONE.minus(percentDecimal)

                }
                Log.i(TAG, "isPlus:$isPlus percentDecimal:$percentDecimal")
                binding.editresult.setText((priceDecimal.multiply(percentDecimal)).toString())
            })
        }

        binding.editprice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.editprice.text?.toString()?.run {
                    if (this.isNotEmpty()) {
                        viewModel.price.postValue(this.toDouble())
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.editpercent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.editpercent.text?.toString()?.run {
                    if (this.isNotEmpty()) {
                        viewModel.percent.postValue(this.toDouble())
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            isPlus = R.id.plus == id
            viewModel.percent.postValue(viewModel.percent.value)
        }

        binding.recyclerview.run {
            adapter = BitAdapter(null)
//            val list = ArrayList<Bit>()
//            list.add(Bit(100.0, 3.0, 103.0, "BitCoin", null))
//            list.add(Bit(100.0, 3.0, 103.0, "BitCoin", null))
//            list.add(Bit(100.0, 3.0, 103.0, "BitCoin", null))
//            list.add(Bit(100.0, 3.0, 103.0, "BitCoin", null))
//            adapter = BitAdapter(list)
        }
    }

    companion object {
        val TAG = "BitPercent"
    }

}

class MainViewModel : ViewModel() {
    var price = MutableLiveData<Double>()
    var percent = MutableLiveData<Double>()
}