package com.dingj.chatjar.content;

public class IpmMessage 
{
	private String ip = null;
	private String text = null;
	private String name = null;
	private String time = null;
	private long unique = 0;
	/**消息类型（发送，接收）*/
	private int mod = -1;
	public IpmMessage() {
		super();
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
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
	public int getMod() {
		return mod;
	}
	public void setMod(int mod) {
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
