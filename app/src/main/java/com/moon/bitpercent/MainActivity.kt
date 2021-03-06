package com.moon.bitpercent

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.moon.bitpercent.databinding.ActivityMainBinding
import com.moon.bitpercent.room.BitDao
import com.moon.bitpercent.room.BitDatabase
import com.moon.bitpercent.room.BitEntity
import kotlinx.android.synthetic.main.item_info.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var isPlus = true
    private val bitAdapter = BitAdapter(arrayListOf())
    private var exitDialog: Dialog? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = BitDatabase.getInstance(this).bitDao()
        val repository = BitRepository.getInstance(dao)
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(
            this,
            factory
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

            bitList.observe(this@MainActivity, Observer { list ->
                bitAdapter.setItems(list)
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
            adapter = bitAdapter
        }

        binding.save.setOnClickListener {
            if (binding.editprice.text.toString().isEmpty() || binding.editpercent.text.toString()
                    .isEmpty()
            ) {
                Toast.makeText(this, R.string.invalid_price_percent, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.insertBit(
                BitEntity(
                    type = binding.type.text.toString(),
                    price = binding.editprice.text.toString().toDouble(),
                    percent = binding.editpercent.text.toString().toDouble(),
                    result = binding.editresult.text.toString().toDouble()
                )
            )
        }

        MobileAds.initialize(this)

        binding.adView.loadAd(AdRequest.Builder().build())
        exitDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.exit_dialog)
        }
        exitDialog?.findViewById<Button>(R.id.review)?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        }
        exitDialog?.findViewById<Button>(R.id.exit)?.setOnClickListener {
            finish()
        }
        //Test
//        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
        val adLoader = AdLoader.Builder(this, "ca-app-pub-8549606613390169/9913918777")
            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                exitDialog?.findViewById<TemplateView>(R.id.template)?.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())

    }

    override fun onBackPressed() {
        exitDialog?.show()
    }


    companion object {
        val TAG = "BitPercent"
    }

}

class MainViewModel(private val bitRepository: BitRepository) : ViewModel() {
    var price = MutableLiveData<Double>()
    var percent = MutableLiveData<Double>()

    var bitList: LiveData<List<BitEntity>> = bitRepository.getBits()

    fun insertBit(bit: BitEntity) {
        viewModelScope.launch {
            bitRepository.insert(bit)
        }
    }
}

class MainViewModelFactory(private val bitRepository: BitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(bitRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }

}

class BitRepository private constructor(private val bitDao: BitDao) {
    fun getBits() = bitDao.getAllBit()
    suspend fun insert(bit: BitEntity) {
        withContext(Dispatchers.IO) {
            bitDao.insert(bit)
        }
    }

    companion object {
        @Volatile
        private var instance: BitRepository? = null

        fun getInstance(inputMsgDao: BitDao) =
            instance ?: synchronized(this) {
                instance ?: BitRepository(inputMsgDao).also { instance = it }
            }
    }
}