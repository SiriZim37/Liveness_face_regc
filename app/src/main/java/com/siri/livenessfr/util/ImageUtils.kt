package com.siri.livenessfr.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.view.View
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


object ImageUtils {

    fun loadBitmapFromView(view: View): Bitmap {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.background

        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }

        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }
    fun saveToExternalStorage(applicationContext: Context, bitmap: Bitmap): Uri? {
        val directory = Environment.getExternalStorageDirectory()
        val dir = File("${directory.absolutePath}/my_images")
        dir.mkdirs()

        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        val outFile = File(dir, fileName)
        val outStream = FileOutputStream(outFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.flush()
        outStream.close()

        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(outFile)
        applicationContext.sendBroadcast(intent)

        return intent.data
    }

    fun saveToExternalStorageAbsolute(applicationContext: Context, bitmap: Bitmap): Uri? {
        val directory = Environment.getExternalStorageDirectory()
        val dir = File("${directory.absolutePath}/my_images")
        dir.mkdirs()

        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        val outFile = File(dir, fileName)
        val outStream = FileOutputStream(outFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.flush()
        outStream.close()

        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = FileProvider.getUriForFile(applicationContext, "${applicationContext.packageName}.provider", outFile)
        applicationContext.sendBroadcast(intent)

        return intent.data
    }


}