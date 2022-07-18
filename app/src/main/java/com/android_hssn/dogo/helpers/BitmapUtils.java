package com.android_hssn.dogo.helpers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class BitmapUtils {
    public BitmapUtils() {

    }

    /**
     * Converts bitmap to byte array in PNG format
     *
     * @param bitmap source bitmap
     * @return result byte array
     */
    public static byte[] convertBitmapToByteArray(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(format, 100, baos);
            return baos.toByteArray();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    Log.e(BitmapUtils.class.getSimpleName(), "ByteArrayOutputStream was not closed");
                }
            }
        }
    }

    public static Bitmap uriToBitmap(Context mContext, Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    mContext.getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap mImage = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
            return mImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts bitmap to the byte array without compression
     *
     * @param bitmap source bitmap
     * @return result byte array
     */
    public static byte[] convertBitmapToByteArrayUncompressed(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byteBuffer.rewind();
        return byteBuffer.array();
    }

    /**
     * Converts compressed byte array to bitmap
     *
     * @param src source array
     * @return result bitmap
     */
    public static Bitmap convertCompressedByteArrayToBitmap(byte[] src) {
        return BitmapFactory.decodeByteArray(src, 0, src.length);
    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public static Bitmap resizeAndCropCenter(Bitmap bitmap, int size, boolean recycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if (w == size && h == size) return bitmap;
        // scale the image so that the shorter side equals to the target;
        // the longer side will be center-cropped.
        float scale = (float) size / Math.min(w, h);
        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        int width = Math.round(scale * bitmap.getWidth());
        int height = Math.round(scale * bitmap.getHeight());
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2f, (size - height) / 2f);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) bitmap.recycle();
        return target;
    }

    private static Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        return config;
    }


    public static void createScaledBitmap(Context c, String path, Float percentage, String fName) {
        Bitmap srcBitmap = null;
        File file = new File(path);
        String mDirectoryPath = file.getParent();
        Uri fileUri = Uri.fromFile(file);
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    c.getContentResolver().openFileDescriptor(fileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            srcBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            int srcWidth = srcBitmap.getWidth();
            int srcHeight = srcBitmap.getHeight();
            int dstWidth = (int) (srcWidth * percentage);
            int dstHeight = (int) (srcHeight * percentage);
            Bitmap dstBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, true);
            HelperFunctions.saveFileInPath(dstBitmap, mDirectoryPath, fName, c);
            dstBitmap.recycle();
            srcBitmap.recycle();
            dstBitmap = null;
            srcBitmap = null;
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void createScaledBitmap(Context c, Uri fileUri, String mDirectoryPath, Float percentage, String fName) {
        Bitmap srcBitmap = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    c.getContentResolver().openFileDescriptor(fileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            srcBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            int srcWidth = srcBitmap.getWidth();
            int srcHeight = srcBitmap.getHeight();
            int dstWidth = (int) (srcWidth * percentage);
            int dstHeight = (int) (srcHeight * percentage);
            Bitmap dstBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, true);
            HelperFunctions.saveFileInPath(dstBitmap, mDirectoryPath, fName, c);
            dstBitmap.recycle();
            srcBitmap.recycle();
            dstBitmap = null;
            srcBitmap = null;
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void createScaledBitmap(Context c, Bitmap srcBitmap, String mDirectoryPath, Float percentage, String fName) {

            int srcWidth = srcBitmap.getWidth();
            int srcHeight = srcBitmap.getHeight();
            int dstWidth = (int) (srcWidth * percentage);
            int dstHeight = (int) (srcHeight * percentage);
            Bitmap dstBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, true);
            HelperFunctions.saveFileInPath(dstBitmap, mDirectoryPath, fName, c);
            dstBitmap.recycle();
            srcBitmap.recycle();
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public String compressImage(Uri sourceuri, Context c, String path, String fName) {
        String imageUri = sourceuri.getPath();
        String destinationFilename = path + File.separator + fName;

        String filePath = getRealPathFromURI(imageUri, c);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 4032.0f;
        float maxWidth = 3024.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

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

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
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

//      check the rotation of the image and display it properly
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

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destinationFilename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return destinationFilename;

    }


    private String getRealPathFromURI(String contentURI, Context c) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = c.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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



    private Bitmap drawWatermarkOnBitmap(Bitmap src, Context mContext) {
        Bitmap bm = src.copy(Bitmap.Config.ARGB_8888, true);
        Canvas cv = new Canvas(bm);
        cv.drawBitmap(bm, 0, 0, null);
        final String captionString = "COPY";
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.RED);
        paintText.setAlpha(128);
        paintText.setTextSize(setTextSizeForWidth(paintText, bm.getWidth() - HelperFunctions.convertDpToPixel(5, mContext), captionString));
        paintText.setStyle(Paint.Style.FILL);
        Rect rectText = new Rect();
        paintText.getTextBounds(captionString, 0, captionString.length(), rectText);
        cv.drawText(captionString,
                0, (int) ((bm.getHeight() / 2) + (rectText.height() / 2)), paintText);
        return bm;
    }

    private float setTextSizeForWidth(Paint paint, float desiredWidth,
                                      String text) {
        final float testTextSize = 48f;
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();
        return desiredTextSize;
    }


    public static Bitmap DrawQRcodeOverlay(Bitmap bmp1, Bitmap qrcode, Context mContext) {
        int bmp1_X = bmp1.getWidth();
        int bmp1_Y = bmp1.getHeight();
        int qr_X = qrcode.getWidth();
        int qr_Y = qrcode.getHeight();
        int qr_X_new = (int) HelperFunctions.convertDpToPixel(60, mContext);
        int qr_Y_new = (int) HelperFunctions.convertDpToPixel(60, mContext);
        Bitmap qrcodeScaled = ResizeImageWithAspectRatio(qrcode,qr_X_new,qr_Y_new);
        int left = bmp1_X - qr_X_new;
        int top = bmp1_Y - qr_Y_new;

        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(qrcodeScaled, left, top, null);
        return bmOverlay;
    }


    private static Bitmap ResizeImageWithAspectRatio(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
}