package org.aviran.woodpecker;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by Aviran Abady on 5/26/17.
 */

class WoodpeckerNetwork {
    public static <T> void get(final Peck peck) {
        WoodpeckerGetRequest httpTask = new WoodpeckerGetRequest(peck, new WoodpeckerHttpResponse() {
            @Override
            public void httpSuccess(String data) {
                if (data == null) {
                    peck.getResponse().onError(new WoodpeckerException(""));
                } else {
                    WoodpeckerResponse response = peck.getResponse();
                    T t;
                    try {
                        Type type = response.getType();
                        if (type.equals(String.class)) {
                            response.onSuccess(data);
                        } else {
                            t = new Gson().fromJson(data, response.getType());
                            response.onSuccess(t);
                        }
                    } catch (JsonSyntaxException jse) {
                        response.onError(new WoodpeckerException(data, jse));
                    }
                }
            }

            @Override
            public void httpError() {
                peck.getWoodpecker().handleError(peck.getResponse());
            }
        });

        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
