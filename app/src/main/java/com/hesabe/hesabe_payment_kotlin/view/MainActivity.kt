package com.hesabe.hesabe_payment_kotlin.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hesabe.hesabe_payment_kotlin.R
import com.hesabe.hesabe_payment_kotlin.aes.HesabeCrypt
import com.hesabe.hesabe_payment_kotlin.api.ApiInterface
import com.hesabe.hesabe_payment_kotlin.api.Constants
import com.hesabe.hesabe_payment_kotlin.api.RetrofitClientInstance
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Click listener to start the payment */
        btn_start_payment.setOnClickListener {
            checkout()
        }
    }

    private fun checkout() {
        /* Get payment request object*/
        val obj = getPaymentRequestObject()

        /* Encrypt the Data */
        val encryptedData = HesabeCrypt.encrypt(obj.toString(), Constants.SECRET_KEY, Constants.IV_KEY)

        /* Check out the request using any REST Client (in our case we are using Retrofit) */
        if (encryptedData != null) {
            checkoutRequest(encryptedData)
        }
    }

    private fun getPaymentRequestObject(): JSONObject {
        val obj = JSONObject()
        obj.put("merchantCode", Constants.MERCHANT_CODE) //Use merchant code
        obj.put("amount", "2.000") //Total amount
        obj.put("paymentType", "0") //Type of the payment
        obj.put("responseUrl", "http://my.site.com/result/") //Given URL end point will be used to check the result
        obj.put("failureUrl", "http://my.site.com/result/")
        obj.put("version", "2.0") //Hesabe Payment Gateway version
        obj.put("orderReferenceNumber", "OR-12345")
        obj.put("variable1", "")
        obj.put("variable2", "")
        obj.put("variable3", "")
        obj.put("variable4", "")
        obj.put("variable5", "")
        return obj
    }

    private fun checkoutRequest(encryptedData: String) {
        val service: ApiInterface? = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiInterface::class.java)
        val call = service!!.hesabePay(Constants.ACCESS_CODE, encryptedData)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?> ) {
                if (response.body() != null) {
                    processResponse(response.body()!!)
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable ) {
                t.message
            }
        })
    }

    private fun processResponse(response: String) {
        /* Decrypt Response */
        val decryptedResponse = HesabeCrypt.decrypt(response, Constants.SECRET_KEY, Constants.IV_KEY)
        val trimmedData = String(decryptedResponse!!).replace("\\s+".toRegex(), " ").trim { it <= ' ' }

        /* Get token from decrypted response */
        val responseToken = JSONObject(trimmedData).getJSONObject("response").getString("data")

        /* Create payment URL with response token */
        val paymentURL: String = Constants.BASE_URL +"/payment?data=" + responseToken

        /* Open WebView Activity to load the URL */
        redirectToPayment(paymentURL)
    }

    private fun redirectToPayment(paymentURL: String) {
        val intent = Intent(baseContext, WebViewActivity::class.java)
        intent.putExtra("url", paymentURL)
        startActivity(intent)
    }
}
