package com.example.mediaplayer3;


import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;




import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity 
{

	/**
	 * ListView相关变量
	 */
	
	private SimpleAdapter	mAdapter;
	// 音乐列表 ListView控件
	private ListView		mlvMusicList;		
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
	
	
	// 跳转页面按钮
	private Button			mJumpButton;
	// 下一曲
	private	Button			mPlayNextButton;
	// 播放/暂停
	private Button			mPauseOrPlayButton;
	
	private boolean			mbIsPlaying = false;
	private boolean			mbIsPause = false;
	
	// 当前播放列表歌曲总数
	private int				mTotalMusicCount;
	
	private int				mCurrentItemPosition;
	public MusicInfo		mCurrentPlayingMusicInfo;
	
	// 启动服务传递的Intent
	private Intent 			mServiceIntent;		
	
	// 跳转当前播放页面Intent
	private Intent			mCurrentPlayingIntent;
	
	// 广播接受者
	MainReceiver 			mMainReceiver;
	
	//服务要发送的一些Action
	public static final String ACTION_UPDATE = "com.example.action.ACTION_UPDATE";	//更新信息
	
	public static final String ACTION_REPLAY = "com.example.action.ACTION_REPLAY";	// 恢复播放
	
	public static final String ACTION_PAUSE = "com.example.action.ACTION_PAUSE";	// 暂停播放
	
	public static final String ACTION_NEXT = "com.example.action.ACTION_NEXT";	// 播放下一首
	
	public static final String ACTION_PRE = "com.example.action.ACTION_PRE";	// 播放上一首
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);    
        
        getViewsId();
        
        // 获得媒体信息，存入List模板里
        mMusicInfoList = MusicInfoHelp.getMusicInfo(MainActivity.this);
        
        mTotalMusicCount = mMusicInfoList.size();
        
        // 设置 适配器，显示在ListView上
        List<HashMap<String, String>> musicListMap = MusicInfoHelp.getMusicListMaps(mMusicInfoList);
        mAdapter = new SimpleAdapter(this, musicListMap,
				R.layout.activity_listview, new String[] { "MusicTitle", "Artist", "Time" },
				new int[] { R.id.lv_musicTitle, R.id.lv_musicSinger, R.id.musicTime });
		mlvMusicList.setAdapter(mAdapter);
        
        // 为主界面添加监听器，响应点击事件
        mlvMusicList.setOnItemClickListener(new musicListItemClickListener());
        
        mPauseOrPlayButton.setOnClickListener(new musicPauseOrPlayClickListener());
  
        mPlayNextButton.setOnClickListener(new musicPlayNextClickListener());
        
        mJumpButton.setOnClickListener(new musicJumpToCurrentPlayingClickListener());
        
        
        mMainReceiver = new MainReceiver();
		
        // 动态注册BroadCastReceiver
		IntentFilter filter = new IntentFilter();
		// 指定BroadcastReceiver监听的Action
		filter.addAction(ACTION_UPDATE);
		filter.addAction(ACTION_REPLAY);
		filter.addAction(ACTION_PAUSE);
		filter.addAction(ACTION_NEXT);
		filter.addAction(ACTION_PRE);
		// 注册BroadcastReceiver
		registerReceiver(mMainReceiver, filter);
        
        
    }
		
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}	
	
	@Override
	protected void onDestroy() 
	{
		// 销毁服务
		mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_EXIT);
		startService(mServiceIntent);		
		
	//	super.onDestroy();
	}
	
	/**
	 * 获得控件Id
	 */
	public void getViewsId() 
	{
		// TODO Auto-generated method stub
		mlvMusicList = (ListView) findViewById(R.id.musicList);
		mtvMusicTitle = (TextView) findViewById(R.id.textMusicTile);
		mtvMusicArtist = (TextView) findViewById(R.id.textArtist);
		mivMusicAlbum = (ImageView) findViewById(R.id.musicAlbum);
		mPauseOrPlayButton = (Button) findViewById(R.id.buttonPauseOrPlay);
		mPlayNextButton = (Button) findViewById(R.id.buttonNext);
		mJumpButton = (Button) findViewById(R.id.buttonJump);
	}

	
	
	public class musicJumpToCurrentPlayingClickListener implements OnClickListener 
	{

		@Override
		public void onClick(View v) 
		{
			if (mbIsPlaying == false && mbIsPause == false)
			{
				Toast.makeText(getApplicationContext(), "尚未播放音乐",
					     Toast.LENGTH_SHORT).show();
				return ;
			}
					
			mCurrentPlayingIntent = new Intent(MainActivity.this, CurrentPlayActivity.class);
		
			mCurrentPlayingIntent.putExtra("MusicInfo", mCurrentPlayingMusicInfo);
			
			mCurrentPlayingIntent.putExtra("CurrentItemPosition", mCurrentItemPosition);
			
			mCurrentPlayingIntent.putExtra("TotalMusicCount", mTotalMusicCount);
			
			mCurrentPlayingIntent.putExtra("IsPlaying", mbIsPlaying);
			
			mCurrentPlayingIntent.putExtra("IsPause", mbIsPause);
			
			startActivity(mCurrentPlayingIntent);
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
			mCurrentItemPosition += 1;
			if (mCurrentItemPosition > mTotalMusicCount - 1)
			{
				mCurrentItemPosition = 0;				
			} 
			
			playMusic(mCurrentItemPosition);
			
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
			if (mMusicInfoList != null)
			{
				if (mbIsPlaying == false && mbIsPause == false)
				{
					// 默认播放第一个歌曲
					mCurrentItemPosition = 0;
					playMusic(0);
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
			
		}
		
		/**
		 * 继续播放
		 */
		private void replayMusic() 
		{
			mPauseOrPlayButton.setBackgroundResource(R.drawable.pause);
			mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_CONTINUE);
			startService(mServiceIntent);
			mbIsPlaying = true;
			mbIsPause = false;
			
		}
		
		/**
		 * 暂停
		 */
		private void PauseMusic() 
		{
			mPauseOrPlayButton.setBackgroundResource(R.drawable.play);
			mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_PAUSE);
			startService(mServiceIntent);
			mbIsPlaying = false;
			mbIsPause = true;	
		}

	}

	
	
	
	/**
	 * 监听点击音乐列表
	 * @author Jiawei
	 *
	 */
	public class musicListItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			if (mMusicInfoList != null)
			{
				mCurrentItemPosition = position;
				playMusic(mCurrentItemPosition);
			}
		}
		
	}
	

	public void playMusic(int itemPosition) 
	{  
        if (mMusicInfoList != null) 
        {
        	// 获得选择的音乐的信息
        	mCurrentPlayingMusicInfo = mMusicInfoList.get(itemPosition);
        	
        	// 这里显示标题
        	mtvMusicTitle.setText(mCurrentPlayingMusicInfo.getMusicTitle()); 
        	mtvMusicArtist.setText(mCurrentPlayingMusicInfo.getArtist());
        	
        	// 获取专辑位图对象，为小图  
            Bitmap bitmap = MusicInfoHelp.getArtwork(this, mCurrentPlayingMusicInfo.getMusicId(),
            		mCurrentPlayingMusicInfo.getAlbumId(), true, true);
            
            mivMusicAlbum.setImageBitmap(bitmap);  //这里显示专辑图片
            
            // 添加一系列要传递的数据
            mServiceIntent = new Intent(MainActivity.this, PlayService.class); 
            mServiceIntent.putExtra("MusicTitle", mCurrentPlayingMusicInfo.getMusicTitle());
            mServiceIntent.putExtra("FileFullPath", mCurrentPlayingMusicInfo.getFileFullPath());
            mServiceIntent.putExtra("Artist", mCurrentPlayingMusicInfo.getArtist());
            mServiceIntent.putExtra("CurrentItemPosition", itemPosition);
//			intent.putExtra("CurrentTime", CurrentTime);

            mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_PALY);
			// 启动服务
			startService(mServiceIntent);
			mPauseOrPlayButton.setBackgroundResource(R.drawable.pause);
			mbIsPlaying = true;
			mbIsPause = false;
        }  
    }  
	
	
	public class MainReceiver extends BroadcastReceiver 
	{

		/**
		 * 更新音乐控件信息
		 */
		public void UpdateMusicInfoViews()
		{
			// 获得当前播放音乐的信息
        	mCurrentPlayingMusicInfo = mMusicInfoList.get(mCurrentItemPosition);
        	
        	// 这里显示标题
        	mtvMusicTitle.setText(mCurrentPlayingMusicInfo.getMusicTitle()); 
        	mtvMusicArtist.setText(mCurrentPlayingMusicInfo.getArtist());
        	
        	// 获取专辑位图对象，为小图  
            Bitmap bitmap = MusicInfoHelp.getArtwork(MainActivity.this, mCurrentPlayingMusicInfo.getMusicId(),
            		mCurrentPlayingMusicInfo.getAlbumId(), true, true);
            
            mivMusicAlbum.setImageBitmap(bitmap);  //这里显示专辑图片
		}
		
		
		/**
		 * 接收广播
		 */
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			
			// 获取Intent中的current消息，current代表当前正在播放的歌曲
			mCurrentItemPosition = intent.getIntExtra("CurrentItemPosition", 0);
			
			mbIsPause = intent.getBooleanExtra("IsPause", false);
			
			/**
			 * 更新正在播放音乐界面信息
			 */
			if (action.equals(ACTION_UPDATE)) 
			{
				if (mCurrentItemPosition >= 0) 
				{
					UpdateMusicInfoViews();
				}
			} 
			
			else if (action.equals(ACTION_REPLAY))
			{
				// 修改boolean变量
				mbIsPlaying = true;
				mbIsPause = false;
				
				// 修改 暂停按钮图标
				mPauseOrPlayButton.setBackgroundResource(R.drawable.pause);
	
			}
			
			else if (action.equals(ACTION_PAUSE))
			{
				// 修改boolean变量
				mbIsPlaying = false;
				mbIsPause = true;
				
				// 修改 暂停按钮图标
				mPauseOrPlayButton.setBackgroundResource(R.drawable.play);
				
			}
			
			else if (action.equals(ACTION_NEXT))
			{
				if (mCurrentItemPosition >= 0) 
				{
					UpdateMusicInfoViews();
				}
			}
			
			else if (action.equals(ACTION_PRE))
			{
				if (mCurrentItemPosition >= 0) 
				{
					UpdateMusicInfoViews();
				}
			}
		}

	}
	
	
	
	

}
