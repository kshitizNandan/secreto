package com.secreto.base_activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.secreto.R;
import com.secreto.common.Constants;
import com.secreto.common.MyApplication;
import com.secreto.utils.Logger;
import com.secreto.utils.SDCardHandler;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public abstract class ImagePickerActivity extends BaseActivityWithActionBar {

    private static final String TAG = ImagePickerActivity.class.getName();
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_ALL = 500;
    protected static Uri mImageUri, mVideoUri;
    protected static final int RC_CAMERA_REQUEST = 0;
    protected static final int RC_PICK_IMAGE_REQUEST = 1;
    protected static final int RC_CROP_ACTIVITY = CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
    private static File _photoFile;
    private AlertDialog imagePickerDialog;

    public enum CropAspectRatio {
        HORIZONTAL_RECT,
        VERTICAL_RECT
    }

    protected abstract void onImageSet(File photoFile);

    protected abstract CropAspectRatio getCropAspectRatio();

    protected void showTakeImagePopup() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            if (imagePickerDialog == null) {
                final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.select_photo));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case RC_CAMERA_REQUEST:
                                openPhoneCamera();
                                break;
                            case RC_PICK_IMAGE_REQUEST:
                                openGallery();
                                break;
                        }

                    }
                });
                imagePickerDialog = builder.create();
            }
            imagePickerDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imagePickerDialog != null && imagePickerDialog.isShowing()) {
            imagePickerDialog.dismiss();
        }
    }


    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RC_PICK_IMAGE_REQUEST);
    }

    private void openPhoneCamera() {
        String storageState = Environment.getExternalStorageState();
        try {
            File mediaDir;
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                mediaDir = new File(SDCardHandler.IMAGE_STORAGE_PATH);
            } else {
                mediaDir = new File(getFilesDir(), Constants.APP_NAME);
            }
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
            _photoFile = new File(mediaDir, new Date().getTime() + ".jpg");
            if (!_photoFile.exists()) {
                _photoFile.createNewFile();
            }
        } catch (IOException e) {
            Logger.d(TAG, "Could not create file: " + e);
        }

        if (_photoFile != null) {
            mImageUri = getUriFromFile(_photoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, RC_CAMERA_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionGranted = true;
        switch (requestCode) {
            case PERMISSION_ALL:
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        isPermissionGranted = false;
                        break;
                    }
                }
                break;
        }
        if (isPermissionGranted) {
            Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            showTakeImagePopup();
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
        }
    }

    // My Generic Check Permission Method
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


    private void startCroppingActivity(Uri mImageUri) {
        int minWidth, minHeight;
        if (getCropAspectRatio() == CropAspectRatio.HORIZONTAL_RECT) {
            minWidth = 170;
            minHeight = 120;
        } else if (getCropAspectRatio() == CropAspectRatio.VERTICAL_RECT) {
            minWidth = 196;
            minHeight = 316;
        } else {
            minWidth = 80;
            minHeight = 80;
        }
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity(mImageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(minWidth, minHeight)
                .setMinCropResultSize(minWidth, minHeight)
                .setMultiTouchEnabled(false)
                .start(this);
    }

    // This is special method to get Uri From File for Naugat
    public static Uri getUriFromFile(File file) {
        Uri uri;
        Context context = MyApplication.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_CAMERA_REQUEST:
                    startCroppingActivity(mImageUri);
                    break;
                case RC_PICK_IMAGE_REQUEST:
                    if (data != null) {
                        Uri uri = data.getData();
                        startCroppingActivity(uri);
                    }
                    break;
                case RC_CROP_ACTIVITY:
                    if (data != null) {
                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        String imgPath = result.getUri().getPath();
                        _photoFile = new File(imgPath);
                        onImageSet(_photoFile);
                    }
                    break;
            }
        }
    }

    @Override
    public int getLayoutResource() {
        return 0;
    }

    @Override
    public String getScreenTitle() {
        return null;
    }
}
