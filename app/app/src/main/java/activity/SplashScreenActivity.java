package activity;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import java.io.IOException;

import model.db.DBManager;
import utils.URLUtils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class SplashScreenActivity extends Activity {

    private static String TAG = "SplashScreenActivity";

    DBManager dbManager = null;
    AsyncTask<String, Void, String> execute = null;
    boolean cancelValue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setUI();
    }

    /**
     * Default Initialization of components
     */
    private void setUI() {
        dbManager = DBManager.getInstance(this);

        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        execute = new GetLanguageListAndFrames().execute(URLUtils.LANGUAGE_INFO, URLUtils.CHAPTER_INFO);
    }

    @Override
    protected void onPause() {
        if (execute != null) {
            cancelValue = false;
            execute.cancel(true);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (execute != null) {
            cancelValue = false;
            execute.cancel(true);
        }
        super.onBackPressed();
    }

    private class GetLanguageListAndFrames extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                dbManager.createDataBase(false);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            cancelValue = true;
            return URLUtils.TRUE;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            if (cancelValue) {
                if (result.equals(URLUtils.TRUE)) {
                    startActivity(new Intent(SplashScreenActivity.this, LanguageChooserActivity.class));
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            } else {
                finish();
            }

        }
    }
}
