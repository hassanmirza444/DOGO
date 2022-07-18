package com.android_hssn.dogo.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android_hssn.dogo.R;
import com.baurine.permissionutil.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that provides all centralized control of function for permission handling.
 */
public class CheckPermissionUtil {

    /* Request code for WRITE_EXTERNAL_STORAGE permission */
    private static final int WRITE_SD_REQ_CODE = 201;
    /* Request code for READ_EXTERNAL_STORAGE permission */
    private static final int READ_SD_REQ_CODE = 201;
    /* Request code for CAMERA permission */
    private static final int CAMERA_REQ_CODE = 203;

    public static final int REQUEST_PERMISSION_MULTIPLE = 0;


    /*
     * Checks if WRITE_EXTERNAL_STORAGE is granted or not.
     * @param activity Activity to get the context
     * @param callback PermissionUtil.ReqPermissionCallback Call Back Interface
     *
     */
    public static void checkWriteSd(Activity activity,
                                    PermissionUtil.ReqPermissionCallback callback) {
        PermissionUtil.checkPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                WRITE_SD_REQ_CODE,
                activity.getString(R.string.permission_required),
                activity.getString(R.string.storage_permission_required),
                callback);
    }

    /*
     * Checks if READ_EXTERNAL_STORAGE is granted or not.
     * @param activity Activity to get the context
     * @param callback PermissionUtil.ReqPermissionCallback Call Back Interface
     *
     */
    public static void checkReadSd(Activity activity,
                                    PermissionUtil.ReqPermissionCallback callback) {
        PermissionUtil.checkPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                READ_SD_REQ_CODE,
                activity.getString(R.string.permission_required),
                activity.getString(R.string.storage_permission_required),
                callback);
    }


    /*
     * Checks if CAMERA is granted or not.
     * @param activity Activity to get the context
     * @param callback PermissionUtil.ReqPermissionCallback Call Back Interface
     *
     */
    public static void checkCamera(Activity activity,
                                   PermissionUtil.ReqPermissionCallback callback) {
        PermissionUtil.checkPermission(activity,
                Manifest.permission.CAMERA,
                READ_SD_REQ_CODE,
                activity.getString(R.string.permission_required),
                activity.getString(R.string.camera_permission_required),
                callback);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public static boolean checkAndRequestPermissions(Activity activity) {

        int permissionCamera = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permissionWriteExternal = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Permission List
        List<String> listPermissionsNeeded = new ArrayList<>();

        // Camera Permission
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                Log.e("Permissions", "Camera Permission is required for this app to run");
            }
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        // Read/Write Permission
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_PERMISSION_MULTIPLE);
            return false;
        }

        return true;
    }


}
