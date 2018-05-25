package io.nyris.metamarathon

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import de.nyris.camera.ImageHelper
import de.nyris.camera.Size
import io.nyris.metamarathon.PinViewCropper.PinViewCropper
import io.nyris.metamarathon.PinViewCropper.util.HandleUtil
import io.nyris.sdk.INyris
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions
import android.support.v4.app.NotificationCompat.getExtras
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, PinViewCropper.IFinishedScaleAnimation{
    companion object {
        const val RC_CAMERA_PERM = 123
    }

    lateinit var bitmapForCropping : Bitmap

    var srcSize: Size? = null

    var destSize: Size? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myApp = application as MyApp
        val nyris = myApp.nyris

        initView(nyris)

        EasyPermissions.requestPermissions(
                this@MainActivity,
                "Camera Permission",
                RC_CAMERA_PERM,
                Manifest.permission.CAMERA)
    }

    private fun initView(nyris : INyris){
        val onTakenPictureRunnable = object : OnTakenPictureRunnable() {
            override fun run() {
                val resizedImageBtm = BitmapFactory.decodeByteArray(resizedImage, 0, resizedImage!!.size)
                bitmapForCropping = BitmapFactory.decodeByteArray(originalImage, 0, originalImage!!.size)

                if (srcSize == null) {
                    srcSize = Size(resizedImageBtm.width, resizedImageBtm.height)
                }
                if (destSize == null) {
                    destSize = Size(bitmapForCropping.width, bitmapForCropping.height)
                }

                extractObjects(nyris, resizedImage!!)
            }
        }

        cameraView.addCallback(CameraCallback(onTakenPictureRunnable))
        fabCamera.setOnClickListener({
            cameraView.takePicture()
            cameraView.stopPreview()
        })

        viewPinCropper.addOnPinClickListener { rectF ->
            viewPinCropper.initCropWindow(rectF, this@MainActivity)
        }
    }

    fun extractObjects(nyris : INyris, image : ByteArray){
        nyris
            .objectProposal()
            .extractObjects(image)
            .subscribe({
                if(it.isEmpty()){
                    Toast.makeText(this@MainActivity, "No objects found", Toast.LENGTH_LONG).show()
                    return@subscribe
                }

                fabCamera.visibility = View.GONE
                viewPinCropper.visibility = View.VISIBLE
                val srcWidth = srcSize!!.width
                val srcHeight = srcSize!!.height
                val destWidth = destSize!!.width
                val destHeight = destSize!!.height
                val listRegions = mutableListOf<RectF>()
                for (objectProposal in it) {
                    val region = objectProposal.region!!
                    val rect = RectF(region.left,
                            region.top,
                            region.right,
                            region.bottom)

                    HandleUtil.normalizeRectF(rect, srcWidth, srcHeight,
                            destWidth, destHeight)
                    listRegions.add(rect)
                }

                viewPinCropper.setExtractedObjects(listRegions)
                //viewPinCropper.initCropWindow(listRegions[0])
            }, {
                Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
            })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        onResume()
    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
        viewPinCropper.removePinsView()
        viewPinCropper.visibility = View.GONE
        fabCamera.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
    }

    override fun onFinish(rectF: RectF?) {
        var bitmap = viewPinCropper.getCroppedImage(bitmapForCropping, rectF)
        bitmap = ImageHelper.resize(this, bitmap, 512,512)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val resizedImage = stream.toByteArray()
        bitmap.recycle()

        val intent = Intent(this@MainActivity, ResultActivity::class.java)
        val bundle = Bundle()
        bundle.putByteArray("TAKEN_IMAGE", resizedImage)
        intent.putExtras(bundle)

        startActivity(intent)
    }
}
