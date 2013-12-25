package com.dingj.chat;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.SingleUser;

public class UserAdapter extends BaseAdapter
{
	/**用户列表*/
	private List<SingleUser> mUserList;
	private Context mContext;
	/**未读信息*/
	private IpmMessage mUnReadMsg;
	/**已经读的item*/
	private int mReadItemIndex = -1;
	public void setList(List<SingleUser> listUser)
	{
		mUserList = listUser;
	}
	
	public UserAdapter(Context context)
	{
        mContext = context;
	}
	
	@Override
	public int getCount()
	{
		if(mUserList != null)
		{
			return mUserList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
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
		TextView userName = null;
		TextView userIp = null;
		TextView newMsg = null;
		if(convertView == null)
		{
			convertView = RelativeLayout.inflate(mContext, R.layout.user_list_item, null);
		}
		userName = (TextView)convertView.findViewById(R.id.user_name);
		userIp = (TextView)convertView.findViewById(R.id.user_ip);
		newMsg = (TextView)convertView.findViewById(R.id.newMsg);
		if(userName != null && userIp != null && newMsg != null)
		{
			userName.setText(mUserList.get(position).getUserName());
			userIp.setText(mUserList.get(position).getIp());
			convertView.setTag(mUserList.get(position).getIp());    //ip唯一
			if(mUnReadMsg != null && mUnReadMsg.getIp().equals(mUserList.get(position).getIp()))
			{
				newMsg.setVisibility(View.VISIBLE);
			}
		}
		if(mReadItemIndex != -1 && position == mReadItemIndex)
		{
			newMsg.setVisibility(View.GONE);
			mReadItemIndex = -1;
		}
		return convertView;
	}

	public void setReadItem(int pos)
	{
		mReadItemIndex = pos;
	}
	
	/**
	 * 设置未读信息
	 * @param ipmsg
	 */
	public void setUnReadMsg(IpmMessage ipmsg) 
	{
		mUnReadMsg = ipmsg;
	}
	
	public void clearUser()
	{
		this.mUserList.clear();
	}
	
}
