package com.like360.demo.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.format.Formatter
import android.text.style.ForegroundColorSpan
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.like360.demo.R
import com.like360.demo.ui.MainActivity
import com.like360.demo.utils.CpuTempUtils
import com.like360.demo.utils.MemoryUtils
import com.like360.demo.utils.PhoneStorageUtils

class NotificationService : Service() {
    private lateinit var mNotificationManager: NotificationManager
    private var mNotification: Notification? = null

    /***
     * 垃圾size
     */
    private var mRubbishSize: Long = 0

    inner class LocalBinder : Binder() {
        val service: NotificationService
            get() = this@NotificationService
    }

    companion object {
        const val ACTION_UPDATE = "ACTION_UPDATE"
        const val INTENT_EXTRA_RUBBISH_SIZE = "INTENT_EXTRA_RUBBISH_SIZE"
        const val INTENT_EXTRA_MEMORY_PERCENT_WITH_INT = "INTENT_EXTRA_MEMORY_PERCENT_WITH_INT"

        /***
         * 通知栏channel id
         */
        private const val CHANNEL_ID = 0x112

        fun updatePhoneStatusInfo(context: Context, rubbishSize: Long) {
            val intent = Intent(ACTION_UPDATE)
            intent.putExtra(INTENT_EXTRA_RUBBISH_SIZE, rubbishSize)
            context.sendBroadcast(intent)
        }
    }

    private var mRemoteViews: RemoteViews? = null

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (null != intent) {
                if (ACTION_UPDATE == intent.action) {
                    mRubbishSize = intent.getLongExtra(INTENT_EXTRA_RUBBISH_SIZE, 0L)
                    updateNotification()
                }
            }
        }
    }

    private fun updateNotification() {
        initRemoteViews()
        mNotification!!.`when` = System.currentTimeMillis()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = CHANNEL_ID.toString() + ""
            val channelName: CharSequence = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mNotificationManager!!.createNotificationChannel(mChannel)
        }

        mNotificationManager.notify(CHANNEL_ID, mNotification)

    }

    override fun onBind(intent: Intent): IBinder? {
        return LocalBinder()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val percentInt = MemoryUtils.getUsedMemPercent(this)
        if (null != intent) {
            mRubbishSize = intent.getLongExtra(INTENT_EXTRA_RUBBISH_SIZE, 0L)
        }
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotification = createNotification()
        startForeground(CHANNEL_ID, mNotification)
//        mNotificationManager!!.notify(CHANNEL_ID, mNotification);
        return START_STICKY
    }

    /***
     * 创建通知栏
     * @return
     */
    private fun createNotification(): Notification {
        val channelId = CHANNEL_ID.toString() + ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName: CharSequence = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mNotificationManager.createNotificationChannel(mChannel)
        }
        if (null == mRemoteViews) {
            mRemoteViews = RemoteViews(packageName, R.layout.layout_notification)
            initRemoteViews()
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            //.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContent(mRemoteViews)
            .build()
        notification.`when` = System.currentTimeMillis()
        val intentFilter = IntentFilter(ACTION_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)
        return notification
    }

    private fun initRemoteViews() {
        val successColor = ContextCompat.getColor(this, R.color.color_success);
        if (null != mRemoteViews) {
            val spannableStringBuilder = SpannableStringBuilder()
            val text1 = "发现 "
            val rubbishSize = Formatter.formatFileSize(this, mRubbishSize) + ""
            val text2 = " 垃圾"
            spannableStringBuilder.append(text1).append(rubbishSize).append(text2)
            spannableStringBuilder.setSpan(
                ForegroundColorSpan(successColor),
                text1.length,
                text1.length + rubbishSize.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )

            mRemoteViews!!.setTextViewText(R.id.tv_desc, spannableStringBuilder)


            val intent = MainActivity.actionCleanRubbish(this)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            mRemoteViews!!.setOnClickPendingIntent(R.id.tv_clean, pendingIntent)


            val storagePercentStr = PhoneStorageUtils.getStoragePercent(this)

            mRemoteViews!!.setTextViewText(R.id.tv_storage_percent, storagePercentStr)
            mRemoteViews!!.setTextColor(R.id.tv_storage_percent, successColor)


            mRemoteViews!!.setTextViewText(R.id.tv_cpu_temperature, "${CpuTempUtils.getTemp()}°C")
            mRemoteViews!!.setTextColor(R.id.tv_cpu_temperature, successColor)

            var memoryPercent = MemoryUtils.getUsedMemPercent(this)

            mRemoteViews!!.setTextViewText(R.id.tv_percent, "$memoryPercent%")
            mRemoteViews!!.setInt(R.id.progress_bar, "setSecondaryProgress", memoryPercent)


            val intentMemory = MainActivity.actionCleanMemory(this)
            val pendingIntent2 =
                PendingIntent.getActivity(
                    this,
                    0x111,
                    intentMemory,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            mRemoteViews!!.setOnClickPendingIntent(R.id.fl_memory, pendingIntent2)


            val intentStorage = MainActivity.actionStorage(this);

            val pendingStorage =
                PendingIntent.getActivity(
                    this,
                    0x222,
                    intentStorage,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            mRemoteViews!!.setOnClickPendingIntent(R.id.ll_storage, pendingStorage)


            val intentCpu = MainActivity.actionCpuTemperature(this);

            val pendingCpu =
                PendingIntent.getActivity(this, 0x333, intentCpu, PendingIntent.FLAG_UPDATE_CURRENT)
            mRemoteViews!!.setOnClickPendingIntent(R.id.ll_cpu, pendingCpu)


            val settingsIntent = Intent(Settings.ACTION_SETTINGS)
            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val settingsPendingIntent = PendingIntent.getActivity(
                this,
                101,
                settingsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            mRemoteViews!!.setOnClickPendingIntent(R.id.ll_settings, settingsPendingIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

}