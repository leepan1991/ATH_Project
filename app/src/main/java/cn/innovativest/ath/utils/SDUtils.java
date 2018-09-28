package cn.innovativest.ath.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

import cn.innovativest.ath.common.AppConfig;

public class SDUtils {

    /**
     * SDCard 是否可用
     *
     * @return
     */
    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

//	/**
//	 * SDCard 是否可写 如果空白磁盘小于Constant.SDCARD_BLOCK_SIZE,将不可写
//	 * 
//	 * @return
//	 */
//	public static boolean isSDCardWritable() {
//		return isSDCardWritable(0);
//	}

//	/**
//	 * SDCard 是否可写 如果空白磁盘小于Constant.SDCARD_BLOCK_SIZE,将不可写
//	 * 
//	 * @param writeSize
//	 *            将要写入大小 单位 ：byte
//	 * @return
//	 */
//	public static boolean isSDCardWritable(long writeSize) {
//		if (isSDCardAvailable()) {
//			LogUtils.i("sdcard freeSize/totalSize= " + getSDCardSizeRate());
//			if (getSDCardBlockSize() - writeSize > AppConfig.SDCARD_BLOCK_SIZE)
//				return true;
//		}
//		return false;
//	}

    /**
     * SDCard 剩余大小 单位
     *
     * @return
     */
    public static long getSDCardBlockSize() {
        if (isSDCardAvailable()) {
            StatFs statfs = new StatFs(Environment
                    .getExternalStorageDirectory().getPath());
            long blockSize = statfs.getBlockSize();
            long availableBlocks = statfs.getAvailableBlocks();
            return blockSize * availableBlocks;
        }
        return 0;
    }

    /**
     * SDCard 总大小 单位
     *
     * @return
     */
    public static long getSDCardTotalSize() {
        if (isSDCardAvailable()) {
            StatFs statfs = new StatFs(Environment
                    .getExternalStorageDirectory().getPath());
            int blockSize = statfs.getBlockSize();
            long blockCount = statfs.getBlockCount();
            return blockSize * blockCount;
        }
        return 0;
    }

//	/**
//	 * SDCard 大小比例
//	 * 
//	 * @return
//	 */
//	public static String getSDCardSizeRate() {
//		return StringUtils.conversionBytesUnit(getSDCardBlockSize()) + "/"
//				+ StringUtils.conversionBytesUnit(getSDCardTotalSize());
//	}

    /**
     * 获得缓存文件大小
     *
     * @return
     */
    public static long getCacheSize() {
        return getFileSize(new File(AppConfig.ATH_CACHE_PATH));
    }

    /**
     * 获得文件大小
     *
     * @param filePath 缓存目录
     * @return
     */
    private static long getFileSize(File filePath) {
        long size = 0;
        if (!isSDCardAvailable() || !filePath.exists()) {
            LogUtils.e("SDCard不可用，或目标目录、文件不存在");
            return size;
        } else {
            File[] files = filePath.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    size += getFileSize(file);
                }
                size += file.length();
            }
        }
        return size;
    }

    /**
     * 删除过期缓存文件
     */
    public static void deleteExpiredCacheFile() {
        deleteExpiredFile(new File(AppConfig.ATH_CACHE_PATH), true,
                AppConfig.CACHE_EXPIRED_MILLISECONDS);
    }

    /**
     * @param path
     * @param isDirectory       是否包括path子目录下的文件
     * @param expiredTimeMillis 毫秒
     * @return
     */
    public static int deleteExpiredFile(File path, boolean isDirectory,
                                        long expiredTimeMillis) {
        int iRlt = 0;
        if (!isSDCardAvailable() || !path.exists()) {
            LogUtils.e("SDCard不可用，或目标目录、文件不存在");
            return iRlt;
        }
        long currentTime = System.currentTimeMillis();
        // 判断是否是目录
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (isDirectory) {
                            LogUtils.i("删除子目录下的过期文件：" + file.getAbsolutePath());
                            iRlt += deleteFile(file, isDirectory);
                        } else {
                            LogUtils.i("子目录下的过期文件不允许删除：" + file.getAbsolutePath());
                        }
                    } else {
                        if (currentTime - file.lastModified() > expiredTimeMillis) {
                            if (file.delete()) {
                                iRlt++;
                            } else {
                                LogUtils.i("删除过期文件失败：" + file.getAbsolutePath());
                            }
                        } else {
                            LogUtils.i("文件未过期：" + file.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            if (currentTime - path.lastModified() > expiredTimeMillis) {
                if (path.delete()) {
                    iRlt++;
                } else {
                    LogUtils.i("删除过期文件失败：" + path.getAbsolutePath());
                }
            } else {
                LogUtils.i("文件未过期：" + path.getAbsolutePath());
            }
        }
        LogUtils.i("删除" + path.getPath() + "下过期文件共：" + iRlt + "个");
        return iRlt;
    }

    /**
     * 删除缓存文件
     */
    public static void deleteCacheFile() {
        // 主目录
        deleteFile(new File(AppConfig.ATH_HOME_PATH), false);
        // 缓存目录
        deleteFile(new File(AppConfig.ATH_CACHE_PATH), true);
    }

    /**
     * @param path
     * @param isDirectory 是否包括path子目录下的文件
     * @return 删除文件个数
     */
    public static int deleteFile(File path, boolean isDirectory) {
        int iRlt = 0;
        if (!isSDCardAvailable() || !path.exists()) {
            LogUtils.e("SDCard不可用，或目标目录、文件不存在");
            return iRlt;
        }
        // 判断是否是目录
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    if (isDirectory) {
                        LogUtils.i("删除子目录下的文件：" + file.getAbsolutePath());
                        iRlt += deleteFile(file, isDirectory);
                    } else {
                        LogUtils.i("子目录下的文件不允许删除：" + file.getAbsolutePath());
                    }
                } else {
                    if (file.delete()) {
                        iRlt++;
                    } else {
                        LogUtils.i("删除文件失败：" + file.getAbsolutePath());
                    }
                }
            }
        } else {
            if (path.delete()) {
                iRlt++;
            } else {
                LogUtils.i("删除文件失败：" + path.getAbsolutePath());
            }
        }
        LogUtils.i("删除" + path.getPath() + "下文件共：" + iRlt + "个");
        return iRlt;
    }

    /**
     * 初始化缓存目录、下载目录、视频目录
     */
    public static void initAppDirectory() {
        createDirectory(new File(AppConfig.ATH_HOME_PATH), true);
        createDirectory(new File(AppConfig.ATH_CACHE_PATH), true);
        createDirectory(new File(AppConfig.ATH_DOWNLOAD_PATH), true);
        createDirectory(new File(AppConfig.ATH_CONFIG_PATH), true);
        createDirectory(new File(AppConfig.ATH_CACHE_PIC_PATH), true);
    }

    public static boolean createDirectory(File dir, boolean isMkDirs) {
        boolean bRlt = false;
        if (!isSDCardAvailable()) {
            LogUtils.e("SDCard不可用");
            return bRlt;
        }
        if (!dir.exists()) {
            if (isMkDirs) {
                bRlt = dir.mkdirs();
                LogUtils.i("支持创建多级目录,目录创建成功：" + bRlt);
            } else {
                bRlt = dir.mkdir();
                LogUtils.i("不支持创建多级目录,目录创建成功：" + bRlt);
            }
        } else {
            bRlt = false;
            LogUtils.i("已存在，无需创建:" + dir.getAbsolutePath());
        }
        return bRlt;
    }

}
