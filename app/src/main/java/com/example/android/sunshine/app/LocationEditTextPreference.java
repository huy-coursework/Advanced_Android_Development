package com.example.android.sunshine.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class LocationEditTextPreference extends EditTextPreference {
    private static final int DEFAULT_MINIMUM_LOCATION_LENGTH = 2;

    private int mMinLength;

    public LocationEditTextPreference(Context context, AttributeSet attrs) {
        super(context);
        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LocationEditTextPreference,
                0,
                0);
        try {
            mMinLength =
                    styledAttributes.getInteger(R.styleable.LocationEditTextPreference_minLength,
                                                DEFAULT_MINIMUM_LOCATION_LENGTH);
        } finally {
            styledAttributes.recycle();
        }
    }
}
