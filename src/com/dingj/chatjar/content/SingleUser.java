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
	/**用户名*/
	private String userName;            
	/**别名*/
    private String alias;               
    /**工作组名*/
    private String groupName;           
    /**IP地址*/
    private String ip;                  
    /**主机名*/
    private String hostName;
    /**消息列表*/
    private List<IpmMessage> listIpmMessage = new ArrayList();
    /**保存全部的消息*/
    private List<IpmMessage> listAllMessage = new ArrayList();
    /**接收文件列表*/
    private List<SendFileInfo> listRecv = new ArrayList();			
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
		if(dp.getAdditional() != null)
		{
			String[] buff = dp.getAdditional().split("\0");
			if (buff.length >= 2)
			{
				uv.setAlias(buff[0]);
				uv.setGroupName(buff[1]);
			} else
			{
				uv.setGroupName("对方未分组");
			}
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
		SystemVar.db.insertMessage(ipmMessage);//ipmMessage.getText(), ipmMessage.getIp(),ipmMessage.getTime(),ipmMessage.getName(),ipmMessage.getMod());
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
		if(listRecv != null && !listRecv.contains(sendfileInfo))
		{
			listRecv.add(sendfileInfo);
		}
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
