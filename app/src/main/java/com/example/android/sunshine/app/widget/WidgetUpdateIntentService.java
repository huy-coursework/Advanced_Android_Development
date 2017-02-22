package com.example.android.sunshine.app.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WidgetUpdateIntentService extends IntentService {
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP
    };
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_SHORT_DESC = 1;
    private static final int INDEX_MAX_TEMP = 2;

    public WidgetUpdateIntentService()
    {
        super("WidgetUpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(
                        new ComponentName(this, TodayWidgetProvider.class));

        Cursor cursor = getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        Utility.getPreferredLocation(this),
                        System.currentTimeMillis()),
                FORECAST_COLUMNS,
                null,
                null,
                WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
        if (cursor != null && cursor.moveToFirst())
        {
            int weatherArtResourceId =
                    Utility.getArtResourceForWeatherCondition(cursor.getInt(INDEX_WEATHER_ID));
            String shortDescription = cursor.getString(INDEX_SHORT_DESC);
            double highTemperature = cursor.getDouble(INDEX_MAX_TEMP);

            for (int appWidgetId : appWidgetIds)
            {
                RemoteViews remoteViews =
                        new RemoteViews(getPackageName(), R.layout.widget_today_small);
                remoteViews.setImageViewResource(R.id.widget_icon, weatherArtResourceId);
                remoteViews.setTextViewText(
                        R.id.widget_high_temperature,
                        Utility.formatTemperature(this, highTemperature));
                // Content Descriptions for RemoteViews were only added in ICS MR1
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(remoteViews, shortDescription);
                }

                Intent launchIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
}
