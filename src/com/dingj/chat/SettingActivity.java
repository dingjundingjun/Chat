package com.dingj.chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dingj.chatjar.util.SystemVar;

public class SettingActivity extends Activity implements OnClickListener
{
	/**用户姓名*/
	private EditText mEditUserName;
	/**用户IP*/
	private TextView mTextUserIp;
	/**确定按钮*/
	private Button mBtnOk;
	/**取消按钮*/
	private Button mBtnCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
		init();
	}
	
	public void init()
	{
		mEditUserName = (EditText)findViewById(R.id.user_name);
		mTextUserIp = (TextView)findViewById(R.id.user_ip);
		mBtnOk = (Button)findViewById(R.id.ok);
		mBtnCancel = (Button)findViewById(R.id.cancel);
		mEditUserName.setText(SystemVar.db.getUserName());
		mTextUserIp.setText(SystemVar.WIFIIP);
		mBtnOk.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.ok:
			{
				String userName = mEditUserName.getText().toString();
				if(!userName.equals("") && !userName.trim().equals(""))
				{
					SystemVar.USER_NAME = userName;
					SystemVar.db.modifyUserName(userName);
				}
				else
				{
					Toast.makeText(this, this.getString(R.string.name_cannot_null), Toast.LENGTH_SHORT).show();
					return;
				}
				finish();
				break;
			}
			case R.id.cancel:
			{
				finish();
				break;
			}
		}
	}
}
