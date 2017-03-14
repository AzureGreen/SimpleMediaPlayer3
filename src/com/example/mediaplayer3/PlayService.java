package com.example.mediaplayer3;

import java.util.List;


import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;

public class PlayService extends Service 
{

	// 媒体播放器对象 
	private MediaPlayer mMediaPlayer;
	// 音乐信息列表 List模板
	private List<MusicInfo> mMusicInfoList;		
	// 音乐文件路径
	private String 		mstrFileFullPath = null; 
	// 当前选择播放项
	private int			mCurrentItemPosition = 0;
	// 播放消息
	private int 		mPlayMessage = 0;
	// 记录当前正在播放的音乐
//	private int 		mCurrent = 0; 		
	// 是否暂停
	private boolean		mbIsPause = false;
	
	
	//服务要发送的一些Action
	public static final String ACTION_UPDATE = "com.example.action.ACTION_UPDATE";	//更新动作
	
	public static final String ACTION_REPLAY = "com.example.action.ACTION_REPLAY";	// 恢复播放
	
	public static final String ACTION_PAUSE = "com.example.action.ACTION_PAUSE";	// 暂停播放
	
	public static final String ACTION_NEXT = "com.example.action.ACTION_NEXT";	// 播放下一首
	
	public static final String ACTION_PRE = "com.example.action.ACTION_PRE";	// 播放上一首
	
	@Override
	public void onCreate() 
	{
		
		mMediaPlayer = new MediaPlayer();
		mMusicInfoList = MusicInfoHelp.getMusicInfo(PlayService.this);
		
		/**
		 * 设置音乐播放完成时的监听器
		 */
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() 
		{
			@Override
			public void onCompletion(MediaPlayer mp) 
			{			
				mCurrentItemPosition++;
				if(mCurrentItemPosition > mMusicInfoList.size() - 1) 
				{	//变为第一首的位置继续播放
					mMediaPlayer.seekTo(0);
					mCurrentItemPosition = 0;
				}
				
				UpdateMusicInfoViews(ACTION_UPDATE);
					
				play(0);
			}
		});
		
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 销毁服务对象
	 */
	@Override
	public void onDestroy()
	{
		if (mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer.release();	
			mMediaPlayer = null;
		}
		super.onDestroy();
	}
	
	
	/**
	 * 当服务启动时调用
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		mCurrentItemPosition = intent.getIntExtra("CurrentItemPosition", 0);
		mPlayMessage = intent.getIntExtra("PlayMsg", 0);
		
		switch (mPlayMessage)
		{
		case PlayStatusConst.MSG_PALY:
		{
			play(0);
			break;
		}
		case PlayStatusConst.MSG_STOP:
		{
			
			break;
		}
		case PlayStatusConst.MSG_PAUSE:
		{
			pause();
			break;
		}
		case PlayStatusConst.MSG_CONTINUE:
		{
			replay();
			break;
		}
		case PlayStatusConst.MSG_NEXT:
		{
			next();
			break;
		}
		case PlayStatusConst.MSG_PRE:
		{
			pre();
			break;
		}
		case PlayStatusConst.MSG_EXIT:
		{
			stop();
			break;
		}
		default:
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 停止播放
	 */
	 private void stop()
	 {
		 if (mMediaPlayer != null)
		 {
			 mMediaPlayer.stop();
			 try
			 {
				// 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数 
				 mMediaPlayer.prepare(); 
			 }
			 catch (Exception e)
			 {
				 e.printStackTrace();
			 }
		 }
	 }

	/** 
     * 播放音乐 
     * @param CurrentTime 
     */  
    private void play(int CurrentTime) 
    {  
        try 
        {
        	mMediaPlayer.reset();//把各项参数恢复到初始状态  
        	
        	mstrFileFullPath = mMusicInfoList.get(mCurrentItemPosition).getFileFullPath();
        	
        	mMediaPlayer.setDataSource(mstrFileFullPath);
            // 真正装载音频文件
        	mMediaPlayer.prepare();  
            // 注册一个监听器  
        	mMediaPlayer.setOnPreparedListener(new PreparedListener(CurrentTime));
        }
        catch (Exception e) 
        {  
            e.printStackTrace();  
        }  
    }
	
    private void pre() 
    {
    	if (mbIsPause)
    	{
    		mbIsPause = false;
    		UpdateMusicInfoViews(ACTION_REPLAY);
    	}
    	
   
    	UpdateMusicInfoViews(ACTION_PRE);
    	
		play(0);
	}
    
    private void next() 
    {
    	if (mbIsPause)
    	{
    		mbIsPause = false;
    		UpdateMusicInfoViews(ACTION_REPLAY);
    	}
    	
    	
    	UpdateMusicInfoViews(ACTION_NEXT);
    	
		play(0);
	}
    
    /**
     * 继续播放
     */
    private void replay()
    {
		if (mbIsPause) 
		{
			mMediaPlayer.start();
			mbIsPause = false;
			
			UpdateMusicInfoViews(ACTION_REPLAY);			
		}
	}
    
    /**
     * 暂停音乐
     */
	private void pause() 
	{
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) 
		{
			mMediaPlayer.pause();
			mbIsPause = true;	
			
			UpdateMusicInfoViews(ACTION_PAUSE);		
		}
	}
    
	
	/**
	 * 更新音乐控件信息
	 */
    private void UpdateMusicInfoViews(String action)
    {
    	Intent sendIntent = new Intent(action);
		sendIntent.putExtra("CurrentItemPosition", mCurrentItemPosition);
		sendIntent.putExtra("IsPause", mbIsPause);
		// 发送广播，将被Activity组件中的BroadcastReceiver接收到
		sendBroadcast(sendIntent);
    }
    
    /**
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放   
     * @author Jiawei
     *
     */
    private final class PreparedListener implements OnPreparedListener
    {  
        private int positon;  
          
        public PreparedListener(int positon) 
        {  
            this.positon = positon;  
        }  
          
        @Override  
        public void onPrepared(MediaPlayer mp) 
        {  
        	mMediaPlayer.start();    //开始播放  
            if(positon > 0) 
            {    //如果音乐不是从头播放  
            	mMediaPlayer.seekTo(positon);  
            }  
        }  
    }  
	
	
}
