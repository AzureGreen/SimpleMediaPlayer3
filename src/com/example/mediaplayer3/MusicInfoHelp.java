package com.example.mediaplayer3;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.SimpleAdapter;


public class MusicInfoHelp 
{
	//获取专辑封面的Uri
	private static final Uri mAlbumArtUri = Uri.parse("content://media/external/audio/albumart");
		
	/**
	 * Android系统会自动把添加到SD卡上的歌曲添加到一个SQLite数据库里
	 * 用于从数据库中查询歌曲的信息，保存在List当中
	 * @return
	 */
	public static List<MusicInfo> getMusicInfo(Context context) 
	{  
	    Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
	    		null, 		// 字段　没有字段　就是查询所有信息　相当于SQL语句中的　“ * ”
	    		null, 		// 查询条件
	    		null,		// 条件的对应的参数 
	    		MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  	// 排序方式
	    
	    List<MusicInfo> musicInfoList = new ArrayList<MusicInfo>();
	    
	    for (int i = 0; i < cursor.getCount(); i++) 
	    {  
	    	MusicInfo musicInfo = new MusicInfo();  
	        cursor.moveToNext();  
	        
	        long ulMusicId = cursor.getLong(cursor  
	            .getColumnIndex(MediaStore.Audio.Media._ID));   // 音乐id  
	        
	        String strMusicTitle = cursor.getString((cursor   
	            .getColumnIndex(MediaStore.Audio.Media.TITLE)));// 音乐标题  
	        
	        String strArtist = cursor.getString(cursor  
	            .getColumnIndex(MediaStore.Audio.Media.ARTIST));// 艺术家  
	        
	        String strAlbum = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));	// 专辑名
	        
