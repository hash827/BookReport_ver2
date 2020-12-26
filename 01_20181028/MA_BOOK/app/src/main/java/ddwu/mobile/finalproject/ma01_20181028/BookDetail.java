package ddwu.mobile.finalproject.ma01_20181028;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BookDetail extends AppCompatActivity {
    MyBook myBook;
    TextView tvTitle;
    TextView tvAuthor;
    TextView tvIsbn;
    ImageView imageView;
    TextView tvPublisher;
    TextView tvPrice;
    TextView tvDescription;
    TextView tvLink;
    MyBookDBManager myBookDBManager;
    Context context;


    private ImageFileManager imageFileManager = null;
    private NaverNetworkManager networkManager = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        myBook = (MyBook) intent.getSerializableExtra("BookDetail");

        tvAuthor = findViewById(R.id.tv_detail_author);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvIsbn = findViewById(R.id.tv_detail_isbn);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvPublisher = findViewById(R.id.tv_detail_publisher);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvLink = findViewById(R.id.tv_Link);


        networkManager = new NaverNetworkManager(context);

        tvPrice.setText(myBook.getPrice());
        tvPublisher.setText(myBook.getPublisher());
        tvTitle.setText(myBook.getTitle());
        tvDescription.setText(myBook.getDescription());
        tvIsbn.setText(myBook.getIsbn());
        tvAuthor.setText(myBook.getAuthor());

        tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(myBook.getLink()));
                startActivity(intent);
            }
        });

        BookDetail.GetImageAsyncTask imageAsyncTask = new BookDetail.GetImageAsyncTask();
        imageAsyncTask.execute(myBook.getImage());
        myBookDBManager = new MyBookDBManager(this);





    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_book_indetail:
                boolean result = myBookDBManager.addNewBook(myBook);
                if(result){
                    Toast.makeText(BookDetail.this, "추가 완료!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "추가 실패!", Toast.LENGTH_SHORT).show();
                }

            break;
        }
    }


    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        String imageAddress;
        ImageView imageView = findViewById(R.id.detail_img);
        Bitmap result;

        @Override
        protected Bitmap doInBackground(String... params) {
            String result = params[0];
            Bitmap bitmap = null;
            bitmap = networkManager.downloadImage(result);
            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);


            }

        }




    }}
