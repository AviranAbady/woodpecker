# woodpecker

experimental lean network manager<br/>
So lean, it's not functional... yet, GET requests are partialy supported<br/>
### Integrate
```
compile 'org.aviran.woodpecker:woodpecker:0.0.2'
```

<img src="http://i.imgur.com/35jFhoU.gif"/>


### Easily perform HTTP api calls, chain api calls easily.
```java
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
```

### Login api call is defined by the following request/response classes
```java
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

public class LoginResponse {
    private String token;

    public String getToken() {
        return token;
    }
}

```

### Characters api call is defined by the following request/response classes
```java
@Get("/characters")
public class CharactersRequest extends WoodpeckerRequest {
    @Param
    private int page;

    @Param
    private int pageSize;

    public CharactersRequest(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}


public class CharactersResponse {
    private String[] books;
    private String born;
    private String[] aliases;
    private String url;
    private String father;
    private String mother;
    private String died;
    private String spouse;
    private String[] tvSeries;
    private String name;
    private String[] allegiances;
    private String[] povBooks;
    private String[] playedBy;
    private String gender;
    private String[] titles;
    private String culture;
    
    ...
    ...
}

```
