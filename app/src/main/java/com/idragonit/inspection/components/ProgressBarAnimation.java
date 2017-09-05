package com.idragonit.inspection.components;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by CJH on 2016.01.23.
 */
public class ProgressBarAnimation extends Animation {
    ProgressBar progressBar;
    TextView textProgress;
    float from;
    float  to;

    public ProgressBarAnimation(ProgressBar progressBar, TextView textProgress, float from, float to) {
        this.progressBar = progressBar;
        this.textProgress = textProgress;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
        textProgress.setText((int)(value) + "%");
    }

}
