package com.example.mediaplayer3;

import java.io.Serializable;

/**
 * ������Ϣ��
 * @author Jiawei
 *
 */
public class MusicInfo implements Serializable
{

	private long ulId;   			// ����Id  
    
	private String strMusicTitle;	// ���ֱ���  
        
	private String strArtist;		// ������  
        
	private String strAlbum;		// ר����
	
	private long ulAlbumId;			// ר��Id
	
	private String strDisplayName;	// �ļ���
	
	private long ulTime;			// ʱ��  
        
	private long ulFileSize;  		// �ļ���С  
        
	private String strFileFullPath; 			// �ļ�·��
	
	
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
