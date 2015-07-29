package utils;

import android.content.Context;
import android.preference.PreferenceManager;

import org.unfoldingword.mobile.R;

import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;

/**
 * Created by Fechner on 3/25/15.
 */
public class UWPreferenceManager {

//    private static final String STORY_VERSION_ID = "selected_story_version_id";
//    public static long getSelectedStoryVersion(Context context){
//        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(STORY_VERSION_ID, -1);
//    }
//    public static void setSelectedStoryVersion(Context context, long newValue){
//        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(STORY_VERSION_ID, newValue).commit();
//    }

//    private static final String BIBLE_VERSION_ID = "selected_bible_version_id";
//    public static long getSelectedBibleVersion(Context context){
//        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(BIBLE_VERSION_ID, -1);
//    }
//    public static void setSelectedBibleVersion(Context context, long newValue){
//
//        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(BIBLE_VERSION_ID, newValue).commit();
//    }

    public static void setNewBibleVersion(Context context, Version version){

        long currentId = getSelectedBibleChapter(context);
        BibleChapter requestedChapter = null;
        if(currentId > -1){
            BibleChapter currentChapter = BibleChapter.getModelForId(currentId, DaoDBHelper.getDaoSession(context));
            Book newBook = version.getBookForBookSlug(currentChapter.getBook().getSlugIdentifier(), DaoDBHelper.getDaoSession(context));
            if(newBook != null){
                requestedChapter = newBook.getBibleChapterForNumber(currentChapter.getNumber(), DaoDBHelper.getDaoSession(context));
            }
        }
        if(requestedChapter == null){
            requestedChapter = version.getBooks().get(0).getBibleChapters(true).get(0);
        }

        setSelectedBibleChapter(context, requestedChapter.getId());
    }

    public static void setNewStoriesVersion(Context context, Version version){

        long currentId = getSelectedStoryPage(context);
        StoryPage requestedPage = null;
        if(currentId > -1){

            StoryPage currentPage = DaoDBHelper.getDaoSession(context).getStoryPageDao().loadDeep(currentId);
            Book newBook = version.getBookForBookSlug(currentPage.getStoriesChapter().getBook().getSlugIdentifier(), DaoDBHelper.getDaoSession(context));
            if(newBook != null){
                StoriesChapter requestedChapter = newBook.getStoriesChapterForNumber(currentPage.getStoriesChapter().getNumber(), DaoDBHelper.getDaoSession(context));
                if(requestedChapter != null){
                    requestedPage = requestedChapter.getStoriesChapterForNumber(currentPage.getNumber(), DaoDBHelper.getDaoSession(context));
                }
            }
        }
        if(requestedPage == null){
            requestedPage = version.getBooks().get(0).getStoryChapters(true).get(0).getStoryPages().get(0);
        }

        setSelectedStoryPage(context, requestedPage.getId());
    }


    public static void willDeleteVersion(Context context, Version version){
        willDeleteStoryVersion(context, version);
        willDeleteBibleVersion(context, version);
    }

    private static void willDeleteStoryVersion(Context context, Version version){

        long currentId = getSelectedStoryPage(context);

        if(currentId > -1) {
            StoryPage currentPage = DaoDBHelper.getDaoSession(context).getStoryPageDao().loadDeep(currentId);
            if(currentPage.getStoriesChapter().getBook().getVersionId() == version.getId()){
                setSelectedStoryPage(context, -1);
            }
        }
    }

    private static void willDeleteBibleVersion(Context context, Version version){

        long currentId = getSelectedBibleChapter(context);

        if(currentId > -1) {
            BibleChapter currentChapter = BibleChapter.getModelForId(currentId, DaoDBHelper.getDaoSession(context));
            if(currentChapter.getBook().getVersionId() == version.getId()){
                setSelectedStoryPage(context, -1);
            }
        }
    }

    private static final String BIBLE_CHAPTER_ID = "selected_bible_chapter_id";
    public static long getSelectedBibleChapter(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(BIBLE_CHAPTER_ID, -1);
    }
    public static void setSelectedBibleChapter(Context context, long newValue){

        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(BIBLE_CHAPTER_ID, newValue).commit();
    }

    private static final String STORY_PAGE = "selected_story_page_id";
    public static long getSelectedStoryPage(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(STORY_PAGE, -1);
    }
    public static void setSelectedStoryPage(Context context, long newValue){

        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(STORY_PAGE, newValue).commit();
    }

    private static final String LAST_UPDATED_ID = "last_updated_date";
    public static Long getLastUpdatedDate(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_UPDATED_ID, -1);
    }
    public static void setLastUpdatedDate(Context context, long newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(LAST_UPDATED_ID, newValue).commit();
    }

    private static final String HAS_DOWNLOADED_LOCALES_ID = "LAST_LOCALE_UPDATED_ID";
    public static boolean getHasDownloadedLocales(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getBoolean(HAS_DOWNLOADED_LOCALES_ID, false);
    }
    public static void setHasDownloadedLocales(Context context, boolean newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(HAS_DOWNLOADED_LOCALES_ID, newValue).commit();
    }

    private static final String DATA_DOWNLOAD_URL_KEY = "base_url";
    public static String getDataDownloadUrl(Context context){
       return PreferenceManager.getDefaultSharedPreferences(context).getString(DATA_DOWNLOAD_URL_KEY, context.getResources().getString(R.string.pref_default_base_url));
    }
    public static void setDataDownloadUrl(Context context, String newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putString(DATA_DOWNLOAD_URL_KEY, newValue).commit();
    }

    private static final String LANGUAGES_DOWNLOAD_URL_KEY = "languages_json_url";
    public static String getLanguagesDownloadUrl(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(LANGUAGES_DOWNLOAD_URL_KEY,  context.getResources().getString(R.string.languages_json_url));
    }

}
