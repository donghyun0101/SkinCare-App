package com.iconai.skincare.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.room.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.iconai.skincare.R
import com.iconai.skincare.util.ApiData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Dispatchers.IO
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ResultActivity : AppCompatActivity(), View.OnTouchListener {

    //Main Isolation UI Value
    private var imgResultFace: ImageView? = null //Face 이미지
    private var btnResultStartOver: Button? = null //시작으로 돌아가기 버튼
    private var btnResultDialogOpen: Button? = null //Skin Analysis Result 버튼
    private var btnResultDialogClose: ImageButton? = null //다이얼로그 Close 버튼
    private var btnResultRecommend: Button? = null //Recommend 버튼
    private var tvDialogDailyDate: TextView? = null //다이얼로그 내부 오늘 날짜 텍스트
    private var chartToday: RadarChart? = null
    private var chartOther: LineChart? = null
    private var layoutResultDialog: RelativeLayout? = null
    private var layBackDialog: RelativeLayout? = null
    private var layoutResultDayDialogView: RelativeLayout? = null
    private var layoutResultDailyDialogView: RelativeLayout? = null

    //Main ArrayList UI Value
    private var layoutButtonResultAnalysis: ArrayList<RelativeLayout> = arrayListOf() //Analysis Value 선택 버튼
    private var tvResultAnalysis: ArrayList<TextView> = arrayListOf() //Analysis Value 텍스트
    private var btnResultDialogDaily: ArrayList<Button> = arrayListOf() //다이얼로그 Daily 선택 버튼
    private var btnResultDialogOtherAnalysis: ArrayList<Button> = arrayListOf() //1W 이상부터 Analysis Value 선택 버튼

    //Face Draw Set Value
    private var canvas: Canvas? = null
    private var paint: Paint? = null
    private var path: ArrayList<Path>? = arrayListOf()
    private var drawBitmapValue: Bitmap? = null

    //Use Dialog Value
    private var defaultUserDataValue: ArrayList<RadarEntry> = ArrayList()
    private var defaultUserDailyDataValue: ArrayList<Entry> = ArrayList()
    private var nowTime: Long? = null
    private var nowDate: Date? = null
    private var simpleDateFormat: SimpleDateFormat? = null
    private var getDateString: String? = null

    //Dialog Animation
    private var viewOpen: Animation? = null
    private var viewClose: Animation? = null

    //Skin Analysis Json Data List String Value
    private var jsonDescriptionObject = ArrayList<String>()
    private var jsonMeasurementLocationsObject = ArrayList<String>()
    private var jsonMessageObject = ArrayList<String>()
    private var jsonSumMeasuresObject = ArrayList<String>()
    private var jsonValueObject = ArrayList<String>()

    //Database Value
    private var skinResultDB: AppDatabase? = null
    private var input: SkinResultTable? = null

    //Skin Analysis Percent Value
    private var darkCirclesPercent: Int = 0
    private var eyeBagsPercent: Int = 0
    private var wrinklesPercent: Int = 0
    private var pigmentationPercent: Int = 0
    private var rednessPercent: Int = 0
    private var texturePercent: Int = 0
    private var skinShinePercent: Int = 0
    private var unevenSkinTonePercent: Int = 0
    private var skinSaggingPercent: Int = 0

    //Free JsonArray and JsonObject Value
    private var jsonEmtArray: JSONArray? = null
    private var jsonEmtObject: JSONObject? = null
    private var jsonEmt1Array: JSONArray? = null
    private var jsonEmt1Object: JSONObject? = null
    private var jsonEmt2Array: JSONArray? = null

    //Free Value
    private var btnDailyCount: Int = 0
    private var btnOtherCount: Int = 0
    private var emtParamsValue: ArrayList<String> = ArrayList()
    private var emtDateList: ArrayList<String> = ArrayList()
    private var reverseDateList: ArrayList<String> = ArrayList()
    private var reverseValuePercentList: ArrayList<String> = ArrayList()

    //Eyes Info Value
    private var leftEyeEyeBagsValue: Double = 0.0
    private var leftEyeDarkCircleValue: Double = 0.0
    private var leftEyeShapes: ArrayList<ArrayList<String>> = ArrayList()
    private var rightEyeEyeBagsValue: Double = 0.0
    private var rightEyeDarkCircleValue: Double = 0.0
    private var rightEyeShapes: ArrayList<ArrayList<String>> = ArrayList()

    //Wrinkles Info Value
    private var wrinklesShapes: ArrayList<ArrayList<String>> = ArrayList()
    private var pigmentationShapes: ArrayList<ArrayList<String>> = ArrayList()
    private var rednessShapes: ArrayList<ArrayList<String>> = ArrayList()
    private var textureShapes: ArrayList<ArrayList<String>> = ArrayList()
    private var skinShineShapes: ArrayList<ArrayList<String>> = ArrayList()
    private var unevenSkinToneShapes: ArrayList<ArrayList<String>> = ArrayList()
    private var skinSaggingShapes: ArrayList<ArrayList<String>> = ArrayList()

//    Test Value
//    private var testGetImgBitmap: Bitmap? = null
//    private var testBitmap: Bitmap? = null
//    private var ist: InputStream? = null
//    private var istReader: InputStreamReader? = null
//    private var bufReader: BufferedReader? = null
//    private var bufString: StringBuffer? = null
//    private var bufLine: String? = null
//    private var testJsonData: String? = null
//    private var testJsonObject: JSONObject? = null

    @Entity
    data class SkinResultTable(
            @PrimaryKey val code: Int,
            val dateTime: String,
            val darkCirclesPercentDB: Int,
            val eyeBagsPercentDB: Int,
            val wrinklesPercentDB: Int,
            val pigmentationPercentDB: Int,
            val rednessPercentDB: Int,
            val texturePercentDB: Int,
            val skinShinePercentDB: Int,
            val unevenSkinTonePercentDB: Int,
            val skinSaggingPercentDB: Int
    )

    @Dao
    interface SkinResultInterface {
        @Query("SELECT * FROM SkinResultTable")
        suspend fun getAll(): List<SkinResultTable>

        @Insert
        suspend fun insert(skinResultTable: SkinResultTable)

        @Query("DELETE FROM SkinResultTable")
        suspend fun deleteAll()
    }

    @Database(entities = [SkinResultTable::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun skinResultInterface(): SkinResultInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        init()
        //testInit()
    }

    @SuppressLint("LongLogTag", "SetTextI18n", "ClickableViewAccessibility", "SimpleDateFormat")
    fun init() {
        imgResultFace = findViewById(R.id.img_result_result)
        btnResultStartOver = findViewById(R.id.btn_result_start_over)
        btnResultRecommend = findViewById(R.id.btn_result_recommend)

        layoutButtonResultAnalysis = arrayListOf(
            findViewById(R.id.layout_button_result_darkcircles),
            findViewById(R.id.layout_button_result_eyebags),
            findViewById(R.id.layout_button_result_wrinkles),
            findViewById(R.id.layout_button_result_pigmentation),
            findViewById(R.id.layout_button_result_redness),
            findViewById(R.id.layout_button_result_texture),
            findViewById(R.id.layout_button_result_skinshine),
            findViewById(R.id.layout_button_result_unevenskintone),
            findViewById(R.id.layout_button_result_skinsagging)
        )
        tvResultAnalysis = arrayListOf(
            findViewById(R.id.tv_result_darkcircles),
            findViewById(R.id.tv_result_eyebags),
            findViewById(R.id.tv_result_wrinkles),
            findViewById(R.id.tv_result_pigmentation),
            findViewById(R.id.tv_result_redness),
            findViewById(R.id.tv_result_texture),
            findViewById(R.id.tv_result_skinshine),
            findViewById(R.id.tv_result_unevenskintone),
            findViewById(R.id.tv_result_skinsagging)
        )

        btnResultDialogDaily = arrayListOf(
            findViewById(R.id.btn_dialog_daily_1d),
            findViewById(R.id.btn_dialog_daily_1w),
            findViewById(R.id.btn_dialog_daily_1m),
            findViewById(R.id.btn_dialog_daily_2m),
            findViewById(R.id.btn_dialog_daily_3m)
        )

        btnResultDialogOtherAnalysis = arrayListOf(
            findViewById(R.id.btn_dialog_daily_other_darkcircles),
            findViewById(R.id.btn_dialog_daily_other_eyebags),
            findViewById(R.id.btn_dialog_daily_other_wrinkles),
            findViewById(R.id.btn_dialog_daily_other_pigmentation),
            findViewById(R.id.btn_dialog_daily_other_redness),
            findViewById(R.id.btn_dialog_daily_other_texture),
            findViewById(R.id.btn_dialog_daily_other_skinshine),
            findViewById(R.id.btn_dialog_daily_other_unevenskintone),
            findViewById(R.id.btn_dialog_daily_other_skinsagging)
        )

        btnResultDialogOpen = findViewById(R.id.btn_result_daily)
        btnResultDialogClose = findViewById(R.id.btn_result_dialog_close)
        layoutResultDialog = findViewById(R.id.layout_result_dialog)
        layBackDialog = findViewById(R.id.lay_back_dialog)
        layoutResultDayDialogView = findViewById(R.id.layout_result_day_dialog_view)
        layoutResultDailyDialogView = findViewById(R.id.layout_result_daily_dialog_view)
        tvDialogDailyDate = findViewById(R.id.tv_dialog_daily_date)
        chartToday = findViewById(R.id.chart_today)
        chartOther = findViewById(R.id.chart_other)
        viewOpen = AnimationUtils.loadAnimation(this, R.anim.view_open)
        viewClose = AnimationUtils.loadAnimation(this, R.anim.view_close)

        nowTime = System.currentTimeMillis()
        nowDate = Date(nowTime!!)
        simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mma")
        getDateString = simpleDateFormat?.format(nowDate!!)
        skinResultDB = Room.databaseBuilder(this@ResultActivity, AppDatabase::class.java, "db").build()

        drawBitmapValue = ApiData.imgFaceBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        canvas = Canvas(drawBitmapValue!!)
        paint = Paint()

        imgResultFace?.setImageBitmap(drawBitmapValue)

        CoroutineScope(Main).launch {
            jsonEmtArray = ApiData.faceData?.getJSONArray("results")

            for (i in 0 until jsonEmtArray!!.length()) {
                jsonEmtObject = jsonEmtArray?.getJSONObject(i)

                jsonDescriptionObject.add(jsonEmtObject!!.getString("description"))
                jsonMeasurementLocationsObject.add(jsonEmtObject!!.getString("measurement_locations"))
                jsonMessageObject.add(jsonEmtObject!!.getString("message"))
                jsonSumMeasuresObject.add(jsonEmtObject!!.getString("sum_measures"))
                jsonValueObject.add(jsonEmtObject!!.getString("value"))
            }

            for (i in 0 until jsonDescriptionObject.size) {
                when (jsonDescriptionObject[i]) {
                    "eyes" -> {
                        jsonEmtObject = JSONObject("{\"value\":" + jsonMeasurementLocationsObject[i] + "}")
                        jsonEmtArray = jsonEmtObject?.getJSONArray("value")
                        for (j in 0 until jsonEmtArray!!.length()) {
                            jsonEmtObject = jsonEmtArray?.getJSONObject(j)
                            when (jsonEmtObject?.getString("description")) {
                                "left eye" -> {
                                    jsonEmt1Object = JSONObject(jsonEmtObject!!.getString("mask_shapes"))
                                    jsonEmt1Array = jsonEmt1Object?.getJSONArray("shapes")

                                    for (k in 0 until jsonEmt1Array!!.length()) {
                                        jsonEmt1Object = jsonEmt1Array?.getJSONObject(k)
                                        emtParamsValue.add(jsonEmt1Object!!.getString("color"))
                                        emtParamsValue.add(jsonEmt1Object!!.getString("type"))

                                        jsonEmt1Object = JSONObject("{\"params\":" + jsonEmt1Object?.getString("params") + "}")
                                        jsonEmt2Array = jsonEmt1Object?.getJSONArray("params")

                                        for (c in 0 until jsonEmt2Array!!.length())
                                            emtParamsValue.add(jsonEmt2Array!!.getString(c))
                                        leftEyeShapes.add(ArrayList(emtParamsValue))
                                        emtParamsValue.clear()
                                    }

                                    jsonEmt1Array = jsonEmtObject?.getJSONArray("measures")
                                    for (k in 0 until jsonEmt1Array!!.length()) {
                                        jsonEmt1Object = jsonEmt1Array?.getJSONObject(k)
                                        when (jsonEmt1Object!!.getString("description")) {
                                            "eyebags" -> leftEyeEyeBagsValue = jsonEmt1Object!!.getString("value").toDouble() * 100
                                            "dark circles" -> leftEyeDarkCircleValue = jsonEmt1Object!!.getString("value").toDouble() * 100
                                        }
                                    }
                                }
                                "right eye" -> {
                                    jsonEmt1Object = JSONObject(jsonEmtObject!!.getString("mask_shapes"))
                                    jsonEmt1Array = jsonEmt1Object?.getJSONArray("shapes")

                                    for (k in 0 until jsonEmt1Array!!.length()) {
                                        jsonEmt1Object = jsonEmt1Array?.getJSONObject(k)
                                        emtParamsValue.add(jsonEmt1Object!!.getString("color"))
                                        emtParamsValue.add(jsonEmt1Object!!.getString("type"))

                                        jsonEmt1Object = JSONObject("{\"params\":" + jsonEmt1Object?.getString("params") + "}")
                                        jsonEmt2Array = jsonEmt1Object?.getJSONArray("params")

                                        for (c in 0 until jsonEmt2Array!!.length())
                                            emtParamsValue.add(jsonEmt2Array!!.getString(c))
                                        rightEyeShapes.add(ArrayList(emtParamsValue))
                                        emtParamsValue.clear()
                                    }

                                    jsonEmt1Array = jsonEmtObject?.getJSONArray("measures")
                                    for (k in 0 until jsonEmt1Array!!.length()) {
                                        jsonEmt1Object = jsonEmt1Array?.getJSONObject(k)
                                        when (jsonEmt1Object!!.getString("description")) {
                                            "eyebags" -> rightEyeEyeBagsValue = jsonEmt1Object!!.getString("value").toDouble() * 100
                                            "dark circles" -> rightEyeDarkCircleValue = jsonEmt1Object!!.getString("value").toDouble() * 100
                                        }
                                    }
                                }
                            }
                        }
                        when (jsonMessageObject[i]) {
                            "Ok" -> {
                                darkCirclesPercent = ((leftEyeDarkCircleValue + rightEyeDarkCircleValue) / 2).roundToInt()
                                eyeBagsPercent = ((leftEyeEyeBagsValue + rightEyeEyeBagsValue) / 2).roundToInt()
                            }
                            else -> {
                                darkCirclesPercent = 111
                                eyeBagsPercent = 111
                            }
                        }
                    }
                    "wrinkles" -> wrinklesPercent = getJsonMeasureData(i, 1, wrinklesShapes)
                    "hyperpigmentation" -> pigmentationPercent = getJsonMeasureData(i, 1, pigmentationShapes)
                    "redness" -> rednessPercent = getJsonMeasureData(i, 3, rednessShapes)
                    "texture" -> texturePercent = getJsonMeasureData(i, 1, textureShapes)
                    "Skin shine" -> skinShinePercent = getJsonMeasureData(i, 3, skinShineShapes)
                    "uneven_skin_tone" -> unevenSkinTonePercent = getJsonMeasureData(i, 3, unevenSkinToneShapes)
                    "skin_sagging" -> skinSaggingPercent = getJsonMeasureData(i, 1, skinSaggingShapes)
                }
            }

            input = SkinResultTable(
                skinResultDB!!.skinResultInterface().getAll().size,
                getDateString.toString(),
                darkCirclesPercent,
                eyeBagsPercent,
                wrinklesPercent,
                pigmentationPercent,
                rednessPercent,
                texturePercent,
                skinShinePercent,
                unevenSkinTonePercent,
                skinSaggingPercent
            )
            skinResultDB!!.skinResultInterface().insert(input!!)

            withContext(Main) {
                tvResultAnalysis[0].text = "$darkCirclesPercent%"
                tvResultAnalysis[1].text = "$eyeBagsPercent%"
                tvResultAnalysis[2].text = "$wrinklesPercent%"
                tvResultAnalysis[3].text = "$pigmentationPercent%"
                tvResultAnalysis[4].text = "$rednessPercent%"
                tvResultAnalysis[5].text = "$texturePercent%"
                tvResultAnalysis[6].text = "$skinShinePercent%"
                tvResultAnalysis[7].text = "$unevenSkinTonePercent%"
                tvResultAnalysis[8].text = "$skinSaggingPercent%"

                for (i in 0 until tvResultAnalysis.size)
                    defaultUserDataValue.add(RadarEntry(tvResultAnalysis[i].text.toString().replace("%", "").toFloat()))
            }
        }

        for (i in 0 until layoutButtonResultAnalysis.size)
            layoutButtonResultAnalysis[i].setOnClickListener {
                for (j in 0 until layoutButtonResultAnalysis.size)
                    layoutButtonResultAnalysis[j].isClickable = false

                if (layoutButtonResultAnalysis[i].isSelected) {
                    layoutButtonResultAnalysis[i].isSelected = false
                    if (path?.isEmpty() == false) {
                        path?.clear()
                        paint?.reset()
                        drawBitmapValue = ApiData.imgFaceBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                        canvas = Canvas(drawBitmapValue!!)
                        imgResultFace?.setImageBitmap(drawBitmapValue)
                    }
                } else {
                    for (j in 0 until layoutButtonResultAnalysis.size)
                        layoutButtonResultAnalysis[j].isSelected = i == j

                    if (path?.isEmpty() == false) {
                        path?.clear()
                        paint?.reset()
                        drawBitmapValue = ApiData.imgFaceBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                        canvas = Canvas(drawBitmapValue!!)
                        imgResultFace?.setImageBitmap(drawBitmapValue)
                    }

                    when (i) {
                        0, 1 -> { //Main DarkCircle,  //Main Eyebags
                            path?.add(Path())

                            paint?.setARGB(150, 50, 50, 50)
                            paint?.style = Paint.Style.FILL_AND_STROKE
                            paint?.isAntiAlias = true
                            paint?.strokeWidth = 1F

                            path!![0].addArc(
                                leftEyeShapes[1][2].toFloat() - leftEyeShapes[1][4].toFloat(),
                                leftEyeShapes[1][3].toFloat() - leftEyeShapes[1][5].toFloat(),
                                leftEyeShapes[1][2].toFloat() + leftEyeShapes[1][4].toFloat(),
                                leftEyeShapes[1][3].toFloat() + leftEyeShapes[1][5].toFloat(),
                                leftEyeShapes[1][7].toFloat() + leftEyeShapes[1][6].toFloat(),
                                if (((leftEyeShapes[1][8].toFloat() - leftEyeShapes[1][7].toFloat()) + leftEyeShapes[1][6].toFloat()) > 360F)
                                    (leftEyeShapes[1][8].toFloat() - leftEyeShapes[1][7].toFloat()) - leftEyeShapes[1][6].toFloat()
                                else (leftEyeShapes[1][8].toFloat() - leftEyeShapes[1][7].toFloat()) + leftEyeShapes[1][6].toFloat()
                            )
                            path!![0].addArc(
                                rightEyeShapes[1][2].toFloat() - rightEyeShapes[1][4].toFloat(),
                                rightEyeShapes[1][3].toFloat() - rightEyeShapes[1][5].toFloat(),
                                rightEyeShapes[1][2].toFloat() + rightEyeShapes[1][4].toFloat(),
                                rightEyeShapes[1][3].toFloat() + rightEyeShapes[1][5].toFloat(),
                                rightEyeShapes[1][7].toFloat() + rightEyeShapes[1][6].toFloat(),
                                if (((rightEyeShapes[1][8].toFloat() - rightEyeShapes[1][7].toFloat()) + rightEyeShapes[1][6].toFloat()) > 360F)
                                    (rightEyeShapes[1][8].toFloat() - rightEyeShapes[1][7].toFloat()) - rightEyeShapes[1][6].toFloat()
                                else (rightEyeShapes[1][8].toFloat() - rightEyeShapes[1][7].toFloat()) + rightEyeShapes[1][6].toFloat()
                            )
                            canvas?.clipPath(path!![0], Region.Op.REPLACE)

                            path!![0].addArc(
                                leftEyeShapes[0][2].toFloat() - leftEyeShapes[0][4].toFloat(),
                                leftEyeShapes[0][3].toFloat() - leftEyeShapes[0][5].toFloat(),
                                leftEyeShapes[0][2].toFloat() + leftEyeShapes[0][4].toFloat(),
                                leftEyeShapes[0][3].toFloat() + leftEyeShapes[0][5].toFloat(),
                                leftEyeShapes[0][7].toFloat() + leftEyeShapes[0][6].toFloat(),
                                if (((leftEyeShapes[0][8].toFloat() - leftEyeShapes[0][7].toFloat()) + leftEyeShapes[0][6].toFloat()) > 360F)
                                    (leftEyeShapes[0][8].toFloat() - leftEyeShapes[0][7].toFloat()) - leftEyeShapes[0][6].toFloat()
                                else (leftEyeShapes[0][8].toFloat() - leftEyeShapes[0][7].toFloat()) + leftEyeShapes[0][6].toFloat()
                            )
                            path!![0].addArc(
                                rightEyeShapes[0][2].toFloat() - rightEyeShapes[0][4].toFloat(),
                                rightEyeShapes[0][3].toFloat() - rightEyeShapes[0][5].toFloat(),
                                rightEyeShapes[0][2].toFloat() + rightEyeShapes[0][4].toFloat(),
                                rightEyeShapes[0][3].toFloat() + rightEyeShapes[0][5].toFloat(),
                                rightEyeShapes[0][7].toFloat() + rightEyeShapes[0][6].toFloat(),
                                if (((rightEyeShapes[0][8].toFloat() - rightEyeShapes[0][7].toFloat()) + rightEyeShapes[0][6].toFloat()) > 360F)
                                    (rightEyeShapes[0][8].toFloat() - rightEyeShapes[0][7].toFloat()) - rightEyeShapes[0][6].toFloat()
                                else (rightEyeShapes[0][8].toFloat() - rightEyeShapes[0][7].toFloat()) + rightEyeShapes[0][6].toFloat()
                            )
                            canvas?.clipPath(path!![0], Region.Op.XOR)

                            canvas?.clipRect(0F, 0F, drawBitmapValue!!.width.toFloat(), drawBitmapValue!!.height.toFloat(), Region.Op.XOR)
                            canvas?.drawRect(0F, 0f, drawBitmapValue!!.width.toFloat(), drawBitmapValue!!.height.toFloat(), paint!!)
                        }
                        2 -> { //Main Wrinkles
                            path?.add(Path())

                            for (j in 0 until wrinklesShapes.size) {
                                for (k in 1..wrinklesShapes[j].size / 2) {
                                    if (k == 1) path!![0].moveTo(wrinklesShapes[j][k].toFloat(), wrinklesShapes[j][k + (wrinklesShapes[j].size / 2)].toFloat())
                                    path!![0].lineTo(wrinklesShapes[j][k].toFloat(), wrinklesShapes[j][k + (wrinklesShapes[j].size / 2)].toFloat())
                                }

                                paint?.setARGB(100, 255, 255, 255)
                                paint?.style = Paint.Style.FILL_AND_STROKE
                                paint?.isAntiAlias = true
                                paint?.strokeWidth = 1F

                                canvas?.drawPath(path!![0], paint!!)
                            }
                        }
                        3 -> { //Main Pigmentation
                            path?.add(Path())

                            for (j in 0 until pigmentationShapes.size) {
                                for (k in 1..pigmentationShapes[j].size / 2)
                                    path!![0].addCircle(pigmentationShapes[j][k].toFloat() - 5F, pigmentationShapes[j][k + (pigmentationShapes[j].size / 2)].toFloat() - 5F, 5F, Path.Direction.CCW)

                                paint?.setARGB(100, 255, 255, 255)
                                paint?.style = Paint.Style.FILL_AND_STROKE
                                paint?.isAntiAlias = true
                                paint?.strokeWidth = 1F

                                canvas?.drawPath(path!![0], paint!!)
                            }
                        }
                        4 -> { //Main Redness
                            path?.add(Path())
                            path?.add(Path())
                            path?.add(Path())

                            for (j in 0 until rednessShapes.size) {
                                for (k in 1..rednessShapes[j].size / 2) {
                                    when (rednessShapes[j][0].toInt()) {
                                        1 -> {
                                            if (k == 1) path!![0].moveTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
                                            path!![0].lineTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
                                        }
                                        2 -> {
                                            if (k == 1) path!![1].moveTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
                                            path!![1].lineTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
                                        }
                                        3 -> {
                                            if (k == 1) path!![2].moveTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
                                            path!![2].lineTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
                                        }
                                    }
                                }

                                when (rednessShapes[j][0].toInt()) {
                                    1 -> {
                                        paint?.setARGB(100, 255, 255, 255)
                                        paint?.style = Paint.Style.FILL_AND_STROKE
                                        paint?.isAntiAlias = true
                                        paint?.strokeWidth = 1F

                                        canvas?.drawPath(path!![0], paint!!)
                                    }
                                    2 -> {
                                        paint?.setARGB(100, 244, 154, 97)
                                        paint?.style = Paint.Style.FILL_AND_STROKE
                                        paint?.isAntiAlias = true
                                        paint?.strokeWidth = 1F

                                        canvas?.drawPath(path!![1], paint!!)
                                    }
                                    3 -> {
                                        paint?.setARGB(100, 250, 160, 100)
                                        paint?.style = Paint.Style.FILL_AND_STROKE
                                        paint?.isAntiAlias = true
                                        paint?.strokeWidth = 1F

                                        canvas?.drawPath(path!![2], paint!!)
                                    }
                                }
                            }
                        }
                        5 -> { //Main Texture
                            path?.add(Path())

                            for (j in 0 until textureShapes.size) {
                                for (k in 1..textureShapes[j].size / 2) {
                                    if (k == 1) path!![0].moveTo(textureShapes[j][k].toFloat(), textureShapes[j][k + (textureShapes[j].size / 2)].toFloat())
                                    path!![0].lineTo(textureShapes[j][k].toFloat(), textureShapes[j][k + (textureShapes[j].size / 2)].toFloat())
                                }

                                paint?.setARGB(100, 255, 255, 255)
                                paint?.style = Paint.Style.FILL_AND_STROKE
                                paint?.isAntiAlias = true
                                paint?.strokeWidth = 1F

                                canvas?.drawPath(path!![0], paint!!)
                            }
                        }
                        6 -> { //Main SkinShine
                            path?.add(Path())
                            path?.add(Path())
                            path?.add(Path())

                            for (j in 0 until skinShineShapes.size) {
                                for (k in 1..skinShineShapes[j].size / 2) {
                                    when (skinShineShapes[j][0].toInt()) {
                                        1 -> {
                                            if (k == 1) path!![0].moveTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
                                            path!![0].lineTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
                                        }
                                        2 -> {
                                            if (k == 1) path!![1].moveTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
                                            path!![1].lineTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
                                        }
                                        3 -> {
                                            if (k == 1) path!![2].moveTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
                                            path!![2].lineTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
                                        }
                                    }
                                }

                                when (skinShineShapes[j][0].toInt()) {
                                    1 -> {
                                        paint?.setARGB(100, 255, 255, 255)
                                        paint?.style = Paint.Style.FILL_AND_STROKE
                                        paint?.isAntiAlias = true
                                        paint?.strokeWidth = 1F

                                        canvas?.drawPath(path!![0], paint!!)
                                    }
                                    2 -> {
                                        paint?.setARGB(100, 244, 154, 97)
                                        paint?.style = Paint.Style.FILL_AND_STROKE
                                        paint?.isAntiAlias = true
                                        paint?.strokeWidth = 1F

                                        canvas?.drawPath(path!![1], paint!!)
                                    }
                                    3 -> {
                                        paint?.setARGB(100, 250, 160, 100)
                                        paint?.style = Paint.Style.FILL_AND_STROKE
                                        paint?.isAntiAlias = true
                                        paint?.strokeWidth = 1F

                                        canvas?.drawPath(path!![2], paint!!)
                                    }
                                }
                            }
                        }
                        7 -> { //Main Uneven Skin Tone
                            path?.add(Path())
                            path?.add(Path())
                            path?.add(Path())

                            for (j in 0 until unevenSkinToneShapes.size) {
                                for (k in 1..unevenSkinToneShapes[j].size / 2) {
                                    when (unevenSkinToneShapes[j][0].toInt()) {
                                        1 -> {
                                            if (k == 1) path!![0].moveTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
                                            path!![0].lineTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
                                        }
                                        2 -> {
                                            if (k == 1) path!![1].moveTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
                                            path!![1].lineTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
                                        }
                                        3 -> {
                                            if (k == 1) path!![2].moveTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
                                            path!![2].lineTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
                                        }
                                    }
                                }

                                when (unevenSkinToneShapes[j][0].toInt()) {
                                    1 -> {
                                        paint?.setARGB(100, 255, 255, 255)
                                        paint?.style = Paint.Style.FILL_AND_STROKE
                                        paint?.isAntiAlias = true
                                        paint?.strokeWidth = 1F

                                        canvas?.drawPath(path!![0], paint!!)
                                    }
                                    2 -> {
                                        paint?.setARGB(100, 244, 154, 97)
                                        paint?.style = Paint.Style.FILL_AND_STROKE
                                        paint?.isAntiAlias = true
                                        paint?.strokeWidth = 1F

                                        canvas?.drawPath(path!![1], paint!!)
                                    }
                                    3 -> {
                                        paint?.setARGB(100, 250, 160, 100)
                                        paint?.style = Paint.Style.FILL_AND_STROKE
                                        paint?.isAntiAlias = true
                                        paint?.strokeWidth = 1F

                                        canvas?.drawPath(path!![2], paint!!)
                                    }
                                }
                            }
                        }
                        8 -> { //Main Skin Sagging
                            path?.add(Path())

                            for (j in 0 until skinSaggingShapes.size) {
                                for (k in 1..skinSaggingShapes[j].size / 2) {
                                    if (k == 1) path!![0].moveTo(skinSaggingShapes[j][k].toFloat(), skinSaggingShapes[j][k + (skinSaggingShapes[j].size / 2)].toFloat())
                                    path!![0].lineTo(skinSaggingShapes[j][k].toFloat(), skinSaggingShapes[j][k + (skinSaggingShapes[j].size / 2)].toFloat())
                                }

                                paint?.setARGB(100, 255, 255, 255)
                                paint?.style = Paint.Style.FILL_AND_STROKE
                                paint?.isAntiAlias = true
                                paint?.strokeWidth = 1F

                                canvas?.drawPath(path!![0], paint!!)
                            }
                        }
                    }

                    imgResultFace?.setImageBitmap(drawBitmapValue)
                }

                for (j in 0 until layoutButtonResultAnalysis.size)
                    layoutButtonResultAnalysis[j].isClickable = true
            }

        layBackDialog?.setOnTouchListener(this)
        btnResultStartOver?.setOnClickListener {
            it.isSelected = true

            ApiData.skinType = null
            ApiData.skinConcerns.clear()
            ApiData.gender = null
            ApiData.age = null
            ApiData.imgFaceBitmap = null
            ApiData.imgFaceFile = null
            ApiData.faceData = null

            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            finish()
        }
        btnResultRecommend?.setOnClickListener {
            Toast.makeText(this@ResultActivity, "Non Data", Toast.LENGTH_SHORT).show()
        }
        btnResultDialogOpen?.setOnClickListener {
            btnResultDialogDaily[0].performClick()
            btnResultDialogOtherAnalysis[0].performClick()

            layoutResultDialog?.visibility = View.VISIBLE
            layoutResultDialog?.startAnimation(viewOpen)
        }
        btnResultDialogClose?.setOnClickListener {
            btnResultDialogDaily[0].isPressed = false
            btnResultDialogOtherAnalysis[0].isPressed = false
            btnResultDialogDaily[0].isSelected = false
            btnResultDialogOtherAnalysis[0].isSelected = false

            layoutResultDialog?.visibility = View.INVISIBLE
            layoutResultDialog?.startAnimation(viewClose)
        }
        tvDialogDailyDate?.text = getDateString

        for (i in 0 until btnResultDialogDaily.size)
            btnResultDialogDaily[i].setOnClickListener {
                btnDailyCount = i
                if (i == 0) {
                    layoutResultDayDialogView?.visibility = View.VISIBLE
                    layoutResultDailyDialogView?.visibility = View.GONE
                    todayChartSet(true)
                    dailyChartSet(false, 0, 0)
                } else {
                    layoutResultDayDialogView?.visibility = View.GONE
                    layoutResultDailyDialogView?.visibility = View.VISIBLE
                    todayChartSet(false)
                    dailyChartSet(true, btnDailyCount, btnOtherCount)
                }

                for (j in 0 until btnResultDialogDaily.size)
                    btnResultDialogDaily[j].isSelected = j == i
            }

        for (i in 0 until btnResultDialogOtherAnalysis.size)
            btnResultDialogOtherAnalysis[i].setOnClickListener {
                btnOtherCount = i
                dailyChartSet(true, btnDailyCount, btnOtherCount)
                for (j in 0 until btnResultDialogOtherAnalysis.size)
                    btnResultDialogOtherAnalysis[j].isSelected = j == i
            }
    }

    private fun getJsonMeasureData(cnt: Int, intensityCnt: Int, valueList: ArrayList<ArrayList<String>>): Int {
        for (d in 1..intensityCnt) {
            jsonEmtObject = JSONObject("{\"value\":" + jsonMeasurementLocationsObject[cnt] + "}")
            jsonEmtArray = jsonEmtObject?.getJSONArray("value")
            for (j in 0 until jsonEmtArray!!.length()) {
                jsonEmtObject = jsonEmtArray?.getJSONObject(j)
                jsonEmt1Object = JSONObject("{\"shapes\":" + jsonEmtObject!!.getString("visualization_data") + "}")

                if (!jsonEmt1Object!!.isNull("shapes")) {
                    jsonEmt1Array = jsonEmt1Object?.getJSONArray("shapes")

                    for (k in 0 until jsonEmt1Array!!.length()) {
                        jsonEmt1Object = jsonEmt1Array?.getJSONObject(k)
                        if ((jsonEmt1Object!!.getString("intensity") == d.toString()) || intensityCnt == 1) {
                            emtParamsValue.add(jsonEmt1Object!!.getString("intensity"))

                            jsonEmt2Array = jsonEmt1Object?.getJSONArray("x")
                            for (c in 0 until jsonEmt2Array!!.length()) {
                                emtParamsValue.add(jsonEmt2Array!![c].toString())
                            }
                            jsonEmt2Array = jsonEmt1Object?.getJSONArray("y")
                            for (c in 0 until jsonEmt2Array!!.length()) {
                                emtParamsValue.add(jsonEmt2Array!![c].toString())
                            }
                            valueList.add(ArrayList(emtParamsValue))
                            emtParamsValue.clear()
                        }
                    }
                }
            }
        }

        return when (jsonMessageObject[cnt]) {
            "Ok" -> (jsonValueObject[cnt].toDouble() * 100).roundToInt()
            else -> 111
        }
    }

    private fun todayChartSet(set: Boolean) {
        val defaultDataSet = RadarDataSet(defaultDataValue(), null)
        val userDataSet = RadarDataSet(defaultUserDataValue, null)
        val dataRadar = RadarData()

        CoroutineScope(Main).launch {
            for (i in 0 until btnResultDialogDaily.size)
                btnResultDialogDaily[i].isClickable = false

            for (i in 0 until btnResultDialogOtherAnalysis.size)
                btnResultDialogOtherAnalysis[i].isClickable = false

            btnResultDialogClose?.isClickable = false

            when (set) {
                true -> {
                    withContext(IO) {
                        defaultDataSet.apply {
                            color = ContextCompat.getColor(this@ResultActivity, R.color.lt_blue);
                            fillColor = ContextCompat.getColor(this@ResultActivity, R.color.lt_blue);
                            setDrawFilled(true)
                            fillAlpha = 180
                            lineWidth = 0f
                            label = null
                            isDrawHighlightCircleEnabled = true
                            setDrawHighlightIndicators(false)
                        }

                        userDataSet.apply {
                            color = ContextCompat.getColor(this@ResultActivity, R.color.lt_pink);
                            fillColor = ContextCompat.getColor(this@ResultActivity, R.color.lt_pink);
                            setDrawFilled(true)
                            fillAlpha = 180
                            lineWidth = 0f
                            label = null
                            isDrawHighlightCircleEnabled = true
                            setDrawHighlightIndicators(false)
                        }

                        dataRadar.apply {
                            addDataSet(defaultDataSet)
                            addDataSet(userDataSet)
                            setDrawValues(false)
                        }

                        chartToday!!.run {
                            data = dataRadar //데이터 세팅
                            invalidate()

                            description.isEnabled = false //Description 안보이게 하기
                            webLineWidth = 1F //Web 두께
                            webColor = ContextCompat.getColor(this@ResultActivity, R.color.lt_gray); //Web Color
                            webAlpha = 500 //Web Alpha (Inner 와 공통으로 적용됨)
                            webLineWidthInner = 1F //Web Inner 두께
                            webColorInner = ContextCompat.getColor(this@ResultActivity, R.color.lt_gray); //Web Inner Color
                            isRotationEnabled = false //회전 여부

                            xAxis.run {
                                val arrayList = arrayOf(
                                    defaultUserDataValue[0].y.toInt().toString(),
                                    defaultUserDataValue[1].y.toInt().toString(),
                                    defaultUserDataValue[2].y.toInt().toString(),
                                    defaultUserDataValue[3].y.toInt().toString(),
                                    defaultUserDataValue[4].y.toInt().toString(),
                                    defaultUserDataValue[5].y.toInt().toString(),
                                    defaultUserDataValue[6].y.toInt().toString(),
                                    defaultUserDataValue[7].y.toInt().toString(),
                                    defaultUserDataValue[8].y.toInt().toString()
                                )

                                textSize = 10f //작은 원 안에 라벨 크기
                                textColor = ContextCompat.getColor(this@ResultActivity, R.color.black); //라벨 Color
                                xOffset = 0F
                                yOffset = 0F
                                position = XAxis.XAxisPosition.TOP
                                valueFormatter = IAxisValueFormatter { value, _ -> arrayList[value.toInt()] }
                            }

                            yAxis.run {
                                setLabelCount(2, true)
                                textSize = 0f
                                textColor = 0x00000000
                                axisMinimum = 0f
                                axisMaximum = 100f
                                setDrawLabels(false)
                            }
                            legend.run {
                                isEnabled = false
                            }
                        }
                    }
                }
                false -> {
                    if (chartToday != null)
                        chartToday!!.clear()
                }
            }

            for (i in 0 until btnResultDialogDaily.size)
                btnResultDialogDaily[i].isClickable = true

            for (i in 0 until btnResultDialogOtherAnalysis.size)
                btnResultDialogOtherAnalysis[i].isClickable = true

            btnResultDialogClose?.isClickable = true
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun dailyChartSet(set: Boolean, date: Int, value: Int) {
        var otherDataSet: LineDataSet
        var dataLine = LineData()
        CoroutineScope(Main).launch {
            for (i in 0 until btnResultDialogDaily.size)
                btnResultDialogDaily[i].isClickable = false

            for (i in 0 until btnResultDialogOtherAnalysis.size)
                btnResultDialogOtherAnalysis[i].isClickable = false

            btnResultDialogClose?.isClickable = false

            when (set) {
                true -> {
                    if (chartOther != null)
                        chartOther!!.clear()

                    if (reverseDateList != null)
                        reverseDateList.clear()
                    if (reverseValuePercentList != null)
                        reverseValuePercentList.clear()
                    if (emtDateList != null)
                        emtDateList.clear()
                    if (defaultUserDailyDataValue != null)
                        defaultUserDailyDataValue.clear()

                    withContext(IO) {
                        when (date) {
                            1 -> dailyDataValue(Calendar.DATE, -6, value) //Result Dialog 1W
                            2 -> dailyDataValue(Calendar.MONTH, -1, value) //Result Dialog 1M
                            3 -> dailyDataValue(Calendar.MONTH, -2, value) //Result Dialog 2M
                            4 -> dailyDataValue(Calendar.MONTH, -3, value) //Result Dialog 3M
                        }

                        delay(1)
                        otherDataSet = LineDataSet(defaultUserDailyDataValue, null)
                        otherDataSet.apply {
                            color = ContextCompat.getColor(this@ResultActivity, R.color.lt_blue)
                            fillColor = ContextCompat.getColor(this@ResultActivity, R.color.lt_pink)
                            setCircleColor(ContextCompat.getColor(this@ResultActivity, R.color.lt_blue))
                            circleHoleColor = ContextCompat.getColor(this@ResultActivity, R.color.lt_blue)
                            setDrawFilled(true)

                            when (date) {
                                1 -> circleRadius = 4F //Result Dialog 1W
                                2 -> circleRadius = 2F //Result Dialog 1M
                                3 -> circleRadius = 1F //Result Dialog 2M
                                4 -> circleRadius = 1F //Result Dialog 3M
                            }

                            fillAlpha = 250
                        }
                        dataLine.apply {
                            addDataSet(otherDataSet)
                            setDrawValues(false)
                        }
                        chartOther!!.run {
                            data = dataLine
                            invalidate()

                            setDrawGridBackground(true)
                            setGridBackgroundColor(Color.WHITE)

                            xAxis.run {
                                position = XAxis.XAxisPosition.BOTTOM //x 축 표시에 대한 위치 설정
                                valueFormatter = IAxisValueFormatter { value, _ ->
                                    if (emtDateList.size < 7) {
                                        if (emtDateList.size == 2) {
                                            if (value.toInt() == 1) emtDateList[1]
                                            else ""
                                        } else emtDateList[value.toInt()]
                                    } else emtDateList[value.toInt()]
                                }

                                when {
                                    emtDateList.size == 2 -> setLabelCount(3, true)
                                    emtDateList.size > 7 -> setLabelCount(7, true)
                                    else -> setLabelCount(emtDateList.size - 1, true)
                                }
                                textColor = ContextCompat.getColor(this@ResultActivity, R.color.black) // X축 텍스트컬러설정
                                gridColor = ContextCompat.getColor(this@ResultActivity, R.color.lt_gray) // X축 줄 컬러설정
                            }
                            axisLeft.run { //Y축의 왼쪽면 설정
                                setDrawAxisLine(false)
                                axisMinimum = 0f
                                axisMaximum = 100f
                                valueFormatter = IAxisValueFormatter { value, _ ->
                                    if (value == 0f) "" else value.toInt().toString()
                                }
                                textColor = ContextCompat.getColor(this@ResultActivity, R.color.black)  //Y축 텍스트 컬러 설정
                                gridColor = ContextCompat.getColor(this@ResultActivity, R.color.lt_gray)  // Y축 줄의 컬러 설정
                            }
                            axisRight.run { //Y축의 오른쪽면 설정
                                setDrawLabels(false)
                                setDrawAxisLine(false)
                                setDrawGridLines(false) //y축의 활성화를 제거함
                            }

                            if (emtDateList.size == 2)
                                setVisibleXRangeMinimum(2.toFloat()) //라인차트에서 최대로 보여질 X축의 데이터 설정
                            else
                                setVisibleXRangeMinimum((emtDateList.size - 2).toFloat()) //라인차트에서 최대로 보여질 X축의 데이터 설정

                            description = null

                            legend.run {
                                isEnabled = false
                            }
                        }
                    }
                }
                false -> {
                    if (chartOther != null) {
                        chartOther!!.clear()

                        reverseDateList.clear()
                        reverseValuePercentList.clear()
                        emtDateList.clear()
                        defaultUserDailyDataValue.clear()
                    }
                }
            }

            for (i in 0 until btnResultDialogDaily.size)
                btnResultDialogDaily[i].isClickable = true

            for (i in 0 until btnResultDialogOtherAnalysis.size)
                btnResultDialogOtherAnalysis[i].isClickable = true

            btnResultDialogClose?.isClickable = true
        }
    }

    //TODO DefaultRadar Chart
    private fun defaultDataValue(): ArrayList<RadarEntry> {
        val dataValues: ArrayList<RadarEntry> = ArrayList()
        dataValues.add(RadarEntry(73F))
        dataValues.add(RadarEntry(35F))
        dataValues.add(RadarEntry(60F))
        dataValues.add(RadarEntry(42F))
        dataValues.add(RadarEntry(50F))
        dataValues.add(RadarEntry(90F))
        dataValues.add(RadarEntry(88F))
        dataValues.add(RadarEntry(72F))
        dataValues.add(RadarEntry(18F))
        return dataValues
    }

    private suspend fun dailyDataValue(calendar: Int, amount: Int, analysisValue: Int) {
        var equalsString = "0"

        val toDayDate = simpleDateFormat?.parse(skinResultDB!!.skinResultInterface().getAll()[skinResultDB!!.skinResultInterface().getAll().size - 1].dateTime)
        val calWeekDay = Calendar.getInstance()
        calWeekDay.time = toDayDate!!
        calWeekDay.add(calendar, amount)

        for (i in skinResultDB!!.skinResultInterface().getAll().size - 1 downTo 0) {
            var emtDate: Date? = simpleDateFormat?.parse(skinResultDB!!.skinResultInterface().getAll()[i].dateTime)
            var emtDateFormat = SimpleDateFormat("dd/MM")

            var calThisDay = Calendar.getInstance()
            calThisDay.time = emtDate!!

            if (calWeekDay < calThisDay) {
                if (emtDateFormat.format(emtDate) != equalsString) {

                    reverseDateList.add(emtDateFormat.format(emtDate))
                    when (analysisValue) {
                        0 -> reverseValuePercentList.add(skinResultDB!!.skinResultInterface().getAll()[i].darkCirclesPercentDB.toString())
                        1 -> reverseValuePercentList.add(skinResultDB!!.skinResultInterface().getAll()[i].eyeBagsPercentDB.toString())
                        2 -> reverseValuePercentList.add(skinResultDB!!.skinResultInterface().getAll()[i].wrinklesPercentDB.toString())
                        3 -> reverseValuePercentList.add(skinResultDB!!.skinResultInterface().getAll()[i].pigmentationPercentDB.toString())
                        4 -> reverseValuePercentList.add(skinResultDB!!.skinResultInterface().getAll()[i].rednessPercentDB.toString())
                        5 -> reverseValuePercentList.add(skinResultDB!!.skinResultInterface().getAll()[i].texturePercentDB.toString())
                        6 -> reverseValuePercentList.add(skinResultDB!!.skinResultInterface().getAll()[i].skinShinePercentDB.toString())
                        7 -> reverseValuePercentList.add(skinResultDB!!.skinResultInterface().getAll()[i].unevenSkinTonePercentDB.toString())
                        8 -> reverseValuePercentList.add(skinResultDB!!.skinResultInterface().getAll()[i].skinSaggingPercentDB.toString())
                    }
                    equalsString = emtDateFormat.format(emtDate)
                }
            }
            if (calWeekDay >= calThisDay)
                break
        }
        var j = 1F
        emtDateList.add("0")

        for (i in reverseDateList.size - 1 downTo 0) {

            emtDateList.add(reverseDateList[i])
            defaultUserDailyDataValue.add(Entry(j, reverseValuePercentList[i].toFloat()))
            j += 1F
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v) {
            layBackDialog -> return true
        }
        return true
    }

//    fun testInit() {
//        ist = assets.open("data.json")
//        istReader = InputStreamReader(ist)
//        bufReader = BufferedReader(istReader)
//
//        bufString = StringBuffer()
//        bufLine = bufReader?.readLine()
//        while (bufLine != null) {
//            bufString?.append(bufLine + "\n")
//            bufLine = bufReader?.readLine()
//        }
//
//        testJsonData = bufString.toString()
//        testJsonObject = JSONObject(testJsonData!!)
//
//        layoutButtonResultAnalysis = arrayListOf(
//            findViewById(R.id.layout_button_result_darkcircles),
//            findViewById(R.id.layout_button_result_eyebags),
//            findViewById(R.id.layout_button_result_wrinkles),
//            findViewById(R.id.layout_button_result_pigmentation),
//            findViewById(R.id.layout_button_result_redness),
//            findViewById(R.id.layout_button_result_texture),
//            findViewById(R.id.layout_button_result_skinshine),
//            findViewById(R.id.layout_button_result_unevenskintone),
//            findViewById(R.id.layout_button_result_skinsagging)
//        )
//
//        tvResultAnalysis = arrayListOf(
//            findViewById(R.id.tv_result_darkcircles),
//            findViewById(R.id.tv_result_eyebags),
//            findViewById(R.id.tv_result_wrinkles),
//            findViewById(R.id.tv_result_pigmentation),
//            findViewById(R.id.tv_result_redness),
//            findViewById(R.id.tv_result_texture),
//            findViewById(R.id.tv_result_skinshine),
//            findViewById(R.id.tv_result_unevenskintone),
//            findViewById(R.id.tv_result_skinsagging)
//        )
//
//        imgResultFace = findViewById(R.id.img_result_result)
//
//        testGetImgBitmap = BitmapFactory.decodeResource(resources, R.drawable.skincare)
//        testBitmap = Bitmap.createScaledBitmap(testGetImgBitmap!!, 800, 1280, true)
//
//        drawBitmapValue = ApiData.imgFaceBitmap?.copy(Bitmap.Config.ARGB_8888, true)
//        canvas = Canvas(drawBitmapValue!!)
//        paint = Paint()
//
//        imgResultFace?.setImageBitmap(drawBitmapValue)
//
//        CoroutineScope(Main).launch {
//            jsonEmtArray = testJsonObject?.getJSONArray("results")
//
//            for (i in 0 until jsonEmtArray!!.length()) {
//                jsonEmtObject = jsonEmtArray?.getJSONObject(i)
//
//                jsonDescriptionObject.add(jsonEmtObject!!.getString("description"))
//                jsonMeasurementLocationsObject.add(jsonEmtObject!!.getString("measurement_locations"))
//                jsonMessageObject.add(jsonEmtObject!!.getString("message"))
//                jsonSumMeasuresObject.add(jsonEmtObject!!.getString("sum_measures"))
//                jsonValueObject.add(jsonEmtObject!!.getString("value"))
//            }
//
//            for (i in 0 until jsonDescriptionObject.size) {
//                when (jsonDescriptionObject[i]) {
//                    "eyes" -> {
//                        jsonEmtObject = JSONObject("{\"value\":" + jsonMeasurementLocationsObject[i] + "}")
//                        jsonEmtArray = jsonEmtObject?.getJSONArray("value")
//                        for (j in 0 until jsonEmtArray!!.length()) {
//                            jsonEmtObject = jsonEmtArray?.getJSONObject(j)
//                            when (jsonEmtObject?.getString("description")) {
//                                "left eye" -> {
//                                    jsonEmt1Object = JSONObject(jsonEmtObject!!.getString("mask_shapes"))
//                                    jsonEmt1Array = jsonEmt1Object?.getJSONArray("shapes")
//
//                                    for (k in 0 until jsonEmt1Array!!.length()) {
//                                        jsonEmt1Object = jsonEmt1Array?.getJSONObject(k)
//                                        emtParamsValue.add(jsonEmt1Object!!.getString("color"))
//                                        emtParamsValue.add(jsonEmt1Object!!.getString("type"))
//
//                                        jsonEmt1Object = JSONObject("{\"params\":" + jsonEmt1Object?.getString("params") + "}")
//                                        jsonEmt2Array = jsonEmt1Object?.getJSONArray("params")
//
//                                        for (c in 0 until jsonEmt2Array!!.length())
//                                            emtParamsValue.add(jsonEmt2Array!!.getString(c))
//                                        leftEyeShapes.add(ArrayList(emtParamsValue))
//                                        emtParamsValue.clear()
//                                    }
//
//                                    jsonEmt1Array = jsonEmtObject?.getJSONArray("measures")
//                                    for (k in 0 until jsonEmt1Array!!.length()) {
//                                        jsonEmt1Object = jsonEmt1Array?.getJSONObject(k)
//                                        when (jsonEmt1Object!!.getString("description")) {
//                                            "eyebags" -> leftEyeEyeBagsValue = jsonEmt1Object!!.getString("value").toDouble() * 100
//                                            "dark circles" -> leftEyeDarkCircleValue = jsonEmt1Object!!.getString("value").toDouble() * 100
//                                        }
//                                    }
//
//                                    Log.e("leftEyeEyeBagsValue", leftEyeEyeBagsValue.toString())
//                                    Log.e("leftEyeDarkCircleValue", leftEyeDarkCircleValue.toString())
//                                }
//                                "right eye" -> {
//                                    jsonEmt1Object = JSONObject(jsonEmtObject!!.getString("mask_shapes"))
//                                    jsonEmt1Array = jsonEmt1Object?.getJSONArray("shapes")
//
//                                    for (k in 0 until jsonEmt1Array!!.length()) {
//                                        jsonEmt1Object = jsonEmt1Array?.getJSONObject(k)
//                                        emtParamsValue.add(jsonEmt1Object!!.getString("color"))
//                                        emtParamsValue.add(jsonEmt1Object!!.getString("type"))
//
//                                        jsonEmt1Object = JSONObject("{\"params\":" + jsonEmt1Object?.getString("params") + "}")
//                                        jsonEmt2Array = jsonEmt1Object?.getJSONArray("params")
//
//                                        for (c in 0 until jsonEmt2Array!!.length())
//                                            emtParamsValue.add(jsonEmt2Array!!.getString(c))
//                                        rightEyeShapes.add(ArrayList(emtParamsValue))
//                                        emtParamsValue.clear()
//                                    }
//
//                                    jsonEmt1Array = jsonEmtObject?.getJSONArray("measures")
//                                    for (k in 0 until jsonEmt1Array!!.length()) {
//                                        jsonEmt1Object = jsonEmt1Array?.getJSONObject(k)
//                                        when (jsonEmt1Object!!.getString("description")) {
//                                            "eyebags" -> rightEyeEyeBagsValue = jsonEmt1Object!!.getString("value").toDouble() * 100
//                                            "dark circles" -> rightEyeDarkCircleValue = jsonEmt1Object!!.getString("value").toDouble() * 100
//                                        }
//                                    }
//
//                                    Log.e("rightEyeEyeBagsValue", rightEyeEyeBagsValue.toString())
//                                    Log.e("rightEyeDarkCircleValue", rightEyeDarkCircleValue.toString())
//                                }
//                            }
//                        }
//                        when (jsonMessageObject[i]) {
//                            "Ok" -> {
//                                darkCirclesPercent = ((leftEyeDarkCircleValue + rightEyeDarkCircleValue) / 2).roundToInt()
//                                eyeBagsPercent = ((leftEyeEyeBagsValue + rightEyeEyeBagsValue) / 2).roundToInt()
//                            }
//                            else -> {
//                                darkCirclesPercent = 111
//                                eyeBagsPercent = 111
//                            }
//                        }
//                    }
//                    "wrinkles" -> wrinklesPercent = getJsonMeasureData(i, 1, wrinklesShapes)
//                    "hyperpigmentation" -> pigmentationPercent = getJsonMeasureData(i, 1, pigmentationShapes)
//                    "redness" -> rednessPercent = getJsonMeasureData(i, 3, rednessShapes)
//                    "texture" -> texturePercent = getJsonMeasureData(i, 1, textureShapes)
//                    "Skin shine" -> skinShinePercent = getJsonMeasureData(i, 3, skinShineShapes)
//                    "uneven_skin_tone" -> unevenSkinTonePercent = getJsonMeasureData(i, 3, unevenSkinToneShapes)
//                    "skin_sagging" -> skinSaggingPercent = getJsonMeasureData(i, 1, skinSaggingShapes)
//                }
//            }
//
//            withContext(Main) {
//                tvResultAnalysis[0].text = "$darkCirclesPercent%"
//                tvResultAnalysis[1].text = "$eyeBagsPercent%"
//                tvResultAnalysis[2].text = "$wrinklesPercent%"
//                tvResultAnalysis[3].text = "$pigmentationPercent%"
//                tvResultAnalysis[4].text = "$rednessPercent%"
//                tvResultAnalysis[5].text = "$texturePercent%"
//                tvResultAnalysis[6].text = "$skinShinePercent%"
//                tvResultAnalysis[7].text = "$unevenSkinTonePercent%"
//                tvResultAnalysis[8].text = "$skinSaggingPercent%"
//            }
//        }
//
//        for (i in 0 until layoutButtonResultAnalysis.size)
//            layoutButtonResultAnalysis[i].setOnClickListener {
//
//                if (layoutButtonResultAnalysis[i].isSelected) {
//                    layoutButtonResultAnalysis[i].isSelected = false
//                    if (path?.isEmpty() == false) {
//                        path?.clear()
//                        paint?.reset()
//                        drawBitmapValue = ApiData.imgFaceBitmap?.copy(Bitmap.Config.ARGB_8888, true)
//                        canvas = Canvas(drawBitmapValue!!)
//                        imgResultFace?.setImageBitmap(drawBitmapValue)
//                    }
//                } else {
//                    for (j in 0 until layoutButtonResultAnalysis.size)
//                        layoutButtonResultAnalysis[j].isSelected = i == j
//
//                    if (path?.isEmpty() == false) {
//                        path?.clear()
//                        paint?.reset()
//                        drawBitmapValue = ApiData.imgFaceBitmap?.copy(Bitmap.Config.ARGB_8888, true)
//                        canvas = Canvas(drawBitmapValue!!)
//                        imgResultFace?.setImageBitmap(drawBitmapValue)
//                    }
//
//                    when (i) {
//                        0, 1 -> { //Main DarkCircle,  //Main Eyebags
//                            path?.add(Path())
//
//                            paint?.setARGB(150, 50, 50, 50)
//                            paint?.style = Paint.Style.FILL_AND_STROKE
//                            paint?.isAntiAlias = true
//                            paint?.strokeWidth = 1F
//
//                            path!![0].addArc(
//                                leftEyeShapes[1][2].toFloat() - leftEyeShapes[1][4].toFloat(),
//                                leftEyeShapes[1][3].toFloat() - leftEyeShapes[1][5].toFloat(),
//                                leftEyeShapes[1][2].toFloat() + leftEyeShapes[1][4].toFloat(),
//                                leftEyeShapes[1][3].toFloat() + leftEyeShapes[1][5].toFloat(),
//                                leftEyeShapes[1][7].toFloat() + leftEyeShapes[1][6].toFloat(),
//                                if (((leftEyeShapes[1][8].toFloat() - leftEyeShapes[1][7].toFloat()) + leftEyeShapes[1][6].toFloat()) > 360F)
//                                    (leftEyeShapes[1][8].toFloat() - leftEyeShapes[1][7].toFloat()) - leftEyeShapes[1][6].toFloat()
//                                else (leftEyeShapes[1][8].toFloat() - leftEyeShapes[1][7].toFloat()) + leftEyeShapes[1][6].toFloat()
//                            )
//                            path!![0].addArc(
//                                rightEyeShapes[1][2].toFloat() - rightEyeShapes[1][4].toFloat(),
//                                rightEyeShapes[1][3].toFloat() - rightEyeShapes[1][5].toFloat(),
//                                rightEyeShapes[1][2].toFloat() + rightEyeShapes[1][4].toFloat(),
//                                rightEyeShapes[1][3].toFloat() + rightEyeShapes[1][5].toFloat(),
//                                rightEyeShapes[1][7].toFloat() + rightEyeShapes[1][6].toFloat(),
//                                if (((rightEyeShapes[1][8].toFloat() - rightEyeShapes[1][7].toFloat()) + rightEyeShapes[1][6].toFloat()) > 360F)
//                                    (rightEyeShapes[1][8].toFloat() - rightEyeShapes[1][7].toFloat()) - rightEyeShapes[1][6].toFloat()
//                                else (rightEyeShapes[1][8].toFloat() - rightEyeShapes[1][7].toFloat()) + rightEyeShapes[1][6].toFloat()
//                            )
//                            canvas?.clipPath(path!![0], Region.Op.REPLACE)
//
//                            path!![0].addArc(
//                                leftEyeShapes[0][2].toFloat() - leftEyeShapes[0][4].toFloat(),
//                                leftEyeShapes[0][3].toFloat() - leftEyeShapes[0][5].toFloat(),
//                                leftEyeShapes[0][2].toFloat() + leftEyeShapes[0][4].toFloat(),
//                                leftEyeShapes[0][3].toFloat() + leftEyeShapes[0][5].toFloat(),
//                                leftEyeShapes[0][7].toFloat() + leftEyeShapes[0][6].toFloat(),
//                                if (((leftEyeShapes[0][8].toFloat() - leftEyeShapes[0][7].toFloat()) + leftEyeShapes[0][6].toFloat()) > 360F)
//                                    (leftEyeShapes[0][8].toFloat() - leftEyeShapes[0][7].toFloat()) - leftEyeShapes[0][6].toFloat()
//                                else (leftEyeShapes[0][8].toFloat() - leftEyeShapes[0][7].toFloat()) + leftEyeShapes[0][6].toFloat()
//                            )
//                            path!![0].addArc(
//                                rightEyeShapes[0][2].toFloat() - rightEyeShapes[0][4].toFloat(),
//                                rightEyeShapes[0][3].toFloat() - rightEyeShapes[0][5].toFloat(),
//                                rightEyeShapes[0][2].toFloat() + rightEyeShapes[0][4].toFloat(),
//                                rightEyeShapes[0][3].toFloat() + rightEyeShapes[0][5].toFloat(),
//                                rightEyeShapes[0][7].toFloat() + rightEyeShapes[0][6].toFloat(),
//                                if (((rightEyeShapes[0][8].toFloat() - rightEyeShapes[0][7].toFloat()) + rightEyeShapes[0][6].toFloat()) > 360F)
//                                    (rightEyeShapes[0][8].toFloat() - rightEyeShapes[0][7].toFloat()) - rightEyeShapes[0][6].toFloat()
//                                else (rightEyeShapes[0][8].toFloat() - rightEyeShapes[0][7].toFloat()) + rightEyeShapes[0][6].toFloat()
//                            )
//                            canvas?.clipPath(path!![0], Region.Op.XOR)
//
//                            canvas?.clipRect(0F, 0F, drawBitmapValue!!.width.toFloat(), drawBitmapValue!!.height.toFloat(), Region.Op.XOR)
//                            canvas?.drawRect(0F, 0f, drawBitmapValue!!.width.toFloat(), drawBitmapValue!!.height.toFloat(), paint!!)
//                        }
//                        2 -> { //Main Wrinkles
//                            path?.add(Path())
//
//                            for (j in 0 until wrinklesShapes.size) {
//                                for (k in 1..wrinklesShapes[j].size / 2) {
//                                    if (k == 1) path!![0].moveTo(wrinklesShapes[j][k].toFloat(), wrinklesShapes[j][k + (wrinklesShapes[j].size / 2)].toFloat())
//                                    path!![0].lineTo(wrinklesShapes[j][k].toFloat(), wrinklesShapes[j][k + (wrinklesShapes[j].size / 2)].toFloat())
//                                }
//
//                                paint?.setARGB(100, 255, 255, 255)
//                                paint?.style = Paint.Style.FILL_AND_STROKE
//                                paint?.isAntiAlias = true
//                                paint?.strokeWidth = 1F
//
//                                canvas?.drawPath(path!![0], paint!!)
//                            }
//                        }
//                        3 -> { //Main Pigmentation
//                            path?.add(Path())
//
//                            for (j in 0 until pigmentationShapes.size) {
//                                for (k in 1..pigmentationShapes[j].size / 2)
//                                    path!![0].addCircle(pigmentationShapes[j][k].toFloat() - 5F, pigmentationShapes[j][k + (pigmentationShapes[j].size / 2)].toFloat() - 5F, 5F, Path.Direction.CCW)
//
//                                paint?.setARGB(100, 255, 255, 255)
//                                paint?.style = Paint.Style.FILL_AND_STROKE
//                                paint?.isAntiAlias = true
//                                paint?.strokeWidth = 1F
//
//                                canvas?.drawPath(path!![0], paint!!)
//                            }
//                        }
//                        4 -> { //Main Redness
//                            path?.add(Path())
//                            path?.add(Path())
//                            path?.add(Path())
//
//                            for (j in 0 until rednessShapes.size) {
//                                for (k in 1..rednessShapes[j].size / 2) {
//                                    when (rednessShapes[j][0].toInt()) {
//                                        1 -> {
//                                            if (k == 1) path!![0].moveTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
//                                            path!![0].lineTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
//                                        }
//                                        2 -> {
//                                            if (k == 1) path!![1].moveTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
//                                            path!![1].lineTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
//                                        }
//                                        3 -> {
//                                            if (k == 1) path!![2].moveTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
//                                            path!![2].lineTo(rednessShapes[j][k].toFloat(), rednessShapes[j][k + (rednessShapes[j].size / 2)].toFloat())
//                                        }
//                                    }
//                                }
//
//                                when (rednessShapes[j][0].toInt()) {
//                                    1 -> {
//                                        paint?.setARGB(100, 255, 255, 255)
//                                        paint?.style = Paint.Style.FILL_AND_STROKE
//                                        paint?.isAntiAlias = true
//                                        paint?.strokeWidth = 1F
//
//                                        canvas?.drawPath(path!![0], paint!!)
//                                    }
//                                    2 -> {
//                                        paint?.setARGB(100, 244, 154, 97)
//                                        paint?.style = Paint.Style.FILL_AND_STROKE
//                                        paint?.isAntiAlias = true
//                                        paint?.strokeWidth = 1F
//
//                                        canvas?.drawPath(path!![1], paint!!)
//                                    }
//                                    3 -> {
//                                        paint?.setARGB(100, 250, 160, 100)
//                                        paint?.style = Paint.Style.FILL_AND_STROKE
//                                        paint?.isAntiAlias = true
//                                        paint?.strokeWidth = 1F
//
//                                        canvas?.drawPath(path!![2], paint!!)
//                                    }
//                                }
//                            }
//                        }
//                        5 -> { //Main Texture
//                            path?.add(Path())
//
//                            for (j in 0 until textureShapes.size) {
//                                for (k in 1..textureShapes[j].size / 2) {
//                                    if (k == 1) path!![0].moveTo(textureShapes[j][k].toFloat(), textureShapes[j][k + (textureShapes[j].size / 2)].toFloat())
//                                    path!![0].lineTo(textureShapes[j][k].toFloat(), textureShapes[j][k + (textureShapes[j].size / 2)].toFloat())
//                                }
//
//                                paint?.setARGB(100, 255, 255, 255)
//                                paint?.style = Paint.Style.FILL_AND_STROKE
//                                paint?.isAntiAlias = true
//                                paint?.strokeWidth = 1F
//
//                                canvas?.drawPath(path!![0], paint!!)
//                            }
//                        }
//                        6 -> { //Main SkinShine
//                            path?.add(Path())
//                            path?.add(Path())
//                            path?.add(Path())
//
//                            for (j in 0 until skinShineShapes.size) {
//                                for (k in 1..skinShineShapes[j].size / 2) {
//                                    when (skinShineShapes[j][0].toInt()) {
//                                        1 -> {
//                                            if (k == 1) path!![0].moveTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
//                                            path!![0].lineTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
//                                        }
//                                        2 -> {
//                                            if (k == 1) path!![1].moveTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
//                                            path!![1].lineTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
//                                        }
//                                        3 -> {
//                                            if (k == 1) path!![2].moveTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
//                                            path!![2].lineTo(skinShineShapes[j][k].toFloat(), skinShineShapes[j][k + (skinShineShapes[j].size / 2)].toFloat())
//                                        }
//                                    }
//                                }
//
//                                when (skinShineShapes[j][0].toInt()) {
//                                    1 -> {
//                                        paint?.setARGB(100, 255, 255, 255)
//                                        paint?.style = Paint.Style.FILL_AND_STROKE
//                                        paint?.isAntiAlias = true
//                                        paint?.strokeWidth = 1F
//
//                                        canvas?.drawPath(path!![0], paint!!)
//                                    }
//                                    2 -> {
//                                        paint?.setARGB(100, 244, 154, 97)
//                                        paint?.style = Paint.Style.FILL_AND_STROKE
//                                        paint?.isAntiAlias = true
//                                        paint?.strokeWidth = 1F
//
//                                        canvas?.drawPath(path!![1], paint!!)
//                                    }
//                                    3 -> {
//                                        paint?.setARGB(100, 250, 160, 100)
//                                        paint?.style = Paint.Style.FILL_AND_STROKE
//                                        paint?.isAntiAlias = true
//                                        paint?.strokeWidth = 1F
//
//                                        canvas?.drawPath(path!![2], paint!!)
//                                    }
//                                }
//                            }
//                        }
//                        7 -> { //Main Uneven Skin Tone
//                            path?.add(Path())
//                            path?.add(Path())
//                            path?.add(Path())
//
//                            for (j in 0 until unevenSkinToneShapes.size) {
//                                for (k in 1..unevenSkinToneShapes[j].size / 2) {
//                                    when (unevenSkinToneShapes[j][0].toInt()) {
//                                        1 -> {
//                                            if (k == 1) path!![0].moveTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
//                                            path!![0].lineTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
//                                        }
//                                        2 -> {
//                                            if (k == 1) path!![1].moveTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
//                                            path!![1].lineTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
//                                        }
//                                        3 -> {
//                                            if (k == 1) path!![2].moveTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
//                                            path!![2].lineTo(unevenSkinToneShapes[j][k].toFloat(), unevenSkinToneShapes[j][k + (unevenSkinToneShapes[j].size / 2)].toFloat())
//                                        }
//                                    }
//                                }
//
//                                when (unevenSkinToneShapes[j][0].toInt()) {
//                                    1 -> {
//                                        paint?.setARGB(100, 255, 255, 255)
//                                        paint?.style = Paint.Style.FILL_AND_STROKE
//                                        paint?.isAntiAlias = true
//                                        paint?.strokeWidth = 1F
//
//                                        canvas?.drawPath(path!![0], paint!!)
//                                    }
//                                    2 -> {
//                                        paint?.setARGB(100, 244, 154, 97)
//                                        paint?.style = Paint.Style.FILL_AND_STROKE
//                                        paint?.isAntiAlias = true
//                                        paint?.strokeWidth = 1F
//
//                                        canvas?.drawPath(path!![1], paint!!)
//                                    }
//                                    3 -> {
//                                        paint?.setARGB(100, 250, 160, 100)
//                                        paint?.style = Paint.Style.FILL_AND_STROKE
//                                        paint?.isAntiAlias = true
//                                        paint?.strokeWidth = 1F
//
//                                        canvas?.drawPath(path!![2], paint!!)
//                                    }
//                                }
//                            }
//                        }
//                        8 -> { //Main Skin Sagging
//                            path?.add(Path())
//
//                            for (j in 0 until skinSaggingShapes.size) {
//                                for (k in 1..skinSaggingShapes[j].size / 2) {
//                                    if (k == 1) path!![0].moveTo(skinSaggingShapes[j][k].toFloat(), skinSaggingShapes[j][k + (skinSaggingShapes[j].size / 2)].toFloat())
//                                    path!![0].lineTo(skinSaggingShapes[j][k].toFloat(), skinSaggingShapes[j][k + (skinSaggingShapes[j].size / 2)].toFloat())
//                                }
//
//                                paint?.setARGB(100, 255, 255, 255)
//                                paint?.style = Paint.Style.FILL_AND_STROKE
//                                paint?.isAntiAlias = true
//                                paint?.strokeWidth = 1F
//
//                                canvas?.drawPath(path!![0], paint!!)
//                            }
//                        }
//                    }
//
//                    imgResultFace?.setImageBitmap(drawBitmapValue)
//                }
//            }
//    }
}
