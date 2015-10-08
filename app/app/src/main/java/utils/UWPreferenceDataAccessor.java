package utils;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.StoryPage;

/**
 * Created by Fechner on 8/14/15.
 */
public class UWPreferenceDataAccessor {

    private static UWPreferenceDataAccessor ourInstance = new UWPreferenceDataAccessor();
    public static UWPreferenceDataAccessor getOurInstance(){
        return ourInstance;
    }

    private BibleChapter currentChapter;
    private BibleChapter currentSecondChapter;

    private StoryPage currentPage;
    private StoryPage currentSecondPage;

    private List<PreferencesStoryPageChangedListener> storyPageChangedListeners = new ArrayList<>();
    private List<PreferencesBibleChapterChangedListener> bibleChapterChangedListeners = new ArrayList<>();

    public static void addStoryPageListener(PreferencesStoryPageChangedListener listener){
        ourInstance.storyPageChangedListeners.add(listener);
    }
    public static void addBibleChapterListener(PreferencesBibleChapterChangedListener listener){
        ourInstance.bibleChapterChangedListeners.add(listener);
    }

    public static void removeStoryPageListener(PreferencesStoryPageChangedListener listener){
        ourInstance.storyPageChangedListeners.remove(listener);
    }
    public static void removeBibleChapterListener(PreferencesBibleChapterChangedListener listener){
        ourInstance.bibleChapterChangedListeners.remove(listener);
    }

    public static void changedToNewStoriesPage(Context context, StoryPage page, boolean isSecond){
        UWPreferenceDataManager.setNewStoriesPage(context, page, isSecond);
        ourInstance.updateStoryPages(context);
    }

    public static void changeToNewBibleChapter(Context context, BibleChapter chapter, boolean isSecond){
        UWPreferenceDataManager.changedToBibleChapter(context, chapter.getId(), isSecond);
        ourInstance.updateBibleChapters(context);
    }

    private void updateStoryPages(Context context){
        boolean mainDidChange = !storyPageIsUpToDate(context, false);
        boolean secondDidChange = !storyPageIsUpToDate(context, true);

        if(mainDidChange){
            getCurrentStoryPage(context, false);
        }
        if(secondDidChange){
            getCurrentStoryPage(context, true);
        }

        if(mainDidChange || secondDidChange){
            updateStoryListeners();
        }
    }

    public void updateStoryListeners(){

        currentPage.refresh();
        currentSecondPage.refresh();
        for(PreferencesStoryPageChangedListener listener : storyPageChangedListeners){
            if(listener != null){
                listener.storyPageChanged(currentPage, currentSecondPage);
            }
        }
    }

    private void updateBibleChapters(Context context){
        boolean mainDidChange = !bibleChapterIsUpToDate(context, false);
        boolean secondDidChange = !bibleChapterIsUpToDate(context, true);

        if(mainDidChange){
            getCurrentBibleChapter(context, false);
        }
        if(secondDidChange){
            getCurrentBibleChapter(context, true);
        }

        if(mainDidChange || secondDidChange){
            updateChapterListeners();
        }
    }

    private void updateChapterListeners(){

        for(PreferencesBibleChapterChangedListener listener : bibleChapterChangedListeners){
            if(listener != null){
                listener.bibleChapterChanged(currentChapter, currentSecondChapter);
            }
        }
    }

    /**
     * @param context Context to use
     * @param second true of this is for the second version in the diglot view
     * @return the currently chosen chapter
     */
    @Nullable
    synchronized public static BibleChapter getCurrentBibleChapter(Context context, boolean second){
        return getInstance().getBibleChapter(context, second);
    }

    /**
     * @param context Context to use
     * @param second true of this is for the second version in the diglot view
     * @return The currently chosen StoryPage
     */
    @Nullable
    synchronized public static StoryPage getCurrentStoryPage(Context context, boolean second){
        return getInstance().getStoryPage(context, second);
    }

    private static UWPreferenceDataAccessor getInstance() {
        return ourInstance;
    }

    private UWPreferenceDataAccessor() {
    }

    @Nullable
    private BibleChapter getBibleChapter(Context context, boolean second){

        return (second)? getCurrentSecondChapter(context) : getCurrentChapter(context);
    }

    /**
     * Checks if cached chapter is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private BibleChapter getCurrentChapter(Context context){

        long chapterId = UWPreferenceManager.getCurrentBibleChapter(context, false);
        if(currentChapter == null || chapterId != currentChapter.getId()){
            currentChapter = loadChapterFromDB(context, chapterId);
        }
        else if(chapterId < 0){
            currentChapter = null;
        }
        return currentChapter;
    }

    /**
     * Checks if cached chapter is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private BibleChapter getCurrentSecondChapter(Context context) {

        long chapterId = UWPreferenceManager.getCurrentBibleChapter(context, true);
        if(currentSecondChapter == null || chapterId != currentSecondChapter.getId()){
            currentSecondChapter = loadChapterFromDB(context, chapterId);
        }
        else if(chapterId < 0){
            currentSecondChapter = null;
        }
        return currentSecondChapter;
    }

    synchronized private boolean bibleChapterIsUpToDate(Context context, boolean isSecond){
        if(isSecond) {
            long chapterId = UWPreferenceManager.getCurrentBibleChapter(context, true);
            return chapterId < 0 || (currentSecondChapter != null && chapterId == currentSecondChapter.getId());
        }
        else{
            long chapterId = UWPreferenceManager.getCurrentBibleChapter(context, false);
            return chapterId < 0 || (currentChapter != null && chapterId == currentChapter.getId());
        }
    }

    synchronized private boolean storyPageIsUpToDate(Context context, boolean isSecond){
        if(isSecond) {
            long chapterId = UWPreferenceManager.getCurrentStoryPage(context, true);
            return chapterId < 0 || (currentSecondPage != null && chapterId == currentSecondPage.getId());
        }
        else{
            long chapterId = UWPreferenceManager.getCurrentStoryPage(context, false);
            return chapterId < 0 || (currentPage != null && chapterId == currentPage.getId());
        }
    }

    @Nullable
    private BibleChapter loadChapterFromDB(Context context, long id){
        if(id < 0){
            return null;
        }
        return BibleChapter.getModelForId(id, DaoDBHelper.getDaoSession(context));
    }

    @Nullable
    private StoryPage getStoryPage(Context context, boolean second){

        return (second)? getCurrentSecondPage(context) : getCurrentPage(context);
    }

    /**
     * Checks if cached page is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private StoryPage getCurrentPage(Context context){

        long chapterId = UWPreferenceManager.getCurrentStoryPage(context, false);
        if(currentPage == null || chapterId != currentPage.getId()){
            currentPage = loadPageFromDB(context, chapterId);
        }
        return currentPage;
    }

    /**
     * Checks if cached page is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private StoryPage getCurrentSecondPage(Context context) {

        long chapterId = UWPreferenceManager.getCurrentStoryPage(context, true);
        if(currentSecondPage == null || chapterId != currentSecondPage.getId()){
            currentSecondPage = loadPageFromDB(context, chapterId);
        }
        return currentSecondPage;
    }

    @Nullable
    private StoryPage loadPageFromDB(Context context, long id){
        if(id < 0){
            return null;
        }
        return DaoDBHelper.getDaoSession(context).getStoryPageDao().loadDeep(id);
    }

    public interface PreferencesStoryPageChangedListener{
        void storyPageChanged(StoryPage mainPage, StoryPage secondaryPage);
    }

    public interface PreferencesBibleChapterChangedListener{
        void bibleChapterChanged(BibleChapter mainChapter, BibleChapter secondaryChapter);
    }
}
