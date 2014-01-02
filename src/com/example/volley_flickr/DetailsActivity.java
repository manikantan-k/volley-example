package com.example.volley_flickr;

import java.util.List;

import android.os.Bundle;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.example.volley_flickr.MainActivity;

public class DetailsActivity extends Activity {



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}
	
	private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;


    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;
    private NetworkImageView mImageView;
    private TextView mTextView;
    private int mOriginalOrientation;
    private JSONItem thatItem;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mImageView = (NetworkImageView) findViewById(R.id.imageView);
        mTextView = (TextView) findViewById(R.id.description);
        

        Bundle bundle = getIntent().getExtras();

        final int pos = bundle.getInt(".pos");
        final int thumbnailTop = bundle.getInt(".top");
        final int thumbnailLeft = bundle.getInt( ".left");
        final int thumbnailWidth = bundle.getInt(".width");
        final int thumbnailHeight = bundle.getInt(".height");
        mOriginalOrientation = bundle.getInt( ".orientation");
        
        Log.d("Det", pos +"<" + MainActivity.thatBitmap.getByteCount());

        thatItem = MainActivity.items.get(pos);
        //mImageView.setImageUrl( thatItem.getUrl(), ApplicationController.getImageLoader() );
        
        

        if (savedInstanceState == null) {
            ViewTreeObserver observer = mImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                
                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);


                    int[] screenLocation = new int[2];
                    mImageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];
                    
                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / mImageView.getWidth();
                    mHeightScale = (float) thumbnailHeight / mImageView.getHeight();
    
                    runEnterAnimation();
                    
                    return true;
                }
            });
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	//mImageView.setImageBitmap(MainActivity.thatBitmap);
    	//mImageView.setImageUrl( thatItem.getUrl(), ApplicationController.getImageLoader() );
        mTextView.setText(thatItem.getTitle());
    }


    public void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION );
        

        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.setScaleX(mWidthScale);
        mImageView.setScaleY(mHeightScale);
        mImageView.setTranslationX(mLeftDelta);
        mImageView.setTranslationY(mTopDelta);
        
        // We'll fade the text in later
        mTextView.setAlpha(0);
        
        // Animate scale and translation to go from thumbnail to full size
        mImageView.animate().setDuration(duration).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).
                withEndAction(new Runnable() {
                    public void run() {
                        // Animate the description in after the image animation
                        // is done. Slide and fade the text in from underneath
                        // the picture.
                        mTextView.setTranslationY(-mTextView.getHeight());
                        mTextView.animate().setDuration(duration/2).
                                translationY(0).alpha(1).
                                setInterpolator(sDecelerator);
                    }
                });
        mImageView.setImageBitmap(MainActivity.thatBitmap);
        

    }
    

    public void runExitAnimation(final Runnable endAction) {
        final long duration = (long) (ANIM_DURATION );

        final boolean fadeOut;
        if (getResources().getConfiguration().orientation != mOriginalOrientation) {
            mImageView.setPivotX(mImageView.getWidth() / 2);
            mImageView.setPivotY(mImageView.getHeight() / 2);
            mLeftDelta = 0;
            mTopDelta = 0;
            fadeOut = true;
        } else {
            fadeOut = false;
        }

        // First, slide/fade text out of the way
        mTextView.animate().translationY(-mTextView.getHeight()).alpha(0).
                setDuration(duration/2).setInterpolator(sAccelerator).
                withEndAction(new Runnable() {
                    public void run() {
                        // Animate image back to thumbnail size/location
                        mImageView.animate().setDuration(duration).
                                scaleX(mWidthScale).scaleY(mHeightScale).
                                translationX(mLeftDelta).translationY(mTopDelta).
                                withEndAction(endAction);
                        if (fadeOut) {
                            mImageView.animate().alpha(0);
                        }
                        
                    }
                });

        
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it is complete.
     */
    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                // *Now* go ahead and exit the activity
                finish();
            }
        });
    }


    
    @Override
    public void finish() {
        super.finish();
        
        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

}
