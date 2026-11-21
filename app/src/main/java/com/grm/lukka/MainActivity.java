package com.grm.lukka;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

/** Actividad principal de LUKKA Television. Base inicial de reproducción IPTV. */
public class MainActivity extends Activity {
    private ExoPlayer player;
    private PlayerView playerView;
    private EditText urlInput;
    private TextView status;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createUi();
    }

    private void createUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(3, 8, 18));

        TextView title = new TextView(this);
        title.setText("LUKKA  TELEVISION");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setPadding(12, 18, 12, 12);
        root.addView(title);

        playerView = new PlayerView(this);
        playerView.setUseController(true);
        root.addView(playerView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout controls = new LinearLayout(this);
        controls.setPadding(10, 8, 10, 8);
        urlInput = new EditText(this);
        urlInput.setSingleLine(true);
        urlInput.setTextColor(Color.WHITE);
        urlInput.setHintTextColor(Color.LTGRAY);
        urlInput.setHint("URL autorizada .m3u8 o .ts");
        controls.addView(urlInput, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Button play = new Button(this);
        play.setText("REPRODUCIR");
        play.setOnClickListener(v -> playUrl(urlInput.getText().toString().trim()));
        controls.addView(play);
        root.addView(controls);

        status = new TextView(this);
        status.setTextColor(Color.rgb(80, 180, 255));
        status.setText("Listo · HLS / MPEG-TS");
        status.setPadding(12, 4, 12, 12);
        root.addView(status);
        setContentView(root);
    }

    private void createPlayerIfNeeded() {
        if (player != null) return;
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(1500, 15000, 1500, 3000)
                .build();
        DefaultHttpDataSource.Factory http = new DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(20000);
        player = new ExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(this, http))
                .build();
        playerView.setPlayer(player);
        player.addListener(new Player.Listener() {
            @Override public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_BUFFERING) status.setText("Conectando / cargando búfer…");
                else if (state == Player.STATE_READY) status.setText("Reproduciendo");
                else if (state == Player.STATE_ENDED) status.setText("Transmisión finalizada");
            }
            @Override public void onPlayerError(PlaybackException error) {
                status.setText("Error: " + error.getErrorCodeName());
            }
        });
    }

    private void playUrl(String value) {
        if (value.isEmpty()) { status.setText("Introduce una URL .m3u8 o .ts"); return; }
        Uri uri = Uri.parse(value);
        String scheme = uri.getScheme();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            status.setText("La URL debe comenzar con http:// o https://");
            return;
        }
        createPlayerIfNeeded();
        status.setText("Conectando…");
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.play();
    }

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && player != null) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_CHANNEL_UP
                    || event.getKeyCode() == KeyEvent.KEYCODE_CHANNEL_DOWN) {
                status.setText("Control de canal: pendiente integrar lista M3U");
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override protected void onStop() {
        if (player != null) player.pause();
        super.onStop();
    }

    @Override protected void onDestroy() {
        if (player != null) { player.release(); player = null; }
        super.onDestroy();
    }
}
