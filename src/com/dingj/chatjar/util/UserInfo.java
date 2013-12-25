package com.dingj.chatjar.util;

import java.util.ArrayList;
import java.util.List;

import jding.debug.JDingDebug;

import com.dingj.chatjar.content.SingleUser;

/**
 * 保存一些用户的信息
 * @author dingj
 *
 */
public class UserInfo
{
	private static final String TAG = "UserInfo";
	private static boolean DEBUG = true;
	private static UserInfo mUserInfo = null;
	/** 用户列表 */
	public List<SingleUser> mUserList = new ArrayList();

	public UserInfo()
	{
		super();
	}

	public static UserInfo getInstance()
	{
		if (mUserInfo == null)
		{
			mUserInfo = new UserInfo();
		}
		return mUserInfo;
	}

	/**
	 * 向在线用户集合中添加用户
	 * 
	 * @param user
	 *            用户对象
	 * @return true 添加成功 添加失败
	 */
	public synchronized boolean addUsers(SingleUser user)
	{
		if (user.getIp().equals("127.0.0.1")
				|| user.getIp().equals(SystemVar.WIFIIP))
			return false;
		if(DEBUG)
		{
		JDingDebug.printfD(TAG,"use:" + user.getIp() + " local:"
				+ SystemVar.WIFIIP);
		}
		for (int i = 0; i < mUserList.size(); i++)
		{
			if (mUserList.get(i).getIp().equals(user.getIp()))
			{
				mUserList.set(i, user);
				return false;
			}
		}
		mUserList.add(user);
		return true;
	}

	/**
	 * 清空用户列表
	 */
	public void clearUserList()
	{
		if(mUserList != null)
		mUserList.clear();
	}

	/**
	 * 获取用户列表
	 * 
	 * @return
	 */
	public synchronized List<SingleUser> getAllUsers()
	{
		return mUserList;
	}

}
