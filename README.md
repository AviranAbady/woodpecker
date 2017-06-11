# woodpecker

Lean HTTP client for Android.<br/>
Aiming to provide simplicity, minimal setup, chain requests without nesting code blocks.<br/>
GET, POST, PUT, HEAD requests are supported.<br/>
Upstream/Downstream progress listeners are available for upload/download progress tracking.<br/>
<br/>
See it in action, <a href="https://github.com/AviranAbady/woodpecker-demo">Android demo project</a>.

### Integrate
```gradle
compile 'org.aviran.woodpecker:woodpecker:0.9.1'
```

<img src="http://i.imgur.com/35jFhoU.gif"/>


### Over the top proof of concept
The following scenario fires 6 http requests in a row, passing data from one to the other when
 necessary.<br/>
 Quick initialization, build the request chain without nesting them inside each other's
 response callbacks.<br/>
```java
// Initialize Woodpecker
Woodpecker.initialize(new WoodpeckerSettings("http://woodpecker.aviran.org"));

// Run the following 6 requests, consecutively, passing data from one to the other.

// POST  login    /login - post body: username=user&password=password
// GET   list     /list?page=1&pageSize=10
// GET   item     /item/{id}
// POST  review   /review - post body: { name: Aviran, review: This is awesome }
// GET   get      /image.png - download binary file
// PUT   upload   /upload - upload binary image file

Woodpecker
  .begin()  // POST /login
  .request(new LoginRequest("username", "p@ssw0rd"))
  .then(new WoodpeckerResponse<LoginResponse>() {
      @Override
      public void onSuccess(LoginResponse response) {
          // Update authentication token for the follwing requests
          Woodpecker.getSettings().addHeader("token", response.getToken());
      }
  })       // GET /list?page=1&pageSize=10
  .request(new ListRequest(1, 10))
  .then(new WoodpeckerResponse<List<ItemResponse>>() {
      @Override
      public void onSuccess(List<ItemResponse> response) {
          // Get next request object
          ItemRequest itemRequest = (ItemRequest) getNextRequest();
          // Update it
          itemRequest.setId(response.get(0).getId());
      }
  })      // GET /item/{id}   - id is updated in run time by previous request
  .request(new ItemRequest(-1))
  .then(new WoodpeckerResponse<ItemResponse>() {
      @Override
      public void onSuccess(ItemResponse response) {
      }
  })      // POST /review  - JSON encoded post
  .request(new ReviewRequest(1, "Aviran", "This is awesome!"))
  .then(new WoodpeckerResponse<String>() {
      @Override
      public void onSuccess(String response) {
      }
  })      // GET /image.png - request with progress tracking
  .request(new DownloadFileRequest(progressListener))
  .then(new WoodpeckerResponse<InputStream>() {
      @Override
      public void onSuccess(InputStream response) {
      }
  })      // POST multipart data - 2 files uploaded, progress tracking
  .request(createFileUploadRequest())
  .then(new WoodpeckerResponse<UploadResponse>() {
      @Override
      public void onSuccess(UploadResponse response) {
      }
  })     // Error handler for the entire chain
  .error(new WoodpeckerError() {
      @Override
      public void onError(WoodpeckerResponse response) {
      }
  });
```

### Login api call is defined by the following request/response classes
```java
@Post("/login")
public class LoginRequest extends WoodpeckerRequest {
    @Param // @Param used to define 'username' as a request data parameter
    private String username;

    @Param
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

// POJO structure to the response of LoginRequest
public class LoginResponse {
    private String token;

    public String getToken() {
        return token;
    }
}
```

### Item api call - Demonstrating usage of url path variable
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

### Review api call - Demonstrating posting JSON body
```java
@Post("/review")
public class ReviewRequest extends WoodpeckerRequest {
    // @Param is not used in this class, therefore class structure
    // will be serialized to json, and will be sent as request body.
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

### GET'ing binary file, with download progress tracking
```java
// Progress listener that will be supplied to the request,
// will be executed on the UI Thread.
progressListener = new WoodpeckerProgressListener() {
    @Override
    public void onProgress(String name, int progress, int totalSize) {
        // log progress / totalSize
    }
}

// By using the @Progress annotation, this request will invoke progress
// notification calls to the supplied listener
@Get("/woodpecker.jpg")
public class DownloadFileRequest extends WoodpeckerRequest {
    @Progress
    WoodpeckerProgressListener progressListener;

    public DownloadFileRequest(WoodpeckerProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
```

### POST multipart, with upload progress tracking
```java
@Post("/upload")
public class UploadRequest extends WoodpeckerRequest {
    @Param
    private String uploadToken;

    @File
    private WoodpeckerFileStream fileUpload;

    @File
    private WoodpeckerFileStream anotherFileUpload;

    @Progress
    private WoodpeckerProgressListener progressListener;

    public UploadRequest(String uploadToken,
                         WoodpeckerFileStream fileUpload,
                         WoodpeckerFileStream anotherFileUpload,
                         WoodpeckerProgressListener progressListener) {
        this.uploadToken = uploadToken;
        this.fileUpload = fileUpload;
        this.anotherFileUpload = anotherFileUpload;
        this.progressListener = progressListener;
    }
}
```

License
=======

    Copyright 2017 Aviran Abady.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.