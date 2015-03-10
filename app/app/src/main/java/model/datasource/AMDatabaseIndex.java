package model.datasource;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Fechner on 2/25/15.
 */
public class AMDatabaseIndex {

    public static String DB_NAME = "_un_folding_word";
    public static int DB_VERSION = 4;

    private static AMDatabaseIndex ourInstance = new AMDatabaseIndex();

    public static AMDatabaseIndex getInstance() {
        return ourInstance;
    }



    private AMDatabaseIndex() {

    }

    static public void createTables(SQLiteDatabase database, Context context){

        database.execSQL(new ProjectDataSource(context).getTableCreationString());
        database.execSQL(new LanguageDataSource(context).getTableCreationString());
        database.execSQL(new VersionDataSource(context).getTableCreationString());
        database.execSQL(new BookDataSource(context).getTableCreationString());
        database.execSQL(new BibleChapterDataSource(context).getTableCreationString());
        database.execSQL(new StoriesChapterDataSource(context).getTableCreationString());
        database.execSQL(new PageDataSource(context).getTableCreationString());
    }
}
