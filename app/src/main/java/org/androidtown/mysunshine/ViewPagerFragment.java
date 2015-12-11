package org.androidtown.mysunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ChaeYoungDo on 2015-08-16.
 * 이미지, Github에 올리기
 */
public class ViewPagerFragment extends android.support.v4.app.Fragment {
    private static final String ARG_POSITION = "position";
    private LinearLayoutManager mLayoutManager;
    private int position;
    private RecyclerView.Adapter mForecastAdapter;
    List<String> weekForecast;
    private PagerAdapter pagerAdapter;

    public static ViewPagerFragment newInstance(int position) {
        ViewPagerFragment f = new ViewPagerFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {//forecastfragment menu생성하고 그것은 refresh버튼
        inflater.inflate(R.menu.forecastfragment, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        weatherTask.execute(location);
    }

    private void positionWeather(String i){
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(i);
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateWeather();
    }

    public class ForecastAdapter extends RecyclerView.Adapter<ForecastHolder>{

        @Override
        public ForecastHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.list_item_forecast,parent,false);
            return new ForecastHolder(view);
        }

        @Override
        public void onBindViewHolder(ForecastHolder holder, int position) {

            holder.textView.setText(weekForecast.get(position));

        }

        @Override
        public int getItemCount() {
            return weekForecast.size();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String[] data = {
                "데이터 받기 전",
                "데이터 받기 전",
                "데이터 받기 전",
                "데이터 받기 전",
                "데이터 받기 전",

        };
        /*내가 안됐던 이유 : 1. Adapter에서 getItemCount에서 0을 리턴해줬다. 2. getActivity로 리사이클뷰만들어서 해보는거
        3. 내 방식대로 xml에 있는거 인플레이션해서 해보는거 4. 승효형꺼 어디서 rootview 리턴해주는지
        5. rootview를 리턴해주는 것과 framelayout에서 fl new framelaout으로 만들어서 getactivity 넣어서 리턴해주는거
        params랑 setlayoutparams이게 정확히 뭘 의미하는지 알기! 용혁이 만나고 와서 !!!! gogogo        */

        weekForecast = new ArrayList<String>(Arrays.asList(data));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);
        RecyclerView rv = new RecyclerView(getActivity());
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());
        mForecastAdapter = new ForecastAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(mForecastAdapter);
        pagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        FetchWeatherTask weatherTask = new FetchWeatherTask();
//        positionWeather(pagerAdapter.returnTitle(position)); - 방법 굿
        switch (position){
            case 0:
                weatherTask.execute("Seoul");
                break;
            case 1:
                weatherTask.execute("Incheon");
                break;
            case 2:
                weatherTask.execute("Pohang");
                break;
            case 3:
                weatherTask.execute("Daegu");
                break;
            case 4:
                weatherTask.execute("Gapyeong");
            default:
                break;
        }
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity().getApplicationContext(), weekForecast.get(position).toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, weekForecast.get(position).toString());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        params.setMargins(margin, margin, margin, margin);
        rv.setLayoutParams(params);
        //rv.setBackgroundResource(R.drawable.background_card);
        fl.addView(rv);
        return fl;
    }
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {//fetch - 가지고오다
        /*원래 main thread와 일반thread를 가지고 handler를 사용하여 하지만 asynctask를 사용하면 하나의 클래스에서 작업을 할 수 있게 지원해 준다.*/

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 E요일");//리스트뷰에 날짜 형식
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {

            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);//최고점
            long roundedLow = Math.round(low);//최저점

            String highLowStr = "최고"+ roundedHigh + "℃ / 최저" + roundedLow + "℃";//두개를 합쳐서 한번에 나타낸다. 스트링형
            return highLowStr;
        }

        //Parsing 다른 형식으로 저장된 데이터를 원하는 형식의 데이터로 변환하는 것
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";


            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;//날짜
                String description;//날씨
                String highAndLow;//최고,저

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);//i값에 따른 배열을 하나하나 dayforecast에 넣는다

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);//날씨

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String value = prefs.getString(getString(R.string.Temperature_key),
                        getString(R.string.Celsius_values));
                if(value.equals(getString(R.string.Degrees_Fahrenheit_values))){
                    high = (high * 1.8) + 32;
                    low = (low * 1.8) + 32;
                }

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }
        @Override
        protected String[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =//url을 받아옴 오픈웨더맵에서
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()//각 형식에 맞게 파라미터로 받음
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();//url변수에 가져온 주소를 넣고 연결?
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();//버퍼는 문자열을 더해주는
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);//여기서 받아온 url을 타고 json의 형식에 맞춰 넣는다
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                weekForecast = new ArrayList<String>(Arrays.asList(result));
                mForecastAdapter.notifyDataSetChanged();

                // New data is back from the server.  Hooray!
            }
        }
    }
}
