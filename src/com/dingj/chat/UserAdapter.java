package com.dingj.chat;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingj.chatjar.content.SingleUser;

public class UserAdapter extends BaseAdapter
{
	/**用户列表*/
	private List<SingleUser> mUserList;
	private Context mContext;
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
		if(convertView == null)
		{
			convertView = RelativeLayout.inflate(mContext, R.layout.user_list_item, null);
			userName = (TextView)convertView.findViewById(R.id.user_name);
			userIp = (TextView)convertView.findViewById(R.id.user_ip);
		}
		if(userName != null && userIp != null)
		{
			userName.setText(mUserList.get(position).getUserName());
			userIp.setText(mUserList.get(position).getIp());
		}
		return convertView;
	}
	
}
