package com.dingj.chatjar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CCmsgDatabaseHelper extends SQLiteOpenHelper 
{
	public final static String DATABASE_NAME = "ccmsg.db";
	private final static int DATABASE_VERSION = 1;
	public final static String ACCOUNT_TABLE_NAME = "account";
	public final static String MESSAGE_TABLE_NAME = "messages";
	public final static String USER_TABEL = "user";
	public final static String ACCOUNT_ID = "_id";
	public final static String ACCOUNT_IP = "ip";
	public final static String ACCOUNT_NAME = "name";
	public final static String MESSAGE_ID = "_id";
	public final static String MESSAGE_CONTENT = "mcontent";
	public final static String MESSAGE_KEY = "messagekey";
	public final static String MESSAGE_TIME = "mtime";
	public final static String MESSAGE_FROM = "mfrom";
	public final static String USER_NAME = "name";
	private final static String createAccountTabel = "CREATE TABLE "
			+ ACCOUNT_TABLE_NAME + "(" + ACCOUNT_ID
			+ " INTEGER primary key autoincrement," + ACCOUNT_IP + " text,"
			+ ACCOUNT_NAME + " text)";  
	private final static String createMessageKey = "CREATE TABLE "
			+ MESSAGE_TABLE_NAME + "(" + MESSAGE_ID
			+ " INTEGER primary key autoincrement," + MESSAGE_CONTENT
			+ " text," + MESSAGE_TIME + " text," + MESSAGE_FROM + " text," + MESSAGE_KEY + " text)";
	private final static String createUserTabel = "CREATE TABLE " + USER_TABEL + "(" + ACCOUNT_ID + " INTEGER primary key autoincrement," + USER_NAME + " text)";
	private Context mContext;
	private String dbPath = "/data/data/com.chinachip.ccmsg/databases/ccmsg.db";
	public CCmsgDatabaseHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		try
		{
			db.execSQL(createAccountTabel);
			db.execSQL(createMessageKey);
			db.execSQL(createUserTabel);
		}
		catch(Exception e)
		{
			
		}
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		try
		{
		db.execSQL(createAccountTabel);
		db.execSQL(createMessageKey);
		db.execSQL(createUserTabel);
		}
		catch(Exception e)
		{
			
		}
	}
}
