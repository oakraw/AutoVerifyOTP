package com.oakraw.autoverifyotp

import android.content.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.*


class SMSBroadcastReceiver : BroadcastReceiver() {

    private var otpReceiver: OTPReceiveListener? = null

    fun initOTPListener(receiver: OTPReceiveListener) {
        this.otpReceiver = receiver
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    var message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    if (otpReceiver != null) {
                        val pattern = Pattern.compile("(\\d{6})")
                        val matcher = pattern.matcher(message)

                        if (matcher.find()) {
                            val otp = matcher.group(0)
                            otpReceiver?.onOTPReceived(otp)
                        }

                    }
                }

                CommonStatusCodes.TIMEOUT ->
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    otpReceiver?.onOTPTimeOut()
            }
        }
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut()
    }
}