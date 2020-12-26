package ddwu.mobile.finalproject.ma01_20181028;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Upload extends AppCompatActivity {

    EditText etTarget;
    ListView lvList;
    String apiAddress;

    String query;

    MyProBookAdapter adapter;
    ArrayList<MyBook> resultList;
    ArrayList<MyBook> proList;
    NaverBookXmlParser parser;
    NaverNetworkManager networkManager;
    ImageFileManager imgFileManager;

    MyBookDBManager myBookDBManager;
    MyReportDBManager myReportDBManager;

    TextView tvTitle;
    TextView tvAuthor;
    TextView tvIsbn;
    ImageView imageView;
    RadioGroup radioGroup;

    MyBook uploadBook;
    int read=1;
    String uploadText="";
    ArrayList<MyReport> list;
    MyReport report;
    String what;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_mybook);

        lvList = findViewById(R.id.pro_book);

        resultList = new ArrayList();
        adapter = new MyProBookAdapter(this, R.layout.listview_book, resultList);
        lvList.setAdapter(adapter);

        myBookDBManager = new MyBookDBManager(this);
        myReportDBManager = new MyReportDBManager(this);

        apiAddress = getResources().getString(R.string.api_url);
        parser = new NaverBookXmlParser();
        networkManager = new NaverNetworkManager(this);
        networkManager.setClientId(getResources().getString(R.string.client_id));
        networkManager.setClientSecret(getResources().getString(R.string.client_secret));
        imgFileManager = new ImageFileManager(this);

        SharedPreferences sf = getSharedPreferences("upload", 0);
        what = sf.getString("what", "all");


        AlertDialog.Builder builder = new AlertDialog.Builder(Upload.this);
        builder.setTitle("도서 공유")
                .setMessage("공유할 도서를 선택하세요")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();

        radioGroup = findViewById(R.id.read_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_all){
                    read = 1;
                    onResume();
                }
                else if(checkedId == R.id.radio_read1){
                    read = 2;
                    onResume();
                }
                else if(checkedId == R.id.radio_read2){
                    read = 3;
                    onResume();
                }
                else if(checkedId == R.id.radio_read3){
                    read = 4;
                    onResume();
                }

            }
        });

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position;
                long _id = resultList.get(pos).get_id();
                uploadBook = myBookDBManager.getBook(_id);
                if(what.equals("all") || what.equals("book")) {
                    uploadText += "<책 정보>" + "\n";
                    uploadText += "제목: " + uploadBook.getTitle() + "\n";
                    uploadText += "작가: " + uploadBook.getAuthor() + "\n";
                    uploadText += "출판사" + uploadBook.getPublisher() + "\n\n\n";
                }
                if(what.equals("all")|| what.equals("report")) {
                    list = myReportDBManager.getAllReport(String.valueOf(_id));
                    uploadText += "<독서 기록> " + "\n";
                    for (int i = 0; i < list.size(); i++) {
                        report = list.get(i);
                        uploadText += "날짜: " + report.getDate() + "\n";
                        uploadText += "페이지: " + report.getPage() + "\n";
                        uploadText += "내용: " + report.getReportContent() + "\n\n";
                    }
                }
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, uploadText);
                sharingIntent.setPackage("com.kakao.talk");
                startActivity(sharingIntent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 임시 파일 삭제
        imgFileManager.clearTemporaryFiles();

    }
    protected void onResume() {
        super.onResume();
        if(read == 1) {
            proList = myBookDBManager.getAllProBook();
        }
        else {
            proList = myBookDBManager.getProBookByState(read);
        }
        resultList.clear();
        resultList.addAll(proList);
        adapter.setList(resultList);
        adapter.notifyDataSetChanged();
    }


}
