package com.dingj.chatjar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.dingj.chatjar.content.DataPacket;
import com.dingj.chatjar.content.SendFileInfo;
import com.dingj.chatjar.content.SingleUser;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import jding.debug.JDingDebug;

/**
 * 处理一些发送 
 * @author 丁俊
 *
 */
public class SendUtil
{
	private static final String TAG = "NetUtil";
	private static boolean DEBUG = false;
	private static int length = 0;
	/**
	 * 判断端口是否被占用
	 * 
	 * @return
	 */
	public static boolean checkPort()
	{
		try
		{
			new DatagramSocket(IpMsgConstant.IPMSG_DEFAULT_PORT).close();
			return true;
		} catch (SocketException ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 发送UDP数据包
	 * 
	 * @param dataPacket
	 *            封装的数据包
	 * @param targetIp
	 *            目标IP
	 */
	public static void sendUdpPacket(DataPacket dataPacket, String targetIp)
	{
		try
		{
			byte[] dataBit = dataPacket.toString().getBytes(
					SystemVar.DEFAULT_CHARACT);
			DatagramPacket sendPacket = new DatagramPacket(dataBit,
					dataBit.length, InetAddress.getByName(targetIp),
					IpMsgConstant.IPMSG_DEFAULT_PORT);
			if (DEBUG)
			{
				JDingDebug.printfD(TAG, "dataBit: " + dataPacket.toString());
			}
			SocketManage.getInstance().getUdpSocket().send(sendPacket);
		} catch (UnsupportedEncodingException ex)
		{
		} catch (IOException ex)
		{
		}
	}
	
	 /**
	 * 发送局域网内广播
	 * @param dataPacket
	 */
	public static void broadcastUdpPacket(DataPacket dataPacket)
	{
		sendUdpPacket(dataPacket, "255.255.255.255");
	}

	public static void broadcastUdpPacketToEvery(DataPacket dataPacket, int ip)
	{
		for (int i = 0; i < 255; i++)
		{
			String tempIp = Util.getIp3(ip) + i;
			sendUdpPacket(dataPacket, tempIp);
		}
	}
	
	/**
	 * 发送普通聊天消息给指定的ip
	 * @param msg
	 * @param ip
	 */
	public static void sendMessage(String msg,String ip)
	{
		DataPacket tmpPacket = new DataPacket(IpMsgConstant.IPMSG_SENDMSG);
		tmpPacket.setAdditional(msg);
		tmpPacket.setIp(ip);
		SendUtil.sendUdpPacket(tmpPacket, tmpPacket.getIp());
	}
	
	public static String getSB(String path,DataPacket data)
    {
    	 File file = null;
    	 StringBuffer sb = new StringBuffer();
    	 String hex = Long.toHexString(Long.valueOf(data.getPacketNo()).longValue());
    	 length = 0;
    	 if (path != null)
 		{
 			byte[] d = new byte[]
 			{ 0x00, 0x30 };
 			sb.append(new String(d));
 			sb.append(":");
 			file = new File(path);
 			if(file.isDirectory())
 			{
 				try {
					getLenght(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
 			}
 			FileInputStream fileInput = null;
 			try
 			{	
 				sb.append(file.getName());
 				sb.append(":");
 				if(file.isDirectory())
 				{
 					sb.append(Integer.toHexString(length));
 				}
 				else
 				{
 					fileInput = new FileInputStream(file);
 					sb.append(Integer.toHexString(fileInput.available()));//fileInput.available()
 				}
 				sb.append(":");
 				sb.append(hex);
 				sb.append(":");
 				if(file.isDirectory())
 				{
 					sb.append("2");
 				}
 				else
 				{
 					sb.append("1");
 				}
 				sb.append(":");
 				sb.append(new String(new byte[]
 				{ 0x07 ,0x00}));
 			} catch (FileNotFoundException e)
 			{
 				e.printStackTrace();
 			} catch (IOException e)
 			{
 				e.printStackTrace();
 			} finally
 			{
 				if (fileInput != null)
 				{
 					try
 					{
 						fileInput.close();
 					} catch (IOException e)
 					{
 						e.printStackTrace();
 					}
 				}
 			}
 		}
    	 JDingDebug.printfSystem("sb:" + sb.toString());
    	
    	 return sb.toString();
    }
	
	private static void getLenght(File file) throws IOException 
	{
		JDingDebug.printfSystem("fileName: " + file.getName());
		File files[] = file.listFiles();
		for(int i=0;i<files.length;i++)
		{
			File tempFile = files[i];
			if(tempFile.isDirectory())
			{
				getLenght(tempFile);
			}
			else
			{
				FileInputStream fileInput = null;
				try {
					fileInput = new FileInputStream(tempFile);
					length += fileInput.available();
					fileInput.close();
				} catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void sendFiles(String ip,String path,long t)
    {
    	 DataPacket data=new DataPacket(IpMsgConstant.IPMSG_SENDMSG | IpMsgConstant.IPMSG_SENDCHECKOPT | IpMsgConstant.IPMSG_FILEATTACHOPT,t);//3146272
    	 File file = new File(path);
	     data.setIp(ip);
	     data.setAdditional(getSB(path,data));
	     SendFileInfo sendFileInfo = new SendFileInfo();
	     sendFileInfo.setFileNo(Long.toHexString(Long.parseLong(data.getPacketNo())));
	     sendFileInfo.setFilePath(file.getPath());
	     sendFileInfo.setSend(true);
	     sendFileInfo.setIp(ip);
	     sendFileInfo.setFileName(file.getName());
	     sendFileInfo.isStop = false;
	     if(file.isDirectory())
	    	 sendFileInfo.isDir = true;
	     sendFileInfo.setFileSize(length);
	     length = 0;
//	     SystemVar.TRANSPORT_FILE_LIST.add(sendFileInfo);
	     SingleUser singleUser = Util.getUserWithIp(ip, UserInfo.getInstance());
	     singleUser.addSendFile(sendFileInfo);
	     sendUdpPacket(data, data.getIp());
    }
	
	public static void sendFile(String ip,String path,long unique)
    {
	     DataPacket data=new DataPacket(IpMsgConstant.IPMSG_SENDMSG | IpMsgConstant.IPMSG_SENDCHECKOPT | IpMsgConstant.IPMSG_FILEATTACHOPT,0);
	     File file = new File(path);
	     data.setIp(ip);
	     data.setAdditional(getSB(path,data));
	     SendFileInfo sendFileInfo = new SendFileInfo();
	     sendFileInfo.setFileNo(Long.toHexString(Long.parseLong(data.getPacketNo())));
	     sendFileInfo.setFilePath(file.getPath());
	     sendFileInfo.setSend(true);
	     sendFileInfo.setIp(ip);
	     sendFileInfo.setUniqueTime(unique);
	     sendFileInfo.setFileName(file.getName());
	     sendFileInfo.setTransState(SendFileInfo.TRANSSTATE_NOT_START);
	     sendFileInfo.isStop = false;
	     if(file.isDirectory())
	    	 sendFileInfo.isDir = true;
//	     SystemVar.TRANSPORT_FILE_LIST.add(sendFileInfo);
	     SingleUser singleUser = Util.getUserWithIp(ip, UserInfo.getInstance());
	     singleUser.addSendFile(sendFileInfo);
	     sendUdpPacket(data, data.getIp());
    }
}
