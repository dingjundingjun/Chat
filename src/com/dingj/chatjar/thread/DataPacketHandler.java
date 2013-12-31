package com.dingj.chatjar.thread;

import com.dingj.chatjar.content.DataPacket;
import com.dingj.chatjar.content.FileInfo;
import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.Observer;
import com.dingj.chatjar.content.SendFileInfo;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.db.MsgDatabase;
import com.dingj.chatjar.util.IpMsgConstant;
import com.dingj.chatjar.util.SendUtil;
import com.dingj.chatjar.util.Notify;
import com.dingj.chatjar.util.NotifyRecvFile;
import com.dingj.chatjar.util.SystemVar;
import com.dingj.chatjar.util.UserInfo;
import com.dingj.chatjar.util.Util;

import jding.debug.JDingDebug;
import android.content.Context;

public class DataPacketHandler extends DataPacketAnalytical
{
	private MsgDatabase database = null;
	private Context mContext;
	private Observer mObserver;
	/** 弹通知 */
	private Notify mNotify;
	private UserInfo mUserInfo = null;
	private static boolean DEBUG = true;
	private static final String TAG = "DataPacketHandler";

	public DataPacketHandler(Context context)
	{
		super();
		mContext = context;
		database = new MsgDatabase(mContext);
		database.createDB();
		database.getWriteable();
		SystemVar.db = database;
		mUserInfo = UserInfo.getInstance();
	}

	@Override
	public void ansentry(DataPacket dataPacket) // 有新的用户登陆
	{
		if(dataPacket == null)
		{
			return;
		}
		if(DEBUG)
		{
			JDingDebug.printfD(TAG, "ansentry");
		}
		SingleUser usertemp = SingleUser.changeDataPacket(dataPacket);
		DataPacket dp = new DataPacket(
				IpMsgConstant.IPMSG_ANSENTRY);
		dp.setAdditional(SystemVar.USER_NAME + "\0");
		dp.setIp(Util.getLocalHostIp());
		SendUtil.sendUdpPacket(dp, dataPacket.getIp());    //发送一个消息响应登录
		if(mUserInfo.addUsers(usertemp))
		{
			database.insertAccount(usertemp.getIp(), usertemp.getUserName()); // 写入数据库
		}
		mObserver = SystemVar.gCCMsgControl.getObserver();
		if (mObserver != null)
		{
			mObserver.notifyAddUser(usertemp); // 通知UI更新
			
		}
	}

	@Override
	public void reciveMsg(DataPacket dataPacket)
	{
		if ((IpMsgConstant.IPMSG_SENDCHECKOPT & dataPacket.getOption()) != 0)
		{
			// 发送一个“已经接收到消息”的信息给发送者
			DataPacket tmpPacket = new DataPacket(IpMsgConstant.IPMSG_RECVMSG);
			tmpPacket.setAdditional(dataPacket.getPacketNo());
			tmpPacket.setIp(dataPacket.getIp());
			SendUtil.sendUdpPacket(tmpPacket, tmpPacket.getIp());
		}

		SingleUser mUserVo = Util.getUserWithIp(dataPacket.getIp(), mUserInfo);

		if (DEBUG)
		{
			JDingDebug.printfD(TAG,
					"getFileOption:" + dataPacket.getCommandNo() + "   "
							+ dataPacket.getFileOption());
		}
		if (dataPacket.getFileOption() == IpMsgConstant.IPMSG_FILEATTACHOPT)// 接收文件
		{
			analyReciveMsg(dataPacket, mUserVo);
		} 
		else    // 接收消息
		{
			
			String additional = dataPacket.getAdditional();
			IpmMessage ipmMessage = new IpmMessage();
			ipmMessage.setIp(dataPacket.getIp());
			ipmMessage.setText(additional);
			ipmMessage.setName(mUserVo.getUserName());
			ipmMessage.setTime(Util.getTime());
			ipmMessage.setMod(Util.IPM_MOD_RECV);
			ipmMessage.setUniqueTime(System.currentTimeMillis());
			mUserVo.add(ipmMessage);
			unreadMessage(ipmMessage);
			if(DEBUG)
			{
				JDingDebug.printfD(TAG, "recive msg ==>" + additional);
			}
		}
	}

	public void recvfile(SendFileInfo sendFileInfo) // 有文件需要接收
	{

		mObserver = SystemVar.gCCMsgControl.getObserver();
		if (mObserver != null) // 如果是在当前的界面 就不弹通知 否则则弹通知
		{
			mObserver.notifyRecvFile();
		} else
		{
			mNotify = new NotifyRecvFile(mContext);
			mNotify.productNotify(sendFileInfo.getIp());
		}
	}

	public void unreadMessage(IpmMessage ipmMessage)
	{
		mObserver = SystemVar.gCCMsgControl.getObserver();
		if (mObserver != null)
		{
			mObserver.notifyNewMessage(ipmMessage);
		} else
		{
//			notify = new CCNotifyUnReadMessage(mContext);
//			notify.productNotify(ipmMessage.getIp());
		}
	}

	public void sendStop()
	{
		mObserver = SystemVar.gCCMsgControl.getObserver();
		if (mObserver != null)
		{
			mObserver.sendStop();
		}
	}

