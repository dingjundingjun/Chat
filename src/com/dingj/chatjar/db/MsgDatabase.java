package com.dingj.chatjar.db;


import jding.debug.JDingDebug;

import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.util.SystemVar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MsgDatabase
{
	private SQLiteDatabase db = null;
	private Context mContext;
	private CCmsgDatabaseHelper ccmsgDatabaseHelper = null;
	private boolean DEBUG = true;
	private final String TAG = "MsgDatabase";
	public MsgDatabase(Context context)
	{
		mContext = context;
	}

	public void createDB()
	{
		ccmsgDatabaseHelper = new CCmsgDatabaseHelper(mContext);
	}

	public void getWriteable()
	{
		db = ccmsgDatabaseHelper.getWritableDatabase();
		ccmsgDatabaseHelper.onUpgrade(db, 0, 0);
	}

	public boolean checkDBOpen()
	{
		if (db == null)
			return false;
		return db.isOpen();
	}

	/**
	 * 修改当前用户名称
	 * @param name
	 */
	public void modifyUserName(String name)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(CCmsgDatabaseHelper.USER_NAME, name);
		try
		{
			String queryUserNameSql = "select * from "
					+ CCmsgDatabaseHelper.USER_TABEL;
			Cursor cursor = null;
			try
			{
				cursor = db.rawQuery(queryUserNameSql, null);
				if(cursor.getCount() == 0)
				{
					db.insert(CCmsgDatabaseHelper.USER_TABEL, null, contentValues);
				}
				if(cursor != null)
				{
					cursor.close();
				}
			}
			catch(Exception e)
			{
				
			}
			db.update(CCmsgDatabaseHelper.USER_TABEL, contentValues,"_id=1",null);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取用户名
	 * @return
	 */
	public String getUserName()
	{
		String queryUserNameSql = "select * from "
				+ CCmsgDatabaseHelper.USER_TABEL;
		Cursor cursor = null;
		try
		{
			cursor = db.rawQuery(queryUserNameSql, null);
			if(cursor != null && cursor.moveToFirst())
			{
				SystemVar.USER_NAME = cursor.getString(cursor.getColumnIndex(CCmsgDatabaseHelper.USER_NAME));
			}
		}
		catch(Exception e)
		{
			
		}
		if(cursor != null)
		{
			cursor.close();
		}
		return SystemVar.USER_NAME;
	}
	
	public void insertAccount(String ip, String name)
	{
		String queryUserNameSql = "select * from "
				+ CCmsgDatabaseHelper.ACCOUNT_TABLE_NAME + " where ip='" + ip + "'";
		Cursor cursor = null;
		try
		{
			cursor = db.rawQuery(queryUserNameSql, null);
			if(cursor.getCount() != 0)
			{
				if(cursor != null)
				{
					cursor.close();
				}
				return;
			}
			if(cursor != null)
			{
				cursor.close();
			}
		}
		catch(Exception e)
		{
			
		}
		if(DEBUG)
		{
			JDingDebug.printfD(TAG, "insertAccount = " + ip);
		}
		ContentValues contentValues = new ContentValues();
		contentValues.put(CCmsgDatabaseHelper.ACCOUNT_IP, ip);
		contentValues.put(CCmsgDatabaseHelper.ACCOUNT_NAME, name);
		try
		{
			db.insert(CCmsgDatabaseHelper.ACCOUNT_TABLE_NAME, null,
					contentValues);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 将信息写入数据库
	 * @param content
	 * @param key
	 * @param time
	 * @param from
	 */
	public void insertMessage(IpmMessage ipmMessage)//String content, String key, String time,String from,int mod)
	{
		//ipmMessage.getText(), ipmMessage.getIp(),ipmMessage.getTime(),ipmMessage.getName(),ipmMessage.getMod());
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_CONTENT, ipmMessage.getText());
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_TIME, ipmMessage.getTime());
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_FROM, ipmMessage.getName());
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_KEY, ipmMessage.getIp());
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_MOD, ipmMessage.getMod());
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_UNIQUE,ipmMessage.getUniqueTime());
		try
		{
			db.insert(CCmsgDatabaseHelper.MESSAGE_TABLE_NAME, null,
					contentValues);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void deleteAccount(String ip)
	{
		try
		{
			db.delete(CCmsgDatabaseHelper.ACCOUNT_NAME,
					CCmsgDatabaseHelper.ACCOUNT_IP + "=?", new String[]
					{ ip });
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void deleteMessages(String key)
	{
		try
		{
			db.delete(CCmsgDatabaseHelper.MESSAGE_TABLE_NAME,
					CCmsgDatabaseHelper.MESSAGE_KEY + "=?", new String[]
					{ key });
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void closeDB()
	{
		if (checkDBOpen())
		{
			db.close();
		}
	}

	public void getMessages(SingleUser user)
	{
		user.cleanAllList();
		String queryMessagesSql = "select * from "
				+ CCmsgDatabaseHelper.MESSAGE_TABLE_NAME + " where messagekey="
				+ "'" + user.getIp() + "'";
		if(DEBUG)
		{
			JDingDebug.printfD(TAG, "queryMessagesSql = " + queryMessagesSql);
		}
		Cursor cursor = null;
		try
		{
			cursor = db.rawQuery(queryMessagesSql, null);
			if (cursor != null && cursor.moveToFirst())
			{
				while (cursor != null)
				{
					IpmMessage ipmMessage = new IpmMessage();
					ipmMessage.setText(cursor.getString(1));
					ipmMessage.setTime(cursor.getString(2));
					ipmMessage.setName(cursor.getString(3));
					ipmMessage.setMod(cursor.getInt(4));
					ipmMessage.setUniqueTime(cursor.getLong(5));
					if(DEBUG)
					{
						JDingDebug.printfD(TAG, "text = " + cursor.getString(1));
						JDingDebug.printfD(TAG, "time = " + cursor.getString(2));
						JDingDebug.printfD(TAG, "name = " + cursor.getString(3));
						JDingDebug.printfD(TAG, "mod = " + cursor.getString(4));
						JDingDebug.printfD(TAG, "unique = " + cursor.getString(5));
					}
					user.addAllMessages(ipmMessage);
					if (!cursor.isLast())
						cursor.moveToNext();
					else
					{
						break;
					}
				}
			}
		} catch (SQLException e)
		{
		} finally
		{
			if (cursor != null)
			{
				cursor.close();
				cursor = null;
			}
		}
	}
	
	
}
