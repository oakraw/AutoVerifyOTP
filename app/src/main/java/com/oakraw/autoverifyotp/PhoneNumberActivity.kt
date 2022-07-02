package com.oakraw.autoverifyotp

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.oakraw.autoverifyotp.databinding.ActivityPhoneNumberBinding

class PhoneNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneNumberBinding

    private val request: GetPhoneNumberHintIntentRequest =
        GetPhoneNumberHintIntentRequest.builder().build()

    private val phoneNumberLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                val phoneNumber =
                    Identity.getSignInClient(this).getPhoneNumberFromIntent(result.data)
                binding.etPhone.setText(phoneNumber)
            } catch (e: ApiException) {
                showError(e)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btSubmit.setOnClickListener {
            val intent = Intent(this, OtpActivity::class.java)
            startActivity(intent)
        }

        requestPhoneNumber()

        var appSignature = AppSignatureHelper(this)
        Log.d("App Signatures", appSignature.appSignatures.toString())
    }

    private fun requestPhoneNumber() {
        Identity.getSignInClient(this)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener { result ->
                try {
                    phoneNumberLauncher.launch(IntentSenderRequest.Builder(result).build())
                } catch (e: ActivityNotFoundException) {
                    showError(e)
                }
            }
            .addOnFailureListener { e ->
                showError(e)
            }
    }

    private fun showError(e: Throwable) {
        binding.layoutPhoneNumber.error = e.message
    }

}