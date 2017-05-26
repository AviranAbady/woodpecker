package org.aviran.woodpeckerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.aviran.woodpecker.Woodpecker;
import org.aviran.woodpecker.WoodpeckerError;
import org.aviran.woodpecker.WoodpeckerRequest;
import org.aviran.woodpecker.WoodpeckerResponse;
import org.aviran.woodpecker.WoodpeckerSettings;
import org.aviran.woodpeckerapp.model.ItemRequest;
import org.aviran.woodpeckerapp.model.ListRequest;
import org.aviran.woodpeckerapp.model.ItemResponse;
import org.aviran.woodpeckerapp.model.LoginRequest;
import org.aviran.woodpeckerapp.model.LoginResponse;
import org.aviran.woodpeckerapp.model.ReviewRequest;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // POST  login           /login?username=user&password=password
        // GET   list            /list?page=1&pageSize=10
        // GET   item            /item/{id}
        // POST  review          { itemId: id, name: Aviran, review: This is awesome }

        Woodpecker.initialize(new WoodpeckerSettings("http://woodpecker.aviran.org"));

        Woodpecker
                .begin()
                .request(new LoginRequest("aviran", "12345"))
                .then(new WoodpeckerResponse<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse response) {
                        Woodpecker.getSettings().addHeader("token", response.getToken());
                    }
                })
                .request(new ListRequest(1, 10))
                .then(new WoodpeckerResponse<List<ItemResponse>>() {
                    @Override
                    public void onSuccess(List<ItemResponse> response) {
                        ItemRequest itemRequest = (ItemRequest) getNextRequest();
                        itemRequest.setId(response.get(0).getId());
                    }
                })
                .request(new ItemRequest(-1))
                .then(new WoodpeckerResponse<ItemResponse>() {
                    @Override
                    public void onSuccess(ItemResponse response) {
                        Log.i("WP", response.toString());
                    }
                })
                .request(new ReviewRequest(1, "Aviran", "This is awesome!"))
                .then(new WoodpeckerResponse<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.i("WP", response.toString());
                    }
                })
                .error(new WoodpeckerError() {
                    @Override
                    public void onError(WoodpeckerResponse response) {
                        Log.e("WP", "ERROR");
                    }
                });
    }
}
