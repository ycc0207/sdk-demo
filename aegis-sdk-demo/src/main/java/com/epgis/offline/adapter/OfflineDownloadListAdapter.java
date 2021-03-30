package com.epgis.offline.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.commons.utils.EncryptUtil;
import com.epgis.commons.utils.TextUtils;
import com.epgis.epgisapp.R;
import com.epgis.offline.DBException;
import com.epgis.offline.DownloadState;
import com.epgis.offline.OfflineMapCity;
import com.epgis.offline.controller.OfflineMapDownloadController;
import com.epgis.offline.fragment.OfflineMapFragment;
import com.epgis.offline.helper.ItemSlideHelper;
import com.epgis.offline.service.DownloadService;
import com.epgis.offline.service.EventMessage;
import com.epgis.offline.util.FileUtil;
import com.epgis.offline.util.NetworkUtil;
import com.epgis.offline.util.SharePreferenceUtil;
import com.epgis.offline.widget.PopupDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.epgis.offline.util.SharePreferenceUtil.ACTIVE_POSSIVE;

/**
 * Created by Lynn on 2019/3/15.
 */

public class OfflineDownloadListAdapter extends RecyclerView.Adapter<OfflineDownloadListAdapter.OfflineDownViewHolder>
        implements ItemSlideHelper.Callback{

    private ArrayList<OfflineMapCity> mTotal = new ArrayList<OfflineMapCity>();
    public static final String COLLET_ITEM_CLICK_TYPE_DEL = "DEL";
    public int mCityLastState = -1;
    public int mLastAdCode = -1;
    private OfflineMapFragment mFragment;
    private Context mAppContext;
    private Context mContext;
    private boolean isScrolling;
    private boolean isDelMore = false;
    // 字体按钮的样式
    //private int blueColor = 0xff333333; // 蓝色
    private int redColor = 0xffff4546; // 红色
    private int pause_grayColor = 0xff333333;// 暂停灰
    private int finish_grayColor = 0xff999999; // 已下载灰

    public static final int TYPE_DOWNLOADING_LIST = 0;//正在下载列表
    public static final int TYPE_DOWNLOAD_COMPLETE_LIST = 1;//下载完成列表
    private int currentTypeList = 0;

    private ItemClickListener mItemClickListener;
    private PopupDialog dialog;

    public void setOnItemCliclkListener(ItemClickListener itemCliclkListener) {
        this.mItemClickListener = itemCliclkListener;
    }

    public void setAdpaterType(int type){
        currentTypeList = type;
    }

    @Override
    public int getHorizontalRange(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof LinearLayout) {
            ViewGroup viewGroup = (ViewGroup) holder.itemView;
            //viewGroup包含3个控件，即消息主item、删除，返回为删除宽度
            int size = viewGroup.getChildAt(1).getLayoutParams().width;
            return size;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder getChildViewHolder(View childView) {
        return mRecyclerView.getChildViewHolder(childView);
    }

    @Override
    public View findTargetView(float x, float y) {
        return mRecyclerView.findChildViewUnder(x, y);
    }

    public interface onDownloadListListener {
        void onLoadData(boolean loadformDB, OfflineMapCity city);
    }

    private onDownloadListListener mDownloadListListener;

    public void setOnDownloadListListener(onDownloadListListener l) {
        this.mDownloadListListener = l;
    }

    public OfflineDownloadListAdapter(OfflineMapFragment fragment, Handler handler) {
        this.mFragment = fragment;
        this.mContext = fragment.getActivity();
        this.mAppContext = fragment.getActivity().getApplicationContext();
    }

    public void clearListData() {
        this.mTotal.clear();
    }

    public void setListData(ArrayList<OfflineMapCity> mDownloading) {
        if (mDownloading != null) {
            for (OfflineMapCity data : mDownloading) {
                if (currentTypeList == TYPE_DOWNLOADING_LIST) {
                    if (data.mStatus != DownloadState.state_download_completed) {
                        this.mTotal.add(data);
                    }
                }else if(currentTypeList == TYPE_DOWNLOAD_COMPLETE_LIST){
                    if (data.mStatus == DownloadState.state_download_completed) {
                        this.mTotal.add(data);
                    }
                }
            }
            setUnfinishWorkToSp(mDownloading);
        } else {
            this.mTotal = mDownloading;
        }
        notifyDataSetChanged();
    }

    public void notifyDownloadListChanged(final OfflineMapCity city) {
        if (!containCity(mTotal, city)) {
            this.mTotal.add(city);
        }
        notifyDataSetChanged();
    }

    /**
     * 通知下载完成UI变化
     * @param city
     */
    public void notifyDownCompleteListChanged(final OfflineMapCity city){
        if(currentTypeList == TYPE_DOWNLOADING_LIST) {
            if (containCity(mTotal, city)) {
                this.mTotal.remove(city);
                notifyDataSetChanged();
            }
        }else if(currentTypeList == TYPE_DOWNLOAD_COMPLETE_LIST){
            if (!containCity(mTotal, city)) {
                this.mTotal.add(city);
            }
        }
    }

    public void notifyDeleteListChanged(final OfflineMapCity city){
        if(containCity(mTotal, city)){
            this.mTotal.remove(city);
            notifyDataSetChanged();
        }
    }

    private boolean containCity(ArrayList<OfflineMapCity> mTotal, OfflineMapCity city) {
        if (mTotal != null && mTotal.size() > 0) {
            for (OfflineMapCity data : mTotal) {
                if (data.getAdCode().equals(city.getAdCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    public interface ItemClickListener {
        void onItemCliclk(String type, OfflineMapCity offlineMapCity, int position);
    }

    public void notifyUpdateTip(int state) {
        this.mFragment.updateDownloadTopTipState(state);
    }

    public void clearLastStatus() {
        mLastAdCode = -1;
        mCityLastState = -1;
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }

    public void setDelMore(boolean delMore) {
        isDelMore = delMore;
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    public void loadData(boolean loadformDB, OfflineMapCity city) {
        if (mDownloadListListener != null)
            mDownloadListListener.onLoadData(loadformDB, city);
    }

//    @Override
//    public int getCount() {
//        return mTotal.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mTotal.get(position);
//    }


    private RecyclerView mRecyclerView;
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.addOnItemTouchListener(new ItemSlideHelper(mAppContext, this));
    }

    @NonNull
    @Override
    public OfflineDownViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(currentTypeList == TYPE_DOWNLOADING_LIST ? R.layout.offline_download_list_item_child : R.layout.offline_download_complete_list_item_child, parent, false);
        return new OfflineDownViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfflineDownViewHolder holder, int position) {
        final OfflineMapCity cityItem = mTotal.get(position);
        bindChildView(cityItem, holder, position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mTotal.size();
    }

    private void bindChildView(final OfflineMapCity cityItem, final OfflineDownViewHolder holder, final int groupPosition) {
        // 0、绑定状态
        holder.operateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityItem == null) {
                    return;
                }
                int cityState = cityItem.mStatus;
                String prencet = "0.0%";
                double downloadedProgress = cityItem.getCompletedPercent();
                if (downloadedProgress == 0) {
                    prencet = "0.0%";
                }else {
                    prencet = formatStr(downloadedProgress) + "%";
                }
                switch (cityState) {
                    case DownloadState.state_undownload:
                        if (NetworkUtil.checkNetwork(mAppContext) == 0) {
                            Toast.makeText(mAppContext, "网络异常，请检查网络后重试！", Toast.LENGTH_LONG).show();
                        } else {
                            holder.tvOperatorAndDownSize.setText("等待" + prencet);
                            holder.tvOperatorAndDownSize.setTextColor(pause_grayColor);
                            //holder.tvOperator.setBackgroundResource(R.drawable.offline_download_btn_gray);

                            Intent startIntent = new Intent(mAppContext, DownloadService.class);
                            startIntent.setAction(DownloadService.ACTION_START);
                            startIntent.putExtra("OfflineMapCity", cityItem);
                            mAppContext.startService(startIntent);
                        }
                        break;
                    case DownloadState.state_download_doing:
                        holder.tvOperatorAndDownSize.setText("已暂停" + prencet);
                        holder.tvOperatorAndDownSize.setTextColor(redColor);
                        //holder.tvOperator.setBackgroundResource(R.drawable.offline_download_btn_blue);

                        Intent pauseIntent = new Intent(mAppContext, DownloadService.class);
                        pauseIntent.setAction(DownloadService.ACTION_PAUSE);
                        pauseIntent.putExtra("OfflineMapCity", cityItem);
                        mAppContext.startService(pauseIntent);
                        break;
                    case DownloadState.state_download_waiting:
                        holder.tvOperatorAndDownSize.setText("已暂停" + prencet);
                        holder.tvOperatorAndDownSize.setTextColor(redColor);
                        //holder.tvOperator.setBackgroundResource(R.drawable.offline_download_btn_blue);

                        Intent pauseIntent2 = new Intent(mAppContext, DownloadService.class);
                        pauseIntent2.setAction(DownloadService.ACTION_PAUSE);
                        pauseIntent2.putExtra("OfflineMapCity", cityItem);
                        mAppContext.startService(pauseIntent2);
                        break;
                    case DownloadState.state_download_pause:
                        if (NetworkUtil.checkNetwork(mAppContext) == 0) {
                            Toast.makeText(mAppContext, "网络异常，请检查网络后重试！", Toast.LENGTH_LONG).show();
                        } else {
                            holder.tvOperatorAndDownSize.setText("等待" + prencet);
                            holder.tvOperatorAndDownSize.setTextColor(pause_grayColor);
                            //holder.tvOperator.setBackgroundResource(R.drawable.offline_download_btn_gray);

                            Intent startIntent2 = new Intent(mAppContext, DownloadService.class);
                            startIntent2.setAction(DownloadService.ACTION_START);
                            startIntent2.putExtra("OfflineMapCity", cityItem);
                            mAppContext.startService(startIntent2);
                        }
                        break;
                    case DownloadState.state_upgrade:
                        if (NetworkUtil.checkNetwork(mAppContext) == 0) {
                            Toast.makeText(mAppContext, "网络异常，请检查网络后重试！", Toast.LENGTH_LONG).show();
                        } else {
                            holder.tvOperatorAndDownSize.setText("等待" + prencet);
                            holder.tvOperatorAndDownSize.setTextColor(pause_grayColor);
                            //holder.tvOperator.setBackgroundResource(R.drawable.offline_download_btn_gray);

                            Intent upgradeIntent = new Intent(mAppContext, DownloadService.class);
                            upgradeIntent.setAction(DownloadService.ACTION_UPGRADE);
                            upgradeIntent.putExtra("OfflineMapCity", cityItem);
                            mAppContext.startService(upgradeIntent);
                        }
                        break;
                    case DownloadState.state_download_completed:
                        //无法点击
                        break;
                    case DownloadState.state_data_error:
                        if (NetworkUtil.checkNetwork(mAppContext) == 0) {
                            Toast.makeText(mAppContext, "网络异常，请检查网络后重试！", Toast.LENGTH_LONG).show();
                        } else {
                            holder.tvOperatorAndDownSize.setText("等待" + prencet);
                            holder.tvOperatorAndDownSize.setTextColor(pause_grayColor);
                            //holder.tvOperator.setBackgroundResource(R.drawable.offline_download_btn_gray);

                            Intent errorIntent = new Intent(mAppContext, DownloadService.class);
                            errorIntent.setAction(DownloadService.ACTION_ERROR);
                            errorIntent.putExtra("OfflineMapCity", cityItem);
                            mAppContext.startService(errorIntent);
                        }
                        break;
                }
            }
        });
        //holder.updateLayout.setOnClickListener(onClickListener);
        // 传递位置参数
        holder.ivOperateMore.setTag(cityItem.getAdCode());
        holder.ivOperateMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v); //弹出底部自定义popup_window view
            }
        });
        holder.tvCityName.setTag(cityItem.getAdCode());
        holder.tvCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v); //弹出底部自定义popup_window view
            }
        });
