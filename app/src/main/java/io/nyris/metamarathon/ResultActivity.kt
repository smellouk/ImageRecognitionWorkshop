package io.nyris.metamarathon

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import io.nyris.sdk.disposable
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    private var mAdapter: OfferListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_result)
        val myApp = application as MyApp
        val nyris = myApp.nyris
        lifecycle.addObserver(nyris)

        mAdapter = OfferListAdapter()
        rcOffers.adapter = mAdapter

        val image = intent.extras.getByteArray("TAKEN_IMAGE")
        nyris
            .imageMatching()
            /*.exact(false)
            .similarity(true) // Activate only similarity search
            .ocr(false)*/
            /*.similarityThreshold(0.4F)*/ //return mOffers only with score equal or above 0.7F
            .match(image)
            .subscribe({
                pProgress.visibility = View.GONE
                if(it.offers.isEmpty()){
                    Toast.makeText(this@ResultActivity, "No offers found", Toast.LENGTH_LONG).show()
                    return@subscribe
                }
                mAdapter?.setOffers(it.offers)
            },{
                pProgress.visibility = View.GONE
                Toast.makeText(this@ResultActivity, it.message, Toast.LENGTH_LONG).show()
            }).disposable()
    }
}
