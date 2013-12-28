package com.dingj.chatjar.content;

import java.util.List;

import jding.debug.JDingDebug;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.dingj.chat.R;
import com.dingj.chatjar.content.FileProgressBar.UIListener;
import com.dingj.chatjar.util.Util;

public class MessageItem implements UIListener
{
	private boolean DEBUG = true;
	private final String TAG = "MessageItem";
	public Context mContext;
	public List<IpmMessage> mMessageList;
	public View mMainView;
	public SingleUser mSingleUser;
	private Button mRectBtn;
	private FileProgressBar mFileProgressBar;
	private LinearLayout mProgressLayout;
	private LinearLayout mProgressControl;
	private TextView mTextTip;
	private SendFileInfo mSendFileInfo;
	public void init(Context c,List<IpmMessage> messageList,int position,SingleUser singleUser,View mainView)
	{
		mMainView = mainView;
		mContext = c;
		mMessageList = messageList;
		mSingleUser = singleUser;
		
		LinearLayout msgLayout = (LinearLayout) mMainView.findViewById(R.id.msg_layout);
		TextView textMsg = (TextView) mMainView.findViewById(R.id.msg);
		TextView textTime = (TextView) mMainView.findViewById(R.id.time);
		IpmMessage temMsg = mMessageList.get(position);
		textMsg.setText(temMsg.getText());
		textTime.setText(temMsg.getTime());
		LinearLayout.LayoutParams param = (LayoutParams) msgLayout.getLayoutParams();
		mProgressLayout = (LinearLayout) mMainView.findViewById(R.id.progress_main);
		mProgressControl = (LinearLayout)mMainView.findViewById(R.id.progress_control);
		mTextTip = (TextView)mMainView.findViewById(R.id.tip);
		
		switch(temMsg.getMod())
		{
			case Util.IPM_MOD_RECV:
			{
				param.gravity = Gravity.LEFT;
				msgLayout.setBackgroundResource(R.drawable.msg_item_from_bg);
				mProgressLayout.setVisibility(View.GONE);
				break;
			}
			case Util.IPM_MOD_SEND:
			{
				msgLayout.setBackgroundResource(R.drawable.msg_item_to_bg);
				param.gravity = Gravity.RIGHT;
				mProgressLayout.setVisibility(View.GONE);
				break;
			}
			case Util.IPM_MOD_RECV_FILE:
			{
				mRectBtn = (Button)mMainView.findViewById(R.id.progress_btn);
				mFileProgressBar = (FileProgressBar)mMainView.findViewById(R.id.progressBar);
				mRectBtn.setTag(R.string.time_key,temMsg.getUniqueTime());
				mRectBtn.setText(R.string.recv_msg);
				int length = mSingleUser.getRecvList().size();
				for(int i = 0;i < length; i++)
				{
					long time = mSingleUser.getRecvList().get(i).getUniqueTime();
					if(time == temMsg.getUniqueTime())
					{
						mSendFileInfo = mSingleUser.getRecvList().get(i);
						break;
					}
				}
				if(mSendFileInfo != null)
				{
//					if(!mSendFileInfo.isDir)
//					{
//						int progress = (int) (mSendFileInfo.getSendSize()*100/mSendFileInfo.getFileSize());
//						mFileProgressBar.setProgress(progress);
//						if(progress == 100)
//						{
//							notifyFinish();
//						}
//					}
				}
				mRectBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if(mRectBtn.getText().equals(mContext.getString(R.string.recv_msg)))
						{
							mRectBtn.setText(R.string.btn_cancel);
							if(mSendFileInfo != null)
							{
								mFileProgressBar.setMod(FileProgressBar.FILE_MOD_RECV);
								mFileProgressBar.setSendFile(mContext,mSendFileInfo);
								mFileProgressBar.setListener(MessageItem.this);
								mFileProgressBar.recvFile();
							}
						}
						else
						{
							if(DEBUG)
							{
								JDingDebug.printfD(TAG, "取消传输");
							}
						}
						
					}
				});
				mProgressLayout.setVisibility(View.VISIBLE);
				break;
			}
			case Util.IPM_MOD_SEND_FILE:
			{
				mProgressLayout.setVisibility(View.VISIBLE);
				break;
			}
		}
		if(DEBUG)
		{
			JDingDebug.printfD(TAG, "msg=" + mMessageList.get(position).getText());
			JDingDebug.printfD(TAG, "time=" + mMessageList.get(position).getTime());
		}
	}
	
	public View getMainView()
	{
		return mMainView;
	}

	@Override
	public void notifyFinish()
	{
		mProgressLayout.setVisibility(View.VISIBLE);
		mProgressControl.setVisibility(View.GONE);
		mTextTip.setText("完成传输");
		Toast.makeText(mContext, "文件传输完成", Toast.LENGTH_SHORT).show();
		
	}
}
