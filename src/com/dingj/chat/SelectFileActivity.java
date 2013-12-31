package com.dingj.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 选择文件(这里只显示本地盘，SD卡没加，原理一样)
 * @author dingj
 *
 */
public class SelectFileActivity extends Activity implements OnClickListener
{
	/**上级目录按钮*/
	private Button mBtnLastDir;
	/**发送按钮*/
	private Button mBtnSend;
	/**文件列表*/
	private ListView mFileList;
	/**文件列表*/
	private File[] mFile;
	/**本地盘路径*/
	private final String LOCAL_PATH = Environment.getExternalStorageDirectory().getPath();
	/**文件路径*/
	private String mPath;
	/**路径栈*/
	private Stack<String> mPathStack = new Stack<String>();
	/**适配器*/
	private SelectFileAdapter mSelectFileAdapter = new SelectFileAdapter();
	/**保存选中的文件列表*/
	private ArrayList<String> mFilePathList = new ArrayList<String>();
	public static final int SELECT_RESULT	= 1;
	public static final String EXTRA_FILE_LIST = "extra_file_list";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_file_layout);
		initUI();
	}
	
	private void initUI()
	{
		mFilePathList.clear();
		mFileList = (ListView)findViewById(R.id.file_list);
		mBtnLastDir = (Button)findViewById(R.id.last_dir);
		mBtnSend = (Button)findViewById(R.id.send);
		mBtnLastDir.setOnClickListener(this);
		mBtnSend.setOnClickListener(this);
		setPath(LOCAL_PATH);
		mFileList.setAdapter(mSelectFileAdapter);
		mSelectFileAdapter.notifyDataSetChanged();
		mFileList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3)
			{
				File file = mFile[index];
				if(file.isDirectory())
				{
					mPathStack.push(file.getPath());
					setPath(file.getPath());
					mSelectFileAdapter.notifyDataSetChanged();
					mFilePathList.clear();
				}
			}
		});
		mPathStack.push(LOCAL_PATH);
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.last_dir:
			{
				if(mPathStack.size() == 1)
				{
					break;
				}
				mFilePathList.clear();
				mPathStack.pop();
				setPath(mPathStack.peek());
				mSelectFileAdapter.notifyDataSetChanged();
				break;
			}
			case R.id.send:
			{
				Intent intent = new Intent();
				intent.putStringArrayListExtra(EXTRA_FILE_LIST, mFilePathList);
				setResult(SELECT_RESULT, intent);
				finish();
				break;
			}
		}
	}
	
	public class SelectFileAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return mFile.length;
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				convertView = LinearLayout.inflate(SelectFileActivity.this, R.layout.file_select_item, null);
			}
			CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.check);
			TextView textView = (TextView) convertView.findViewById(R.id.file_name);
			textView.setText(mFile[position].getName());
			checkBox.setTag(position);
			if (mFilePathList.contains(mFile[position].getPath()))
			{
				checkBox.setChecked(true);
			}
			else
			{
				checkBox.setChecked(false);
			}
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					int position = (Integer) buttonView.getTag();
					String path = mFile[position].getPath();
					if(isChecked)
					{
						if(!mFilePathList.contains(path))
						{
							mFilePathList.add(path);
						}
					}
					else
					{
						if(mFilePathList.contains(path))
						{
							mFilePathList.remove(path);
						}
					}
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 设置文件路径
	 * @param path
	 */
	public void setPath(String path)
	{
		mPath = path;
		File file = new File(mPath);
		if(file.exists() && file.isDirectory())
		{
			mFile = file.listFiles();
		}
	}
}
