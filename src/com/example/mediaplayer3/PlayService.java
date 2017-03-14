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

	// ý�岥�������� 
	private MediaPlayer mMediaPlayer;
	// ������Ϣ�б� Listģ��
	private List<MusicInfo> mMusicInfoList;		
	// �����ļ�·��
	private String 		mstrFileFullPath = null; 
	// ��ǰѡ�񲥷���
	private int			mCurrentItemPosition = 0;
	// ������Ϣ
	private int 		mPlayMessage = 0;
	// ��¼��ǰ���ڲ��ŵ�����
//	private int 		mCurrent = 0; 		
	// �Ƿ���ͣ
	private boolean		mbIsPause = false;
	
	
	//����Ҫ���͵�һЩAction
	public static final String ACTION_UPDATE = "com.example.action.ACTION_UPDATE";	//���¶���
	
	public static final String ACTION_REPLAY = "com.example.action.ACTION_REPLAY";	// �ָ�����
	
	public static final String ACTION_PAUSE = "com.example.action.ACTION_PAUSE";	// ��ͣ����
	
	public static final String ACTION_NEXT = "com.example.action.ACTION_NEXT";	// ������һ��
	
	public static final String ACTION_PRE = "com.example.action.ACTION_PRE";	// ������һ��
	
	@Override
	public void onCreate() 
	{
		
		mMediaPlayer = new MediaPlayer();
		mMusicInfoList = MusicInfoHelp.getMusicInfo(PlayService.this);
		
		/**
		 * �������ֲ������ʱ�ļ�����
		 */
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() 
		{
			@Override
			public void onCompletion(MediaPlayer mp) 
			{			
				mCurrentItemPosition++;
				if(mCurrentItemPosition > mMusicInfoList.size() - 1) 
				{	//��Ϊ��һ�׵�λ�ü�������
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
	 * ���ٷ������
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
	 * ����������ʱ����
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
	 * ֹͣ����
	 */
	 private void stop()
	 {
		 if (mMediaPlayer != null)
		 {
			 mMediaPlayer.stop();
			 try
			 {
				// �ڵ���stop�������Ҫ�ٴ�ͨ��start���в���,��Ҫ֮ǰ����prepare���� 
				 mMediaPlayer.prepare(); 
			 }
			 catch (Exception e)
			 {
				 e.printStackTrace();
			 }
		 }
	 }

	/** 
     * �������� 
     * @param CurrentTime 
     */  
    private void play(int CurrentTime) 
    {  
        try 
        {
        	mMediaPlayer.reset();//�Ѹ�������ָ�����ʼ״̬  
        	
        	mstrFileFullPath = mMusicInfoList.get(mCurrentItemPosition).getFileFullPath();
        	
        	mMediaPlayer.setDataSource(mstrFileFullPath);
            // ����װ����Ƶ�ļ�
        	mMediaPlayer.prepare();  
            // ע��һ��������  
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
     * ��������
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
     * ��ͣ����
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
	 * �������ֿؼ���Ϣ
	 */
    private void UpdateMusicInfoViews(String action)
    {
    	Intent sendIntent = new Intent(action);
		sendIntent.putExtra("CurrentItemPosition", mCurrentItemPosition);
		sendIntent.putExtra("IsPause", mbIsPause);
		// ���͹㲥������Activity����е�BroadcastReceiver���յ�
		sendBroadcast(sendIntent);
    }
    
    /**
     * ʵ��һ��OnPrepareLister�ӿ�,������׼���õ�ʱ��ʼ����   
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
        	mMediaPlayer.start();    //��ʼ����  
            if(positon > 0) 
            {    //������ֲ��Ǵ�ͷ����  
            	mMediaPlayer.seekTo(positon);  
            }  
        }  
    }  
	
	
}
