package model.daoModels;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.DaoException;
import model.UWDatabaseModel;
import model.parsers.BookParser;
import signing.Status;
import view.ViewContentHelper;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "BOOK".
 */
public class Book extends model.UWDatabaseModel  implements java.io.Serializable {

    private Long id;
    private String uniqueSlug;
    private String slug;
    private String title;
    private String description;
    private String sourceUrl;
    private String signatureUrl;
    private java.util.Date modified;
    private long versionId;
    private long audioBookId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient BookDao myDao;

    private Version version;
    private Long version__resolvedKey;

    private AudioBook audioBook;
    private Long audioBook__resolvedKey;

    private List<BibleChapter> bibleChapters;
    private List<StoriesChapter> storyChapters;
    private List<Verification> verifications;

    // KEEP FIELDS - put your custom fields here
    static private final String TAG = "Book";
    // KEEP FIELDS END

    public Book() {
    }

    public Book(Long id) {
        this.id = id;
    }

    public Book(Long id, String uniqueSlug, String slug, String title, String description, String sourceUrl, String signatureUrl, java.util.Date modified, long versionId, long audioBookId) {
        this.id = id;
        this.uniqueSlug = uniqueSlug;
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.sourceUrl = sourceUrl;
        this.signatureUrl = signatureUrl;
        this.modified = modified;
        this.versionId = versionId;
        this.audioBookId = audioBookId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBookDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueSlug() {
        return uniqueSlug;
    }

    public void setUniqueSlug(String uniqueSlug) {
        this.uniqueSlug = uniqueSlug;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public java.util.Date getModified() {
        return modified;
    }

    public void setModified(java.util.Date modified) {
        this.modified = modified;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public long getAudioBookId() {
        return audioBookId;
    }

    public void setAudioBookId(long audioBookId) {
        this.audioBookId = audioBookId;
    }

    /** To-one relationship, resolved on first access. */
    public Version getVersion() {
        long __key = this.versionId;
        if (version__resolvedKey == null || !version__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VersionDao targetDao = daoSession.getVersionDao();
            Version versionNew = targetDao.load(__key);
            synchronized (this) {
                version = versionNew;
            	version__resolvedKey = __key;
            }
        }
        return version;
    }

    public void setVersion(Version version) {
        if (version == null) {
            throw new DaoException("To-one property 'versionId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.version = version;
            versionId = version.getId();
            version__resolvedKey = versionId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public AudioBook getAudioBook() {
        long __key = this.audioBookId;
        if (audioBook__resolvedKey == null || !audioBook__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AudioBookDao targetDao = daoSession.getAudioBookDao();
            AudioBook audioBookNew = targetDao.load(__key);
            synchronized (this) {
                audioBook = audioBookNew;
            	audioBook__resolvedKey = __key;
            }
        }
        return audioBook;
    }

    public void setAudioBook(AudioBook audioBook) {
        if (audioBook == null) {
            throw new DaoException("To-one property 'audioBookId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.audioBook = audioBook;
            audioBookId = audioBook.getId();
            audioBook__resolvedKey = audioBookId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<BibleChapter> getBibleChapters() {
        if (bibleChapters == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BibleChapterDao targetDao = daoSession.getBibleChapterDao();
            List<BibleChapter> bibleChaptersNew = targetDao._queryBook_BibleChapters(id);
            synchronized (this) {
                if(bibleChapters == null) {
                    bibleChapters = bibleChaptersNew;
                }
            }
        }
        return bibleChapters;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetBibleChapters() {
        bibleChapters = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<StoriesChapter> getStoryChapters() {
        if (storyChapters == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StoriesChapterDao targetDao = daoSession.getStoriesChapterDao();
            List<StoriesChapter> storyChaptersNew = targetDao._queryBook_StoryChapters(id);
            synchronized (this) {
                if(storyChapters == null) {
                    storyChapters = storyChaptersNew;
                }
            }
        }
        return storyChapters;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetStoryChapters() {
        storyChapters = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Verification> getVerifications() {
        if (verifications == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VerificationDao targetDao = daoSession.getVerificationDao();
            List<Verification> verificationsNew = targetDao._queryBook_Verifications(id);
            synchronized (this) {
                if(verifications == null) {
                    verifications = verificationsNew;
                }
            }
        }
        return verifications;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetVerifications() {
        verifications = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here


    public boolean isDownloaded(){

        return ((getStoryChapters() != null && getStoryChapters().size() > 0) || (getBibleChapters() != null && getBibleChapters().size() > 0));
    }

    @Override
    public void insertModel(DaoSession session) {
        session.getBookDao().insert(this);
        this.refresh();
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {

        Book newBook = (Book) newModel;
        this.uniqueSlug = newBook.uniqueSlug;
        this.title = newBook.title;
        this.description = newBook.description;
        this.sourceUrl = newBook.sourceUrl;
        this.signatureUrl = newBook.signatureUrl;
        this.versionId = newBook.versionId;

        boolean wasUpdated = (newBook.modified.compareTo(this.modified) > 0);
        this.modified = newBook.modified;
        update();
        return wasUpdated;
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json) {
        return null;
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent) {
        try {
            return BookParser.parseBook(json, parent);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    //endregion

    /**
     * Deletes all data related to the sources of this book
     */
    public void deleteBookContent(Context context){

        for(Verification verification : getVerifications()){
            verification.delete();
        }

        if(getStoryChapters() != null){
            for(StoriesChapter chapter : storyChapters){
                chapter.delete();
            }
        }
        if(getBibleChapters() !=null){
            for(BibleChapter chapter : bibleChapters){
                chapter.delete();
            }
        }
    }

    /**
     * @return The book following this book, or null if there is none.
     */
    @Nullable
    public Book getNextBook(){
        return this.getVersion().getNextBook(this);
    }

    /**
     * @return the verification status of this book
     */
    public int getVerificationStatus(){
        refresh();
        update();
        if(getVerifications() == null || getVerifications().size() < 1){
            return 2;
        }

        int status = 0;
        for(Verification verification : getVerifications()){
            if(verification.getStatus() > status){
                status = verification.getStatus();
            }
        }
        return status;
    }

    /**
     * @return verification text to display for this book
     */
    public String getVerificationText(){

        int status = getVerificationStatus();

        return ViewContentHelper.getTextForStatus(Status.statusFromInt(status), title);
    }

    /**
     * @param sorted true if the chapters should be sorted
     * @return this book's BibleChapters with the option of being sorted
     */
    public List<BibleChapter> getBibleChapters(boolean sorted){
        if(!sorted){
            return getBibleChapters();
        }
        else{
            List<BibleChapter> bibleChaptersNew = getBibleChapters();
            Collections.sort(bibleChaptersNew);
            return bibleChaptersNew;
        }
    }

//    /**
//     * @param sorted true if the chapters should be sorted
//     * @return this book's StoryChapters with the option of being sorted
//     */
//    public List<StoriesChapter> getStoryChapters(boolean sorted){
//        if(!sorted){
//            return getStoryChapters();
//        }
//        else{
//            List<StoriesChapter> storiesChaptersNew = getStoryChapters();
//            Collections.sort(storiesChaptersNew);
//            return storiesChaptersNew;
//        }
//    }

    /**
     * @param uniqueSlug Slug that is unique to only one model
     * @param session Session to use
     * @return Unique Model with the passed slug
     */
    static public Book getModelForUniqueSlug(String uniqueSlug, DaoSession session){

        BookDao dao = session.getBookDao();
        return dao.queryBuilder()
                .where(BookDao.Properties.UniqueSlug.eq(uniqueSlug))
                .unique();
    }

    /**
     * @param number number of the chapter you want
     * @return chapter of this book with the passed number
     */
    public BibleChapter getBibleChapterForNumber(String number){
        BibleChapterDao dao = daoSession.getBibleChapterDao();
        return dao.queryBuilder()
                .where(BibleChapterDao.Properties.BookId.eq(getId()), BibleChapterDao.Properties.Slug.eq(number))
                .unique();
    }

    /**
     * @param number number of the chapter you want
     * @return chapter of this book with the passed number
     */
    public StoriesChapter getStoriesChapterForNumber(String number){

        StoriesChapterDao dao = daoSession.getStoriesChapterDao();
        return dao.queryBuilder()
                .where(StoriesChapterDao.Properties.BookId.eq(getId()), StoriesChapterDao.Properties.Slug.eq(number))
                .unique();
    }

    //region UWDatabaseModel

    public void deleteAudio(Context context){

        getVersion().deleteAudio(context);
//        for(AudioChapter chapter : getAudioBook().getAudioChapters()){
//            Log.i(TAG, "Deleting source: " + chapter.getSource());
//            UWFileUtils.deleteSource(chapter.getSource(), context);
//        }
//        setAudioSaveState(DownloadState.DOWNLOAD_STATE_NONE.ordinal());
//        update();
    }
    @Override
    public String toString() {
        return "Book{" +
                "description='" + description + '\'' +
                ", id=" + id +
                ", modified=" + modified +
                ", signatureUrl='" + signatureUrl + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", slug='" + slug + '\'' +
                ", title='" + title + '\'' +
                ", uniqueSlug='" + uniqueSlug + '\'' +
                ", versionId=" + versionId +
                ", version__resolvedKey=" + version__resolvedKey +
                '}';
    }
    // KEEP METHODS END

}
