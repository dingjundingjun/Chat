package com.dingj.chatjar.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.ListIterator;
import java.util.Stack;
import java.util.StringTokenizer;

import jding.debug.JDingDebug;

import com.dingj.chatjar.content.ReciveFile.ProgressListener;
import com.dingj.chatjar.content.ReciveFile.ReciFileThread;
import com.dingj.chatjar.util.IpMsgConstant;
import com.dingj.chatjar.util.SystemVar;

public class SendFileInfo
{
	/**文件序号*/
	private String fileNo;
	/**文件名*/
	private String fileName;
	/**文件路径*/
	private String filePath;
	private DataPacket dataPacker;
	/**发送的ip*/
	private String ip;
	/**传输中的大小*/
	private long sendSize = 0;
	/**文件大小*/
	private long fileSize = 0;
	/**是否是发送*/
	public boolean isSend = false;
	/**是否中断*/
	public boolean isStop = true;
	/**是否是文件夹*/
	public boolean isDir = false;
	public boolean isBreakTransport = false;
	private String property;
	/**文件标识*/
	private long uniqueTime = 0;
	public boolean isDirStop = false;
	/**传输状态*/
	public int mTransState = -1;
	/**文件还没开始传输*/
	public static final int TRANSSTATE_NOT_START = -1;
	/**正在传输*/
	public static final int TRANSSTATE_TRANSLATING = 1;
	/**传输出错或者中断了*/
	public static final int TRANSSTATE_ERROR = 2;
	/**传输完成*/
	public static final int TRANSSTATE_FINISH = 3;
	private ProgressListener mProgressListener;
	public SendFileInfo() 
	{
		super();
	}
	public void setProperty(String per)
	{
		property = per;
	}
	
	/**
	 * 设置传输状态
	 * @param state
	 */
	public void setTransState(int state)
	{
		mTransState = state;
	}
	
	/**
	 * 获取传输状态
	 * @return
	 */
	public int getTransState()
	{
		return mTransState;
	}
	
	public boolean isBreakTransport() 
	{
		return isBreakTransport;
	}
	
	public void setBreakTransport(boolean isBreakTransport) 
	{
		this.isBreakTransport = isBreakTransport;
	}
	
	public String getProperty()
	{
		return property;
	}
	
	public String getFileNo() 
	{
		return fileNo;
	}
	
	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath()
	{
		return this.filePath;
	}
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	public long getSendSize()
	{
		return sendSize;
	}

	public void setSendSize(long sendSize)
	{
		this.sendSize += sendSize;
	}

	public long getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(long fileSize)
	{
		this.fileSize = fileSize;
	}

	public DataPacket getDataPacker()
	{
		return dataPacker;
	}

	public void setDataPacker(DataPacket dataPacker)
	{
		this.dataPacker = dataPacker;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}
	public void setSend(boolean b)
	{
		isSend = b;
	}
	
	public void setUniqueTime(long time)
	{
		this.uniqueTime = time;
	}
	
	public long getUniqueTime()
	{
		return uniqueTime;
	}
	
	public void recv(String path, SendFileInfo sendFileInfo)
	{
		DataPacket dataPack = getDataPacker();
		if (dataPack.getFileInfo() != null && dataPack.getFileInfo().size() > 0)
		{
			isStop = false;
			char c = '/';
			if (!(path.indexOf(path.length() - 1) == c))
			{
				path += '/';
			}
			ReciFileThread reciFileThread = new ReciFileThread(dataPack, path);
			reciFileThread.start();
		}
	}
	
	/**
	 * 接收文件线程
	 * @author dingj
	 *
	 */
	public class ReciFileThread extends Thread
	{
		private Socket socket;
		private DataPacket dataPack;
		/**文件大小*/
		private long fileSize;
		/**目的地址*/
		private String path;
		private byte[] buf = new byte[524288];
		private File file;
		private long tempSize = 0;

		public ReciFileThread(DataPacket dataPack, String path)
		{
			this.dataPack = dataPack;
			this.path = path;
			JDingDebug.printfSystem("保存到:" + path);
		}

