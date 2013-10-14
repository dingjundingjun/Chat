package com.dingj.chatjar.thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import jding.debug.JDingDebug;

import com.dingj.chatjar.content.DataPacket;
import com.dingj.chatjar.util.IpMsgConstant;
import com.dingj.chatjar.util.PacketQueue;
import com.dingj.chatjar.util.SocketManage;
import com.dingj.chatjar.util.SystemVar;

/**
 * 监听UDP 接收发来的消息
 * @author dingj
 *
 */
public class RecvPacketThread implements Runnable
{
	private final String TAG = "RecvPacketThread";
	private boolean DEBUG = true;
	public void run()
	{
		SystemVar.init();
		try
		{
			DatagramSocket defaultSocket = SocketManage.getInstance()
					.getUdpSocket();
			// DatagramPacket pack = new DatagramPacket(new
			// byte[IpMsgConstant.PACKET_LENGTH], IpMsgConstant.PACKET_LENGTH);
			while (true)
			{
				if(DEBUG)
				{
				    JDingDebug.printfSystem("1111111111");
				}
				DatagramPacket pack = new DatagramPacket(
						new byte[IpMsgConstant.PACKET_LENGTH],
						IpMsgConstant.PACKET_LENGTH);
				defaultSocket.receive(pack);
				byte[] buffer = new byte[pack.getLength()];
				// JDingDebug.printfSystem("长度:" + pack.getLength());
				System.arraycopy(pack.getData(), 0, buffer, 0, buffer.length);    //将收到的数据拷贝倒buffer
				if(DEBUG)
				{
				    JDingDebug.printfSystem("内容:" + new String(pack.getData()));
				}
				// for(int i=0;i<buffer.length-1;i++)
				// {
				// if(buffer[i] == 0)
				// {
				// buffer[i] = 7;
				// }
				// }
				DataPacket dataPacket = DataPacket.createDataPacket(buffer,
						pack.getAddress().getHostAddress());
				if (dataPacket != null)
				{
					SystemVar.PACKET_QUEUE_EMPTY.acquire();
					PacketQueue.pushPacket(dataPacket);
					SystemVar.PACKET_QUEUE_FULL.release();
				}
			}
		} catch (SocketException ex)
		{
			ex.printStackTrace();
			System.exit(0);
		} catch (IOException ex)
		{
			ex.printStackTrace();
		} catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}
	}
}
