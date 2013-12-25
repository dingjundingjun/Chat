package com.dingj.chatjar.util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Time;

import com.dingj.chatjar.content.Observer;
import com.dingj.chatjar.content.SingleUser;

/**
 * 工具类
 * @author dingj
 *
 */
public class Util
{
	/** 用户上线 */
	public final static int HANDLER_ADD_USER = 0;
	/**新消息到来*/
	public final static int HANDLER_NEW_MSG = 1;
	
	public static void sendFile(String ip,String path,long t)
	{
			File file = new File(path);
			if(file.isDirectory())
				IpMsgService.sendFiles(ip,path,t);
			else
				IpMsgService.sendFile(ip,path,t);
	}
   
   /**
    * 获取当前系统时间
 * @return 
 */
public static String getTime()
	{
		Time time = new Time();
		time.setToNow();
		int hour = time.hour;
		int minute = time.minute;
		int year = time.year;
		int month = time.month + 1;
		int day = time.monthDay;
		String timetemp;
		if(minute < 10)
			timetemp = year + "-" + month + "-" + day + " " + hour + ":0" + minute;
		else
			timetemp = year + "-" + month + "-" + day + " " + hour + ":" + minute;
		return timetemp;
	}
   
   public static void sendStopRecvFile(String fileNO,String ip)
   {
	   IpMsgService.sendStopRecvFile(fileNO,ip);
   }
	
   public static void sendStopSendFile(String fileNO,String ip)
   {
	   String no = "" + Integer.parseInt(fileNO, 16);
	   IpMsgService.sendStopSendFile(no,ip);
   }
   
	/**
	 * 根据IP地址，从用户列表中找出对应的用户
	 * @param ip IP地址
	 * @param userList 用户信息类
	 * @return 单个用户信息
	 */
	public static SingleUser getUserWithIp(String ip, UserInfo userList)
	{
		SingleUser mUserVo = null;
		for (int i = 0; i < userList.getAllUsers().size(); i++) // 根据IP从已经在线的用户列表中找出发送消息过来的那个用户
		{
			SingleUser usersVo = userList.getAllUsers().get(i);
			if (usersVo.getIp().equals(ip))
			{
				mUserVo = usersVo;
				break;
			}
		}
		return mUserVo;
	}
	
	/**
	 * IP地址转换
	 * 
	 * @param i
	 * @return
	 */
	public static String intToIp(int i)
	{
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}
	
	public static String getIp3(int i)
	{
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ ".";
	}
	
	public static boolean isWifeOpen(Context context)
	{
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.isWifiEnabled();
	}
	
	/**
	 * 获得本机ip
	 * 
	 * @return 本机ip
	 */
	public static String getLocalHostIp()
	{
		try
		{
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException ex)
		{
			return null;
		}
	}
}
