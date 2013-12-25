package com.dingj.chat;

import java.util.List;

import jding.debug.JDingDebug;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.util.SystemVar;

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
		
		if(convertView == null)
		{
			convertView = LinearLayout.inflate(mContext, R.layout.msg_item_layout, null);
		}
		TextView textMsg = (TextView) convertView.findViewById(R.id.msg);
		TextView textTime = (TextView) convertView.findViewById(R.id.time);
		textMsg.setText(mMessageList.get(position).getText());
		textTime.setText(mMessageList.get(position).getTime());
		if(DEBUG)
		{
			JDingDebug.printfD(TAG, "msg=" + mMessageList.get(position).getText());
			JDingDebug.printfD(TAG, "time=" + mMessageList.get(position).getTime());
		}
		return convertView;
	}
	
	public void setUser(SingleUser su)
	{
		this.mSingleUser = su;
		SystemVar.db.getMessages(mSingleUser);
		this.mMessageList = mSingleUser.getMessageList();
	}
}
