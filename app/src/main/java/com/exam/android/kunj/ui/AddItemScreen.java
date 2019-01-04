package com.exam.android.kunj.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.exam.android.kunj.ImageAppApplication;
import com.exam.android.kunj.R;
import com.exam.android.kunj.db.models.ImageModel;
import com.exam.android.kunj.utils.ImagePickerUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
public class AddItemScreen extends AppCompatActivity {

    /**
     * Request codes for various startActivityForResults
     */
    private static final int REQUEST_CODE_GALLERY_PHOTO = 1;
    private static final int REQUEST_CODE_PERMISSIONS_REQUEST_UPLOAD_READ_EXTERNAL_STORAGE = 2;
    
    @BindView(R.id.add_item_screen_title_edit_text)
    EditText mTitleEditText;
    @BindView(R.id.add_item_screen_description_edit_text)
    EditText mDescriptionEditText;
    @BindView(R.id.add_item_screen_image_view)
    ImageView mImageView;

    private String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_screen);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
            actionBar.setTitle(R.string.add_item_screen_action_bar_title);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            /*Upload image for attachment*/
            case REQUEST_CODE_PERMISSIONS_REQUEST_UPLOAD_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            galleryIntent();
                        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(AddItemScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // user denied flagging NEVER ASK AGAIN.
                            showCustomSettingPermissionDialog();
                        }
                        /*Break for loop*/
                        break;
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY_PHOTO) {
                Uri selectedImageUri = data.getData();
                String path = getRealPathFromURI(selectedImageUri);

                File destinationFile = null;
                try {
                    destinationFile = ImagePickerUtils.createImageFile();
                    ImagePickerUtils.copy(new File(path), destinationFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(destinationFile != null) {
                    imagePath = destinationFile.getAbsolutePath();
                    Bitmap bm = BitmapFactory.decodeFile(ImagePickerUtils.compressImage(imagePath));
                    mImageView.setImageBitmap(bm);
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = getContentResolver().query(contentURI, projection, null, null, null);
        if (cursor == null) { // Source is dropBox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(projection[0]);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void dispatchGalleryPictureIntent() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(AddItemScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(AddItemScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // if the app has requested this permission previously and the user denied the request
                    showCustomPermissionDialog(String.format(getString(R.string.custom_upload_read_storage_permission_dialog_msg_string), getString(R.string.app_name)));
                } else {
                    ActivityCompat.requestPermissions(AddItemScreen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS_REQUEST_UPLOAD_READ_EXTERNAL_STORAGE);
                }
            } else {
                galleryIntent();
            }
        } else {
            galleryIntent();
        }
    }

    private void showCustomPermissionDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(AddItemScreen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS_REQUEST_UPLOAD_READ_EXTERNAL_STORAGE);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"),
                REQUEST_CODE_GALLERY_PHOTO);
    }

    private void showCustomSettingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getString(R.string.custom_setting_storage_permission_dialog_msg_string),
                getString(R.string.app_name)))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.add_item_screen_save_button)
    void clickOnSaveButton() {
        validation();
    }

    @OnClick(R.id.add_item_screen_select_image_button)
    void clickOnSelectImageButton() {
        dispatchGalleryPictureIntent();
    }

    private void validation() {
        String title = mTitleEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();

        if(TextUtils.isEmpty(title)) {
            errorAlertDialog(R.string.add_item_screen_title_error_dialog_message_text);
            return;
        } else if(TextUtils.isEmpty(description)) {
            errorAlertDialog(R.string.add_item_screen_description_error_dialog_message_text);
            return;
        } else if(TextUtils.isEmpty(imagePath)) {
            errorAlertDialog(R.string.add_item_screen_image_error_dialog_message_text);
            return;
        }

        ImageModel imageModel = new ImageModel();
        imageModel.setTitle(title);
        imageModel.setDescription(description);
        imageModel.setPath(imagePath);
        imageModel.setType(ImageModel.LOCAL_FILE);
        imageModel.setViewedTime(System.currentTimeMillis());

        insertData(imageModel);

        finish();
    }

    private void errorAlertDialog(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_item_screen_title_text);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                R.string.ok_string,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void insertData(final ImageModel imageModel) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ImageAppApplication.database.imageDao().insert(imageModel);
                return null;
            }
        }.execute();
    }
}
