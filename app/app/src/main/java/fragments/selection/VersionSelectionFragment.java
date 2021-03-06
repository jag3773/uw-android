/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package fragments.selection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import adapters.versions.VersionViewHolder;
import adapters.versions.VersionViewModel;
import adapters.versions.VersionsAdapter;
import de.greenrobot.event.EventBus;
import eventbusmodels.BiblePagingEvent;
import eventbusmodels.DownloadResult;
import eventbusmodels.DownloadingVersionsEvent;
import eventbusmodels.StoriesPagingEvent;
import fragments.BitrateFragment;
import fragments.VersionInfoFragment;
import model.AudioBitrate;
import model.DaoDBHelper;
import model.DataFileManager;
import model.DownloadState;
import model.daoModels.AudioBook;
import model.daoModels.BibleChapter;
import model.daoModels.Project;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import model.parsers.MediaType;
import services.UWMediaDownloaderService;
import services.UWVersionDownloaderService;
import utils.NetWorkUtil;
import utils.UWPreferenceDataManager;

/**
 * Fragment for users to select a new version
 */
public class VersionSelectionFragment extends DialogFragment implements VersionViewModel.VersionViewModelListener {
    private static final String TAG = "VersionSelectionFragmnt";

    private static final String CHOSEN_PROJECT = "CHOSEN_PROJECT";
    private static final String SHOW_TITLE_PARAM = "SHOW_TITLE_PARAM";
    private static final String IS_SECOND_VERSION_PARAM = "IS_SECOND_VERSION_PARAM";

private VersionSelectionFragmentListener listener;

    protected ExpandableListView listView = null;
    private VersionsAdapter adapter;
    private TextView titleTextView;

    private boolean isSecondVersion;
    private Project chosenProject = null;

    private boolean showProjectTitle = false;

    private BroadcastReceiver receiver;

    //region setup

    /**
     * @param project project for which the version will be showing
     * @param showTitle true if the title should be shown
     * @param isSecondVersion true if this is for the second version in the diglot view
     * @return a newly constructed VersionSelectionFragment
     */
    public static VersionSelectionFragment newInstance(Project project, boolean showTitle, boolean isSecondVersion) {


        Bundle args = new Bundle();
        args.putSerializable(CHOSEN_PROJECT, project);
        args.putBoolean(SHOW_TITLE_PARAM, showTitle);
        args.putBoolean(IS_SECOND_VERSION_PARAM, isSecondVersion);

        VersionSelectionFragment fragment = new VersionSelectionFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public VersionSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(DownloadingVersionsEvent event){

//        Log.d(TAG, "Received Event: " + event.toString());

        // need to wait for the event to finish calling before reloading the list
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    synchronized (this){
                        wait(500);
                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                reloadData();
            }
        }.execute();

    }

