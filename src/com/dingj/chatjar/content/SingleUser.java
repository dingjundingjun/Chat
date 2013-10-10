package com.dingj.chatjar.content;

import java.util.ArrayList;
import java.util.List;

import com.dingj.chatjar.util.SystemVar;


/**
 * 单个用户信息
 * @author dingj
 *
 */
public class SingleUser 
{
	private String userName;            //用户名
    private String alias;               //别名
    private String groupName;           //工作组名
    private String ip;                  //IP地址
    private String hostName;            //主机名
    /**消息列表*/
    private List<IpmMessage> listIpmMessage = new ArrayList();
    /**保存全部的消息*/
    private List<IpmMessage> listAllMessage = new ArrayList();
    private List<SendFileInfo> listRecv = new ArrayList();			//接收文件列表
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAlias() {
        if(alias==null||"".equals(alias))
            return userName;
        else
            return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }


    /**
     * 讲数据包转换为用户列表VO
     * @param dp
     * @return
     */
	public static SingleUser changeDataPacket(DataPacket dp)
	{
		SingleUser uv = new SingleUser();
		uv.setUserName(dp.getSenderName());
		uv.setHostName(dp.getSenderHost());
		String[] buff = dp.getAdditional().split("\0");
		if (buff.length >= 2)
		{
			uv.setAlias(buff[0]);
			uv.setGroupName(buff[1]);
		} else
		{
			uv.setGroupName("对方未分组");
		}
		uv.setIp(dp.getIp());
		return uv;
	}

    public String[] toArray(){
        return new String[]{getAlias(),groupName,hostName,ip};
    }

	public void add(IpmMessage ipmMessage) 
	{
		if(listIpmMessage != null)
			listIpmMessage.add(ipmMessage);
		if(listAllMessage != null)
			listAllMessage.add(ipmMessage);
		SystemVar.db.insertMessage(ipmMessage.getText(), ipmMessage.getIp(),ipmMessage.getTime(),ipmMessage.getName());
	}

	public void addAllMessages(IpmMessage ipmMessage)
	{
		if(listAllMessage != null)
		{
			listAllMessage.add(ipmMessage);
		}
	}
	
	public void addRecvFile(SendFileInfo sendfileInfo)
	{
		if(listRecv != null)
			listRecv.add(sendfileInfo);
	}
	
	public void addSendFile(SendFileInfo sendFileInfo)
	{
		
	}
	
	public List<SendFileInfo> getRecvList()
	{
		return listRecv;
	}
	
	public List<IpmMessage> getMessageList()
	{
		return listAllMessage;
	}
	
	public List<IpmMessage> getTempMessageList()
	{
		return listIpmMessage;
	}
	
	public void cleanTempList()
	{
		if(listIpmMessage != null)
			listIpmMessage.clear();
	}
	
	public void cleanRecvList()
	{
		if(listRecv != null)
			listRecv.clear();
	}
	
	public void cleanAllList()
	{
		if(listAllMessage != null)
			listAllMessage.clear();
	}
}
