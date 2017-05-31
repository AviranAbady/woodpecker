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
            throws MalformedURLException, IllegalAccessException, UnsupportedEncodingException {

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
        try {
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            InputStream inputStream = httpConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            char[] buffer = new char[1024];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, charsRead);
            }
            reader.close();
            inputStream.close();
            httpConnection.disconnect();
            return stringBuilder.toString();
        } catch (MalformedURLException e) {
            return null;
        } catch (ProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
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