//        holder.ivDelMore.setTag(groupPosition);
//        holder.ivDelMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog(v); //弹出底部自定义popup_window view
//            }
//        });
        // 1、刷新地图大小
        holder.tvCityName.setText(cityItem.getCityName());
        double size = ((int) (cityItem.getMapSize() / 1024.0 / 1024.0 * 100)) / 100.0;
        holder.tvMapSize.setText(String.valueOf(size) + " M");
        if (cityItem.getMapSize() != 0) {
            // 2、 刷新下载按钮状态
            holder.operateLayout.setVisibility(View.VISIBLE);
            holder.downloadLayout.setVisibility(View.VISIBLE);
            holder.updateLayout.setVisibility(View.INVISIBLE);
            //holder.ivDelMore.setVisibility(isDelMore ? View.VISIBLE : View.GONE);
            //holder.ivOperateMore.setVisibility(View.GONE);
            String prencet = "0.0%";
            switch (cityItem.mStatus) {
                case DownloadState.state_download_completed:
                    holder.operateLayout.setVisibility(View.INVISIBLE);
                    holder.ivProgress.setVisibility(View.INVISIBLE);
                    cityItem.setDownloadedSize(0L);
                    return;
                default:
                    //holder.tvDownloadSize.setVisibility(View.VISIBLE);
                    holder.operateLayout.setVisibility(View.VISIBLE);
                    holder.ivProgress.setVisibility(View.VISIBLE);
            }
            double downloadedProgress = cityItem.getCompletedPercent();
            if (downloadedProgress == 0) {
                holder.ivProgress.setProgress(0);
            }else {
                if (downloadedProgress >= 100) {
                    downloadedProgress = 100;
                } else if (downloadedProgress >= 99.5) {
                    checkIsDownloadComplete(cityItem, holder);
                }
                prencet = formatStr(downloadedProgress) + "%";
            }
            switch (cityItem.mStatus) {
                case DownloadState.state_undownload:
                    holder.tvOperatorAndDownSize.setText("下载" + prencet);
                    holder.tvOperatorAndDownSize.setTextColor(pause_grayColor);
                    break;
                case DownloadState.state_download_doing:
                    holder.tvOperatorAndDownSize.setText("下载中"+ prencet);
                    holder.tvOperatorAndDownSize.setTextColor(pause_grayColor);
                    break;
                case DownloadState.state_download_waiting:
                    holder.tvOperatorAndDownSize.setText("等待"+ prencet);
                    holder.tvOperatorAndDownSize.setTextColor(pause_grayColor);
                    break;
                case DownloadState.state_download_pause:
                    holder.tvOperatorAndDownSize.setText("已暂停"+ prencet);
                    holder.tvOperatorAndDownSize.setTextColor(redColor);
                    break;
                case DownloadState.state_download_completed:
//                    holder.tvOperator.setBackgroundResource(0);
//                    holder.tvOperator.setText("已下载");
//                    holder.tvOperator.setTextColor(finish_grayColor);
////                    if (!OfflineMapDownloadController.isBaseMap(cityItem.getAdCode())) {
//                    holder.tvOperator.setVisibility(View.GONE);
//                    holder.ivOperateMore.setVisibility(View.VISIBLE);
                    holder.operateLayout.setVisibility(View.INVISIBLE);
//                    }
                    break;
                case DownloadState.state_data_error:
                    holder.tvUpdateOrRetry.setText("重试");
                    holder.downloadLayout.setVisibility(View.INVISIBLE);
                    holder.updateLayout.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_upgrade:
                    holder.tvUpdateOrRetry.setText("有更新");
                    holder.downloadLayout.setVisibility(View.INVISIBLE);
                    holder.updateLayout.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
//            // 3、刷新status 和 进度条
//            if (holder.ivProgress == null) {
//                return;
//            }

            holder.ivProgress.setProgress((int)downloadedProgress);
        }
    }

    private void setOperaAndDownSize(){

    }

    /**
     * 判断是否已经下载完毕，防止下载进程跳跃
     *
     * @param offlineMapCity
     * @param holder
     */
    private void checkIsDownloadComplete(OfflineMapCity offlineMapCity, OfflineDownViewHolder holder) {
        if (offlineMapCity != null) {
            // 校验文件
            File file = new File(FileUtil.getAppSDCardMapPath(),
                    EncryptUtil.getEncryptName(mContext, String.valueOf(offlineMapCity.getAdCode())));
            if (file.exists() && file.length() >= offlineMapCity.getDownloadedSize()) {

                offlineMapCity.mStatus = DownloadState.state_download_completed;
                EventMessage message = new EventMessage(DownloadState.state_download_completed, offlineMapCity);
                EventBus.getDefault().post(message);

//                if (holder != null && holder.tvDownloadSize != null && holder.downloadProgress != null
//                        && holder.tvOperator != null && holder.ivOperateMore != null) {
//                    holder.tvDownloadSize.setVisibility(View.GONE);
//                    holder.downloadProgress.setVisibility(View.GONE);
//                    holder.tvOperator.setVisibility(View.GONE);
//                    holder.ivOperateMore.setVisibility(View.VISIBLE);
//                }
            }
        }
    }

    private void showDialog(View v) {
        if (dialog == null) {
            dialog = new PopupDialog(mContext, R.layout.app_download_popup_view).builder();
        }

        View view = dialog.getLayoutView();
        // 获取自定义Dialog布局中的控件
        TextView title = (TextView) view.findViewById(R.id.app_collect_popup_title);
        String name = "title";
        int position = -1;
        if (mTotal != null && !mTotal.isEmpty() && v.getTag() != null) {
            String adcode = v.getTag().toString();
            if(!TextUtils.isEmpty(adcode)) {
                int code = Integer.valueOf(adcode);
                for(int i = 0; i < mTotal.size(); i++){
                    OfflineMapCity city = mTotal.get(i);
                    if(code == city.getAdCode()){
                        position = i;
                        name = mTotal.get(position).getCityName();
                        break;
                    }
                }
            }
        }

        title.setText(name);

        TextView del = (TextView) view.findViewById(R.id.app_collect_popup_del);
        TextView cancel = (TextView) view.findViewById(R.id.app_collect_popup_cancel);

        del.setOnClickListener(new MyClickListener(position));
        cancel.setOnClickListener(new MyClickListener(position));
        if (dialog != null) {
            dialog.show();
        }
    }

    // 自定义点击接口
    class MyClickListener implements View.OnClickListener {

        int mPosition;

        private MyClickListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.app_collect_popup_del:
                    ItemClick(COLLET_ITEM_CLICK_TYPE_DEL);
                    break;
                case R.id.app_collect_popup_cancel:
                    ItemClick(null);
                    break;
            }
        }

        private void ItemClick(String type) {
            if (type != null && mTotal != null && !mTotal.isEmpty()) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemCliclk(type, mTotal.get(mPosition), mPosition);
                }
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    // 刷新下载进度 UI
    private long curTime = 0;

    public void updateProgress(OfflineMapCity fileBean) {
        for (OfflineMapCity data : mTotal) {
            if (data.getAdCode().equals(fileBean.getAdCode())) {
                data.setMapSize(fileBean.getMapSize());
                data.setDownloadedSize(fileBean.getDownloadedSize());
                data.mStatus = fileBean.mStatus;
                if (System.currentTimeMillis() - curTime > 1000) {
                    curTime = System.currentTimeMillis();
                    notifyDataSetChanged();
                }
                return;
            }
        }
    }

    // 刷新等待下载 UI
    public void waiting(OfflineMapCity fileBean) {
        for (OfflineMapCity data : mTotal) {
            if (data.getAdCode().equals(fileBean.getAdCode())) {
                data.mStatus = DownloadState.state_download_waiting;
                notifyDataSetChanged();
                return;
            }
        }
    }

    // 刷新开始下载 UI
    public void start(OfflineMapCity fileBean) {
        for (OfflineMapCity data : mTotal) {
            if (data.getAdCode().equals(fileBean.getAdCode())) {
                data.mStatus = DownloadState.state_download_doing;
                notifyDataSetChanged();
                return;
            }
        }
    }

    // 刷新暂停下载 UI
    public void pause(OfflineMapCity fileBean) {
        for (OfflineMapCity data : mTotal) {
            if (data.getAdCode().equals(fileBean.getAdCode())) {
                data.mStatus = DownloadState.state_download_pause;
                notifyDataSetChanged();
                return;
            }
        }
    }

    // 刷新下载结束 UI
    public void finished(OfflineMapCity fileBean) {
        for (OfflineMapCity data : mTotal) {
            if (data.getAdCode().equals(fileBean.getAdCode())) {
                data.mStatus = DownloadState.state_download_completed;
                notifyDataSetChanged();
                return;
            }
        }
    }

    // 刷新下载错误 UI
    public void error(OfflineMapCity fileBean) {
        for (OfflineMapCity data : mTotal) {
            if (data.getAdCode().equals(fileBean.getAdCode())) {
                data.mStatus = DownloadState.state_data_error;
                notifyDataSetChanged();
                return;
            }
        }
    }

    public OfflineMapCity deleDownload(OfflineMapCity offlineMapCity) {
        if (offlineMapCity != null) {
            // 删除文件
            File file = new File(FileUtil.getAppSDCardMapPath(), EncryptUtil.getEncryptName(mContext, String.valueOf(offlineMapCity.getAdCode())));
            if (file.exists()) {
                file.delete();
            }
            // 重置表的状态
            offlineMapCity.setDownloadedSize(0L);
            OfflineMapDownloadController mOfflineMapDownloadController = OfflineMapDownloadController.getInstance(mAppContext.getApplicationContext());
            if (mOfflineMapDownloadController != null) {
                try {
                    mOfflineMapDownloadController.downloadOfflineMapCity(offlineMapCity);
                    mOfflineMapDownloadController.deleteOfflineMapCity(offlineMapCity);
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
        }
        return offlineMapCity;
    }

    private static String formatStr(double progress) {
        DecimalFormat df = new DecimalFormat("0.0");
        String format = df.format(progress);
        return format;
    }

    // 更新全部
    public void updateAll() {
        Intent pauseIntent = new Intent(mAppContext, DownloadService.class);
        pauseIntent.setAction(DownloadService.ACTION_UPDATE_ALL);
        mAppContext.startService(pauseIntent);
    }

    // 下载全部
    public void downloadAll() {
        Intent pauseIntent = new Intent(mAppContext, DownloadService.class);
        pauseIntent.setAction(DownloadService.ACTION_DOWNLOAD_ALL);
        mAppContext.startService(pauseIntent);
    }

    // 暂停全部
    public void pauseAll() {
        Intent pauseIntent = new Intent(mAppContext, DownloadService.class);
        pauseIntent.setAction(DownloadService.ACTION_PAUSE_ALL);
        mAppContext.startService(pauseIntent);
    }

    // 刷新任务列表
    private volatile CopyOnWriteArraySet<String> PASSIVE_LISTS = new CopyOnWriteArraySet<>();

    private void setUnfinishWorkToSp(ArrayList<OfflineMapCity> mDownloading) {
        final Set<String> passiveSet = SharePreferenceUtil.getInstance(mAppContext).get(ACTIVE_POSSIVE);
        if (passiveSet != null && passiveSet.size() > 0) {
            Iterator iterator = passiveSet.iterator();
            while (iterator.hasNext()) {
                String tmp = (String) iterator.next();
                PASSIVE_LISTS.add(tmp);
            }
        }
        for (OfflineMapCity city : mDownloading) {
            if (!PASSIVE_LISTS.contains(city.getAdCode().toString()) && city.mStatus != DownloadState.state_download_completed) {
                PASSIVE_LISTS.add(city.getAdCode().toString());
            }
        }
        SharePreferenceUtil.getInstance(mAppContext).update(ACTIVE_POSSIVE, PASSIVE_LISTS);
    }

    public class OfflineDownViewHolder extends RecyclerView.ViewHolder {

        //public RelativeLayout cityListViewContainer; // Container
        public TextView tvCityName; // 城市名字
        //public TextView tvMapStatusAndSize; // 地图下载状态图标和离线地图的大小
        public TextView tvMapSize;//地图大小
        public TextView tvOperatorAndDownSize; // 操作按钮：下载、更新、暂停、重试、继续、 已下载
        //public TextView tvDownloadSize;// 离线下载 进度值

        public ProgressBar ivProgress;//进度条view
        //public View layoutProgressTxt;//文字进度的layout

        public TextView ivOperateMore; // 删除
        public View operateLayout;

        public View downloadLayout;
        public View updateLayout;
        public TextView tvUpdateOrRetry;

        public OfflineDownViewHolder(View view) {
            super(view);
            tvCityName = view.findViewById(R.id.tv_cityname);
            tvMapSize = view.findViewById(R.id.tv_map_size);
            tvOperatorAndDownSize = view.findViewById(R.id.tv_operate_and_progress);
            //tvDownloadSize = view.findViewById(R.id.tv_down_progress);
            ivProgress = view.findViewById(R.id.progress_bar_h);
            operateLayout = view.findViewById(R.id.operate_layout);
            ivOperateMore = view.findViewById(R.id.tv_msg_remind_delete);
            downloadLayout = view.findViewById(R.id.download_layout);
            updateLayout = view.findViewById(R.id.update_layout);
            tvUpdateOrRetry = view.findViewById(R.id.tv_update_or_retry);
        }
    }
}