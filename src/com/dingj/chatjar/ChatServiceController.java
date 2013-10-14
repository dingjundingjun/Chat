package com.dingj.chatjar;


import com.dingj.chat.ChatService;
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
	private Observer mNotifyControl;
	private final String SERVICE_ACTION = "com.jdingj.chat.service";
	public ChatServiceController(Context context,Observer observer)
	{
		super();
		this.mContext = context;
		mNotifyControl = observer;
	}
	/**
	 * 初始化绑定服务
	 */
	public void init()
	{
		Intent intent = new Intent(SERVICE_ACTION);
		boolean bind = mContext.bindService(intent, serviceConnect, Context.BIND_AUTO_CREATE);
		if(!bind)
		{
			Toast.makeText(mContext, "bind service failed", Toast.LENGTH_SHORT).show();
			return;
		}
		SystemVar.gCCMsgControl.attach(mNotifyControl);
	}
	
	/**
	 * 连接局域网
	 */
	public void connect()
	{
		if(mCharService != null)
		{
			mCharService.connect();
		}
	}
	
	public void logn()
	{
		if(mCharService != null)
		{
			mCharService.logn();
		}
	}
	/**
	 * 下线断开局域网
	 */
	public void disconnect()
	{
		mContext.unbindService(serviceConnect);
		SystemVar.gCCMsgControl.attach(mNotifyControl);
	}
	
	private ServiceConnection serviceConnect = new ServiceConnection()
	{
		
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mCharService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mCharService = ((ChatService.CharBind)service).getService();
			mCharService.connect();
		}
	};
}
