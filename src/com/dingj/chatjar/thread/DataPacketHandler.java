package com.dingj.chatjar.thread;

import com.dingj.chatjar.content.DataPacket;
import com.dingj.chatjar.content.FileInfo;
import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.Observer;
import com.dingj.chatjar.content.SendFileInfo;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.db.MsgDatabase;
import com.dingj.chatjar.util.IpMsgConstant;
import com.dingj.chatjar.util.NetUtil;
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
	private static boolean DEBUG = false;
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
		SingleUser usertemp = SingleUser.changeDataPacket(dataPacket);
		if (mUserInfo.addUsers(usertemp))
		{
			mObserver = SystemVar.gCCMsgControl.getObserver();
			if (mObserver != null)
			{
				mObserver.notifyAddUser(usertemp); // 通知UI更新
				database.insertAccount(usertemp.getIp(), usertemp.getUserName()); // 写入数据库
			}
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
			NetUtil.sendUdpPacket(tmpPacket, tmpPacket.getIp());
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
			ipmMessage.setName(dataPacket.getSenderName());
			ipmMessage.setTime(Util.getTime());
			mUserVo.add(ipmMessage);
			unreadMessage(ipmMessage);
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
			mObserver.notifyNewMessage();
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
				String size = "0x" + fileinfo.getFileSize();
				long fileSize = Long.decode(size);
				if(DEBUG)
				{
					JDingDebug.printfD(TAG,"fileSize:" + fileSize);
				}
				sendFileInfo.setFileSize(fileSize);
				sendFileInfo.setSend(false);
				SystemVar.TRANSPORT_FILE_LIST.add(sendFileInfo);
				IpmMessage ipmMessage = new IpmMessage();
				ipmMessage.setIp(dataPacket.getIp());
				ipmMessage.setText(dataPacket.getSenderName() + "传来文件"
						+ sendFileInfo.getFileName() + "等待接收");
				ipmMessage.setName(dataPacket.getSenderName());
				ipmMessage.setTime(Util.getTime());
				userVo.add(ipmMessage); // 将信息保存到单个user中
				s = sendFileInfo;
			}
			recvfile(s);
		}
	}

	@Override
	public void br_entry(DataPacket dataPacket)
	{
		SingleUser user = SingleUser.changeDataPacket(dataPacket);
		mUserInfo.addUsers(user);
		DataPacket dp = new DataPacket(
				IpMsgConstant.IPMSG_ANSENTRY);
		dp.setAdditional(SystemVar.USER_NAME);
		dp.setIp(NetUtil.getLocalHostIp());
		ansentry(dataPacket);
		NetUtil.sendUdpPacket(dp, dataPacket.getIp());
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
		JDingDebug.printfSystem("that:" + fileNo);
		for (int i = 0; i < SystemVar.TRANSPORT_FILE_LIST
				.size(); i++)
		{
			JDingDebug.printfSystem("this:"
					+ SystemVar.TRANSPORT_FILE_LIST.get(i)
							.getDataPacker().getPacketNo()
					+ " that:" + fileNo);
			String longFileNo = Long.toHexString(Long.valueOf(
					SystemVar.TRANSPORT_FILE_LIST.get(i)
							.getDataPacker().getPacketNo())
					.longValue());
			if (fileNo.equals(longFileNo))
				;
			// if((SystemVar.TRANSPORT_FILE_LIST.get(i).getFileNo()
			// + "0").equals(fileNo))
			{
				JDingDebug.printfSystem("中断");
				SystemVar.TRANSPORT_FILE_LIST.get(i).isBreakTransport = true;
				break;
			}
		}
		sendStop();
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