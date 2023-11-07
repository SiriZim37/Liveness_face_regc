package com.siri.livenessfr.common.extension

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.siri.livenessfr.util.ImageUtils


fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun ViewGroup.gone() {
    this.visibility = View.GONE
}

fun ViewGroup.visible(body: () -> Unit) {
    this.visibility = View.VISIBLE
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun ViewGroup.isVisible() = this.visibility == View.VISIBLE

fun View.captureToExternalStorage(applicationContext: Context): Uri? {
    isDrawingCacheEnabled = true
    val bitmap = ImageUtils.loadBitmapFromView(this)
    isDrawingCacheEnabled = false
    return ImageUtils.saveToExternalStorage(applicationContext, bitmap)
}

fun View.captureToExternalStorageAbsolute(applicationContext: Context): Uri? {
    isDrawingCacheEnabled = true
    val bitmap = ImageUtils.loadBitmapFromView(this)
    isDrawingCacheEnabled = false
    return ImageUtils.saveToExternalStorageAbsolute(applicationContext, bitmap)
}

fun View.captureScreenToBitmap(applicationContext: Context) : Bitmap {
    isDrawingCacheEnabled = true
    val bitmap = ImageUtils.loadBitmapFromView(this)
    isDrawingCacheEnabled = false
    return bitmap
}