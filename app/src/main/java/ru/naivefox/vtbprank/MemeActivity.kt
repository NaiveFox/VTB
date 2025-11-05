package ru.naivefox.vtbprank

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MemeActivity : AppCompatActivity() {

    private var vibrator: Vibrator? = null
    private var cameraManager: CameraManager? = null
    private var torchId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)

        // Экран не гаснет, пока живёт активити
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Сразу включаем вибрацию и фонарик
        startVibration()
        if (ensureTorchPermission()) {
            turnTorch(true)
        }
    }

    // Нам не нужно гасить при сворачивании — гасим только когда активити реально уничтожается
    override fun onDestroy() {
        super.onDestroy()
        turnTorch(false)
        vibrator?.cancel()
    }

    private fun startVibration() {
        val vib = if (Build.VERSION.SDK_INT >= 31) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrator = vib

        // Паттерн по кругу: 0 пауза, 400мс вибра, 120мс пауза (повтор бесконечный)
        val pattern = longArrayOf(0, 400, 120)
        if (Build.VERSION.SDK_INT >= 26) {
            vib.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vib.vibrate(pattern, 0)
        }
    }

    private fun ensureTorchPermission(): Boolean {
        // На Android 13+ FLASHLIGHT — normal permission, камера не нужна
        return if (Build.VERSION.SDK_INT >= 33) {
            true
        } else {
            val ok = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (!ok) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 777)
            }
            ok
        }
    }

    private fun turnTorch(on: Boolean) {
        try {
            if (cameraManager == null) {
                cameraManager = getSystemService(CameraManager::class.java)
                // Берём первую камеру со вспышкой
                cameraManager?.cameraIdList?.firstOrNull { id ->
                    val chars = cameraManager!!.getCameraCharacteristics(id)
                    chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }?.let { torchId = it }
            }
            torchId?.let { id ->
                cameraManager?.setTorchMode(id, on)
            }
        } catch (_: Throwable) {
            // На некоторых девайсах/прошивках фонарик может быть недоступен — игнорим
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 777 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            turnTorch(true)
        }
    }
}
