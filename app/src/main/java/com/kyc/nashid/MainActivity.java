package com.kyc.nashid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.kyc.nashidmrz.SelectDocumentActivity;
import com.kyc.nashidmrz.Utility;
import com.kyc.nashidmrz.mrtd2.SkipNFCLivnessActivity;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    TextView mrztext;
    int REQUEST_CODE_ASK_PERMISSIONS = 100;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mrztext = findViewById(R.id.mrztext);

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);

        } else {
           /* Intent i = new Intent(MainActivity.this, SelectDocumentActivity.class);
            startActivity(i);*/
            Intent intent = new Intent(MainActivity.this, SkipNFCLivnessActivity.class);
            Bitmap croppedImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedImageBitmap, 500, 500, true);
            SkipNFCLivnessActivity.documentCroppedPhoto = croppedImageBitmap;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            croppedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            croppedImageBitmap.recycle();
            Utility.getInstance().scannedImage=byteArray;
            intent.putExtra(getResources().getString(R.string.doc_key), "E-passport");
//            intent.putExtra("BitmapImage", scaledBitmap);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 503) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mrztext.setText(Utility.getInstance().getPassportNumber());
                }
            }, 0);

        }
    }

    private void gotoRead() {
//        Intent intent = new Intent(ComparisionSuccessful.this, MainActivityMRZ.class);
//        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(MainActivity.this, SelectDocumentActivity.class);
                startActivity(i);
            } else {

                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                }
            }

//                }
//                Toas/t.makeText(this, "Camera permission not granted", Toast.LENGTH_SHORT).show();


        }
    }

}
