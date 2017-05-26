package org.aviran.woodpeckerapp.model;

import org.aviran.woodpecker.WoodpeckerRequest;
import org.aviran.woodpecker.annotations.Get;
import org.aviran.woodpecker.annotations.Param;

/**
 * Created by aviran on 30/05/17.
 */

@Get("/login")
public class LoginRequest extends WoodpeckerRequest {
    @Param
    private String username;

    @Param
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
