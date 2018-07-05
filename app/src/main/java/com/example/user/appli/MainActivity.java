package com.example.user.appli;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mysql.jdbc.Blob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText text, text1;
    Button b,b1;
    ImageView img;
    RequestQueue requestQueue;
    private Bitmap bitmap;
    static final int REQUEST_IMAGE_CAPTURE=1;
    String url="http://192.168.1.12/insert_record.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                text = (EditText) findViewById(R.id.gstnumber);
                text1 = (EditText) findViewById(R.id.shopname);
                b = (Button) findViewById(R.id.submitbutton);
                b1=(Button)findViewById(R.id.fetchbutton);
                img=(ImageView)findViewById(R.id.imageView);

                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       dispatchTakePictureIntent();
                    }
                });
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (text.getText().length() != 0 && text1.getText().length() != 0 ) {
                            final String gstid = text.getText().toString().trim();
                            final String shopname = text1.getText().toString().trim();
                           final  Bitmap bitmap= ((BitmapDrawable)img.getDrawable()).getBitmap();

                           // upload();
                           StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String ServerResponse) {
                                            // Showing response message coming from server.
                                            Toast.makeText(MainActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {
                                            // Showing error message if something goes wrong.
                                            Toast.makeText(MainActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {

                                    // Creating Map String Params.
                                    Map<String, String> params = new HashMap<String, String>();
                                    // Adding All values to Params.
                                    params.put("gstid", gstid);
                                    params.put("shopname", shopname);
                                    params.put("image", getStringImage(bitmap));
                                    return params;
                                }
                            };
                            // Creating RequestQueue.
                            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                            // Adding the StringRequest object into requestQueue.
                            requestQueue.add(stringRequest);
                        }

                    }



                });
b1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
getSqlDetails();

    }
});
            }
     public void dispatchTakePictureIntent(){
        Intent takepictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takepictureIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takepictureIntent,REQUEST_IMAGE_CAPTURE);
        }
}
      protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==REQUEST_IMAGE_CAPTURE&&resultCode==RESULT_OK){
            Bundle extras=data.getExtras();
            Bitmap imageBitmap=(Bitmap)extras.get("data");
            img.setImageBitmap(imageBitmap);
        }


}


    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    
        private  void upload(){
    final String gstid = text.getText().toString().trim();
    final String shopname = text1.getText().toString().trim();
    final  Bitmap myBitmap= ((BitmapDrawable)img.getDrawable()).getBitmap();

    StringRequest  stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(MainActivity.this, Response, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("gstid",gstid);
                params.put("shopname",shopname);
                params.put("image",getStringImage(bitmap));

                return params;
            }
        };


    RequestHandler.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
}

    public   void getSqlDetails() {
        String name=text1.getText().toString();

        String url = "http://192.168.1.12/fetchimage.php?name=" + name;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonarray = new JSONArray(response);
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                String image = jsonobject.getString("image");
                                Log.e("image",image);
           ImageView imagview= (ImageView) findViewById(R.id.imageView);
        String src = (image);
        new AsyncTaskLoadImage(imagview).execute(src);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        } }
                }

        );
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    public class AsyncTaskLoadImage extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";
        private ImageView imageView;

        public AsyncTaskLoadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            try {
                int width, height;
                height = bitmap.getHeight();
                width = bitmap.getWidth();

                Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bmpGrayscale);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);
                ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(f);
                c.drawBitmap(bitmap, 0, 0, paint);
                imageView.setImageBitmap(bmpGrayscale);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}