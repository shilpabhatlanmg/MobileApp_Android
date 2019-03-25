package com.protectapp.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.pm.PackageInfoCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.protectapp.R;
import com.protectapp.model.BeaconEvent;
import com.protectapp.model.GetLocationData;
import com.protectapp.model.Incident;
import com.protectapp.model.SpanData;

import org.altbeacon.beacon.Beacon;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AppCommons {
    public static String convertMillistoMinutesSeconds(long millis) {
        int seconds = (int) (millis / 1000);
        int minutesLeft = seconds / 60;
        int secondsLeft = seconds - minutesLeft * 60;
        String minS = minutesLeft / 10 == 0 ? "0" + minutesLeft : String.valueOf(minutesLeft);
        String secS = secondsLeft / 10 == 0 ? "0" + secondsLeft : String.valueOf(secondsLeft);
        return minS + ":" + secS;
    }

    public static final SpannableString getSpannedString(SpanData... spanDataArr) {
        if (spanDataArr == null) return null;
        int cur_index = 0;
        int span_string_length = 0;
        SpannableString content = new SpannableString(getAppendedString(spanDataArr));
        for (final SpanData spanData : spanDataArr) {
            span_string_length += spanData.getString().length();
            content.setSpan(new ForegroundColorSpan(spanData.getColor()), cur_index, span_string_length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(new RelativeSizeSpan(spanData.getRelativeSize()), cur_index, span_string_length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (spanData.getOnClickListener() != null) {
                content.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Log.d("SpanData", "on click");
                        spanData.getOnClickListener().onClick(widget);
                    }

                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);

                    }
                }, cur_index, span_string_length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (spanData.isBold())
                content.setSpan(new StyleSpan(Typeface.BOLD), cur_index, span_string_length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            cur_index = span_string_length;
        }

        return content;


    }

    public static final String getAppendedString(SpanData... spanDataArr) {
        String spanString = "";

        for (SpanData spanData : spanDataArr) {
            spanString += spanData.getString();
        }
        return spanString;
    }

    public static final boolean isVisitor(Context context) {
        return context.getString(R.string.app_flavor).equals(Constants.FLAVOR.VISITOR);
    }

    public static final boolean isSecurity(Context context) {
        return context.getString(R.string.app_flavor).equals(Constants.FLAVOR.SECURITY);
    }

    public static final String getIncidentTypeName(Context context, int type) {
        switch (type) {
            case Constants.INCIDENT.TYPE_MEDICAL:
                return context.getResources().getString(R.string.medical);
            case Constants.INCIDENT.TYPE_FIRE:
                return context.getResources().getString(R.string.fire);
            case Constants.INCIDENT.TYPE_POLICE:
                return context.getResources().getString(R.string.police);
            case Constants.INCIDENT.TYPE_ASSIST:
                return context.getResources().getString(R.string.assist);
        }

        return "";
    }

    public static final int getIncidentColorBGRes(int type) {
        switch (type) {
            case Constants.INCIDENT.TYPE_MEDICAL:
                return R.drawable.medical_incident_strip_bg;
            case Constants.INCIDENT.TYPE_FIRE:
                return R.drawable.fire_incident_strip_bg;
            case Constants.INCIDENT.TYPE_POLICE:
                return R.drawable.police_incident_strip_bg;
            case Constants.INCIDENT.TYPE_ASSIST:
                return R.drawable.assist_incident_strip_bg;
            default:
                return R.drawable.medical_incident_strip_bg;
        }

    }

    public static final DisplayImageOptions getUserImageLoadingOptions() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_user_img_placeholder)
                .showImageForEmptyUri(R.drawable.ic_user_img_placeholder)
                .showImageOnFail(R.drawable.ic_user_img_placeholder)
                .build();
        return imageOptions;

    }


    public static final List<Incident> processIncidentListWithDateHeader(Incident lastIncident, List<Incident> incidentList) {
        List<Incident> processedList = new ArrayList<>();
        try {
            String prevDate = "";
            if (lastIncident != null)
                prevDate = toLocalWebDate(lastIncident.getTimestamp()).split(" ")[0];
            for (Incident incident : incidentList) {

                String[] timestamp = toLocalWebDate(incident.getTimestamp()).split(" ");
                String date = timestamp[0];
                String time = timestamp[1];
                incident.setDisplayableDate(toDisplayableDate(date));
                incident.setDisplayableTime(toDisplayableTime(time));
                if (!date.equals(prevDate)) {
                    prevDate = date;
                    Incident dateHeaderIncident = new Incident();
                    dateHeaderIncident.setDateHeader(true);
                    dateHeaderIncident.setDisplayableDate(incident.getDisplayableDate());
                    dateHeaderIncident.setDisplayableTime(incident.getDisplayableTime());
                    dateHeaderIncident.setTimestamp(incident.getTimestamp());
                    processedList.add(dateHeaderIncident);
                }


                processedList.add(incident);

            }

        } catch (Exception e) {

        } finally {
            return processedList;
        }


    }

    public static final List<Incident> processIncidentList(List<Incident> incidentList) {
        List<Incident> processedList = new ArrayList<>();
        try {
                for (Incident incident : incidentList) {
                    String[] timestamp = toLocalWebDate(incident.getTimestamp()).split(" ");
                    String date = timestamp[0];
                    String time = timestamp[1];
                    incident.setDisplayableDate(toDisplayableDate(date));
                    incident.setDisplayableTime(toDisplayableTime(time));
                    processedList.add(incident);

                }

        } catch (Exception e) {

        } finally {
            return processedList;
        }


    }

    public static final String toDisplayableDate(String localWebDate) {
        String displayableDate = "";
        try {
            SimpleDateFormat webFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat appFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            Date date = webFormat.parse(localWebDate);
            displayableDate = appFormat.format(date);
        } catch (Exception e) {

        } finally {
            return displayableDate;
        }

    }

      public static final String toDisplayableTime(String localWebTime) {
        String displayableTime = "";
        try {
            SimpleDateFormat webFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            SimpleDateFormat appFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
            Date date = webFormat.parse(localWebTime);
            displayableTime = appFormat.format(date).toLowerCase();
        } catch (Exception e) {

        } finally {
            return displayableTime;
        }

    }
    public static final String toDisplayableDateTime(String utcWebDate)
    {

        String displayableDateTime = "";
        try {
            SimpleDateFormat webFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'", Locale.US);
            webFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat appFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
            appFormat.setTimeZone(TimeZone.getDefault());
            Date date = webFormat.parse(utcWebDate);
            displayableDateTime = appFormat.format(date);
        } catch (Exception e) {

        } finally {
            return displayableDateTime;
        }

    }
    public static final String toLocalWebDate(String webDate)
    {

        String localWebDate = "";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            localFormat.setTimeZone(TimeZone.getDefault());
            Date utcDate = utcFormat.parse(webDate);
            localWebDate = localFormat.format(utcDate);
        } catch (Exception e) {

        } finally {
            return localWebDate;
        }

    }
    public static final String extractContactNumber(String number) {
        return number.replace(Constants.COUNTRY_CODE + " ", "");
    }

    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }

    public static final String getLocationDisplayName(GetLocationData locationData) {
        String locationDisplayName="";

        if(locationData.getOrganization()!=null && locationData.getOrganization().getName()!=null)
            locationDisplayName+=locationData.getOrganization().getName();

        if(locationData.getPremise()!=null)
            locationDisplayName+=(locationDisplayName.length()>0 ? ", " : "") + locationData.getPremise();

        if(locationData.getLocationName()!=null)
            locationDisplayName+=(locationDisplayName.length()>0 ? ", " : "") + locationData.getLocationName();


        return locationDisplayName;
    }
