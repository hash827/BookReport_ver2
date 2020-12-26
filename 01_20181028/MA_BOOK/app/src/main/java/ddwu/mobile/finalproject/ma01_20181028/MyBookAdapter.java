package ddwu.mobile.finalproject.ma01_20181028;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyBookAdapter extends BaseAdapter {

    public static final String TAG = "MyBookAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<NaverBookDto> list;
    private NaverNetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;


    public MyBookAdapter(Context context, int resource, ArrayList<NaverBookDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        imageFileManager = new ImageFileManager(context);
        networkManager = new NaverNetworkManager(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public NaverBookDto getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG, "getView with position : " + position);
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tvTitle);
            viewHolder.tvAuthor = view.findViewById(R.id.tvAuthor);
            viewHolder.ivImage = view.findViewById(R.id.ivImage);
            viewHolder.tvIsbn = view.findViewById(R.id.tvIsbn);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        NaverBookDto dto = list.get(position);
        viewHolder.tvTitle.setText(dto.getTitle());
        viewHolder.tvAuthor.setText(dto.getAuthor());
        viewHolder.tvIsbn.setText(dto.getIbsn());


        if(dto.getImageLink() == null){
            viewHolder.ivImage.setImageResource(R.mipmap.ic_launcher);
            return view;
        }

        Bitmap savedbitmap = imageFileManager.getBitmapFromTemporary(dto.getImageLink());
        if(savedbitmap !=null){
            viewHolder.ivImage.setImageBitmap(savedbitmap);
            Log.d(TAG,"Image loading from file");
        }
        else{
            viewHolder.ivImage.setImageResource(R.mipmap.ic_launcher);
            new GetImageAsyncTask(viewHolder).execute(dto.getImageLink());
            Log.d(TAG,"Image loading from network");
        }
        return view;
    }


    public void setList(ArrayList<NaverBookDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvAuthor = null;
        public ImageView ivImage = null;
        public TextView tvIsbn = null;
    }



    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        ViewHolder viewHolder;
        String imageAddress;

        public GetImageAsyncTask(ViewHolder holder) {
            viewHolder = holder;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            imageAddress = params[0];
            Bitmap result = null;
            result = networkManager.downloadImage(imageAddress);

            return result;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null){
                viewHolder.ivImage.setImageBitmap(bitmap);
                imageFileManager.saveBitmapToTemporary(bitmap,imageAddress);
            }

        }



    }

}
