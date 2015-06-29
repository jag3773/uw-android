package model.daoModels;

import java.util.List;
import model.daoModels.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import model.UWDatabaseModel;
import model.parsers.VersionParser;
// KEEP INCLUDES END
/**
 * Entity mapped to table VERSION.
 */
public class Version extends model.UWDatabaseModel  implements java.io.Serializable {

    private Long id;
    private String slug;
    private String name;
    private String statusCheckingEntity;
    private String statusCheckingLevel;
    private String statusComments;
    private String statusContributors;
    private String statusPublishDate;
    private String statusSourceText;
    private String statusSourceTextVersion;
    private String statusVersion;
    private Integer saveState;
    private java.util.Date modified;
    private long languageId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient VersionDao myDao;

    private Language language;
    private Long language__resolvedKey;

    private List<Book> books;

    // KEEP FIELDS - put your custom fields here
    static private final String TAG = "Version";
    // KEEP FIELDS END

    public Version() {
    }

    public Version(Long id) {
        this.id = id;
    }

    public Version(Long id, String slug, String name, String statusCheckingEntity, String statusCheckingLevel, String statusComments, String statusContributors, String statusPublishDate, String statusSourceText, String statusSourceTextVersion, String statusVersion, Integer saveState, java.util.Date modified, long languageId) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.statusCheckingEntity = statusCheckingEntity;
        this.statusCheckingLevel = statusCheckingLevel;
        this.statusComments = statusComments;
        this.statusContributors = statusContributors;
        this.statusPublishDate = statusPublishDate;
        this.statusSourceText = statusSourceText;
        this.statusSourceTextVersion = statusSourceTextVersion;
        this.statusVersion = statusVersion;
        this.saveState = saveState;
        this.modified = modified;
        this.languageId = languageId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getVersionDao() : null;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatusCheckingEntity() {
        return statusCheckingEntity;
    }

    public void setStatusCheckingEntity(String statusCheckingEntity) {
        this.statusCheckingEntity = statusCheckingEntity;
    }

    public String getStatusCheckingLevel() {
        return statusCheckingLevel;
    }

    public void setStatusCheckingLevel(String statusCheckingLevel) {
        this.statusCheckingLevel = statusCheckingLevel;
    }

    public String getStatusComments() {
        return statusComments;
    }

    public void setStatusComments(String statusComments) {
        this.statusComments = statusComments;
    }

    public String getStatusContributors() {
        return statusContributors;
    }

    public void setStatusContributors(String statusContributors) {
        this.statusContributors = statusContributors;
    }

    public String getStatusPublishDate() {
        return statusPublishDate;
    }

    public void setStatusPublishDate(String statusPublishDate) {
        this.statusPublishDate = statusPublishDate;
    }

    public String getStatusSourceText() {
        return statusSourceText;
    }

    public void setStatusSourceText(String statusSourceText) {
        this.statusSourceText = statusSourceText;
    }

    public String getStatusSourceTextVersion() {
        return statusSourceTextVersion;
    }

    public void setStatusSourceTextVersion(String statusSourceTextVersion) {
        this.statusSourceTextVersion = statusSourceTextVersion;
    }

    public String getStatusVersion() {
        return statusVersion;
    }

    public void setStatusVersion(String statusVersion) {
        this.statusVersion = statusVersion;
    }

    public Integer getSaveState() {
        return saveState;
    }

    public void setSaveState(Integer saveState) {
        this.saveState = saveState;
    }

    public java.util.Date getModified() {
        return modified;
    }

    public void setModified(java.util.Date modified) {
        this.modified = modified;
    }

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }

    /** To-one relationship, resolved on first access. */
    public Language getLanguage() {
        long __key = this.languageId;
        if (language__resolvedKey == null || !language__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LanguageDao targetDao = daoSession.getLanguageDao();
            Language languageNew = targetDao.load(__key);
            synchronized (this) {
                language = languageNew;
            	language__resolvedKey = __key;
            }
        }
        return language;
    }

    public void setLanguage(Language language) {
        if (language == null) {
            throw new DaoException("To-one property 'languageId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.language = language;
            languageId = language.getId();
            language__resolvedKey = languageId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Book> getBooks() {
        if (books == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookDao targetDao = daoSession.getBookDao();
            List<Book> booksNew = targetDao._queryVersion_Books(id);
            synchronized (this) {
                if(books == null) {
                    books = booksNew;
                }
            }
        }
        return books;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetBooks() {
        books = null;
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


    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent) {
        try {
            return VersionParser.parseVersion(json, parent);
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json) {
        return null;
    }

    static public Version getModelForSlug(String slug, DaoSession session){
        VersionDao dao = session.getVersionDao();
        Version model = dao.queryBuilder()
                .where(VersionDao.Properties.Slug.eq(slug))
                .unique();

        return (model == null)? null : model;
    }

    @Override
    public void insertModel(DaoSession session) {

        session.getVersionDao().insert(this);
        this.refresh();
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {

        Version newVersion = (Version) newModel;

        this.slug = newVersion.slug;
        this.name = newVersion.name;
        this.statusCheckingEntity = newVersion.statusCheckingEntity;
        this.statusCheckingLevel = newVersion.statusCheckingLevel;
        this.statusComments = newVersion.statusComments;
        this.statusContributors = newVersion.statusContributors;
        this.statusPublishDate = newVersion.statusPublishDate;
        this.statusSourceText = newVersion.statusSourceText;
        this.statusSourceTextVersion = newVersion.statusSourceTextVersion;
        this.statusVersion = newVersion.statusVersion;
        this.saveState = newVersion.saveState;
        this.languageId = newVersion.languageId;

        boolean wasUpdated = (newVersion.modified.compareTo(this.modified) > 0);
        if(wasUpdated){
            this.modified = newVersion.modified;
            return true;
        }
        else{
            this.modified = newVersion.modified;
            return true;
        }
    }

    static public Version getVersionForId(DaoSession session, long id){

        return session.getVersionDao().queryBuilder()
                .where(VersionDao.Properties.Id.eq(id))
                .unique();
    }
    // KEEP METHODS END

}
