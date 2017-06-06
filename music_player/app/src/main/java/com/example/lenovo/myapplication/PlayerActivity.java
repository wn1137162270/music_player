package com.example.lenovo.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Lenovo on 2016/10/4.
 */

public class PlayerActivity extends Activity{

    private Button musicPrevious;
    private Button musicRepeat;
    private Button musicPause;
    private Button musicShuffle;
    private Button musicNext;
    private Button musicSearch;
    private Button musicQueue;
    private TextView musicName=null;
    private TextView musicSinger=null;
    private SeekBar progressBar;
    private TextView currentProgress;
    private TextView finalProgress;

    private int flag;
    private int currentTime;
    private int duration;
    private String name;
    private String singer;
    private String url;
    private int listPosition;
    private boolean isPlaying;
    private boolean isPause;
    private boolean isShuffle=false;
    private boolean isNoneShuffle=true;
    private int repeatState;
    private final int isNoneRepeat=3;
    private final int isAllRepeat=2;
    private final int isOneRepeat=1;
    private List<Music> songs;
    private PlayerReceiver playerReceiver;

    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION";
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION";
    public static final String MUSIC_PLAYING = "com.wwj.action.MUSIC_PLAYING";
    public static final String REPEAT_ACTION = "com.wwj.action.REPEAT_ACTION";
    public static final String SHUFFLE_ACTION = "com.wwj.action.SHUFFLE_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_braodcast_now);


