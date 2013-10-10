package com.dingj.chatjar.content;


public class FileInfo 
{
	private String groupName;
	
	public FileInfo() 
	{
		super();
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	private String fileNo;
	private String fileName;
	private String fileSize;
	private String modifyTime;
	private String fileProperty;

	public String getFileNo()
	{
		return fileNo;
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getFileSize()
	{
		return fileSize;
	}

	public String getModifyTime()
	{
		return modifyTime;
	}

	public String getFileProperty()
	{
		return fileProperty;
	}

	public void setFileNo(String fileNo)
	{
		this.fileNo = fileNo;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public void setFileSize(String fileSize)
	{
		this.fileSize = fileSize;
	}

	public void setModifyTime(String modifyTime)
	{
		this.modifyTime = modifyTime;
	}

	public void setFileProperty(String fileProperty)
	{
		this.fileProperty = fileProperty;
	}
	
	
}