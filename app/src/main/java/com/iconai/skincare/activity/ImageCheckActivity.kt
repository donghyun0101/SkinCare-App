package com.iconai.skincare.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.iconai.skincare.R
import com.iconai.skincare.util.ApiData
import com.iconai.skincare.util.CountingRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit


class ImageCheckActivity : AppCompatActivity() {
    private var layoutImgCheck: RelativeLayout? = null
    private var imgImageCheckResult: ImageView? = null
    private var btnImageCheckResult: Button? = null
    private var btnImageCheckRetake: Button? = null
    private var snackBar: Snackbar? = null
    private var snackBarLayoutParams: RelativeLayout.LayoutParams? = null

    private var client: OkHttpClient? = null
    private var body: MultipartBody? = null
    private var request: Request? = null
    private var response: Response? = null
    private var responseCode: String? = null

    private var asyncDialog: ProgressDialog? = null

    private var errorScan: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagecheck)

        init()
    }

    fun init() {
        asyncDialog = ProgressDialog(this@ImageCheckActivity)
        asyncDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        asyncDialog?.setMessage("Please wait...")
        asyncDialog?.setCancelable(false)

        layoutImgCheck = findViewById(R.id.layout_imgcheck)
        imgImageCheckResult = findViewById(R.id.img_imgcheck_result)
        btnImageCheckResult = findViewById(R.id.btn_imagecheck_result)
        btnImageCheckRetake = findViewById(R.id.btn_imagecheck_retake)

        when (ApiData.gender) {
            "female" -> ApiData.gender = "female"
            "male" -> ApiData.gender = "male"
            else -> ApiData.gender = "female"
        }

        imgImageCheckResult?.setImageBitmap(ApiData.imgFaceBitmap)

        btnImageCheckResult?.setOnClickListener {
            CoroutineScope(Main).launch {
                asyncDialog?.show()

                withContext(IO) {
                    try {
                        client = OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.MINUTES)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build()


                        body = MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart(
                                "image", "skinCare.jpg",
                                RequestBody.create("application/octet-stream".toMediaTypeOrNull(), File(ApiData.imgFaceFile.toString()))
                            )
                            .addFormDataPart("partner_id", "jmlv6b2qtS")
//                            .addFormDataPart("gender", ApiData.gender.toString())
//                            .addFormDataPart("skintone", "0")
                            .build()

                        var requestBody = CountingRequestBody(body!!, listener)

                        request = Request.Builder()
                            //.url("https://partner-test.revieve.com/api/3/analyzeImage/?accept=application/json&Content-Type=multipart/form-data")
                            .url("https://partner-test.revieve.com/api/analyzeImage/3/?skintone=0&gender=${ApiData.gender.toString()}&components=masks,wrinkles,wrinkles_visualization,eyes,skin_sagging,skin_sagging_visualization,redness,redness_visualization,hyperpigmentation,hyperpigmentation_visualization,melasma,melasma_visualization,freckles,freckles_visualization,dark_spots,dark_spots_visualization,texture,texture_visualization,smoothness,radiance,dull_skin,shine,shine_visualization,uneven_skin_tone,uneven_skin_tone_visualization,pore_dilation")
                            .post(requestBody)
                            .build()

                        response = client?.newCall(request!!)?.execute()
                        responseCode = response?.code.toString()

                        ApiData.faceData = JSONObject(response?.body!!.string())

                        Log.e("response", "Response Code : $responseCode")
                        Log.e("response", "Response Data : ${ApiData.faceData}")

                        errorScan = true
                    } catch (e: Exception) {
                        errorScan = false
                        Log.e("response", "Error message : ${e.message}")
                    }
                }

                when (errorScan) {
                    true -> {
                        if (ApiData.faceData?.getString("results") != "null") {
                            startActivity(Intent(this@ImageCheckActivity, ResultActivity::class.java))
                            finish()
                        } else {
                            snackBar = Snackbar.make(window.decorView.rootView, "Noting face. Please take a selfie again.", Snackbar.LENGTH_LONG)
                            snackBarLayoutParams = RelativeLayout.LayoutParams(snackBar!!.view.layoutParams)
                            snackBarLayoutParams?.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                            snackBar!!.view.setPadding(0, 0, 0, 0)
                            snackBar!!.view.setBackgroundColor(ContextCompat.getColor(this@ImageCheckActivity, R.color.alert_red))
                            snackBar!!.view.layoutParams = snackBarLayoutParams
                            snackBar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                            snackBar!!.show()
                        }
                    }
                    false -> {
                        snackBar = Snackbar.make(window.decorView.rootView, "Server Time out. Please again.", Snackbar.LENGTH_LONG)
                        snackBarLayoutParams = RelativeLayout.LayoutParams(snackBar!!.view.layoutParams)
                        snackBarLayoutParams?.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        snackBar!!.view.setPadding(0, 0, 0, 0)
                        snackBar!!.view.setBackgroundColor(ContextCompat.getColor(this@ImageCheckActivity, R.color.alert_red))
                        snackBar!!.view.layoutParams = snackBarLayoutParams
                        snackBar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                        snackBar!!.show()
                    }
                }
                asyncDialog?.dismiss()
            }
        }

        btnImageCheckRetake?.setOnClickListener {
            it.isSelected = true

            ApiData.imgFaceBitmap = null
            ApiData.imgFaceFile = null

            startActivity(Intent(this, CameraActivity::class.java))
            finish()
        }

    }

    private val listener: CountingRequestBody.Listener = object : CountingRequestBody.Listener {
        override fun onRequestProgress(bytesWritten: Long, contentLength: Long) {

            var i = contentLength / 10
            CoroutineScope(Main).launch {
                if (bytesWritten >= 0)
                    asyncDialog?.setMessage("Server Connected...")
                if (bytesWritten >= (i * 2))
                    asyncDialog?.setMessage("Uploading a photo taken with user data and selfies...")
                if (bytesWritten >= contentLength)
                    asyncDialog?.setMessage("Downloading Skin Analysis and Results...")
            }
            Log.e("Progress", "progress bytesWritten=$bytesWritten, contentLength=$contentLength")
        }
    }
}