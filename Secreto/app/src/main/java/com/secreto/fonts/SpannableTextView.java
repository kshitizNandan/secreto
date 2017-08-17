package com.secreto.fonts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;


import com.secreto.R;
import com.secreto.common.MyApplication;
import com.secreto.utils.Logger;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Uses tag handler for applying type faces and styles to text. Note that this is tightly integrated with MyApplication to serve the type faces.
 */
public class SpannableTextView extends AppCompatTextView {
    private static final String TAG = SpannableTextView.class.getSimpleName();
    private static final String BLUE_REGULAR = "blue-regular";
    private static final String DARK_GRAY_REGULAR = "dark-gray-regular";
    private static final String DARK_BLUE_BOLD = "dark-blue-bold";
    private static final String LIGHT_GREY = "light-grey";
    private static final String PRIVACY_POLICY = "privacy-policy";
    private static final String TERMS_OF_USE = "terms-of-use";
    private static final String BLACK_REGULAR = "black-regular";
    private TermsAndPrivacyClickedListener termsAndPrivacyClickedListener;
    private String orgText = null;

    public SpannableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Log.d(TAG, "SpannableTextView");

        final int attributeResourceValue = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "text", 0);
        // Process hard coded string value from layout file.
        if (attributeResourceValue == 0) {
            final String text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
            if (text != null)
                setText(text);
        }
        // Extract text value from strings.
        else
            setText(context.getString(attributeResourceValue));
    }

    public void setTermsAndPrivacyClickedListener(final TermsAndPrivacyClickedListener termsAndPrivacyClickedListener) {
        this.termsAndPrivacyClickedListener = termsAndPrivacyClickedListener;

        //  Enable use of ClickableSpan so that the onClick is fired.
        setMovementMethod(LinkMovementMethod.getInstance());
        // If not set any listview row will not be clickable:
        // http://stackoverflow.com/questions/8558732/listview-textview-with-linkmovementmethod-makes-list-item-unclickable
        setFocusable(false);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        // //Log.d(TAG, "setText");
        if (this.orgText == null)
            this.orgText = text.toString();
        super.setText(Html.fromHtml(text.toString(), imgGetter, tagHandler), BufferType.SPANNABLE);
    }

    private ImageGetter imgGetter = new ImageGetter() {
        public Drawable getDrawable(String source) {
            // //Log.d(TAG, "getDrawable: " + source);
            // final String uri = "drawable/" + source;
            // final int imageResource = getResources().getIdentifier(uri, null, getContext().getPackageName());
            final int imageResource = getResources().getIdentifier(source, "drawable", getContext().getPackageName());
            //  If image does not exists - otherwise an exception is thrown.
            if (imageResource == 0)
                return null;
            BitmapDrawable drawable = null;
            Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), imageResource);
            drawable = new BitmapDrawable(getContext().getResources(), bm);
            drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight() + 5));
            drawable.setGravity(Gravity.CENTER_VERTICAL);
            return drawable;
        }
    };

    private TagHandler tagHandler = new TagHandler() {

        private int start = 0;
        private int end = 0;
        final HashMap<String, String> attributes = new HashMap<String, String>();

        /**
         *  Process and collect any attributes found in the xml.
         *
         */
        private void processAttributes(final XMLReader xmlReader) {
            try {
                Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
                elementField.setAccessible(true);
                Object element = elementField.get(xmlReader);
                if (element != null) {
                    Field attsField = element.getClass().getDeclaredField("theAtts");
                    attsField.setAccessible(true);
                    Object atts = attsField.get(element);
                    Field dataField = atts.getClass().getDeclaredField("data");
                    dataField.setAccessible(true);
                    String[] data = (String[]) dataField.get(atts);
                    Field lengthField = atts.getClass().getDeclaredField("length");
                    lengthField.setAccessible(true);
                    int len = (Integer) lengthField.get(atts);
                    /**
                     *  Look for supported attributes and add to hash map. This is as tight as things can get :) The data index is "just" where
                     * the keys and values are stored.
                     */
                    for (int i = 0; i < len; i++) {
                        attributes.put(data[i * 5 + 1], data[i * 5 + 4]);
                    }
                }
            } catch (Exception e) {
                Logger.e(TAG, "Exception: ", e);
            }
        }

        /**
         * Retrieve and return request attribute by name.
         *
         */
        private String getAttribute(final String name) {
            return attributes.get(name);
        }

        @Override
        public void handleTag(boolean opening, final String tag, Editable output, final XMLReader xmlReader) {
            // //Log.d(TAG, "handleTag: " + tag);
            processAttributes(xmlReader);

            // This is clickable and red. Note that we use indexOf so that we
            // can have an postfix on the tag ie big-name123 and 123 will be the
            // postfix value.
            if (DARK_GRAY_REGULAR.equals(tag)) {
                if (opening) {
                    start = output.length();
                } else {
                    end = output.length();
                    // //Log.d(TAG, "start, end: " + start + ", " + end);
                    output.setSpan(new CustomTypefaceSpan(MyApplication.fontHelveticaRegular), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.dark_gray)), start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//					output.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.text_size_13)), start, end,
//							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//					if (textClickedListener != null) {
//						//  Trigger attached listener when clicked.
//						final CustomClickableSpan clickableSpan = new CustomClickableSpan() {
//							@Override
//							public void onClick(View view) {
//								//  We can also get the id from the tag
//								// Log.d(TAG, "attribute Id: " + getTag());
//								view.setTag(getTag());
//								textClickedListener.onClick(view);
//							}
//						};
//						clickableSpan.setTag(getAttribute("user_id"));
//						output.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//					}
                    // else
                    // //Log.w(TAG, "No listener attached!");
                }
            } else if (BLUE_REGULAR.equals(tag)) {
                if (opening) {
                    start = output.length();
                } else {
                    end = output.length();
                    // //Log.d(TAG, "start, end: " + start + ", " + end);
                    output.setSpan(new CustomTypefaceSpan(MyApplication.fontHelveticaRegular), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimary)), start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else if (DARK_BLUE_BOLD.equals(tag)) {
                if (opening) {
                    start = output.length();
                    // //Log.d(TAG, "start: " + start);
                } else {
                    end = output.length();
                    // //Log.d(TAG, "start, end: " + start + ", " + end);
                    output.setSpan(new CustomTypefaceSpan(MyApplication.fontHelveticaBold), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)), start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else if (LIGHT_GREY.equals(tag)) {
                if (opening) {
                    start = output.length();
                    // //Log.d(TAG, "start: " + start);
                } else {
                    end = output.length();
                    // //Log.d(TAG, "start, end: " + start + ", " + end);
                    output.setSpan(new CustomTypefaceSpan(MyApplication.fontHelveticaRegular), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorAccent)), start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else if (TERMS_OF_USE.equals(tag)) {
                if (opening) {
                    start = output.length();
                } else {
                    end = output.length();
                    // //Log.d(TAG, "start, end: " + start + ", " + end);
                    output.setSpan(new CustomTypefaceSpan(MyApplication.fontHelveticaRegular), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimary)), start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.setSpan(new CustomClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            //  We can also get the id from the tag
                            // Log.d(TAG, "attribute Id: " + getTag());
                            if (termsAndPrivacyClickedListener != null) {
                                termsAndPrivacyClickedListener.onClickTerms(view);
                            }
                        }
                    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else if (PRIVACY_POLICY.equals(tag)) {
                if (opening) {
                    start = output.length();
                } else {
                    end = output.length();
                    // //Log.d(TAG, "start, end: " + start + ", " + end);
                    output.setSpan(new CustomTypefaceSpan(MyApplication.fontHelveticaRegular), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimary)), start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//						//  Trigger attached listener when clicked.
                    output.setSpan(new CustomClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            //  We can also get the id from the tag
                            // Log.d(TAG, "attribute Id: " + getTag());
                            if (termsAndPrivacyClickedListener != null) {
                                termsAndPrivacyClickedListener.onClickPolicy(view);
                            }
                        }
                    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else if (BLACK_REGULAR.equals(tag)) {
                if (opening) {
                    start = output.length();
                    // //Log.d(TAG, "start: " + start);
                } else {
                    end = output.length();
                    // //Log.d(TAG, "start, end: " + start + ", " + end);
                    output.setSpan(new CustomTypefaceSpan(MyApplication.fontHelveticaRegular), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.dark_gray)), start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    };

    /**
     * Remove underline. http://thanksmister.com/2011/05/20/android-remove-underline -from-clickable-text-in-textview/ Also supports setTag
     */
    public class CustomClickableSpan extends ClickableSpan {
        private Object tag;

        @Override
        public void onClick(View widget) {
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setUnderlineText(false);
        }

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }
    }

    /**
     * Change type face based on constructor value.
     */
    public class CustomTypefaceSpan extends MetricAffectingSpan {
        final Typeface typeface;

        public CustomTypefaceSpan(final Typeface typeface) {
            this.typeface = typeface;
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            p.setAntiAlias(true);
            p.setTypeface(this.typeface);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setAntiAlias(true);
            tp.setTypeface(this.typeface);
        }
    }

    /**
     * Easy String.format functionality when working with place holders. This will trigger setText so syntax parsing is run to ensure new text is
     * correctly formatted.
     *
     * @param args
     */
    public void setArgs(final Object... args) {
        setText("");
        setText(String.format(this.orgText, args));
    }

    public void setText(final String format, final Object... args) {
        setText(String.format(format, args));
    }
}
