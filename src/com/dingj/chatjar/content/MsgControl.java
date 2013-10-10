package com.dingj.chatjar.content;
import java.util.ArrayList;
import java.util.List;

public class MsgControl 
{
	public List<Observer> observers = new ArrayList();
	public MsgControl() 
	{
		super();
	}
	
	public void attach(Observer observer)
	{
		if(!observers.contains(observer))
			observers.add(observer);
	}
	
	public void detach(Observer observer)
	{
		if(observers.contains(observer))
			observers.remove(observer);
	}
	
	public Observer getObserver()//获取最后一个
	{
		if(observers.size() > 0)
		return observers.get(observers.size()-1);
		else
			return null;
	}
}
