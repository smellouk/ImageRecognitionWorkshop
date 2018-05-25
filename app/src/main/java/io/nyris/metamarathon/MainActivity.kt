package io.nyris.metamarathon

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks{
    companion object {
        const val RC_CAMERA_PERM = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        EasyPermissions.requestPermissions(
                this@MainActivity,
                "Camera Permission",
                RC_CAMERA_PERM,
                Manifest.permission.CAMERA)
    }

    private fun initView(){
        val onTakenPictureRunnable = object : OnTakenPictureRunnable() {
            override fun run() {
                val intent = Intent(this@MainActivity, ResultActivity::class.java)
                val bundle = Bundle()
                bundle.putBoolean("EXACT", cbExact.isChecked)
                bundle.putBoolean("SIMILARITY", cbSimilarity.isChecked)
                bundle.putBoolean("OCR", cbOcr.isChecked)
                bundle.putByteArray("TAKEN_IMAGE", resizedImage)
                intent.putExtras(bundle)

                startActivity(intent)
            }
        }

        cameraView.addCallback(CameraCallback(onTakenPictureRunnable))
        fabCamera.setOnClickListener({
            cameraView.takePicture()
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
    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
    }
}
