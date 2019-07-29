package com.example.textdetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class MainActivity extends AppCompatActivity {

    private Button camereButton;
    private final static int REQUEST_CAMERA_CAPTURRE=121;
    private FirebaseVisionTextRecognizer textRecognizer;
    FirebaseVisionImage image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        camereButton = findViewById(R.id.camera_button);
        camereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takepic.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(takepic,REQUEST_CAMERA_CAPTURRE);
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CAMERA_CAPTURRE && requestCode  == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap= (Bitmap)extras.get("data");
            recognizeText(bitmap);
        }
    }

    private void recognizeText(Bitmap bitmap) {
        try {
            image =FirebaseVisionImage.fromBitmap(bitmap);
            textRecognizer = FirebaseVision
                    .getInstance()
                    .getOnDeviceTextRecognizer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String resultText = firebaseVisionText.getText();

                        if (resultText.isEmpty()){
                            Toast.makeText(MainActivity.this,"NO TEXT DETECTED",Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                            intent.putExtra(TextRecognization.RESULT_TEXT_,resultText);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
