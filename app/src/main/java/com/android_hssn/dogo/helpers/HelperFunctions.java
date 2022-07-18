package com.android_hssn.dogo.helpers;

import static android.content.pm.PackageManager.GET_META_DATA;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;


import com.android_hssn.dogo.R;
import com.android_hssn.dogo.helpers.BitmapUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Class that provides set of helper functions used in the project
 */
public class HelperFunctions {


    public static final String DATE_FORMATE_MM_DD_YYYY = "MM-DD-YYYY";
    public static final String DATE_FORMATE_DD_MM_YYYY = "DD-MM-YYYY";
    public static final String DATE_FORMATE_YYYY_MM_DD = "YYYY-MM-DD";
    public static final Comparator<String> SortStrings = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
        }
    };
    public static final Comparator<String> SortAlphabeticalOrder = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    };
   
    public static String ENVIRONMENT_DEV = "dev";
    public static String ENVIRONMENT_STAGING = "staging";
    public static String ENVIRONMENT_LIVE = "live";
    public static Context mContext;
    public static String AppPackage = "";
    private static char DOT = '\u2022';
    private static String emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static Dialog mDialog;

    public static void SetAppContext(Context c) {
        mContext = c;
        if (mContext != null)
            AppPackage = mContext.getPackageName();
    }

    public static int dipToPixels(Context context, int dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    //This function takes the Image as parameter and returns Image with half size
    public static Drawable resize(Drawable image, Resources res) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Log.i("width", b.getWidth() + "");
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, b.getWidth() / 2, b.getHeight() / 2, false);
        return new BitmapDrawable(res, bitmapResized);
    }

    //Email Validation
    public static boolean isValidEmail(String email) {
        if (email.matches(emailPattern) && email.length() > 0) {
            return true;
        } else {
            return false;
        }

    }


    //this function apply tint color to image view
    public static void applyTintColor(ImageView imageView, Context context) {
        if (null != imageView)
            imageView.setColorFilter(context.getResources().getColor(R.color.blue));
    }


    //Check for Empty Fields
    public static ArrayList<EditText> isTextFieldEmpty(EditText... editTexts) {
        ArrayList<EditText> retEdit = new ArrayList<EditText>();
        for (EditText et : editTexts) {
            if (et.getText().toString().length() < 1) {
                retEdit.add(et);
            }
        }
        return retEdit;
    }

    public static boolean isInputEmpty(EditText editText) {
        if (null != editText && editText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    public static String TIME_ZONE = "Europe/Berlin";
    public static final String CONS_DATE = "date";
    public static final String CONS_TIME = "time";
    public static final String CONS_DATE_TIME = "date_time";


    public static String ConvertGermanToLocalDateTime(final String type, final String inputText) {
        String outputText = "";

        Locale LocaleToUse = Locale.US;
        Locale systemLocale = Locale.getDefault();
        String FinalLocale = GetLocale(systemLocale.toString());

        if (FinalLocale.equals("de")) {
            LocaleToUse = Locale.GERMAN;
        } else {
            LocaleToUse = Locale.US;
        }

        if (type.equals(CONS_DATE_TIME)) {

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", LocaleToUse);

                outputFormat.setTimeZone(TimeZone.getDefault());
                Date date = inputFormat.parse(inputText);
                outputText = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (type.equals(CONS_DATE)) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(
                        "yyyy-MM-dd", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", LocaleToUse);

                outputFormat.setTimeZone(TimeZone.getDefault());
                Date date = inputFormat.parse(inputText);
                outputText = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(
                        "HH:mm:ss", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", LocaleToUse);

                outputFormat.setTimeZone(TimeZone.getDefault());
                Date date = inputFormat.parse(inputText);
                outputText = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return outputText;
    }

    public static void hideSoftKeyboard(Activity activity, Resources resources) {
        if (resources.getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) { // Check if keyboard is not hidden
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void hideSoftKeyboardOnFragment(View view, Activity activity, Resources resources) {
        if (resources.getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) { // Check if keyboard is not hidden
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(EditText et, Context c) {
        if (null == et || null == c)
            return;
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyboard(EditText et, Context c) {
        if (null == et || null == c)
            return;
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static boolean ConvertToBoolean(String value) {
        if (value.equalsIgnoreCase("1") || value.equalsIgnoreCase("true"))
            return true;
        else
            return false;
    }

    public static String ConvertToString(boolean value) {
        if (value)
            return "1";
        else
            return "0";
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String getAppPackage() {
        return AppPackage;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), GET_META_DATA);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), GET_META_DATA);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static String GetLocale(String locale) {
        String result = "en";

        if (null != locale) {
            if (locale.contains("de")) {
                // german
                result = "de";

            } else if (locale.contains("en")) {
                // english
                result = "en";

            }
        }

        return result;

    }

    public static String GetCurrentLocale() {
        Locale systemLocale = Locale.getDefault();
        String locale = systemLocale.toString();
        String result = "en";

        if (null != locale) {
            if (locale.contains("de")) {
                // german
                result = "de";

            } else if (locale.contains("en")) {
                // english
                result = "en";

            } else if (locale.contains("es")) {
                // Spanish
                result = "es";

            }
        }

        return result;

    }

    public static String GetCurrentCountryCode() {

        String locale  = mContext.getResources().getConfiguration().locale.getCountry();

        return locale;

    }


    public static String convertDecimal(String text) {

        if (GetCurrentLocale().contains("de")) {
            text = text.replaceAll("\\.", ",");

        }

        return text;

    }

    public static String getDateFormat(String Type) {

        String format = "";

        switch (Type) {
            case DATE_FORMATE_MM_DD_YYYY:
                format = "MM-dd-yyyy";
                break;
            case DATE_FORMATE_DD_MM_YYYY:
                format = "dd-MM-yyyy";
                break;
            case DATE_FORMATE_YYYY_MM_DD:
                format = "yyyy-MM-dd";
                break;

            default:
                format = "dd-MM-yyyy";
                break;
        }

        return format;

    }

    public static String FormatDateStringToDefault(String Type, String Date) {
        SimpleDateFormat inputFormat = null;
        SimpleDateFormat outputFormat = null;
        String formated_date = "";
        Locale LocaleToUse = Locale.US;
        Locale systemLocale = Locale.getDefault();
        String FinalLocale = GetLocale(systemLocale.toString());

        if (FinalLocale.equals("de")) {
            LocaleToUse = Locale.GERMAN;
        } else {
            LocaleToUse = Locale.US;
        }

        try {
            inputFormat = new SimpleDateFormat(
                    getDateFormat(Type), Locale.US);
            outputFormat = new SimpleDateFormat("yyyy-MM-dd", LocaleToUse);
            outputFormat.setTimeZone(TimeZone.getDefault());
            java.util.Date date = inputFormat.parse(Date);
            formated_date = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return formated_date;

    }

    public static String displayTimeZone(TimeZone tz) {

        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);

        String result = "";
        if (hours > 0) {
            result = String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID());
        } else {
            result = String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
        }

        return result;

    }

    public static String getDeviceModel() {

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceManufacturerAndModel = manufacturer.replaceAll(" ", "")
                .toUpperCase() + model.replaceAll(" ", "").replaceAll("-", "").toUpperCase();

        return deviceManufacturerAndModel;
    }

    public static void showMessage(final Context c, final String msg) {
        showMessage(c, msg, null);
    }

    public static void showMessage(final Context c, final String msg, String title) {
        try {
            ((Activity) c).runOnUiThread(() -> {
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
                mDialog = new Dialog(c);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setContentView(R.layout.layout_dialog_error);
                mDialog.setCancelable(false);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(c.getResources().getColor(R.color.transparent)));
                mDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

                TextView heading = mDialog.findViewById(R.id.heading);

                if (title != null) {
                    heading.setText(title);
                }

                TextView decs = (TextView) mDialog.findViewById(R.id.decs);
                Button negative = (Button) mDialog.findViewById(R.id.negative);
                Button positive = (Button) mDialog.findViewById(R.id.positive);
                positive.setVisibility(View.GONE);
                decs.setText(msg);

                negative.setOnClickListener(v -> mDialog.dismiss());

                positive.setOnClickListener(v -> mDialog.dismiss());
                mDialog.show();

            });
        } catch (WindowManager.BadTokenException ex) {

        }
    }

    public static void dismissDialog() {
        try {
            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
        } catch (WindowManager.BadTokenException ex) {
            Log.e("dismissDialog", "BadTokenException");
        } catch (final IllegalArgumentException e) {
            Log.e("dismissDialog", "IllegalArgumentException");
        } catch (final Exception e) {
            Log.e("dismissDialog", "Exception");
        }
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    

    public static String getDigit(String quote, Locale locale) {
        // Log.e("VALUE", quote);
        char decimalSeparator;
        if (locale == null) {
            decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
        } else {
            decimalSeparator = new DecimalFormatSymbols(locale).getDecimalSeparator();
        }

        String regex = "[^0-9" + decimalSeparator + "]";
        String valueOnlyDigit = quote.replaceAll(regex, "");
        try {
            // Log.e("VALUE", quote +"====>"+valueOnlyDigit);
            return valueOnlyDigit.replace(",",".");
        } catch (ArithmeticException | NumberFormatException e) {
            //  Log.e("VALUE", "Error in getMoneyAsDecimal", e);
            return null;
        }
    }


 

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static String getBuildVerions(Context context) {
        String versionName = "", versionCode = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), GET_META_DATA);
            versionName = pInfo.versionName;
            versionCode = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "V".concat(versionCode).concat("(").concat(versionName).concat(")");

    }






    public static String getCurrentDateTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);


    }

    public static void saveFileInPath(Bitmap bitmap, String path, String fName, Context c) {
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }

        String destinationFilename = path + File.separator + "" + fName + ".jpg";

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(destinationFilename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }

    }

    public static String getMaskedCreditCardNumber(String CardNo) {
        String initials = CardNo.substring(0, 4);
        String finals = CardNo.substring(12, CardNo.length());
        String mid = "";
        for (int i = 0; i < 8; i++) {
            mid = mid + DOT;
        }
        return initials + mid + finals;
    }

    public static String getNoInitialsMaskedCreditCardNumber(String CardNo, boolean addSpaces) {
        String finals = CardNo.substring(12, CardNo.length());
        String dots = "";
        for (int i = 0; i < 12; i++) {
            dots = dots + DOT;
        }

        String dottedString = dots + finals;

        if (addSpaces) {
            Editable s = new SpannableStringBuilder(dottedString);
            for (int i = 0; i < s.length(); i++) {
                if (i % 5 == 0) {
                    s.insert(i, " ");
                }
            }
            return s.toString();
        } else {
            return dottedString;
        }

    }

    public static Date stringToDate(String string_date, SimpleDateFormat simpleDateFormat) {
        try {
            Date date = simpleDateFormat.parse(string_date);
            Log.e("DATE", "Date : " + date);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int getViewHeight(View view) {
        WindowManager wm =
                (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int deviceWidth;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            deviceWidth = size.x;
        } else {
            deviceWidth = display.getWidth();
        }

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }

    public static int getUniqueInteger(String name) {
        String plaintext = name;
        int hash = name.hashCode();
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(10);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            int temp = 0;
            for (int i = 0; i < hashtext.length(); i++) {
                char c = hashtext.charAt(i);
                temp += (int) c;
            }
            return hash + temp;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static Uri saveImageExternalForSharing(Bitmap image, Context c) {
        Uri uri = null;
        try {
            File file = new File(c.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "to_share_" + System.currentTimeMillis() + ".png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            Log.e("saveImage", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static Uri saveImageForSharing(Bitmap image, Context c) {

        File imagesFolder = new File(c.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image" + System.currentTimeMillis() + ".png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(c, "com.app_4art.technologies.fileprovider", file);

        } catch (IOException e) {
            Log.e("saveImage", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    /**
     * get uri to drawable or any other resource type if u wish
     *
     * @param context    - context
     * @param drawableId - drawable res id
     * @return - uri
     */
    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId));
        return imageUri;
    }

    /**
     * get uri to any resource type
     *
     * @param context - context
     * @param resId   - resource id
     * @return - Uri to resource by given id
     * @throws Resources.NotFoundException if the given ID does not exist.
     */
    public static final Uri getUriToResource(@NonNull Context context,
                                             @AnyRes int resId)
            throws Resources.NotFoundException {
        /** Return a Resources instance for your application's package. */
        Resources res = context.getResources();
        /**
         * Creates a Uri which parses the given encoded URI string.
         * @param uriString an RFC 2396-compliant, encoded URI
         * @throws NullPointerException if uriString is null
         * @return Uri for this given uri string
         */
        Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/' + res.getResourceTypeName(resId)
                + '/' + res.getResourceEntryName(resId));
        /** return uri */
        return resUri;
    }

    String folder = "";



    /**
     * get bytes array from Uri.
     *
     * @param context current context.
     * @param uri     uri fo the file to read.
     * @return a bytes array.
     * @throws IOException
     */
    public static byte[] getBytesFromUri(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        try {
            return getBytes(iStream);
        } finally {
            // close the stream
            try {
                iStream.close();
            } catch (IOException ignored) {
                Log.e("getBytesFromUri", ignored.getMessage());
                /* do nothing */
            }
        }
    }


    /**
     * get bytes from input stream.
     *
     * @param inputStream inputStream.
     * @return byte array read from the inputStream.
     * @throws IOException
     */
    public static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try {
                byteBuffer.close();
            } catch (IOException ignored) {
                Log.e("getBytes", ignored.getMessage());
                /* do nothing */
            }
        }
        return bytesResult;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public static void showCustomDialog(Context context, String title, String message, String buttonText) {

        final Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_dialog);
        mDialog.setCancelable(false);
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        mDialog.getWindow().getDecorView()
                .setBackgroundResource(android.R.color.transparent);
        TextView heading = mDialog.findViewById(R.id.heading);
        TextView decs = mDialog.findViewById(R.id.decs);
        Button negative = mDialog.findViewById(R.id.negative);
        Button positive = mDialog.findViewById(R.id.positive);

        heading.setText(title);
        decs.setText(message);

        negative.setText(buttonText);
        positive.setVisibility(View.INVISIBLE);

        negative.setOnClickListener(v -> mDialog.dismiss());
        mDialog.show();

    }


    public static boolean isValidString(String value) {
        return value != null && !value.equals("") && !value.equals("null");
    }

    public static String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            name = name.substring(0, pos);
        }

        return name;
    }

    public static String getCurrentSyncDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public static String getSDCardPath(Context c) {
        return Environment.getExternalStorageDirectory() + "/Android/data/"
                + c.getPackageName() + "/files/Data/";
    }




    public static String saveThumbnailImage(Drawable drawable, String path, String file_name) {
        String savedImagePath = null;
        Bitmap image = BitmapUtils.drawableToBitmap(drawable);

        File storageDir = new File(path);
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, file_name);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return savedImagePath;
    }

    public static String saveBitmapImage(Bitmap drawable, String path, String file_name) {
        String savedImagePath = null;

        File storageDir = new File(path);
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        if (success) {
            File imageFile = new File(storageDir, file_name);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                drawable.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return savedImagePath;
    }

    public static String saveArtworkDocumentFile(Uri sourceuri, Context c, String path, String fName) {
        BitmapUtils mBitmapUtils = new BitmapUtils();
        return mBitmapUtils.compressImage(sourceuri, c, path, fName);

    }

    public static String getStringResourceByName(String aString, List<String> list) {
        String packageName = mContext.getPackageName();
        int resId = mContext.getResources().getIdentifier(aString, "string", packageName);

        if (resId == 0) {
            Log.d("getStringResourceByName", aString);
            return aString;
        }
        if (list == null) {
            Log.d("List is empty", aString);
            return "";
        }

        if (list.size() == 0)
            return mContext.getString(resId);
        else if (list.size() == 1)
            return mContext.getString(resId, list.get(0));
        else if (list.size() == 2)
            return mContext.getString(resId, list.get(0), list.get(1));
        else if (list.size() == 3)
            return mContext.getString(resId, list.get(0), list.get(1), list.get(2));
        else if (list.size() == 4)
            return mContext.getString(resId, list.get(0), list.get(1), list.get(2), list.get(3));
        return mContext.getString(resId, list, list);
    }

    public static String getMonthName(int index, final boolean shortName, boolean UpperCase) {
        Locale systemLocale = Locale.getDefault();
        if (index < 0)
            index = 0;
        String format = "%tB";

        if (shortName)
            format = "%tb";

        Calendar calendar = Calendar.getInstance(systemLocale);
        calendar.set(Calendar.MONTH, index);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        if (UpperCase) {
            return String.format(systemLocale, format, calendar).toUpperCase(systemLocale);
        } else {
            return String.format(systemLocale, format, calendar);
        }
    }

    public static void setTextInputLayoutStateError(TextInputLayout textInputLayout, Context c) {
        if (textInputLayout != null) {
            int color_red = c.getResources().getColor(R.color.red);
            ColorStateList colorStateList = ColorStateList.valueOf(color_red);
            textInputLayout.setDefaultHintTextColor(colorStateList);
        }
    }

    public static Date localToGMT(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gmt = new Date(sdf.format(date));
        return gmt;
    }

    public static Date gmttoLocalDate(Date date) {

        String timeZone = Calendar.getInstance().getTimeZone().getID();
        Date local = new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
        return local;
    }

    public static void setTextInputLayoutStateDefault(TextInputLayout textInputLayout, Context c) {
        if (textInputLayout != null) {
            int hint_color = c.getResources().getColor(R.color.hint_color);
            int dark_gray = c.getResources().getColor(R.color.dark_gray);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_pressed}, //1
                            new int[]{android.R.attr.state_focused}, //2
                            new int[]{android.R.attr.state_focused, android.R.attr.state_pressed}, //3
                            new int[]{-android.R.attr.state_focused} //3
                    },
                    new int[]{
                            dark_gray, //1
                            dark_gray, //2
                            dark_gray, //3
                            hint_color //4
                    }
            );
            textInputLayout.setDefaultHintTextColor(colorStateList);
        }
    }


    private static ProgressDialog mProgressDialog;

    public static void showProgress(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public static void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static void openBrowser(Context context, String url) {
       /* Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);*/

        Uri webpage = Uri.parse(url);

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            webpage = Uri.parse("http://" + url);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void vibrate(Context context, int milliSeconds){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliSeconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(milliSeconds);
        }
    }
}
