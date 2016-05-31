package com.tranetech.openspace.listviewwithbuttondemo;

/**
 * Created by Arpit Patel on 08-Apr-16.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserCustomAdapter extends BaseAdapter implements DownloadCallBack {
   Context context;
    int layoutResourceId;
    ArrayList<User> data = new ArrayList<User>();
    static View row;
    static DownloadTask downloadTask;
    ArrayList<File> localFiles = new ArrayList<>();
    File folder;
    DownloadCallBack downloadCallBack;

    public UserCustomAdapter(Context context, int layoutResourceId,
                             ArrayList<User> data) {

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        downloadCallBack = this;


    }

    public UserCustomAdapter() {
    }

    public void sync(){
        folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Exam Papers");
        localFiles = new ArrayList<>(Arrays.asList(folder.listFiles()));
        Log.d("sync", "sync: Local Size : "+localFiles.size());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        row = convertView;
        UserHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new UserHolder();
            holder.tv_paper_name = (TextView) row.findViewById(R.id.tv_paper_name);
//            holder.tv_paper_desc = (TextView) row.findViewById(R.id.tv_paper_desc);
            holder.bt_download = (Button) row.findViewById(R.id.bt_download);
            holder.btn_open = (Button) row.findViewById(R.id.btn_open);
            row.setTag(holder);
        } else {
            holder = (UserHolder) row.getTag();
        }
        final User user = data.get(position);
      //  Log.d("sync", "getView: Name : "+user.getName());

//        holder.tv_paper_desc.setText(user.getAddress());
//        holder.textLocation.setText(user.getLocation());
        Boolean status = false;
        for (int i = 0; i < localFiles.size(); i++) {
          //  Log.d("sync", "getView: filename : "+user.getName());
          //  Log.d("sync", "getView: local file : "+localFiles.get(i).getName());
            if(user.getName().equals(localFiles.get(i).getName().replace(".pdf",""))){
                Log.d("sync", "getView: True");
               // holder.tv_paper_name.setText("downloaded");
                status = true;
            }
        }


        final UserHolder finalHolder = holder;

        holder.bt_download.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("Download Button Clicked", "**********");
//                Toast.makeText(context, "Download  "+ finalHolder.tv_paper_name.getText().toString()+"  " + position,
//                        Toast.LENGTH_LONG).show();
                File extStore = Environment.getExternalStorageDirectory();
                File myFile = new File(extStore.getAbsolutePath() + "/Exam Papers/"+finalHolder.tv_paper_name.getText().toString()+".pdf");

                if (!myFile.exists()) {

                    // execute this when the downloader must be fired
                    downloadTask = new DownloadTask(context, downloadCallBack);
                  //  downloadTask.execute("http://ia.tranetech.ae:82/upload/uploads/five-point-someone-chetan-bhagat_ebook.pdf",""+finalHolder.tv_paper_name.getText().toString()+".pdf");
                    downloadTask.execute("https://letuscsolutions.files.wordpress.com/2015/07/five-point-someone-chetan-bhagat_ebook.pdf",""+finalHolder.tv_paper_name.getText().toString()+".pdf");


                } else {

                    Toast.makeText(context, "File already Exists in "+myFile, Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.btn_open.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String PaperName = user.getName();
                File extStore = Environment.getExternalStorageDirectory();
                File myFile = new File(extStore.getAbsolutePath() + "/Exam Papers/" + PaperName + ".pdf");

                if (myFile.exists()) {

                    File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Exam Papers/" + PaperName + ".pdf");  // -> filename
                    Uri path = Uri.fromFile(pdfFile);
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(path, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        context.startActivity(pdfIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        if(!status){
            holder.tv_paper_name.setText(user.getName());
            holder.btn_open.setVisibility(View.GONE);
            holder.bt_download.setVisibility(View.VISIBLE);
            return row;
        }else {
            holder.tv_paper_name.setText(user.getName());
            holder.bt_download.setVisibility(View.GONE);
            holder.btn_open.setVisibility(View.VISIBLE);
            return row;
        }


    }

    @Override
    public void onDownloadComplete() {
        sync();
    }

    static class UserHolder {
        TextView tv_paper_name;
//        TextView tv_paper_desc;
        Button bt_download;
        Button btn_open;
    }
}

