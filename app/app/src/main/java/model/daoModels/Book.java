package model.daoModels;

import java.util.List;
import model.daoModels.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import android.util.Log;
import java.util.Collections;
import model.UWDatabaseModel;
import model.parsers.BookParser;
import org.json.JSONException;
import org.json.JSONObject;
// KEEP INCLUDES END
/**
 * Entity mapped to table BOOK.
 */
public class Book extends model.UWDatabaseModel  implements java.io.Serializable {

    private Long id;
    private String slug;
    private String title;
    private String description;
    private String sourceUrl;
    private String signatureUrl;
    private java.util.Date modified;
    private long versionId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient BookDao myDao;

    private Version version;
    private Long version__resolvedKey;

    private List<Verification> verifications;
    private List<BibleChapter> bibleChapters;
    private List<StoriesChapter> storyChapters;

    // KEEP FIELDS - put your custom fields here
    static private final String TAG = "Version";
    // KEEP FIELDS END

    public Book() {
    }

    public Book(Long id) {
        this.id = id;
    }

    public Book(Long id, String slug, String title, String description, String sourceUrl, String signatureUrl, java.util.Date modified, long versionId) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.sourceUrl = sourceUrl;
        this.signatureUrl = signatureUrl;
        this.modified = modified;
        this.versionId = versionId;
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

    public List<StoriesChapter> getStoryChapters(boolean sorted){
        if(!sorted){
            return getStoryChapters();
        }
        else{
            StoriesChapterDao targetDao = daoSession.getStoriesChapterDao();
            List<StoriesChapter> storiesChaptersNew = getStoryChapters();
            Collections.sort(storiesChaptersNew);
            return storiesChaptersNew;
        }
    }


    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json) {
        return null;
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent) {
        try {
            return BookParser.parseBook(json, parent        );
    }
    catch (JSONException e){
        e.printStackTrace();
        return null;
    }
}
    public String getSlugIdentifier(){

        return slug.substring(slug.length() - 3);
    }

    static public Book getModelForSlug(String slug, DaoSession session){

        BookDao dao = session.getBookDao();
        Book model = dao.queryBuilder()
                .where(BookDao.Properties.Slug.eq(slug))
                .unique();

        return (model == null)? null : model;
    }

    public BibleChapter getBibleChapterForNumber(String number, DaoSession session){
        BibleChapterDao dao = session.getBibleChapterDao();
        BibleChapter model = dao.queryBuilder()
                .where(BibleChapterDao.Properties.BookId.eq(getId()), BibleChapterDao.Properties.Slug.eq(number))
                .unique();

        return model;
    }

    public StoriesChapter getStoriesChapterForNumber(String number, DaoSession session){

        StoriesChapterDao dao = session.getStoriesChapterDao();
        StoriesChapter model = dao.queryBuilder()
                .where(StoriesChapterDao.Properties.BookId.eq(getId()), StoriesChapterDao.Properties.Slug.eq(number))
                .unique();

        return model;
    }

    @Override
    public void insertModel(DaoSession session) {
        session.getBookDao().insert(this);
        this.refresh();
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {

        Book newBook = (Book) newModel;
        this.slug = newBook.slug;
        this.title = newBook.title;
        this.description = newBook.description;
        this.sourceUrl = newBook.sourceUrl;
        this.signatureUrl = newBook.signatureUrl;
        this.versionId = newBook.versionId;

        boolean wasUpdated = (newBook.modified.compareTo(this.modified) > 0);
        if (wasUpdated) {
            this.modified = newBook.modified;
            return true;
        } else {
            this.modified = newBook.modified;
            return false;
        }
    }
    // KEEP METHODS END

}
