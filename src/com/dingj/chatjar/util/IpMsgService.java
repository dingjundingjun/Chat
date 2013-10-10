package com.dingj.chatjar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.dingj.chatjar.content.DataPacket;
import com.dingj.chatjar.content.SendFileInfo;

import jding.debug.JDingDebug;

public class IpMsgService 
{
	private static int length = 0;
    public static void sendMessage(String Text,String[] ips)
    {
        Text=Text.trim();
        for(int i=0;i<ips.length;i++)
        {
            DataPacket data=new DataPacket(IpMsgConstant.IPMSG_SENDMSG);
            data.setIp(ips[i]);
            data.setAdditional(Text);
            NetUtil.sendUdpPacket(data, data.getIp());
        }
    }
    
    public static void sendStopRecvFile(String fileNO,String ip)
    {
        DataPacket data=new DataPacket(230);
        data.setIp(ip);
        data.setAdditional(fileNO);
        NetUtil.sendUdpPacket(data, data.getIp());
    }
    
    public static void sendStopSendFile(String fileNO,String ip)
    {
        DataPacket data=new DataPacket(232);
        data.setIp(ip);
        data.setAdditional(fileNO);
        NetUtil.sendUdpPacket(data, data.getIp());
    }
    
    public static void sendFile(String ip,String path,long t)
    {
	     DataPacket data=new DataPacket(IpMsgConstant.IPMSG_SENDMSG | IpMsgConstant.IPMSG_SENDCHECKOPT | IpMsgConstant.IPMSG_FILEATTACHOPT,t);
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
	     SystemVar.TRANSPORT_FILE_LIST.add(sendFileInfo);
	     NetUtil.sendUdpPacket(data, data.getIp());
    }
    
    public static void stopSendFile(String ip,String fileNo)
    {
    	 DataPacket data=new DataPacket(IpMsgConstant.IPMSG_STOPFILE,0);
	     data.setIp(ip);
	     data.setAdditional(fileNo);
	     NetUtil.sendUdpPacket(data, data.getIp());
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
	     SystemVar.TRANSPORT_FILE_LIST.add(sendFileInfo);
	     NetUtil.sendUdpPacket(data, data.getIp());
    }
    
    public static String getSB(String path,DataPacket data)
    {
    	 File file = null;
    	 StringBuffer sb = new StringBuffer();
    	 String hex = Long.toHexString(Long.valueOf(data.getPacketNo()).longValue());
    	 
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
				} catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
