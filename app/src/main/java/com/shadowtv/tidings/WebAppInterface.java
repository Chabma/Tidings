package com.shadowtv.tidings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * Created by Cameron Abma on 7/16/2015.
 */
public class WebAppInterface {

    ImageView img;
    Context mContext;
    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }



    @JavascriptInterface
    public void webSearch(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(browserIntent);
    }

    @JavascriptInterface
    public String VideoPlay(String searchTerms, int[] channels, String token) {
        Calendar now = Calendar.getInstance();
        int nowYear = now.get(Calendar.YEAR);

        String nowMonth;
        if(now.get(Calendar.MONTH) + 1 > 12){nowMonth = "00";}
        else if(now.get(Calendar.MONTH) + 1 < 10){nowMonth = "0"+(now.get(Calendar.MONTH) + 1);}
        else{nowMonth  = Integer.toString(now.get(Calendar.MONTH) + 1);}

        String nowDay;
        if(now.get(Calendar.DAY_OF_MONTH) < 10){nowDay = "0"+now.get(Calendar.DAY_OF_MONTH);}
        else{nowDay  = Integer.toString(now.get(Calendar.DAY_OF_MONTH));}

        String nowHour;
        if(now.get(Calendar.HOUR_OF_DAY) + 3 < 0){nowHour = "23";}
        else if(now.get(Calendar.HOUR_OF_DAY) + 3 < 10){nowHour = "0"+(now.get(Calendar.HOUR_OF_DAY) + 3);}
        else{nowHour  = Integer.toString(now.get(Calendar.HOUR_OF_DAY) + 3);}

        String nowMinute;
        if(now.get(Calendar.MINUTE) < 10){nowMinute = "0"+now.get(Calendar.MINUTE);}
        else{nowMinute  = Integer.toString(now.get(Calendar.MINUTE));}

        String nowSecond;
        if(now.get(Calendar.SECOND) < 10){nowSecond = "0"+now.get(Calendar.SECOND);}
        else{nowSecond  = Integer.toString(now.get(Calendar.SECOND));}


        String UTCString = nowYear+"-"+nowMonth+"-"+nowDay+"T"+nowHour+":"+nowMinute+":"+nowSecond+".000Z";

        JSONArray channelList = new JSONArray();
        for (int x = 0; x < channels.length; x++) {
            channelList.put(channels[x]);
        }
        try {
            URL url;
            URLConnection urlConn;
            DataOutputStream printout;
            InputStream input;
            url = new URL("http://api.shadowtv.net/reperio/v1/search");
            urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.addRequestProperty("Authorization", token);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.connect();

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("query", searchTerms);

                JSONObject jsonChannels = new JSONObject();
                jsonChannels.put("channels", channelList);
            jsonParam.put("filter", jsonChannels);

                JSONObject jsonDateRange = new JSONObject();
                jsonDateRange.put("lowerDateUTC", UTCString);
            jsonParam.put("dataRange", jsonDateRange);

                JSONObject jsonInclude = new JSONObject();
                jsonInclude.put("guide", false );
                jsonInclude.put("captions", false);
                jsonInclude.put("body", false);
            jsonParam.put("include", jsonInclude);
            // Send POST output.
            Log.d("TAG", jsonParam.toString());

            printout = new DataOutputStream(urlConn.getOutputStream());
            String str = jsonParam.toString();
            byte[] data = str.getBytes("UTF-8");
            printout.write(data);
            printout.flush();
            printout.close();

            input = urlConn.getInputStream();
            Log.d("TAG", "MADE CONNECTION LINK - SEARCH");
            BufferedReader rd = new BufferedReader(new InputStreamReader(input));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            input.close();

            //Log.d("TAG", "return message : " + response);
            Log.d("TAG", "CONNECTION COMPLETED - SEARCH");
            //turning text response to json object and getting elements from that object
            JSONObject respJSON = new JSONObject(response.toString());
            //Log.d("TAG", "return body : " + respJSON.getString("body"));
            JSONArray hits = respJSON.getJSONArray("hits");
            Log.d("TAG", String.valueOf(hits.length()));
            JSONObject vidHits = hits.getJSONObject(0);
            String vidDate = vidHits.getString("startDateUTC");
            JSONObject channelInfo = vidHits.getJSONObject("channel");
            String vidChannel = channelInfo.getString("channel");

            String link = "http://api.shadowtv.net/reperio/v1/links/" + vidChannel + "/" + vidDate + "/30";
            url = new URL(link);
            urlConn = url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.addRequestProperty("Authorization", token);
            urlConn.connect();


            input = urlConn.getInputStream();
            rd = new BufferedReader(new InputStreamReader(input));
            response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            input.close();

            Log.d("TAG", "CONNECTION OCCURRED - LINK");
            //turning text response to json object and getting elements from that object
            respJSON = new JSONObject(response.toString());
            Log.d("TAG", link);
            Log.d("TAG", respJSON.getJSONArray("links").toString());
            return respJSON.toString();

        } catch (IOException e) {
            Log.d("TAG", "failed to create inputStream (probably)");
            Log.d("TAG", e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("TAG", "Something else went wrong in text");
            e.printStackTrace();
        }

        return null;
    }
}