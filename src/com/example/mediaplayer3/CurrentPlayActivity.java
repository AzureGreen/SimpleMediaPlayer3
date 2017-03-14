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
		
	// ��һ��
	private Button			mPlayPreButton;
	// ��һ��
	private	Button			mPlayNextButton;
	// ����/��ͣ
	private Button			mPauseOrPlayButton;
	
	private Button			mBackToMainButton;
	
	private boolean			mbIsPlaying;
	
	private boolean         mbIsPause;
	
	private int				mCurrentItemPosition = 0;
	
	private int 			mTotalMusicCount;
	
	public MusicInfo		mCurrentPlayingMusicInfo;
	
	private Intent			mServiceIntent;
	
	public static final String ACTION_REPLAY = "com.example.action.ACTION_REPLAY";	// �ָ�����
	
	public static final String ACTION_PAUSE = "com.example.action.ACTION_PAUSE";	// ��ͣ����
	
	public static final String ACTION_NEXT = "com.example.action.ACTION_NEXT";	// ��ͣ����
	
	public static final String ACTION_PRE = "com.example.action.ACTION_PRE";	// ��ͣ����
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentplay);
		
        mMusicInfoList = MusicInfoHelp.getMusicInfo(CurrentPlayActivity.this);
        
        // ��ʼ��Intent��������ز���
		initIntentParams();
		
		// ���ÿؼ��ϵ�������Ϣ
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
	 * ������һ�ף�mCurrentItemPosition+1ʵ�ֲ�����һ��
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
	 * ������һ�ף�mCurrentItemPosition+1ʵ�ֲ�����һ��
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
	 * �����ͣ�������ť
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
				// Ĭ�ϲ��ŵ�һ������
				mCurrentItemPosition = 0;
			//	playMusic(0);
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
		
		/**
		 * ��������
		 */
		private void replayMusic() 
		{
			mPauseOrPlayButton.setBackgroundResource(R.drawable.pausecurrent);
			mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_CONTINUE);
			startService(mServiceIntent);
			
/*			Intent sendIntent = new Intent(ACTION_REPLAY);
			// ���͹㲥������Activity����е�BroadcastReceiver���յ�
			sendBroadcast(sendIntent);
*/			
			mbIsPlaying = true;
			mbIsPause = false;
			
		}
		
		/**
		 * ��ͣ
		 */
		private void PauseMusic() 
		{
			mPauseOrPlayButton.setBackgroundResource(R.drawable.playcurrent);
			mServiceIntent.putExtra("PlayMsg", PlayStatusConst.MSG_PAUSE);
			startService(mServiceIntent);
/*			
			Intent sendIntent = new Intent(ACTION_PAUSE);
			// ���͹㲥������Activity����е�BroadcastReceiver���յ�
			sendBroadcast(sendIntent);
*/					
			mbIsPlaying = false;
			mbIsPause = true;	
		}

	}

	
	
	/**
	 * ���ÿؼ��ϵ�������Ϣ
	 */
	public void setMusicInfoViews() 
	{
		// TODO Auto-generated method stub
		// ������ʾ����
		mtvMusicTitle.setText(mCurrentPlayingMusicInfo.getMusicTitle()); 
    	mtvMusicArtist.setText(mCurrentPlayingMusicInfo.getArtist());
    	
    	// ��ȡר��λͼ����Ϊ��ͼ  
        Bitmap bitmap = MusicInfoHelp.getArtwork(this, mCurrentPlayingMusicInfo.getMusicId(),
        		mCurrentPlayingMusicInfo.getAlbumId(), true, false);

        mivMusicAlbum.setImageBitmap(bitmap);  //������ʾר��ͼƬ
		
	}


	/**
	 * ��ÿؼ�Id
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
	 * ��ʼ��Intent��������ز���
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
