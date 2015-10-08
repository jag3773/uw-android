package model.daoModels;

import java.util.List;
import model.daoModels.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import model.UWDatabaseModel;
import model.parsers.StoriesChapterParser;
// KEEP INCLUDES END
/**
 * Entity mapped to table "STORIES_CHAPTER".
 */
public class StoriesChapter extends model.UWDatabaseModel  implements java.io.Serializable, Comparable<StoriesChapter> {

    private Long id;
    private String uniqueSlug;
    private String slug;
    private String number;
    private String title;
    private String ref;
    private long bookId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient StoriesChapterDao myDao;

    private Book book;
    private Long book__resolvedKey;

    private List<StoryPage> storyPages;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public StoriesChapter() {
    }

    public StoriesChapter(Long id) {
        this.id = id;
    }

    public StoriesChapter(Long id, String uniqueSlug, String slug, String number, String title, String ref, long bookId) {
        this.id = id;
        this.uniqueSlug = uniqueSlug;
        this.slug = slug;
        this.number = number;
        this.title = title;
        this.ref = ref;
        this.bookId = bookId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStoriesChapterDao() : null;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    /** To-one relationship, resolved on first access. */
    public Book getBook() {
        long __key = this.bookId;
        if (book__resolvedKey == null || !book__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookDao targetDao = daoSession.getBookDao();
            Book bookNew = targetDao.load(__key);
            synchronized (this) {
                book = bookNew;
            	book__resolvedKey = __key;
            }
        }
        return book;
    }

    public void setBook(Book book) {
        if (book == null) {
            throw new DaoException("To-one property 'bookId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.book = book;
            bookId = book.getId();
            book__resolvedKey = bookId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<StoryPage> getStoryPages() {
        if (storyPages == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StoryPageDao targetDao = daoSession.getStoryPageDao();
            List<StoryPage> storyPagesNew = targetDao._queryStoriesChapter_StoryPages(id);
            synchronized (this) {
                if(storyPages == null) {
                    storyPages = storyPagesNew;
                }
            }
        }
        return storyPages;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetStoryPages() {
        storyPages = null;
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

    public AudioChapter getAudioForChapter(){

        AudioBook audioBook = getBook().getAudioBook();
        if(audioBook != null){
            AudioChapter audioChapter = audioBook.getChapter(Integer.parseInt(this.getNumber()));
            if(audioChapter != null ){
                return audioChapter;
            }
        }
        return null;
    }

    @Nullable
    public StoriesChapter getNextChapter(){

        for(StoriesChapter chapter : getBook().getStoryChapters()){
            if(Integer.parseInt(chapter.getNumber()) == Integer.parseInt(number) + 1){
                return chapter;
            }
        }
        return null;
    }

    //region UWDatabaseModel

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent) {
        try {
            return StoriesChapterParser.parseStoriesChapter(json, parent);
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

        session.getStoriesChapterDao().insert(this);
        refresh();
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {

        StoriesChapter newStoriesChapter = (StoriesChapter) newModel;

        this.uniqueSlug = newStoriesChapter.uniqueSlug;
        this.number = newStoriesChapter.number;
        this.title = newStoriesChapter.title;
        this.ref = newStoriesChapter.ref;
        this.bookId = newStoriesChapter.bookId;
        update();
        return true;
    }

    //endregion

    //region dataFinding

    /**
     * @param number number of this chapter's page that is desired
     * @return StoryPage with the passed Number
     */
    public StoryPage getStoryPageForNumber(String number){

        StoryPageDao dao = daoSession.getStoryPageDao();
        return dao.queryBuilder()
                .where(StoryPageDao.Properties.StoryChapterId.eq(getId()), StoryPageDao.Properties.Number.eq(number))
                .unique();
    }



    //endregion

    //region Comparable<>

    //endregion


    @Override
    public int compareTo(@NonNull StoriesChapter another) {
        return Integer.parseInt(this.getNumber()) - Integer.parseInt(another.getNumber());
    }

    //region convenience methods

    /**
     * @param uniqueSlug slug that's unique to a single instance
     * @param session used DaoSession
     * @return StoriesChapter with the passed slug
     */
    static public StoriesChapter getModelForUniqueSlug(String uniqueSlug, DaoSession session){

        StoriesChapterDao dao = session.getStoriesChapterDao();
        return dao.queryBuilder()
                .where(StoriesChapterDao.Properties.UniqueSlug.eq(uniqueSlug))
                .unique();
    }

//    static public StoriesChapter getModelForId(long id, DaoSession session){
//
//        StoriesChapterDao dao = session.getStoriesChapterDao();
//        StoriesChapter model = dao.queryBuilder()
//                .where(StoriesChapterDao.Properties.Id.eq(id))
//                .unique();
//
//        return (model == null)? null : model;
//    }

    //endregion

    @Override
    public String toString() {
        return "StoriesChapter{" +
                "book__resolvedKey=" + book__resolvedKey +
                ", bookId=" + bookId +
                ", id=" + id +
                ", number='" + number + '\'' +
                ", ref='" + ref + '\'' +
                ", slug='" + slug + '\'' +
                ", title='" + title + '\'' +
                ", uniqueSlug='" + uniqueSlug + '\'' +
                '}';
    }
    // KEEP METHODS END

}
