package com.dingj.chatjar.util;

import java.net.DatagramSocket;
import java.net.SocketException;

import jding.debug.JDingDebug;

/**
 * SOCKET 管理  单例模式
 * 
 */
public class SocketManage
{
	 //udp SOCKET
    private static DatagramSocket udpSocket;

    private static SocketManage instance;

	/**
	 * 新建一个udp连接 端口号2425
	 * @throws SocketException
	 */
	private SocketManage() throws SocketException
	{
		udpSocket = new DatagramSocket(IpMsgConstant.IPMSG_DEFAULT_PORT);
		JDingDebug.printfSystem("port: " + udpSocket.getLocalPort());
	}

    /**
     * 返回单例对象
     * @return SocketManage
     * @throws SocketException
     */
	public static SocketManage getInstance() throws SocketException
	{
		if (instance == null)
			return instance = new SocketManage();
		else
			return instance;
	}

    /**
     * 返回IPMsg通信端口的 UDP SOCKET
     * @return UDP SOCKET
     */
	public DatagramSocket getUdpSocket()
	{
		return udpSocket;
	}
}
