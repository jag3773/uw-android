package fragments;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import adapters.selectionAdapters.CollapsibleVersionAdapter;
import model.DaoDBHelper;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceManager;
import view.AnimatedExpandableListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragments.VersionSelectionFragment.VersionSelectionFragmentListener} interface
 * to handle interaction events.
 * Use the {@link VersionSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VersionSelectionFragment extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHOSEN_PROJECT_ID = "CHOSEN_PROJECT_ID";
    static final String STORIES_SLUG = "obs";
    private static final String SHOW_TITLE_PARAM = "SHOW_TITLE_PARAM";

    private String chosenProjectId;
    protected AnimatedExpandableListView mListView = null;
    private View footerView = null;
    private Project chosenProject = null;
    CollapsibleVersionAdapter adapter;

    private boolean showTitle = false;
    private TextView titleTextView;

    private VersionSelectionFragmentListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VersionSelectionFragment.
     */
    public static VersionSelectionFragment newInstance(String projId, boolean showTitle) {

        VersionSelectionFragment fragment = new VersionSelectionFragment();
        Bundle args = new Bundle();
        args.putString(CHOSEN_PROJECT_ID, projId);
        args.putBoolean(SHOW_TITLE_PARAM, showTitle);
        fragment.setArguments(args);
        return fragment;
    }

    public VersionSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            chosenProjectId = getArguments().getString(CHOSEN_PROJECT_ID);
            showTitle = getArguments().getBoolean(SHOW_TITLE_PARAM);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showTitle = getArguments().getBoolean(SHOW_TITLE_PARAM);

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
        setupViews(view, inflater);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!showTitle){
            titleTextView.setVisibility(View.GONE);
        }
        else{
            if (chosenProject == null) {
                addProject();
            }
            titleTextView.setText(chosenProject.getTitle());
            titleTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setupViews(View view, LayoutInflater inflater){
        titleTextView = (TextView) view.findViewById(R.id.version_selection_text_view);
        if(!showTitle){
            titleTextView.setVisibility(View.GONE);
        }
        else{
            if (chosenProject == null) {
                addProject();
            }
            titleTextView.setText(chosenProject.getTitle());
            titleTextView.setVisibility(View.VISIBLE);
        }
        prepareListView(view, inflater);
    }


    protected void prepareListView(View view, LayoutInflater inflater){

        //getting instance of ExpandableListView
        mListView = (AnimatedExpandableListView) view.findViewById(R.id.versions_list);

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if(!showTitle) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mListView.isGroupExpanded(groupPosition)) {
                                mListView.collapseGroupWithAnimation(groupPosition);
                            } else {
                                mListView.expandGroupWithAnimation(groupPosition);
                            }
                        }
                    });
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        if (chosenProject == null) {
            addProject();
        }

        adapter = new CollapsibleVersionAdapter(this, this.chosenProject);

        mListView.setAdapter(adapter);

        int selectedIndex;
        if(chosenProject.getSlug().equalsIgnoreCase(STORIES_SLUG)){
            setupForStories();
        }
        else{
            selectedIndex = setupForBible();
            if(selectedIndex >= 0) {
                mListView.expandGroup(selectedIndex);
            }
        }
    }

    private int setupForStories(){

        Context context = getContext();
        int selectedIndex = 0;
        String selectedVersion = UWPreferenceManager.getSelectedStoryVersion(context);
        if(Long.parseLong(selectedVersion) < 0){
            selectedIndex = 0;
        }
        else {
            Version version = Version.getVersionForId(DaoDBHelper.getDaoSession(context), Long.parseLong(selectedVersion));

            for(int i = 0; i < chosenProject.getLanguages().size(); i++){
                if(chosenProject.getLanguages().get(i).getSlug().equalsIgnoreCase(version.getLanguage().getSlug())){
                    selectedIndex = i;
                    mListView.expandGroup(i);
                }

            }
        }
//        for(int i = 0; i < adapter.getGroupCount(); i++){
//            mListView.expandGroup(i);
//        }
        return selectedIndex;
    }

    private int setupForBible(){

        Context context = getContext();
        int selectedIndex = -1;
        String selectedVersion = UWPreferenceManager.getSelectedBibleVersion(context);
        if(Long.parseLong(selectedVersion) >= 0){
            Version version = Version.getVersionForId(DaoDBHelper.getDaoSession(context), Long.parseLong(selectedVersion));

            for(int i = 0; i < chosenProject.getLanguages().size(); i++){
                if(chosenProject.getLanguages().get(i).getSlug().equalsIgnoreCase(version.getLanguage().getSlug())){
                    selectedIndex = i;
                    break;
                }
            }
        }
        return selectedIndex;
    }

    private void addProject(){

        this.chosenProject = Project.getProjectForId(Long.parseLong(chosenProjectId), DaoDBHelper.getDaoSession(getContext()));
    }

    private Context getContext(){
        return getActivity().getApplicationContext();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (VersionSelectionFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if(adapter != null) {
            adapter.willDestroy();
        }
    }

    public void rowSelected(){

        this.mListener.rowWasSelected();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface VersionSelectionFragmentListener {
        // TODO: Update argument type and name
        public void rowWasSelected();
    }

}
