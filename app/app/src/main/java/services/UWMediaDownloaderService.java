/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package services;

import android.content.Intent;
import android.os.IBinder;

import model.AudioBitrate;
import model.DaoDBHelper;
import model.daoModels.Book;
import model.daoModels.Version;
import model.parsers.MediaType;
import runnables.UpdateMediaRunnable;

/**
 * Created by PJ fechner
 * Service to download and add a Version to the DB
 */
public class UWMediaDownloaderService extends UWUpdaterService {

    public static final String STOP_DOWNLOAD_MEDIA_MESSAGE = "STOP_DOWNLOAD_MEDIA_MESSAGE";
    public static final String VERSION_PARAM = "VERSION_PARAM";
    public static final String IS_VIDEO_PARAM = "IS_VIDEO_PARAM";
    public static final String BITRATE_PARAM = "BITRATE_PARAM";

    private static final String TAG = "MediaDownloaderService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null && intent.getExtras() != null) {
            long versionId = intent.getExtras().getLong(VERSION_PARAM);
            boolean isVideo = intent.getExtras().getBoolean(IS_VIDEO_PARAM);
            AudioBitrate bitrate = (AudioBitrate) intent.getExtras().getSerializable(BITRATE_PARAM);
            Version version = Version.getVersionForId(versionId, DaoDBHelper.getDaoSession(getApplicationContext()));

            for (Book book : version.getBooks()) {
                addRunnable(new UpdateMediaRunnable(false, book, this, bitrate), version, MediaType.MEDIA_TYPE_AUDIO);
            }
        }

        return START_STICKY;
    }
}
