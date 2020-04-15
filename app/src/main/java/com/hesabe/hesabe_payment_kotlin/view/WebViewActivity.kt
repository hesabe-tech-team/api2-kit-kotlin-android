package com.hesabe.hesabe_payment_kotlin.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.hesabe.hesabe_payment_kotlin.R
import com.hesabe.hesabe_payment_kotlin.aes.HesabeCrypt
import com.hesabe.hesabe_payment_kotlin.api.Constants
import kotlinx.android.synthetic.main.activity_web_view.*
import org.json.JSONObject

class WebViewActivity : AppCompatActivity() {

    private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        /* Get the Payment URL passed from MainActivity */
        val url = intent.getStringExtra("url")

        /* Load the URL in WebView */
        redirectToPayment(url)

    }

    private fun redirectToPayment(url: String?) {
        web_view.loadUrl(url)
        val webSettings: WebSettings = web_view.settings
        webSettings.javaScriptEnabled = true
        web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (!flag) {
                    view.loadUrl(url)
                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                /* Check if URL contains the value given at 'responseUrl' field */
                if (url.contains("http://my.site.com/result/")) {
                    flag = true

                    /* If yes, parse the result */
                    parseResult(url)
                }
            }
        }
    }

    private fun parseResult(url:String) {

        val parse = Uri.parse(url)
        val data = parse.getQueryParameter("data")

        if(data != null) {
            /* Decrypt the result */
            val decrypt: ByteArray? = HesabeCrypt.decrypt(data, Constants.SECRET_KEY, Constants.IV_KEY)
            val decryptedData = decrypt?.let { String(it).replace("\\s+".toRegex(), " ").trim { it <= ' ' } }

            if(decryptedData != null) {
                /* Get the decrypted data as a JSONObject */
                val decryptedObject = JSONObject(decryptedData).getJSONObject("response")

                /* Get the Result code */
                val resultCode = decryptedObject.getString("resultCode")

                /* Get other details */
                val amount = decryptedObject.getString("amount")
                val paymentToken = decryptedObject.getString("paymentToken")
                val paymentId = decryptedObject.getString("paymentId")
                val administrativeCharge = decryptedObject.getString("administrativeCharge")
                val orderReferenceNumber = decryptedObject.getString("orderReferenceNumber")

                /* Pass the details to Result Activity */
                val intent = Intent(baseContext, ResultActivity::class.java)
                intent.putExtra("resultCode", resultCode)
                intent.putExtra("amount", amount)
                intent.putExtra("paymentToken", paymentToken)
                intent.putExtra("paymentId", paymentId)
                intent.putExtra("administrativeCharge", administrativeCharge)
                intent.putExtra("orderReferenceNumber", orderReferenceNumber)
                startActivity(intent)
                finish()
            }
        }
    }

}
