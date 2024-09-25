package com.example.nfcdemo

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.nfcdemo.ui.CardData
import com.example.nfcdemo.ui.MainActivityViewmodel
import com.example.nfcdemo.ui.theme.NFCDEMOTheme
import com.example.parcelable
import com.vignesh.nfccardreader.NfcCardManager
import com.vignesh.nfccardreader.NfcCardReader


class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var nfcCardManager: NfcCardManager? = null
    private var nfcCardReader: NfcCardReader? = null
    lateinit var viewmodel: MainActivityViewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        viewmodel = ViewModelProvider(this)[MainActivityViewmodel::class.java]

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            viewmodel.upDateTheValue(CardData(isLoading = false, isNFCPresent = false))
        }
        nfcCardManager = NfcCardManager(this)
        nfcCardReader = NfcCardReader()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NFCDEMOTheme {
                val composeData = viewmodel.stateFlow
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CardInfo(modifier = Modifier.padding(innerPadding), composeData)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcCardManager?.enableDispatch();
        when (nfcAdapter?.isEnabled) {
            false -> {
                viewmodel.upDateTheValue(
                    CardData(
                        isNFCPresent = false,
                        isNFCEnable = false
                    )
                )
            }
            true -> {
                viewmodel.upDateTheValue(
                   viewmodel.stateFlow.copy(isNFCPresent = true)
                )
            }
            else -> {
                viewmodel.upDateTheValue(
                    CardData(
                        isLoading = false,
                        isNFCPresent = false,
                    )
                )
            }
        }
    }


    override fun onPause() {
        super.onPause()
        nfcCardManager?.disableDispatch();
    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!", modifier = modifier
        )
    }


    @Composable
    fun CardInfo(modifier: Modifier = Modifier, composeData: CardData) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (composeData.isLoading && composeData.isNFCPresent) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.scan))
                val progress by animateLottieCompositionAsState(composition)
                LottieAnimation(
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(300.dp),
                    composition = composition,
                    renderMode = RenderMode.AUTOMATIC
                )
            }
            if (!composeData.isNFCPresent) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_lottiess))
                    val progress by animateLottieCompositionAsState(composition)
                    LottieAnimation(
                        iterations = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        composition = composition,
                        renderMode = RenderMode.AUTOMATIC
                    )
                    val string =if (!composeData.isNFCEnable)"NFC Not Enabled" else "NFC Not Supported"
                    Text(
                        text = string,
                        modifier = Modifier.padding(top = 20.dp),
                        color = Color.Blue,
                    )

                }
            }
            AnimatedVisibility(visible = !composeData.isLoading && composeData.isNFCPresent) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(700.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RowText(
                        modifier = Modifier.padding(top = 250.dp),
                        key = "Card Number",
                        value = composeData.cradNumber ?: ""
                    )

                    RowText(
                        modifier = Modifier.padding(top = 25.dp),
                        key = "Expiry Date ",
                        value = composeData.experyDate ?: ""
                    )
                    RowText(
                        modifier = Modifier.padding(top = 25.dp),
                        key = "Card Type ",
                        value = composeData.cardType ?: ""
                    )
                    Spacer(modifier = Modifier.height(100.dp))

                    OutlinedButton(onClick = { viewmodel.upDateTheValue(CardData(isLoading = true)) }) {

                        Text(
                            text = "Re-Fetch it",
                            modifier = Modifier,
                            color = Color.Blue,
                        )
                    }
                }

            }
        }
    }

    @Composable
    fun RowText(modifier: Modifier = Modifier, key: String = "", value: String = "") {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = key)
            Text(text = " : ")
            Spacer(modifier = Modifier.width(50.dp))
            Text(text = value)
        }
    }

    @Composable
    @Preview
    fun Pro(modifier: Modifier = Modifier) {
        CardInfo(composeData = CardData("niscnksnskcs", "cksancknckancsa", true))
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        NFCDEMOTheme {
            Greeting("Android")
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // When the card is brought closer to the device, a new intent with TAG info is dispatched
        val tag = intent.parcelable<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            val cardResponse = nfcCardReader?.readCard(tag) // read the card data with tag
            if (cardResponse != null && cardResponse.emvCard != null) {
                val data = cardResponse.emvCard
                // use card data such as card number, expiry date etc
                Log.d("Tag", cardResponse.emvCard.cardNumber)
                Log.e("Tag", cardResponse.emvCard.expireDate)
                viewmodel.upDateTheValue(
                    CardData(
                        data.cardNumber, data.expireDate, false, cardType = data.applicationLabel
                    )
                )
            } else if (cardResponse != null && cardResponse.emvCard == null) {
                Toast.makeText(this, cardResponse.error.toString(), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error in reading the card", Toast.LENGTH_LONG).show()
            }
        }
    }
}
