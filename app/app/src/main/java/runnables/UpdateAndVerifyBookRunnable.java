/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package runnables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import model.daoModels.Book;
import model.parsers.MediaType;
import services.UWUpdaterService;
import signing.UWSigning;

/**
 * Created by PJ Fechner on 6/17/15.
 * Runnable which updates a Book
 */
public class UpdateAndVerifyBookRunnable implements Runnable{

    private static final String TAG = "UpdateVerificationRunnable";
    public static final String CHAPTERS_JSON_KEY = "chapters";
    private UWUpdaterService updater;
    private Book book;
    byte[] bookText;
    String sigText;

    public UpdateAndVerifyBookRunnable(Book book, UWUpdaterService updater, byte[] bookText, String sigText) {
        this.book = book;
        this.updater = updater;
        this.bookText = bookText;
        this.sigText = sigText;
    }


    @Override
    public void run() {
        updateVerification();
    }

    private void updateVerification(){

        try {
            UWSigning.updateBookVerification(updater.getApplicationContext(), book, bookText, sigText);
            parseText(bookText);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private void parseText(byte[] text){
        if (text != null) {

            if(book.getSourceUrl().contains("usfm")){
                UpdateBibleChaptersRunnable runnable = new UpdateBibleChaptersRunnable(
                        text, updater, book);
                updater.addRunnable(runnable, book.getVersion(), MediaType.MEDIA_TYPE_TEXT);
                updater.runnableFinished(book.getVersion(), MediaType.MEDIA_TYPE_TEXT);
            }
            else{
                try {
                    UpdateStoriesChaptersRunnable runnable = new UpdateStoriesChaptersRunnable(
                            new JSONObject(new String(text)).getJSONArray(UpdateBookContentRunnable.CHAPTERS_JSON_KEY), updater, book);
                    updater.addRunnable(runnable, book.getVersion(), MediaType.MEDIA_TYPE_TEXT);
                    updater.runnableFinished(book.getVersion(), MediaType.MEDIA_TYPE_TEXT);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }


        }
    }

}
