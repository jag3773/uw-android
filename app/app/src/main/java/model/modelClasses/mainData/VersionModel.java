package model.modelClasses.mainData;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.VersionDataSource;
import model.modelClasses.StatusModel;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Fechner on 1/22/15.
 */
public class VersionModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface {

    private static final String TAG = "ResourceModel";

    public enum DOWNLOAD_STATE{
        DOWNLOAD_STATE_ERROR(0),
        DOWNLOAD_STATE_NONE(1),
        DOWNLOAD_STATE_DOWNLOADING(2),
        DOWNLOAD_STATE_DOWNLOADED(3);

         DOWNLOAD_STATE(int i) {
        }

        public static DOWNLOAD_STATE createState(int value) {

            switch (value) {
                case 1:{
                    return DOWNLOAD_STATE_NONE;
                }
                case 2:{
                    return DOWNLOAD_STATE_DOWNLOADING;
                }
                case 3:{
                    return DOWNLOAD_STATE_DOWNLOADED;
                }
                default:{
                    return DOWNLOAD_STATE_ERROR;
                }
            }
        }
    }

    private class VersionJsonModel {

        long mod;
        String name;
        String slug;
        StatusJsonModel status;
    }

    private class StatusJsonModel {

        String checking_entity;
        String checking_level;
        String comments;
        String contributors;
        String publish_date;
        String source_text;
        String source_text_version;
        String version;
    }

    private static final String DATE_MODIFIED_JSON_KEY = "date_modified";
    private static final String NAME_JSON_KEY = "name";
    private static final String SLUG_JSON_KEY = "slug";
    private static final String STATUS_JSON_KEY = "status";


    public String name;
    public long dateModified;
    public StatusModel status;
    public DOWNLOAD_STATE downloadState;

    public String verificationText;
    public int verificationStatus;

    private ArrayList<String> signingOrganizations = null;

    public ArrayList<String> getSigningOrganizations(Context context){

        if(signingOrganizations == null) {
            Map<String, String> allOrgs = new HashMap<String, String>();
            for (BookModel model : this.getChildModels(context)) {

                Map<String, String> bookOrgs = model.getVerificationOrganizations(context);
                allOrgs.putAll(bookOrgs);
            }

            if (allOrgs.size() < 1) {
                return null;
            }
            ArrayList<String> finalOrgs = new ArrayList<String>();
            for (String value : allOrgs.values()) {
                finalOrgs.add(value);
            }
            signingOrganizations = finalOrgs;
        }
        return signingOrganizations;
    }

    private LanguageModel parent;
    public LanguageModel getParent(Context context){

        if(parent == null){
            parent = (LanguageModel) this.getDataSource(context).loadParentModelFromDatabase(this);
        }
        return parent;
    }
    public void setParent(LanguageModel parent){
        this.parent = parent;
    }

    private ArrayList<BookModel> books = null;
    public ArrayList<BookModel> getChildModels(Context context){

        if(books == null){
            books = this.getDataSource(context).getChildModels(this);
        }

        if(books.size() == 0){
            books = null;
        }
        return books;
    }

    public void resetBooks(){
        books = null;
    }

    public VersionModel() {
        super();
        this.status = new StatusModel();
    }

