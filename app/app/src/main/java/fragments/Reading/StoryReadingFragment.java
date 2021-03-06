/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package fragments.Reading;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unfoldingword.mobile.R;

import java.util.List;

import adapters.StoryPagerAdapter;
import de.greenrobot.event.EventBus;
import eventbusmodels.StoriesPagingEvent;
import model.daoModels.StoryPage;
import singletons.UWAudioPlayer;
import view.ReadingDoubleTapHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoryReadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryReadingFragment extends Fragment implements ReadingDoubleTapHandler.ReadingDoubleTapHandlerListener{

    private static final String TEXT_SIZE_PARAM = "TEXT_SIZE_PARAM";

    private ViewPager readingViewPager;

    private ReadingFragmentListener listener;
    private StoryPagerAdapter adapter;

    private int textSize;

    public static StoryReadingFragment newInstance(int textSize) {
        StoryReadingFragment fragment = new StoryReadingFragment();
        Bundle args = new Bundle();
        args.putInt(TEXT_SIZE_PARAM, textSize);
        fragment.setArguments(args);
        return fragment;
    }

    public StoryReadingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textSize = getArguments().getInt(TEXT_SIZE_PARAM);
        if(getActivity() instanceof ReadingFragmentListener){
            this.listener = (ReadingFragmentListener) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerEventListeners();
    }

    private void registerEventListeners() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEventListeners() {
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(StoriesPagingEvent event) {

        update(event.mainStoryPage, event.secondaryStoryPage, true);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterEventListeners();
    }

    public void setTextSize(int textSize){
        this.textSize = textSize;
        adapter.setTextSize(this.textSize);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_story_reading, container, false);
        setupViews(view);
        setDiglotShowing(false);

        return view;
    }

    public void update(){

        StoriesPagingEvent pagingEvent = getPagingEvent();
        update(pagingEvent.mainStoryPage, pagingEvent.secondaryStoryPage, false);
    }

    private StoriesPagingEvent getPagingEvent() {
        return StoriesPagingEvent.getStickyEvent(getActivity().getApplicationContext());
    }

    private void update(StoryPage mainPage, StoryPage secondaryPage, boolean animateScroll){

        if(mainPage != null && secondaryPage != null) {

            if(adapter != null) {
                adapter.update(mainPage.getStoriesChapter(), secondaryPage.getStoriesChapter());

                int currentIndex = mainPage.getStoriesChapter().getStoryPages().indexOf(mainPage);
                readingViewPager.setCurrentItem(currentIndex, animateScroll);
            }
        }
    }

    private void setupViews(View view){

        setupPager(view);
        adapter.setIsLandscape(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void setupPager(View view){

        StoriesPagingEvent event = getPagingEvent();

        final StoryPage mainPage = event.mainStoryPage;
        StoryPage secondaryPage = event.secondaryStoryPage;

        if(mainPage != null && secondaryPage != null) {
            adapter = new StoryPagerAdapter(getActivity(), mainPage.getStoriesChapter(), secondaryPage.getStoriesChapter(), textSize);
        }
        else{
            adapter = new StoryPagerAdapter(getActivity(), null, null, textSize);
        }

        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);
        readingViewPager.setAdapter(adapter);
        readingViewPager.setOnTouchListener(new ReadingDoubleTapHandler(getResources(), this));
        update(mainPage, secondaryPage, false);

        readingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state != 0) {
                    return;
                }
                int position = readingViewPager.getCurrentItem();
                List<StoryPage> pages = adapter.getMainChapter().getStoryPages();

                if (position < pages.size()) {
                    StoryPage mainModel = pages.get(position);
                    StoryPage secondaryModel = adapter.getSecondChapter().getStoryPages().get(position);
                    StoriesPagingEvent event = new StoriesPagingEvent(mainModel, secondaryModel);
                    UWAudioPlayer.getInstance(getContext()).onEvent(event);
                    EventBus.getDefault().postSticky(event);
                }
            }
        });
    }

    @Override
    public boolean doubleTapWasRegistered() {

        if(listener != null) {
            listener.toggleHidden();
            return true;
        }
        else {
            return false;
        }
    }

    public void setDiglotShowing(boolean showing){

        if(adapter != null) {
            adapter.setIsDiglot(showing);
        }
    }

    public void setOrientationAsLandscape(boolean isLandscape){
        adapter.setIsLandscape(isLandscape);
    }
}
