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

import com.dingj.chatjar.util.IpMsgConstant;
import com.dingj.chatjar.util.SystemVar;

import jding.debug.JDingDebug;

public class ReciveFile
{
	private boolean isStop = false;
	private ProgressListener mProgressListener;
	public ReciveFile()
	{
		super();
	}

	public void recv(String path, SendFileInfo sendFileInfo)
	{
		DataPacket dataPack = sendFileInfo.getDataPacker();
		if (dataPack.getFileInfo() != null && dataPack.getFileInfo().size() > 0)
		{
			isStop = false;
			char c = '/';
			if (!(path.indexOf(path.length() - 1) == c))
			{
				path += '/';
			}
			ReciFileThread reciFileThread = new ReciFileThread(dataPack, path,
					sendFileInfo);
			reciFileThread.start();
		}
	}

	public class ReciFileThread extends Thread
	{
		private Socket socket;
		private DataPacket dataPack;
		private long fileSize;
		private String path;
		private SendFileInfo mSendFileInfo;
		private byte[] buf = new byte[524288];
		private File file;
		private long tempSize = 0;

		public ReciFileThread(DataPacket dataPack, String path,
				SendFileInfo sendFileInfo)
		{
			this.dataPack = dataPack;
			this.path = path;
			this.mSendFileInfo = sendFileInfo;
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
				if (mSendFileInfo.getProperty().equals("1")
						|| mSendFileInfo.getProperty().equals("2001"))
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
						mSendFileInfo.getFileNo()).longValue()); // 指定文件序号
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
				if (mSendFileInfo.getProperty().equals("1")
						|| mSendFileInfo.getProperty().equals("2001")) // 文件
				{
					int allow = 0;
					file = new File(path);
					JDingDebug.printfSystem("send fileSize:"
							+ mSendFileInfo.getFileSize());
					fileSize = mSendFileInfo.getFileSize();
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
						mProgressListener.setRecvProgress(progress);
						mSendFileInfo.setSendSize(rLength);
						if (mSendFileInfo.isStop == true)
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
					fileSize = (int) mSendFileInfo.getFileSize();// Integer.parseInt(""
																	// +
																	// mSendFileInfo.getFileSize(),
																	// 16);
					JDingDebug.printfSystem("fileSize :"
							+ mSendFileInfo.getFileSize());
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
					mSendFileInfo.setFileSize(fileSize);
					mSendFileInfo.setSendSize(0);
					boolean isStop = false;
					while (!isStop)
					{
						isStop = readDir(stack, inputStream, rootDirInfo);
					}
					mSendFileInfo.isDirStop = true;
				}
				if (fileOutputStream != null)
					fileOutputStream.flush();
			} catch (IOException e)
			{
				e.printStackTrace();
			} finally
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
						mSendFileInfo.setSendSize(rlength);
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
