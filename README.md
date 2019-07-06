# Android-QR-Code-Scanner
Created as a final project for DMIT2504 Android Development by TJ Helm

# Creation Tutorial
Follow the steps in order to create the same project

## Android Project Setup
1. Create a new android studio project with an empty activity
     - Set and keep track of the package name it is important for FireBase to work properly
     - Make sure the minimum SDK is set to 21

## FireBase Setup
**Summary**  
This sets up your Firebase cloud service to handle the app.
1. Create a new FireBase account at [here](https://firebase.google.com/)
2. Create a new FireBase Project
     - Name the project to your liking
     - Select your location and agree to the terms
3. Select the android icon in the center
4. **Enter the application package name you held on to previously**
5. Set the nickname
6. Select "Register App"
7. Download the json config file and move it to your app directory using your file explorer
8. Select Next
9. Add the dependencies as it lists.
10. Select next and it will attempt to connect to the app, you can just skip this.

## Setting Up QR Code App
**Summary**  
Each of these steps will make sure you have the proper dependencies and setup to run the qr code.
1. Place this dependency in the build.gradle of your app: `implementation 'com.google.firebase:firebase-ml-vision:21.0.0'`
2. Place this in your application just after the activities: `
<meta-data
      android:name="com.google.firebase.ml.vision.DEPENDENCIES"
      android:value="barcode" />
`

## Coding the Application
**Summary**  
This will step you through actual programming each part of the application
1. In the main activity layout place two buttons down
2. In the java file add two variables (These will be the codes for determining which action is recieved.)
```java
static final int REQUEST_IMAGE_CAPTURE = 1;
static final int REQUEST_IMAGE_SELECT = 2;
```
3. Create a third variable that will hold the image once it is grabbed.
```java
Bitmap qrCode;
```
4. Create two button event functions called `takePicture` and `selectPicture`
5. Link the functions to the buttons's onClick event.
6. In the `takePicture` function, add this code that will start an intent to take a picture
```java
Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
}
```
7. In the `selectPicture` function, add this code that will start an intent to select a picture from the galary. 
The filter "image/*" is so that the user can only select an item of type image.
```java
Intent findPictureIntent = new Intent(Intent.ACTION_PICK);
if (findPictureIntent.resolveActivity(getPackageManager()) != null) {
    findPictureIntent.setType("image/*");
    startActivityForResult(findPictureIntent, REQUEST_IMAGE_SELECT);
}
```
8. Add the function `loadQRCode(Bitmap bitmap)` that will take in a bitmap image and process it
```java
private void loadQRCode(Bitmap bitmap)
{

}
```
9. Add this code to the previous function to allow it to process an image, convert it to a FirebaseVisionImage, 
detect QR codes, and product a result. It also loads an activity that has not been created yet, so don't
worry about the error
```java
//Gets the qr code detector from firebase
FirebaseVisionBarcodeDetectorOptions options =
        new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();

//Turns it into a proccessable image
FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
        .getVisionBarcodeDetector();

//Get the result
Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> codes) {
                //Try and start the activity with the URL recieved from the qrCode
                try {
                    Intent webViewerIntent = new Intent(MainActivity.this, WebViewerActivity.class);
                    webViewerIntent.putExtra("URL", codes.get(0).getDisplayValue());
                    startActivity(webViewerIntent);
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Could not load url. Is this QR code invalid?", Toast.LENGTH_LONG).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Shows a failed message
                Toast.makeText(MainActivity.this, "Could not process the qr code.", Toast.LENGTH_SHORT).show();
            }
        });
```
10. Override the function `onActivityResult` to catch that data
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) 
{

}
```
11. In the previously created function, add this code which processes the given code to check the type of activity result 
and sends the image if found off to the `loadQRCode` function
```java
//if image is taken by the camera
if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) 
{
    //Gets the image from a bundle
    Bundle extras = data.getExtras();
    loadQRCode((Bitmap)extras.get("data"));
}
//if image is selected from the device
else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK)
{
    //Use Uri to find the location on the device the image is at
    final Uri uri = data.getData();
    try
    {
        //Attempt to load that image.
        loadQRCode(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
    } catch (IOException e)
    {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
```

## Creating the WebViewer
**Summary**  
Now that the app is processing the URL from the QRCode, we need to load it into a WebView to see where the URL leads.
1. Create an Empty Layout called WebViewerActivity
2. Add a WebView View to the activity
3. In the code behind create a variable to hold the webview
4. put the webview from the layout using `findViewByID` in that variable
5. Call this function to load the URL into the webview `webView.loadURL(getIntent().getStringExtra("URL"));`
6. Your app may need enable javascript with this 
```java
WebSettings webSettings = webview.getSettings();
webSettings.setJavaScriptEnabled(true);
```
7. You may also want to prevent redirection from your webview to a browser
```java
webview.setWebViewClient(new WebViewClient() {
  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url){
    view.loadUrl(url);
    return false;
  }
});
```
8. Make sure you call the `loadURL` function after all of the optionals

# Congradulations
You have completed the QR Code Scanner App. You can now go test it out on your device!
