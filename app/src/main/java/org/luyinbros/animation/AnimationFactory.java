package org.luyinbros.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class AnimationFactory {

    public static AnimatorSet heartbeatAnimator(@NonNull View mainView,
                                         final @NonNull View rippleView) {

        AnimatorSet animatorSet1 = new AnimatorSet();
        {
            animatorSet1.setDuration(100)
                    .play(ObjectAnimator.ofFloat(mainView, "scaleX", 0.8f, 1.3f))
                    .with(ObjectAnimator.ofFloat(mainView, "scaleY", 0.8f, 1.3f));
            animatorSet1.setInterpolator(new LinearInterpolator());
            animatorSet1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rippleView.setVisibility(View.VISIBLE);
                }
            });
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        {
            AnimatorSet scaleSmall = new AnimatorSet();
            scaleSmall.playTogether(
                    ObjectAnimator.ofFloat(mainView, "scaleX", 1.3f, 1f),
                    ObjectAnimator.ofFloat(mainView, "scaleY", 1.3f, 1f));
            scaleSmall.setDuration(200);
            scaleSmall.setInterpolator(new OvershootInterpolator());

            AnimatorSet rippleAnimSet = new AnimatorSet();
            rippleAnimSet.setDuration(100);
            rippleAnimSet.playTogether(ObjectAnimator.ofFloat(rippleView, "scaleX", 1.3f, 1.6f),
                    ObjectAnimator.ofFloat(rippleView, "scaleY", 1.3f, 1.6f),
                    ObjectAnimator.ofFloat(rippleView, "alpha", 1.0f, 0f));
            rippleAnimSet.setInterpolator(new LinearInterpolator());

            animatorSet2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rippleView.setVisibility(View.GONE);
                }
            });
            animatorSet2.playTogether(scaleSmall, rippleAnimSet);

        }
        AnimatorSet createAnimatorSet = new AnimatorSet();
        createAnimatorSet.play(animatorSet1).before(animatorSet2);
        return createAnimatorSet;

    }
}
