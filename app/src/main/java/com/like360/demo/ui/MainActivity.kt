package com.like360.demo.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import com.boundless.ktx.base.BVBActivity
import com.like360.demo.R
import com.like360.demo.databinding.ActivityMainBinding
import com.like360.demo.ext.toast
import com.like360.demo.service.NotificationService

class MainActivity : BVBActivity<ActivityMainBinding>() {
    companion object {

        private const val ACTION_CLEAN_MEMORY: String = "ACTION_CLEAN_MEMORY"
        private const val ACTION_CLEAN_RUBBISH: String = "ACTION_CLEAN_RUBBISH"
        private const val ACTION_CPU_TEMPERATURE: String = "ACTION_CPU_TEMPERATURE"
        private const val ACTION_STORAGE: String = "ACTION_STORAGE"


        fun actionCleanRubbish(context: Context): Intent {
            val intent: Intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ACTION_CLEAN_RUBBISH, true)
            return intent
        }

        fun actionCleanMemory(context: Context): Intent {
            val intent: Intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ACTION_CLEAN_MEMORY, true)
            return intent
        }

        fun actionStorage(context: Context): Intent {
            val intent: Intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ACTION_STORAGE, true)
            return intent
        }

        fun actionCpuTemperature(context: Context): Intent {
            val intent: Intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ACTION_CPU_TEMPERATURE, true)
            return intent
        }


    }


    override fun afterOnCreate(savedInstanceState: Bundle?) {


        var rubbishSize: Long = (Math.random() * System.currentTimeMillis()).toLong() / 1000

        val intent = Intent(this, NotificationService::class.java)
        intent.putExtra(NotificationService.INTENT_EXTRA_RUBBISH_SIZE, rubbishSize)
        ContextCompat.startForegroundService(this, intent)

        val countDownTimer: CountDownTimer = object : CountDownTimer(600000, 6000) {
            override fun onTick(millisUntilFinished: Long) {
                rubbishSize = (Math.random() * System.currentTimeMillis()).toLong() / 1000

                NotificationService.updatePhoneStatusInfo(
                    mActivity, rubbishSize
                )
                if (mDataBinding.lottieView.isAnimating) {
                    mDataBinding.lottieView.cancelAnimation()
                    mDataBinding.lottieView.visibility = View.GONE
                }

            }

            override fun onFinish() {
                stopService()
            }
        }
        countDownTimer.start()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        var issCleanRubbish = intent?.hasExtra(ACTION_CLEAN_RUBBISH) == true
        if (issCleanRubbish) {
            toast("清除垃圾")
            mDataBinding.lottieView.run {
                this.visibility = View.VISIBLE
                this.imageAssetsFolder = "images"
                this.setAnimation("scan_rubbish_anim.json")
                this.repeatCount = ValueAnimator.INFINITE
                this.playAnimation()
            }

        } else if (intent?.hasExtra(ACTION_CLEAN_MEMORY) == true) {
            toast("清除内存")

        }
    }


    override fun onDestroy() {
        stopService()
        super.onDestroy()
    }

    private fun stopService() {
        stopService(Intent(mActivity, NotificationService::class.java))
    }

    override fun getLayoutId(): Int = R.layout.activity_main
}