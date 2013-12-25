package com.dingj.chatjar;

import java.io.IOException;
import java.net.ServerSocket;

import jding.debug.JDingDebug;

import com.dingj.chatjar.content.DataPacket;
import com.dingj.chatjar.thread.DataPacketHandler;
import com.dingj.chatjar.thread.RecvPacketThread;
import com.dingj.chatjar.thread.SendFileHandler;
import com.dingj.chatjar.util.IpMsgConstant;
import com.dingj.chatjar.util.SendUtil;
import com.dingj.chatjar.util.SystemVar;
import com.dingj.chatjar.util.UserInfo;
import com.dingj.chatjar.util.Util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.Toast;

public class ChatService extends Service
{
	private ServerSocket mServerSocket = null;	
	private Context mContext;
	private final boolean DEBUG = true;
	private final String TAG = "CharService";
	private CharBind mIbinder = new CharBind();
			
	public class CharBind extends Binder
	{
		public ChatService getService()
		{
			return ChatService.this;
		}
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		initThread();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		if(DEBUG)
		{
			JDingDebug.printfD(TAG, "onBind" + mIbinder);
		}
		return mIbinder;
	}
	
	private void initNetProceess()
	{
        // 详见StrictMode文档
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
	}
	
	/**
	 * 登录
	 */
	public void logn() 
	{
		if(DEBUG)
		{
			JDingDebug.printfD(TAG,"wifiIP:" + SystemVar.WIFIIP);
		}
    	WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) 
        {
        	Toast.makeText(this, "wifi is not work", Toast.LENGTH_SHORT).show();
        	return;
        }
    	WifiInfo wifiInfo = wifiManager.getConnectionInfo();     
    	int ipAddress = wifiInfo.getIpAddress(); 
    	SystemVar.WIFIIP = Util.intToIp(ipAddress);
    	if(DEBUG)
    	{
    	    JDingDebug.printfD(TAG,"hostIp:" + SystemVar.WIFIIP);
    	}
    	//从数据库获取姓名
    	SystemVar.USER_NAME = SystemVar.db.getUserName();
        DataPacket dp=new DataPacket(IpMsgConstant.IPMSG_BR_ENTRY);
        dp.setAdditional(SystemVar.USER_NAME + "\0");
        dp.setIp(SystemVar.WIFIIP);
        if(DEBUG)
        {
        	JDingDebug.printfD(TAG,"hostIp:" + dp.getIp());
        }
//        SendUtil.broadcastUdpPacket(dp);
        UserInfo.getInstance().clearUserList();    //登录以前重新刷新一次
        SendUtil.broadcastUdpPacketToEvery(dp, ipAddress);
	}
	
	private void initThread()
	{
		try {
			mServerSocket = new ServerSocket(IpMsgConstant.IPMSG_DEFAULT_PORT);
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		new Thread(new RecvPacketThread()).start();//recive packet 
		new Thread(new DataPacketHandler(this)).start();
	}
}
