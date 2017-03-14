package com.example.mediaplayer3;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CurrentPlayActivity extends Activity 
{
	// 音乐信息列表 List模板
	private List<MusicInfo> mMusicInfoList;	
	
	/**
	 * 下面菜单相关变量
	 */
	// 音乐标题
	private TextView		mtvMusicTitle;		
	// 音乐家
	private TextView		mtvMusicArtist;		
	// 专辑封面
	private ImageView 		mivMusicAlbum; 	
		
	// 上一曲
	private Button			mPlayPreButton;
	// 下一曲
	private	Button			mPlayNextButton;
	// 播放/暂停
	private Button			mPauseOrPlayButton;
	
	private Button			mBackToMainButton;
	
	private boolean			mbIsPlaying;
	
	private boolean         mbIsPause;
	
	private int				mCurrentItemPosition = 0;
	
	private int 			mTotalMusicCount;
	
	public MusicInfo		mCurrentPlayingMusicInfo;
	
	private Intent			mServiceIntent;
	
	public static final String ACTION_REPLAY = "com.example.action.ACTION_REPLAY";	// 恢复播放
	
	public static final String ACTION_PAUSE = "com.example.action.ACTION_PAUSE";	// 暂停播放
	
	public static final String ACTION_NEXT = "com.example.action.ACTION_NEXT";	// 暂停播放
	
	public static final String ACTION_PRE = "com.example.action.ACTION_PRE";	// 暂停播放
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentplay);
		
        mMusicInfoList = MusicInfoHelp.getMusicInfo(CurrentPlayActivity.this);
        
        // 初始化Intent传来的相关参数
		initIntentParams();
		
		// 设置控件上的音乐信息
		getViewsId();
		setMusicInfoViews();
		
		mServiceIntent = new Intent(CurrentPlayActivity.this, PlayService.class);
		
		mPauseOrPlayButton.setOnClickListener(new musicPauseOrPlayClickListener());
		
		mPlayNextButton.setOnClickListener(new musicPlayNextClickListener());
		
		mPlayPreButton.setOnClickListener(new musicPlayPreClickListener());
		
		mBackToMainButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override		
			public void onClick(View v) 
			{	
				 CurrentPlayActivity.this.finish();
			}
		}
		);
		
	}
	
	
	
	/**
	 * 播放下一首，mCurrentItemPosition+1实现播放上一首
	 * @author Jiawei
	 *
	 */
	public class musicPlayPreClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			mCurrentItemPosition --;
			if (mCurrentItemPosition < 0)
			{
				mCurrentItemPosition = mTotalMusicCount - 1;				
			} 
			
			mCurrentPlayingMusicInfo = mMusicInfoList.get(mCurrentItemPosition);
			
			setMusicInfoViews();
			
			mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_PRE);
			mServiceIntent.putExtra("CurrentItemPosition", mCurrentItemPosition);
			startService(mServiceIntent);

			mbIsPlaying = true;
			mbIsPause = false;

		}
		
	}
	
	/**
	 * 播放下一首，mCurrentItemPosition+1实现播放下一首
	 * @author Jiawei
	 *
	 */
	public class musicPlayNextClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			mCurrentItemPosition ++;
			if (mCurrentItemPosition > mTotalMusicCount - 1)
			{
				mCurrentItemPosition = 0;				
			} 
			
			mCurrentPlayingMusicInfo = mMusicInfoList.get(mCurrentItemPosition);
			
			setMusicInfoViews();
			
			mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_NEXT);
			mServiceIntent.putExtra("CurrentItemPosition", mCurrentItemPosition);
			startService(mServiceIntent);
			
			mbIsPlaying = true;
			mbIsPause = false;
			
		}
		
	}
	
	
	/**
	 * 点击暂停或继续按钮
	 * @author Jiawei
	 *
	 */
	public class musicPauseOrPlayClickListener implements OnClickListener 
	{
		@Override
		public void onClick(View v) 
		{
			if (mbIsPlaying == false && mbIsPause == false)
			{
				// 默认播放第一个歌曲
				mCurrentItemPosition = 0;
			//	playMusic(0);
			}
			else if (mbIsPlaying == true && mbIsPause == false)
			{
				// 暂停
				PauseMusic();
			}
			else if (mbIsPlaying == false && mbIsPause == true)
			{
				// 继续播放
				replayMusic();
			}
			
		}
		
		/**
		 * 继续播放
		 */
		private void replayMusic() 
		{
			mPauseOrPlayButton.setBackgroundResource(R.drawable.pausecurrent);
			mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_CONTINUE);
			startService(mServiceIntent);
			
/*			Intent sendIntent = new Intent(ACTION_REPLAY);
			// 发送广播，将被Activity组件中的BroadcastReceiver接收到
			sendBroadcast(sendIntent);
*/			
			mbIsPlaying = true;
			mbIsPause = false;
			
		}
		
		/**
		 * 暂停
		 */
		private void PauseMusic() 
		{
			mPauseOrPlayButton.setBackgroundResource(R.drawable.playcurrent);
			mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_PAUSE);
			startService(mServiceIntent);
/*			
			Intent sendIntent = new Intent(ACTION_PAUSE);
			// 发送广播，将被Activity组件中的BroadcastReceiver接收到
			sendBroadcast(sendIntent);
*/					
			mbIsPlaying = false;
			mbIsPause = true;	
		}

	}

	
	
	/**
	 * 设置控件上的音乐信息
	 */
	public void setMusicInfoViews() 
	{
		// TODO Auto-generated method stub
		// 这里显示标题
		mtvMusicTitle.setText(mCurrentPlayingMusicInfo.getMusicTitle()); 
    	mtvMusicArtist.setText(mCurrentPlayingMusicInfo.getArtist());
    	
    	// 获取专辑位图对象，为大图  
        Bitmap bitmap = MusicInfoHelp.getArtwork(this, mCurrentPlayingMusicInfo.getMusicId(),
        		mCurrentPlayingMusicInfo.getAlbumId(), true, false);

        mivMusicAlbum.setImageBitmap(bitmap);  //这里显示专辑图片
		
	}


	/**
	 * 获得控件Id
	 */
	public void getViewsId() 
	{
		// TODO Auto-generated method stub
		
		mtvMusicTitle = (TextView) findViewById(R.id.textCurrentMusicTile);
		mtvMusicArtist = (TextView) findViewById(R.id.textCurrentArtist);
		
		mtvMusicArtist = (TextView) findViewById(R.id.textCurrentArtist);
		mivMusicAlbum = (ImageView) findViewById(R.id.currentMusicAlbum);
		mPauseOrPlayButton = (Button) findViewById(R.id.buttonCurrentPauseOrPlay);
		mPlayNextButton = (Button) findViewById(R.id.buttonCurrentNext);
		mPlayPreButton = (Button) findViewById(R.id.buttonCurrentPre);
		mBackToMainButton = (Button) findViewById(R.id.buttonReturn);
		
	}
	
	/**
	 * 初始化Intent传来的相关参数
	 */
	public void initIntentParams()
	{
		Intent intent = this.getIntent(); 
		mCurrentPlayingMusicInfo = (MusicInfo)intent.getSerializableExtra("MusicInfo");
		
		mCurrentItemPosition = intent.getIntExtra("CurrentItemPosition", 0);
		mTotalMusicCount = intent.getIntExtra("TotalMusicCount", 0);
		mbIsPlaying = intent.getBooleanExtra("IsPlaying", false);
		mbIsPause = intent.getBooleanExtra("IsPause", false);
		
	}
	
}
