package com.example.android.sunshine.app.muzei;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;
import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;

public class WeatherMuzeiSource extends MuzeiArtSource {
    private static final String[] WEATHER_PROJECTION = new String[] {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
    };
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_SHORT_DESC = 1;

    public WeatherMuzeiSource() {
        super(WeatherMuzeiSource.class.getName());
    }

    @Override
    protected void onUpdate(int reason) {
        String locationQuery = Utility.getPreferredLocation(this);
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                locationQuery,
                System.currentTimeMillis());
        Cursor cursor = getContentResolver().query(
                weatherUri,
                WEATHER_PROJECTION,
                null,
                null,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int weatherId = cursor.getInt(INDEX_WEATHER_ID);
                String desc = cursor.getString(INDEX_SHORT_DESC);
                String imageUrl = Utility.getImageUrlForWeatherCondition(weatherId);
                if (imageUrl != null) {
                    publishArtwork(new Artwork.Builder()
                            .imageUri(Uri.parse(imageUrl))
                            .title(desc)
                            .byline(locationQuery)
                            .viewIntent(new Intent(this, MainActivity.class))
                            .build());
                }
            }
            cursor.close();
        }
    }
}
