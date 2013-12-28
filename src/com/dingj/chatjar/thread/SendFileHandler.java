package com.dingj.chatjar.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import com.dingj.chatjar.content.SendFileInfo;
import com.dingj.chatjar.util.SystemVar;

import jding.debug.JDingDebug;


import android.content.Context;

public class SendFileHandler extends Thread
{
	private ServerSocket mServerSocket = null;
	private boolean stop = false;
	public SendFileHandler(ServerSocket serverSocket) 
	{
		mServerSocket = serverSocket;
	}

	@Override
	public void run()
	{
		while(!stop)
		{
			try 
			{
				Socket socket = mServerSocket.accept();
				SocketAddress  ss = socket.getRemoteSocketAddress();
				JDingDebug.printfSystem("有TCP链接进来:" + ss.toString());
				SendFileThread sendFileThread = new SendFileThread(socket);
				sendFileThread.start();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public long getSendingSize(String ip)
	{
		for(int i=0;i<SystemVar.TRANSPORT_FILE_LIST.size();i++)
		{
			if (SystemVar.TRANSPORT_FILE_LIST.get(i).getIp().equals(ip))
			{
				SendFileInfo sendFileInfo = SystemVar.TRANSPORT_FILE_LIST.get(i);
				return sendFileInfo.getSendSize();
			}
		}
		return 0;
	}
	
	public long getSendFileSize(String ip)
	{
		for(int i=0;i<SystemVar.TRANSPORT_FILE_LIST.size();i++)
		{
			if (SystemVar.TRANSPORT_FILE_LIST.get(i).getIp().equals(ip))
			{
				SendFileInfo sendFileInfo = SystemVar.TRANSPORT_FILE_LIST.get(i);
				return sendFileInfo.getFileSize();
			}
		}
		return 0;
	}
}
