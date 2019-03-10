package com.yakin.fastselector.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.yakin.fastselector.R;

public class ChoosePopupWindow extends PopupWindow implements View.OnClickListener {

    private Activity activity;

    private View popupView;
    private Animation animationIn, animationOut;

    private boolean isDismissing;

    public ChoosePopupWindow(Activity activity) {
        super(activity);
        this.activity = activity;
        View inflateView = LayoutInflater.from(activity).inflate(R.layout.fast_choose_popup_layout, null);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable());
        this.setContentView(inflateView);

        animationIn = AnimationUtils.loadAnimation(activity, R.anim.fast_slide_up_in);
        animationOut = AnimationUtils.loadAnimation(activity, R.anim.fast_slide_down_out);
        popupView = inflateView.findViewById(R.id.popup);
        inflateView.setOnClickListener(this);
        inflateView.findViewById(R.id.take_image).setOnClickListener(this);
        inflateView.findViewById(R.id.take_video).setOnClickListener(this);
        inflateView.findViewById(R.id.take_audio).setOnClickListener(this);
        inflateView.findViewById(R.id.take_cancel).setOnClickListener(this);
    }

    @Override
    public void showAsDropDown(View parent) {
        if(parent == null) {
            parent = activity.getWindow().getDecorView();
        }
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        isDismissing = false;
        popupView.startAnimation(animationIn);
    }

    @Override
    public void dismiss() {
        if(!isDismissing) {
            isDismissing = true;
            popupView.startAnimation(animationOut);
            animationOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isDismissing = false;
                    ChoosePopupWindow.super.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.take_image) {
            if(listener != null) {
                listener.onImageClick();
            }
        } else if(v.getId() == R.id.take_video) {
            if(listener != null) {
                listener.onVideoClick();
            }
        } else if(v.getId() == R.id.take_audio) {
            if(listener != null) {
                listener.onAudioClick();
            }
        }
        dismiss();
    }

    private OnChooseItemClickListener listener;

    public void setOnChooseItemClickListener(OnChooseItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnChooseItemClickListener {
        void onImageClick();
        void onVideoClick();
        void onAudioClick();
    }
}
