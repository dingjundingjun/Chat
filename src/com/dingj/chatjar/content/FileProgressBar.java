package com.dingj.chatjar.content;

import java.io.File;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dingj.chat.R;
import com.dingj.chatjar.content.SendFileInfo.ProgressListener;
import com.dingj.chatjar.util.SystemVar;
import com.dingj.chatjar.util.UserInfo;

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
	/**显示提示*/
	private TextView mTextTip;
	private Context mContext;
	private View mMainView;
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
	
	public void setContext(Context c)
	{
		this.mContext =c;
	}
	
	public void recvFile()
	{
		File file = new File(SystemVar.DEFAULT_FILE_PATH);
		if(!file.exists())
		{
			file.mkdirs();
		}
		if(mSendFileInfo.getTransState() == SendFileInfo.TRANSSTATE_TRANSLATING)
		{
			showTransporting();
			setProgress(mSendFileInfo.getProgress());
			mSendFileInfo.setListener(this);
		}
		else if(mSendFileInfo.getTransState() == SendFileInfo.TRANSSTATE_NOT_START)
		{
			mSendFileInfo.setListener(this);
			showTransporting();
			mSendFileInfo.recv(SystemVar.DEFAULT_FILE_PATH + mSendFileInfo.getFileName(),mSendFileInfo);
		}
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
						showTransportFinish();
					}
					break;
				}
			}
		}
	}
	
	/**
	 * 显示传输完成的界面
	 */
	public void showTransportFinish()
	{
		mTextTip.setVisibility(View.VISIBLE);
		mTextTip.setText(mContext.getText(R.string.recv_finish));
		mMainView.setVisibility(View.GONE);
	}
	
	/**
	 * 显示正在文件传输
	 */
	public void showTransporting()
	{
		mTextTip.setVisibility(View.VISIBLE);
		mTextTip.setText(mContext.getText(R.string.recv_transporting));
		mMainView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 文件还没开始传输
	 */
	public void showTransportNotStart()
	{
		mTextTip.setVisibility(View.GONE);
		mMainView.setVisibility(View.VISIBLE);
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
	
	public void setTipandMainView(TextView tip,View mainView)
	{
		mMainView = mainView;
		mTextTip = tip;
	}
	
	public void showTransportError()
	{
		mTextTip.setVisibility(View.VISIBLE);
		mTextTip.setText(mContext.getText(R.string.recv_error));
		mMainView.setVisibility(View.GONE);
	}
}