public static final String generateConfirmReportMsg(Context context,int incidentType)
{
    String msg = context.getString(R.string.confirm_report_are_you_sure)+" ";
    switch (incidentType)
    {
        case Constants.INCIDENT.TYPE_FIRE:msg+=context.getString(R.string.confirm_report_message_fire);break;
        case Constants.INCIDENT.TYPE_ASSIST:msg+=context.getString(R.string.confirm_report_message_assist);break;
        case Constants.INCIDENT.TYPE_MEDICAL:msg+=context.getString(R.string.confirm_report_message_medical);break;
        case Constants.INCIDENT.TYPE_POLICE:msg+=context.getString(R.string.confirm_report_message_police);break;

    }
    msg +=" "+context.getString(R.string.confirm_report_at_your_location);
    return msg;
}


    /**
     * Returns current date in format yyyy-MM-dd HH:mm
     *
     * @return
     */
    public static String getCurrentDate() {
        String dateString = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            dateString = formatter.format(new Date());
        } catch (Exception e) {

        }

        return dateString;
    }
    public static final String getChatDateFlag(String webDate) {
        String chatDate = "";
        if (webDate==null)return chatDate;
        try {
            SimpleDateFormat webFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = webFormat.parse(webDate);
                 chatDate= getChatDateFlag(date.getTime());
        } catch (Exception e) {

        } finally {
            return chatDate;
        }

    }
    public static final String toWebDate(long timeMillis)
    {
        String webDate="";
        try
        {
            Date date = new Date(timeMillis);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getDefault());
            webDate = formatter.format(date);
        }
        catch (Exception e)
        {

        }
       return webDate;

    }
    public static final String getChatOnlyTime(long timeMillis)
    {
        String chatTime="";
        try
        {
            Date date = new Date(timeMillis);
            DateFormat formatter = new SimpleDateFormat("hh:mm aa");
            formatter.setTimeZone(TimeZone.getDefault());
            chatTime = formatter.format(date);
        }
        catch (Exception e)
        {

        }
        return chatTime;

    }
    public static final String getChatDateFlag(long timeMillis)
    {
        String chatDateFlag="";
        if (timeMillis==0)return "";
        try
        {
            Calendar now = Calendar.getInstance();
            chatDateFlag = DateUtils.getRelativeTimeSpanString(timeMillis, now.getTimeInMillis(), DateUtils.DAY_IN_MILLIS).toString();


        }
        catch (Exception e)
        {

        }
        return chatDateFlag;

    }
    public static final String getChatDateFlagWithTime(long timeMillis)
    {
        String chatDateFlag="";
        if (timeMillis==0)return "";
        try
        {
            Calendar now = Calendar.getInstance();
            chatDateFlag = DateUtils.getRelativeTimeSpanString(timeMillis, now.getTimeInMillis(), DateUtils.DAY_IN_MILLIS).toString();
            chatDateFlag += ", "+getChatOnlyTime(timeMillis);

        }
        catch (Exception e)
        {

        }
        return chatDateFlag;

    }
    public static final int  generateChatNotificationCode(int user_id_1,int user_id_2)
    {

        return user_id_1>user_id_2 ? user_id_1*String.valueOf(user_id_2).length()*10+user_id_2 :
                user_id_2*String.valueOf(user_id_1).length()*10+user_id_1;


    }
    public static final Bitmap getBitmap(Context context,Uri imageUri)
    {
        try {
            InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            return BitmapFactory.decodeStream(imageStream);
        }
        catch (Exception e)
        {

        }
        return null;
    }
    public static final  String toBase64(Context context,Uri imageUri)
    {
        if (imageUri!=null)
        {
            Bitmap bitmap = getBitmap(context,imageUri);
            if (bitmap!=null)
                return encodeImage(bitmap);

        }

        return "";
    }
    public static final String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public static final void showToast(Context context,int message)
    {
        new AppToastBuilder(context)
                .setMessage(context.getString(message))
                .build()
                .show();
    }
    public static final void showToast(Context context,String message)
    {
        new AppToastBuilder(context)
                .setMessage(message)
                .build()
                .show();
    }
    public static final void showError(Context context,String message)
    {
        new AppToastBuilder(context)
                .setMessage(message!=null ? message : context.getString(R.string.something_went_wrong))
                .build()
                .show();
    }
    public static void callUser(Context context,String mobile)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+mobile));
        context.startActivity(intent);
    }

    public static String getVersionName(Context context)
    {

        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
           return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown-01";
        }
    }

    public static final BeaconEvent toBeaconEvent(Beacon beacon) {
        BeaconEvent beaconEvent = new BeaconEvent();

        if (beacon == null) {
            beaconEvent.setMajorID(null);
            beaconEvent.setMinorID(null);
        } else {
            beaconEvent.setMajorID(beacon.getId2().toString());
            beaconEvent.setMinorID(beacon.getId3().toString());
            beaconEvent.setBeaconDistance(beacon.getDistance());
        }

        return beaconEvent;
    }
}
