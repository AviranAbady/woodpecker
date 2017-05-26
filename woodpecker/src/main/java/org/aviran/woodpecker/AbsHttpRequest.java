package org.aviran.woodpecker;

import android.os.AsyncTask;

import org.aviran.woodpecker.annotations.Param;
import org.aviran.woodpecker.annotations.Path;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Aviran Abady on 5/31/17.
 */

public abstract class AbsHttpRequest extends AsyncTask<Void, Void, String> {

    public abstract String performRequest();

    protected final Peck peck;
    protected final WoodpeckerHttpResponse responseListener;

    public AbsHttpRequest(Peck peck, WoodpeckerHttpResponse listener) {
        this.peck = peck;
        this.responseListener = listener;
    }



    protected  String parseRequestParameters(WoodpeckerRequest request, StringBuilder url, boolean encode)
            throws IllegalAccessException, UnsupportedEncodingException {
        StringBuilder parameters = new StringBuilder();
        Field[] fields = request.getClass().getDeclaredFields();
        if(fields.length > 0) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(Param.class)) {
                    parameters.append(field.getName());
                    parameters.append("=");
                    field.setAccessible(true);
                    if(encode) {
                        parameters.append(URLEncoder.encode(field.get(request).toString(), "utf-8"));
                    }
                    else {
                        parameters.append(field.get(request).toString());
                    }
                    parameters.append("&");
                }
                else if(field.isAnnotationPresent(Path.class)) {
                    //todo refactor to pattern/match
                    field.setAccessible(true);
                    String str = url.toString().replaceAll("\\{" + field.getName() + "\\}",field.get(request).toString());
                    url.setLength(0);
                    url.append(str);
                }
            }
            if(parameters.length() > 0) {
                parameters.deleteCharAt(parameters.length() - 1);
            }
        }
        return parameters.toString();
    }

    protected String doInBackground(Void... v) {
        return performRequest();
    }

    protected String readInputSteam(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(inputStream);
        char[] buffer = new char[1024];
        int charsRead;
        while ((charsRead = reader.read(buffer)) != -1) {
            stringBuilder.append(buffer, 0, charsRead);
        }
        reader.close();
        return stringBuilder.toString();
    }

    protected void generateErrorResponse(WoodpeckerResponse response, HttpURLConnection httpConnection) {
        try {
            response.setResponseCode(httpConnection.getResponseCode());
            response.setRawResponse(readInputSteam(httpConnection.getErrorStream()));
        } catch (IOException e) {

        }
    }

    protected void addHeaders(HttpURLConnection httpConnection) {
        Map<String, String> headers = Woodpecker.getSettings().getHeaders();
        if(headers == null || headers.size() == 0) {
            return;
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            httpConnection.addRequestProperty(header.getKey(), header.getValue());
        }
    }

    protected void onPostExecute(String response) {
        if (response == null) {
            responseListener.httpError();
            return;
        }
        responseListener.httpSuccess(response);
        peck.getWoodpecker().peck();
    }
}