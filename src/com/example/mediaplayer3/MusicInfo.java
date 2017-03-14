package com.example.mediaplayer3;

import java.io.Serializable;

/**
 * 音乐信息类
 * @author Jiawei
 *
 */
public class MusicInfo implements Serializable
{

	private long ulId;   			// 音乐Id  
    
	private String strMusicTitle;	// 音乐标题  
        
	private String strArtist;		// 艺术家  
        
	private String strAlbum;		// 专辑名
	
	private long ulAlbumId;			// 专辑Id
	
	private String strDisplayName;	// 文件名
	
	private long ulTime;			// 时长  
        
	private long ulFileSize;  		// 文件大小  
        
	private String strFileFullPath; 			// 文件路径
	
	
	public void setMusicId(long ulId) 
	{
		this.ulId = ulId;
	}
	
	public long getMusicId() 
	{
		return this.ulId;
	}
	
	public void setMusicTitle(String strMusicTitle) 
	{
		this.strMusicTitle = strMusicTitle;
	}
	
	public String getMusicTitle() 
	{
		return this.strMusicTitle;
	}
	
	public void setArtist(String strArtist) 
	{
		this.strArtist = strArtist;
	}
	
	public String getArtist() 
	{
		return this.strArtist;
	}
	
	public void setAlbum(String strAlbum) 
	{
		this.strAlbum = strAlbum;
	}
	
	public String getAlbum() 
	{
		return this.strAlbum;
	}
	
	public void setAlbumId(long ulAlbumId) 
	{
		this.ulAlbumId = ulAlbumId;
	}
	
	public long getAlbumId() 
	{
		return this.ulAlbumId;
	}
	
	public void setDisplayName(String strDisplayName) 
	{
		this.strDisplayName = strDisplayName;
	}
	
	public String getDisplayName() 
	{
		return this.strDisplayName;
	}
		
	public void setTime(long ulTime) 
	{
		this.ulTime = ulTime;
	}
	
	public long getTime() 
	{
		return this.ulTime;
	}
	
	public void setFileSize(long ulFileSize) 
	{
		this.ulFileSize = ulFileSize;
	}
	
	public long getFileSize() 
	{
		return this.ulFileSize;
	}
	
	public void setFileFullPath(String strFileFullPath) 
	{
		this.strFileFullPath = strFileFullPath;
	}
	
	public String getFileFullPath() 
	{
		return this.strFileFullPath;
	}

}
