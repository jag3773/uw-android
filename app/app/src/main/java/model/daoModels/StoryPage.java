package model.daoModels;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.dao.DaoException;
import model.UWDatabaseModel;
import model.parsers.StoryPageParser;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "STORY_PAGE".
 */
public class StoryPage extends model.UWDatabaseModel  implements java.io.Serializable {

    private Long id;
    private String uniqueSlug;
    private String slug;
    private String number;
    private String text;
    private String imageUrl;
    private long storyChapterId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient StoryPageDao myDao;

    private StoriesChapter storiesChapter;
    private Long storiesChapter__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public StoryPage() {
    }

    public StoryPage(Long id) {
        this.id = id;
    }

    public StoryPage(Long id, String uniqueSlug, String slug, String number, String text, String imageUrl, long storyChapterId) {
        this.id = id;
        this.uniqueSlug = uniqueSlug;
        this.slug = slug;
        this.number = number;
        this.text = text;
        this.imageUrl = imageUrl;
        this.storyChapterId = storyChapterId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStoryPageDao() : null;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getStoryChapterId() {
        return storyChapterId;
    }

    public void setStoryChapterId(long storyChapterId) {
        this.storyChapterId = storyChapterId;
    }

    /** To-one relationship, resolved on first access. */
    public StoriesChapter getStoriesChapter() {
        long __key = this.storyChapterId;
        if (storiesChapter__resolvedKey == null || !storiesChapter__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StoriesChapterDao targetDao = daoSession.getStoriesChapterDao();
            StoriesChapter storiesChapterNew = targetDao.load(__key);
            synchronized (this) {
                storiesChapter = storiesChapterNew;
            	storiesChapter__resolvedKey = __key;
            }
        }
        return storiesChapter;
    }

    public void setStoriesChapter(StoriesChapter storiesChapter) {
        if (storiesChapter == null) {
            throw new DaoException("To-one property 'storyChapterId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.storiesChapter = storiesChapter;
            storyChapterId = storiesChapter.getId();
            storiesChapter__resolvedKey = storyChapterId;
        }
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

    //region UWDatabaseModel

    /**
     * @param number number of this chapter's page that is desired
     * @return StoryPage with the passed Number
     */
    public StoryPage getStoryPageForNumber(String number){

        StoryPageDao dao = daoSession.getStoryPageDao();
        return dao.queryBuilder()
                .where(StoryPageDao.Properties.StoryChapterId.eq(getStoryChapterId()), StoryPageDao.Properties.Number.eq(number))
                .unique();
    }

    public StoryPage getNextStoryPage(){
        int newNumber = Integer.parseInt(getNumber()) + 1;
        String newNumberText = (newNumber < 10)? "0" : "";
        newNumberText += Integer.toString(newNumber);
        return getStoryPageForNumber(newNumberText);
    }

    public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent) {
        try {
            return StoryPageParser.parseStoryPage(json, parent);
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

    @Override
    public void insertModel(DaoSession session) {

        session.getStoryPageDao().insert(this);
        refresh();
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {

        StoryPage newPage = (StoryPage) newModel;

        this.uniqueSlug = newPage.uniqueSlug;
        this.number = newPage.number;
        this.text = newPage.text;
        this.imageUrl = newPage.imageUrl;
        this.storyChapterId = newPage.storyChapterId;
        update();

        return false;
    }

    //endRegion

    /**
     * @param uniqueSlug Slug that is unique to only one instance
     * @param session Session to use to find model
     * @return Unique StoryPage with the passed slug
     */
    static public StoryPage getModelForUniqueSlug(String uniqueSlug, DaoSession session){

        StoryPageDao dao = session.getStoryPageDao();
        return dao.queryBuilder()
                .where(StoryPageDao.Properties.UniqueSlug.eq(uniqueSlug))
                .unique();
    }

    @Override
    public String toString() {
        return "StoryPage{" +
                "imageUrl='" + imageUrl + '\'' +
                ", number='" + number + '\'' +
                ", slug='" + slug + '\'' +
                ", storiesChapter__resolvedKey=" + storiesChapter__resolvedKey +
                ", storyChapterId=" + storyChapterId +
                ", text='" + text + '\'' +
                ", uniqueSlug='" + uniqueSlug + '\'' +
                '}';
    }
    // KEEP METHODS END

}
