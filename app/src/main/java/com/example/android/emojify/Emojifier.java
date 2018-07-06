package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class Emojifier {
    final private static String TAG = Emojifier.class.getSimpleName();

    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    public static void detectFaces(Context context, Bitmap pic) {
        FaceDetector detector = new FaceDetector.Builder(context)
                // improves performance by disabling tracking
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(pic).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        if(faces.size() == 0) {
            Toast.makeText(context, "No faces were detected! :-(",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Number of faces detected: " +
            faces.size(), Toast.LENGTH_SHORT).show();

            for(int i = 0; i < faces.size(); i++) {
                getClassifications(faces.get(i));
                whichEmoji(faces.get(i));
            }
        }

        detector.release();
    }

    public static void getClassifications(Face face) {
        Log.d(TAG, "Left eye open: " + face.getIsLeftEyeOpenProbability() + "\n" +
                        "Right eye open: " + face.getIsRightEyeOpenProbability() + "\n" +
                        "Person is smiling: " + face.getIsSmilingProbability());
    }

    public static void whichEmoji(Face face) {
        boolean smiling = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;
        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;

        Emoji emoji;
        if(smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            }
            else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK;
            }
            else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_SMILE;
            }
            else {
                emoji = Emoji.SMILE;
            }
        }
        else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            }
            else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK_FROWN;
            }
            else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_FROWN;
            }
            else {
                emoji = Emoji.FROWN;
            }
        }
    }

    private enum Emoji {
        SMILE,
        LEFT_WINK,
        RIGHT_WINK,
        CLOSED_EYE_SMILE,

        FROWN,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_FROWN,
    }
}
