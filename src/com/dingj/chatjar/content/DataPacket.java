package com.dingj.chatjar.content;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.dingj.chatjar.util.ByteBuffer;
import com.dingj.chatjar.util.IpMsgConstant;
import com.dingj.chatjar.util.SystemVar;

import jding.debug.JDingDebug;

/**
 * 包数据保存 只要用来保存解析收到的包
 * 
 * @author dingj
 * 
 */
public class DataPacket
{
	/** 版本号 */
	private String version;
	/** 命令 */
	private long commandNo;
	/** 包序列 */
	private String packetNo;
	/** 发送者名称 */
	private String senderName = null;
	/** 发送者主机 */
	private String senderHost = null;
	/** 附加内容 */
	private String additional = null;
	private String ip = null;
	private List<FileInfo> mFileInfoList;
	/** 超过这个数说明包含来附件 */
	private final static int ADDITIONAL = 6;
	private static boolean DEBUG = true;
	private static String TAG = "DataPacket";

	public DataPacket()
	{

	}

	public DataPacket(long commandNo)
	{
		this.commandNo = commandNo;
		this.packetNo = "" + System.currentTimeMillis() / 1000L;
		this.version = IpMsgConstant.IPMSG_VERSION;
		this.senderName = SystemVar.USER_NAME;
		this.senderHost = SystemVar.HOST_NAME;
	}

	public DataPacket(long commandNo, long t)
	{
		this.commandNo = commandNo;
		this.packetNo = "" + (System.currentTimeMillis() / 1000L + t);
		this.version = IpMsgConstant.IPMSG_VERSION;
		this.senderName = SystemVar.USER_NAME;
		this.senderHost = SystemVar.HOST_NAME;
	}

	public String getAdditional()
	{
		return additional;
	}

	public void setAdditional(String additional)
	{
		this.additional = additional;
	}

	public long getCommandNo()
	{
		return commandNo;
	}

	public void setCommandNo(int commandNo)
	{
		this.commandNo = commandNo;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getPacketNo()
	{
		return packetNo;
	}

	public void setPacketNo(String packetNo)
	{
		this.packetNo = packetNo;
	}

	public String getSenderHost()
	{
		return senderHost;
	}

	public void setSenderHost(String senderHost)
	{
		this.senderHost = senderHost;
	}

	public String getSenderName()
	{
		return senderName;
	}

	public void setSenderName(String senderName)
	{
		this.senderName = senderName;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public List<FileInfo> getFileInfo()
	{
		return mFileInfoList;
	}

	/**
	 * @return Low 8 bits from command number 32 bits
	 */
	public long getCommandFunction()
	{
		return commandNo & 0x000000FF;
	}

	/**
	 * @return (High 24 bits from command number 32 bits)
	 */
	public long getOption()
	{
		return commandNo & 0xFFFFFF00;
	}

	public long getFileOption()
	{
		return commandNo & 0x00E00000;
	}

	/**
	 * 根据发送过来的包，解析和封装
	 * 
	 * @param 收到的包的数据
	 * @param 发送者的IP
	 * @return 放回一个封装好的数据类型
	 */
	public static DataPacket createDataPacket(byte[] data, String ip)
	{
		if (data == null || ip == null)
			return null;
		try
		{
			String dataStr = new String(data, SystemVar.DEFAULT_CHARACT);
			// JDingDebug.printfSystem("buffer:" + dataStr);
			String[] dataArr = dataStr.split(":"); // 协议格式是以':'分隔
			if (dataArr == null)
				return null;
			else
			{
				DataPacket packet = new DataPacket();
				packet.setVersion(dataArr.length >= 1 ? dataArr[0] : "");// version
				packet.setPacketNo((dataArr.length >= 2 ? dataArr[1] : ""));// NO
				packet.setSenderName(dataArr.length >= 3 ? dataArr[2] : "");// sender
				packet.setSenderHost(dataArr.length >= 4 ? dataArr[3] : "");// sender
				packet.setCommandNo(dataArr.length >= 5 ? Integer
						.parseInt(dataArr[4]) : 0);// command 命令
				if (dataArr.length >= ADDITIONAL) // 是否包含附件信息
				{
					// if has additional
					StringBuffer strBuf = new StringBuffer();
					if (dataArr.length == ADDITIONAL)
					{
						strBuf.append(dataArr[5]);
					} else
					{
						for (int i = 5; i < dataArr.length; i++)
						{
							if (i == 5)
							{
								dataArr[5] = dataArr[5].trim();
							}
							strBuf.append(dataArr[i] + ":");
						}
					}
					packet.setAdditional(strBuf.toString());
					if (DEBUG)
					{
						JDingDebug.printfD(TAG,
								"additonal:" + strBuf.toString());
					}
				}
				packet.setIp(ip);
				return packet;
			}
		} catch (UnsupportedEncodingException ex)
		{
			return null;
		} catch (Exception e)
		{
			return null;
		}
	}

	public String toString()
	{
		return version + ":" + packetNo + ":" + senderName + ":" + senderHost
				+ ":" + commandNo + ":" + additional;
//		return "ip = " + ip + "\nversion = " + version + "\n" + "packetNo = " + packetNo + "\n" + "senderName = " + senderName + "\n" + "senderHost = " + senderHost + "\n" + "commandNo = " + commandNo 
//				+ "\n" + "addition = " + additional;
				
	}

	public byte[] toByte()
	{
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(version).append(":");
		strbuf.append(packetNo).append(":");
		strbuf.append(senderName).append(":");
		strbuf.append(senderHost).append(":");
		strbuf.append(Long.toString(commandNo)).append(":");
		strbuf.append(additional);
		String tmpstr = new String(strbuf);
		ByteBuffer bb = new ByteBuffer();
		try
		{
			bb.append(tmpstr.getBytes("GB2312"));
		} catch (UnsupportedEncodingException ex)
		{
			ex.printStackTrace();
			return null;
		}
		byte[] nullbyte =
		{ 0 };
		bb.append(nullbyte);
		JDingDebug.printfSystem("toByte:" + new String(bb.getBytes()));
		return bb.getBytes();
	}

	/**
	 * 解析additional 用在文件传输
	 * 
	 */
	public boolean anilyAdditional()
	{
		try
		{
			if (DEBUG)
			{
				JDingDebug.printfD(TAG, "additional anily:" + additional);
			}
			if (additional == null || additional.equals("")
					|| additional.length() < 10)
			{
				return false;
			}
			StringTokenizer tokenizer = new StringTokenizer(additional, ":",
					false);
			int i = 0;
			FileInfo fileInfo = null;
			String nil = new String(new byte[]
			{ 0x07 });
			JDingDebug.printfSystem("nil:" + nil);
			while (tokenizer.hasMoreTokens())
			{
				String str = tokenizer.nextToken();
				if (str.contains(nil))
				{
					i = 0;
				}
				switch(i % 9)
				{
					case 0:
						fileInfo = new FileInfo();
						byte[] nullbyte =
						{ 0x07 };
						str = str.replace(new String(nullbyte), "");
						fileInfo.setFileNo(str);
						break;
					case 1:
						fileInfo.setFileName(str);
						break;
					case 2:
						fileInfo.setFileSize(str);
						break;
					case 3:
						fileInfo.setModifyTime(str);
						break;
					case 4:
						fileInfo.setFileProperty(str);
						if (mFileInfoList == null)
						{
							mFileInfoList = new ArrayList<FileInfo>();
						}
						mFileInfoList.add(fileInfo);
						break;
					default:
						break;
				}
				i++;
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}

}