# woodpecker

Experimental lean http client, aiming to provide a simple way to perform http requests.<br/>
So lean, it's not functional... yet.<br/>
GET requests are partially supported.<br/>
### Integrate
```
compile 'org.aviran.woodpecker:woodpecker:0.0.3'
```

<img src="http://i.imgur.com/35jFhoU.gif"/>


### Easily perform HTTP api calls, chain api calls easily.
```java
// Initialize Woodpecker
Woodpecker.initialize(new WoodpeckerSettings("http://woodpecker.aviran.org"));

// Run the following 4 request, one after the other.

// POST  login   /login?username=user&password=password
// GET   list    /list?page=1&pageSize=10
// GET   item    /item/{id}
// POST  review  { itemId: id, name: Aviran, review: This is awesome }


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
```

### Login api call is defined by the following request/response classes
```java
@Post("/login")
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

### List api call is defined by the following request/response classes
```java
@Get("/list")
public class ListRequest extends WoodpeckerRequest {
    @Param
    private int page;

    @Param
    private int pageSize;

    public ListRequest(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}

public class ItemResponse {
    private int id;
    private String name;
    private int[] values;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int[] getValues() {
        return values;
    }
}

```

### Item api call - Demonstrating using url path variable
```java
@Get("/item/{id}")
public class ItemRequest extends WoodpeckerRequest {
    @Path
    private int id;

    public ItemRequest(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
```

### Review api call - Demonstrating posting JSON body (no parameters)
```java
@Post("/review")
public class ReviewRequest extends WoodpeckerRequest {
    private int itemId;
    private String name;
    private String text;

    public ReviewRequest(int itemId, String name, String text) {
        this.itemId = itemId;
        this.name = name;
        this.text = text;
    }
}
```