        setFindViewById();
        setMyOnClickListener();
        songs=MediaUtil.getSongs(PlayerActivity.this);
        playerReceiver=new PlayerReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(UPDATE_ACTION);
        intentFilter.addAction(MUSIC_CURRENT);
        intentFilter.addAction(MUSIC_DURATION);
        registerReceiver(playerReceiver,intentFilter);
    }

    public void setFindViewById(){
        musicPrevious= (Button) findViewById(R.id.previous_music);
        musicRepeat= (Button) findViewById(R.id.repeat_music);
        musicPause= (Button) findViewById(R.id.pause_music);
        musicShuffle= (Button) findViewById(R.id.shuffle_music);
        musicNext= (Button) findViewById(R.id.next_music);
        musicSearch= (Button) findViewById(R.id.search_music);
        musicQueue= (Button) findViewById(R.id.music_queue);
        musicName= (TextView) findViewById(R.id.music_name03);
        musicSinger=(TextView) findViewById(R.id.music_singer03);
        progressBar= (SeekBar) findViewById(R.id.progress_bar);
        currentProgress= (TextView) findViewById(R.id.current_progress);
        finalProgress= (TextView) findViewById(R.id.final_progress);
    }

    public void setMyOnClickListener(){
        MyOnClickListener myOnClickListener =new MyOnClickListener();
        musicPrevious.setOnClickListener(myOnClickListener);
        musicRepeat.setOnClickListener(myOnClickListener);
        musicPause.setOnClickListener(myOnClickListener);
        musicShuffle.setOnClickListener(myOnClickListener);
        musicNext.setOnClickListener(myOnClickListener);
        musicQueue.setOnClickListener(myOnClickListener);
        musicSearch.setOnClickListener(myOnClickListener);
        progressBar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        name=bundle.getString("name");
        singer=bundle.getString("singer");
        url=bundle.getString("url");
        listPosition=bundle.getInt("listPosition");
        repeatState = bundle.getInt("repeatState");
        isShuffle = bundle.getBoolean("shuffleState");
        flag = bundle.getInt("MSG");
        currentTime = bundle.getInt("currentTime");
        duration = bundle.getInt("duration");
        initView();
    }

    public void initView(){
        musicName.setText(name);
        musicSinger.setText(singer);
        progressBar.setProgress(currentTime);
        progressBar.setMax(duration);
        Log.i("msg",String.valueOf(repeatState));
        switch (repeatState) {
            case isOneRepeat:
                musicShuffle.setClickable(false);
                musicRepeat.setBackgroundResource(R.drawable.repeat_music_on);
                break;
            case isAllRepeat:
                musicShuffle.setClickable(false);
                musicRepeat.setBackgroundResource(R.drawable.repeat_music);
                break;
            case isNoneRepeat:
                musicShuffle.setClickable(true);
                musicRepeat.setBackgroundResource(R.drawable.repeat_music_off);
                break;
        }
        if(isShuffle){
            isNoneShuffle=false;
            musicRepeat.setClickable(false);
            musicShuffle.setBackgroundResource(R.drawable.shuffle_music_on);
        }
        else{
            isNoneShuffle=true;
            musicRepeat.setClickable(true);
            musicShuffle.setBackgroundResource(R.drawable.shuffle_music_off);
        }
        if(flag==AppConstant.PlayerMsg.PLAY_MSG){
            play();
        }
        else if(flag==AppConstant.PlayerMsg.PLAYING_MSG){
            Toast.makeText(PlayerActivity.this,"正在播放"+name+"...",LENGTH_SHORT).show();
        }
        musicPause.setBackgroundResource(R.drawable.pause_selector);
        isPlaying=true;
        isPause=false;
    }

    public class MyOnClickListener implements View.OnClickListener{
        Intent intent=new Intent(PlayerActivity.this,PlayerService.class);

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.previous_music:
                    musicPause.setBackgroundResource(R.drawable.pause_selector);
                    previous();
                    isPlaying=true;
                    isPause=false;
                    break;
                case R.id.repeat_music:
                    Intent repeatIntent=new Intent(REPEAT_ACTION);
                    if(repeatState==isNoneRepeat){
                        musicShuffle.setClickable(false);
                        musicRepeat.setBackgroundResource(R.drawable.repeat_music_on);
                        repeatOne();
                        repeatState=isOneRepeat;
                        Toast.makeText(PlayerActivity.this,"单曲循环", LENGTH_SHORT).show();
                        intent.putExtra("repeatState",isOneRepeat);
                        sendBroadcast(repeatIntent);
                    }
                    else if(repeatState==isOneRepeat){
                        musicShuffle.setClickable(false);
                        musicRepeat.setBackgroundResource(R.drawable.repeat_music);
                        repeatAll();
                        repeatState=isAllRepeat;
                        Toast.makeText(PlayerActivity.this,"全部歌曲循环", LENGTH_SHORT).show();
                        intent.putExtra("repeatState",isAllRepeat);
                        sendBroadcast(repeatIntent);
                    }
                    else if(repeatState==isAllRepeat){
                        musicShuffle.setClickable(true);
                        musicRepeat.setBackgroundResource(R.drawable.repeat_music_off);
                        repeatNone();
                        repeatState=isNoneRepeat;
                        Toast.makeText(PlayerActivity.this,"循环停止", LENGTH_SHORT).show();
                        intent.putExtra("repeatState",isNoneRepeat);
                    }
                    break;
                case R.id.pause_music:
                    if(isPlaying){
                        musicPause.setBackgroundResource(R.drawable.start_selector);
                        intent.setAction("com.wwj.media.MUSIC_SERVICE");
                        intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
                        startService(intent);
                        isPlaying=false;
                        isPause=true;
                    }
                    else if(isPause) {
                        musicPause.setBackgroundResource(R.drawable.pause_selector);
                        intent.setAction("com.wwj.media.MUSIC_SERVICE");
                        intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);
                        startService(intent);
                        isPlaying = true;
                        isPause = false;
                    }
                    break;
                case R.id.shuffle_music:
                    Intent shuffleIntent=new Intent(SHUFFLE_ACTION);
                    if(isNoneShuffle){
                        musicRepeat.setClickable(false);
                        musicShuffle.setBackgroundResource(R.drawable.shuffle_music_on);
                        shuffle();
                        isShuffle=true;
                        isNoneShuffle=false;
                        Toast.makeText(PlayerActivity.this,"随机播放", LENGTH_SHORT).show();
                        shuffleIntent.putExtra("shuffleState",true);
                        sendBroadcast(shuffleIntent);
                    }
                    else{
                        musicRepeat.setClickable(true);
                        musicShuffle.setBackgroundResource(R.drawable.shuffle_music_off);
                        isShuffle=false;
                        isNoneShuffle=true;
                        Toast.makeText(PlayerActivity.this,"随机播放停止", LENGTH_SHORT).show();
                        shuffleIntent.putExtra("shuffleState",false);
                        sendBroadcast(shuffleIntent);
                    }
                    break;
                case R.id.next_music:
                    musicPause.setBackgroundResource(R.drawable.pause_selector);
                    next();
                    isPlaying=true;
                    isPause=false;
                    break;
            }
        }
    }
    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                audioTrackChange(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void play(){
        repeatNone();
        Intent intent=new Intent(PlayerActivity.this,PlayerService.class);
        intent.setAction("com.wwj.media.MUSIC_SERVICE");
        intent.putExtra("url", url);
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("MSG", flag);
        startService(intent);
    }

    public void previous(){
        listPosition=listPosition-1;
        if(listPosition>=0){
            Music song=songs.get(listPosition);
            musicName.setText(song.getName());
            musicSinger.setText(song.getSinger());
            url=song.getUrl();
            Intent intent = new Intent(PlayerActivity.this,PlayerService.class);
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", song.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.PREVIOUS_MSG);
            startService(intent);
        }
        else{
            listPosition=listPosition+1;
            Toast.makeText(PlayerActivity.this,"没有上一首了", LENGTH_SHORT).show();
        }
    }

    public void repeatOne(){
        Intent intent=new Intent(CTL_ACTION);
        intent.putExtra("control",1);
        sendBroadcast(intent);
    }

    public void repeatAll(){
        Intent intent=new Intent(CTL_ACTION);
        intent.putExtra("control",2);
        sendBroadcast(intent);
    }

    public void repeatNone(){
        Intent intent=new Intent(CTL_ACTION);
        intent.putExtra("control",3);
        sendBroadcast(intent);
    }

    public void shuffle(){
        Intent intent=new Intent(CTL_ACTION);
        intent.putExtra("control",4);
        sendBroadcast(intent);
    }

    public void next(){
        listPosition=listPosition+1;
        if(listPosition<=songs.size()-1){
            Music song=songs.get(listPosition);
            musicName.setText(song.getName());
            musicSinger.setText(song.getSinger());
            url=song.getUrl();
            Intent intent = new Intent(PlayerActivity.this,PlayerService.class);
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", song.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);
            startService(intent);
        }
        else{
            listPosition=listPosition+1;
            Toast.makeText(PlayerActivity.this,"没有下一首了", LENGTH_SHORT).show();
        }
    }

    public void audioTrackChange(int progress){
        Intent intent = new Intent(PlayerActivity.this,PlayerService.class);
        intent.setAction("com.wwj.media.MUSIC_SERVICE");
        intent.putExtra("url", url);
        intent.putExtra("listPosition", listPosition);
        if(isPause) {
            intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
        }
        else {
            intent.putExtra("MSG", AppConstant.PlayerMsg.PROGRESS_CHANGE);
        }
        intent.putExtra("progress", progress);
        startService(intent);
    }

    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(UPDATE_ACTION)){
                listPosition=intent.getIntExtra("current",-1);
                url = songs.get(listPosition).getUrl();
                if(listPosition >= 0) {
                    musicName.setText(songs.get(listPosition).getName());
                    musicSinger.setText(songs.get(listPosition).getSinger());
                }
                if(listPosition == 0) {
                    finalProgress.setText(MediaUtil.formatTime(songs.get(listPosition).getDuration()));
                    musicPause.setBackgroundResource(R.drawable.pause_selector);
                    isPause = true;
                }
            }
            else if(action.equals(MUSIC_CURRENT)){
                currentTime=intent.getIntExtra("currentTime",-1);
                currentProgress.setText(MediaUtil.formatTime((long)currentTime));
                progressBar.setProgress(currentTime);
            }
            else if(action.equals(MUSIC_DURATION)){
                duration=intent.getIntExtra("duration",-1);
                finalProgress.setText(MediaUtil.formatTime((long)duration));
                progressBar.setMax(duration);
            }
        }
    }
}
