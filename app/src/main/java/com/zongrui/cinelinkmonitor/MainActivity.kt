package com.zongrui.cinelinkmonitor

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.hardware.usb.UsbDevice
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.jiangdg.ausbc.CameraClient
import com.jiangdg.ausbc.base.CameraActivity
import com.jiangdg.ausbc.callback.IDeviceConnectCallBack
import com.jiangdg.ausbc.camera.CameraUvcStrategy
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio
import com.serenegiant.usb.USBMonitor
import com.zongrui.cinelinkmonitor.camera.UvcMonitorConfig
import com.zongrui.cinelinkmonitor.lut.CubeLut
import com.zongrui.cinelinkmonitor.lut.CubeLutParseException
import com.zongrui.cinelinkmonitor.lut.CubeLutParser
import com.zongrui.cinelinkmonitor.render.DesqueezeFactor
import com.zongrui.cinelinkmonitor.render.LutPreviewEffect
import com.zongrui.cinelinkmonitor.settings.MonitorMode
import com.zongrui.cinelinkmonitor.settings.RenderSettings
import com.zongrui.cinelinkmonitor.settings.RenderSettingsStore
import com.zongrui.cinelinkmonitor.ui.LandscapeMonitorLayout
import java.io.IOException

class MainActivity : CameraActivity() {
    private lateinit var root: FrameLayout
    private lateinit var previewContainer: FrameLayout
    private lateinit var previewView: AspectRatioTextureView
    private lateinit var statusText: TextView
    private lateinit var modeButton: Button
    private lateinit var desqueezeButton: Button
    private lateinit var lutButton: Button
    private lateinit var lutImportButton: Button
    private lateinit var lutIntensity: SeekBar
    private lateinit var hintText: TextView
    private lateinit var settingsStore: RenderSettingsStore

    private val handler = Handler(Looper.getMainLooper())
    private val layoutSpec = LandscapeMonitorLayout.Default
    private val uvcStrategy: CameraUvcStrategy by lazy {
        CameraUvcStrategy(this).apply {
            setDeviceConnectStatusListener(usbStatusCallback)
        }
    }
    private val uvcCameraClient: CameraClient by lazy {
        CameraClient.newBuilder(this)
            .setEnableGLES(true)
            .setCameraStrategy(uvcStrategy)
            .setCameraRequest(
                CameraRequest.Builder()
                    .setPreviewWidth(UvcMonitorConfig.DEFAULT_PREVIEW_WIDTH)
                    .setPreviewHeight(UvcMonitorConfig.DEFAULT_PREVIEW_HEIGHT)
                    .create(),
            )
            .openDebug(true)
            .build()
    }
    private var settings = RenderSettings.default()
    private var currentLut: CubeLut? = null
    private var lutEffect: LutPreviewEffect? = null
    private var usbStatus = "等待 USB UVC 信号 · A7C II"

    private val usbStatusCallback = object : IDeviceConnectCallBack {
        override fun onAttachDev(device: UsbDevice?) {
            val label = device?.usbLabel() ?: "USB 设备"
            showUsbStatus("检测到 $label，等待 USB 授权")
        }

        override fun onDetachDec(device: UsbDevice?) {
            val label = device?.usbLabel() ?: "USB 设备"
            showUsbStatus("$label 已断开")
        }

        override fun onConnectDev(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
            val label = device?.usbLabel() ?: "USB UVC"
            showUsbStatus("$label 已授权，正在打开预览")
        }

        override fun onDisConnectDec(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
            val label = device?.usbLabel() ?: "USB UVC"
            showUsbStatus("$label 预览连接已关闭")
        }

        override fun onCancelDev(device: UsbDevice?) {
            val label = device?.usbLabel() ?: "USB 设备"
            showUsbStatus("已取消 $label 的 USB 授权")
        }
    }

