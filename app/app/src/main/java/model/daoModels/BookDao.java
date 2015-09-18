package model.daoModels;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import model.daoModels.Book;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BOOK".
*/
public class BookDao extends AbstractDao<Book, Long> {

    public static final String TABLENAME = "BOOK";

    /**
     * Properties of entity Book.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UniqueSlug = new Property(1, String.class, "uniqueSlug", false, "UNIQUE_SLUG");
        public final static Property Slug = new Property(2, String.class, "slug", false, "SLUG");
        public final static Property Title = new Property(3, String.class, "title", false, "TITLE");
        public final static Property Description = new Property(4, String.class, "description", false, "DESCRIPTION");
        public final static Property SourceUrl = new Property(5, String.class, "sourceUrl", false, "SOURCE_URL");
        public final static Property SignatureUrl = new Property(6, String.class, "signatureUrl", false, "SIGNATURE_URL");
        public final static Property Modified = new Property(7, java.util.Date.class, "modified", false, "MODIFIED");
        public final static Property VersionId = new Property(8, long.class, "versionId", false, "VERSION_ID");
    };

    private DaoSession daoSession;

    private Query<Book> version_BooksQuery;

    public BookDao(DaoConfig config) {
        super(config);
    }
    
    public BookDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BOOK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"UNIQUE_SLUG\" TEXT," + // 1: uniqueSlug
                "\"SLUG\" TEXT," + // 2: slug
                "\"TITLE\" TEXT," + // 3: title
                "\"DESCRIPTION\" TEXT," + // 4: description
                "\"SOURCE_URL\" TEXT," + // 5: sourceUrl
                "\"SIGNATURE_URL\" TEXT," + // 6: signatureUrl
                "\"MODIFIED\" INTEGER," + // 7: modified
                "\"VERSION_ID\" INTEGER NOT NULL );"); // 8: versionId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BOOK\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Book entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uniqueSlug = entity.getUniqueSlug();
        if (uniqueSlug != null) {
            stmt.bindString(2, uniqueSlug);
        }
 
        String slug = entity.getSlug();
        if (slug != null) {
            stmt.bindString(3, slug);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(5, description);
        }
 
        String sourceUrl = entity.getSourceUrl();
        if (sourceUrl != null) {
            stmt.bindString(6, sourceUrl);
        }
 
        String signatureUrl = entity.getSignatureUrl();
        if (signatureUrl != null) {
            stmt.bindString(7, signatureUrl);
        }
 
        java.util.Date modified = entity.getModified();
        if (modified != null) {
            stmt.bindLong(8, modified.getTime());
        }
        stmt.bindLong(9, entity.getVersionId());
    }

    @Override
    protected void attachEntity(Book entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Book readEntity(Cursor cursor, int offset) {
        Book entity = new Book( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uniqueSlug
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // slug
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // title
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // description
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // sourceUrl
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // signatureUrl
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)), // modified
            cursor.getLong(offset + 8) // versionId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Book entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUniqueSlug(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSlug(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDescription(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSourceUrl(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSignatureUrl(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setModified(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
        entity.setVersionId(cursor.getLong(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Book entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Book entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "books" to-many relationship of Version. */
    public List<Book> _queryVersion_Books(long versionId) {
        synchronized (this) {
            if (version_BooksQuery == null) {
                QueryBuilder<Book> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.VersionId.eq(null));
                version_BooksQuery = queryBuilder.build();
            }
        }
        Query<Book> query = version_BooksQuery.forCurrentThread();
        query.setParameter(0, versionId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getVersionDao().getAllColumns());
            builder.append(" FROM BOOK T");
            builder.append(" LEFT JOIN VERSION T0 ON T.\"VERSION_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Book loadCurrentDeep(Cursor cursor, boolean lock) {
        Book entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Version version = loadCurrentOther(daoSession.getVersionDao(), cursor, offset);
         if(version != null) {
            entity.setVersion(version);
        }

        return entity;    
    }

    public Book loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Book> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Book> list = new ArrayList<Book>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Book> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Book> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
