package com.dingj.chatjar.util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jding.debug.JDingDebug;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Time;

import com.dingj.chat.R;
import com.dingj.chatjar.content.FileProgressBar;
import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.Observer;
import com.dingj.chatjar.content.SendFileInfo;
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
	/**接收文件*/
	public final static int HANDLER_RECV_FILE = 2;
	/**发送消息类型*/
	public final static int IPM_MOD_SEND = 0;
	/**接收消息类型*/
	public final static int IPM_MOD_RECV = 1;
	/**接收文件消息*/
	public final static int IPM_MOD_SEND_FILE = 2;
	/**发送文件消息*/
	public final static int IPM_MOD_RECV_FILE = 3;
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
	
	/**
	 * 组装一个消息
	 * @param sender
	 * @param msg
	 * @param ip
	 * @return
	 */
	public static IpmMessage newMessage(String sender,String msg,String ip)
	{
		IpmMessage ipmMessage = new IpmMessage();
		ipmMessage.setIp(ip);
		ipmMessage.setText(msg);
		ipmMessage.setName(sender);
		ipmMessage.setTime(getTime());
		ipmMessage.setMod(Util.IPM_MOD_SEND);
		return ipmMessage;
	}
	
	/**
	 * 从指定账户中查找出对应文件标识的文件类
	 * @param singleUser
	 * @param unique
	 * @return
	 */
	public static SendFileInfo getSendFileInfoFromUnique(SingleUser singleUser,long unique)
	{
		int length = singleUser.getRecvList().size();
		for(int i = 0;i < length; i++)
		{
			long time = singleUser.getRecvList().get(i).getUniqueTime();
			if(time == unique)
			{
				return singleUser.getRecvList().get(i);
			}
		}
		return null;
	}
}
