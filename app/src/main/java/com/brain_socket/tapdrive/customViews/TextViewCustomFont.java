package com.brain_socket.tapdrive.customViews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.utils.LocalizationHelper;


/**
 * <p> This is a normal TextView widget that can have a custom Font.
 * The fonts are referenced by an id listed below:
 * <p/>
 * <p> font dinarOneLight: fontId = 1
 * <p> font scDubai: fontId = 2
 * <p/>
 * <p> The fontId attribute can be set in the XML definition of the custom TextView
 * by setting app:fontId="id"
 *
 * @author Nabil Souk
 */
public class TextViewCustomFont extends TextView {

    // fonts
    public static Typeface fontFaceRegular = null;
    public static Typeface fontFaceBold = null;

    public TextViewCustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextViewCustomFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        try {
            if (!isInEditMode()) {
                // get the typed array for the custom attrs
                TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
                // get fontId set in the XML
                int fontId = a.getInteger(R.styleable.CustomFontTextView_fontId, 0);
                // check fontId if equal to any or the predefined ids for the custom fonts
                switch (fontId) {
                    case 1:
                        this.setTypeface(getTFThin(getContext()));
                        break;
                    case 2:
                        this.setTypeface(getTFRegular(getContext()));
                        break;
                    case 3:
                        this.setTypeface(getTFBold(getContext()));
                        break;
                    default:
                        break;
                }

                //Don't forget this
                a.recycle();
            }
        } catch (Exception ignored) {
        }
    }

    public static Typeface getTFThin(Context context) {
        try {
            if (fontFaceRegular == null) {

                String locale = LocalizationHelper.getCurrentLocale();

                if (!locale.equalsIgnoreCase("")) {
                    if (locale.equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                        fontFaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/gotham-light.ttf");
                    } else {
                        fontFaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/DroidKufi-Regular.ttf");
                    }
                } else {
                    if (LocalizationHelper.getDeviceLocale().equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                        fontFaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/gotham-light.ttf");
                    } else {
                        fontFaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/DroidKufi-Regular.ttf");
                    }
                }

            }
        } catch (Exception e) {
            fontFaceRegular = Typeface.DEFAULT;
        }
        return fontFaceRegular;
    }

    public static Typeface getTFRegular(Context context) {
        try {
            if (fontFaceRegular == null) {

                String locale = LocalizationHelper.getCurrentLocale();

                if (!locale.equalsIgnoreCase("")) {
                    if (locale.equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                        fontFaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
                    } else {
                        fontFaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/DroidKufi-Regular.ttf");
                    }
                } else {
                    if (LocalizationHelper.getDeviceLocale().equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                        fontFaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
                    } else {
                        fontFaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/DroidKufi-Regular.ttf");
                    }
                }

            }
        } catch (Exception e) {
            fontFaceRegular = Typeface.DEFAULT;
        }
        return fontFaceRegular;
    }

    public static Typeface getTFBold(Context context) {
        try {
            if (fontFaceBold == null) {

                String locale = LocalizationHelper.getCurrentLocale();

                if (!locale.equalsIgnoreCase("")) {
                    if (locale.equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                        fontFaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Bold.ttf");
                    } else {
                        fontFaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/DroidKufi-Bold.ttf");
                    }
                } else {
                    if (LocalizationHelper.getDeviceLocale().equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                        fontFaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Bold.ttf");
                    } else {
                        fontFaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/DroidKufi-Bold.ttf");
                    }
                }

            }
        } catch (Exception e) {
            fontFaceBold = Typeface.DEFAULT;
        }
        return fontFaceBold;
    }
}
