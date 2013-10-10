package com.dingj.chatjar.content;

public abstract class Observer 
{

	public Observer() 
	{
		super();
	}
	/**用户上线*/
	public abstract void notifyAddUser(SingleUser user);
	/**接收文件*/
	public abstract void notifyRecvFile();
	/**新的消息*/
	public abstract void notifyNewMessage();
	/**停止接收文件*/
	public abstract void sendStop();
}
