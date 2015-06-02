package nyc.c4q.scar.memer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondActivity extends AppCompatActivity {

    private Uri imageFileUri;
    private Intent intent;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        imageView = (ImageView) findViewById(R.id.insert_pic_id);
        Button changeImageButton = (Button) findViewById(R.id.change_img);

        if (savedInstanceState != null) {
            imageFileUri = (Uri) savedInstanceState.get("newSaveInstance_KEY");
            imageView.setImageURI(imageFileUri);
        } else {
            imageFileUri = (Uri) getIntent().getExtras().get("filePackedForNextActivity");
            imageView.setImageURI(imageFileUri);
        }

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();
            }
        });
    }

    //This replaces the image in the box with either a new picture or a pre-existing photo from the gallery.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.GALLERY_ID_CODE && resultCode == RESULT_OK && data != null) {
            imageFileUri = data.getData();
            imageView.setImageURI(imageFileUri);
        }

        if (requestCode == MainActivity.CAMERA_ID_CODE && resultCode == RESULT_OK) {
//            imageFileUri = Uri.parse(MainActivity.tempFileDir);
            imageView.setImageURI(null);
//            imageView.setImageURI(Uri.parse(MainActivity.tempFileDir));
        }
    }

    //Displays the pop-up dialog with the opition to choose a camera or gallery.
    private void showDialogBox() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Please Choose:");
        final String[] items = {"Camera", "Gallery"};
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (items[which].equalsIgnoreCase("Camera")) {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    Uri imageFileUri = Uri.parse(MainActivity.tempFileDir);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, MainActivity.CAMERA_ID_CODE);
                    }
                }

                if (items[which].equalsIgnoreCase("Gallery")) {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, MainActivity.GALLERY_ID_CODE);
                }
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    //Saves the current state
    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        toSave.putParcelable("newSaveInstance_KEY", imageFileUri);
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }
}