package com.example.lenovo.myapplication;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import static android.R.id.list;

public class MainActivity extends Activity{

    private Button musicPrevious;
    private Button musicRepeat;
    private Button musicPause;
    private Button musicShuffle;
    private Button musicNext;
    private Button musicPlaying;
    private TextView musicName;
    private TextView musicSinger;
    private TextView musicDuration;
    private ImageView musicAlbum;
    private ListView musicList;
    private List<Music> allSongs=null;
    private SimpleAdapter mySimpleAdapter;

    private int listPosition=0;
    private MainReceiver mainReceiver;
    private int duration;
    private int currentTime;
    private boolean isFirstTime=true;
    private boolean isPlaying;
    private boolean isPause;
    private boolean isShuffle=false;
    private boolean isNoneShuffle=true;
    private int repeatState;
    private final int isNoneRepeat=3;
    private final int isAllRepeat=2;
    private final int isOneRepeat=1;

    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION";
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION";
    public static final String REPEAT_ACTION = "com.wwj.action.REPEAT_ACTION";
    public static final String SHUFFLE_ACTION = "com.wwj.action.SHUFFLE_ACTION";

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicList= (ListView) findViewById(R.id.music_broadcast_list);
        musicList.setOnItemClickListener(new MusicListItemClickListener());
        musicList.setOnCreateContextMenuListener(new MusicListItemContextMenuListener());

        allSongs=MediaUtil.getSongs(getApplicationContext());
        setFindViewById();
        setMyOnClickListener();
        setListAdapter(MediaUtil.getSongsMap(allSongs));
        repeatState=isNoneRepeat;

