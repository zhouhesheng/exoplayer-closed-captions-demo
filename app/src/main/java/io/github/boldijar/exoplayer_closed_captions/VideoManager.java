package io.github.boldijar.exoplayer_closed_captions;

import android.util.Log;

import androidx.annotation.OptIn;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.Player;
import androidx.media3.common.TrackGroup;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;

public class VideoManager {

    @OptIn(markerClass = UnstableApi.class)
    public static void logExoPlayerTrack(Player mExoPlayer) {
        if (mExoPlayer == null)
            return;

        Tracks exoTracks = mExoPlayer.getCurrentTracks();
        int groupIdx = 0;
        for (Tracks.Group groupInfo : exoTracks.getGroups()) {
            @C.TrackType int trackType = groupInfo.getType();
            Log.d("LOG", "groupInfo trackType: " + trackType + " groupId: " + groupInfo.getMediaTrackGroup().id);

            TrackGroup group = groupInfo.getMediaTrackGroup();
            for (int i = 0; i < group.length; i++) {
                // Individual track information.
                Format trackFormat = group.getFormat(i);
                Log.d("LOG", String.format("trackId:%s group %s:%s trackType %s label `%s` mime %s isSelected %s isSupported %s",
                        trackFormat.id, groupIdx, i, trackType, trackFormat.label, trackFormat.sampleMimeType, groupInfo.isTrackSelected(i), groupInfo.isTrackSupported(i)));
            }

            groupIdx++;
        }
    }
}
