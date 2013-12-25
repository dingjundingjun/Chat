package com.dingj.chatjar.content;
import java.util.ArrayList;
import java.util.List;

import jding.debug.JDingDebug;

public class MsgControl 
{
	private boolean DEBUG = true;
	private String TAG = "MsgControl";
	public List<Observer> observers = new ArrayList();
	public MsgControl() 
	{
		super();
	}
	
	public void attach(Observer observer)
	{
		if(!observers.contains(observer))
		{
			JDingDebug.printfD(TAG, "attach");
			observers.add(observer);
		}
	}
	
	public void detach(Observer observer)
	{
		if(observers.contains(observer))
		{
			JDingDebug.printfD(TAG, "detach");
			observers.remove(observer);
		}
			
	}
	
	public Observer getObserver()//获取最后一个
	{
		JDingDebug.printfD(TAG, "getObserver = " + observers.size());
		if(observers.size() > 0)
		return observers.get(observers.size()-1);
		else
			return null;
	}
}
