package com.dingj.chat;

import java.util.List;

import jding.debug.JDingDebug;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.dingj.chatjar.content.FileProgressBar;
import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.MessageItem;
import com.dingj.chatjar.content.SendFileInfo;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.util.SendUtil;
import com.dingj.chatjar.util.SystemVar;
import com.dingj.chatjar.util.Util;

public class MessageAdapter extends BaseAdapter
{
	private SingleUser mSingleUser;
	private Context mContext;
	private List<IpmMessage> mMessageList;
	private boolean DEBUG = true;
	private final String TAG = "MessageAdapter";
	public MessageAdapter(Context c)
	{
		this.mContext = c;
	}
	
	@Override
	public int getCount()
	{
		return mMessageList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
//		MessageItem messageItem = new MessageItem();
		if(convertView == null)
		{
			convertView = LinearLayout.inflate(mContext, R.layout.msg_item_layout, null);
		}
		LinearLayout msgLayout = (LinearLayout) convertView.findViewById(R.id.msg_layout);
		TextView textMsg = (TextView) convertView.findViewById(R.id.msg);
		TextView textTime = (TextView) convertView.findViewById(R.id.time);
		IpmMessage temMsg = mMessageList.get(position);
		textMsg.setText(temMsg.getText());
		textTime.setText(temMsg.getTime());
		LinearLayout.LayoutParams param = (LayoutParams) msgLayout.getLayoutParams();
		LinearLayout progressLayout = (LinearLayout) convertView.findViewById(R.id.progress_control);
		TextView mTextIip = (TextView)convertView.findViewById(R.id.tip);
		Button mRecvBtn = (Button)convertView.findViewById(R.id.progress_btn);
		FileProgressBar fileProgressBar = (FileProgressBar)convertView.findViewById(R.id.progressBar);
		switch(temMsg.getMod())
		{
			case Util.IPM_MOD_RECV:
			{
				param.gravity = Gravity.LEFT;
				msgLayout.setBackgroundResource(R.drawable.msg_item_from_bg);
				progressLayout.setVisibility(View.GONE);
				mTextIip.setVisibility(View.GONE);
				break;
			}
			case Util.IPM_MOD_SEND:
			{
				msgLayout.setBackgroundResource(R.drawable.msg_item_to_bg);
				param.gravity = Gravity.RIGHT;
				progressLayout.setVisibility(View.GONE);
				mTextIip.setVisibility(View.GONE);
				break;
			}
			case Util.IPM_MOD_RECV_FILE:
			{
				param.gravity = Gravity.LEFT;
				msgLayout.setBackgroundResource(R.drawable.msg_item_from_bg);
				mRecvBtn.setTag(R.string.time_key,temMsg.getUniqueTime());
				mRecvBtn.setTag(R.string.progress_key,fileProgressBar);
				fileProgressBar.setTipandMainView(mTextIip, progressLayout);
				fileProgressBar.setContext(mContext);
				SendFileInfo sendFileInfo = Util.getSendFileInfoFromUnique(mSingleUser, temMsg.getUniqueTime());
				progressLayout.setVisibility(View.VISIBLE);
				if(DEBUG)
				{
					JDingDebug.printfD(TAG, "sendFileInfo time =" + temMsg.getUniqueTime());
				}
				if(sendFileInfo == null)    //为空说明是掉线再进.
				{
					//需要重数据库里读取状态显示
					int state = SystemVar.db.getFileTransportState(temMsg.getUniqueTime());
					switch(state)
					{
						case SendFileInfo.TRANSSTATE_NOT_START:
						case SendFileInfo.TRANSSTATE_TRANSLATING:
						case SendFileInfo.TRANSSTATE_ERROR:
						{
							fileProgressBar.showTransportError();
							break;
						}
						case SendFileInfo.TRANSSTATE_FINISH:
						{
							fileProgressBar.showTransportFinish();
							break;
						}
					}
					break;
				}
				fileProgressBar.setSendFile(sendFileInfo);
				if(DEBUG)
				{
					JDingDebug.printfD(TAG, "sendFileInfo state =" + sendFileInfo.getTransState());
				}
				switch(sendFileInfo.getTransState())
				{
					case SendFileInfo.TRANSSTATE_NOT_START:
					{
						mRecvBtn.setText(R.string.recv_msg);
						fileProgressBar.showTransportNotStart();
						break;
					}
					case SendFileInfo.TRANSSTATE_TRANSLATING:
					{
						fileProgressBar.recvFile();
						mRecvBtn.setText(R.string.btn_cancel);
						fileProgressBar.showTransporting();
						break;
					}
					case SendFileInfo.TRANSSTATE_ERROR:
					{
						fileProgressBar.showTransportError();
						break;
					}
					case SendFileInfo.TRANSSTATE_FINISH:
					{
						fileProgressBar.showTransportFinish();
						break;
					}
				}
				mRecvBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if(DEBUG)
						{
							JDingDebug.printfD(TAG, "接收文件咯");
						}
						if(((Button)v).getText().toString().equals(mContext.getString(R.string.recv_msg)))    //点击就接收
						{
							((Button)v).setText(mContext.getString(R.string.btn_cancel));
							SendFileInfo sendFileInfo = Util.getSendFileInfoFromUnique(mSingleUser,  (Long)v.getTag(R.string.time_key));
							FileProgressBar tempBar = (FileProgressBar) v.getTag(R.string.progress_key);
							tempBar.setMod(FileProgressBar.FILE_MOD_RECV);
							tempBar.setSendFile(sendFileInfo);
							tempBar.recvFile();
						}
					}
				});

