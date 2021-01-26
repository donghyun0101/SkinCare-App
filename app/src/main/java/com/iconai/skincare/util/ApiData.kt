package com.iconai.skincare.util

import android.graphics.Bitmap
import org.json.JSONObject
import java.io.File

object ApiData {
    var skinType: String? = null
    var skinConcerns: ArrayList<String> = ArrayList()
    var gender: String? = null
    var age: String? = null
    var imgFaceBitmap: Bitmap? = null
    var imgFaceFile: File? = null
    var faceData: JSONObject? = null

}
