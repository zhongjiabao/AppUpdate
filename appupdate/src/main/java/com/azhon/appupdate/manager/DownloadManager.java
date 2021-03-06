package com.azhon.appupdate.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.azhon.appupdate.activity.PermissionActivity;
import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.dialog.UpdateDialog;
import com.azhon.appupdate.service.DownloadService;
import com.azhon.appupdate.utils.Constant;
import com.azhon.appupdate.utils.PermissionUtil;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.manager
 * 文件名:    DownloadManager
 * 创建时间:  2018/1/27 on 10:27
 * 描述:     TODO
 *
 * @author 阿钟
 */


public class DownloadManager {
    /**
     * 上下文
     */
    private Context context;
    /**
     * 要更新apk的下载地址
     */
    private String apkUrl = "";
    /**
     * 要更新apk的versionCode
     */
    private int apkVersionCode = 1;
    /**
     * apk下载好的名字 .apk 结尾
     */
    private String apkName = "";
    /**
     * apk 下载存放的位置
     */
    private String downloadPath;
    /**
     * 图通知栏的标 资源路径
     */
    private int smallIcon = -1;
    /**
     * 整个库的一些配置属性，可以从这里配置
     */
    private UpdateConfiguration configuration;

    private static DownloadManager manager;

    public static DownloadManager getInstance(Context context) {
        if (manager == null) {
            manager = new DownloadManager(context);
        }
        return manager;
    }

    /**
     * 供此依赖库自己使用.
     *
     * @return {@link DownloadManager}
     * @hide
     */
    public static DownloadManager getInstance() {
        if (manager == null) {
            throw new RuntimeException("请先调用 getInstance(Context context) !");
        }
        return manager;
    }

    private DownloadManager(Context context) {
        this.context = context;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public DownloadManager setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
        return this;
    }

    public int getApkVersionCode() {
        return apkVersionCode;
    }

    public DownloadManager setApkVersionCode(int apkVersionCode) {
        this.apkVersionCode = apkVersionCode;
        return this;
    }

    public String getApkName() {
        return apkName;
    }

    public DownloadManager setApkName(String apkName) {
        this.apkName = apkName;
        return this;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public DownloadManager setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        return this;
    }

    public int getSmallIcon() {
        return smallIcon;
    }

    public DownloadManager setSmallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    public DownloadManager setConfiguration(UpdateConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public UpdateConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * 开始下载
     */
    public void download() {
        if (checkParams()) {
            //检查权限
            if (!PermissionUtil.checkStoragePermission(context)) {
                //没有权限,去申请权限
                context.startActivity(new Intent(context, PermissionActivity.class));
                return;
            }
            context.startService(new Intent(context, DownloadService.class));
        } else {
            //显示升级对话框
            UpdateDialog dialog = new UpdateDialog(context);
            dialog.show();
        }
    }

    /**
     * 检查参数
     */
    private boolean checkParams() {
        if (TextUtils.isEmpty(apkUrl)) {
            throw new RuntimeException("apkUrl can not be empty!");
        }
        if (TextUtils.isEmpty(apkName)) {
            throw new RuntimeException("apkName can not be empty!");
        }
        if (!apkName.endsWith(Constant.APK_SUFFIX)) {
            throw new RuntimeException("apkName must endsWith .apk!");
        }
        if (TextUtils.isEmpty(downloadPath)) {
            throw new RuntimeException("downloadPath can not be empty!");
        }
        if (smallIcon == -1) {
            throw new RuntimeException("smallIcon can not be empty!");
        }
        //如果用户没有进行配置，则使用默认的配置
        if (configuration == null) {
            configuration = new UpdateConfiguration();
        }
        //设置了 VersionCode 则库中进行对话框逻辑处理
        if (apkVersionCode > 1) {
            return false;
        }
        return true;
    }

    /**
     * 释放资源
     */
    public void release() {
        manager = null;
    }
}
