package com.dingj.chatjar;


import com.dingj.chatjar.content.Observer;
import com.dingj.chatjar.util.SystemVar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

/**
 * @author dingj
 * 后台服务控制类
 * 作用：控制后台网络，对行为进行封装
 *
 */
public class ChatServiceController
{
	private Context mContext;
	private final String TAG = "CharServiceController";
	private boolean DEBUG = true;
	/**服务接口*/
	private ChatService mCharService;
	/**和界面的回调接口*/
	private Observer mNotifyControl;
	private static ChatServiceController mChatsChatServiceController = null;
	private final String SERVICE_ACTION = "com.jding.chatjar.chatservice";
	public ChatServiceController()
	{
		super();
	}
	
	public static ChatServiceController getInstance(Context c,Observer ob)
	{
		if(mChatsChatServiceController == null)
		{
			mChatsChatServiceController = new ChatServiceController();
		}
		mChatsChatServiceController.init(c, ob);
		return mChatsChatServiceController;
	}
	
	/**
	 * 初始化绑定服务
	 */
	public void init(Context context,Observer observer)
	{
		this.mContext = context;
		this.mNotifyControl = observer;
		Intent intent = new Intent(SERVICE_ACTION);    //启动服务
		mContext.startService(intent);
		boolean bind = mContext.bindService(intent, serviceConnect, Context.BIND_AUTO_CREATE);
		if(!bind)
		{
			Toast.makeText(mContext, "bind service failed", Toast.LENGTH_SHORT).show();
			return;
		}
		attach(observer);
	}
	
	private ServiceConnection serviceConnect = new ServiceConnection()
	{
		public void onServiceDisconnected(ComponentName name)
		{
			mCharService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mCharService = ((ChatService.CharBind) service).getService();
			mNotifyControl.connectServiceSuccess();
		}
	};
	
	/**
	 * 释放当前的控制句柄
	 */
	public void detach(Observer ob)
	{
		SystemVar.gCCMsgControl.detach(ob);
	}
	
	public void attach(Observer ob)
	{
		SystemVar.gCCMsgControl.attach(ob);
	}
	
	/**
	 * 连接上线
	 */
	public void logn()
	{
		//重新登录前是否要退出一下？
		if(mCharService != null)
		{
			mCharService.logn();
		}
	}
}
