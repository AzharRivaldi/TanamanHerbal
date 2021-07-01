package com.azhar.tanamanherbal.activities

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.azhar.tanamanherbal.R
import com.azhar.tanamanherbal.adapter.MainAdapter
import com.azhar.tanamanherbal.model.ModelMain
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class MainActivity : AppCompatActivity() {

    var modelMain: MutableList<ModelMain> = ArrayList()
    lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set transparent statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        //transparent background searchview
        val searchPlateId = searchTanaman.getContext()
                .resources.getIdentifier("android:id/search_plate", null, null)
        val searchPlate = searchTanaman.findViewById<View>(searchPlateId)
        searchPlate.setBackgroundColor(Color.TRANSPARENT)
        searchTanaman.setImeOptions(EditorInfo.IME_ACTION_DONE)

        searchTanaman.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mainAdapter.filter.filter(newText)
                return true
            }
        })

        mainAdapter = MainAdapter(this, modelMain)
        rvListTanaman.setLayoutManager(LinearLayoutManager(this))
        rvListTanaman.setHasFixedSize(true)
        rvListTanaman.adapter = mainAdapter

        //get data json
        getNamaTanaman()
    }

    fun getNamaTanaman() {
        try {
            val stream = assets.open("tanaman_herbal.json")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            val strContent = String(buffer, StandardCharsets.UTF_8)
            try {
                val jsonObject = JSONObject(strContent)
                val jsonArray = jsonObject.getJSONArray("daftar_tanaman")
                for (i in 0 until jsonArray.length()) {
                    val jsonObjectData = jsonArray.getJSONObject(i)
                    val dataApi = ModelMain()
                    dataApi.nama = jsonObjectData.getString("nama")
                    dataApi.deskripsi = jsonObjectData.getString("deskripsi")
                    dataApi.image = jsonObjectData.getString("image_url")
                    modelMain.add(dataApi)
                }
                mainAdapter.notifyDataSetChanged()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } catch (ignored: IOException) {
            Toast.makeText(this@MainActivity,
                    "Oops, ada yang tidak beres. Coba ulangi beberapa saat lagi.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val window = activity.window
            val layoutParams = window.attributes
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
    }

}