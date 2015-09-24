package com.shadowtv.tidings;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.LoginFilter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewStub;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;


import com.aphidmobile.flip.FlipViewController;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;



    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public static Object SPLASH_LOCK = new Object();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        startActivity(new Intent(this, SplashActivity.class));

        mTitle = getTitle();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void sendFeedback(View button) {
        // update the main content by replacing fragments ( this is for polls and pies)
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.d("TAG", "THIS SI THE FRAGMENT ACTIVITY: " + fragmentManager.findFragmentById(R.id.container).getActivity());
        Log.d("TAG", "THIS SI THE FRAGMENT View: " + fragmentManager.findFragmentById(R.id.container).getView());
        String ChoiceOne = Integer.toString(((Spinner) fragmentManager.findFragmentById(R.id.container).getView().findViewById(R.id.SpinnerFeedbackType)).getSelectedItemPosition());
        RadioGroup group = (RadioGroup) fragmentManager.findFragmentById(R.id.container).getView().findViewById(R.id.radioGroup);
        String ChoiceTwo = Integer.toString(group.indexOfChild((RadioButton) group.findViewById(group.getCheckedRadioButtonId())));
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(30, ChoiceOne, ChoiceTwo))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Tidings");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            menu.clear();
            Log.d("TAG", "currently selected pos" + mNavigationDrawerFragment.getmCurrentSelectedPosition() + "");
            if(mNavigationDrawerFragment.getmCurrentSelectedPosition() + 1 == 2) {
                //if the position is bars graph then use bars graph menu
                getMenuInflater().inflate(R.menu.bars_menu, menu);
            }
            else {
                getMenuInflater().inflate(R.menu.home, menu);
            }
            //restoreActionBar();
            return true;
        }
        menu.clear();
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.bar_by_day:
                ((PlaceholderFragment)fragmentManager.findFragmentById(R.id.container)).changeCurrentItem(0);
                return true;
            case R.id.bar_by_week:
                ((PlaceholderFragment)fragmentManager.findFragmentById(R.id.container)).changeCurrentItem(1);
                return true;
            case R.id.bar_by_month:
                ((PlaceholderFragment)fragmentManager.findFragmentById(R.id.container)).changeCurrentItem(2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a web view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        // save a reference so custom methods
        // can access views
        private View topLevelView;
        private View currentView;

        // save a reference to show the d3
        private WebView webview;

        public String[] results = new String[3];
        public String tToken = "";

        public String[] barDayData = new String[7];
        public String[] barWeekData = new String[5];
        public String[] barMonthData = new String[2];
        public String[][] barAllData = new String[3][];

        public JSONArray donutResults = new JSONArray();
        public View firstDonutWebView;
        public JSONArray formResults = new JSONArray();

        public String FormData;


        MyAdapter mAdapter;

        ViewPager mPager;

        static final int NUM_ITEMS = 3;





        public ArrayList<String> donuts = new ArrayList<String>();



        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public static PlaceholderFragment newInstance(int sectionNumber, String choiceOne, String choiceTwo) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            if (sectionNumber == 30) {
                args.putString("candidateChoice", choiceOne);
                args.putString("weeklyChoice", choiceTwo);
                Log.d("TAG", "ChoiceOne: " + choiceOne);
                Log.d("TAG", "ChoiceTwo: " + choiceTwo);
            }
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        public void changeCurrentItem(int position){
            mPager.setCurrentItem(position);
        }


        //define webRequestThread
        Thread webRequestThread = new Thread(){
            @Override
            public void run() {
                try {
                    URL url;
                    URLConnection urlConn;
                    DataOutputStream printout;
                    InputStream input;
                    url = new URL("http://api.shadowtv.net/reperio/login");
                    urlConn = url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.connect();

                    //Create JSONObject here
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("username", "shadowtv");
                    jsonParam.put("password", "630ninth");
                    // Send POST output.
                    printout = new DataOutputStream(urlConn.getOutputStream());
                    String str = jsonParam.toString();
                    byte[] data = str.getBytes("UTF-8");
                    printout.write(data);
                    printout.flush();
                    printout.close();

                    //Retrieve Input
                    input = urlConn.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(input));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    input.close();

                    Log.d("TAG", "CONNECTION OCCURRED Login");
                    Log.d("TAG", "return message : " + response);
                    tToken = response.substring(15,79);

                    //GETTING TEXT
                    results[0] = getText("12", tToken,0);
                    results[1] = getText("10", tToken,0);
                    results[2] = getText("3", tToken,0);
                }

                //EXCEPTIONS
                catch (IOException e) {
                    Log.d("TAG", "failed to create outputStream (probably)");
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    Log.d("TAG", "something with json messed up");
                    e.printStackTrace();
                }
                catch (Exception e){
                    Log.d("TAG", "Something else went wrong");
                    e.printStackTrace();
                }
            }
        };


        Thread webRequestThreadBAR = new Thread(){
            @Override
            public void run() {
                barDayData[0] = getBarData( "d", 0);
                Log.d("TAG", barDayData[0]);
                barDayData[1] = getBarData( "d", 1);
                Log.d("TAG", barDayData[1]);
                barDayData[2] = getBarData( "d", 2);
                Log.d("TAG", barDayData[2]);
                barDayData[3] = getBarData( "d", 3);
                Log.d("TAG", barDayData[3]);
                barDayData[4] = getBarData( "d", 4);
                barDayData[5] = getBarData( "d", 5);
                barDayData[6] = getBarData( "d", 6);
                barWeekData[0] = getBarData( "w", 0);
                barWeekData[1] = getBarData( "w", 1);
                barWeekData[2] = getBarData( "w", 2);
                barWeekData[3] = getBarData( "w", 3);
                barWeekData[4] = getBarData( "w", 4);
                barMonthData[0] = getBarData( "m", 0);
                barMonthData[1] = getBarData( "m", 1);

                barAllData[0] = barDayData;
                barAllData[1] = barWeekData;
                barAllData[2] = barMonthData;
            }
        };

        Thread webRequestThreadForm = new Thread(){
            @Override
            public void run() {

                FormData = getFormData(0);
            }
        };

        Thread webRequestThreadDonutGET = new Thread(){
            @Override
            public void run() {
                try{
                    donutResults.put(0, new JSONObject(getDonutData(0)));
                    donutResults.put(1, new JSONObject(getDonutData(1)));
                    donutResults.put(2, new JSONObject(getDonutData(2)));
                    donutResults.put(3, new JSONObject(getDonutData(3)));
                    donutResults.put(4, new JSONObject(getDonutData(4)));
                    formResults.put(0, new JSONObject(getFormData(0)));
                    formResults.put(1, new JSONObject(getFormData(1)));
                    formResults.put(2, new JSONObject(getFormData(2)));
                    formResults.put(3, new JSONObject(getFormData(3)));
                    formResults.put(4, new JSONObject(getFormData(4)));
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        };


        Thread webRequestThreadDonut = new Thread(){
            @Override
            public void run() {
                try {
                    URL url;
                    URLConnection urlConn;
                    InputStream input;
                    url = new URL(" http://brazil.shadowtv.com/server");

                    urlConn = url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.connect();

                    HashMap<String, String> params = new HashMap<String, String>();
                    Log.d("TAG", topLevelView.toString());
                    params.put("candidate", getArguments().getString("candidateChoice"));
                    params.put("choice", getArguments().getString("weeklyChoice"));

                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getQuery(params));
                    writer.flush();
                    writer.close();
                    os.close();

                    //Retrieve Input
                    input = urlConn.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(input));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    input.close();

                    Log.d("TAG", "CONNECTION OCCURRED Donut");
                    Log.d("TAG", "return message : " + response);
                    donutResults.put(new JSONObject(response.toString()));
                    donutResults.put(1, new JSONObject(getDonutData(1)));
                    donutResults.put(2, new JSONObject(getDonutData(2)));
                    donutResults.put(3, new JSONObject(getDonutData(3)));
                    donutResults.put(4, new JSONObject(getDonutData(4)));
                    formResults.put(0, new JSONObject(getFormData(0)));
                    formResults.put(1, new JSONObject(getFormData(1)));
                    formResults.put(2, new JSONObject(getFormData(2)));
                    formResults.put(3, new JSONObject(getFormData(3)));
                    formResults.put(4, new JSONObject(getFormData(4)));


                }

                //EXCEPTIONS
                catch (IOException e) {
                    Log.d("TAG", "failed to create outputStream (probably)");
                    Log.d("TAG", "candidate choice :" + getArguments().getString("candidateChoice"));
                    e.printStackTrace();
                }
                catch (Exception e){
                    Log.d("TAG", "Something else went wrong");
                    e.printStackTrace();
                }
            }
        };


        private String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for(Map.Entry<String, String> pair : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }


        //when new view is made
        @Override
        public View onCreateView(
                LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState)
        {
            //do most of the default things
            super.onCreateView(inflater, container,
                    savedInstanceState);

            //inflate home fragment layout
            boolean attachToRoot = false;
            topLevelView = inflater.inflate(
                R.layout.fragment_home,
                container,
                attachToRoot);

            Log.d("TAG", "inflated home fragment");
            Log.d("TAG", "section number: "+ getArguments().getInt(ARG_SECTION_NUMBER));
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {


                //Url Connection
                webRequestThread.start();

                try {
                    //wait for webrequest to finish
                    webRequestThread.join();
                    initPieChart();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                webRequestThreadBAR.start();
                try {
                    //wait for webrequest to finish
                    webRequestThreadBAR.join();
                    mAdapter = new MyAdapter(getFragmentManager(), getActivity(), barAllData );
                    //Log.d("TAG", "first toplevelview: "+ topLevelView.toString());
                    mPager = (ViewPager)topLevelView.findViewById(R.id.pager);
                    mPager.setAdapter(mAdapter);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                    //initBarChart();

            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                Calendar now = Calendar.getInstance();
                String nowWeek;
                if(now.get(Calendar.WEEK_OF_YEAR) < 10){nowWeek = "0"+(now.get(Calendar.WEEK_OF_YEAR) );}
                else{nowWeek  = Integer.toString(now.get(Calendar.WEEK_OF_YEAR) );}
                Log.d("TAG", "real week is: " + nowWeek);
                SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String week = sharedPreferences.getString("nowWeek", "null");
                Log.d("TAG", "saved week is: " + week);
                if (!week.equals(nowWeek)) {
                    topLevelView = inflater.inflate(
                            R.layout.questionaire,
                            container,
                            attachToRoot);
                    //getFormData() will be called here (probably in a webrequestThread)
                    webRequestThreadForm.start();
                    try{
                        webRequestThreadForm.join();
                        // BELOW IS JUST SAMPLE STUFF TO SHOW EDITING THIS WOULD BE POSSIBLE
                        JSONObject tempObj = new JSONObject(FormData);

                        ((TextView) topLevelView.findViewById(R.id.TextViewTitle_two)).setText(tempObj.getString("question"));
                        //adding radio button below would be in a for loop
                        for(int i = 0; i < tempObj.getJSONArray("answers").length(); i++) {
                            RadioButton temp_butn = new RadioButton(this.getActivity());
                            temp_butn.setText(tempObj.getJSONArray("answers").getString(i));
                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            p.weight = 1;
                            temp_butn.setLayoutParams(p);
                            //temp_butn.setId("Choice" + i.toString());
                            ((RadioGroup) topLevelView.findViewById(R.id.radioGroup)).addView(temp_butn);
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nowWeek", nowWeek);
                        editor.putString("FormData", FormData);
                        editor.apply();
                        Log.d("TAG", "now saved week is: " + sharedPreferences.getString("nowWeek", "not correct still"));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PlaceholderFragment.newInstance(29))
                            .commit();
                }

            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 29){
                firstDonutWebView = null;
                View stub = topLevelView.findViewById(
                        R.id.flip_stub);
                /* add this in later for offline use
                if (donutResults == null){
                    SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String temp = sharedPreferences.getString("donutResults", "donut results error");
                    try {
                        donutResults = new JSONObject(temp);
                    }
                    catch (JSONException e){
                        Log.d("TAG", "JSON from SHARED PREFERENCES EXCEPTION");
                        e.printStackTrace();
                    }
                }*/
                webRequestThreadDonutGET.start();
                try {
                    webRequestThreadDonutGET.join();
                    // inflate stub by setting to visible
                    stub.setVisibility(View.VISIBLE);
                    FlipViewController flipView = (FlipViewController) topLevelView.findViewById(R.id.flip_view);

                    //We're creating a DonutChartAdapter instance, by passing in the current context and the
                    //values to display after each flip
                    donuts.add("yay");
                    donuts.add("this");
                    donuts.add("really");
                    donuts.add("works!!");
                    Adapter donutAdapter = new DonutChartAdapter(this.getActivity(), donuts);
                    flipView.setAdapter(donutAdapter);

                    SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("donutResults", donutResults.get(0).toString());
                    editor.apply();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 30) {
                firstDonutWebView = null;
                webRequestThreadDonut.start();

                try {
                    //wait for webrequest to finish
                    webRequestThreadDonut.join();
                    SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("donutResults", donutResults.get(0).toString());
                    editor.apply();
                    View stub = topLevelView.findViewById(
                            R.id.flip_stub);

                    // inflate stub by setting to visible
                    stub.setVisibility(View.VISIBLE);
                    FlipViewController flipView = (FlipViewController)topLevelView.findViewById(R.id.flip_view);

                    //We're creating a DonutChartAdapter instance, by passing in the current context and the
                    //values to display after each flip
                    donuts.add("yay");
                    donuts.add("this");
                    donuts.add("really");
                    donuts.add("works!!");
                    Adapter donutAdapter = new DonutChartAdapter(this.getActivity(), donuts);
                    flipView.setAdapter(donutAdapter);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            return topLevelView;
        }

        public class DonutChartAdapter extends BaseAdapter {
            private LayoutInflater inflater;
            private ArrayList<String> notes;

            public DonutChartAdapter(Context currentContext, ArrayList<String> allNotes ) {
                inflater = LayoutInflater.from(currentContext);
                notes = allNotes;
                View layout;
                layout = inflater.inflate(R.layout.activity_flip, null);
                View stub = layout.findViewById(
                        R.id.donut_chart_stub);

                // inflate stub by setting to visible
                stub.setVisibility(View.VISIBLE);
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View layout = convertView;
                if (convertView == null) {
                    layout = inflater.inflate(R.layout.activity_flip, null);
                    //currentView = layout;

                    View stub = layout.findViewById(
                            R.id.donut_chart_stub);

                    // inflate stub by setting to visible
                    stub.setVisibility(View.VISIBLE);
                }

                //Get's value from our ArrayList by the position
                //Log.d("TAG", "notes get position "+ notes.get(position));

                TextView tView = (TextView) layout.findViewById(R.id.note);
                Log.d("TAG", "postion for getview: "+ Integer.toString(position));
                tView.setText(donuts.get(position));
                initDonutChart(layout, position);

                return layout;
            }

        }

        public static class MyAdapter extends FragmentPagerAdapter {
            protected Context mContext;
            String[][] d;

            public MyAdapter(FragmentManager fm, Context context, String[][] data) {
                super(fm);
                mContext = context;
                d = data;
               // Log.d("TAG", "THIS IS THE DATA FOR MYADAPTER" + data[0][0].toString());
            }

            @Override
            public int getCount() {
                return NUM_ITEMS;
            }

            @Override
            public Fragment getItem(int position) {
                //Log.d("TAG", "called getItem from Myadapter with position: " + position);
                return BarFragment.newInstance(position, d[position]);
            }
        }

        public static class BarFragment extends Fragment {
            // save a reference to show the d3
            private WebView webview;
            int mNum;
            String[] d;


            /**
             * Create a new instance of CountingFragment, providing "num"
             * as an argument.
             */
            static BarFragment newInstance(int num, String[] data) {
                BarFragment f = new BarFragment();

                // Supply num input as an argument.
                Bundle args = new Bundle();
                args.putInt("num", num);
                args.putStringArray("strings", data);
                f.setArguments(args);
                //Log.d("TAG", "THIS IS THE DATA FOR BAR FRAGMENT" + data[0]);
                return f;
            }

            /**
             * When creating, retrieve this instance's number from its arguments.
             */
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                mNum = getArguments() != null ? getArguments().getInt("num") : 1;
                d = getArguments() != null ? getArguments().getStringArray("strings") : new String[7];
            }

            /**
             * The Fragment's UI is just a simple text view showing its
             * instance number.
             */
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View v = inflater.inflate(R.layout.bars_home, container, false);
                //Log.d("TAG", "second toplevelview: " + v.toString());
                //Log.d("TAG", "THIS IS MORE BARFRAGMENT DATA: " + d[0]);
                ((TextView)v.findViewById(R.id.text)).setText("Fragment #" + mNum);
                initBarChart(mNum, d, v);

                return v;
            }

            // initialize the WebView and the "bar chart"
            public void initBarChart(int number, String[] d, View v)
            {
                final String[] data = d;
                final int num = number;
                //Log.d("TAG", "THIS IS INITBARCHART DATA : "+ data[0]);
                //get reference to the view stub
                View stub = v.findViewById(
                        R.id.bar_chart_stub);


                // inflate stub by setting to visible
                stub.setVisibility(View.VISIBLE);

                //get reference to inflated webview
                webview = (WebView)v.findViewById(
                        R.id.bar_chart_webview);
                Log.d("TAG", "THIS IS THE WEBVIEW: "+webview);

                //set Javascript to enabled
                WebSettings webSettings = webview.getSettings();
                webSettings.setJavaScriptEnabled(true);

                //set up chromeclient (sends javascript alerts and browser stuff)
                webview.setWebChromeClient(new WebChromeClient());


                webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(
                            WebView view,
                            String url) {

                        // after the HTML page loads,
                        // load the pie chart
                        synchronized (SPLASH_LOCK) {
                            SPLASH_LOCK.notifyAll();
                        }

                        String sendData = "";
                        for (int x = 0; x < data.length; x++){
                            sendData = sendData + data[x] +",";
                            Log.d("TAG", "THIS IS WHAT IS BEING SENT TO THE JS at position "+Integer.toString(x)+": " + data[x]);
                        }
                        sendData.substring(0, sendData.length() - 1);
                        webview.loadUrl("javascript:startApp( [" + sendData+"],"+num+")");
                        Log.d("TAG", "Finished load bar chart call");
                    }
                });

                webview.loadUrl("file:///android_asset/html/barchart.html");

                Log.d("TAG", "Bar chart created with mNum: " + mNum);
            }


        }



        // initialize the WebView and the "pie chart"
        public void initPieChart()
        {
            //get reference to the view stub
            View stub = topLevelView.findViewById(
                    R.id.pie_chart_stub);

            // inflate stub by setting to visible
            stub.setVisibility(View.VISIBLE);

            //get reference to inflated webview
            webview = (WebView)topLevelView.findViewById(
                    R.id.pie_chart_webview);

            //set Javascript to enabled
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);

            //set up chromeclient ( sends javascript alerts and browser stuff)
            webview.setWebChromeClient(new WebChromeClient());

            //Set up web app interface ( web app interface class)
            WebAppInterface webapp = new WebAppInterface(getActivity());
            webview.addJavascriptInterface(webapp, "Android");

            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(
                        WebView view,
                        String url) {
                    // after the HTML page loads,
                    // load the pie chart
                    synchronized (SPLASH_LOCK) {
                        SPLASH_LOCK.notifyAll();
                    }
                    webview.loadUrl("javascript:startApp( ['" + removePunctuation(results[0]) + "','" + removePunctuation(results[1]) + "','" + removePunctuation(results[2]) + "'], '" + tToken + "')");
                    Log.d("TAG", "Finished load pie chart call");
                }
            });
            // note the mapping
            // from  file:///android_asset
            // to PieChartExample/assets
            webview.loadUrl("file:///android_asset/html/piechart.html");
        }


        // initialize the WebView and the "donut chart"
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public void initDonutChart(View thisView, int position)
        {
            //get reference to the view stub


            //get reference to inflated webview
            webview = (WebView) thisView.findViewById(
                    R.id.donut_chart_webview);

            //set Javascript to enabled
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);

            //set up chromeclient ( sends javascript alerts and browser stuff)
            webview.setWebChromeClient(new WebChromeClient());

            //Set up web app interface ( web app interface class)
            WebAppInterface webapp = new WebAppInterface(getActivity());
            webview.addJavascriptInterface(webapp, "Android");
            final JSONObject finalDonut;
            final JSONObject finalForm;
            JSONObject tempDonut = null;
            JSONObject tempForm = null;
            try {
                tempDonut = donutResults.getJSONObject(position);
                Log.d("tag",((JSONArray)tempDonut.get("questionOne")).toString() );
                for(int i = 0; i < ((JSONArray)tempDonut.get("questionOne")).length(); i++){
                    if ((((JSONArray)tempDonut.get("questionOne")).getJSONObject(i)).getInt("count") < 2){
                        ((JSONArray)tempDonut.get("questionOne")).remove(i);
                        i--;
                    }
                }

                tempForm = formResults.getJSONObject(position);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            finalDonut = tempDonut;
            finalForm = tempForm;
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(
                        WebView view,
                        String url) {



                    Log.d("TAG", "got to the on page finished on donut");
                    // after the HTML page loads,
                    // load the bar chart
                    synchronized (SPLASH_LOCK) {
                        SPLASH_LOCK.notifyAll();
                    }
                    Log.d("TAG", "DonutResults : " + donutResults.toString());
                    Log.d("TAG", "this is the webview after : " + webview.toString());
                    if (firstDonutWebView != null) {
                        ((WebView) (firstDonutWebView)).loadUrl("javascript:startApp(" + finalDonut + ","+finalForm+")");
                        firstDonutWebView = null;
                    } else {
                        webview.loadUrl("javascript:startApp("+ finalDonut + ","+finalForm+")");
                    }
                    Log.d("TAG", "Finished load donut chart call");
                }
            });
            Log.d("TAG", "made it here");
            Log.d("TAG", "this is the webview before : " + webview.toString());
            if (firstDonutWebView == null){
                firstDonutWebView = webview;
            }
            webview.loadUrl("file:///android_asset/html/donutchart.html");

        }


        public String getText(String channel, String token, int timeDiff) {
            try {
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
                if(now.get(Calendar.HOUR_OF_DAY) - (1 + timeDiff ) < 0){nowHour = "23";}
                else if(now.get(Calendar.HOUR_OF_DAY) - (1 + timeDiff ) < 10){nowHour = "0"+(now.get(Calendar.HOUR_OF_DAY) - (1 + timeDiff ));}
                else{nowHour  = Integer.toString(now.get(Calendar.HOUR_OF_DAY) - (1 + timeDiff ));}

                String nowMinute;
                if(now.get(Calendar.MINUTE) < 10){nowMinute = "0"+now.get(Calendar.MINUTE);}
                else{nowMinute  = Integer.toString(now.get(Calendar.MINUTE));}

                String nowSecond;
                if(now.get(Calendar.SECOND) < 10){nowSecond = "0"+now.get(Calendar.SECOND);}
                else{nowSecond  = Integer.toString(now.get(Calendar.SECOND));}

                String link = "http://api.shadowtv.net/reperio/v1/captions/" + channel + "/"+nowYear+"-"+nowMonth+"-" + nowDay+"T" +nowHour+ ":" + nowMinute + ":" + nowSecond + ".000Z/3599?include=body";
                //Log.d("TAG", "request Link : " + link);
                URL url;
                URLConnection urlConn;
                InputStream input;
                url = new URL(link);
                urlConn = url.openConnection();
                urlConn.addRequestProperty("Authorization", token);
                urlConn.setDoInput(true);
                urlConn.setUseCaches(false);
                urlConn.connect();
                input = urlConn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(input));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                input.close();

                //Log.d("TAG", "return message : " + response);
                Log.d("TAG", "CONNECTION OCCURRED - TEXT");
                //turning text response to json object and getting elements from that object
                JSONObject respJSON = new JSONObject(response.toString());
                //Log.d("TAG", "return body : " + respJSON.getString("body"));
                return respJSON.getString("body");

            } catch (IOException e) {
                Log.d("TAG", "failed to create inputStream (probably)");
                Log.d("TAG", e.toString());
                e.printStackTrace();
            }

            catch (Exception e){
                Log.d("TAG", "Something else went wrong in text");
                e.printStackTrace();
            }

            return "";
        }



        public String getBarData( String type, int timeDiff) {
            try {
                Calendar now = Calendar.getInstance();
                int nowYear = now.get(Calendar.YEAR);
                int diffM = 0, diffD = 0 , diffW = 0;
                if (type == "m"){
                    diffM = timeDiff;
                }
                else if (type == "d"){
                    diffD = timeDiff;
                }
                else if (type == "w"){
                    diffW = timeDiff;
                }


                String nowMonth;
                if(now.get(Calendar.MONTH) - diffM + 1 > 12){nowMonth = "00";}
                else if(now.get(Calendar.MONTH) - diffM + 1 < 10){nowMonth = "0"+(now.get(Calendar.MONTH) - diffM + 1);}
                else{nowMonth  = Integer.toString(now.get(Calendar.MONTH) - diffM + 1);}

                String nowDay;
                if(now.get(Calendar.DAY_OF_MONTH) - diffD < 10){nowDay = "0"+(now.get(Calendar.DAY_OF_MONTH) - diffD);}
                else{nowDay  = Integer.toString(now.get(Calendar.DAY_OF_MONTH) - diffD);}

                String nowWeek;
                if(now.get(Calendar.WEEK_OF_YEAR) - diffW < 10){nowWeek = "0"+(now.get(Calendar.WEEK_OF_YEAR) - diffW);}
                else{nowWeek  = Integer.toString(now.get(Calendar.WEEK_OF_YEAR) - diffW);}

                String link = "";
                if (type == "d") {
                    link = " http://brazil.shadowtv.com/server/BARS/" + nowYear + "/MONTH/" + nowMonth + "/" + nowDay + "/TOTAL.json";
                }if (type == "m") {
                    link = " http://brazil.shadowtv.com/server/BARS/" + nowYear + "/MONTH/" + nowMonth + "/TOTAL.json";
                }
                if (type == "w") {
                    link = " http://brazil.shadowtv.com/server/BARS/" + nowYear + "/WEEK/" + nowWeek + "/TOTAL.json";
                }
                //Log.d("TAG", "request BAR Link : " + link);

                URL url;
                URLConnection urlConn;
                InputStream input;
                url = new URL(link);
                urlConn = url.openConnection();
                //Retrieve Input
                input = urlConn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(input));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                input.close();

                //Log.d("TAG", "CONNECTION OCCURRED BARS");
                //Log.d("TAG", "return message : " + response);

                return response.toString();
            }

            //EXCEPTIONS
            catch (IOException e) {
                Log.d("TAG", "failed to create outputStream (probably)");
                e.printStackTrace();
            }
            catch (Exception e){
                Log.d("TAG", "Something else went wrong");
                Toast.makeText(getActivity(), "CONNECTION FAILED: BARS", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return "";
        }



        public String getFormData(int d) {
            try {
                Calendar now = Calendar.getInstance();
                int nowYear = now.get(Calendar.YEAR);

                String nowWeek;
                if(now.get(Calendar.WEEK_OF_YEAR) - d  < 10){nowWeek = "0"+(now.get(Calendar.WEEK_OF_YEAR) - d );}
                else{nowWeek  = Integer.toString(now.get(Calendar.WEEK_OF_YEAR) - d );}

                String link = " http://brazil.shadowtv.com/server/POLLS/" + nowYear + "/WEEK/" + nowWeek + "/question.json";

                Log.d("TAG", "request Form Link : " + link);

                URL url;
                URLConnection urlConn;
                InputStream input;
                url = new URL(link);
                urlConn = url.openConnection();
                //Retrieve Input
                input = urlConn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(input));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                input.close();

                Log.d("TAG", "CONNECTION OCCURRED FORM");
                Log.d("TAG", "return message : " + response);

                //instead of this probably will have to store this data somewhere so that the data can be used by the program where this is called,, maybe shared preferences

                return response.toString();
            }

            //EXCEPTIONS
            catch (IOException e) {
                Log.d("TAG", "failed to create outputStream (probably)");
                e.printStackTrace();
            }
            catch (Exception e){
                Log.d("TAG", "Something else went wrong");
                Toast.makeText(getActivity(), "CONNECTION FAILED: FORM", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return "";
        }


        public String getDonutData(int d) {
            try {
                Calendar now = Calendar.getInstance();
                int nowYear = now.get(Calendar.YEAR);

                String nowWeek;
                if(now.get(Calendar.WEEK_OF_YEAR) - d  < 10){nowWeek = "0"+(now.get(Calendar.WEEK_OF_YEAR) - d );}
                else{nowWeek  = Integer.toString(now.get(Calendar.WEEK_OF_YEAR) - d );}

                String link = " http://brazil.shadowtv.com/server/POLLS/" + nowYear + "/WEEK/" + nowWeek + "/TOTAL.json";

                Log.d("TAG", "request Form Link : " + link);

                URL url;
                URLConnection urlConn;
                InputStream input;
                url = new URL(link);
                urlConn = url.openConnection();
                //Retrieve Input
                input = urlConn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(input));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                input.close();

                Log.d("TAG", "CONNECTION OCCURRED FORM");
                Log.d("TAG", "return message : " + response);

                //instead of this probably will have to store this data somewhere so that the data can be used by the program where this is called,, maybe shared preferences

                return response.toString();
            }

            //EXCEPTIONS
            catch (IOException e) {
                Log.d("TAG", "failed to create outputStream (probably)");
                e.printStackTrace();
            }
            catch (Exception e){
                Log.d("TAG", "Something else went wrong");
                Toast.makeText(getActivity(), "CONNECTION FAILED: FORM", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return "";
        }


        public String removePunctuation (String inputfile){
            //returns a string equal to the given string 'inputfile', but without punctuation
            String words = inputfile.replaceAll("\n", " ");
            words = words.replaceAll("'", "");
            words = words.replaceAll("\\p{P}", " ");
            return words;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((HomeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
            Log.d("TAG", "Attached activity");

        }
    }

}





// initialize the WebView and the "bar chart"
        /*public void initBarChart()
        {
            //get reference to the view stub
            View stub = topLevelView.findViewById(
                    R.id.pie_chart_stub);

            // inflate stub by setting to visible
            stub.setVisibility(View.VISIBLE);

            //get reference to inflated webview
            webview = (WebView)topLevelView.findViewById(
                    R.id.pie_chart_webview);

            //set Javascript to enabled
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);

            //set up chromeclient (sends javascript alerts and browser stuff)
            webview.setWebChromeClient(new WebChromeClient());

            //Set up web app interface (web app interface class)
            WebAppInterface webapp = new WebAppInterface(getActivity());
            webview.addJavascriptInterface(webapp, "Android");

            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(
                        WebView view,
                        String url) {

                    // after the HTML page loads,
                    // load the bar chart
                    synchronized (SPLASH_LOCK) {
                        SPLASH_LOCK.notifyAll();
                    }
                    Log.d("TAG", "Finished load bar chart call");
                }
            });


            // note the mapping
            // from  file:///android_asset
            // to PieChartExample/assets
            webview.loadUrl("file:///android_asset/html/barchart.html");
        }
        */