    public VersionModel(JSONObject jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    public VersionModel(JSONObject jsonObject, long parentId, boolean sideLoaded) {
        super(jsonObject, parentId, sideLoaded);
    }

    public BookModel findBookForJsonSlug(Context context, String slug){

        for(BookModel model : this.getChildModels(context)){

            if(model.slug.substring(0, 3).equalsIgnoreCase(slug.substring(0, 3))){

                return model;
            }
        }
        return null;
    }
    
    @Override
    public VersionDataSource getDataSource(Context context) {
        return new VersionDataSource(context);
    }

    @Override
    public void initModelFromJson(JSONObject json, boolean preLoaded){


        try {
            name = json.getString("name");
            dateModified = json.getLong("mod");
            slug = json.getString("slug");

            JSONObject statusJson = json.getJSONObject("status");
            status = new StatusModel();


            status.checkingEntity = statusJson.getString("checking_entity");
            status.checkingLevel = statusJson.getString("checking_level");
            status.comments = statusJson.getString("comments");
            status.contributors = statusJson.getString("contributors");
            status.publishDate = statusJson.getString("publish_date");
            status.sourceText = statusJson.getString("source_text");
            status.sourceTextVersion = statusJson.getString("source_text_version");
            status.version = statusJson.getString("version");
        }
        catch (JSONException e){
            e.printStackTrace();
        }

//
//        VersionJsonModel model = new Gson().fromJson(json.toString(), VersionJsonModel.class);
//
//        name = model.name;
//        dateModified = model.mod;
//        slug = model.slug;
//
//        status = new StatusModel();
//        status.checkingEntity = model.status.checking_entity;
//        status.checkingLevel = model.status.checking_level;
//        status.comments = model.status.comments;
//        status.contributors = model.status.contributors;
//        status.publishDate = model.status.publish_date;
//        status.sourceText = model.status.source_text;
//        status.sourceTextVersion = model.status.source_text_version;
//        status.version = model.status.version;
        uid = -1;
        verificationStatus = -1;
    }

    @Override
    public void initModelFromJson(JSONObject json, long parentId, boolean sideLoaded) {
        if(sideLoaded){
            this.initModelFromSideLoadedJson(json);
        }
        else {
            this.initModelFromJson(json, sideLoaded);
//            this.slug += ((LanguageModel) parent).slug;
        }

        this.parentId = parentId;
        downloadState = DOWNLOAD_STATE.DOWNLOAD_STATE_NONE;
        this.verificationText = "";
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getChildIdentifier() {
        return Long.toString(this.uid);
    }

    public VersionModel save(Context context){
        this.getDataSource(context).createOrUpdateDatabaseModel(this);
        return this.getDataSource(context).getModel(Long.toString(this.uid));
    }

    public int getVerificationStatus(Context context){

        if(this.verificationStatus > -1){
            return this.verificationStatus;
        }
        int verifyStatus = 0;

        if(this.getChildModels(context) == null || downloadState != DOWNLOAD_STATE.DOWNLOAD_STATE_DOWNLOADED){
            verificationStatus = -1;
            return verificationStatus;
        }

        for(BookModel book : this.getChildModels(context)){
            switch (book.getVerificationStatus(context)){
                case -1:
                case 0:{
                    break;
                }
                case 1:{
                    if(verifyStatus < 1){
                        verifyStatus = 1;
                        this.verificationText += book.getTitle() + " Has Expired \n";
                    }
                    break;
                }
                case 3:{
                    if(verifyStatus < 3){
                        verifyStatus = 3;
                        this.verificationText += book.getTitle() + " Failed \n";
                    }
                    break;
                }
                default:{
                    verifyStatus = 2;
                    this.verificationText += book.getTitle() + " Encountered an Error \n";
                    break;
                }
            }
        }
        this.verificationStatus = verifyStatus;
        return  verificationStatus;
    }

    public String toString() {
        return "VersionModel{" +
                ", slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                ", dateModified=" + dateModified +
                ", status=" + status.toString() +
                ", parent=" + parent +
                status.toString() +
                "} " + super.toString();
    }

    protected class VersionSideLoadedModel {

        long date_modified;
        String name;
        String slug;

        String checking_entity;
        String checking_level;
        String comments;
        String contributors;
        String publish_date;
        String source_text;
        String source_text_version;
        String version;

        BookModel.BookSideLoadedModel[] books;

        private VersionSideLoadedModel(VersionModel model, Context context) {

            this.date_modified = model.dateModified;
            this.name = model.name;
            this.slug = model.slug;
            this.checking_entity = model.status.checkingEntity;
            this.checking_level = model.status.checkingLevel;
            this.comments = model.status.comments;
            this.contributors = model.status.contributors;
            this.publish_date = model.status.publishDate;
            this.source_text = model.status.sourceText;
            this.source_text_version = model.status.sourceTextVersion;
            this.version = model.status.version;

            ArrayList<BookModel> bookModels = model.getChildModels(context);
            this.books = new BookModel.BookSideLoadedModel[bookModels.size()];

            for(int i = 0; i < bookModels.size(); i++){
                this.books[i] = bookModels.get(i).getAsSideLoadedModel(context);
            }
        }
    }


    protected VersionSideLoadedModel getAsSideLoadedModel(Context context){

        return new VersionSideLoadedModel(this, context);
    }

    public void initModelFromSideLoadedJson(JSONObject json){

        VersionSideLoadedModel model = new Gson().fromJson(json.toString(), VersionSideLoadedModel.class);

        name = model.name;
        dateModified = model.date_modified;
        slug = model.slug;

        status = new StatusModel();
        status.checkingEntity = model.checking_entity;
        status.checkingLevel = model.checking_level;
        status.comments = model.comments;
        status.contributors = model.contributors;
        status.publishDate = model.publish_date;
        status.sourceText = model.source_text;
        status.sourceTextVersion = model.source_text_version;
        status.version = model.version;
        uid = -1;
    }
}
