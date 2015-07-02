package services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tasks.JsonDownloadTask;
import tasks.UpdateLanguageLocaleRunnable;
import tasks.UpdateProjectsRunnable;
import utils.URLUtils;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UWUpdater extends Service {

    private static final String TAG = "UpdateService";

    public static final String BROAD_CAST_DOWN_COMP = "org.unfoldingword.mobile.DOWNLOAD_COMPLETED";

    private Looper mServiceLooper;
    private Handler mServiceHandler;

    int numberRunning = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private UWUpdater getThis(){
        return this;
    }
    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("DataDownloadServiceThread", 2);

        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        super.onCreate();
    }

    public void addRunnable(Runnable runnable){

        numberRunning++;
        mServiceHandler.post(runnable);
    }

    public void runnableFinished(){

        numberRunning--;
        if(numberRunning == 0){
            stopService();
        }
    }

    private void stopService(){
        getApplicationContext().sendBroadcast(new Intent(URLUtils.BROAD_CAST_DOWN_COMP));
        this.stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        addRunnable(new UpdateRunnable());
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    class UpdateRunnable implements Runnable {

        @Override
        public void run() {

            new JsonDownloadTask(new JsonDownloadTask.DownloadTaskListener() {
                @Override
                public void downloadFinishedWithJson(String jsonString) {

                    try{
                        JSONObject json = new JSONObject(jsonString);
                        long lastModified = json.getLong("mod");

                        long currentUpdated = UWPreferenceManager.getLastUpdatedDate(getApplicationContext());
                        if(true){//lastModified > currentUpdated) {
                            UWPreferenceManager.setLastUpdatedDate(getApplicationContext(), lastModified);
                            addRunnable(new UpdateProjectsRunnable(json.getJSONArray("cat"), getThis()));
                        }
                        runnableFinished();
                    } catch (JSONException e){
                        e.printStackTrace();
                        runnableFinished();
                    }
                }
            }).execute(UWPreferenceManager.getDataDownloadUrl(getApplicationContext()));

            new JsonDownloadTask(new JsonDownloadTask.DownloadTaskListener() {
                @Override
                public void downloadFinishedWithJson(String jsonString) {

                    try{
//                        JSONObject jsonObj = new JSONObject(jsonString);
                        JSONArray locales = new JSONArray(jsonString);
                        addRunnable(new UpdateLanguageLocaleRunnable(locales, getThis()));

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }).execute(UWPreferenceManager.getLanguagesDownloadUrl(getApplicationContext()));

        }
    }
}