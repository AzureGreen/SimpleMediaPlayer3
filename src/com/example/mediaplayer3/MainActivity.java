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
	 * ListView��ر���
	 */
	
	private SimpleAdapter	mAdapter;
	// �����б� ListView�ؼ�
	private ListView		mlvMusicList;		
	// ������Ϣ�б� Listģ��
	private List<MusicInfo> mMusicInfoList;	
	
	/**
	 * ����˵���ر���
	 */
	// ���ֱ���
	private TextView		mtvMusicTitle;		
	// ���ּ�
	private TextView		mtvMusicArtist;		
	// ר������
	private ImageView 		mivMusicAlbum; 	
	
	
	// ��תҳ�水ť
	private Button			mJumpButton;
	// ��һ��
	private	Button			mPlayNextButton;
	// ����/��ͣ
	private Button			mPauseOrPlayButton;
	
	private boolean			mbIsPlaying = false;
	private boolean			mbIsPause = false;
	
	// ��ǰ�����б��������
	private int				mTotalMusicCount;
	
	private int				mCurrentItemPosition;
	public MusicInfo		mCurrentPlayingMusicInfo;
	
	// �������񴫵ݵ�Intent
	private Intent 			mServiceIntent;		
	
	// ��ת��ǰ����ҳ��Intent
	private Intent			mCurrentPlayingIntent;
	
	// �㲥������
	MainReceiver 			mMainReceiver;
	
	//����Ҫ���͵�һЩAction
	public static final String ACTION_UPDATE = "com.example.action.ACTION_UPDATE";	//������Ϣ
	
	public static final String ACTION_REPLAY = "com.example.action.ACTION_REPLAY";	// �ָ�����
	
	public static final String ACTION_PAUSE = "com.example.action.ACTION_PAUSE";	// ��ͣ����
	
	public static final String ACTION_NEXT = "com.example.action.ACTION_NEXT";	// ������һ��
	
	public static final String ACTION_PRE = "com.example.action.ACTION_PRE";	// ������һ��
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);    
        
        getViewsId();
        
        // ���ý����Ϣ������Listģ����
        mMusicInfoList = MusicInfoHelp.getMusicInfo(MainActivity.this);
        
        mTotalMusicCount = mMusicInfoList.size();
        
        // ���� ����������ʾ��ListView��
        List<HashMap<String, String>> musicListMap = MusicInfoHelp.getMusicListMaps(mMusicInfoList);
        mAdapter = new SimpleAdapter(this, musicListMap,
				R.layout.activity_listview, new String[] { "MusicTitle", "Artist", "Time" },
				new int[] { R.id.lv_musicTitle, R.id.lv_musicSinger, R.id.musicTime });
		mlvMusicList.setAdapter(mAdapter);
        
        // Ϊ��������Ӽ���������Ӧ����¼�
        mlvMusicList.setOnItemClickListener(new musicListItemClickListener());
        
        mPauseOrPlayButton.setOnClickListener(new musicPauseOrPlayClickListener());
  
        mPlayNextButton.setOnClickListener(new musicPlayNextClickListener());
        
        mJumpButton.setOnClickListener(new musicJumpToCurrentPlayingClickListener());
        
        
        mMainReceiver = new MainReceiver();
		
        // ��̬ע��BroadCastReceiver
		IntentFilter filter = new IntentFilter();
		// ָ��BroadcastReceiver������Action
		filter.addAction(ACTION_UPDATE);
		filter.addAction(ACTION_REPLAY);
		filter.addAction(ACTION_PAUSE);
		filter.addAction(ACTION_NEXT);
		filter.addAction(ACTION_PRE);
		// ע��BroadcastReceiver
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
		// ���ٷ���
		mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_EXIT);
		startService(mServiceIntent);		
		
	//	super.onDestroy();
	}
	
	/**
	 * ��ÿؼ�Id
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
				Toast.makeText(getApplicationContext(), "��δ��������",
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
	 * ������һ�ף�mCurrentItemPosition+1ʵ�ֲ�����һ��
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
	 * �����ͣ�������ť
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
					// Ĭ�ϲ��ŵ�һ������
					mCurrentItemPosition = 0;
					playMusic(0);
				}
				else if (mbIsPlaying == true && mbIsPause == false)
				{
					// ��ͣ
					PauseMusic();
				}
				else if (mbIsPlaying == false && mbIsPause == true)
				{
					// ��������
					replayMusic();
				}
			}
			
		}
		
		/**
		 * ��������
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
		 * ��ͣ
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
	 * ������������б�
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
        	// ���ѡ������ֵ���Ϣ
        	mCurrentPlayingMusicInfo = mMusicInfoList.get(itemPosition);
        	
        	// ������ʾ����
        	mtvMusicTitle.setText(mCurrentPlayingMusicInfo.getMusicTitle()); 
        	mtvMusicArtist.setText(mCurrentPlayingMusicInfo.getArtist());
        	
        	// ��ȡר��λͼ����ΪСͼ  
            Bitmap bitmap = MusicInfoHelp.getArtwork(this, mCurrentPlayingMusicInfo.getMusicId(),
            		mCurrentPlayingMusicInfo.getAlbumId(), true, true);
            
            mivMusicAlbum.setImageBitmap(bitmap);  //������ʾר��ͼƬ
            
            // ���һϵ��Ҫ���ݵ�����
            mServiceIntent = new Intent(MainActivity.this, PlayService.class); 
            mServiceIntent.putExtra("MusicTitle", mCurrentPlayingMusicInfo.getMusicTitle());
            mServiceIntent.putExtra("FileFullPath", mCurrentPlayingMusicInfo.getFileFullPath());
            mServiceIntent.putExtra("Artist", mCurrentPlayingMusicInfo.getArtist());
            mServiceIntent.putExtra("CurrentItemPosition", itemPosition);
//			intent.putExtra("CurrentTime", CurrentTime);

            mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_PALY);
			// ��������
			startService(mServiceIntent);
			mPauseOrPlayButton.setBackgroundResource(R.drawable.pause);
			mbIsPlaying = true;
			mbIsPause = false;
        }  
    }  
	
	
	public class MainReceiver extends BroadcastReceiver 
	{

		/**
		 * �������ֿؼ���Ϣ
		 */
		public void UpdateMusicInfoViews()
		{
			// ��õ�ǰ�������ֵ���Ϣ
        	mCurrentPlayingMusicInfo = mMusicInfoList.get(mCurrentItemPosition);
        	
        	// ������ʾ����
        	mtvMusicTitle.setText(mCurrentPlayingMusicInfo.getMusicTitle()); 
        	mtvMusicArtist.setText(mCurrentPlayingMusicInfo.getArtist());
        	
        	// ��ȡר��λͼ����ΪСͼ  
            Bitmap bitmap = MusicInfoHelp.getArtwork(MainActivity.this, mCurrentPlayingMusicInfo.getMusicId(),
            		mCurrentPlayingMusicInfo.getAlbumId(), true, true);
            
            mivMusicAlbum.setImageBitmap(bitmap);  //������ʾר��ͼƬ
		}
		
		
		/**
		 * ���չ㲥
		 */
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			
			// ��ȡIntent�е�current��Ϣ��current����ǰ���ڲ��ŵĸ���
			mCurrentItemPosition = intent.getIntExtra("CurrentItemPosition", 0);
			
			mbIsPause = intent.getBooleanExtra("IsPause", false);
			
			/**
			 * �������ڲ������ֽ�����Ϣ
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
				// �޸�boolean����
				mbIsPlaying = true;
				mbIsPause = false;
				
				// �޸� ��ͣ��ťͼ��
				mPauseOrPlayButton.setBackgroundResource(R.drawable.pause);
	
			}
			
			else if (action.equals(ACTION_PAUSE))
			{
				// �޸�boolean����
				mbIsPlaying = false;
				mbIsPause = true;
				
				// �޸� ��ͣ��ťͼ��
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
