package io.nyris.metamarathon

import android.app.Application
import io.nyris.sdk.INyris
import io.nyris.sdk.Nyris

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class MyApp : Application(){
    lateinit var nyris : INyris
    override fun onCreate() {
        super.onCreate()
        nyris = Nyris.createInstance("fWydyMDsiNTHhaGjd9k1bYYUcsqybAyR", BuildConfig.DEBUG)
    }
}