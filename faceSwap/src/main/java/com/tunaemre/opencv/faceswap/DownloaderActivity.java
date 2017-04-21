package com.tunaemre.opencv.faceswap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.pitt.library.fresh.FreshDownloadView;
import com.tunaemre.opencv.faceswap.app.ExtendedCompatActivity;
import com.tunaemre.opencv.faceswap.constant.Constant;
import com.tunaemre.opencv.faceswap.enumerator.DownloaderStatus;
import com.tunaemre.opencv.faceswap.util.CacheOperator;
import com.tunaemre.opencv.faceswap.util.DialogOperator;
import com.tunaemre.opencv.faceswap.util.NetworkOperator;
import com.tunaemre.opencv.faceswap.util.PermissionOperator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class DownloaderActivity extends ExtendedCompatActivity
{
	private FreshDownloadView downloadView = null;
	private Animation tweenAnimation = null;

	private FileDownloader fileDownloaderTask = null;
	
	private PermissionOperator permissionOperator = new PermissionOperator();

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_downloader);	
	}

	@Override
	protected void onDestroy()
	{
		if (fileDownloaderTask != null)
		{
			fileDownloaderTask.cancel(true);
			fileDownloaderTask = null;
		}

		super.onDestroy();
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		if (requestCode == PermissionOperator.REQUEST_STORAGE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			checkPermission();
		else if (requestCode == PermissionOperator.REQUEST_STORAGE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_DENIED)
		{
			DialogOperator.ShowConfirm(this, "Storage permission should be granded.", new Handler()
			{
				@Override
				public void dispatchMessage(Message msg)
				{
					checkPermission();
				}
			});
		}
	}
	
	@Override
	protected void prepareActivity()
	{
		downloadView = (FreshDownloadView) findViewById(R.id.downloadProgressView);
		tweenAnimation = AnimationUtils.loadAnimation(DownloaderActivity.this, R.anim.tween);
		downloadView.startAnimation(tweenAnimation);

		checkPermission();
	}
	
	private void checkPermission()
	{
		if (!permissionOperator.isStoragePermissionGranded(this))
		{
			permissionOperator.requestStoragePermission(this);
			return;
		}
		
		final Handler refreshConnections = new Handler()
		{
			public void handleMessage(Message msg)
			{
				if (NetworkOperator.CheckInternetConnection(DownloaderActivity.this)) downloadFaceLandmarksFile();
				else
					DialogOperator.ShowOfflineAlert(DownloaderActivity.this, this);
			};
		};
		
		DialogOperator.ShowConfirm(this, "Face Landmarks file will be downloaded.", new Handler()
		{
			@Override
			public void dispatchMessage(Message msg)
			{
				if (NetworkOperator.CheckInternetConnection(DownloaderActivity.this)) downloadFaceLandmarksFile();
				else
					DialogOperator.ShowOfflineAlert(DownloaderActivity.this, refreshConnections);
			}
		});
	}

	private void downloadFaceLandmarksFile()
	{
		downloadView.clearAnimation();
		downloadView.reset();

		((TextView) findViewById(R.id.txtDownload)).setText("Downloading");

		fileDownloaderTask = new FileDownloader(Constant.FaceLandmarksURL, Constant.FaceLandmarksDownloadPath, Constant.FaceLandmarksFileName);
	}

	private class FileDownloader extends AsyncTask<Void, Integer, DownloaderStatus>
	{
		private HttpURLConnection mConnection;

		private int mDownloadedByte = 0;
		private boolean mIsDownloading = true;

		private String mFileURL;
		private String mLocalPath;
		private String mLocalFileName;
		private File mLocalFile;

		private static final int MAX_BUFFER_SIZE = 256;

		private FileDownloader(String fileURL, String localPath, String localFileName)
		{
			this.mFileURL = fileURL;
			this.mLocalPath = localPath;
			this.mLocalFileName = localFileName;

			executeOnExecutor(THREAD_POOL_EXECUTOR);
		}

		@Override
		protected void onPreExecute()
		{
			if (downloadView != null) downloadView.reset();
		}

		@Override
		protected DownloaderStatus doInBackground(Void... param)
		{
			try
			{
				mLocalFile = new File(DownloaderActivity.this.getDir(mLocalPath, Context.MODE_PRIVATE),  mLocalFileName);
				if (mLocalFile.exists()) mLocalFile.delete();

				mLocalFile.createNewFile();
			}
			catch (Exception e)
			{
				e.printStackTrace();

				return DownloaderStatus.FileSaveError;
			}

			try
			{
				mConnection = (HttpURLConnection) new URL(mFileURL).openConnection();
				final double fileSize = mConnection.getContentLength();

				Log.e("File Size", String.valueOf(fileSize));

				OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(mLocalFile));
				mConnection.connect();

				InputStream inputStream = mConnection.getInputStream();

				while (mIsDownloading)
				{
					byte buffer[];

					if (fileSize - mDownloadedByte >= MAX_BUFFER_SIZE) buffer = new byte[MAX_BUFFER_SIZE];
					else
						buffer = new byte[(int) (fileSize - mDownloadedByte)];

					int read = inputStream.read(buffer);

					if (read == -1)
					{
						new Handler(Looper.getMainLooper()).post(new Runnable()
						{
							@Override
							public void run()
							{
								if (downloadView != null) downloadView.upDateProgress(100);
							}
						});

						break;
					}

					outputStream.write(buffer, 0, read);
					mDownloadedByte += read;

					new Handler(Looper.getMainLooper()).post(new Runnable()
					{
						@Override
						public void run()
						{
							if (downloadView != null) downloadView.upDateProgress((int) ((mDownloadedByte / fileSize) * 100));
						}
					});
				}

				if (mIsDownloading) mIsDownloading = false;

				outputStream.close();

				return DownloaderStatus.Success;

			}
			catch (Exception e)
			{
				e.printStackTrace();
				return DownloaderStatus.DownloadError;
			}
		}

		@Override
		protected void onCancelled()
		{
			if (mLocalFile.exists()) mLocalFile.delete();

			super.onCancelled();
		}

		@Override
		protected void onPostExecute(DownloaderStatus result)
		{
			fileDownloaderTask = null;
			
			if (result == DownloaderStatus.Success)
			{
				CacheOperator.getInstance(DownloaderActivity.this).setFaceLandmarksDownloaded(true);
				
				startActivity(new Intent(DownloaderActivity.this, MainActivity.class));
				finish();
			}
			else if (result == DownloaderStatus.DownloadError)
			{
				downloadView.showDownloadError();
				
				CacheOperator.getInstance(DownloaderActivity.this).setFaceLandmarksDownloaded(false);
				
				DialogOperator.ShowAlert(DownloaderActivity.this, "Connection error.", new Handler()
				{
					public void dispatchMessage(Message msg) {finish();};	
				});
			}
			else
			{
				downloadView.showDownloadError();
				
				CacheOperator.getInstance(DownloaderActivity.this).setFaceLandmarksDownloaded(false);
				
				DialogOperator.ShowAlert(DownloaderActivity.this, "File system error.", new Handler()
				{
					public void dispatchMessage(Message msg) {finish();};	
				});
			}
		}

	}

}
