package activity.readingSelection;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import activity.AnimationParadigm;
import activity.SettingsActivity;
import activity.UWBaseActivity;
import activity.reading.ReadingActivity;
import activity.reading.StoryReadingActivity;
import activity.sharing.LoadActivity;
import activity.sharing.ShareActivity;
import adapters.ShareAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import adapters.selectionAdapters.InitialPageAdapter;
import fragments.CheckingLevelInfoFragment;
import model.DaoDBHelper;
import model.daoModels.Language;
import model.daoModels.Project;
import services.UWUpdaterService;
import utils.NetWorkUtil;
import utils.URLUtils;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 2/27/15.
 */
public class InitialScreenActivity extends UWBaseActivity{

    static final public String PROJECT_PARAM = "PROJECT_PARAM";
    static public final String GENERAL_CHECKING_LEVEL_FRAGMENT_ID = "GENERAL_CHECKING_LEVEL_FRAGMENT_ID";

    private FrameLayout visibleLayout = null;

    private Button mRefreshButton = null;
    private Button settingsButton = null;
    private ListView listview;

    InitialPageAdapter adapter;

    private List<Project> mProjects = null;

    /**
     * This broadcast for When the update is completed
     */
    private BroadcastReceiver updateReceiver;

    //region Activity Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_list_activity);
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UWPreferenceManager.getIsFirstLaunch(getApplicationContext())){
            showCheckingLevelFragment();
        }
    }

    @Override
    protected void onPause() {

        unregisterReceiver();

        super.onPause();
    }

    @Override
    public int getBackResource() {
        return -1;
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_LEFT_RIGHT;
    }
    @Override
    public void rightButtonClicked() {

        startSharingActivity();
    }

    //endregion

    //region Receiver Handling

    private BroadcastReceiver createReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(URLUtils.BROAD_CAST_DOWN_COMP)) {
                    Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Download error", Toast.LENGTH_SHORT).show();
                }
                visibleLayout.setVisibility(View.GONE);
                reload();
            }
        };
    }

    private void unregisterReceiver(){

        if(updateReceiver != null) {
            getApplicationContext().unregisterReceiver(updateReceiver);
        }
        updateReceiver = null;
    }

    //endregion

    //region Setup

    private void setupViews(){

        setupToolbar(true, getString(R.string.app_name), false);
        getToolbar().setRightImageFontAwesome(FontAwesomeIcons.fa_download);
        setupListView();
        addSettingsFooter();
    }

    private void setupListView() {

        List<GeneralRowInterface> data = this.getData();

        if (listview == null) {
            listview = (ListView) findViewById(R.id.generalList);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startReadingActivity(mProjects.get(position - 1));
                }
            });
            setupRefreshButton();
        }

        if(adapter == null){
            adapter = new InitialPageAdapter(this.getApplicationContext(), data, -1);
            listview.setAdapter(adapter);
        } else{
            adapter.updateWithData(data);
        }
    }

    private void addSettingsFooter(){

        if(settingsButton == null) {
            LayoutInflater inflater = getLayoutInflater();
            View footerView = inflater.inflate(R.layout.settings_footer, null);


            // change version number
            settingsButton = (Button) footerView.findViewById(R.id.settings_button);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToSettings();
                }
            });
            listview.addFooterView(footerView);
        }
    }

    private void setupRefreshButton() {

        if(mRefreshButton == null) {
            LayoutInflater inflater = getLayoutInflater();
            View mview1 = inflater.inflate(R.layout.header_view, null);
            visibleLayout = (FrameLayout) mview1.findViewById(R.id.refreshView);
            mRefreshButton = (Button) mview1.findViewById(R.id.refreshButton);
            mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update();
                }
            });

            listview.addHeaderView(mview1);

            IntentFilter filter = new IntentFilter();
            filter.addAction(URLUtils.BROAD_CAST_DOWN_COMP);
            filter.addAction(URLUtils.BROAD_CAST_DOWN_ERROR);
            createReceiver();
            registerReceiver(updateReceiver, filter);
        }
    }

    protected List<GeneralRowInterface> getData(){

        if(mProjects == null){
            updateProjects();
        }

        if(mProjects == null || mProjects.size() < 1){
            return new ArrayList<GeneralRowInterface>();
        }

        List<GeneralRowInterface> dataList = new ArrayList<GeneralRowInterface>();
        for(Project row : mProjects) {
            dataList.add(new GeneralRowInterface.BasicGeneralRowInterface(row.getUniqueSlug(), row.getTitle()));
            List<Language> langs = row.getLanguages();
        }

        return dataList;
    }

    //endregion

    //region updating

    private void update(){

        if (!NetWorkUtil.isConnected(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Failed connecting to the internet.");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        } else {
            visibleLayout.setVisibility(View.VISIBLE);
            // to handle new data from network
            startService(new Intent(getApplicationContext(), UWUpdaterService.class));
        }
    }

    private void reload(){

        mProjects = null;
        this.setupListView();
    }

    private void updateProjects() {

        mProjects = Project.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));
    }

    //endregion

    //region Navigation actions

    private void moveToSettings(){

        goToNewActivity(SettingsActivity.class);
    }

    private void startReadingActivity(Project project){

        Class nextActivity = (project.getUniqueSlug().equalsIgnoreCase(getString(R.string.open_bible_stories_slug)))?
                StoryReadingActivity.class : ReadingActivity.class;

        Intent newIntent = new Intent(this, nextActivity).putExtra(PROJECT_PARAM, project);

        goToNextActivity(newIntent);
    }

    private void showCheckingLevelFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        CheckingLevelInfoFragment fragment = new CheckingLevelInfoFragment();
        fragment.show(ft, GENERAL_CHECKING_LEVEL_FRAGMENT_ID);
        UWPreferenceManager.setIsFirstLaunch(getApplicationContext(), false);
    }

    private void startSharingActivity(){

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Select Share Method");

        AlertDialog dialogue = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setCustomTitle(titleView)
                .setAdapter(new ShareAdapter(getApplicationContext(), Arrays.asList("Send/Save Versions", "Receive/Load Versions")),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0: {
                                        goToNewActivity(new Intent(getApplicationContext(), ShareActivity.class));
                                        break;
                                    }
                                    case 1: {
                                        goToNewActivity(new Intent(getApplicationContext(), LoadActivity.class));
                                        break;
                                    }
                                    default: {
                                        dialog.cancel();
                                    }
                                }
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialogue.show();
    }
    //endregion
}