    private val statusTicker = object : Runnable {
        override fun run() {
            refreshStatus()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        enterImmersiveMode()
    }

    override fun getRootView(inflater: LayoutInflater): View {
        settingsStore = RenderSettingsStore(this)
        settings = settingsStore.load()

        root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(5, 8, 10))
        }
        previewContainer = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
        }
        previewView = AspectRatioTextureView(this)
        previewContainer.addView(
            previewView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER,
            ),
        )
        root.addView(
            previewContainer,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ).apply {
                rightMargin = dp(layoutSpec.previewRightInsetDp)
            },
        )
        root.addView(buildTopBar())
        root.addView(buildControlRail())
        root.addView(buildHint())
        return root
    }

    override fun initView() {
        super.initView()
        bindControls()
        applySettings()
    }

    override fun initData() {
        super.initData()
        handler.post(statusTicker)
    }

    override fun onDestroy() {
        handler.removeCallbacks(statusTicker)
        super.onDestroy()
    }

    override fun getCameraView(): IAspectRatio = previewView

    override fun getCameraViewContainer(): ViewGroup = previewContainer

    override fun getCameraClient(): CameraClient = uvcCameraClient

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LUT && resultCode == Activity.RESULT_OK) {
            data?.data?.let(::loadLut)
        }
    }

    private fun buildTopBar(): View {
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(8), dp(14), dp(8))
            setBackgroundColor(Color.argb(176, 6, 12, 15))
        }
        val title = TextView(this).apply {
            text = "CineLink Monitor"
            setTextColor(Color.WHITE)
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
        }
        statusText = TextView(this).apply {
            setTextColor(Color.rgb(143, 227, 218))
            textSize = 12f
            gravity = Gravity.END
            maxLines = 2
        }
        top.addView(title, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        top.addView(statusText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        return FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP,
        ).apply {
            leftMargin = dp(12)
            topMargin = dp(10)
            rightMargin = dp(layoutSpec.overlayRightInsetDp)
        }.let { params -> top.apply { layoutParams = params } }
    }

    private fun buildControlRail(): View {
        val rail = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(12), dp(14), dp(12), dp(14))
            setBackgroundColor(Color.argb(230, 6, 12, 15))
        }

        val title = TextView(this).apply {
            text = "监看控制"
            setTextColor(Color.WHITE)
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
        }
        val lutLabel = TextView(this).apply {
            text = "LUT 强度"
            setTextColor(Color.rgb(164, 188, 190))
            textSize = 12f
            gravity = Gravity.CENTER
        }

        modeButton = monitorButton()
        desqueezeButton = monitorButton()
        lutButton = monitorButton()
        lutImportButton = monitorButton("导入 LUT")
        lutIntensity = SeekBar(this).apply {
            max = 100
            progressTintList = ColorStateList.valueOf(Color.rgb(73, 211, 201))
            thumbTintList = ColorStateList.valueOf(Color.rgb(232, 248, 246))
        }

        rail.addView(title, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(28)))
        rail.addView(modeButton, controlButtonParams(topMargin = 12))
        rail.addView(desqueezeButton, controlButtonParams(topMargin = 8))
        rail.addView(lutButton, controlButtonParams(topMargin = 8))
        rail.addView(lutImportButton, controlButtonParams(topMargin = 8))
        rail.addView(lutLabel, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(28)).apply {
            topMargin = dp(12)
        })
        rail.addView(lutIntensity, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(42)))

        return FrameLayout.LayoutParams(
            dp(layoutSpec.controlRailWidthDp),
            FrameLayout.LayoutParams.MATCH_PARENT,
            Gravity.END,
        ).let { params -> rail.apply { layoutParams = params } }
    }

    private fun buildHint(): View {
        hintText = TextView(this).apply {
            text = "将 A7C II 设置为 USB Streaming / Live Stream 后，用 USB-C 数据线连接 Pixel 6 Pro"
            setTextColor(Color.rgb(232, 248, 246))
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(dp(22), dp(16), dp(22), dp(16))
            setBackgroundColor(Color.argb(150, 16, 33, 38))
        }
        return FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER,
        ).apply {
            leftMargin = dp(28)
            rightMargin = dp(layoutSpec.overlayRightInsetDp)
        }.let { params -> hintText.apply { layoutParams = params } }
    }

    private fun bindControls() {
        modeButton.setOnClickListener {
            settings = settings.copySettings(
                monitorMode = if (settings.monitorMode == MonitorMode.Video) MonitorMode.Photo else MonitorMode.Video,
            )
            persistAndApply()
        }
        desqueezeButton.setOnClickListener {
            settings = settings.copySettings(
                desqueezeFactor = when (settings.desqueezeFactor.value) {
                    1f -> DesqueezeFactor.Cinema133
                    1.33f -> DesqueezeFactor.custom(1.50f)
                    1.50f -> DesqueezeFactor.custom(1.80f)
                    else -> DesqueezeFactor.Normal
                },
            )
            persistAndApply()
        }
        lutButton.setOnClickListener {
            settings = settings.copySettings(lutEnabled = !settings.lutEnabled)
            persistAndApply()
        }
        lutImportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, REQUEST_LUT)
        }
        lutIntensity.progress = (settings.lutIntensity * 100).toInt()
        lutIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    settings = settings.copySettings(lutIntensity = progress / 100f)
                    persistAndApply()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    private fun loadLut(uri: Uri) {
        try {
            val lut = contentResolver.openInputStream(uri)?.use(CubeLutParser::parse)
                ?: throw IOException("Unable to open selected LUT.")
            currentLut = lut
            settings = settings.copySettings(
                lutEnabled = true,
                lutName = lut.title,
            )
            persistAndApply()
            showHint("LUT 已导入：${lut.title}")
        } catch (error: CubeLutParseException) {
            showHint("LUT 格式错误：${error.message}")
        } catch (error: IOException) {
            showHint("无法读取 LUT：${error.message}")
        }
    }

    private fun persistAndApply() {
        settingsStore.save(settings)
        applySettings()
    }

    private fun applySettings() {
        previewView.scaleX = settings.desqueezeFactor.value
        modeButton.text = if (settings.monitorMode == MonitorMode.Video) "视频" else "照片"
        desqueezeButton.text = settings.desqueezeFactor.label
        lutButton.text = if (settings.lutEnabled) "LUT 开" else "LUT 关"
        lutIntensity.progress = (settings.lutIntensity * 100).toInt()
        applyLutEffect()
        refreshStatus()
    }

    private fun applyLutEffect() {
        val client = getCameraClient() ?: return
        lutEffect?.let(client::removeRenderEffect)
        lutEffect = null
        val lut = currentLut
        if (settings.lutEnabled && lut != null) {
            lutEffect = LutPreviewEffect(this, lut, settings.lutIntensity).also(client::addRenderEffect)
        }
    }

    private fun refreshStatus() {
        val opened = getCameraClient()?.isCameraOpened() == true
        statusText.text = if (opened) {
            "USB Streaming 已连接 · ${settings.monitorMode.name} · LUT ${if (settings.lutEnabled) "ON" else "OFF"}"
        } else {
            usbStatus
        }
        hintText.visibility = if (opened) View.GONE else View.VISIBLE
    }

    private fun showHint(message: String) {
        hintText.text = message
        hintText.visibility = View.VISIBLE
        handler.postDelayed({ refreshStatus() }, 2600)
    }

    private fun showUsbStatus(message: String) {
        handler.post {
            usbStatus = message
            if (::hintText.isInitialized && getCameraClient().isCameraOpened() != true) {
                hintText.text = message
                hintText.visibility = View.VISIBLE
            }
            refreshStatus()
        }
    }

    private fun enterImmersiveMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        }
    }

    private fun monitorButton(textValue: String = ""): Button =
        Button(this).apply {
            text = textValue
            isAllCaps = false
            setTextColor(Color.rgb(232, 248, 246))
            textSize = 12f
            maxLines = 2
            includeFontPadding = false
            setBackgroundColor(Color.rgb(16, 33, 38))
        }

    private fun controlButtonParams(topMargin: Int): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(46)).apply {
            this.topMargin = dp(topMargin)
        }

    private fun UsbDevice.usbLabel(): String = "VID ${vendorId.toString(16)} / PID ${productId.toString(16)}"

    private fun RenderSettings.copySettings(
        monitorMode: MonitorMode = this.monitorMode,
        desqueezeFactor: DesqueezeFactor = this.desqueezeFactor,
        lutEnabled: Boolean = this.lutEnabled,
        lutIntensity: Float = this.lutIntensity,
        lutName: String? = this.lutName,
    ): RenderSettings = RenderSettings(
        monitorMode = monitorMode,
        desqueezeFactor = desqueezeFactor,
        lutEnabled = lutEnabled,
        lutIntensity = lutIntensity,
        lutName = lutName,
    )

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val REQUEST_LUT = 3001
    }
}