    public void onEventMainThread(DownloadResult event){
        downloadEnded(event);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (VersionSelectionFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chosenProject = (Project) getArguments().getSerializable(CHOSEN_PROJECT);
        if(chosenProject != null) {
            chosenProject = Project.getProjectForId(chosenProject.getId(), DaoDBHelper.getDaoSession(getContext()));
            showProjectTitle = getArguments().getBoolean(SHOW_TITLE_PARAM);
            isSecondVersion = getArguments().getBoolean(IS_SECOND_VERSION_PARAM);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!showProjectTitle){
            titleTextView.setVisibility(View.GONE);
        }
        else{
            titleTextView.setText(chosenProject.getTitle());
            titleTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_version_selection, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view){

        titleTextView = (TextView) view.findViewById(R.id.version_selection_text_view);
        titleTextView.setText(chosenProject.getTitle());
        titleTextView.setVisibility((showProjectTitle) ? View.VISIBLE : View.GONE);

        prepareListView(view);
    }

    protected void prepareListView(View view){

        listView = (ExpandableListView) view.findViewById(R.id.versions_list);
        long versionId = getVersionId();

        adapter = new VersionsAdapter(this, VersionViewModel.createModels(getContext(), chosenProject, this), versionId);
        listView.setAdapter(adapter);
        int selectedGroup = adapter.getIndexOfChosenVersion();
        if(selectedGroup > -1) {
            listView.expandGroup(selectedGroup);
        }
    }

    private long getVersionId() {

        if (chosenProject.isBibleStories()) {

            StoriesPagingEvent event = StoriesPagingEvent.getStickyEvent(getApplicationContext());
            StoryPage page = isSecondVersion ? event.secondaryStoryPage : event.mainStoryPage;

            return (page != null) ? page.getStoriesChapter().getBook().getVersionId() : -1;
        } else {

            BiblePagingEvent event = BiblePagingEvent.getStickyEvent(getApplicationContext());
            BibleChapter chapter = isSecondVersion ? event.secondaryChapter : event.mainChapter;

            return (chapter != null) ? chapter.getBook().getVersionId() : -1;
        }
    }

    private void downloadEnded(DownloadResult result){

        String resultText = "";
        switch (result){
            case DOWNLOAD_RESULT_SUCCESS:{
                resultText = "Succeeded";
                break;
            }
            case DOWNLOAD_RESULT_CANCELED:{
                resultText = "Was Canceled";
                break;
            }
            case DOWNLOAD_RESULT_FAILED:{
                resultText = "Failed";
                break;
            }
        }

        Toast.makeText(getActivity().getApplicationContext(), "Download " + resultText, Toast.LENGTH_SHORT).show();
        reloadData();
    }


    private void reloadData(){
        adapter.updateModels();
        listView.invalidateViews();
    }

    private boolean canDownload(){
        if (!NetWorkUtil.isConnected(getApplicationContext())) {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Alert")
                    .setMessage("Failed connecting to the internet.")
                    .setPositiveButton("OK", null)
                    .create().show();
            return false;
        } else {
            return true;
        }
    }

    public void doAction(final VersionViewModel viewModel, final VersionViewHolder viewHolder, DownloadState state, final MediaType type) {

        switch (state){
            case DOWNLOAD_STATE_NONE:{

                if (canDownload()) {
                    viewHolder.setupForDownloadState(DownloadState.DOWNLOAD_STATE_DOWNLOADING);
                    downloadResource(viewModel, type);
                }
                break;
            }
            case DOWNLOAD_STATE_DOWNLOADED:{

                String additionalText = (type == MediaType.MEDIA_TYPE_TEXT)? "\n\n*NOTE* All associated content will also be deleted" : "";
                View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
                ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Please Confirm");
                new AlertDialog.Builder(getActivity())
                        .setCustomTitle(titleView)
                        .setMessage("Delete " + viewModel.getTitle() + "?"  + additionalText)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewHolder.setupForDownloadState(DownloadState.DOWNLOAD_STATE_DOWNLOADING);
                                deleteResource(viewModel, type);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                break;
            }
            case DOWNLOAD_STATE_DOWNLOADING:{

                View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
                ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Please Confirm");
                new AlertDialog.Builder(getActivity())
                        .setCustomTitle(titleView)
                        .setMessage("Stop download?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopDownload(viewModel, type);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                break;
            }
        }
    }

    private void downloadResource(VersionViewModel viewModel, MediaType type){

        switch (type){
            case MEDIA_TYPE_TEXT:{
                downloadText(viewModel);
                break;
            }
            case MEDIA_TYPE_AUDIO:{
                downloadAudio(viewModel);
                break;
            }
            case MEDIA_TYPE_VIDEO:{
                downloadVideo(viewModel);
                break;
            }
        }
    }

    private void deleteResource(VersionViewModel viewModel, MediaType type){

        switch (type){
            case MEDIA_TYPE_TEXT:{
                deleteText(viewModel);
                break;
            }
            case MEDIA_TYPE_AUDIO:{
                deleteAudio(viewModel);
                break;
            }
            case MEDIA_TYPE_VIDEO:{
                deleteVideo(viewModel);
                break;
            }
        }
    }

    private void stopDownload(VersionViewModel viewModel, MediaType type){

        switch (type){
            case MEDIA_TYPE_TEXT:{
                deleteText(viewModel);
                break;
            }
            case MEDIA_TYPE_AUDIO:{
                deleteAudio(viewModel);
                break;
            }
            case MEDIA_TYPE_VIDEO:{
                break;
            }
        }
    }

    private void downloadText(VersionViewModel viewModel){

        downloadText(viewModel.getVersion());
    }

    private void downloadText(Version version){
        Intent downloadIntent = new Intent(getContext(), UWVersionDownloaderService.class);
        downloadIntent.putExtra(UWVersionDownloaderService.VERSION_PARAM, version.getId());
        getContext().startService(downloadIntent);
        EventBus.getDefault().postSticky(DownloadingVersionsEvent.forceGetEventAdding(version, MediaType.MEDIA_TYPE_TEXT));
    }

    private void downloadAudio(final VersionViewModel viewModel){

        final AudioBook audioBook = viewModel.getVersion().getBooks().get(0).getAudioBook();
        if(audioBook != null) {

            BitrateFragment.newInstance(audioBook.getAudioChapters().get(0).getBitRates(),
                    "Select Audio Bitrate", new BitrateFragment.BitrateFragmentListener() {
                        @Override
                        public void bitrateChosen(DialogFragment fragment, final AudioBitrate bitrate) {

                            DataFileManager.getStateOfContent(getApplicationContext(), viewModel.getVersion(), MediaType.MEDIA_TYPE_TEXT, new DataFileManager.GetDownloadStateResponse() {
                                @Override
                                public void foundDownloadState(DownloadState state) {
                                    if(state == DownloadState.DOWNLOAD_STATE_NONE){
                                        for(VersionViewModel.ResourceViewModel model : viewModel.getResources()){
                                            if(model.getType() == MediaType.MEDIA_TYPE_TEXT){
                                                downloadText(viewModel.getVersion());
                                                break;
                                            }
                                        }
                                    }
                                    downloadAudio(viewModel, bitrate);
                                }
                            });
                            fragment.dismiss();
                        }

                        @Override
                        public void dismissed() {
                            reloadData();
                        }
            }).show(getActivity().getSupportFragmentManager(), "BitrateFragment");
        }
    }

    private void downloadAudio(final VersionViewModel viewModel, AudioBitrate bitrate){

        Intent downloadIntent = new Intent(getContext(), UWMediaDownloaderService.class)
                .putExtra(UWMediaDownloaderService.VERSION_PARAM, viewModel.getVersion().getId())
                .putExtra(UWMediaDownloaderService.IS_VIDEO_PARAM, false)
                .putExtra(UWMediaDownloaderService.BITRATE_PARAM, bitrate);
        getContext().startService(downloadIntent);
        EventBus.getDefault().postSticky(DownloadingVersionsEvent.forceGetEventAdding(viewModel.getVersion(), MediaType.MEDIA_TYPE_AUDIO));
    }

    private void downloadVideo(final VersionViewModel viewModel){

        Intent downloadIntent = new Intent(getContext(), UWMediaDownloaderService.class);
        downloadIntent.putExtra(UWMediaDownloaderService.VERSION_PARAM, viewModel.getVersion().getId());
        downloadIntent.putExtra(UWMediaDownloaderService.IS_VIDEO_PARAM, true);
        getContext().startService(downloadIntent);
    }

    private void deleteText(final VersionViewModel model){


        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                final Version version = model.getVersion();
                UWPreferenceDataManager.willDeleteVersion(getContext(), version);
                version.deleteContent(getContext());
                refreshData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                reloadData();
            }
        }.execute();
    }

    private void deleteAudio(final VersionViewModel model){


        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Version version = model.getVersion();
                version.deleteAudio(getContext());
                refreshData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                reloadData();
            }
        }.execute();
    }

    private void deleteVideo(VersionViewModel model){

    }

    private void refreshData() {
        BiblePagingEvent.refreshPagingEvent(getApplicationContext());
        StoriesPagingEvent.refreshPagingEvent(getApplicationContext());
    }

    @Override
    public void resourceChosen(VersionViewModel.ResourceViewModel viewModel, Version version) {

        if(listener != null){
            version.update();
            listener.versionWasSelected(version, isSecondVersion, viewModel.getType());
        }
    }

    @Override
    public void showCheckingLevel(Version version, MediaType type) {

        VersionInfoFragment.createFragment(version, type)
                .show(getActivity().getSupportFragmentManager(), "VersionInfoFragment");
    }

    private Context getApplicationContext(){
        return getActivity().getApplicationContext();
    }
    public interface VersionSelectionFragmentListener {
        /**
         * Called when the user selects a version
         * @param version Version the was selected
         * @param isSecondVersion true if this is for the second version in the diglot view
         */
        void versionWasSelected(Version version, boolean isSecondVersion, MediaType mediaType);
    }

}
