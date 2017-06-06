package com.example.lenovo.myapplication;

import android.content.Context;
import android.text.Layout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lenovo on 2016/10/1.
 */

class ViewContainer{
    ImageView musicAlbum;
    TextView musicName;
    TextView musicSinger;
    TextView musicDuration;
    ImageButton listDown;
}

public class MyAdapter extends BaseAdapter{

    private Context context;
    private List<Music> songs;
    private Music song;
    private int pos=-1;
    private ViewContainer vc;

    public MyAdapter(Context context,List<Music> songs){
        this.context=context;
        this.songs=songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        vc=null;
        if(convertView==null){
            vc=new ViewContainer();
            convertView= LayoutInflater.from(context).inflate(R.layout.music_broadcast_list_item,null);
            vc.musicName= (TextView) convertView.findViewById(R.id.music_name02);
            vc.musicSinger= (TextView) convertView.findViewById(R.id.music_singer02);
            vc.musicDuration= (TextView) convertView.findViewById(R.id.music_duration);
            convertView.setTag(vc);
        }
        else{
            vc= (ViewContainer) convertView.getTag();
        }
        song=songs.get(position);
        vc.musicName.setText(song.getName());
        vc.musicSinger.setText(song.getSinger());
        vc.musicDuration.setText(String.valueOf(formatTime(song.getDuration())));
        return convertView;
    }

    public static String formatTime(Long time){
        String min=time/(1000*60)+"";
        String sec=time%(1000*60)+"";
        if(min.length()<2)
            min="0"+min;
        switch(sec.length()){
            case 4:
                sec="0"+sec;
                break;
            case 3:
                sec="00"+sec;
                break;
            case 2:
                sec="000"+sec;
                break;
            case 1:
                sec="0000"+sec;
                break;
        }
        return min+":"+sec.trim().substring(0,2);
    }
}




