package nyc.c4q.scar.memer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    static final int CAMERA_ID_CODE = 0, GALLERY_ID_CODE = 1;
//    File file = new File(this.getFilesDir(), "id.jpg");  ==== going to try another way =====
    static final String tempFileDir = Environment.getExternalStorageDirectory().getPath() + "/_pictureholder_id.jpg";;
    private Intent dialogIntent;
    private Uri imageFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton dialogButton = (ImageButton) findViewById(R.id.dialogButton_id);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();
            }
        });
    }

    //This handles what happen if the camera or gallery button is selected from the dialog view.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //The if statement for the gallery function.
        if (requestCode == GALLERY_ID_CODE && resultCode == RESULT_OK && data != null) {
            imageFileUri = data.getData();
        }

        //The if statement for the camera function.
        if (requestCode == CAMERA_ID_CODE && resultCode == RESULT_OK) {
            imageFileUri = Uri.parse(String.valueOf(imageFileUri));
        }

        //This starts the next activity with a new purpose for dialogIntent.
        dialogIntent = new Intent(this, SecondActivity.class);
        dialogIntent.putExtra("filePackedForNextActivity", imageFileUri);
        startActivity(dialogIntent);
        //Takes the activity of the stack.
       // this.finish();
    }

    //This is for the pop-up Dialog to show.
    private void showDialogBox() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Please Choose:");

        final String[] items = {"Camera", "Gallery"}; //might have to change this to a string array.

        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (items[which].equalsIgnoreCase("Camera")) {
                    dialogIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    dialogIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.parse(tempFileDir));

                    if (dialogIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(dialogIntent, CAMERA_ID_CODE);
                    }
                }

                if (items[which].equalsIgnoreCase("Gallery")) {
                    dialogIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(dialogIntent, GALLERY_ID_CODE);
                }
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}