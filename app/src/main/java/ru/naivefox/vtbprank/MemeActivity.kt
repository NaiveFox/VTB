package ru.naivefox.vtbprank

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.*
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MemeActivity : AppCompatActivity() {

    private var vibrator: Vibrator? = null
    private var cameraManager: CameraManager? = null
    private var torchId: String? = null

    // надёжный повтор вибра каждые ~520 мс
    private val handler = Handler(Looper.getMainLooper())
    private var keepVibrating = false
    private val vibLoop = object : Runnable {
        override fun run() {
            vibrateOnce(400)                 // 400 мс вибра
            if (keepVibrating) handler.postDelayed(this, 520) // пауза ~120 мс
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        startVibrationLoop()
        if (ensureTorchPermission()) turnTorch(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVibrationLoop()
        turnTorch(false)
    }

    // ---------- ВИБРА ----------

    private fun getVibrator(): Vibrator {
        val v = if (Build.VERSION.SDK_INT >= 31) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        return v
    }

    private fun startVibrationLoop() {
        vibrator = getVibrator()
        // если девайс вообще без вибромотора — просто выходим
        try {
            val has = if (Build.VERSION.SDK_INT >= 26) vibrator?.hasVibrator() == true else true
            if (!has) return
        } catch (_: Throwable) { /* ок */ }

        // запускаем «вечную» вибру через Handler — надёжнее, чем waveform repeat
        keepVibrating = true
        handler.post(vibLoop)
    }

    private fun vibrateOnce(durationMs: Long) {
        val v = vibrator ?: return
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(durationMs)
            }
        } catch (_: Throwable) { /* некоторые прошивки могут блокировать — игнорим */ }
    }

    private fun stopVibrationLoop() {
        keepVibrating = false
        handler.removeCallbacks(vibLoop)
        try { vibrator?.cancel() } catch (_: Throwable) {}
    }

    // ---------- ФОНАРЬ ----------

    private fun ensureTorchPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            true // FLASHLIGHT — normal
        } else {
            val ok = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (!ok) ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 777)
            ok
        }
    }

    private fun turnTorch(on: Boolean) {
        try {
            if (cameraManager == null) {
                cameraManager = getSystemService(CameraManager::class.java)
                torchId = cameraManager?.cameraIdList?.firstOrNull { id ->
                    val chars = cameraManager!!.getCameraCharacteristics(id)
                    chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }
            }
            torchId?.let { cameraManager?.setTorchMode(it, on) }
        } catch (_: Throwable) { /* может быть недоступно — игнорим */ }
    }

    override fun onRequestPermissionsResult(code: Int, perms: Array<out String>, res: IntArray) {
        super.onRequestPermissionsResult(code, perms, res)
        if (code == 777 && res.isNotEmpty() && res[0] == PackageManager.PERMISSION_GRANTED) {
            turnTorch(true)
        }
    }
}
