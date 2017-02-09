package com.tunaemre.opencv.faceswap.util;

import com.tunaemre.opencv.faceswap.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;

public class DialogOperator
{
	public static void ShowAlert(final Activity activity, String message, final Handler handler)
	{
		new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.app_name))
		.setMessage(message)
		.setNeutralButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				handler.sendEmptyMessage(0);
			}
		})
		.setCancelable(false)
		.show();
	}
	
	public static void ShowConfirm(final Activity activity, String message, final Handler handler)
	{
		new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.app_name))
		.setMessage(message)
		.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				handler.sendEmptyMessage(0);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				activity.finish();
			}
		})
		.setCancelable(false)
		.show();
	}
	
	public static void ShowOfflineAlert(final Activity activity)
	{
		new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.app_name))
		.setMessage("Device is offline. Check your connection.")
		.setNeutralButton("Close", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				activity.finish();
			}
		})
		.setCancelable(false)
		.show();
	}

	public static void ShowOfflineAlert(final Activity activity, final Handler refreshHandler)
	{
		new AlertDialog.Builder(activity)
		.setTitle(activity.getString(R.string.app_name))
		.setMessage("Device is offline. Check your connection.")
		.setNegativeButton("Close", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				activity.finish();
			}
		})
		.setPositiveButton("Refresh", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				if (refreshHandler != null) refreshHandler.sendEmptyMessage(0);
			}
		})
		.setCancelable(false)
		.show();

	}
}