        IntentFilter intentFilter=new IntentFilter();
        mainReceiver=new MainReceiver();
        intentFilter.addAction(UPDATE_ACTION);
        intentFilter.addAction(MUSIC_CURRENT);
        intentFilter.addAction(MUSIC_DURATION);
        intentFilter.addAction(REPEAT_ACTION);
        intentFilter.addAction(SHUFFLE_ACTION);
        registerReceiver(mainReceiver,intentFilter);
    }

    public void setFindViewById(){
        musicPrevious= (Button) findViewById(R.id.previous_music);
        musicRepeat= (Button) findViewById(R.id.repeat_music);
        musicPause= (Button) findViewById(R.id.pause_music);
        musicShuffle= (Button) findViewById(R.id.shuffle_music);
        musicNext= (Button) findViewById(R.id.next_music);
        musicPlaying= (Button) findViewById(R.id.music_playing);
        musicName= (TextView) findViewById(R.id.music_name);
        musicDuration= (TextView) findViewById(R.id.music_duration);
    }

    public void setMyOnClickListener(){
        MyOnClickListener myOnClickListener =new MyOnClickListener();
        musicPrevious.setOnClickListener(myOnClickListener);
        musicRepeat.setOnClickListener(myOnClickListener);
        musicPause.setOnClickListener(myOnClickListener);
        musicShuffle.setOnClickListener(myOnClickListener);
        musicNext.setOnClickListener(myOnClickListener);
        musicPlaying.setOnClickListener(myOnClickListener);
    }

    public class MyOnClickListener implements View.OnClickListener{
        Intent intent=new Intent();

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.previous_music:
                    musicPause.setBackgroundResource(R.drawable.pause_selector);
                    previous();
                    isPlaying=true;
                    isPause=false;
                    isFirstTime=false;
                    break;
                case R.id.repeat_music:
                    Log.d("msg","单曲循环");
                    if(repeatState==isNoneRepeat){
                        musicShuffle.setClickable(false);
                        musicRepeat.setBackgroundResource(R.drawable.repeat_music_on);
                        repeatOne();
                        repeatState=isOneRepeat;
                        Toast.makeText(MainActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
                    }
                    else if(repeatState==isOneRepeat){
                        Log.d("msg","全部歌曲循环");
                        musicShuffle.setClickable(false);
                        musicRepeat.setBackgroundResource(R.drawable.repeat_music);
                        repeatAll();
                        repeatState=isAllRepeat;
                        Toast.makeText(MainActivity.this,"全部歌曲循环",Toast.LENGTH_SHORT).show();
                    }
                    else if(repeatState==isAllRepeat){
                        Log.d("msg","循环停止");
                        musicShuffle.setClickable(true);
                        musicRepeat.setBackgroundResource(R.drawable.repeat_music_off);
                        repeatNone();
                        repeatState=isNoneRepeat;
                        Toast.makeText(MainActivity.this,"循环停止",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.pause_music:
                    if(isFirstTime)
                    {
                        musicPause.setBackgroundResource(R.drawable.pause_selector);
                        play();
                        isFirstTime=false;
                        isPlaying=true;
                        isPause=false;
                    }
                    else{
                        if(isPlaying){
                            musicPause.setBackgroundResource(R.drawable.start_selector);
                            intent.setAction("com.wwj.media.MUSIC_SERVICE");
                            intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
                            startService(intent);
                            isPlaying=false;
                            isPause=true;
                        }
                        else if(isPause){
                            musicPause.setBackgroundResource(R.drawable.pause_selector);
                            intent.setAction("com.wwj.media.MUSIC_SERVICE");
                            intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);
                            startService(intent);
                            isPlaying=true;
                            isPause=false;
                        }
                    }
                    break;
                case R.id.shuffle_music:
                    if(isNoneShuffle){
                        musicRepeat.setClickable(false);
                        musicShuffle.setBackgroundResource(R.drawable.shuffle_music_on);
                        shuffle();
                        isShuffle=true;
                        isNoneShuffle=false;
                        Toast.makeText(MainActivity.this,"随机播放",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        musicRepeat.setClickable(true);
                        musicShuffle.setBackgroundResource(R.drawable.shuffle_music_off);
                        isShuffle=false;
                        isNoneShuffle=true;
                        Toast.makeText(MainActivity.this,"随机播放停止",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.next_music:
                    musicPause.setBackgroundResource(R.drawable.pause_selector);
                    next();
                    isPlaying=true;
                    isPause=false;
                    isFirstTime=false;
                    break;
                case R.id.music_playing:
                    Music song=allSongs.get(listPosition);
                    musicName.setText(song.getName());
                    Intent intent=new Intent(MainActivity.this,PlayerActivity.class);
                    intent.putExtra("name", song.getName());
                    intent.putExtra("url", song.getUrl());
                    intent.putExtra("singer",song.getSinger());
                    intent.putExtra("listPosition", listPosition);
                    intent.putExtra("currentTime", currentTime);
                    intent.putExtra("repeatState", repeatState);
                    intent.putExtra("duration",duration);
                    intent.putExtra("MSG", AppConstant.PlayerMsg.PLAYING_MSG);
                    startActivity(intent);
                    break;
            }
        }
    }

    public class MusicListItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            listPosition=position;
            playMusic(listPosition);
        }
    }

    public class MusicListItemContextMenuListener implements View.OnCreateContextMenuListener {

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
            musicListItemDialog();
        }
    }

    public void setListAdapter(List<HashMap<String,String>> songs){
        mySimpleAdapter=new SimpleAdapter(this,songs,R.layout.music_broadcast_list_item,
                new String[]{"name","singer","duration"},
                new int[]{R.id.music_name02,R.id.music_singer02,R.id.music_duration02});
        musicList.setAdapter(mySimpleAdapter);
    }

    public void previous(){
        listPosition=listPosition-1;
        if(listPosition>=0){
            Music song=allSongs.get(listPosition);
            musicName.setText(song.getName());
            Intent intent = new Intent();
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", song.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.PREVIOUS_MSG);
            startService(intent);
        }
        else{
            listPosition=listPosition+1;
            Toast.makeText(MainActivity.this,"没有上一首了",Toast.LENGTH_SHORT).show();
        }
    }

    public void repeatOne(){
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 1);
        sendBroadcast(intent);
    }

    public void repeatAll(){
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 2);
        sendBroadcast(intent);
    }

    public void repeatNone(){
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 3);
        sendBroadcast(intent);
    }

    public void play(){
        Music song=allSongs.get(listPosition);
        musicName.setText(song.getName());

        Intent intent = new Intent();
        intent.setAction("com.wwj.media.MUSIC_SERVICE");
        intent.putExtra("listPosition", 0);
        intent.putExtra("url", song.getUrl());
        intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
        startService(intent);
    }

    public void shuffle(){
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 4);
        sendBroadcast(intent);
    }

    public void next(){
        listPosition=listPosition+1;
        if(listPosition<=allSongs.size()-1){
            Music song=allSongs.get(listPosition);
            musicName.setText(song.getName());
            Intent intent = new Intent();
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", song.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);
            startService(intent);
        }
        else{
            listPosition=listPosition-1;
            Toast.makeText(MainActivity.this,"没有下一首了",Toast.LENGTH_SHORT).show();
        }
    }

    public void playMusic(int listPosition){
        if(allSongs!=null){

            Music song=allSongs.get(listPosition);
            musicName.setText(song.getName());
            Intent intent=new Intent(MainActivity.this,PlayerActivity.class);
            intent.putExtra("name", song.getName());
            intent.putExtra("url", song.getUrl());
            intent.putExtra("singer",song.getSinger());
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("currentTime", currentTime);
            intent.putExtra("repeatState", repeatState);
            intent.putExtra("shuffleState", isShuffle);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            startActivity(intent);
        }
    }

    public void musicListItemDialog(){

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            new AlertDialog.Builder(this).setIcon(R.drawable.music_queue_off)
                    .setTitle("退出")
                    .setMessage("您确定要退出？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    Intent intent=new Intent(MainActivity.this,PlayerService.class);
                                    unregisterReceiver(mainReceiver);
                                    stopService(intent);
                                }
                            }).show();
        }
        return super.onKeyDown(keyCode, event);
    }

    public class MainReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(UPDATE_ACTION)){
                listPosition=intent.getIntExtra("current",-1);
                if(listPosition>=0){
                    musicName.setText(allSongs.get(listPosition).getName());
                }
            }
            else if(action.equals(MUSIC_CURRENT)){
                currentTime=intent.getIntExtra("currentTime",-1);
                musicDuration.setText(MediaUtil.formatTime((long) currentTime));
            }
            else if(action.equals(MUSIC_DURATION)){
                duration=intent.getIntExtra("duration",-1);
            }
            else if(action.equals(REPEAT_ACTION)){
                repeatState=intent.getIntExtra("repeatState",-1);
                switch(repeatState){
                    case isOneRepeat:
                        musicRepeat.setBackgroundResource(R.drawable.repeat_music_on);
                        musicShuffle.setClickable(false);
                        break;
                    case isAllRepeat:
                        musicRepeat.setBackgroundResource(R.drawable.repeat_music);
                        musicShuffle.setClickable(false);
                        break;
                    case isNoneRepeat:
                        musicRepeat.setBackgroundResource(R.drawable.repeat_music_off);
                        musicShuffle.setClickable(false);
                        break;
                }
            }
            else{
                isShuffle=intent.getBooleanExtra("shuffleState",false);
                if(isShuffle){
                    musicShuffle.setBackgroundResource(R.drawable.shuffle_music_on);
                    musicRepeat.setClickable(false);
                    isNoneShuffle=false;
                }
                else{
                    musicShuffle.setBackgroundResource(R.drawable.shuffle_music_off);
                    musicRepeat.setClickable(true);
                    isNoneShuffle=true;
                }
            }
        }
    }
}
