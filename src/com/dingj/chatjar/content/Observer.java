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
	public abstract void notifyNewMessage(IpmMessage ipmsg);
	/**停止接收文件*/
	public abstract void sendStop();
	/**绑定服务成功*/
	public abstract void connectServiceSuccess();
}
