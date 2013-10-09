package com.dingj.chat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.dingj.chatjar.ChatServiceController;
import com.dingj.chatjar.content.Observer;
import com.dingj.chatjar.content.SingleUser;

public class MainActivity extends Activity
{
	/**服务控制类*/
	private ChatServiceController mCharServiceController;
	/**显示用户列表*/
	private ListView mUserListView;
	/**用户适配器*/
	private UserAdapter mUserAdapter;
	/**用户列表*/
	private List<SingleUser> mUserList = new ArrayList<SingleUser>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
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
		mCharServiceController.disconnect();
	}

	/**
	 * 初始化
	 */
	private void init()
	{
		mUserList.clear();
		mCharServiceController = new ChatServiceController(
				getApplicationContext(), new NotifyObserver());
		mCharServiceController.init();
		mCharServiceController.connect();
		
		mUserListView = (ListView)findViewById(R.id.user_list);
		mUserAdapter = new UserAdapter(getApplicationContext());
		mUserListView.setAdapter(mUserAdapter);
	}

	private class NotifyObserver extends Observer
	{

		@Override
		public void notifyAddUser(SingleUser user)
		{
			mUserList.add(user);
			mUserAdapter.setList(mUserList);
			mUserAdapter.notifyDataSetChanged();
		}

		@Override
		public void notifyRecvFile()
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyNewMessage()
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void sendStop()
		{
			// TODO Auto-generated method stub

		}

	}
}
