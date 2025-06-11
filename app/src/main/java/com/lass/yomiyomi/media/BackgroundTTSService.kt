package com.lass.yomiyomi.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import com.lass.yomiyomi.R
import com.lass.yomiyomi.MainActivity
import androidx.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.MediaMetadataCompat

@AndroidEntryPoint
class BackgroundTTSService : Service() {
    
    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = "ACTION_SKIP_NEXT"
        const val ACTION_SKIP_PREVIOUS = "ACTION_SKIP_PREVIOUS"
        
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "BACKGROUND_TTS_CHANNEL"
    }
    
    @Inject
    lateinit var backgroundTTSManager: BackgroundTTSManager
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private lateinit var mediaSession: MediaSessionCompat
    
    override fun onCreate() {
        super.onCreate()
        
        createNotificationChannel()
        
        // MediaSessionCompat 생성 및 활성화
        mediaSession = MediaSessionCompat(this, "YomiYomiTTS")
        mediaSession.isActive = true
        
        // 미디어 버튼 콜백 등록
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                backgroundTTSManager.resume()
            }
            override fun onPause() {
                backgroundTTSManager.pause()
            }
            override fun onSkipToNext() {
                backgroundTTSManager.skipNext()
            }
            override fun onSkipToPrevious() {
                backgroundTTSManager.skipPrevious()
            }
        })
        
        // BackgroundTTSManager 상태 관찰
        serviceScope.launch {
            backgroundTTSManager.isPlaying.collectLatest { isPlaying ->
                updateNotification(isPlaying)
                
                // TTS가 정지되고 큐가 비어있으면 서비스 종료
                if (!isPlaying && backgroundTTSManager.progress.value.totalCount == 0) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
        
        serviceScope.launch {
            backgroundTTSManager.currentText.collectLatest { currentText ->
                updateNotificationText(currentText)
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, createNotification(false, "", TTSProgress(0, 0)))
            }
            ACTION_STOP -> {
                // 매니저에서 TTS만 정지하고, 서비스는 상태 관찰을 통해 자동 종료
                backgroundTTSManager.stop()
            }
            ACTION_PLAY_PAUSE -> {
                backgroundTTSManager.togglePlayPause()
            }
            ACTION_SKIP_NEXT -> {
                backgroundTTSManager.skipNext()
            }
            ACTION_SKIP_PREVIOUS -> {
                backgroundTTSManager.skipPrevious()
            }
        }
        
        return START_NOT_STICKY // 서비스가 죽어도 자동 재시작하지 않음
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        backgroundTTSManager.stop()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "백그라운드 TTS",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "백그라운드에서 일본어 문장을 읽어주는 서비스입니다"
                setSound(null, null)
                enableVibration(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(isPlaying: Boolean, currentText: String, progress: TTSProgress): Notification {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE
        )
        
        val playPauseIntent = Intent(this, BackgroundTTSService::class.java).apply {
            setAction(ACTION_PLAY_PAUSE)
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE
        )
        
        val skipNextIntent = Intent(this, BackgroundTTSService::class.java).apply {
            setAction(ACTION_SKIP_NEXT)
        }
        val skipNextPendingIntent = PendingIntent.getService(
            this, 1, skipNextIntent, PendingIntent.FLAG_IMMUTABLE
        )
        
        val skipPreviousIntent = Intent(this, BackgroundTTSService::class.java).apply {
            setAction(ACTION_SKIP_PREVIOUS)
        }
        val skipPreviousPendingIntent = PendingIntent.getService(
            this, 2, skipPreviousIntent, PendingIntent.FLAG_IMMUTABLE
        )
        
        val stopIntent = Intent(this, BackgroundTTSService::class.java).apply {
            setAction(ACTION_STOP)
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 3, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )
        
        val displayText = if (currentText.isNotBlank()) {
            val progressText = "${progress.currentIndex + 1}/${progress.totalCount}"
            if (currentText.length > 50) {
                "${currentText.take(50)}... ($progressText)"
            } else {
                "$currentText ($progressText)"
            }
        } else {
            "대기 중..."
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("YomiYomi - 백그라운드 학습")
            .setContentText(displayText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(mainPendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setOnlyAlertOnce(false)
            // MediaStyle 적용
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(
                android.R.drawable.ic_media_previous,
                "이전",
                skipPreviousPendingIntent
            )
            .addAction(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (isPlaying) "일시정지" else "재생",
                playPausePendingIntent
            )
            .addAction(
                android.R.drawable.ic_media_next,
                "다음",
                skipNextPendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "정지",
                stopPendingIntent
            )
            .build()
    }
    
    private fun updateNotification(isPlaying: Boolean) {
        val currentText = backgroundTTSManager.currentText.value
        val progress = backgroundTTSManager.progress.value
        // PlaybackState 설정
        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
            .setState(
                if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                1.0f
            )
        mediaSession.setPlaybackState(stateBuilder.build())

        // MediaMetadata 설정
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, if (currentText.isNotBlank()) currentText else "YomiYomi TTS")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "YomiYomi")
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Background Learning")
            .build()
        mediaSession.setMetadata(metadata)

        val notification = createNotification(isPlaying, currentText, progress)
        val notificationManager = NotificationManagerCompat.from(this)
        try {
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // 알림 권한이 없는 경우 무시
        }
    }
    
    private fun updateNotificationText(currentText: String) {
        serviceScope.launch {
            val isPlaying = backgroundTTSManager.isPlaying.value
            val progress = backgroundTTSManager.progress.value
            // MediaMetadata도 갱신!
            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, if (currentText.isNotBlank()) currentText else "YomiYomi TTS")
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "YomiYomi")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Background Learning")
                .build()
            mediaSession.setMetadata(metadata)

            val notification = createNotification(isPlaying, currentText, progress)
            val notificationManager = NotificationManagerCompat.from(this@BackgroundTTSService)
            try {
                notificationManager.notify(NOTIFICATION_ID, notification)
            } catch (e: SecurityException) {
                // 알림 권한이 없는 경우 무시
            }
        }
    }
} 
