package com.example.pranav.image_effects;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    Button btn_select,btn_edit;
    ImageView imageView;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_edit = (Button) findViewById(R.id.btn_Edit);
        btn_select = (Button) findViewById(R.id.btn_select);
        imageView = (ImageView) findViewById(R.id.image_view);

        btn_edit.setEnabled(false);

        btn_edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(MainActivity.this, SecondActivity.class);
                        Bundle values = new Bundle();
                        values.putString("Uri", String.valueOf(selectedImage));
                        i.putExtras(values);
                        startActivity(i);
                        finish();
                    }
                }
        );

        btn_select.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectImage();
                    }
                }
        );
    }

    public void SelectImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case GALLERY_REQUEST:
                    selectedImage = data.getData();
                    try{
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap image = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(image);
                    }catch(IOException ex){
                        ex.printStackTrace();
                        Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
                    }
            }
        }
        btn_edit.setEnabled(true);
    }

}
