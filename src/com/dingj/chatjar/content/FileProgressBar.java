package com.dingj.chatjar.content;

import java.io.File;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.dingj.chatjar.content.ReciveFile.ProgressListener;
import com.dingj.chatjar.util.SystemVar;

public class FileProgressBar extends ProgressBar implements ProgressListener
{
	/**用于接收文件*/
	public static int FILE_MOD_RECV = 1;
	/**用于发送文件*/
	public static int FILE_MOD_SEND = 2;
	public int mod = -1;
	/**传输的文件*/
	private SendFileInfo mSendFileInfo;
	/**更新UI*/
	private final int HANDLER_UPDATE_UI = 0;
	private UIHandler mUIHandler = new UIHandler();
	private UIListener mUIListener;
	public FileProgressBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public void setMod(int m)
	{
		this.mod = m;
	}
	
	public int getMod()
	{
		return mod;
	}

	public void setSendFile(SendFileInfo sendFileInfo)
	{
		this.mSendFileInfo = sendFileInfo;
	}
	
	public void recvFile()
	{
		File file = new File(SystemVar.DEFAULT_FILE_PATH);
		if(!file.exists())
		{
			file.mkdirs();
		}
		ReciveFile reciveFile = new ReciveFile();
		reciveFile.setListener(this);
    	reciveFile.recv(SystemVar.DEFAULT_FILE_PATH + mSendFileInfo.getFileName(),mSendFileInfo);
	}

	@Override
	public void setRecvProgress(int progress)
	{
		Message msg = mUIHandler.obtainMessage();
		msg.what = HANDLER_UPDATE_UI;
		msg.arg1 = progress;
		mUIHandler.sendMessage(msg);
	}
	
	public class UIHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
				case HANDLER_UPDATE_UI:
				{
					setProgress(msg.arg1);
					invalidate();
					if(msg.arg1 == 100)
					{
						mUIListener.notifyFinish();
					}
					break;
				}
			}
		}
	}
	
	public void setListener(UIListener ul)
	{
		mUIListener = ul;
	}
	
	public interface UIListener
	{
		/**通知接收文件完毕*/
		public void notifyFinish();
	}
	
}
