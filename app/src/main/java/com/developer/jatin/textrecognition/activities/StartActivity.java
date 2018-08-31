package com.developer.jatin.textrecognition.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.developer.jatin.textrecognition.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import java.util.List;

public class StartActivity extends AppCompatActivity {

	private static final int REQUEST_IMAGE_CAPTURE = 1;

	private ImageView imageView;
	private TextView txtView, helpText;
	private Bitmap imageBitmap;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FirebaseApp.initializeApp(this);
		setContentView(R.layout.activity_start);
		Button snapBtn = findViewById(R.id.snapBtn);
		Button detectBtn = findViewById(R.id.detectBtn);
		helpText = findViewById(R.id.help_text);
		imageView = findViewById(R.id.imageView);
		txtView = findViewById(R.id.txtView);
		snapBtn.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				dispatchTakePictureIntent();
			}
		});
		detectBtn.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				if (imageBitmap != null) {
					detectTxt();
				} else {
					Toast.makeText(StartActivity.this, "Please snap a picture first",
						Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			imageBitmap = (Bitmap) extras.get("data");
			imageView.setVisibility(View.VISIBLE);
			helpText.setVisibility(View.GONE);
			imageView.setImageBitmap(imageBitmap);
		}
	}

	private void detectTxt() {
		FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
		FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
		detector.detectInImage(image)
			.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
				@Override public void onSuccess(FirebaseVisionText firebaseVisionText) {
					processTxt(firebaseVisionText);
				}
			})
			.addOnFailureListener(new OnFailureListener() {
				@Override public void onFailure(@NonNull Exception e) {
				}
			});
	}

	private void processTxt(FirebaseVisionText text) {
		List<FirebaseVisionText.Block> blocks = text.getBlocks();
		if (blocks.size() == 0) {
			Toast.makeText(StartActivity.this, "No Text :(", Toast.LENGTH_LONG).show();
			return;
		}

		StringBuilder txt = new StringBuilder();
		for (FirebaseVisionText.Block block : text.getBlocks()) {
			txt.append(block.getText()).append(" ");
		}
		txtView.setText(txt.toString());
	}
}
