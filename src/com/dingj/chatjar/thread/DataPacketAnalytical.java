package com.dingj.chatjar.thread;

import com.dingj.chatjar.content.DataPacket;
import com.dingj.chatjar.content.IpmMessage;
import com.dingj.chatjar.content.SendFileInfo;
import com.dingj.chatjar.content.SingleUser;
import com.dingj.chatjar.util.IpMsgConstant;
import com.dingj.chatjar.util.NetUtil;
import com.dingj.chatjar.util.PacketQueue;
import com.dingj.chatjar.util.SystemVar;
import com.dingj.chatjar.util.UserInfo;

import jding.debug.JDingDebug;

/**
 * 解析发送过来的协议
 * 
 */
public abstract class DataPacketAnalytical implements Runnable
{
	public String gPacktNo;
	public DataPacketAnalytical()
	{
		
	}

	public void run()
	{
		while (true)
		{
			try
			{
				SystemVar.PACKET_QUEUE_FULL.acquire();
				DataPacket dataPacket = PacketQueue.popPacket(); // 取出一个包
				SystemVar.PACKET_QUEUE_EMPTY.release();
				JDingDebug.printfSystem("command:"
						+ dataPacket.getCommandFunction());
				switch((int) dataPacket.getCommandFunction())
				{
					case IpMsgConstant.IPMSG_BR_ENTRY:    // 登录后应答信息
					{
						ansentry(dataPacket);
						break;
					}
					case IpMsgConstant.IPMSG_SENDMSG:    //发送消息过来
					{
						reciveMsg(dataPacket);
						break;
					}
					case IpMsgConstant.IPMSG_ANSENTRY:// 添加成功设置在线用户数
					{
						br_entry(dataPacket);
						break;
					}
					case IpMsgConstant.IPMSG_BR_EXIT:// 用户下线
					{
						exit(dataPacket);
						
						break;
					}
					case IpMsgConstant.IPMSG_STOPFILE:// 停止发送
					{
						stopFile(dataPacket);
						break;
					}

					case 230:// 停止接收
					{
						stopRecive(dataPacket);
						break;
					}
				}
			} catch (InterruptedException ex)
			{
			}
		}
	}
	
	/**新的用户上线*/
	public abstract void ansentry(DataPacket dataPacket);
    
	/**成功设置应答*/
	public abstract void br_entry(DataPacket dataPacket);
	
	/**接收到消息，这里面可能包括很多种消息，需要分开解析*/
	public abstract void reciveMsg(DataPacket dataPacket);
	
	/**停止接收文件*/
	public abstract void stopRecive(DataPacket dataPacket);
	
	/**停止发送文件*/
	public abstract void stopFile(DataPacket dataPacket);
	
	/**用户下线*/
	public abstract void exit(DataPacket dataPacket);
}
