package view;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

/**
 * Created by Fechner on 7/1/15.
 */
public class UWToolbar {

    private Activity activity;

    private Toolbar toolbar;
    private ImageButton leftButton;
    private ImageView logoView;

    private LinearLayout titleLayout;
    private TextView titleText;
    private ImageView titleButtonIndicator;

    private ImageButton checkingLevelImage;

    private LinearLayout rightButtonLayout;
    private TextView rightButtonText;
    private ImageView rightButtonButtonIndicator;
    private ImageView rightButtonImage;

    private UWToolbarListener listener;

    private boolean hidden = false;

    public UWToolbar(Toolbar toolbar, Activity activity, boolean hasLogo, int backResource,  UWToolbarListener listener) {

        this.listener = listener;
        this.toolbar = toolbar;
        this.activity = activity;
        setupViews();
        setVisibilities(hasLogo, backResource);
        if(backResource > 0){
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    leftButtonClicked();
                }
            });
        }
    }

    private void setupViews(){

        this.leftButton = (ImageButton) activity.findViewById(R.id.toolbar_left_button);
        this.logoView = (ImageView) activity.findViewById(R.id.toolbar_logo_image);

        this.titleLayout = (LinearLayout) activity.findViewById(R.id.toolbar_middle_button_layout);
        this.titleText = (TextView) activity.findViewById(R.id.toolbar_title);
        this.titleButtonIndicator = (ImageView) activity.findViewById(R.id.toolbar_title_button_image);
        this.checkingLevelImage = (ImageButton) activity.findViewById(R.id.toolbar_checking_level_image_view);

        this.rightButtonLayout = (LinearLayout) activity.findViewById(R.id.toolbar_right_button_layout);
        this.rightButtonText = (TextView) activity.findViewById(R.id.toolbar_right_button_text_view);
        this.rightButtonButtonIndicator = (ImageView) activity.findViewById(R.id.toolbar_right_button_image_indicator);
        this.rightButtonImage = (ImageView) activity.findViewById(R.id.toolbar_right_image_button);
    }

    private void setVisibilities(boolean hasLogo, int backResource){

        logoView.setVisibility((hasLogo)? View.VISIBLE : View.GONE);
        leftButton.setVisibility((backResource > 0)? View.VISIBLE : View.INVISIBLE);
        if(backResource > 0) {
            leftButton.setImageResource(backResource);
        }

        titleLayout.setVisibility(View.GONE);
        checkingLevelImage.setVisibility(View.GONE);
        rightButtonLayout.setVisibility(View.GONE);
    }

    public void setBackResource(int resource){
        leftButton.setImageResource(resource);
    }

    public void setTitle(String text, boolean clickable){

        if(text != null) {
            titleText.setText(text);
            if (clickable) {
                titleLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        centerButtonClicked();
                    }
                });
                titleButtonIndicator.setVisibility(View.VISIBLE);
            } else {
                titleButtonIndicator.setVisibility(View.GONE);
            }
            titleLayout.setVisibility(View.VISIBLE);
        }
        else{
            titleLayout.setVisibility(View.GONE);
        }
    }

    public void setBackButtonResource(int resource){
        leftButton.setImageResource(resource);
    }

    public void setCheckingLevelImage(int resource){

        if(resource > 0) {

            checkingLevelImage.setImageResource(resource);
            checkingLevelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkingLevelButtonClicked();
                }
            });
            checkingLevelImage.setVisibility(View.VISIBLE);
        }
        else{
            checkingLevelImage.setVisibility(View.GONE);
        }
    }

    public void setRightButtonText(String text, boolean clickable){

        if(text != null) {
            rightButtonText.setText(text);
            if (clickable) {
                rightButtonLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rightButtonClicked();
                    }
                });
                rightButtonButtonIndicator.setVisibility(View.VISIBLE);
            } else {
                rightButtonButtonIndicator.setVisibility(View.GONE);
            }

            rightButtonText.setVisibility(View.VISIBLE);
            rightButtonLayout.setVisibility(View.VISIBLE);
        }
        else{
            rightButtonLayout.setVisibility(View.GONE);
        }
    }

    public void setRightImageVisible(boolean visible){

        rightButtonButtonIndicator.setVisibility((visible)? View.GONE : View.VISIBLE);
        rightButtonText.setVisibility((visible)? View.GONE : View.VISIBLE);
        rightButtonImage.setVisibility((visible)? View.VISIBLE : View.GONE);
        rightButtonLayout.setVisibility((visible)? View.VISIBLE : View.GONE);
    }

    public void toggleHidden(){

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        if(!hidden){
            params.addRule(RelativeLayout.ABOVE, R.id.top_marker_layout);
        }
        else{
            params.removeRule(RelativeLayout.ABOVE);
        }

        toolbar.setLayoutParams(params);
        hidden = !hidden;
//        for(int rule : params.getRules()){
//            if(rule == new RelativeLayout.LayoutParams(RelativeLayout.ABOVE, R.id.top_marker_layout))
//        }
    }


    private void leftButtonClicked(){

        if(this.listener != null){
            this.listener.leftButtonClicked();
        }
    }

    private void centerButtonClicked(){
        if(this.listener != null){
            this.listener.centerButtonClicked();
        }
    }

    private void checkingLevelButtonClicked(){
        if(this.listener != null){
            this.listener.checkingLevelButtonClicked();
        }
    }

    private void rightButtonClicked(){
        if(this.listener != null){
            this.listener.rightButtonClicked();
        }
    }

    public void setBackgroundColor(int color){
        this.toolbar.setBackgroundColor(color);
    }

    public interface UWToolbarListener{

        void leftButtonClicked();
        void centerButtonClicked();
        void checkingLevelButtonClicked();
        void rightButtonClicked();
    }
}
