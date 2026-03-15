package com.drhako.myford;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.VideoView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer musicPlayer;
    private boolean uiShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView videoView = findViewById(R.id.introVideoView);
        View blurOverlay = findViewById(R.id.blurOverlay);
        View mainContent = findViewById(R.id.mainContent);
        LinearLayout btnOpenMaps = findViewById(R.id.btnOpenMaps);

        // 1. Video Ayarı (1080p intro_video.mp4)
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro_video);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            videoView.start();
        });

        // 2. Müzik Ayarı (Kırptığın intro_music.mp3)
        musicPlayer = MediaPlayer.create(this, R.raw.intro_music);
        if (musicPlayer != null) {
            musicPlayer.start();
            musicPlayer.setOnCompletionListener(MediaPlayer::release);
        }

        // 3. Arayüzün Süzülerek Gelmesi (Video başladıktan 3 saniye sonra)
        videoView.postDelayed(() -> {
            if (!uiShown) {
                blurOverlay.animate().alpha(1.0f).setDuration(1500);
                mainContent.animate().alpha(1.0f).setDuration(2000);
                uiShown = true;
            }
        }, 3000); 

        // 4. Yandex Navigasyon Butonu
        btnOpenMaps.setOnClickListener(v -> {
            try {
                // Yandex'i doğrudan harita ve trafik açık modda başlatır
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("yandexnavi://"));
                intent.setPackage("ru.yandex.yandexnavi");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                // Yandex yüklü değilse uyarı ver ve Play Store'a yönlendir
                Toast.makeText(this, "Yandex Navigasyon yüklü değil!", Toast.LENGTH_SHORT).show();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ru.yandex.yandexnavi")));
                } catch (Exception e2) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=ru.yandex.yandexnavi")));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            musicPlayer.release();
        }
    }
}
