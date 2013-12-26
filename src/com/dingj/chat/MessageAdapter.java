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
import com.dingj.chatjar.content.SingleUser;
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
		MessageItem messageItem = new MessageItem();
//		if(convertView == null)
//		{
//			convertView = LinearLayout.inflate(mContext, R.layout.msg_item_layout, null);
//		}
//		LinearLayout msgLayout = (LinearLayout) convertView.findViewById(R.id.msg_layout);
//		TextView textMsg = (TextView) convertView.findViewById(R.id.msg);
//		TextView textTime = (TextView) convertView.findViewById(R.id.time);
//		IpmMessage temMsg = mMessageList.get(position);
//		textMsg.setText(temMsg.getText());
//		textTime.setText(temMsg.getTime());
//		LinearLayout.LayoutParams param = (LayoutParams) msgLayout.getLayoutParams();
//		LinearLayout progressLayout = (LinearLayout) convertView.findViewById(R.id.progress);
//		switch(temMsg.getMod())
//		{
//			case Util.IPM_MOD_RECV:
//			{
//				param.gravity = Gravity.LEFT;
//				msgLayout.setBackgroundResource(R.drawable.msg_item_from_bg);
//				progressLayout.setVisibility(View.GONE);
//				break;
//			}
//			case Util.IPM_MOD_SEND:
//			{
//				msgLayout.setBackgroundResource(R.drawable.msg_item_to_bg);
//				param.gravity = Gravity.RIGHT;
//				progressLayout.setVisibility(View.GONE);
//				break;
//			}
//			case Util.IPM_MOD_RECV_FILE:
//			{
//				Button mRectBtn = (Button)convertView.findViewById(R.id.progress_btn);
//				FileProgressBar fileProgressBar = (FileProgressBar)convertView.findViewById(R.id.progressBar);
//				mRectBtn.setTag(R.string.time_key,temMsg.getUniqueTime());
//				mRectBtn.setTag(R.string.progress_key,fileProgressBar);
//				mRectBtn.setText(R.string.recv_msg);
//				mRectBtn.setTag(R.string.recv_msg,"recv");
//				mRectBtn.setOnClickListener(new OnClickListener()
//				{
//					@Override
//					public void onClick(View v)
//					{
//						if(v.getTag(R.string.recv_msg).equals("recv"))
//						{
//							((Button)v).setText(R.string.btn_cancel);
//							v.setTag(R.string.recv_msg,"cancel");
//							int length = mSingleUser.getRecvList().size();
//							for(int i = 0;i < length; i++)
//							{
//								long time = mSingleUser.getRecvList().get(i).getUniqueTime();
//								JDingDebug.printfD(TAG, "onClick ==time:" + time + " " + (Long)v.getTag(R.string.time_key));
//								if(time == (Long)v.getTag(R.string.time_key))
//								{
//									if(DEBUG)
//									{
//										JDingDebug.printfD(TAG, "开始接收文件咯:" + mSingleUser.getRecvList().get(i).getFileName());
//									}
//									FileProgressBar tempBar = (FileProgressBar) v.getTag(R.string.progress_key);
//									tempBar.setMod(FileProgressBar.FILE_MOD_RECV);
//									tempBar.setSendFile(mSingleUser.getRecvList().get(i));
//									tempBar.recvFile();
//									break;
//								}
//							}
//						}
//						else
//						{
//							if(DEBUG)
//							{
//								JDingDebug.printfD(TAG, "取消传输");
//							}
//						}
//						
//					}
//				});
//				progressLayout.setVisibility(View.VISIBLE);
//				break;
//			}
//			case Util.IPM_MOD_SEND_FILE:
//			{
//				progressLayout.setVisibility(View.VISIBLE);
//				break;
//			}
//		}
//		if(DEBUG)
//		{
//			JDingDebug.printfD(TAG, "msg=" + mMessageList.get(position).getText());
//			JDingDebug.printfD(TAG, "time=" + mMessageList.get(position).getTime());
//		}
		
		messageItem.init(mContext, mMessageList, position, mSingleUser);
		return messageItem.getMainView();
	}
	
	public void setUser(SingleUser su)
	{
		this.mSingleUser = su;
		SystemVar.db.getMessages(mSingleUser);
		this.mMessageList = mSingleUser.getMessageList();
	}
}
