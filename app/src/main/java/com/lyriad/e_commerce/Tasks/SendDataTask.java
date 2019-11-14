package com.lyriad.e_commerce.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.lyriad.e_commerce.Utils.Constants;
import com.lyriad.e_commerce.Utils.Response;
import com.lyriad.e_commerce.Utils.Result;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendDataTask extends AsyncTask<JSONObject, Void, Result> {

    private String subdomain;
    private String method;
    private final Response.Listener listener;
    private final Response.ErrorListener errorListener;

    public SendDataTask(String subdomain, String method, Response.Listener listener, Response.ErrorListener errorListener) {
        this.subdomain = subdomain;
        this.method = method;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    protected Result doInBackground(JSONObject... params) {

        JSONObject data = null;
        Exception error = null;
        JSONObject result = null;

        if (params.length == 1) {
            data = params[0];
        } else if (params.length > 1) {
            return new Result<>(new Exception("Needed post data. Got more parameters."));
        }

        try {

            URL url = new URL(String.format("%s%s", Constants.API_URL, subdomain));

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("token", Constants.API_TOKEN);
            urlConnection.setRequestMethod(method);

            if (data != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(data.toString());
                writer.flush();
            }

            int responseCode = urlConnection.getResponseCode();

            if (responseCode >=  200 && responseCode < 300) {

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                result = new JSONObject(convertInputStreamToString(inputStream));

            } else {
                error = new Exception(String.format(Constants.LOCALE, "HTTP error: code %d", responseCode));
            }

        } catch (Exception e) {
            error = new Exception("Error sending data");
        }

        if (error != null) {
            return new Result<>(error);
        } else {
            return new Result<>(result);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (result.getResult() == null) {
            Log.wtf("ERROR", "HttpPostTask: Result was null");
            errorListener.onErrorResponse(result.getError());
        } else {
            listener.onResponse(result.getResult());
        }
    }

    @Override
    protected void onCancelled() {
        errorListener.onErrorResponse(new Exception("Task cancelled"));
    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}