package jding.debug;

import android.util.Log;

public class JDingDebug
{
	private static boolean debug = true;
	public static void printfD(String tag,String p)
	{
		if(debug == true)
			Log.d(tag, p);
	}
	
	public static void printfE(String tag,String p)
	{
		if(debug == true)
			Log.e(tag, p);
	}
	
	public static void printfSystem(String p)
	{
		if(debug == true)
			System.out.println(p);
	}
}
