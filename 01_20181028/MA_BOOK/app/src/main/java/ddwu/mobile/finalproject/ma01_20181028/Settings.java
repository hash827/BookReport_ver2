package ddwu.mobile.finalproject.ma01_20181028;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    RadioGroup radioGroup;
    RadioButton radioButton;

    MyBookDBManager myBookDBManager;
    MyReportDBManager myReportDBManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        myBookDBManager = new MyBookDBManager(this);
        myReportDBManager = new MyReportDBManager(this);

        radioGroup = (RadioGroup)findViewById(R.id.radio_upload);
        SharedPreferences sf = getSharedPreferences("upload", 0);
        String str = sf.getString("what", "all");
        switch (str) {
            case "all":
                radioButton = findViewById(R.id.radioButton);
                radioButton.setChecked(true);
                break;
            case "book":
                radioButton = findViewById(R.id.radioButton2);
                radioButton.setChecked(true);
                break;
            case "report":
                radioButton = findViewById(R.id.radioButton3);
                radioButton.setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            SharedPreferences sf = getSharedPreferences("upload", 0);
            SharedPreferences.Editor editor = sf.edit();
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton) {
                    editor.putString("what", "all");
                    editor.commit();
                } else if (checkedId == R.id.radioButton2) {
                    editor.putString("what", "book");
                    editor.commit();
                } else if (checkedId == R.id.radioButton3) {
                    editor.putString("what", "report");
                    editor.commit();
                }
            }
        });

    }
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_delete_data:
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("데이터 삭제")
                        .setMessage("모든 데이터가 삭제되는데 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (myReportDBManager.removeAllReport()) {
                                    if (myBookDBManager.removeAllBook()) {
                                        Toast.makeText(Settings.this, "삭제완료", Toast.LENGTH_SHORT).show();
                                        onResume();
                                    }
                                } else {
                                    Toast.makeText(Settings.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setCancelable(false)
                        .show();
                break;
        }
    }
}
