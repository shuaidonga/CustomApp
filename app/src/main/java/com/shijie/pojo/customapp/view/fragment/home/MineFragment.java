package com.shijie.pojo.customapp.view.fragment.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shijie.pojo.androidsdk.okhttp.listener.DisposeDataListener;
import com.shijie.pojo.androidsdk.utils.ImageLoaderUtil;
import com.shijie.pojo.customapp.R;
import com.shijie.pojo.customapp.activity.LoginActivity;
import com.shijie.pojo.customapp.activity.SettingActivity;
import com.shijie.pojo.customapp.constant.Constant;
import com.shijie.pojo.customapp.hybird.AboutActivity;
import com.shijie.pojo.customapp.hybird.H5VideoActivity;
import com.shijie.pojo.customapp.hybird.ShoppingActivity;
import com.shijie.pojo.customapp.manager.UserManager;
import com.shijie.pojo.customapp.module.update.UpdateModel;
import com.shijie.pojo.customapp.network.http.RequestCenter;
import com.shijie.pojo.customapp.service.update.UpdateService;
import com.shijie.pojo.customapp.share.ShareDialog;
import com.shijie.pojo.customapp.util.Util;
import com.shijie.pojo.customapp.view.dialog.CommonDialog;
import com.shijie.pojo.customapp.view.dialog.MyQrCodeDialog;
import com.shijie.pojo.customapp.view.fragment.BaseFragment;

import cn.sharesdk.framework.Platform;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 项目名: CustomApp
 * 包名: com.shijie.pojo.customapp.view.fragment.home
 * 创建者:  zsj
 * 创建事件: 2017/5/11 10:37
 * 描述:
 */

public class MineFragment extends BaseFragment implements View.OnClickListener {

    /**
     * UI
     */
    private View mContentView;
    private RelativeLayout mLoginLayout;
    private CircleImageView mPhotoView;
    private TextView mLoginInfoView;
    private TextView mLoginView;
    private RelativeLayout mLoginedLayout;
    private TextView mUserNameView;
    private TextView mTickView;
    private TextView mVideoPlayerView;
    private TextView mShareView;
    private TextView mQrCodeView;
    private TextView mUpdateView;
    private TextView mShoppingView;
    private TextView mVideoView;
    private TextView mAboutView;
    //自定义了一个广播接收器
    private LoginBroadcastReceiver receiver =
            new LoginBroadcastReceiver();

