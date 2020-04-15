package com.hesabe.hesabe_payment_kotlin.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hesabe.hesabe_payment_kotlin.R
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        //get result
        val resultCode = intent.getStringExtra("resultCode")
        val amount = intent.getStringExtra("amount")
        val paymentId = intent.getStringExtra("paymentId")
        val administrativeCharge = intent.getStringExtra("administrativeCharge")
        val orderReferenceNumber = intent.getStringExtra("orderReferenceNumber")

        //set result
        tv_result_code.text = resultCode
        tv_amount.text = amount
        tv_payment_id.text = paymentId
        tv_administrative_charge.text = administrativeCharge
        tv_order_reference_number.text = orderReferenceNumber

        //button click listener to redirect home screen
        btn_finish.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
