package com.exam.android.kunj.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
public class ImagePickerUtils {

    private static final String TAG = "ImagePickerUtils";
    private static final String PHOTO_ALBUM_NAME= "Gallery";
    private static final String JPG_FILE_PREFIX = "IMG_";
    private static final String JPG_FILE_SUFFIX = ".jpg";

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = JPG_FILE_PREFIX + timeStamp + "_";
        File imageF = File.createTempFile(imageFileName, JPG_FILE_SUFFIX, getAlbumDir());
        Log.d(TAG, "Utils: createImageFile: tempFile: "+imageF.getAbsolutePath());
        return imageF;
    }

    public static File createImageFile(String imageFileName) throws IOException {
        // Create an image file name
        File imageF = File.createTempFile(imageFileName, JPG_FILE_SUFFIX, getAlbumDir());
        Log.d(TAG, "Utils: createImageFile: tempFile: "+imageF.getAbsolutePath());
        return imageF;
    }

    private static File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = getRequiredAlbumDir(PHOTO_ALBUM_NAME);
            if (storageDir != null) {
                if (!storageDir.exists()) {
                    if (!storageDir.mkdirs()) {
                        Log.d(TAG, "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.e(TAG, "Utils: getAlbumDir: External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    //Return required Album Dir on SDCard
    private static File getRequiredAlbumDir(String albumName) {
            File imageAppDir = new File(Environment.getExternalStorageDirectory()+"/ImageApp");
            if(!imageAppDir.exists() && !imageAppDir.mkdir()) {
                Log.e(TAG, "Utils: getRequiredAlbumDir: failed to create 'ImageApp' directory");
                return null;
            }
            File galleryDir = new File(imageAppDir.getAbsolutePath() + "/Gallery");
            if(!galleryDir.exists() && !galleryDir.mkdir()) {
                Log.e(TAG, "Utils: getRequiredAlbumDir: failed to create 'Gallery' directory");
                return null;
            }
            return galleryDir;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String compressImage(String filePath) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        /*by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
         *you try the use the bitmap here, you will get null.*/
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        if(actualHeight <= 0 && actualWidth <= 0) {
            return filePath;
        }

        /*max Height and width values of the compressed image is taken as 816x612*/
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        /*width and height values are set maintaining the aspect ratio of the image*/
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        /*setting inSampleSize value allows to load a scaled down version of the original image*/
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        /*inJustDecodeBounds set to false to load the actual bitmap*/
        options.inJustDecodeBounds = false;

       /*this options allow android to claim the bitmap memory if it runs low on memory*/
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            /*load the bitmap from its path*/
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        /*check the rotation of the image and display it properly*/
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out;
        try {
            out = new FileOutputStream(filePath);

            /*write the compressed bitmap at the destination specified by filename.*/
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
