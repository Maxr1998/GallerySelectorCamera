package de.Maxr1998.gallery_selector

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log


class ForwarderActivity : Activity() {

    private lateinit var outputUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 10)
        } else onRequestPermissionsResult(10, arrayOf(WRITE_EXTERNAL_STORAGE), arrayOf(PERMISSION_GRANTED).toIntArray())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permissions.firstOrNull() == WRITE_EXTERNAL_STORAGE && grantResults.firstOrNull() == PERMISSION_GRANTED)
            if (intent.action == MediaStore.ACTION_IMAGE_CAPTURE) {
                // Save output Uri
                outputUri = intent.extras[MediaStore.EXTRA_OUTPUT] as Uri
                // Open Gallery
                val select = Intent(Intent.ACTION_GET_CONTENT)
                select.type = "image/*"
                select.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(select, 0)
                return
            }
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val imageUri = data?.data
            imageUri?.let {
                Log.d("GallerySelector", "Copying " + it.toString() + " to " + outputUri.toString())
                val inputStream = contentResolver.openInputStream(it)
                val outputStream = contentResolver.openOutputStream(outputUri)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                setResult(RESULT_OK)
            }
        } else setResult(RESULT_CANCELED)
        finish()
    }
}