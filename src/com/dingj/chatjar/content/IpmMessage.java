package com.dingj.chatjar.content;

public class IpmMessage
{
	/** 用户IP */
	private String ip = null;
	/** 消息内容 */
	private String text = null;
	/** 用户姓名 */
	private String name = null;
	/** 消息日期 */
	private String time = null;
	/** 文件传输状态 */
	private int mFileTransportState = -1;
	private long unique = 0;
	/** 消息类型（发送，接收） */
	private int mod = -1;

	public IpmMessage()
	{
		super();
	}

	public void setFileTransportState(int state)
	{
		mFileTransportState = state;
	}
	
	public int getFileTransportState()
	{
		return mFileTransportState;
	}
	
	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setName(String senderName)
	{
		this.name = senderName;
	}

	public String getName()
	{
		return name;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getTime()
	{
		return this.time;
	}

	public int getMod()
	{
		return mod;
	}

	public void setMod(int mod)
	{
		this.mod = mod;
	}

	public void setUniqueTime(long time)
	{
		this.unique = time;
	}

	public long getUniqueTime()
	{
		return unique;
	}
}
