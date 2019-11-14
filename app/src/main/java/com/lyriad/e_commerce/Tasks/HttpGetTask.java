package com.lyriad.e_commerce.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.lyriad.e_commerce.Utils.Constants;
import com.lyriad.e_commerce.Utils.Response;
import com.lyriad.e_commerce.Utils.Result;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpGetTask extends AsyncTask<Map<String, String>, Void, Result> {

    private String subdomain;
    private final Response.Listener listener;
    private final Response.ErrorListener errorListener;

    public HttpGetTask(String subdomain, Response.Listener listener, Response.ErrorListener errorListener) {
        this.subdomain = subdomain;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    protected Result doInBackground(Map<String, String>... params) {

        JSONArray data = null;
        Map<String, String> headers = new HashMap<>();

        if (params.length == 1) {
            headers = params[0];
        } else if (params.length > 1) {
            return new Result<>(new Exception("Needed headers map. Got more parameters."));
        }

        try {
            URL url = new URL(String.format("%s%s", Constants.API_URL, subdomain));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("token", Constants.API_TOKEN);
            if (headers.size() > 0) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            connection.connect();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                final StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                data = new JSONArray(stringBuilder.toString());
                Log.i("DATA", data.toString());

            } catch (Exception e) {

                return new Result(new Exception("Request error"));
            } finally {

                connection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data != null) {

            return new Result<>(data);

        } else {
            return new Result<>(new Exception(String.format("Failed to fetch data from %s", subdomain)));
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (result.getResult() == null) {
            Log.wtf("ERROR", "HttpGetTask: Result was null");
            errorListener.onErrorResponse(result.getError());
        } else {
            listener.onResponse(result.getResult());
        }
    }

    @Override
    protected void onCancelled() {
        errorListener.onErrorResponse(new Exception("Task cancelled"));
    }
}
