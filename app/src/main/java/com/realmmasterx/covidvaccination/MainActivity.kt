package com.realmmasterx.covidvaccination

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var searchButton: Button
    private lateinit var pinCodeEdt: EditText
    lateinit var centersRV: RecyclerView
    private lateinit var centerRVAdapter: CenterRVAdapter
    private lateinit var centerList: List<CenterRVModal>
    private lateinit var loadingPB: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchButton = findViewById(R.id.idBtnSearch)
        pinCodeEdt = findViewById(R.id.idEdtPinCode)
        centersRV = findViewById(R.id.centersRV)
        loadingPB = findViewById(R.id.idPBLoading)
        centerList = ArrayList()
        searchButton.setOnClickListener {
            val pinCode = pinCodeEdt.text.toString()

            if (pinCode.length != 6) {
                Toast.makeText(this, "Please enter valid pin code", Toast.LENGTH_SHORT).show()
            } else {
                (centerList as ArrayList<CenterRVModal>).clear()
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(
                        this,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            loadingPB.setVisibility(View.VISIBLE)
                            val dateStr: String = """$dayOfMonth - ${monthOfYear + 1} - $year"""
                            getAppointments(pinCode, dateStr)
                        },
                        year,
                        month,
                        day
                )
                dpd.show()
            }
        }
    }
    private fun getAppointments(pinCode: String, date: String) {
        val url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=" + pinCode + "&date=" + date
        val queue = Volley.newRequestQueue(this@MainActivity)

        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
                    Log.e("TAG", "SUCCESS RESPONSE IS $response")
                    loadingPB.setVisibility(View.GONE)
                    try {
                        val centerArray = response.getJSONArray("centers")
                        if (centerArray.length() == 0) {
                            Toast.makeText(this, "No Center Found", Toast.LENGTH_SHORT).show()
                        }
                        for (i in 0 until centerArray.length()) {
                            val centerObj = centerArray.getJSONObject(i)
                            val centerName: String = centerObj.getString("name")
                            val centerAddress: String = centerObj.getString("address")
                            val centerFromTime: String = centerObj.getString("from")
                            val centerToTime: String = centerObj.getString("to")
                            val fee_type: String = centerObj.getString("fee_type")
                            val sessionObj = centerObj.getJSONArray("sessions").getJSONObject(0)
                            val ageLimit: Int = sessionObj.getInt("min_age_limit")
                            val vaccineName: String = sessionObj.getString("vaccine")
                            val avaliableCapacity: Int = sessionObj.getInt("available_capacity")
                            val center = CenterRVModal(
                                    centerName,
                                    centerAddress,
                                    centerFromTime,
                                    centerToTime,
                                    fee_type,
                                    ageLimit,
                                    vaccineName,
                                    avaliableCapacity
                            )
                            centerList = centerList + center
                        }
                        centerRVAdapter = CenterRVAdapter(centerList)
                        centersRV.layoutManager = LinearLayoutManager(this)
                        centersRV.adapter = centerRVAdapter
                        centerRVAdapter.notifyDataSetChanged()

                    } catch (e: JSONException) {
                        e.printStackTrace();
                    }
                },
                        { error ->
                            Log.e("TAG", "RESPONSE IS $error")
                            Toast.makeText(this@MainActivity, "Fail to get response", Toast.LENGTH_SHORT).show()
                        })
        queue.add(request)
    }
}