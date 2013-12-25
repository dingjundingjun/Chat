package com.dingj.chat;

import java.util.ArrayList;
import java.util.List;

import jding.debug.JDingDebug;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.dingj.chatjar.ChatServiceController;
import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.Observer;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.util.UserInfo;
import com.dingj.chatjar.util.Util;

public class MainActivity extends Activity implements OnClickListener
{
	/** 服务控制类 */
	private ChatServiceController mCharServiceController;
	/** 显示用户列表 */
	private ListView mUserListView;
	/** 用户适配器 */
	private UserAdapter mUserAdapter;
	/** 用户列表 */
	private List<SingleUser> mUserList = new ArrayList<SingleUser>();
	private final boolean DEBUG = true;
	private final String TAG = "MainActivity";
	private NotifyHandler mNotifyHandler = new NotifyHandler();
	private Button mBtnFresh;
	private Button mBtnSetting;
	/**监听类*/
	private NotifyObserver mNotifyObserver = new NotifyObserver();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBtnFresh = (Button) findViewById(R.id.fresh);
		mBtnFresh.setOnClickListener(this);
		mBtnSetting = (Button) findViewById(R.id.setting);
		mBtnSetting.setOnClickListener(this);
		iniNet();
		init();
	}

	private void iniNet()
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
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		mCharServiceController.detach(mNotifyObserver);
	}

	/**
	 * 初始化
	 */
	private void init()
	{
		mUserList.clear();
		mCharServiceController = ChatServiceController.getInstance(
				getApplicationContext(), mNotifyObserver);
		if (DEBUG)
		{
			JDingDebug.printfD(TAG, "connect over");
		}
		mUserListView = (ListView) findViewById(R.id.user_list);
		mUserAdapter = new UserAdapter(getApplicationContext());
		mUserListView.setAdapter(mUserAdapter);
		mUserListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3)
			{
				Intent intent = new Intent(MainActivity.this,
						MessageActivity.class);
				String ip = (String) view.getTag();
				intent.putExtra("ip", ip);
				startActivity(intent);
			}
		});
	}

	private class NotifyHandler extends Handler 
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
				case Util.HANDLER_ADD_USER:
				{
					mUserAdapter.setList(UserInfo.getInstance().getAllUsers());
					mUserAdapter.notifyDataSetChanged();
					break;
				}
				case Util.HANDLER_NEW_MSG:
				{
					IpmMessage ipmsg = (IpmMessage) msg.obj;
					mUserAdapter.setUnReadMsg(ipmsg);
					mUserAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	private class NotifyObserver extends Observer
	{
		@Override
		public void notifyAddUser(SingleUser user)
		{
			mNotifyHandler.sendEmptyMessage(Util.HANDLER_ADD_USER);
		}

		@Override
		public void notifyRecvFile()
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void notifyNewMessage(IpmMessage ipmsg)
		{
			if(DEBUG)
			{
				JDingDebug.printfD(TAG, "notifyNewMessage");
			}
			Message msg = new Message();
			msg.obj = ipmsg;
			msg.what = Util.HANDLER_NEW_MSG;
			mNotifyHandler.sendMessage(msg);
		}

		@Override
		public void sendStop()
		{

		}

		@Override
		public void connectServiceSuccess()
		{
			mCharServiceController.logn();
		}

	}

	@Override
	public void onClick(View arg0)
	{
		switch(arg0.getId())
		{
			case R.id.fresh:
			{
				updateLogn();
				break;
			}
			case R.id.setting:
			{
				Intent intent = new Intent(this,SettingActivity.class);
				startActivity(intent);
				break;
			}
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mCharServiceController.attach(mNotifyObserver);
		
	}
	
	/**
	 * 刷新列表
	 */
	private void updateLogn()
	{
		mCharServiceController.logn();
		mUserAdapter.clearUser();
		mUserListView.setAdapter(mUserAdapter);
	}
}
