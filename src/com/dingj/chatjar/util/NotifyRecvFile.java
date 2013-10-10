package com.dingj.chatjar.util;

import jding.debug.JDingDebug;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotifyRecvFile extends Notify
{
	private Context mContext;
	public NotifyRecvFile(Context context) 
	{
		super(context);
		mContext = context;
	}

	@Override
	public void productNotify(String ip) 
	{
//		int id = 0;
//		for(int i=0;i<SystemVar.getAllUsers().size();i++)
//		{
//			 if(SystemVar.getAllUsers().get(i).getIp().equals(ip))
//			 {
//				 id = i;
//				 break;
//			 }
//		}
//		NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE); 
//		Notification notification = new Notification(R.drawable.ic_launcher,mContext.getString(R.string.hasnewmessage),System.currentTimeMillis());
//		Intent intent = new Intent(mContext,ProcessActivity.class);
//		intent.setAction(SystemVar.CCMSG_ACTION_RECVFILE);
//		intent.putExtra("id", id);
//		notification.setLatestEventInfo(mContext, "接收文件", "新文件要接收", PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
//		notification.flags|=Notification.FLAG_AUTO_CANCEL; 
//		notification.defaults |= Notification.DEFAULT_SOUND; 
//		manager.notify(1, notification); 
	}

	@Override
	public void processNotify() 
	{
		
	}
	
}
