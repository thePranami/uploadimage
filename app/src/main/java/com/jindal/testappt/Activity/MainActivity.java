package com.jindal.testappt.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jindal.testappt.ApiInterface;
import com.jindal.testappt.DataModel;
import com.jindal.testappt.DataPojo;
import com.jindal.testappt.MyAdapter;
import com.jindal.testappt.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Button sendButton, getData, upload;
    private EditText name, mobile;
    private RecyclerView recyclerView;
    List<DataModel> list = new ArrayList<>();
    int PICK_IMAGE_REQUEST = 111;
    private ImageView imageView;
    String url = "https://loopintechies.com/homeservice/android/index.php";
    public static Uri uri;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = (Button)findViewById(R.id.button_noti);

        recyclerView = findViewById(R.id.recycler);
        getData = findViewById(R.id.getData);
        upload=findViewById(R.id.upload);
        name=findViewById(R.id.name);
        mobile=findViewById(R.id.mobile);

        imageView = findViewById(R.id.image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile();
            }
        });
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://loopintechies.com/homeservice/android/")
                        .addConverterFactory(GsonConverterFactory.create()).build();
                ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                Call<DataPojo> call = apiInterface.getApiData("SUBSERVICE", "1");
                call.enqueue(new Callback<DataPojo>() {
                    @Override
                    public void onResponse(Call<DataPojo> call, Response<DataPojo> response) {
                        if (response.isSuccessful()){
                            //list =(ArrayList<DataModel>)response.body().getData();

                            if (response.body().getError()==0){

                                for (int i=0; i<response.body().getData().size(); i++){
                                    list.add(new DataModel(response.body().getData().get(i).getName(),
                                            response.body().getData().get(i).getPrice()));
                                }
                                MyAdapter myAdapter =new MyAdapter(MainActivity.this, list);
                                RecyclerView.LayoutManager manager = new LinearLayoutManager(MainActivity.this);
                                recyclerView.setLayoutManager(manager);
                                recyclerView.setAdapter(myAdapter);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<DataPojo> call, Throwable t) {

                    }
                });

            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNoti();
            }
        });
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationManager mNotificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
//            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
//            mChannel.enableLights(true);
//            mChannel.setLightColor(Color.RED);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            mNotificationManager.createNotificationChannel(mChannel);
//        }
//
//        MyNotiFicationManager.getInstance(MainActivity.this).showNotification("Hello", "How are you Sushil")

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImage(name.getText().toString(), mobile.getText().toString(), encodeToBase64(bitmap));
            }
        });
    }

    public void checkNoti(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(this.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle("MyData")
                .setContentText("One new data inserted")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notiIntent = new Intent(this, MainActivity.class);
        notiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notiBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(0, notiBuilder.build());
        Toast.makeText(this, "Thanks, You are Welcome!", Toast.LENGTH_SHORT).show();
    }

    public void openFile(){
        Intent imgIntent = new Intent();
        imgIntent.setType("image/*");
        imgIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imgIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                InputStream inputStream = getContentResolver().openInputStream(uri);
                //bitmap = BitmapFactory.decodeStream(inputStream);
                Log.d("ImageLog1", bitmap.toString());
                imageView.setImageBitmap(bitmap);
               // String filepath = getPath(uri);
               // sendImage("oktest", "1234543", filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Picasso.get().load(uri).into(imageView);
            //Picasso.with(this).load(imgUri).into(adminImageView);
        }
    }
    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader;
        loader = new CursorLoader(getApplicationContext(),  contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public static String encodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("ImageLog:", imageEncoded);
        return imageEncoded;
    }

    public void sendImage(final String name, final String mobile, final String imagePath) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getInt("error") == 1) {
                        Toast.makeText(MainActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }else if (object.getInt("error")==0){
                        Toast.makeText(MainActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("API", "REGISTRATION_DEMO");
                map.put("name", name);
                map.put("mobile", mobile);
                map.put("image", imagePath);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }


    // upload image API


//    case 'REGISTRATION_DEMO':{
//        $name = $_POST["name"];
//        $mobile = $_POST["mobile"];
//        $image= $_FILES['image']['name'];
//
//        $sql2 = "insert into registration_demo(name, mobile, image) values('$name','$mobile', '$image')";
//        $upload_path = "image_demo/".$name.".jpg";
//        file_put_contents($upload_path, base64_decode($image));
//        //move_uploaded_file($_FILES['image']['tmp_name'], "image_demo/$image");
//
//
//        $result2 = $con->query($sql2);
//        $insertid = mysqli_insert_id($con);
//        if($result2)
//        {
//
//            $data['msg'] = 'User Registered';
//            $data['error'] = 0;
//
//
//        }else{
//            $data['msg'] = 'Something went wrong!';
//            $data['error'] = 1;
//        }
//    }
//    echo json_encode($data);
//
//	break;
}

