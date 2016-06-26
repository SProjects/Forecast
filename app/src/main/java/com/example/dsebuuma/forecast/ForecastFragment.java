package com.example.dsebuuma.forecast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;
    private String locationQuery;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        String[] data = {
//            "Mon 6/23 - Sunny - 31/17",
//            "Tue 6/24 - Foggy - 21/8",
//            "Wed 6/25 - Cloudy - 22/17",
//            "Thurs 6/26 - Rainy - 18/11",
//            "Fri 6/27 - Foggy - 21/10",
//            "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
//            "Sun 6/29 - Sunny - 20/7"
//        };
//        List<String> weekForeCast = new ArrayList<String>(Arrays.asList(data));
        List<String> weekForeCast = new ArrayList<String>();

        forecastAdapter = new ArrayAdapter<String>(
            getContext(),
            R.layout.list_item_forecast,
            R.id.list_item_forecast_textview,
            weekForeCast
        );

        ListView foreCastListView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        foreCastListView.setAdapter(forecastAdapter);

        foreCastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = forecastAdapter.getItem(position);
//                Toast.makeText(getContext(), forecast, Toast.LENGTH_SHORT).show();
                Intent detailActivityIntent = new Intent(getContext(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(detailActivityIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getContext(), forecastAdapter);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        locationQuery = sharedPref.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_default_location_value)
        );

        weatherTask.execute(locationQuery);
    }

//    public class FetchWeatherTask extends AsyncTask<Void, Void, String[]> {
//        private static final String OPEN_WEATHER_MAP_API_KEY = "a7bd52a89ef7f20cf8209cb040c3a5f1"; //Place your API Key here
//        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName(); //Create you won log tag
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String[] doInBackground(Void... voids) {
//
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String forecastJsonStr = null;
//
//            String format = "json";
//            String units = "metric";
//            int numDays = 14;
//
//            try {
//                // Construct the URL for the OpenWeatherMap query
//                // Possible parameters are avaiable at OWM's forecast API page, at
//                // http://openweathermap.org/API#forecast
//                final String FORECAST_BASE_URL =
//                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
//                final String QUERY_PARAM = "q";
//                final String FORMAT_PARAM = "mode";
//                final String UNITS_PARAM = "units";
//                final String DAYS_PARAM = "cnt";
//                final String APPID_PARAM = "APPID";
//
//                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
//                        .appendQueryParameter(QUERY_PARAM, locationQuery)
//                        .appendQueryParameter(FORMAT_PARAM, format)
//                        .appendQueryParameter(UNITS_PARAM, units)
//                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
//                        .appendQueryParameter(APPID_PARAM, OPEN_WEATHER_MAP_API_KEY)
//                        .build();
//
//                URL url = new URL(builtUri.toString());
//
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                forecastJsonStr = buffer.toString();
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attempting
//                // to parse it.
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            try {
//                return new WeatherDataParser(getContext()).getWeatherDataFromJson(forecastJsonStr, numDays);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String[] results) {
//            super.onPostExecute(results);
//            if (results != null) {
//                forecastAdapter.clear();
//                for(String dayForecastStr : results) {
//                    forecastAdapter.add(dayForecastStr);
//                }
//                // New data is back from the server.  Hooray!
//            }
//        }
//    }
}
