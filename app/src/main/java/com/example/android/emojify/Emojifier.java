package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class Emojifier {

    public static void detectFaces(Context context, Bitmap pic) {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(pic).build();

        SparseArray<Face> faces = detector.detect(frame);

        if(faces.size() == 0) {
            Toast.makeText(context, "No faces were detected! :-(",
                    Toast.LENGTH_SHORT).show();
        }
        else if (faces.size() == 1) {
            Toast.makeText(context, "One face detected!",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Number of faces detected: " +
            faces.size(), Toast.LENGTH_SHORT).show();
        }

        detector.release();
    }
}
