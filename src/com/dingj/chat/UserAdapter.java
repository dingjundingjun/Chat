package com.dingj.chat;

import java.util.ArrayList;
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
	/**已经读的item*/
	private int mReadItemIndex = -1;
	/**记录有未读消息的ip*/
	private List<String> mUnReadIp = new ArrayList<String>();
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
			String ip = mUserList.get(position).getIp();
			userName.setText(mUserList.get(position).getUserName());
			userIp.setText(ip);
			convertView.setTag(mUserList.get(position).getIp());    //ip唯一
			//判断是否有未读消息
			if(mUnReadIp.contains(ip))
			{
				newMsg.setVisibility(View.VISIBLE);
			}
			else
			{
				newMsg.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	/**
	 * 设置未读信息
	 * @param ipmsg
	 */
	public void setUnReadMsg(IpmMessage ipmsg) 
	{
		if (!mUnReadIp.contains(ipmsg.getIp()))
		{
			mUnReadIp.add(ipmsg.getIp());
		}
	}
	
	/**
	 * 删除未读信息标记
	 * @param ip
	 */
	public void deleteUnReadIp(String ip)
	{
		if(mUnReadIp.contains(ip))
		{
			mUnReadIp.remove(ip);
		}
	}
	
	public void clearUser()
	{
		this.mUserList.clear();
	}
	
}
