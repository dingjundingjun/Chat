package com.dingj.chatjar.db;


import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.SingleUser;

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

	public void insertAccount(String ip, String name)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(CCmsgDatabaseHelper.ACCOUNT_IP, ip);
		contentValues.put(CCmsgDatabaseHelper.ACCOUNT_NAME, name);
		try
		{
			db.insert(CCmsgDatabaseHelper.ACCOUNT_TABEL_NAME, null,
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
	public void insertMessage(String content, String key, String time,
			String from)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_CONTENT, content);
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_TIME, time);
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_FROM, from);
		contentValues.put(CCmsgDatabaseHelper.MESSAGE_KEY, key);
		try
		{
			db.insert(CCmsgDatabaseHelper.MESSAGE_TABEL_NAME, null,
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
			db.delete(CCmsgDatabaseHelper.MESSAGE_TABEL_NAME,
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
		String queryMessagesSql = "select * from "
				+ CCmsgDatabaseHelper.MESSAGE_TABEL_NAME + " where messagekey="
				+ "'" + user.getIp() + "'";
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
