package com.example.productfinder.features.stores

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class StoreRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentData = intent?.data
        if (intentData != null) {
            handleDeepLink(intentData)
        }
        finish()
    }

    private fun handleDeepLink(uri: Uri) {
        val product = uri.getQueryParameter("product") ?: return
        val store = uri.getQueryParameter("store") ?: "all"

        val storeUrl = when (store.lowercase()) {
            "mercadolibre" -> "https://www.mercadolibre.com.mx/search?q=$product"
            "amazon" -> "https://www.amazon.com.mx/s?k=$product"
            "ebay" -> "https://www.ebay.com/sch/i.html?_nkw=$product"
            else -> "https://www.google.com/search?tbm=shop&q=$product"
        }

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(browserIntent)
    }
}