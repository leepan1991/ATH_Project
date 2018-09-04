package cn.innovativest.ath.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.innovativest.ath.utils.LogUtils;

public class DownloadThread extends Thread {
	String mUrl, mFilePath;
	boolean mRun = true;
	DownloadListener mListener;

	public DownloadThread(String url, String filePath, DownloadListener l) {
		this.mUrl = url;
		this.mFilePath = filePath;
		mListener = l;
	}

	public void cancel() {
		mRun = false;
	}

	@Override
	public void run() {
		 doDownload1();
//		doDownload2();
	}

	private void doDownload1() {
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(mUrl);
			conn = (HttpURLConnection) url.openConnection();
			// conn.setReadTimeout(10 * 1000);
			// conn.setConnectTimeout(10 * 1000);
			// conn.setDoInput(true); // 允许输入流
			// conn.setDoOutput(true); // 允许输出流
			// conn.setUseCaches(false);// 设置不使用缓存:
			// conn.setRequestProperty("Connection", "Keep-Alive");
			// conn.setRequestMethod("GET");

			// 此处是post发送数据
			// OutputStream out = conn.getOutputStream();
			// out.write();
			// out.close();
			// 建立文件
			
			File file = new File(mFilePath);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
			}
			file.createNewFile();

			int responseCode = conn.getResponseCode();
			LogUtils.i( "responseCode = " + responseCode);
			if (responseCode == 200) {
				int fileLen = conn.getContentLength(); // 取得文件的长度
				
				fos = new FileOutputStream(file);
				bis = new BufferedInputStream(conn.getInputStream());
				byte[] buf = new byte[10 * 1024];
				int progress = 0;
				int bufLen = 0;
				while (mRun && (bufLen = bis.read(buf)) != -1) {
					progress += bufLen;
					fos.write(buf, 0, bufLen);
					if (mListener != null) {
						mListener.onCompleted(DownloadListener.state_loading, mUrl, mFilePath, fileLen, progress);
					}
				}
				if (mRun) {
					if (mListener != null) {
						mListener.onCompleted(DownloadListener.state_success, mUrl, mFilePath, fileLen, progress);
					}
				} else {
					if (mListener != null) {
						mListener.onCompleted(DownloadListener.state_failed, mUrl, mFilePath, fileLen, progress);
					}
				}
			} else {
				if (mListener != null) {
					mListener.onCompleted(DownloadListener.state_failed, mUrl, mFilePath, 0, 0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (mListener != null) {
				mListener.onCompleted(DownloadListener.state_failed, mUrl, mFilePath, 0, 0);
			}
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (bis != null) {
					bis.close();
				}
				if (conn != null) {
					conn.connect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doDownload2() {
		BufferedInputStream bis = null;
		HttpURLConnection conn = null;
		RandomAccessFile raf = null;
		try {
			URL url = new URL(mUrl);
			conn = (HttpURLConnection) url.openConnection();
			// 建立文件
			long localLength = 0;
			File file = new File(mFilePath);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			} else {
				localLength = file.length();
			}
			int responseCode = conn.getResponseCode();
			LogUtils.i( "responseCode1 = " + responseCode);
			if (responseCode == 200 ) {
				int serverLen = conn.getContentLength(); // 取得文件的长度
				LogUtils.i( "serverLen=" + serverLen + "   localLength=" + localLength);
				long seek = localLength;
				if (serverLen <= 0) {// 表示文件不支持断点下载
					seek = 0;
				} else {
					if (serverLen == localLength) {
						if (mListener != null) {
							mListener.onCompleted(DownloadListener.state_success, mUrl, mFilePath, serverLen, serverLen);
							return;
						}
					} else if (serverLen < localLength) {
						file.createNewFile();
						seek = 0;
					}
				}
				
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent", "NetFox");// User-Agent
				conn.setRequestProperty("RANGE", "bytes=" + seek + "-");// 设置断点位置

				responseCode = conn.getResponseCode();
				LogUtils.i( "responseCode2 = " + responseCode);
				if (responseCode == 200 || responseCode == 206 || responseCode == 416) {// 206表示断点续传。416表示请求范围不满足，可能已经是文件尾了。
					raf = new RandomAccessFile(file, "rw");
					raf.seek(seek);
					
					bis = new BufferedInputStream(conn.getInputStream());
					byte[] buf = new byte[10 * 1024];
					int progress = (int) seek;
					int bufLen = 0;
					while (mRun && (bufLen = bis.read(buf)) != -1) {
						progress += bufLen;
						raf.write(buf, 0, bufLen);
						if (mListener != null) {
							mListener.onCompleted(DownloadListener.state_loading, mUrl, mFilePath, serverLen, progress);
						}
					}
					if (mRun) {
						if (mListener != null) {
							mListener.onCompleted(DownloadListener.state_success, mUrl, mFilePath, serverLen, progress);
						}
					} else {
						if (mListener != null) {
							mListener.onCompleted(DownloadListener.state_failed, mUrl, mFilePath, serverLen, progress);
						}
					}
				} else {
					if (mListener != null) {
						mListener.onCompleted(DownloadListener.state_failed, mUrl, mFilePath, 0, 0);
					}
				}
			}else{
				if (mListener != null) {
					mListener.onCompleted(DownloadListener.state_failed, mUrl, mFilePath, 0, 0);
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
			if (mListener != null) {
				mListener.onCompleted(DownloadListener.state_failed, mUrl, mFilePath, 0, 0);
			}
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (conn != null) {
					conn.connect();
				}
				if (raf != null) {
					raf.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public interface DownloadListener {
		public static final int state_loading = 1;
		public static final int state_success = 2;
		public static final int state_failed = 3;

		void onCompleted(int state, String url, String path, long total, long progress);
	}
}
