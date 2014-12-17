/*
* Copyright (C) 2013 Julien Vermet
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package fr.julienvermet.bugdroid.ui.tablet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.ui.BugFragment;
import fr.julienvermet.bugdroid.ui.BugsListFragment;

public abstract class AbsBugsMultiPaneFragment extends SherlockFragment implements OnClickListener {

    private static final String LOG_TAG = AbsBugsMultiPaneFragment.class.getSimpleName();
    private static final int ANIMATION_DURATION = 200;
    private static final int MIN_VELOCITY = 1000;

    // Android
    protected FragmentManager mFragmentManager;
    private VelocityTracker mVelocityTracker;

    // UI
    public RelativeLayout mLeftPane, mBugsPane, mBugPane;
    private FrameLayout mLeftView;
    protected TextView mLeftName;
    private FrameLayout mLeftToolbar;
    private BugFragment mBugFragment;
    private ImageButton mLeftHandle, mBugRefresh, mBugBookmark, mBugShare, mBugFullscreen, mBugClose;

    // Objects
    private boolean mIsLeftCollapsed = false;
    protected boolean mIsLeftToolbarShown = false;
    protected boolean mIsBugShown = false;
    protected boolean mIsBugsShown = false;
    private float mOffsetX;
    protected Instance mInstance;
    private int mMinLeftWidth, mMaxLeftWidth;
    private int mCollapsedBugsWidth, mExpandedBugsWidth;
    private int mBugWidth;
    private int mDisplayWidth;

    @Override
    public void onResume() {
        super.onResume();
        
        mIsLeftToolbarShown = false;
        mIsBugShown = false;
        mIsBugsShown = false;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mDisplayWidth = size.x;
        
        mBugWidth = (int) getResources().getDimension(R.dimen.multi_bugs_bug_width);
        mMinLeftWidth = (int) getResources().getDimension(R.dimen.multi_bugs_left_min_width);
        mMaxLeftWidth = mDisplayWidth;
        int gradientWidth = (int) getResources().getDimension(R.dimen.multi_bugs_gradient_width);
        mCollapsedBugsWidth = mDisplayWidth - mMinLeftWidth - mBugWidth + (gradientWidth*2);
        mExpandedBugsWidth = mDisplayWidth - mMinLeftWidth;
        
        View view = inflater.inflate(R.layout.fragment_bugs_multipane, null);        
        mLeftPane = (RelativeLayout) view.findViewById(R.id.leftPane);
        mBugsPane = (RelativeLayout) view.findViewById(R.id.bugsPane);
        mBugPane = (RelativeLayout) view.findViewById(R.id.bugPane);

        mLeftToolbar = (FrameLayout) view.findViewById(R.id.leftToolbar);
        mLeftHandle = (ImageButton) view.findViewById(R.id.leftHandle);
        mLeftHandle.setOnTouchListener(mLeftHandleTouchListener);
        mLeftHandle.setOnClickListener(this);

        mLeftView = (FrameLayout) view.findViewById(R.id.leftView);
        mLeftName = (TextView) view.findViewById(R.id.leftName);

        mBugRefresh = (ImageButton) view.findViewById(R.id.bugRefresh);
        mBugBookmark = (ImageButton) view.findViewById(R.id.bugBookmark);
        mBugShare = (ImageButton) view.findViewById(R.id.bugShare);
        mBugFullscreen = (ImageButton) view.findViewById(R.id.bugFullscreen);
        mBugClose = (ImageButton) view.findViewById(R.id.bugClose);

        mBugRefresh.setOnClickListener(this);
        mBugBookmark.setOnClickListener(this);
        mBugShare.setOnClickListener(this);
        mBugFullscreen.setOnClickListener(this);
        mBugClose.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mInstance = BugDroidApplication.mCurrentInstance;
        mFragmentManager = getChildFragmentManager();
    }
    
    protected void showLeftToolbox() {
        Animation toTop = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top);
        toTop.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mLeftToolbar.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) { }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                
            }
        });
        mLeftToolbar.startAnimation(toTop);     
    }

    public void hideBugPane() {
        Animation toRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
        toRight.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationRepeat(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                mBugPane.setVisibility(View.GONE);
            }
        });
        mBugPane.startAnimation(toRight);
        expandBugsPane();
    }

    public void collapseLeftPane() {
        if (!mIsLeftToolbarShown) {
            mIsLeftToolbarShown = true;
            showLeftToolbox();
        }
        collapseLeftPane(ANIMATION_DURATION);
    }
    
    public void collapseLeftPane(int speed) {
        int targetWidth = mMinLeftWidth;
        ValueAnimator animator = ValueAnimator.ofObject(new WidthEvaluator(mLeftPane), mLeftPane.getWidth(), targetWidth);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mLeftName.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLeftView.setVisibility(View.GONE);
                mIsLeftCollapsed = true;
            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(speed);
        animator.start();
    }

    public void expandLeftPane(int speed) {
        int targetWidth = mMaxLeftWidth;
        ValueAnimator animator = ValueAnimator.ofObject(new WidthEvaluator(mLeftPane), mLeftPane.getWidth(), targetWidth);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsLeftCollapsed = false;
            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(speed);
        animator.start();
    }
    
    private void toggleLeftPane() {
        if (mIsLeftCollapsed) {
            expandLeftPane();
        } else {
            collapseLeftPane();
        }
    }

    public void expandLeftPane() {
        expandLeftPane(ANIMATION_DURATION);
    }

    public void collapseBugsPane() {
        int targetWidth = mCollapsedBugsWidth;  
        ValueAnimator animator = ValueAnimator.ofObject(new WidthEvaluator(mBugsPane), mBugsPane.getWidth(), targetWidth);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsBugShown = true;
            }
        });
        animator.setDuration(ANIMATION_DURATION);
        animator.start();        
    }

    public void expandBugsPane() {
        showLeftToolbox();
        int targetWidth = mExpandedBugsWidth;   
        ValueAnimator animator = ValueAnimator.ofObject(new WidthEvaluator(mBugsPane), mBugsPane.getWidth(), targetWidth);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsBugShown = false;
            }
        });
        animator.setDuration(ANIMATION_DURATION);
        animator.start();   
    }

    public void onItemClickOnListFragment(SherlockListFragment fragment, Object data) {
        if (fragment instanceof BugsListFragment) {
            final Bug bug = (Bug) data;
            int orientation = getResources().getConfiguration().orientation;
            switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                Intent intent = BugMultiPaneActivity.getIntent(getActivity(), bug.bugId, bug.summary);
                startActivity(intent);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                if (!mIsBugShown) {
                    collapseBugsPane();
                    mBugPane.setVisibility(View.VISIBLE);
                    Animation toLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
                    toLeft.setAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            if (mBugFragment != null) {
                                FragmentTransaction ft = getSherlockActivity().getSupportFragmentManager()
                                    .beginTransaction();
                                ft.remove(mBugFragment).commit();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mBugFragment = BugFragment.newInstance(bug.bugId, bug.summary);
                            FragmentTransaction ft = getSherlockActivity().getSupportFragmentManager()
                                .beginTransaction();
                            ft.replace(R.id.fragment_container_bug, mBugFragment).commit();
                        }
                    });
                    mBugPane.startAnimation(toLeft);
                } else {
                    mBugFragment = BugFragment.newInstance(bug.bugId, bug.summary);
                    FragmentTransaction ft = getSherlockActivity().getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.replace(R.id.fragment_container_bug, mBugFragment).commit();
                }

                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mLeftHandle) {
            toggleLeftPane();
        } else if (v == mBugRefresh) {
            mBugFragment.refresh();
        } else if (v == mBugBookmark) {
            mBugFragment.bookmark();
        } else if (v == mBugShare) {
            mBugFragment.share();
        } else if (v == mBugFullscreen) {
            mBugFragment.fullscreen();
        } else if (v == mBugClose) {
            hideBugPane();
        }
    }
    
    OnTouchListener mLeftHandleTouchListener = new OnTouchListener() {        
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }

            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLeftView.setVisibility(View.VISIBLE);
                mLeftName.setVisibility(View.GONE);
                mOffsetX = (v.getWidth() - event.getX()) + getResources().getDimension(R.dimen.multi_bugs_gradient_width);
                mVelocityTracker.addMovement(event); 
                break;
            case MotionEvent.ACTION_MOVE:
                float minWidth = getResources().getDimension(R.dimen.multi_bugs_left_min_width);
                float newWidth = event.getRawX() + mOffsetX;
                if (newWidth >= minWidth && newWidth <= mDisplayWidth) {
                    mLeftPane.getLayoutParams().width = (int) (event.getRawX() + mOffsetX);
                    mLeftPane.requestLayout();
                }
                mVelocityTracker.addMovement(event);
                return true;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);          
                int velocityX = (int) mVelocityTracker.getXVelocity();
                if (velocityX > 0){
                    if (velocityX > MIN_VELOCITY) {
                        int targetWidth = mMinLeftWidth;
                        int dist = mLeftPane.getWidth() - targetWidth;
                        int speed = (dist * 1000) / velocityX;
                        collapseLeftPane(speed);
                    } else {
                        collapseLeftPane();
                    } 
                } else if (velocityX < 0) {
                    velocityX = Math.abs(velocityX);
                    if (velocityX > MIN_VELOCITY) {
                        int dist = mDisplayWidth - mLeftPane.getWidth();
                        int speed = (dist * 1000) / velocityX;
                        expandLeftPane(speed); 
                    } else {
                        expandLeftPane();
                    }
                }
                mVelocityTracker.recycle();
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                break;
            }
            return false;
        }
    };

    private class WidthEvaluator extends IntEvaluator {

        private View v;

        public WidthEvaluator(View v) {
            this.v = v;
        }

        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            int num = (Integer) super.evaluate(fraction, startValue, endValue);
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.width = num;
            v.setLayoutParams(params);
            return num;
        }
    }
}