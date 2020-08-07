package it.wazabit.dev.extensions

import android.graphics.Bitmap
import android.graphics.Matrix



fun Bitmap.rotate(angle:Float): Bitmap = Bitmap.createBitmap(this,0,0,width,height,
    Matrix().apply { postRotate(angle) },true)

fun Bitmap.flip(x:Float = 1.0f,y:Float = 1.0f): Bitmap = Bitmap.createBitmap(this,0,0,width,height,
    Matrix().apply { preScale(x,y) },true)