	/**
	 * 分析接收文件包
	 */
	private void analyReciveMsg(DataPacket dataPacket, SingleUser userVo)
	{
		if (gPacktNo == null)
		{
			gPacktNo = dataPacket.getPacketNo();
		} else
		{
			if (gPacktNo.equals(dataPacket.getPacketNo()))// 保证接收一次
			{
				return;
			}
		}
		gPacktNo = dataPacket.getPacketNo();
		// 解析additional
		if (dataPacket.anilyAdditional())
		{
			SendFileInfo s = null;
			for (FileInfo fileinfo : dataPacket.getFileInfo())
			{
				SendFileInfo sendFileInfo = new SendFileInfo();
				sendFileInfo.setDataPacker(dataPacket);
				sendFileInfo.setIp(dataPacket.getIp());
				sendFileInfo.setFileName(fileinfo.getFileName());
				sendFileInfo.setFileNo(fileinfo.getFileNo());
				sendFileInfo.setProperty(fileinfo.getFileProperty());
				sendFileInfo.setTransState(SendFileInfo.TRANSSTATE_NOT_START);
				long time = System.currentTimeMillis();
				sendFileInfo.setUniqueTime(time);
				String size = "0x" + fileinfo.getFileSize();
				long fileSize = Long.decode(size);
				if(DEBUG)
				{
					JDingDebug.printfD(TAG,"fileSize:" + fileSize);
					JDingDebug.printfD(TAG, "gPacktNo:" + gPacktNo);
				}
				sendFileInfo.setFileSize(fileSize);
//				sendFileInfo.setSend(false);
				SystemVar.TRANSPORT_FILE_LIST.add(sendFileInfo);
				IpmMessage ipmMessage = new IpmMessage();
				ipmMessage.setIp(dataPacket.getIp());
				ipmMessage.setText(sendFileInfo.getFileName() + " 大小：" + sendFileInfo.getFileSize());
				ipmMessage.setName(userVo.getUserName());
				ipmMessage.setTime(Util.getTime());
				ipmMessage.setMod(Util.IPM_MOD_RECV_FILE);
				ipmMessage.setUniqueTime(time);
				ipmMessage.setFileTransportState(SendFileInfo.TRANSSTATE_NOT_START);
				userVo.add(ipmMessage); // 将信息保存到单个user中
				userVo.addRecvFile(sendFileInfo);
//				unreadMessage(ipmMessage);
				s = sendFileInfo;
			}
			recvfile(s);
		}
	}

	@Override
	public void br_entry(DataPacket dataPacket)
	{
		if(dataPacket == null)
		{
			return;
		}
		SingleUser usertemp = SingleUser.changeDataPacket(dataPacket);
		if(mUserInfo.addUsers(usertemp))
		{
			JDingDebug.printfD(TAG, "br_entry ==> br_entry");
			mObserver = SystemVar.gCCMsgControl.getObserver();
			if (mObserver != null)
			{
				mObserver.notifyAddUser(usertemp); // 通知UI更新
				database.insertAccount(usertemp.getIp(), usertemp.getUserName()); // 写入数据库
			}
		}
	}

	public void removeUser(SingleUser user)
	{
		mObserver = SystemVar.gCCMsgControl.getObserver();
		if (mObserver != null)
		{
//			mObserver.notifyAddUser(user);
		}
	}

	@Override
	public void stopRecive(DataPacket dataPacket)
	{
		String fileNo = dataPacket.getAdditional();
		JDingDebug.printfSystem("that:" + fileNo);
		for (int i = 0; i < SystemVar.TRANSPORT_FILE_LIST
				.size(); i++)
		{
			JDingDebug.printfSystem("this:"
					+ SystemVar.TRANSPORT_FILE_LIST.get(i)
							.getFileNo());
			if (fileNo.equals(SystemVar.TRANSPORT_FILE_LIST
					.get(i).getFileNo()))
				;
			{
				JDingDebug.printfSystem("中断");
				SystemVar.TRANSPORT_FILE_LIST.get(i).isBreakTransport = true;
				SystemVar.TRANSPORT_FILE_LIST.remove(i);
				break;
			}
		}
	}

	@Override
	public void stopFile(DataPacket dataPacket)
	{
		String fileNo = dataPacket.getAdditional();
		String ip = dataPacket.getIp();
		SingleUser singleUser = Util.getUserWithIp(ip, UserInfo.getInstance());
		JDingDebug.printfSystem("that:" + fileNo);
		for (int i = 0; i < singleUser.getRecvList().size(); i++)
		{
			SendFileInfo sendInfo = singleUser.getRecvList().get(i);
			JDingDebug.printfSystem("this:" + sendInfo.getDataPacker().getPacketNo()+ " that:" + fileNo);
//			String longFileNo = Long.toHexString(Long.valueOf(sendInfo.getDataPacker().getPacketNo()).longValue());
			if(DEBUG)
			{
				JDingDebug.printfD(TAG, "fileNo:" + fileNo + " longFileNo:" + sendInfo.getDataPacker().getPacketNo());
			}
			if (fileNo.startsWith(sendInfo.getDataPacker().getPacketNo()))
			{
				JDingDebug.printfSystem("中断");
				sendInfo.setTransState(SendFileInfo.TRANSSTATE_ERROR);
				break;
			}
		}
//		sendStop();
	}

	@Override
	public void exit(DataPacket dataPacket)
	{
		SingleUser user = SingleUser.changeDataPacket(dataPacket);
		String userIp = user.getIp();
		for (int i = 0; i < mUserInfo.getAllUsers().size(); i++)
		{
			SingleUser usersVo = mUserInfo.getAllUsers().get(i);
			if (usersVo.getIp().equals(userIp))
			{
				mUserInfo.getAllUsers().remove(i);
				removeUser(usersVo);
				break;
			}
		}
	}
}