			String strDisplayName = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));	// 歌曲文件名
			
			long ulAlbumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
	        
	        long ulTime = cursor.getLong(cursor  
	            .getColumnIndex(MediaStore.Audio.Media.DURATION));// 时长  
	        
	        long ulFileSize = cursor.getLong(cursor  
	            .getColumnIndex(MediaStore.Audio.Media.SIZE));  // 文件大小  
	        
	        String strFileFullPath = cursor.getString(cursor  
	            .getColumnIndex(MediaStore.Audio.Media.DATA));  // 文件路径  
	        
		    int isMusic = cursor.getInt(cursor  
		        .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐  
		    if (isMusic != 0) 
		    {   //只把音乐添加到集合当中  
		    	musicInfo.setMusicId(ulMusicId);  
		    	musicInfo.setMusicTitle(strMusicTitle);  
		    	musicInfo.setArtist(strArtist);  
		    	musicInfo.setAlbum(strAlbum);
		    	musicInfo.setDisplayName(strDisplayName);
		    	musicInfo.setAlbumId(ulAlbumId);
		    	musicInfo.setTime(ulTime);  
		    	musicInfo.setFileSize(ulFileSize);  
		    	musicInfo.setFileFullPath(strFileFullPath);  
		    	musicInfoList.add(musicInfo);  
		    }  
	    } 
	    
	return musicInfoList;  
	}  
	
	
	/**
	 * 
	 * @param musicInfoList
	 * @return
	 */
	public static List<HashMap<String, String>> getMusicListMaps(List<MusicInfo> musicInfoList) 
	{
		List<HashMap<String, String>> musicListMap = new ArrayList<HashMap<String, String>>();
		
		for (Iterator iterator = musicInfoList.iterator(); iterator.hasNext();) 
		{
			MusicInfo musicInfo = (MusicInfo) iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			
			map.put("MusicTitle", musicInfo.getMusicTitle());
			map.put("Artist", musicInfo.getArtist());
			map.put("Album", musicInfo.getAlbum());
			map.put("DisplayName", musicInfo.getDisplayName());
			map.put("AlbumId", String.valueOf(musicInfo.getAlbumId()));
		
			String	strTime = formatTime(musicInfo.getTime());
			
			map.put("Time", strTime);			
			map.put("FileSize", String.valueOf(musicInfo.getFileSize()));
			map.put("FileFullPath", musicInfo.getFileFullPath());
			
			musicListMap.add(map);
			
		}
		return musicListMap;	
	}
	
	 /** 
     * 格式化时间，将毫秒转换为分:秒格式 
     * @param time 
     * @return 
     */  
    public static String formatTime(long ulTime) 
    {  
    	ulTime /= 1000;
    	int Minutes = (int) (ulTime / 60);
		int Seconds = (int) (ulTime % 60);		
    	
		String strMinutes = String.valueOf(Minutes);
		String strSeconds = String.valueOf(Seconds);
		
        if (strMinutes.length() < 2)
        {
        	strMinutes = "0" + strMinutes;  
        } 
        
        if (strSeconds.length() < 2) 
        {  
        	strSeconds = "0" + strSeconds;  
        }
        
        return strMinutes + ":" + strSeconds.substring(0, 2);  
    }  
  
    
    /**
	 * 
	 * @param context
	 * @param song_id
	 * @param album_id
	 * @param allowdefalut
	 * @param small
	 * @return
	 */
	public static Bitmap getArtwork(Context context, long ulMusicId, long ulAlbumId, boolean bDefalut, boolean bSmall)
	{
		if(ulAlbumId < 0) 
		{
			if(ulMusicId < 0)  			
			{
				Bitmap bitmap = getMusicAlbumBitmapFromFile(context, -1, -1);
				if(bitmap != null) 
				{
					return bitmap;
				}
			}
			if(bDefalut) 
			{
				return getDefaultMusicAlbumBitmap(context, bSmall);
			}
			return null;
		}
		
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(mAlbumArtUri, ulAlbumId);
		
		if(uri != null) 
		{
			InputStream in = null;
			
			try 
			{
				in = res.openInputStream(uri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				// 先制定原始大小
				options.inSampleSize = 1;
				// 只进行大小判断
				options.inJustDecodeBounds = true;
				// 调用此方法得到options得到图片的大小
				BitmapFactory.decodeStream(in, null, options);
				/** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例 **/
				/** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/
				if(bSmall)
				{
					options.inSampleSize = computeSampleSize(options, 80);
				} 
				else
				{
					options.inSampleSize = computeSampleSize(options, 600);
				}
				// 我们得到了缩放比例，现在开始正式读入Bitmap数据
				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, options);
			} 
			catch (FileNotFoundException e) 
			{
				Bitmap bitmap = getMusicAlbumBitmapFromFile(context, ulMusicId, ulAlbumId);
				if(bitmap != null) {
					if(bitmap.getConfig() == null) 
					{
						bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
						if(bitmap == null && bDefalut) 
						{
							return getDefaultMusicAlbumBitmap(context, bSmall);
						}
					}
				} 
				else if(bDefalut) 
				{
					bitmap = getDefaultMusicAlbumBitmap(context, bSmall);
				}
				return bitmap;
			} 
			finally 
			{
				try 
				{
					if(in != null) 
					{
						in.close();
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	 /**
	 * 对图片进行合适的缩放
	 * @param options
	 * @param target
	 * @return
	 */
	public static int computeSampleSize(Options options, int target)
	{
		int width = options.outWidth;
		int height = options.outHeight;
		int candidateWidth = width / target;
		int candidateHeight = height / target;
		int candidate = Math.max(candidateWidth, candidateHeight);
		if(candidate == 0) 
		{
			return 1;
		}
		
		if(candidate > 1) 
		{
			if((width > target) && (width / candidate) < target) 
			{
				candidate -= 1;
			}
		}
		if(candidate > 1) 
		{
			if((height > target) && (height / candidate) < target) 
			{
				candidate -= 1;
			}
		}
		return candidate;
	}
	
	
	/**
	 * 获得默认位图
	 * @param context
	 * @return
	 */
	public static Bitmap getDefaultMusicAlbumBitmap(Context context, boolean bSmall) 
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		if(bSmall)
		{	//返回小图片
			return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.music4), null, opts);
		}
		return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.defaultalbum), null, opts);
	}


	/**
	 * 
	 * @param context
	 * @param ulMusicId
	 * @param ulAlbumId
	 * @return
	 */
	public static Bitmap getMusicAlbumBitmapFromFile(Context context, long ulMusicId, long ulAlbumId)
	{
		Bitmap bitmap = null;
		if(ulMusicId < 0 && ulAlbumId < 0) 
		{
			throw new IllegalArgumentException("Must be an album or a song!");
		}
		try 
		{
			BitmapFactory.Options options = new BitmapFactory.Options();
			FileDescriptor fd = null;
			Uri uri = null;
			if(ulAlbumId < 0)
			{
				uri = Uri.parse("content://media/external/audio/media/"
						+ ulMusicId + "/albumart");
			} 
			else 
			{
				uri = ContentUris.withAppendedId(mAlbumArtUri, ulAlbumId);
			}
			
			ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
			if(pfd != null) 
			{
				fd = pfd.getFileDescriptor();
			}
			
			options.inSampleSize = 1;
			// 只进行大小判断
			options.inJustDecodeBounds = true;
			// 调用此方法得到options得到图片大小
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// 我们的目标是在800pixel的画面上显示
			// 所以需要调用computeSampleSize得到图片缩放的比例
			options.inSampleSize = 100;
			// 我们得到了缩放的比例，现在开始正式读入Bitmap数据
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			//根据options参数，减少所需要的内存
			bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		return bitmap;
	}
}