		public void run()
		{
			OutputStream fileOutputStream = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try
			{
				String hex = Long.toHexString(Long.valueOf(
						dataPack.getPacketNo()).longValue()); // 这一个是文件消息序列号
				long command;
				if (getProperty().equals("1")
						|| getProperty().equals("2001"))
				{
					command = IpMsgConstant.IPMSG_GETFILEDATA
							| IpMsgConstant.IPMSG_FILEATTACHOPT;
				} else
				{
					command = IpMsgConstant.IPMSG_GETDIRFILES
							| IpMsgConstant.IPMSG_FILEATTACHOPT;
				}
				DataPacket tcpIPMPack = new DataPacket(command);
				tcpIPMPack.setPacketNo("" + new Date().getTime());
				String hexFileNo = Long.toHexString(Long.valueOf(
						getFileNo()).longValue()); // 指定文件序号
				// tcpIPMPack.setAdditional(hex + ":" +
				// mSendFileInfo.getFileNo() + ":0:");
				tcpIPMPack.setAdditional(hex + ":" + hexFileNo + ":0:");
				JDingDebug.printfSystem("ip address:" + dataPack.getIp());
				socket = new Socket(InetAddress.getByName(dataPack.getIp()),
						IpMsgConstant.IPMSG_DEFAULT_PORT);
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				JDingDebug.printfSystem("tcpIPMPack:" + tcpIPMPack.toString());
				outputStream.write(tcpIPMPack.toString().getBytes(
						SystemVar.DEFAULT_CHARACT));
				outputStream.flush(); // 首先请求接收文件
				int rLength = 0;
				setTransState(SendFileInfo.TRANSSTATE_TRANSLATING);
				if (getProperty().equals("1")
						|| getProperty().equals("2001")) // 文件
				{
					int allow = 0;
					file = new File(path);
					JDingDebug.printfSystem("send fileSize:"
							+ getFileSize());
					fileSize = getFileSize();
					JDingDebug.printfSystem("send fileSize:" + fileSize);
					
					while (allow < fileSize
							&& ((rLength = inputStream.read(buf)) != -1))
					{
						if (fileOutputStream == null)
						{
							if (!file.exists())
							{
								file.createNewFile();
							}
							fileOutputStream = new FileOutputStream(file);
						}
						fileOutputStream.write(buf, 0, rLength);
						fileOutputStream.flush();
						allow += rLength;
						int progress = (int) (allow*100/fileSize);
						if(mProgressListener != null)
						{
							mProgressListener.setRecvProgress(progress);
						}
						setSendSize(rLength);
						if (isStop == true)
						{
							// IpmMessage ipmMessage = new IpmMessage();
							// ipmMessage.setIp(mUser.getIp());
							// ipmMessage.setText("文件终止传输");
							// ipmMessage.setName(mUser.getUserName());
							// ipmMessage.setTime(SystemVar.getTime());
							// mUser.add(ipmMessage);
							// SystemVar.sendStopRecvFile(hex+":",mSendFileInfo.getIp());
							// break;
						}
					}
					if (allow >= fileSize)
					{
						JDingDebug.printfSystem("allow >= fileSize");
						// IpmMessage ipmMessage = new IpmMessage();
						// ipmMessage.setIp(mUser.getIp());
						// ipmMessage.setText("文件" +mSendFileInfo.getFileName()
						// + "接收成功");
						// ipmMessage.setName(mUser.getUserName());
						// ipmMessage.setTime(SystemVar.getTime());
						// mUser.add(ipmMessage);
					}
					
				} else
				{
					Stack<String> stack = new Stack<String>();
					fileSize = (int) getFileSize();// Integer.parseInt(""
																	// +
																	// mSendFileInfo.getFileSize(),
																	// 16);
					JDingDebug.printfSystem("fileSize :"
							+ getFileSize());
					JDingDebug.printfSystem("filePath:" + path);
					path = path.substring(0, path.lastIndexOf("/"));
					path = path.substring(0, path.lastIndexOf("/"));
					file = new File(path);
					stack.push(path);
					if (fileSize == 0 && !file.exists())
					{
						file.mkdirs();
					}
					FileDirInfo rootDirInfo = new FileDirInfo();
					setFileSize(fileSize);
					setSendSize(0);
					boolean isStop = false;
					while (!isStop)
					{
						isStop = readDir(stack, inputStream, rootDirInfo);
					}
					isDirStop = true;
				}
				if (fileOutputStream != null)
					fileOutputStream.flush();
				setTransState(SendFileInfo.TRANSSTATE_FINISH);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			} 
			finally
			{
				JDingDebug.printfSystem("recv file finally");
				try
				{
					outputStream.close();
					inputStream.close();
					if (socket != null)
						socket.close();
					if (fileOutputStream != null)
						fileOutputStream.close();
					fileOutputStream = null;
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		int jdtemp = 0;

		public boolean readDir(Stack<String> stack, InputStream inputStream,
				FileDirInfo rootDirInfo)
		{
			boolean isReturn = false;
			byte[] bufHeadLength = new byte[4];
			int packLength = 0;
			FileOutputStream outputStream = null;
			try
			{
				// if(tempSize >=fileSize)
				// {
				// return true;
				// }
				JDingDebug.printfSystem("在这里挂:" + stack.size());
				int rlength = inputStream.read(bufHeadLength);
				JDingDebug.printfSystem("rlength:" + rlength);
				String bufHeadStr = new String(bufHeadLength);
				JDingDebug.printfSystem("bufHeadStr:" + bufHeadStr);
				if (rlength == -1)
				{
					return true;
				} else
				{
					try
					{
						packLength = Integer.valueOf(bufHeadStr, 16);// 实际上是发送字节的长度
						jdtemp += packLength;
						JDingDebug.printfSystem("jdtemp:" + jdtemp);
					} catch (Exception e)
					{
						e.printStackTrace();
						return true;
					}
				}
				JDingDebug.printfSystem("bufHeadStr:" + bufHeadStr);
				byte[] headByte = new byte[packLength - 4];
				rlength = inputStream.read(headByte);

				if (rlength == -1)
				{
					return true;
				}
				String headStr = bufHeadStr + new String(headByte, "GBK");
				JDingDebug.printfSystem("headStr:" + headStr);
				StringTokenizer tokenizer = new StringTokenizer(headStr, ":",
						false);
				int i = 0;
				FileDirInfo tempFileDirInfo = null;
				while (tokenizer.hasMoreTokens())
				{
					String str = tokenizer.nextToken();
					switch(i)
					{
						case 0:
							tempFileDirInfo = new FileDirInfo();
							tempFileDirInfo.setPackLenth(packLength);
							break;
						case 1:
							tempFileDirInfo.setName(str);
							break;
						case 2:
							tempFileDirInfo.setSize(Integer.valueOf(str, 16));
							break;
						case 3:
							tempFileDirInfo.setProperty(str);
							break;
						case 4:
							tempFileDirInfo.setProperty14(str);
							break;
						case 5:
							tempFileDirInfo.setProperty16(str);
							break;
					}
					i++;
				}
				// if (rootDirInfo.getProperty14() ==
				// null&&rootDirInfo.getProperty16() == null)
				// {
				// rootDirInfo.setProperty14(tempFileDirInfo.getProperty14()) ;
				// rootDirInfo.setProperty16(tempFileDirInfo.getProperty16()) ;
				// }
				// else
				// {
				// if
				// (tempFileDirInfo.getProperty14().equals(rootDirInfo.getProperty14())
				// &&
				// tempFileDirInfo.getProperty16().equals(rootDirInfo.getProperty16()))
				// {
				// //isReturn = true;
				// }
				// }
				if (tempFileDirInfo.getProperty().equals("3"))
				{
					stack.pop();
					if (stack.size() == 1)
						return true;
					if (isReturn)
					{
						return true;
					}
					// readDir(stack, inputStream, rootDirInfo);
					return false;
				} else if (tempFileDirInfo.getProperty().equals("2"))
				{
					stack.push(tempFileDirInfo.getName());// 把文件夹名入栈
				}

				StringBuffer sb = new StringBuffer();
				ListIterator<String> ite = stack.listIterator();
				while (ite.hasNext())
				{
					String str = ite.next();
					sb.append(str);
					sb.append("/");
					JDingDebug.printfSystem("sbtoString:" + sb.toString());
				}

				if (tempFileDirInfo.getProperty().equals("2"))
				{
					File file = new File(sb.toString());// 创建文件夹

					if (!file.exists())
					{
						file.mkdirs();
					}

				} else if (tempFileDirInfo.getProperty().equals("1"))
				{
					File file = new File(sb.toString()
							+ tempFileDirInfo.getName());
					if (!file.exists())
					{
						file.createNewFile();
					}
					outputStream = new FileOutputStream(file);
					byte[] buf = new byte[800];
					int length = tempFileDirInfo.getSize();
					rlength = 0;
					int bufLength = length > buf.length ? buf.length : length;
					while (bufLength != 0
							&& (rlength = inputStream.read(buf, 0, bufLength)) != -1)
					{

						length -= rlength;
						tempSize += rlength;
						bufLength = length > buf.length ? buf.length : length;
						setSendSize(rlength);
						outputStream.write(buf, 0, rlength);
						outputStream.flush();
						// JDingDebug.printfSystem("buflength:" + bufLength +
						// " tempSize:" + tempSize + " rlenght:" + rlength);
						// if(mSendFileInfo.isStop == true)
						// {
						// break;
						// }
					}
					outputStream.flush();
				}
				if (isReturn)
				{
					return true;
				}
			} catch (IOException e)
			{
				e.printStackTrace();
				return true;
			} finally
			{
				try
				{
					if (outputStream != null)
						outputStream.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return false;
		}
	}
	
	public interface ProgressListener 
	{
		public abstract void setRecvProgress(int progress);
	}
	
	public void setListener(ProgressListener pl)
	{
		this.mProgressListener = pl;
	}
}
