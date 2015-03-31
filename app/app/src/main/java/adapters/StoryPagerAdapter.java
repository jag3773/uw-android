package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import activity.bookSelection.StoryChapterSelectionActivity;
import model.database.DBManager;
import model.modelClasses.mainData.StoriesChapterModel;
import utils.AsyncImageLoader;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class StoryPagerAdapter extends PagerAdapter implements ImageLoadingListener {


    private static final String TAG = "ViewPagerAdapter";

    protected String SELECTED_POS = "";

    DisplayImageOptions options;
    DBManager dbManager = null;
    private Activity activity;
    private static Context context;
    private TextView chapterTextView;
    private ImageLoader mImageLoader;
    private ViewGroup container;
    private StoriesChapterModel currentChapter;

    private int lastChapterNumber = -1;


    public StoryPagerAdapter(Object context, StoriesChapterModel model, ImageLoader mImageLoader, TextView chapterTextView, String positionHolder) {
        this.context = (Context) context;
        currentChapter = model;
        this.mImageLoader = mImageLoader;
        this.chapterTextView = chapterTextView;
        setImageOptions();
        dbManager = DBManager.getInstance(this.context);
        this.activity = (Activity) context;
        lastChapterNumber = currentChapter.getParent(this.context).getStoryChildModels(this.context).size();
        SELECTED_POS = positionHolder;
    }

    @Override
    public int getCount() {
        return currentChapter.getChildModels(context).size() + 1;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.container = container;
        View view = null;

        if (position == getCount() - 1) {
            view = getNextChapterView(inflater);
        } else {

            view = inflater.inflate(R.layout.stories_pager_layout, container, false);
            ImageView chapterImageView = (ImageView) view.findViewById(R.id.chapter_image_view);
            TextView storyTextView = (TextView) view.findViewById(R.id.story_text_view);
            storyTextView.setText(currentChapter.getChildModels(context).get(position).text);
            String imgUrl = currentChapter.getChildModels(context).get(position).imageUrl;
            String lastBitFromUrl = AsyncImageLoader.getLastBitFromUrl(imgUrl);
            String path = lastBitFromUrl.replaceAll("[{//:}]", "");

            String imagePath = "assets://images/" + path;

            mImageLoader.displayImage(imagePath, chapterImageView, options, this);

        }
        ((ViewPager) container).addView(view);
        return view;
    }

    private View getNextChapterView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.next_chapter_screen_layout, container, false);
        Button nextButton = (Button) view.findViewById(R.id.next_chapter_screen_button);

        if(Integer.parseInt(currentChapter.number) == lastChapterNumber){
            String nextButtonString = context.getResources().getString(R.string.chapters);
            nextButton.setText(nextButtonString);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });
        }
        else {
            String nextButtonString = context.getResources().getString(R.string.next_chapter);
            nextButton.setText(nextButtonString);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToNextChapter();
                }
            });
        }
        return view;
    }

    private void moveToNextChapter(){

        int chapterNumber = Integer.parseInt(currentChapter.number);
//        String languageName = currentChapter.language;
        String nextChapterNumber = Integer.toString(chapterNumber + 1);
        if(nextChapterNumber.length() == 1){
            nextChapterNumber = "0" + nextChapterNumber;
        }

        ArrayList<StoriesChapterModel> chapters = currentChapter.getParent(context).getStoryChildModels(context);

        StoriesChapterModel nextChapter = null;
        int newChapterNumber = Integer.parseInt(this.currentChapter.number);
        for(StoriesChapterModel chapter : chapters){
            if(Integer.parseInt(chapter.number) == newChapterNumber + 1){
                nextChapter = chapter;
                break;
            }
        }

        this.currentChapter = nextChapter;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(StoryChapterSelectionActivity.CHAPTERS_INDEX_STRING, newChapterNumber).commit();
        getCount();

        int current_value = Integer.parseInt(currentChapter.number);
        chapterTextView.setText(nextChapter.title);
        UWPreferenceManager.setSelectedStoryChapter(context, nextChapter.uid);

        notifyDataSetChanged();

        ((ViewPager) this.container).setCurrentItem(0);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    private void setImageOptions() {
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .showImageOnLoading(new ColorDrawable(Color.WHITE))
                .showImageOnFail(new ColorDrawable(Color.WHITE)).build();
    }

    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String url, View view, FailReason failReason) {
        ImageView imageView = (ImageView) view.findViewById(R.id.chapter_image_view);
        if (url.contains("file")) {
            String w = AsyncImageLoader.getLastBitFromUrl(url);
            mImageLoader.displayImage("assets://images/" + w, imageView, options);
        } else {
            String lastBitFromUrl = AsyncImageLoader.getLastBitFromUrl(url);
            String s = lastBitFromUrl.replaceAll("[}]", "");
            mImageLoader.displayImage("assets://images/" + s, imageView, options);
        }

    }

    @Override
    public void onLoadingComplete(String url, View view, Bitmap bitmap) {

//        if (url.contains("file")) {
//            System.out.println("It worked!");
//        } else {
//            String lastBitFromUrl = "";
//            if (url.contains("}}")) {
//                String replace = url.replace("}}", "");
//                lastBitFromUrl = URLUtils.getLastBitFromUrl(replace);
//            } else {
//                lastBitFromUrl = URLUtils.getLastBitFromUrl(url);
//                ImageDatabaseHandler.storeImage(context, bitmap, lastBitFromUrl);
//            }
//        }
//

    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(SELECTED_POS, position).commit();
    }

}
