package com.example.pranav.image_effects;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class SecondActivity extends AppCompatActivity {

    private ImageView imageView;
    //private Drawable photo;
    private Bitmap bitmap_imageOriginal, bitmap_imageNew;
    private Button btnInvert, btnFilter, btnSave, btnBack;
    int indicator = 0;

    Handler h = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            imageView.setImageBitmap(bitmap_imageNew);
            btnSave.setEnabled(true);
            indicator = 1;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        btnInvert = (Button) findViewById(R.id.btn_invert);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnFilter = (Button) findViewById(R.id.btn_filter);
        btnBack = (Button) findViewById(R.id.btn_back);
        imageView = (ImageView) findViewById(R.id.image);

        btnSave.setEnabled(false);

        Bundle b  = this.getIntent().getExtras();
        final String uri = b.getString("Uri");
        Uri selectedImage = Uri.parse(uri);
        try{
            InputStream imageStream = getContentResolver().openInputStream(selectedImage);
            bitmap_imageOriginal = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(bitmap_imageOriginal);
        }catch(IOException ex){
            ex.printStackTrace();
            Toast.makeText(SecondActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
        }

        //photo = ContextCompat.getDrawable(this,R.mipmap.brosis);
        //bitmap_imageOriginal = ((BitmapDrawable) photo).getBitmap();

        btnInvert.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                bitmap_imageNew = invert(bitmap_imageOriginal);
                                h.sendEmptyMessage(0);
                            }
                        };
                        Toast.makeText(getApplicationContext(), "Wait for the image to invert!", Toast.LENGTH_SHORT).show();
                        Thread t = new Thread(r);
                        t.start();
                    }
                }
        );

        btnFilter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bitmap_imageNew = applyFilter(bitmap_imageOriginal);
                        imageView.setImageBitmap(bitmap_imageNew);
                        btnSave.setEnabled(true);
                        indicator = 2;
                    }
                }
        );

        btnSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap_imageNew, indicator == 1? "inverted" : "over -layed", "description");
                        Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_LONG).show();
                    }
                }
        );

        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(SecondActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
        );
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap invert(Bitmap original) {

        Bitmap finalImage = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());

        int A, R, G, B;
        int pixelColor;
        int height = original.getHeight();
        int width  = original.getWidth();
        Log.i("pranav", "" +height+" "+width);
        for(int i = 0; i < width ; i++){
            for(int j = 0; j < height; j++){
                pixelColor = original.getPixel(i, j);
                A = Color.alpha(pixelColor);
                R = 255 - Color.red(pixelColor);
                B = 255 - Color.blue(pixelColor);
                G = 255 - Color.green(pixelColor);
                finalImage.setPixel(i, j, Color.argb(A, R , G, B));
            }
        }
        return finalImage;
    }

    public Bitmap applyFilter(Bitmap original){
        Drawable[] layers = new Drawable[2];

        //layers[0] = ContextCompat.getDrawable(this, R.mipmap.brosis);

        layers[0] = new BitmapDrawable(getResources(),original);
        layers[1] = ContextCompat.getDrawable(this, R.mipmap.filter);
        LayerDrawable image = new LayerDrawable(layers);
        return drawableToBitmap(image);
    }
}
