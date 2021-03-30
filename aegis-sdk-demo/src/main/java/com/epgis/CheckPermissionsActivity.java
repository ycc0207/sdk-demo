package com.epgis;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;


import com.epgis.epgisapp.R;
import com.epgis.base.utils.BrandUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 继承了Activity，实现Android6.0的运行时权限检测
 * 需要进行运行时权限检测的Activity可以继承这个类
 *
 * @author yinbin.lu
 */
public class CheckPermissionsActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = CheckPermissionsActivity.class.getSimpleName();
    private AlertDialog mAlertDialog;
    private static final int PERMISSON_REQUESTCODE = 0;
    private static final int CODE_REQUEST_CAMERA_PERMISSIONS = 1;
    /**
     * 需要进行动态检测的权限数组（针对6.0+）
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private boolean isNeedCheck = true;// 判断是否需要检测，防止不停的弹框

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
        if (isNeedCheck) {
            Log.i(TAG, "Log消息：权限检测中...\n(6.0以上的系统请点击《获取定位》按钮进行授权)");
            checkPermissions(needPermissions);
        } else {
            Log.i(TAG, "Log消息：相关权限已授权");
        }
        }
    }

    /**
     * 6.0+ 动态权限申请检测----start----
     *
     * @param permissions 权限列表
     */
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
            List<String> needRequestPermissonList = findDeniedPermissions(permissions);
            if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
                ActivityCompat.requestPermissions(this, needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]), PERMISSON_REQUESTCODE);
            }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions 权限列表
     * @return 需要
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        }
        return needRequestPermissonList;
    }

    /**
     * 动态权限回调，判断是否授权；未授权提示用户跳转设置界面进行授权
     *
     * @param requestCode     请求码
     * @param permissions     权限列表
     * @param paramArrayOfInt 授权信息
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] paramArrayOfInt) {
        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                Log.i(TAG, "Log消息：权限未授权，请进入设置界面进行授权操作");
                showMissingPermissionDialog();
                isNeedCheck = true;
            } else {
                isNeedCheck = false;
                Log.i(TAG, "Log消息：权限授权通过，可获取定位");
            }
        }
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults 授权结果
     * @return 是否授权
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 吐司弹窗未授权提示信息
     */
    private void showMissingPermissionDialog() {
        if (mAlertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.notifyTitle);
            builder.setMessage(R.string.notifyMsg);
            // 拒绝, 退出应用
            builder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.setPositiveButton(R.string.setting,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            settingPermissionActivity(CheckPermissionsActivity.this);
                        }
                    });
            builder.setCancelable(false);
            mAlertDialog = builder.create();
        }
        if (mAlertDialog != null && !mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    /**
     * 动态权限申请检测----end----
     * 跳转应用的设置
     */
    public static void settingPermissionActivity(Activity activity) {
        //判断是否为小米系统
        if (TextUtils.equals(BrandUtils.getSystemInfo().getOs(), BrandUtils.SYS_MIUI)) {
            Intent miuiIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            miuiIntent.putExtra("extra_pkgname", activity.getPackageName());
            //检测是否有能接受该Intent的Activity存在
            List<ResolveInfo> resolveInfos = activity.getPackageManager().queryIntentActivities(miuiIntent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfos.size() > 0) {
                activity.startActivityForResult(miuiIntent, CODE_REQUEST_CAMERA_PERMISSIONS);
                return;
            }
        }
        //如果不是小米系统 则打开Android系统的应用设置页
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, CODE_REQUEST_CAMERA_PERMISSIONS);
    }

    /**
     * activity回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_REQUEST_CAMERA_PERMISSIONS) {
            //TODO something;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