				break;
			}
			case Util.IPM_MOD_SEND_FILE:
			{
				msgLayout.setBackgroundResource(R.drawable.msg_item_to_bg);
				param.gravity = Gravity.RIGHT;
				progressLayout.setVisibility(View.VISIBLE);
				
				mRecvBtn.setTag(R.string.time_key,temMsg.getUniqueTime());
				mRecvBtn.setTag(R.string.progress_key,fileProgressBar);
				mRecvBtn.setText(mContext.getString(R.string.btn_cancel));
				fileProgressBar.setTipandMainView(mTextIip, progressLayout);
				fileProgressBar.setContext(mContext);
				SendFileInfo sendFileInfo = Util.getSendFileInfoFromUnique(mSingleUser, temMsg.getUniqueTime());
				progressLayout.setVisibility(View.VISIBLE);
				if(sendFileInfo == null)
				{
					//需要重数据库里读取状态显示
					int state = SystemVar.db.getFileTransportState(temMsg.getUniqueTime());
					switch(state)
					{
						case SendFileInfo.TRANSSTATE_NOT_START:
						case SendFileInfo.TRANSSTATE_TRANSLATING:
						case SendFileInfo.TRANSSTATE_ERROR:
						{
							fileProgressBar.showTransportError();
							break;
						}
						case SendFileInfo.TRANSSTATE_FINISH:
						{
							fileProgressBar.showTransportFinish();
							break;
						}
					}
					break;
				}
				if(DEBUG)
				{
					JDingDebug.printfD(TAG, "sendFileInfo.getTransState:" + sendFileInfo.getTransState());
				}
				switch(sendFileInfo.getTransState())
				{
					case SendFileInfo.TRANSSTATE_NOT_START:
					case SendFileInfo.TRANSSTATE_TRANSLATING:
					{
						fileProgressBar.setSendFile(sendFileInfo);
						fileProgressBar.sendFile();
						fileProgressBar.showTransporting();
						break;
					}
					case SendFileInfo.TRANSSTATE_FINISH:
					{
						fileProgressBar.showTransportFinish();
						break;
					}
					case SendFileInfo.TRANSSTATE_ERROR:
					{
						fileProgressBar.showTransportError();
						break;
					}
				}
				mRecvBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if(((Button)v).getText().equals(mContext.getString(R.string.btn_cancel)))
						{
							SendFileInfo sendFileInfo = Util.getSendFileInfoFromUnique(mSingleUser,  (Long)v.getTag(R.string.time_key));
							sendFileInfo.setTransState(SendFileInfo.TRANSSTATE_ERROR);
							FileProgressBar tempBar = (FileProgressBar) v.getTag(R.string.progress_key);
							SendUtil.sendStopFileTranslate(sendFileInfo.getIp(),sendFileInfo.getFileNo());
							tempBar.showTransportError();
						}
					
					}
				});
				break;
			}
		}
		if(DEBUG)
		{
			JDingDebug.printfD(TAG, "msg=" + mMessageList.get(position).getText());
			JDingDebug.printfD(TAG, "time=" + mMessageList.get(position).getTime());
			JDingDebug.printfD(TAG, "mod=" + temMsg.getMod());
			
		}
		
//		messageItem.init(mContext, mMessageList, position, mSingleUser);
		return convertView;
	}
	
	/**
	 * 设置用户，读取该用户消息
	 * @param su
	 */
	public void setUser(SingleUser su)
	{
		this.mSingleUser = su;
		SystemVar.db.getMessages(mSingleUser);
		this.mMessageList = mSingleUser.getMessageList();
	}
}
