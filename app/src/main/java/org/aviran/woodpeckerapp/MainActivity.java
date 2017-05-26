package org.aviran.woodpeckerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.aviran.woodpecker.Woodpecker;
import org.aviran.woodpecker.WoodpeckerError;
import org.aviran.woodpecker.WoodpeckerResponse;
import org.aviran.woodpeckerapp.model.CharactersRequest;
import org.aviran.woodpeckerapp.model.CharactersResponse;
import org.aviran.woodpeckerapp.model.LoginRequest;
import org.aviran.woodpeckerapp.model.LoginResponse;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Woodpecker
                .begin()
                .request(new LoginRequest("aviran", "12345"))
                .then(new WoodpeckerResponse<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse response) {
                        Log.i("login", response.getToken());
                    }
                })
                .request(new CharactersRequest(1, 10))
                .then(new WoodpeckerResponse<List<CharactersResponse>>() {
                    @Override
                    public void onSuccess(List<CharactersResponse> response) {
                        response.toString();
                    }
                })
                .error(new WoodpeckerError() {

                    @Override
                    public void onError() {

                    }
                });
    }
}
