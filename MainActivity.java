package app.ij.mlwithtensorflowlite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import app.ij.mlwithtensorflowlite.R;
import app.ij.mlwithtensorflowlite.ml.Model;

public class MainActivity extends AppCompatActivity {
    Button cameraBtn, galleryBtn;
    ImageView imgView;
    TextView resultView;
    int imgSize = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraBtn = findViewById(R.id.button);
        galleryBtn = findViewById(R.id.button2);
        resultView = findViewById(R.id.result);
        imgView = findViewById(R.id.imageView);

        sLIS();

    }

    private void sLIS() {
        cameraBtn.setOnClickListener(view -> CAM());
        galleryBtn.setOnClickListener(view -> openGallery());
    }

    // Another unused function that performs a simple calculation
    private void unusedFunctionTwo(int a, int b) {
        int sum = a + b;
        Log.d("MainActivity", "Sum of " + a + " and " + b + " is: " + sum);
    }


    private void CAM() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 3);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    private void clas(Bitmap image) {
        try {
            Model model = Model.newInstance(getApplicationContext());

            TensorBuffer inputBuffer = inpubuff(image);

            Model.Outputs outputs = model.process(inputBuffer);
            TensorBuffer outputBuffer = outputs.getOutputFeature0AsTensorBuffer();

            String[] classes = {"Apple", "Banana", "Orange"};
            int maxIndex = confiind(outputBuffer.getFloatArray());
            resultView.setText(classes[maxIndex]);

            model.close();
        } catch (IOException e) {
            // Handle exception
        }
    }

    private TensorBuffer inpubuff(Bitmap image) {
        int imgSize = 32; // Assuming imgSize is defined elsewhere
        int channels = 3; // Assuming the image has RGB channels

        // Calculate the size needed for the ByteBuffer
        int bufferSize = imgSize * imgSize * channels * Float.SIZE / Byte.SIZE;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
        byteBuffer.order(ByteOrder.nativeOrder());

        // Get the pixel values from the image
        int[] intValues = new int[imgSize * imgSize];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        for (int pixel : intValues) {
            byteBuffer.putFloat(((pixel >> 16) & 0xFF) * (1.f / 1));
            byteBuffer.putFloat(((pixel >> 8) & 0xFF) * (1.f / 1));
            byteBuffer.putFloat((pixel & 0xFF) * (1.f / 1));
        }

        // Rewind the ByteBuffer to prepare for reading
        byteBuffer.rewind();

        // Create a TensorBuffer with the specified dimensions
        TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, imgSize, imgSize, channels}, DataType.FLOAT32);
        inputBuffer.loadBuffer(byteBuffer);

        return inputBuffer;
    }

    private void unusedFunction() {
        Log.d("MainActivity", "This is an unused function.");
    }


    private int findImageColor(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int pixelCount = width * height;
        int[] pixels = new int[pixelCount];

        image.getPixels(pixels, 0, width, 0, 0, width, height);

        long redSum = 0;
        long greenSum = 0;
        long blueSum = 0;

        for (int pixel : pixels) {
            redSum += Color.red(pixel);
            greenSum += Color.green(pixel);
            blueSum += Color.blue(pixel);
        }

        int averageRed = (int) (redSum / pixelCount);
        int averageGreen = (int) (greenSum / pixelCount);
        int averageBlue = (int) (blueSum / pixelCount);

        return Color.rgb(averageRed, averageGreen, averageBlue);
    }


    private int confiind(float[] confidences) {
        int maxPos = 0;
        float maxConfidence = 0;
        for (int i = 0; i < confidences.length; i++) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i];
                maxPos = i;
            }
        }
        return maxPos;
    }

    private Bitmap combineImages(Bitmap image1, Bitmap image2) {
        int width = Math.max(image1.getWidth(), image2.getWidth());
        int height = Math.max(image1.getHeight(), image2.getHeight());

        Bitmap combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combinedBitmap);

        // Draw the first image onto the canvas
        canvas.drawBitmap(image1, 0, 0, null);

        // Draw the second image at an offset (e.g., 20 pixels to the right and 20 pixels down)
        canvas.drawBitmap(image2, 20, 20, null);

        return combinedBitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 3) {
                Camera_Result(data);
            } else {
                Gallery_Result(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void Camera_Result(Intent data) {
        Bitmap image = (Bitmap) data.getExtras().get("data");
        image = ThumbnailUtils.extractThumbnail(image, Math.min(image.getWidth(), image.getHeight()), Math.min(image.getWidth(), image.getHeight()));
        imgView.setImageBitmap(image);
        image = Bitmap.createScaledBitmap(image, imgSize, imgSize, false);
        clas(image);
    }

    private void Gallery_Result(Intent data) {
        Uri uri = data.getData();
        Bitmap image = null;
        try {
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgView.setImageBitmap(image);
        image = Bitmap.createScaledBitmap(image, imgSize, imgSize, false);
        clas(image);
    }

    // Function added for demonstration, not used elsewhere


}
