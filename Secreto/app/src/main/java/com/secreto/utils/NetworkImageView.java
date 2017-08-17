package com.secreto.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.secreto.R;
import com.secreto.common.Common;
import com.secreto.image.ImageHelper;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request.
 * MSH: Wont work if extending original NetworkImageView !
 */
public class NetworkImageView extends android.support.v7.widget.AppCompatImageView{
    private static final String TAG = NetworkImageView.class.getSimpleName();

    private int REQUIRED_SIZE = 100;

    /**
     * The URL of the network image to load
     */
    private String mUrl;

    /**
     * Resource ID of the image to be used as a placeholder until the network
     * image is loaded.
     */
    private int mDefaultImageId;

    /**
     * Resource ID of the image to be used if the network response fails.
     */
    private int mErrorImageId;

    /**
     * Local copy of the ImageLoader.
     */
    private ImageLoader mImageLoader;

    /**
     * Current ImageContainer. (either in-flight or finished)
     */
    private ImageContainer mImageContainer;

    private boolean mRoundCorncers;
    private float mRoundedCornerDP;

    public NetworkImageView(Context context) {
        this(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        applyStyle(context, attrs);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyStyle(context, attrs);
    }

    public void setRequiredImageSize(int size) {
        this.REQUIRED_SIZE = size;
    }

    private void applyStyle(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.com_android_volley_toolbox_NetworkImageView);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            if (a.getIndex(i) == R.styleable.com_android_volley_toolbox_NetworkImageView_roundedCorner) {
                //TODO: We need to cache the rounded images or find a faster way to render them as the
                // current method causes bad performance.
                // Comment this line out to disable rounded corners.
                this.mRoundCorncers = a.getBoolean(R.styleable.com_android_volley_toolbox_NetworkImageView_roundedCorner, false);
            } else if (a.getIndex(i) == R.styleable.com_android_volley_toolbox_NetworkImageView_roundedCornerDP) {
//				int radius = 2;
                int radius = (int) a.getFloat(R.styleable.com_android_volley_toolbox_NetworkImageView_roundedCornerDP, 2);
                float temp = Common.dipToPixel(context, radius);
                this.mRoundedCornerDP = context.getResources().getDisplayMetrics().density * temp;
            }
        }
        a.recycle();
    }

    /**
     * Sets URL of the image that should be loaded into this view. Note that
     * calling this will immediately either set the cached image (if available)
     * or the default image specified by
     * {@link NetworkImageView#setDefaultImageResId(int)} on the view.
     * <p/>
     * NOTE: If applicable, {@link NetworkImageView#setDefaultImageResId(int)}
     * and {@link NetworkImageView#setErrorImageResId(int)} should be called
     * prior to calling this function.
     *
     * @param url         The URL that should be loaded into this ImageView.
     * @param imageLoader ImageLoader that will be used to make the request.
     */
    public void setImageUrl(String url, ImageLoader imageLoader) {
        mUrl = url;
        mImageLoader = imageLoader;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }

    /**
     * Sets the default image resource ID to be used for this view until the
     * attempt to load it completes.
     */
    public void setDefaultImageResId(int defaultImage) {
        mDefaultImageId = defaultImage;
    }

    /**
     * Sets the error image resource ID to be used for this view in the event
     * that the image requested fails to load.
     */
    public void setErrorImageResId(int errorImage) {
        mErrorImageId = errorImage;
    }

    /**
     * Loads the image for the view if it isn't already loaded.
     *
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     */
    private void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = getWidth();
        int height = getHeight();
        Bitmap imgBit = null;

        boolean isFullyWrapContent = getLayoutParams() != null
                && getLayoutParams().height == LayoutParams.WRAP_CONTENT
                && getLayoutParams().width == LayoutParams.WRAP_CONTENT;
        // if the view's bounds aren't known yet, and this is not a
        // wrap-content/wrap-content
        // view, hold off on loading the image.
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }


        // if the URL to be loaded in this view is empty, cancel any old
        // requests and clear the
        // currently loaded image.
        if (TextUtils.isEmpty(mUrl)) {
            if (mImageContainer != null) {
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }

            if (mDefaultImageId != 0) {
                setImageResource(mDefaultImageId);
            } else {
                setImageBitmap(null);
            }
            return;
        }

        // if there was an old request in this view, check if it needs to be
        // canceled.
        if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
            if (mImageContainer.getRequestUrl().equals(mUrl)) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's
                // fetching a different URL.
                mImageContainer.cancelRequest();

                if (mDefaultImageId != 0) {
                    setImageResource(mDefaultImageId);
                } else {
                    setImageBitmap(null);
                }
            }
        }

        // The pre-existing content of this view didn't match the current URL.
        // Load the new image
        // from the network.

        try {
            Logger.d(TAG, "mUrl: " + mUrl);
            if (mUrl.startsWith("http://") || mUrl.startsWith("https://")) {
                // Logger.d(TAG, "1>>");
                ImageContainer newContainer = mImageLoader.get(mUrl,
                        new ImageListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (mErrorImageId != 0) {
                                    setImageResource(mErrorImageId);
                                }
                            }

                            @Override
                            public void onResponse(final ImageContainer response, boolean isImmediate) {
                                // If this was an immediate response that was
                                // delivered inside of a layout
                                // pass do not set the image immediately as it
                                // will trigger a requestLayout
                                // inside of a layout. Instead, defer setting
                                // the image by posting back to
                                // the main thread.

                                // Commented by Anand - it remove unnecessary
                                // image blinking
                                /*
                                 * if (isImmediate && isInLayoutPass) { post(new
								 * Runnable() {
								 * 
								 * @Override public void run() {
								 * onResponse(response, false); } }); return; }
								 */
                                if (response.getBitmap() != null) {
                                    Bitmap rBitmap = null;
                                    try {
                                        if (mRoundCorncers) {
                                            //Logger.d(TAG, "WDITH " + getWidth() + " x " + getHeight());
                                            Rect rect = new Rect(0, 0, getWidth(), getHeight());
                                            rBitmap = ImageHelper.getRoundedCornerBitmap(response.getBitmap(), rect, (int) mRoundedCornerDP, getScaleType());
                                            if (rBitmap != null) {
                                                setImageBitmap(rBitmap);
                                            }
                                        } else
                                            setImageBitmap(response.getBitmap());
                                    } catch (Exception e) {
                                        Logger.e(TAG, "Exception: ", e);
                                    } finally {
                                        /*if(rBitmap!=null)
                                        {
											rBitmap.recycle();
											rBitmap=null;
										}*/
                                    }

                                } else if (mDefaultImageId != 0) {
                                    setImageResource(mDefaultImageId);
                                }
                            }
                        });

                // update the ImageContainer to be the new bitmap container.
                mImageContainer = newContainer;
            } else {
                Bitmap data = decodeFile(mUrl);
                if (data != null) {
                    try {
                        if (mRoundCorncers) {
                            Rect rect = new Rect(0, 0, getWidth(), getHeight());
                            imgBit = ImageHelper.getRoundedCornerBitmap(data, rect, (int) mRoundedCornerDP, getScaleType());
                            if (imgBit != null) {
                                setImageBitmap(imgBit);
                            }
                        } else {
                            setImageBitmap(data);
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, "Exception: ", e);
                    }

                } else if (mDefaultImageId != 0) {
                    setImageResource(mDefaultImageId);
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "Exception: ", e);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mImageContainer != null) {
            // If the view was bound to an image request, cancel it and clear
            // out the image from the view.
            mImageContainer.cancelRequest();
            setImageBitmap(null);
            // also clear out the container so we can reload the image if
            // necessary.
            mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(String f) {
        try {
            String filePath = f;
            if (f.startsWith("fi")) {
                filePath = f.substring(6);
            }

            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inPurgeable = true;
            o.inInputShareable = true;
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(filePath), null, o);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            try {
                // decode with inSampleSize
                final BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                final FileInputStream fin = new FileInputStream(filePath);
                final Bitmap bit = BitmapFactory.decodeStream(fin, null, o2);
                fin.close();
                return bit;
            } catch (Exception e) {
                Logger.e(TAG, "Exception: ", e);
            }
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "Exception: ", e);
        }
        return null;
    }

}