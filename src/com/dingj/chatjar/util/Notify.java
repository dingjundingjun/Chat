package com.dingj.chatjar.util;

import android.content.Context;

public abstract class Notify 
{
	private String notifyName;
	private Context mContext;
	public Notify(Context context) 
	{
		super();
		mContext = context;
	}

	public String getNotifyName() 
	{
		return notifyName;
	}

	public void setNotifyName(String notifyName) 
	{
		this.notifyName = notifyName;
	}
	
	public abstract void productNotify(String ip);
	public abstract void processNotify();
	
}

