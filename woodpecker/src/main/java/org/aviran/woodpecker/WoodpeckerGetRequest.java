package org.aviran.woodpecker;

import android.os.AsyncTask;

import org.aviran.woodpecker.annotations.Get;
import org.aviran.woodpecker.annotations.Param;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Aviran Abady on 5/26/17.
 */

public class WoodpeckerGetRequest extends AsyncTask<Void, Void, String> {
    private final Peck peck;
    private final WoodpeckerHttpResponse responseListener;
    private URL url;

    public WoodpeckerGetRequest(Peck peck, WoodpeckerHttpResponse listener) {
        this.peck = peck;
        this.responseListener = listener;
        try {
            url = createURL(peck.getRequest());
        } catch (MalformedURLException |
                IllegalAccessException |
                UnsupportedEncodingException  e) {
            listener.httpError();
        }
    }

    private URL createURL(WoodpeckerRequest request)
            throws
            MalformedURLException,
            IllegalAccessException,
            UnsupportedEncodingException {

        Get requestAnnotation = request.getClass().getAnnotation(Get.class);
        StringBuilder url = new StringBuilder();
        url.append(peck.getWoodpecker().getBaseURL());
        url.append(requestAnnotation.value());

        Field[] fields = request.getClass().getDeclaredFields();
        if(fields.length > 0) {
            url.append("?");
            for (Field field : fields) {
                if (field.isAnnotationPresent(Param.class)) {
                    url.append(field.getName());
                    url.append("=");
                    field.setAccessible(true);
                    url.append(URLEncoder.encode(field.get(request).toString(),"utf-8"));
                    url.append("&");
                }
            }
            url.deleteCharAt(url.length() - 1);
        }

        return new URL(url.toString());
    }

    protected String doInBackground(Void... v) {
        HttpURLConnection httpConnection = null;
        try {
             new StringBuilder();
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            addHeaders(httpConnection);
            String response = readInputSteam(httpConnection.getInputStream());
            httpConnection.getInputStream().close();
            httpConnection.disconnect();
            peck.getResponse().setRawResponse(response);
            peck.getResponse().setResponseCode(httpConnection.getResponseCode());
            return response;
        } catch (MalformedURLException e) {
            return null;
        } catch (ProtocolException e) {
            return null;
        } catch (IOException e) {
            generateErrorResponse(peck.getResponse(), httpConnection);
            return null;
        }
    }

    private String readInputSteam(InputStream inputStream) throws IOException {
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

    private void generateErrorResponse(WoodpeckerResponse response, HttpURLConnection httpConnection) {
        try {
            response.setResponseCode(httpConnection.getResponseCode());
            response.setRawResponse(readInputSteam(httpConnection.getErrorStream()));
        } catch (IOException e) {

        }
    }

    private void addHeaders(HttpURLConnection httpConnection) {
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