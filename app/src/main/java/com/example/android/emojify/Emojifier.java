package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class Emojifier {
    final private static String TAG = Emojifier.class.getSimpleName();

    private static final float EMOJI_SCALE_FACTOR = .9f;
    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    public static Bitmap detectFacesAndOverLayEmoji(Context context, Bitmap pic) {
        FaceDetector detector = new FaceDetector.Builder(context)
                // improves performance by disabling tracking
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(pic).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        Bitmap resultBitmap = pic;

        if(faces.size() == 0) {
            Toast.makeText(context, "No faces were detected! :-(",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Number of faces detected: " +
            faces.size(), Toast.LENGTH_SHORT).show();

            for(int i = 0; i < faces.size(); i++) {

                Face face = faces.get(i);

                getClassifications(face);

                Emoji emoji = whichEmoji(face);

                Bitmap emojiBitmap = getBitmapEmoji(context, emoji);

                if(emojiBitmap == null) {
                    Toast.makeText(context, R.string.no_emoji, Toast.LENGTH_SHORT).show();
                }

                resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face);
            }
        }
        detector.release();
        return resultBitmap;
    }

    public static void getClassifications(Face face) {
        Log.d(TAG, "Left eye open: " + face.getIsLeftEyeOpenProbability() + "\n" +
                        "Right eye open: " + face.getIsRightEyeOpenProbability() + "\n" +
                        "Person is smiling: " + face.getIsSmilingProbability());
    }

    public static Emoji whichEmoji(Face face) {
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
        return emoji;
    }

    private static Bitmap getBitmapEmoji(Context context, Emoji emoji) {

        switch (emoji) {
            case SMILE:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.smile);
            case LEFT_WINK:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwink);
            case RIGHT_WINK:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwink);
            case CLOSED_EYE_SMILE:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_smile);
            case FROWN:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.frown);
            case LEFT_WINK_FROWN:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwinkfrown);
            case RIGHT_WINK_FROWN:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwinkfrown);
            case CLOSED_EYE_FROWN:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_frown);
            default:
                return null;
        }
    }

    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
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
