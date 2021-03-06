package model;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.unfoldingword.mobile.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import de.greenrobot.common.io.FileUtils;
import model.daoModels.AudioChapter;
import model.daoModels.Book;
import model.daoModels.Version;
import model.parsers.MediaType;
import utils.FileNameHelper;
import utils.FileUtil;

/**
 * Created by Fechner on 11/23/15.
 */
public class DataFileManager {

    public interface GetDownloadStateResponse{
        void foundDownloadState(DownloadState state);
    }

    private static final String TAG = "DataFileManager";
    private static final String TEMP_FILE_FOLDER_NAME = "sideload";

    private static final int FILES_PER_TEXT = 2;
    private static final int FILES_PER_AUDIO = 1;
    private static final int FILES_PER_VIDEO = 2;

    public static void saveDataForBook(Context context, Book book, byte[] data, MediaType type){
        saveDataForBook(context, book, data, type, book.getSourceUrl());
    }

    public static void saveDataForBook(Context context, Book book, byte[] data, MediaType type, String url){
        FileUtil.saveFile(getFileForDownload(context, type, book.getVersion(), FileNameHelper.getSaveFileName(context, book, type, url)), data);
    }

    public static void saveSignatureForBook(Context context, Book book, byte[] data, MediaType type){

        saveSignatureForBook(context, book, data, type, book.getSignatureUrl());
    }

    public static void saveSignatureForBook(Context context, Book book, byte[] data, MediaType type, String url){

        FileUtil.saveFile(getFileForDownload(context, type, book.getVersion(), FileNameHelper.getSaveFileName(context, book, type, url)), data);
    }

    public static void getStateOfContent(final Context context, final Version version, final MediaType type, final GetDownloadStateResponse response){

        new AsyncTask<Void, DownloadState, DownloadState>(){

            @Override
            protected DownloadState doInBackground(Void... params) {

                File mediaFolder = getFileForDownload(context, type, version);
                if(!mediaFolder.exists()){
                    Log.d(TAG, "Media folder didn't exist");
                    return DownloadState.DOWNLOAD_STATE_NONE;
                }
                else {
                    return verifyStateForContent(version, type, mediaFolder);
                }
            }

            @Override
            protected void onPostExecute(DownloadState downloadState) {
                super.onPostExecute(downloadState);
                response.foundDownloadState(downloadState);
            }
        }.execute();
    }

    public static void getStateOfContent(final Context context, final List<Version> versions, final MediaType type, final GetDownloadStateResponse response){

        new AsyncTask<Void, DownloadState, DownloadState>(){

            @Override
            protected DownloadState doInBackground(Void... params) {

//                Log.d(TAG, "started checking of content state asynctask");
                for(Version version : versions) {
                    File mediaFolder = getFileForDownload(context, type, version);
                    if (mediaFolder.exists() && verifyStateForContent(version, type, mediaFolder) == DownloadState.DOWNLOAD_STATE_DOWNLOADED) {
                        // version had that type of media
                        return DownloadState.DOWNLOAD_STATE_DOWNLOADED;
                    }
                }

                return DownloadState.DOWNLOAD_STATE_NONE;
            }

            @Override
            protected void onPostExecute(DownloadState downloadState) {
                super.onPostExecute(downloadState);
                response.foundDownloadState(downloadState);
            }
        }.execute();
    }



    public static Uri getUri(Context context, Book book, MediaType type, String fileUrl){

        return Uri.fromFile(getFileForDownload(context, type, book.getVersion(), FileNameHelper.getSaveFileName(context, book, type, fileUrl)));
    }

    public static int getDownloadedBitrate(Context context, Version version, MediaType type){

        File audioFolder = getFileForDownload(context, type, version);
        if(audioFolder.exists() && audioFolder.isDirectory()){
            File[]files = audioFolder.listFiles();
            for(File file : files){
                String fileName = file.getName();
                Pattern bitrateFinder = Pattern.compile("(\\d*)kbps");
                Matcher matcher = bitrateFinder.matcher(fileName);
                while (matcher.find()) {
                    String group = matcher.group(0);
                    String bitrate = group.substring(0, group.indexOf("k"));
                    if(isNumeric(bitrate)){
                        return Integer.parseInt(bitrate);
                    }
                }
            }
        }
        return -1;
    }

