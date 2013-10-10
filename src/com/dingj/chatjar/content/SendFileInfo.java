package com.dingj.chatjar.content;
public class SendFileInfo
{
	private String fileNo;
	private String fileName;
	private String filePath;
	private DataPacket dataPacker;
	private String ip;
	private long sendSize = 0;
	private long fileSize = 0;
	public boolean isSend = false;
	public boolean isStop = true;
	public boolean isDir = false;
	public boolean isBreakTransport = false;
	private String property;
	
	public boolean isDirStop = false;
	public SendFileInfo() 
	{
		super();
	}
	public void setProperty(String per)
	{
		property = per;
	}
	
	
	public boolean isBreakTransport() {
		return isBreakTransport;
	}
	public void setBreakTransport(boolean isBreakTransport) {
		this.isBreakTransport = isBreakTransport;
	}
	public String getProperty()
	{
		return property;
	}
	public String getFileNo() {
		return fileNo;
	}
	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath()
	{
		return this.filePath;
	}
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	public long getSendSize() {
		return sendSize;
	}
	public void setSendSize(long sendSize) {
		this.sendSize += sendSize;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public DataPacket getDataPacker() {
		return dataPacker;
	}
	public void setDataPacker(DataPacket dataPacker) {
		this.dataPacker = dataPacker;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setSend(boolean b)
	{
		isSend = b;
	}
}
