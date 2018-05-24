package io.nyris.metamarathon

import android.os.Handler
import de.nyris.camera.Callback
import de.nyris.camera.CameraView

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class CameraCallback(private val runnable : OnTakenPictureRunnable) : Callback{
    val handler = Handler()

    override fun onPictureTaken(p0: CameraView?, resizedImage: ByteArray?) {
        runnable.resizedImage = resizedImage
        handler.post(runnable)
    }

    override fun onPictureTakenOriginal(p0: CameraView?, originalImage: ByteArray?) {
        runnable.originalImage = originalImage
    }

    override fun isWithExif(): Boolean {
        return false
    }

    override fun onError(p0: String?) {
    }

    override fun onCameraOpened(p0: CameraView?) {
    }

    override fun onCameraClosed(p0: CameraView?) {
    }

}