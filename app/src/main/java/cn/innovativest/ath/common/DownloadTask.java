package cn.innovativest.ath.common;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import cn.innovativest.ath.utils.LogUtils;

public class DownloadTask {
	public static final String ACTION_DOWNLOAD_LOADING = "action_download_loading";
	public static final String ACTION_DOWNLOAD_SUCCESS = "action_download_success";
	public static final String ACTION_DOWNLOAD_FAILED = "action_download_failed";

	private String mUrl, mFilePath;
	boolean mRun, mIsShowNotif;
	private Context mCtx;
	private DownloadListener ml;

	public DownloadTask(Context ctx, String url, String filePath, boolean isNotif, DownloadListener l) {
		mCtx = ctx;
		ml = l;
		mIsShowNotif = isNotif;
		this.mUrl = url;
		this.mFilePath = filePath;

	}

	public void cancel() {
		mRun = false;
	}

	public void start() {
		LogUtils.i( "mUrl = " + mUrl);
		new DownloadThread(mUrl, mFilePath, new DownloadThread.DownloadListener() {

			@Override
			public void onCompleted(int state, String url, String path, long total, long progress) {
				// LogUtil.i(
				// state+"  "+url+"  "+path+"  "+total+"  "+progress);
				switch (state) {
				case DownloadThread.DownloadListener.state_loading: {
					Intent i = new Intent(ACTION_DOWNLOAD_LOADING);
					i.putExtra("url", mUrl);
					i.putExtra("path", mFilePath);
					i.putExtra("total", total);
					i.putExtra("progress", progress);
					LocalBroadcastManager.getInstance(mCtx).sendBroadcast(i);
					if (ml != null) {
						ml.onDownloading(mUrl, total, progress);
					}
					break;
				}
				case DownloadThread.DownloadListener.state_success: {
					Intent i = new Intent(ACTION_DOWNLOAD_SUCCESS);
					i.putExtra("url", mUrl);
					i.putExtra("path", mFilePath);
					LocalBroadcastManager.getInstance(mCtx).sendBroadcast(i);
					if (ml != null) {
						ml.onComplete(mUrl, path);
					}
					break;
				}
				case DownloadThread.DownloadListener.state_failed: {
					Intent i = new Intent(ACTION_DOWNLOAD_FAILED);
					i.putExtra("url", mUrl);
					i.putExtra("path", mFilePath);
					LocalBroadcastManager.getInstance(mCtx).sendBroadcast(i);
					if (ml != null) {
						ml.onFailed(mUrl);
					}
					break;
				}
				}
			}
		}).start();
	}

	public static interface DownloadListener {
		void onComplete(String url, String localPath);

		void onDownloading(String url, long totalSize, long downSize);

		void onFailed(String url);
	}
}