    public static boolean isNumeric(String str) {
        try {
            int d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static boolean deleteContentForBook(Context context, Version version, MediaType type){

        File desiredFolder = getFileForDownload(context, type, version);
        if(desiredFolder.exists()){
            if(FileUtil.deleteContents(desiredFolder)) {
                return desiredFolder.delete();
            }
        }
        return false;
    }

    private static DownloadState verifyStateForContent(Version version, MediaType type, File folder){

        int expectedSize = getCountForMediaType(version, type);
        File[] files = folder.listFiles();
        int numberOfFiles = files.length;
        if (expectedSize < 1) {
//            Log.d(TAG, "expected size is < 1");
            return DownloadState.DOWNLOAD_STATE_NONE;
        }
        else if(expectedSize > numberOfFiles){
//            Log.d(TAG, "expected size is " + expectedSize + " but number of files is " + numberOfFiles);
            return DownloadState.DOWNLOAD_STATE_DOWNLOADING;
        }
        else if (expectedSize == numberOfFiles) {
//            Log.d(TAG, "expected size is good!");
            return DownloadState.DOWNLOAD_STATE_DOWNLOADED;
        }
        else{
            Log.e(TAG, "error, file were larger than expected. that shouldn't happen");
            return DownloadState.DOWNLOAD_STATE_ERROR;
        }
    }

    private static int getCountForMediaType(Version version, MediaType type){

        switch (type){
            case MEDIA_TYPE_TEXT:{
                return version.getBooks().size() * FILES_PER_TEXT;
            }
            case MEDIA_TYPE_AUDIO:{
                int finalCount = 0;
                for(Book book : version.getBooks()){
                    finalCount += book.getAudioBook().getAudioChapters().size();
                }
                return finalCount;
            }
            case MEDIA_TYPE_VIDEO:{
                return version.getBooks().size() * FILES_PER_VIDEO;
            }
            default: return -1;
        }
    }

    private static String getPath(Context context, MediaType mediaType, Version version){
        return context.getFilesDir() + "/" + version.getUniqueSlug() + "/" + mediaType.getPathForType();
    }

    private static File getFileForDownload(Context context, MediaType mediaType, Version version){
        return new File(getPath(context, mediaType, version));
    }

    private static File getFileForDownload(Context context, MediaType mediaType, Version version, String fileName){
        return new File(getPath(context, mediaType, version), fileName);
    }

//    @Nullable
//    public static Uri createUriForSideLoad(Context context, Version version, List<MediaType> types, String fileName){
//
//        FileUtil.clearTemporaryFiles(context);
//        saveTempFileForSideLoading(context, version.getAsSideLoadJson(context).toString().getBytes(), FileNameHelper.getShareTextFileName(version));
//        for(MediaType type : types){
//            if(type == MediaType.MEDIA_TYPE_AUDIO){
//                int bitRate = getDownloadedBitrate(context, version, MediaType.MEDIA_TYPE_AUDIO);
//                if(!saveAudioFilesForPreload(context, version, bitRate)){
//                    return null;
//                }
//            }
//        }
//        File outFile = new File(FileUtil.getTempStorageDir(context));
//
//        Uri compressedFile = compressFiles(version, FileUtil.getUriForTempDir(context, TEMP_FILE_FOLDER_NAME), Uri.fromFile(outFile));
//        return compressedFile;
//    }

    public static Uri createUriForSideLoad(Context context, Map<Version, List<MediaType>> versions, String fileName) {

        FileUtil.clearTemporaryFiles(context);

        for(Map.Entry<Version, List<MediaType>> entry : versions.entrySet()) {

            for (MediaType type : entry.getValue()) {
                if (type == MediaType.MEDIA_TYPE_AUDIO) {
                    int bitRate = getDownloadedBitrate(context, entry.getKey(), MediaType.MEDIA_TYPE_AUDIO);
                    if (!saveAudioFilesForPreload(context, entry.getKey(), bitRate)) {
                        return null;
                    }
                }
                else if(type == MediaType.MEDIA_TYPE_TEXT) {
                    if(saveTempFileForSideLoading(context, entry.getKey().getAsSideLoadJson(context).toString().getBytes(), FileNameHelper.getShareTextFileName(entry.getKey())) == null){
                        return null;
                    }
                }
            }
        }
        File outFile = new File(FileUtil.getTempStorageDir(context));

        Uri compressedFile = compressFiles(fileName, FileUtil.getUriForTempDir(context, TEMP_FILE_FOLDER_NAME), Uri.fromFile(outFile));
        return compressedFile;
    }

    private static boolean saveAudioFilesForPreload(Context context, Version version, int bitRate){

        boolean success = true;
        for(Book book : version.getBooks()){
            for(AudioChapter chapter : book.getAudioBook().getAudioChapters()){

                if(!saveAudioForSideLoad(context, book, chapter, bitRate)){
                    success = false;
                }
//                saveAudioSignatureForSideLoad(context, version, chapter, bitRate);
            }
        }
        return success;
    }

    private static Uri compressFiles(String fileName, Uri filesUri, Uri newFile){

        File outFile = new File(newFile.getPath(), fileName);
        zipFolder(new File(filesUri.getPath()), outFile);
        return Uri.fromFile(outFile);
    }

    private static void zipFolder(File srcFile, File outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (File file : files) {
                Log.d("", "Adding file: " + file.getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
        }
    }

    private static void unzipFiles(File archive, File newFile){
        try {
            ZipFile zipfile = new ZipFile(archive);
            int entries = zipfile.size();
            int total = 0;

            for (Enumeration<?> e = zipfile.entries(); e.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, newFile);
            }
            zipfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void unzipEntry(ZipFile zipfile, ZipEntry entry,
                            File outputDir) throws IOException {

        if(!outputDir.exists()){
            outputDir.mkdirs();
        }
//        if (entry.isDirectory()) {
//            outputDir.mkdirs();
//            createDir(new File(outputDir, entry.getName()));
//            return;
//        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        BufferedInputStream inputStream = new
                BufferedInputStream(zipfile
                .getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(outputFile));

        try{
            copy(inputStream, outputStream);
        }
        finally{
            outputStream.close();
            inputStream.close();
        }
    }

    public static int copy(BufferedInputStream in, BufferedOutputStream out) {
        byte[] buffer = new byte[1024];

        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024)) != (-1)) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public static Uri uncompressSideLoadedFiles(Context context, File currentFile){

        FileUtil.clearTemporaryFiles(context);
        File outFile = new File(context.getFilesDir()
                + "/" + context.getString(R.string.app_name) + "/temp/sideload");

        unzipFiles(currentFile, outFile);
        return Uri.fromFile(outFile);
    }


    private static boolean saveAudioForSideLoad(Context context, Book book, AudioChapter audioChapter, int bitRate){

        File file = new File(getUri(context, book, MediaType.MEDIA_TYPE_AUDIO, audioChapter.getAudioUrl(bitRate)).getPath());
        File newFile = new File(FileUtil.getUriForTempDir(context, TEMP_FILE_FOLDER_NAME).getPath());
        newFile = new File(newFile, FileNameHelper.getShareAudioFileName(audioChapter, bitRate));

        try {
            if(!newFile.exists()){
                newFile.createNewFile();
            }
            FileUtils.copyFile(file, newFile);
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    private static void saveAudioSignatureForSideLoad(Context context, Version version, AudioChapter audioChapter, int bitRate){

        Uri fileUri = getUri(context, audioChapter.getAudioBook().getBook(), MediaType.MEDIA_TYPE_AUDIO, audioChapter.getSignatureUrl(bitRate));
        File newFile = new File(FileUtil.getUriForTempDir(context, TEMP_FILE_FOLDER_NAME).getPath());
        FileUtil.copyFile(fileUri, Uri.fromFile(new File(newFile, FileNameHelper.getShareAudioSignatureFileName(audioChapter, bitRate))));
    }

    private static Uri saveTempFileForSideLoading(Context context, byte[] file, String fileName) {
        return FileUtil.createTemporaryFile(context, file, TEMP_FILE_FOLDER_NAME, fileName);
    }
}
