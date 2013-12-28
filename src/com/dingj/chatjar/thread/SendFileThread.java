package com.dingj.chatjar.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.StringTokenizer;

import com.dingj.chatjar.content.SendFileInfo;
import com.dingj.chatjar.util.SystemVar;


import jding.debug.JDingDebug;

public class SendFileThread extends Thread
{
	private Socket mSocket;
	private String property;
	private SendFileInfo sendFileInfo;
	InputStream is = null;
	OutputStream os = null;
	byte[] but = new byte[524288];
	public SendFileThread(Socket mSocket) 
	{
		super();
		this.mSocket = mSocket;
	}

	@Override
	public void run() 
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			is = mSocket.getInputStream();
			os = mSocket.getOutputStream();

			int length = -1;
			while ((length = is.read(but)) != -1)
			{
				sb.append(new String(but, 0, length, "GBK"));
				if (sb.toString().indexOf(":0:") != -1)
				{
					break;
				}
			}
			if(sb.equals(""))
				return;
			String str = sb.toString();
			JDingDebug.printfSystem("发送文件str:" + str);
			StringTokenizer tokenizer = new StringTokenizer(str, ":", false);
			tokenizer.nextToken();
			tokenizer.nextToken();
			tokenizer.nextToken();
			tokenizer.nextToken();
			String command = tokenizer.nextToken();
			String no = tokenizer.nextToken();
			no = no.toLowerCase();
			for(int i=0;i<SystemVar.TRANSPORT_FILE_LIST.size();i++)
			{
				String fileNo = SystemVar.TRANSPORT_FILE_LIST.get(i).getFileNo();
				if (fileNo.equals(no))
				{
					sendFileInfo = SystemVar.TRANSPORT_FILE_LIST.get(i);
					break;
				}
			}
			if(sendFileInfo.isDir == false)
			{
				String path = sendFileInfo.getFilePath();
				File file = new File(path);
				sendFileInfo.setFileSize(file.length());
				JDingDebug.printfSystem("sendFileInfo:" + file.length());
				InputStream inputStream = new FileInputStream(file);
				length = -1;
				while ((length = inputStream.read(but)) != -1)
				{
					if(sendFileInfo.isStop == true)
					{
//						SystemVar.sendStopSendFile(sendFileInfo.getFileNo(),sendFileInfo.getIp());
						break;
					}
					os.write(but, 0, length);
					sendFileInfo.setSendSize(length);
				}
				os.flush();
			}
			else	//发送的是文件夹
			{
				sendFileInfo.setProperty(sendFileInfo.getFileNo());
				String path = sendFileInfo.getFilePath();
				property = sendFileInfo.getProperty();
				File parentFile = new File(path);
				os.write(getSB("2",parentFile).toString().getBytes(SystemVar.DEFAULT_CHARACT));
				os.flush();
				getFiles(new File(path),sendFileInfo.getIp());
				os.write(getSB("3",parentFile).toString().getBytes(SystemVar.DEFAULT_CHARACT));
				os.flush();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (is != null)
				{
					is.close();
				}
				if (mSocket != null)
				{
					mSocket.close();
				}

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void getFiles(File dir,String ip) throws UnknownHostException, IOException
	{
		if(sendFileInfo.isStop == true)
			return;
		File[] files = dir.listFiles();
		for(int k=0;k<files.length;k++)
		{
			File childFile = files[k];
			if(childFile.isDirectory())
			{
				os.write(getSB("2",childFile).toString().getBytes(SystemVar.DEFAULT_CHARACT));
				os.flush();
				getFiles(childFile,ip);
				os.write(getSB("3",childFile).toString().getBytes(SystemVar.DEFAULT_CHARACT));
				os.flush();
			}
			else
			{
				JDingDebug.printfSystem("发送文件：" + childFile.getPath());
				os.write(getSB("1",childFile).toString().getBytes(SystemVar.DEFAULT_CHARACT));
				os.flush();
				
				//发送文件
				InputStream inputStream = new FileInputStream(childFile);
				int length = -1;
				
				while ((length = inputStream.read(but)) != -1)
				{
					if(sendFileInfo.isStop == true)
					{
						break;
					}
					sendFileInfo.setSendSize(length);
					os.write(but, 0, length);
				}
				os.flush();
				if(inputStream != null)
					inputStream.close();
			}
		}
	}
	
	public String getSB(String fileTemp,File file)
	{
		StringBuffer parentsb = new StringBuffer();
		parentsb.append(":");
		if(fileTemp.equals("3"))
		{
			parentsb.append(".");
		}
		else
			parentsb.append(file.getName());
		parentsb.append(":");
		if(fileTemp.equals("1"))
		{
			parentsb.append(Long.toString(file.length(),16));
		}
		else
			parentsb.append("000000000");
		parentsb.append(":");
		parentsb.append(fileTemp);
		parentsb.append(":");
		parentsb.append("14=" + property);
		parentsb.append(":");
		parentsb.append("16=" + property);
		parentsb.append(":");
		byte[] b = null;
		try {
			b = parentsb.toString().getBytes(SystemVar.DEFAULT_CHARACT);
		} catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		long len  = b.length + 4;
		Long.toString(len, 16);
		parentsb.insert(0, "00" + Long.toString(len, 16));
		JDingDebug.printfSystem("sb:" + parentsb);
		return parentsb.toString();
	}
}
