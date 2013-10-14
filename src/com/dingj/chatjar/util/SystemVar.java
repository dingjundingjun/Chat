package com.dingj.chatjar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.Semaphore;

import com.dingj.chatjar.content.MsgControl;
import com.dingj.chatjar.content.SendFileInfo;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.db.MsgDatabase;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.format.Time;
import jding.debug.JDingDebug;

/**
*
* 系统环境变量 和一些工具类
* 
*/
public class SystemVar 
{

   //信号量
   public static Semaphore PACKET_QUEUE_FULL=new Semaphore(0);
   public static Semaphore PACKET_QUEUE_EMPTY=new Semaphore(100);    //队列最大容量

   //运行时可变变量
   public static String USER_NAME = null;                 //显示的用户名称
   public static String HOST_NAME = null;                 //显示的主机名称

   public static List<SingleUser> USER_LIST = new ArrayList();                  //用户列表
   public static List<SendFileInfo> TRANSPORT_FILE_LIST = new ArrayList();			//接收文件列表

   public static List<List<SingleUser>> gAllName = new ArrayList<List<SingleUser>>();
  //运行时不变参数
   public static String USER_HOME;                       //用户工作路径
   public static String DEFAULT_CHARACT;                 //默认编码
   public static String LINE_SEPARATOR;                  //换行标识
   public static String FILE_SEPARATOR;                  //文件分割标识
   public static char OS;                                //操作系统
   public static MsgDatabase db;
   public static MsgControl gCCMsgControl = new MsgControl();	//控制界面通知
   //发送还是接收文件
   public static String CCMSG_ACTION_SENDFILE = "ccmsg.action.sendfile";
   public static String CCMSG_ACTION_RECVFILE = "ccmsg.action.recvfile";
   //广播
   public static String CCMSG_BROADCAST_ADDUSER = "action.ccmsg.broadcast.adduser";	//增加用户
   public static String CCMSG_BROADCAST_NEWMESSAGE = "action.ccmsg.broadcast.newmessage";//新的消息
   public static String CCMSG_BROADCAST_RECVFILE = "action.ccmsg.broadcast.recvfile";
   //保存文件的默认目录
   public static String DEFAULT_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/ipmsg/";
   public static String WIFIIP;
   
   /**
    * 系统参数初始化
    */
   public static void init()
   {
	   USER_NAME=System.getProperty("user.name");
	   USER_NAME = "dingj";
       USER_HOME = "dingjun";
       LINE_SEPARATOR=System.getProperty("line.separator");
       FILE_SEPARATOR=System.getProperty("file.separator");
       OS=IpMsgConstant.OS_OTHER;
//       try {
           HOST_NAME = "DJ_HOST";	//获取用户名
//       } catch (UnknownHostException ex) {
//           HOST_NAME="";
//       }
       DEFAULT_CHARACT="GB2312";
       JDingDebug.printfSystem("name:" + USER_NAME + " home:" + USER_HOME + " hostname:" + HOST_NAME);
//       USER_LIST = new ArrayList<UsersVo>();
//       SEND_FILE_LIST = new ArrayList<SendFileInfo>();
   }
   
   /**
    * 向在线用户集合中添加用户
    * @param user 用户对象
    * @return true 添加成功 添加失败
    *//*
	public static synchronized boolean addUsers(UsersVo user)
	{
		if (user.getIp().equals("127.0.0.1")
				|| user.getIp().equals(SystemVar.WIFIIP))
			return false;
		JDingDebug.printfSystem("use:" + user.getIp() + " local:"
				+ SystemVar.WIFIIP);
		for (int i = 0; i < USER_LIST.size(); i++)
		{
			if (USER_LIST.get(i).getIp().equals(user.getIp()))
			{
				USER_LIST.set(i, user);
				return false;
			}
		}
		USER_LIST.add(user);
		return true;
	}
   
   public static synchronized List<UsersVo> getAllUsers()
   {
	   return USER_LIST;
   }
   */
  /* public static void sendFile(String ip,String path,long t)
	{
			File file = new File(path);
			if(file.isDirectory())
				IpMsgService.sendFiles(ip,path,t);
			else
				IpMsgService.sendFile(ip,path,t);
	}
   
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
			timetemp = year + "年" + month + "月" + day + "日 " + hour + ":0" + minute;
		else
			timetemp = year + "年" + month + "月" + day + "日 " + hour + ":" + minute;
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
   
	*//**
	 * 根据IP地址，从用户列表中找出对应的用户
	 * @param ip IP地址
	 * @param userList 用户信息类
	 * @return 单个用户信息
	 *//*
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
	}*/
}    




