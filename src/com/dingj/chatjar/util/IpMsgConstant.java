package com.dingj.chatjar.util;

public class IpMsgConstant 
{
	 public final static int PACKET_LENGTH=1024;        //数据流大小限制

	    public final static int IPMSG_DEFAULT_PORT=0x0979;   //IPMSG默认端口号 2425

	    public final static char OS_LINUX=1;

	    public final static char OS_WINDOWS=0;
	    
	    public final static char OS_OTHER=2;	//android平台

	    public final static String IPMSG_VERSION="3.1.4";


	   /*=================================================================
	    1. Command & Option
	    ==================================================================*/

	    /*
	     * 1) Command functions (Low 8 bits from command number 32 bits)
	     */
	    public final static int IPMSG_NOOPERATION=0x00000000;       //No Operation
	    public final static int IPMSG_BR_ENTRY=0x00000001;          //用户上线Entry to service (Start-up with a Broadcast command)
	    public final static int IPMSG_BR_EXIT=0x00000002;		  	//用户下线Exit from service (End with a Broadcast command)
	    public final static int IPMSG_ANSENTRY=0x00000003;	  		//通报在线Notify a new entry
	    public final static int IPMSG_BR_ABSENCE=0x00000004;        //Change absence mode
	    public static final int IPMSG_STOPFILE = 0x000000e8;
	    
	    public final static int IPMSG_SENDMSG=0x00000020;        //Message transmission
	    public final static int IPMSG_RECVMSG=0x00000021;        //收到消息Message receiving check

	    public static final long IPMSG_GETFILEDATA = 0x00000060L;
		public static final long IPMSG_RELEASEFILES = 0x00000061L;
		public static final long IPMSG_GETDIRFILES = 0x00000062L;
		public static final long IPMSG_FILEATTACHOPT = 0x00200000L; 
		public static final long IPMSG_FILEATTACHANDENCRYPOPT = 0x00600000L;
		public static final long IPMSG_OPTFILEMASK = 0x00f00000L;

		// send opt
		public static final long IPMSG_SENDCHECKOPT = 0x00000100L;
		public static final long IPMSG_SECRETOPT = 0x00000200L;
		public static final long IPMSG_BROADCASTOPT = 0x00000400L;
		public static final long IPMSG_MULTICASTOPT = 0x00000800L;
		public static final long IPMSG_NOPOPUPOPT = 0x00001000L;
		public static final long IPMSG_AUTORETOPT = 0x00002000L;
		public static final long IPMSG_RETRYOPT = 0x00004000L;
		public static final long IPMSG_PASSWORDOPT = 0x00008000L;
		public static final long IPMSG_NOLOGOPT = 0x00020000L;
		public static final long IPMSG_NEWMUTIOPT = 0x00040000L;
		
	    /**
	     * 2) Option flag (High 24 bits from command number 32 bits)
	     */
}