    public MineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        registerBroadcast();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_mine_layout, null, false);
        initView();
        return mContentView;
    }


    private void initView() {
        mLoginLayout = (RelativeLayout) mContentView.findViewById(R.id.login_layout);
        mLoginLayout.setOnClickListener(this);
        mLoginedLayout = (RelativeLayout) mContentView.findViewById(R.id.logined_layout);
        mLoginedLayout.setOnClickListener(this);

        mPhotoView = (CircleImageView) mContentView.findViewById(R.id.photo_view);
        mPhotoView.setOnClickListener(this);
        mLoginView = (TextView) mContentView.findViewById(R.id.login_view);
        mLoginView.setOnClickListener(this);
        mVideoPlayerView = (TextView) mContentView.findViewById(R.id.video_setting_view);
        mVideoPlayerView.setOnClickListener(this);
        mShareView = (TextView) mContentView.findViewById(R.id.share_imooc_view);
        mShareView.setOnClickListener(this);
        mQrCodeView = (TextView) mContentView.findViewById(R.id.my_qrcode_view);
        mQrCodeView.setOnClickListener(this);

        mLoginInfoView = (TextView) mContentView.findViewById(R.id.login_info_view);
        mUserNameView = (TextView) mContentView.findViewById(R.id.username_view);
        mTickView = (TextView) mContentView.findViewById(R.id.tick_view);

        mUpdateView = (TextView) mContentView.findViewById(R.id.update_view);
        mUpdateView.setOnClickListener(this);

        mShoppingView = (TextView) mContentView.findViewById(R.id.shopping_view);
        mShoppingView.setOnClickListener(this);

        mVideoView = (TextView) mContentView.findViewById(R.id.video_view);
        mVideoView.setOnClickListener(this);

        mAboutView = (TextView) mContentView.findViewById(R.id.about_view);
        mAboutView.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //根据用户信息更新我们的fragment
        if (UserManager.getInstance().hasLogined()) {
            if (mLoginedLayout.getVisibility() == View.GONE) {
                mLoginLayout.setVisibility(View.GONE);
                mLoginedLayout.setVisibility(View.VISIBLE);
                mUserNameView.setText(com.shijie.pojo.customapp.manager.UserManager.getInstance().getUser().data.name);
                mTickView.setText(UserManager.getInstance().getUser().data.tick);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_imooc_view:
                //分享Imooc课网址
                shareFriend();
                break;
            case R.id.login_layout:
            case R.id.login_view:
                //未登陆，则跳轉到登陸页面
                if (!UserManager.getInstance().hasLogined()) {
                    toLogin();
                }
                break;
            case R.id.my_qrcode_view:
                if (!UserManager.getInstance().hasLogined()) {
                    //未登陆，去登陆。
                    toLogin();
                } else {
                    //已登陆根据用户ID生成二维码显示
                    MyQrCodeDialog dialog = new MyQrCodeDialog(mContext);
                    dialog.show();
                }
                break;
            case R.id.video_setting_view:
                mContext.startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.update_view:
                if (hasPermission(Constant.WRITE_READ_EXTERNAL_PERMISSION)) {
                    checkVersion();
                } else {
                    requestPermission(Constant.WRITE_READ_EXTERNAL_CODE, Constant.WRITE_READ_EXTERNAL_PERMISSION);
                }
                break;
            case  R.id.shopping_view:
                startActivity(new Intent(mContext, ShoppingActivity.class));
                break;
            case  R.id.video_view:
                startActivity(new Intent(mContext, H5VideoActivity.class));
                break;
            case  R.id.about_view:
                startActivity(new Intent(mContext, AboutActivity.class));
                break;
        }
    }


    public void doWriteSDCard() {
        checkVersion();
    }

    /**
     * 去登陆页面
     */
    private void toLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 分享慕课网给好友
     */
    private void shareFriend() {
        ShareDialog dialog = new ShareDialog(mContext, false);
        dialog.setShareType(Platform.SHARE_IMAGE);
        dialog.setShareTitle("百度");
        dialog.setShareTitleUrl("http://www.baidu.com");
        dialog.setShareText("百度");
        dialog.setShareSite("imooc");
        dialog.setShareSiteUrl("http://www.baidu.com");
        dialog.setImagePhoto(Environment.getExternalStorageDirectory() + "/test2.jpg");
        dialog.show();
    }

    private void registerBroadcast() {

        IntentFilter filter =
                new IntentFilter(LoginActivity.LOGIN_ACTION);
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(receiver, filter);
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(mContext)
                .unregisterReceiver(receiver);
    }

    //发送版本检查更新请求
    private void checkVersion() {
        RequestCenter.checkVersion(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                final UpdateModel updateModel = (UpdateModel) responseObj;
                if (Util.getVersionCode(mContext) < updateModel.data.currentVersion) {
                    //说明有新版本,开始下载
                    CommonDialog dialog = new CommonDialog(mContext, getString(R.string.update_new_version),
                            getString(R.string.update_title), getString(R.string.update_install),
                            getString(R.string.cancel), new CommonDialog.DialogClickListener() {
                        @Override
                        public void onDialogClick() {
                            Intent intent = new Intent(mContext, UpdateService.class);
                            mContext.startService(intent);
                        }
                    });
                    dialog.show();
                } else {
                    //弹出一个toast提示当前已经是最新版本等处理
                    Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Object reasonObj) {

            }
        });
    }

    /**
     * 接收mina发送来的消息，并更新UI
     */
    private class LoginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.getInstance().hasLogined()) {
                //更新我们的fragment
                if (mLoginedLayout.getVisibility() == View.GONE) {
                    mLoginLayout.setVisibility(View.GONE);
                    mLoginedLayout.setVisibility(View.VISIBLE);
                    mUserNameView.setText(UserManager.getInstance().getUser().data.name);
                    mTickView.setText(UserManager.getInstance().getUser().data.tick);
                    ImageLoaderUtil.getInstance(mContext).displayImage(mPhotoView, UserManager.getInstance().getUser().data.photoUrl);
                }
            }
        }
    }
}
