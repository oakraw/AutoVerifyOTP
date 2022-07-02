package com.oakraw.autoverifyotp

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.oakraw.autoverifyotp.databinding.ActivityOtpBinding

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding

    private var smsBroadcastReceiver: SMSBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startSMSRetrieverClient()

        smsBroadcastReceiver = SMSBroadcastReceiver()

        registerReceiver(
            smsBroadcastReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        )

        smsBroadcastReceiver?.initOTPListener(object : SMSBroadcastReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String) {
                binding.etOtp.setText(otp)
            }

            override fun onOTPTimeOut() {
            }
        })
    }

    private fun startSMSRetrieverClient() {
        val client = SmsRetriever.getClient(this)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener { aVoid -> }
        task.addOnFailureListener { e -> }
    }


    override fun onDestroy() {
        unregisterReceiver(smsBroadcastReceiver)
        super.onDestroy()
    }

}