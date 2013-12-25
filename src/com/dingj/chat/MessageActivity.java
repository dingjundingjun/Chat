package com.dingj.chat;

import jding.debug.JDingDebug;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dingj.chatjar.ChatServiceController;
import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.Observer;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.util.UserInfo;
import com.dingj.chatjar.util.Util;

public class MessageActivity extends Activity implements OnClickListener
{
	private boolean DEBUG = true;
	private final String TAG = "MessageActivity";
	/**返回键*/
	private Button mBtnBack;
	/**清空*/
	private Button mBtnCleanAll;
	/**用户姓名*/
	private TextView mTextName;
	/**发送文件*/
	private Button mBtnSendFile;
	/**发送按钮*/
	private Button mBtnSend;
	/**写内容*/
	private EditText mEditMessage;
	/**用户姓名*/
	private String mUserName;
	/**用户IP*/
	private String mUserIp;
	/**当前通信的用户*/
	private SingleUser mSingleUser;
	/**消息列表*/
	private ListView mMessageList;
	/**消息适配器*/
	private MessageAdapter mMessageAdapter;
	/**服务控制类*/
	private ChatServiceController mChatServiceController;
	/**handler*/
	private NotifyHandler mNotifyHandler = new NotifyHandler();
	/**监听类*/
	private NotifyObserver mNotifyObserver = new NotifyObserver();
	private class NotifyObserver extends Observer
	{
		@Override
		public void notifyAddUser(SingleUser user)
		{
		}

		@Override
		public void notifyRecvFile()
		{
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
		}
	}
	
	private class NotifyHandler extends Handler 
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
				case Util.HANDLER_NEW_MSG:
				{
					if(DEBUG)
					{
						JDingDebug.printfD(TAG, "Notify new msg");
					}
					mMessageAdapter.setUser(mSingleUser);
					mMessageAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.message_layout);
		init();
	}
	
	private void init()
	{
		mMessageList = (ListView)findViewById(R.id.message_list);
		mMessageAdapter = new MessageAdapter(this);
		mTextName = (TextView)findViewById(R.id.name);
		mEditMessage = (EditText)findViewById(R.id.msg_edit);
		mBtnBack = (Button)findViewById(R.id.back);
		mBtnCleanAll = (Button)findViewById(R.id.clean_all);
		mBtnSendFile = (Button)findViewById(R.id.file);
		mBtnSend = (Button)findViewById(R.id.sendMsg);
		mBtnBack.setOnClickListener(this);
		mBtnCleanAll.setOnClickListener(this);
		mBtnSend.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData()
	{
		
		mChatServiceController = ChatServiceController.getInstance(
				getApplicationContext(), mNotifyObserver);
		Intent intent = this.getIntent();
		mUserIp = intent.getStringExtra("ip");
		mSingleUser = Util.getUserWithIp(mUserIp, UserInfo.getInstance());
		if(mSingleUser != null)
		{
			mTextName.setText(mSingleUser.getUserName());
		}
		//显示消息记录
		mMessageAdapter.setUser(mSingleUser);
		mMessageList.setAdapter(mMessageAdapter);
		mMessageAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.back:
			{
				finish();
				break;
			}
			case R.id.clean_all:
			{
				break;
			}
			case R.id.file:
			{
				break;
			}
			case R.id.sendMsg:
			{
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		mChatServiceController.detach(mNotifyObserver);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mChatServiceController.attach(mNotifyObserver);
	}
	
	
}