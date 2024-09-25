package com.vignesh.nfccardreader

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB

class NfcCardManager(private val activity: Activity) {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private val pendingIntent: PendingIntent = PendingIntent.getActivity(
        activity, 0,
        Intent(activity, activity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
        PendingIntent.FLAG_MUTABLE
    )

    companion object {
        private val INTENT_FILTER = arrayOf(
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        )
        private val TECH_LIST = arrayOf(
            arrayOf(
                NfcA::class.java.name,
                NfcB::class.java.name,
                IsoDep::class.java.name
            )
        )
    }

    fun disableDispatch() {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    fun enableDispatch() {
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, INTENT_FILTER, TECH_LIST)
    }
}
