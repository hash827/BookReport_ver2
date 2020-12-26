package ddwu.mobile.finalproject.ma01_20181028;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateReport extends AppCompatActivity {
    String update_id;
    EditText content;
    EditText page;
    TextView date;
    MyReportDBHelper reportDBHelper;
    MyReportDBManager reportDBManager;
    MyReport myReport;
    Calendar cal;
    int year;
    int month;
    int day;
    ImageView mImageView;
    String mCurrentPhotoPath="";
    final int PERMISSION_REQ_CODE = 100;
    private static final int REQUEST_TAKE_PHOTO = 200;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_report);

        Intent intent = getIntent();
        update_id = intent.getStringExtra("update_report");

        reportDBManager = new MyReportDBManager(this);
        myReport = reportDBManager.getReport(Long.parseLong(update_id));

        content = findViewById(R.id.et_update_report_content);
        page = findViewById(R.id.et_update_report_page);
        date = findViewById(R.id.tv_update_report_date);

        mImageView = (ImageView)findViewById(R.id.img_update);

        content.setText(myReport.getReportContent());
        page.setText(myReport.getPage());
        date.setText(myReport.getDate());
        cal = Calendar.getInstance();

        mCurrentPhotoPath = myReport.getImage();
        if (!(mCurrentPhotoPath.equals(""))){
                setPic(mCurrentPhotoPath);
        }

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateReport.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        year = year;
                        month = month + 1;
                        day = dayOfMonth;
                        date.setText(year + "." + month + "." + day);

                    }
                },  cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                datePickerDialog.setMessage("날짜 선택");
                datePickerDialog.show();
            }

        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateReport.this);
                builder.setTitle("이미지 삭제")
                        .setMessage("이미지를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCurrentPhotoPath = "";
                                mImageView.setImageResource(R.drawable.camera);
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });


    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_report: {
                if (!(page.getText().toString().equals("")) && !(content.getText().toString().equals(""))
                        && !(date.getText().toString().equals("")) ) {
                    boolean result = reportDBManager.updateReport(new MyReport(Long.parseLong(update_id),date.getText().toString(),
                            page.getText().toString(), content.getText().toString(), mCurrentPhotoPath));
                    if (result) {
                        Toast.makeText(this, "수정 완료!", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
                else{
                    Toast.makeText(this, "항목을 모두 입력하세요!", Toast.LENGTH_SHORT).show();
                }
               break;
            }

        }
    }
    private void setPic(String mCurrentPhotoPath) {
        File file = new File(mCurrentPhotoPath);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        mImageView.setImageBitmap(bitmap);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }
    /*원본 사진 파일 저장*/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
            }
        }

    }


    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }



    /*현재 시간 정보를 사용하여 파일 정보 생성*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();

        }
    }

}
