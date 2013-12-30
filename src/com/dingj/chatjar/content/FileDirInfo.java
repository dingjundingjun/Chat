package com.dingj.chatjar.content;

/**
 * 该类用来保存文件夹传输时候单个文件内容
 * 文件夹传输格式  002f（消息长度）:11111111（文件名）:5c4f1f（文件大小）:2（文件类型）:14=52c0d655（创建日期）:16=52c0d655（修改日期）:
 * @author dingj
 *
 */
public class FileDirInfo
{
	private String path;
	/**消息长度*/
	private int packLenth;
	/**文件名*/
	private String name;
	private long size;
	/**文件类型  1:文件   2:文件夹*/
	private String property;
	/**文件创建日期*/
	private String property14;
	/**文件修改日期*/
	private String property16;
	public int getPackLenth()
	{
		return packLenth;
	}

	public String getName()
	{
		return name;
	}

	public long getSize()
	{
		return size;
	}

	public String getProperty()
	{
		return property;
	}

	/**
	 * 设置本条消息的长度
	 * @param packLenth
	 */
	public void setPackLenth(int packLenth)
	{
		this.packLenth = packLenth;
	}

	/**
	 * 设置文件名称
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 设置文件大小
	 * @param size
	 */
	public void setSize(long size)
	{
		this.size = size;
	}

	public void setProperty(String property)
	{
		this.property = property;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getProperty14()
	{
		return property14;
	}

	public String getProperty16()
	{
		return property16;
	}

	public void setProperty14(String property14)
	{
		this.property14 = property14;
	}

	public void setProperty16(String property16)
	{
		this.property16 = property16;
	}

}
