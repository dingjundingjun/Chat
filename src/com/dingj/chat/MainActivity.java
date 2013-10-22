package com.dingj.chat;

import java.util.ArrayList;
import java.util.List;

import jding.debug.JDingDebug;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.dingj.chatjar.ChatServiceController;
import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.Observer;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.util.SystemVar;
import com.dingj.chatjar.util.UserInfo;

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
	/** 用户上线 */
	private final int HANDLER_ADD_USER = 0;
	/**新消息到来*/
	private final int HANDLER_NEW_MSG = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBtnFresh = (Button)findViewById(R.id.fresh);
		mBtnFresh.setOnClickListener(this);
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
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mCharServiceController.disconnect();
	}

	/**
	 * 初始化
	 */
	private void init() {
		mUserList.clear();
		mCharServiceController = new ChatServiceController(
				getApplicationContext(), new NotifyObserver());
		mCharServiceController.init();
		mCharServiceController.connect();
		if (DEBUG) {
			JDingDebug.printfD(TAG, "connect over");
		}
		mUserListView = (ListView) findViewById(R.id.user_list);
		mUserAdapter = new UserAdapter(getApplicationContext());
		mUserListView.setAdapter(mUserAdapter);
	}

	private class NotifyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_ADD_USER: {
				mUserAdapter.setList(UserInfo.getInstance().getAllUsers());
				mUserAdapter.notifyDataSetChanged();
				break;
			}
			case HANDLER_NEW_MSG:
			{
				IpmMessage ipmsg = (IpmMessage) msg.obj;
				mUserAdapter.setUnReadMsg(ipmsg);
				mUserAdapter.notifyDataSetChanged();
			}
			}
		}
	}

	private class NotifyObserver extends Observer {
		@Override
		public void notifyAddUser(SingleUser user) {
			mNotifyHandler.sendEmptyMessage(HANDLER_ADD_USER);
		}

		@Override
		public void notifyRecvFile() {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyNewMessage(IpmMessage ipmsg) 
		{
			Message msg = new Message();
			msg.obj = ipmsg;
			msg.what = HANDLER_NEW_MSG;
			mNotifyHandler.sendMessage(msg);
		}

		@Override
		public void sendStop() {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId())
		{
		case R.id.fresh:
		{
			mUserAdapter = new UserAdapter(getApplicationContext());
			mUserListView.setAdapter(mUserAdapter);
			mCharServiceController.logn();
			break;
		}
		}
	}
}
