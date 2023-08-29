package com.domainbangla.remoteview

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient



class MainActivity : AppCompatActivity() {

    val LOG_TAG = "VoiceSample"
    val CUSTOM_SDK_INTENT = "com.vuzix.sample.vuzix_voicecontrolwithsdk.CustomIntent"
     var mVoiceCmdReceiver: VoiceCmdReceiver? = null
    private var mRecognizerActive = false

    lateinit var btnShowMenu:Button
    lateinit var btnOk:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnShowMenu = findViewById<Button>(R.id.btnShow)
        btnOk = findViewById<Button>(R.id.btnOk)
        btnShowMenu.setOnClickListener {
            showPopupMenu(it)
        }

        btnOk.setOnClickListener {
            showToast("Okay actioned")
        }

        // Create the voice command receiver class
        // Create the voice command receiver class
        mVoiceCmdReceiver = VoiceCmdReceiver(this)

        // Now register another intent handler to demonstrate intents sent from the service

        // Now register another intent handler to demonstrate intents sent from the service
        myIntentReceiver = MyIntentReceiver()
        registerReceiver(myIntentReceiver, IntentFilter(CUSTOM_SDK_INTENT))


        // Register a custom command
//        var sc =  VuzixSpeechClient(this);
//        sc.insertKeycodePhrase("show", KeyEvent.KEYCODE_ENTER);


    }

     fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.option_menu) // Inflate your menu XML here

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.option1 -> {
                    // Handle option 1 click
                    true
                }
                R.id.option2 -> {
                    // Handle option 2 click
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onDestroy() {
        mVoiceCmdReceiver!!.unregister()
        unregisterReceiver(myIntentReceiver)
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    fun showRemoteView(){
        val remoteCollapsedViews: RemoteViews =
            RemoteViews("com.domainbangla.remoteview", R.layout.notification_collapsed_views)
        val remoteExpandedViews: RemoteViews =
            RemoteViews("com.domainbangla.remoteview", R.layout.notification_expanded_views)

        remoteCollapsedViews.setTextViewText(R.id.txtVideoCallNotif, "Incoming Video Call")
        remoteExpandedViews.setTextViewText(R.id.txtVideoCallNotif, "Incoming Video Call")

        var acceptPendingIntent: PendingIntent

        acceptPendingIntent = PendingIntent.getActivity( this, 0,
            Intent(this, ViewActivity::class.java).apply {
                action = "accept"
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val rejectPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, ViewActivity::class.java).apply {
                action = "reject"
                /* putExtra("intentType", intentType)*/
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )

        remoteCollapsedViews.setOnClickPendingIntent(
            R.id.btnVideoCallAccept,
            acceptPendingIntent
        )
        remoteExpandedViews.setOnClickPendingIntent(
            R.id.btnVideoCallAccept,
            acceptPendingIntent
        )
        remoteCollapsedViews.setOnClickPendingIntent(
            R.id.btnVideoCalReject,
            rejectPendingIntent
        )
        remoteExpandedViews.setOnClickPendingIntent(R.id.btnVideoCalReject, rejectPendingIntent)

        val notificationBuilder =
            NotificationCompat.Builder(this,"1203S")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.RED)
                .setLights(Color.RED, 3000, 3000)
                .setContentTitle("Incoming Message / Video Request")
              //  .setContentText("Call from dashboard")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory("android.intent.category.LAUNCHER")
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setContent(remoteCollapsedViews)
                .setCustomBigContentView(remoteExpandedViews)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())

                with(NotificationManagerCompat.from(this@MainActivity)) {
                    notify(101, notificationBuilder.build())
                }
    }

    fun showToast(msg:String){
        Toast.makeText(this@MainActivity,msg,Toast.LENGTH_SHORT).show()
    }


    fun RecognizerChangeCallback(isRecognizerActive: Boolean) {
        mRecognizerActive = isRecognizerActive
    }

    /**
     * You may prefer using explicit intents for each recognized phrase. This receiver demonstrates that.
     */
    private var myIntentReceiver: MyIntentReceiver? = null

    class MyIntentReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "Custom Intent Detected", Toast.LENGTH_LONG).show()
        }
    }

}