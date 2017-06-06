package com.example.lenovo.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lenovo on 2016/10/4.
 */

public class MediaUtil {
    public static List<Music> getSongs(Context context) {
        Cursor cur =context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<Music> songs = new ArrayList<>();
        for (int i = 0; i < cur.getCount(); i++) {
            Music song = new Music();
            cur.moveToNext();
            long id = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID));
            long albumId = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String displayName = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String name = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            long duration = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
            int isMusic = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            long size = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.SIZE));
            String url = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
            if (isMusic != 0 && duration / (1000 * 60) >= 1) {
                song.setId(id);
                song.setAlbumId(albumId);
                song.setAlbum(album);
                song.setDisplayName(displayName);
                song.setName(name);
                song.setSinger(singer);
                song.setDuration(duration);
                song.setSize(size);
                song.setUrl(url);
                songs.add(song);
            }
        }
        return songs;
    }

    public static List<HashMap<String,String>> getSongsMap(List<Music> songs){
        List<HashMap<String,String>> list=new ArrayList<>();
        for(Iterator iterator = songs.iterator(); iterator.hasNext();) {
            Music song= (Music) iterator.next();
            HashMap<String,String> hashMap=new HashMap<String,String>();
            hashMap.put("name",song.getName());
            hashMap.put("albumId",String.valueOf(song.getAlbumId()));
            hashMap.put("album",song.getAlbum());
            hashMap.put("displayName",song.getDisplayName());
            hashMap.put("singer",song.getSinger());
            hashMap.put("duration",formatTime(song.getDuration()));
            hashMap.put("size",String.valueOf(song.getSize()));
            hashMap.put("url",song.getUrl());
            list.add(hashMap);
        }
        return list;
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
