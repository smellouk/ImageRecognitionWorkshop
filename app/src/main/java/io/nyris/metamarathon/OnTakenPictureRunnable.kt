package io.nyris.metamarathon

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
abstract class OnTakenPictureRunnable : Runnable{
    var resizedImage : ByteArray? = null
    var originalImage : ByteArray? = null

    override fun run() {
    }
}