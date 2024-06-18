package io.github.boldijar.exoplayer_closed_captions

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView


class MainActivity : AppCompatActivity(), Player.Listener,
    AdapterView.OnItemClickListener {

    companion object {
        const val STREAM = " http://x1.xj12.tv:8096/videos/1456828/original.mkv?DeviceId=e612a1232339da74&MediaSourceId=64c543cf44410e29b71a3f8a5c8efed2&PlaySessionId=bd881dbc1d674f7db0125de8a557ef8d&api_key=b1005f7843304882ab9e019385fea977"
        // const val STREAM = "http://x1.xj12.tv:8096/emby/videos/1453516/master.m3u8?DeviceId=e612a1232339da74&MediaSourceId=ec136c06477e60c0ec2d40f5c5c59730&PlaySessionId=12860baa433e4318984b8a695aba5361&api_key=b1005f7843304882ab9e019385fea977&VideoCodec=h264,mpeg2video,hevc&AudioCodec=ac3,aac,mp3&VideoBitrate=616001&AudioBitrate=384000&AudioStreamIndex=1&SubtitleStreamIndex=2&SubtitleMethod=Hls&TranscodingMaxAudioChannels=6&SegmentContainer=m4s,ts&SegmentLength=3&MinSegments=1&BreakOnNonKeyFrames=True&SubtitleStreamIndexes=2,3,4,5,6,7&ManifestSubtitles=vtt&hevc-profile=Main,Main10,Rext&h264-level=51&TranscodeReasons=ContainerBitrateExceedsLimit"
    }

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var languagesListView: ListView

    private var languages: List<Language> = mutableListOf()
    private var selectedLanguageCode: String? = null

    private fun prepare() {
        playerView.player = null
        playerView.player = player
        playerView.keepScreenOn = true
        player.setMediaItem(MediaItem.fromUri(Uri.parse(STREAM)))
        player.playWhenReady = true
        player.prepare()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.exo_player)
        languagesListView = findViewById(R.id.list_view)
        languagesListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        languagesListView.onItemClickListener = this
        player.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        player.release()
    }

    override fun onResume() {
        super.onResume()
        prepare()
    }

    private fun refreshLanguages() {
        languages = ExoUtils.getLanguagesFromTrackInfo(player.currentTracks)
        val listViewItems =
            languages.map { it.name }
                .toTypedArray()
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_single_choice,
                listViewItems
            )
        languagesListView.adapter = adapter
        val selectedIndex = languages.indexOfFirst { it.code == selectedLanguageCode }
        languagesListView.setItemChecked(selectedIndex, true)
    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
        selectedLanguageCode = player.trackSelectionParameters.preferredTextLanguages.firstOrNull()
        refreshLanguages()

        VideoManager.logExoPlayerTrack(player)
    }


    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val selection = languages.getOrNull(p2)
        selectedLanguageCode = if (selection?.code == selectedLanguageCode) {
            null
        } else {
            selection?.code
        }
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setPreferredTextLanguage(selectedLanguageCode)
            .build()
        refreshLanguages()
    }


}