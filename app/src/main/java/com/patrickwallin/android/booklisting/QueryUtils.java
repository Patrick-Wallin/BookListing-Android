package com.patrickwallin.android.booklisting;

/**
 * Created by Patrick on 11/19/2016.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    private static final String SAMPLE_JSON_RESPONSE = "{\n" +
            " \"kind\": \"books#volumes\",\n" +
            " \"totalItems\": 1070,\n" +
            " \"items\": [\n" +
            "  {\n" +
            "   \"kind\": \"books#volume\",\n" +
            "   \"id\": \"bmJIl_wPgQsC\",\n" +
            "   \"etag\": \"KLQeQg0ldyM\",\n" +
            "   \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/bmJIl_wPgQsC\",\n" +
            "   \"volumeInfo\": {\n" +
            "    \"title\": \"Professional Android 4 Application Development\",\n" +
            "    \"authors\": [\n" +
            "     \"Reto Meier\"\n" +
            "    ],\n" +
            "    \"publisher\": \"John Wiley & Sons\",\n" +
            "    \"publishedDate\": \"2012-05-01\",\n" +
            "    \"description\": \"Provides information on using Android 3 to build and enhance mobile applications, covering such topics as creating user interfaces, using intents, databases, creating and controlling services, creating app widgets, playing audio and video, telphony, and using sensors. Original.\",\n" +
            "    \"industryIdentifiers\": [\n" +
            "     {\n" +
            "      \"type\": \"ISBN_13\",\n" +
            "      \"identifier\": \"9781118102275\"\n" +
            "     },\n" +
            "     {\n" +
            "      \"type\": \"ISBN_10\",\n" +
            "      \"identifier\": \"1118102274\"\n" +
            "     }\n" +
            "    ],\n" +
            "    \"readingModes\": {\n" +
            "     \"text\": false,\n" +
            "     \"image\": true\n" +
            "    },\n" +
            "    \"pageCount\": 817,\n" +
            "    \"printType\": \"BOOK\",\n" +
            "    \"categories\": [\n" +
            "     \"Computers\"\n" +
            "    ],\n" +
            "    \"maturityRating\": \"NOT_MATURE\",\n" +
            "    \"allowAnonLogging\": false,\n" +
            "    \"contentVersion\": \"0.9.0.0.preview.1\",\n" +
            "    \"imageLinks\": {\n" +
            "     \"smallThumbnail\": \"http://books.google.com/books/content?id=bmJIl_wPgQsC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api\",\n" +
            "     \"thumbnail\": \"http://books.google.com/books/content?id=bmJIl_wPgQsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api\"\n" +
            "    },\n" +
            "    \"language\": \"en\",\n" +
            "    \"previewLink\": \"http://books.google.com/books?id=bmJIl_wPgQsC&printsec=frontcover&dq=android&hl=&cd=1&source=gbs_api\",\n" +
            "    \"infoLink\": \"http://books.google.com/books?id=bmJIl_wPgQsC&dq=android&hl=&source=gbs_api\",\n" +
            "    \"canonicalVolumeLink\": \"http://books.google.com/books/about/Professional_Android_4_Application_Devel.html?hl=&id=bmJIl_wPgQsC\"\n" +
            "   },\n" +
            "   \"saleInfo\": {\n" +
            "    \"country\": \"US\",\n" +
            "    \"saleability\": \"NOT_FOR_SALE\",\n" +
            "    \"isEbook\": false\n" +
            "   },\n" +
            "   \"accessInfo\": {\n" +
            "    \"country\": \"US\",\n" +
            "    \"viewability\": \"PARTIAL\",\n" +
            "    \"embeddable\": true,\n" +
            "    \"publicDomain\": false,\n" +
            "    \"textToSpeechPermission\": \"ALLOWED\",\n" +
            "    \"epub\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"pdf\": {\n" +
            "     \"isAvailable\": false\n" +
            "    },\n" +
            "    \"webReaderLink\": \"http://books.google.com/books/reader?id=bmJIl_wPgQsC&hl=&printsec=frontcover&output=reader&source=gbs_api\",\n" +
            "    \"accessViewStatus\": \"SAMPLE\",\n" +
            "    \"quoteSharingAllowed\": false\n" +
            "   },\n" +
            "   \"searchInfo\": {\n" +
            "    \"textSnippet\": \"Professional Android 4 Application Development: Provides an in-depth look at the Android application components and their lifecycles Explores Android UI metaphors, design philosophies, and UI APIs to create compelling user interfaces for ...\"\n" +
            "   }\n" +
            "  }\n" +
            " ]\n" +
            "}";

    public static final String LOG_TAG = TextUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<BookListingInformation> fetchBookListingData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<BookListingInformation> bookListings = extractBookListing(jsonResponse);

        // Return the {@link Event}
        return bookListings;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book listing JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<BookListingInformation> extractBookListing(String urls) {
        // Create an empty ArrayList that we can start adding earthquakes to
        /*
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        if(urls == null || urls.isEmpty())
            return null;

        List<BookListingInformation> bookListings = new ArrayList<>();


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //earthquakes.add(new EarthquakeInformation("7.2","San Francisco","Feb 2, 2016"));
            //JSONObject root = new JSONObject(SAMPLE_JSON_RESPONSE);
            JSONObject root = new JSONObject(urls);
            JSONArray items = root.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject book = items.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");



                String publishedDate = "";
                String title = "";
                ArrayList<String> author = new ArrayList<>();
                if(volumeInfo.has("publishedDate"))
                    publishedDate = volumeInfo.getString("publishedDate");
                if(volumeInfo.has("title"))
                    title = volumeInfo.getString("title");
                if(volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    if (authors != null && authors.length() > 0) {
                        for (int iAuthors = 0; iAuthors < authors.length(); iAuthors++) {
                            author.add(authors.getString(iAuthors));
                        }
                    }
                }

                bookListings.add(new BookListingInformation(title,author,publishedDate));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing book information JSON", e);
        }

        // Return the list of earthquakes
        return bookListings;
    }

}
