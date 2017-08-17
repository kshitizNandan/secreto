package com.secreto.image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView.ScaleType;

public class ImageHelper {
    private static final String TAG = ImageHelper.class.getSimpleName();

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, Rect imgRect, int pixels, ScaleType scaleType) {
        Bitmap output = null;
        try {
            if (scaleType == ScaleType.CENTER_INSIDE) {
                float ratio = imgRect.right / (float) bitmap.getWidth();
                imgRect.right = (int) (bitmap.getWidth() * ratio);
                imgRect.bottom = (int) (bitmap.getHeight() * ratio);

                int newWidth = imgRect.right;
                int newHeight = imgRect.bottom;

                try {
                    output = Bitmap.createBitmap(newWidth, imgRect.bottom, Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    return null;
                }

                Canvas canvas = new Canvas(output);
                int sourceWidth = bitmap.getWidth();
                int sourceHeight = bitmap.getHeight();

                final int color = 0xff424242;
                final Paint paint = new Paint();
                final Rect targetRect = new Rect(0, 0, imgRect.right, imgRect.bottom);

                final RectF rectF = new RectF(targetRect);
                final float roundPx = pixels;

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);
                canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

                float scale;
                float dx;
                float dy;

                if (sourceWidth <= newWidth && sourceHeight <= newHeight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) newWidth / (float) sourceWidth, (float) newHeight / (float) sourceHeight);
                }

                dx = (int) ((newWidth - sourceWidth * scale) * 0.5f + 0.5f);
                dy = (int) ((newHeight - sourceHeight * scale) * 0.5f + 0.5f);

                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
                canvas.drawBitmap(bitmap, matrix, paint);

                return output;

            } else if (scaleType == ScaleType.CENTER_CROP) {
                int newWidth = imgRect.right;
                int newHeight = imgRect.bottom;

                try {
                    output = Bitmap.createBitmap(newWidth, imgRect.bottom, Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    return null;
                }

                Canvas canvas = new Canvas(output);
                int sourceWidth = bitmap.getWidth();
                int sourceHeight = bitmap.getHeight();
                final int color = 0xff424242;
                final Paint paint = new Paint();
                final Rect targetRect = new Rect(0, 0, imgRect.right, imgRect.bottom);

                final RectF rectF = new RectF(targetRect);
                final float roundPx = pixels;

                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                // canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);
                canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

                float scale;
                float dx = 0, dy = 0;

                if (sourceWidth * newHeight > newWidth * sourceHeight) {
                    scale = (float) newHeight / (float) sourceHeight;
                    dx = (newWidth - sourceWidth * scale) * 0.5f;
                } else {
                    scale = (float) newWidth / (float) sourceWidth;
                    dy = (newHeight - sourceHeight * scale) * 0.5f;
                }

                // Log.d(TAG, "newWidth " + newWidth);
                // Log.d(TAG, "newHeight " + newHeight);
                //
                // Log.d(TAG, "sourceWidth " + sourceWidth);
                // Log.d(TAG, "sourceHeight " + sourceHeight);

                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
                canvas.drawBitmap(bitmap, matrix, paint);

                return output;
            } else {
                int newWidth = imgRect.right;
                int newHeight = imgRect.bottom;

                // Lets see if we can handle the low mem situation.
                try {
                    output = Bitmap.createBitmap(newWidth, imgRect.bottom, Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    return null;
                }

                Canvas canvas = new Canvas(output);
                int sourceWidth = bitmap.getWidth();
                int sourceHeight = bitmap.getHeight();
                final int color = 0xff424242;
                final Paint paint = new Paint();
                final Rect targetRect = new Rect(0, 0, imgRect.right, imgRect.bottom);

                final RectF rectF = new RectF(targetRect);
                final float roundPx = pixels;

                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                // canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);
                canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

                float scale = 0;
                float dx;
                float dy;

                // if (sourceWidth <= newWidth && sourceHeight <= newHeight) {
                // scale = Math.max((float) newWidth / (float) sourceWidth,
                // (float) newHeight / (float) sourceHeight);
                // } else {
                // scale = Math.max((float) newWidth / (float) sourceWidth,
                // (float) newHeight / (float) sourceHeight);
                // }
                scale = Math.max((float) newWidth / (float) sourceWidth, (float) newHeight / (float) sourceHeight);

                dx = (int) ((newWidth - sourceWidth * scale) * 0.5f + 0.5f);
                dy = (int) ((newHeight - sourceHeight * scale) * 0.5f + 0.5f);

                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);

                canvas.drawBitmap(bitmap, matrix, paint);

                // Log.d(TAG, ">> newWidth " + newWidth);
                // Log.d(TAG, "newHeight " + newHeight);
                //
                // Log.d(TAG, "sourceWidth " + sourceWidth);
                // Log.d(TAG, "sourceHeight " + sourceHeight);

                return output;
            }
        } catch (Exception e) {
        } finally {
            /*
             * if(output!=null) {
			 * 
			 * output.recycle(); output=null; }
			 */
        }

        return output;

    }

    static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels, boolean roundTopLeftCorner, boolean roundTopRightCorner,
                                         boolean roundBottomLeftCorner, boolean roundBottomRightCorner) {

        Bitmap output = null;
        if (bitmap != null) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, w, h);
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;
            // Log.d(TAG, "round px: "+ pixels);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            if (!roundTopLeftCorner) {
                canvas.drawRect(0, 0, w / 2f, h / 2f, paint);
            }
            if (!roundTopRightCorner) {
                canvas.drawRect(w / 2f, 0, w, h / 2f, paint);
            }
            if (!roundBottomLeftCorner) {
                canvas.drawRect(0, h / 2f, w / 2f, h, paint);
            }
            if (!roundBottomRightCorner) {
                canvas.drawRect(w / 2f, h / 2f, w, h, paint);
            }
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        return output;
    }
}