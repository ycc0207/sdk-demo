package com.epgis.offline.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.commons.utils.EncryptUtil;
import com.epgis.epgisapp.R;
import com.epgis.offline.DBException;
import com.epgis.offline.DownloadState;
import com.epgis.offline.OfflineMapCity;
import com.epgis.offline.OfflineMapManager;
import com.epgis.offline.OfflineMapProvince;
import com.epgis.offline.controller.AllCityController;
import com.epgis.offline.controller.DownloadCityController;
import com.epgis.offline.controller.OfflineMapDownloadController;
import com.epgis.offline.db.AllCity;
import com.epgis.offline.db.DownloadCity;
import com.epgis.offline.fragment.OfflineMapFragment;
import com.epgis.offline.service.DownloadService;
import com.epgis.offline.util.FileUtil;
import com.epgis.offline.util.NetworkUtil;
import com.epgis.offline.widget.PopupDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lynn on 2019/3/15.
 */
public class OfflineAllCityListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener {
    private String TAG = "OfflineMapManager";
    private boolean isDebug = true;
    private boolean[] isOpen;// 记录一级目录是否打开
    private List<OfflineMapProvince> provinceList = null;
    private SparseArray<ArrayList<OfflineMapCity>> cityInMap = null;
    private OfflineMapManager offlineMapManager;
    private Context mContext;
    private Handler mHandler;
    public static final String ITEM_CLICK_TYPE_DOWNLOAD_ALL = "DOWNLOAD_ALL";

    public int mCityLastState = -1;
    public int mLastAdCode = -1;
    private boolean isScrolling;
    private OfflineMapFragment mFragment;
    // 字体按钮的样式
    private int blueColor = 0xff0091ff; // 蓝色
    private int BASE_MAP_AD_CODE = 100000;
    private int redColor = 0xffff4546; // 红色
    private int pause_grayColor = 0xff666666;// 暂停灰
    private int finish_grayColor = 0xff999999; // 已下载灰
    private ItemClickListener mItemClickListener;
    private PopupDialog dialog;

    public OfflineAllCityListAdapter(OfflineMapFragment fragment, Handler handler, List<OfflineMapProvince> provinceList, SparseArray<ArrayList<OfflineMapCity>> cityInMap) {
        this.mFragment = fragment;
        this.mContext = fragment.getActivity().getApplicationContext();
        this.provinceList = provinceList;
        this.cityInMap = cityInMap;

        isOpen = new boolean[provinceList.size()];
        mHandler = handler;
    }

    public interface ItemClickListener {
        void onItemCliclk(String type, int position);
    }

    public void setOnItemCliclkListener(ItemClickListener itemCliclkListener) {
        this.mItemClickListener = itemCliclkListener;
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    @Override
    public int getGroupCount() {
        return provinceList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        OfflineMapProvince province = provinceList.get(groupPosition);
        ArrayList<OfflineMapCity> cityList = cityInMap.get(province.getAdCode(), null);
        if (cityList == null) {
            return 0;
        } else {
            return cityList.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return provinceList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        OfflineMapProvince province = provinceList.get(groupPosition);
        ArrayList<OfflineMapCity> cityList = cityInMap.get(province.getAdCode(), null);
        if (cityList == null) {
            return null;
        } else {
            return cityList.get(childPosition);
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        TextView groupName, tvDownload, proviceMapSize;
        ImageView groupImage, download;
        LinearLayout proviceUnDownAllLayout, proviceDownAllLayout;
        if (convertView == null) {
            convertView = (RelativeLayout) RelativeLayout.inflate(mContext, R.layout.offline_download_list_item_group, null);
        }
        groupName = (TextView) convertView.findViewById(R.id.tv_group_name);
        download = (ImageView) convertView.findViewById(R.id.iv_group_image_download);
        tvDownload = (TextView) convertView.findViewById(R.id.tv_group_image_download);
        groupImage = (ImageView) convertView.findViewById(R.id.iv_group_image);
        proviceMapSize = convertView.findViewById(R.id.text_data_size);
        proviceUnDownAllLayout = convertView.findViewById(R.id.tv_group_image_undownload_layout);
        proviceDownAllLayout = convertView.findViewById(R.id.tv_group_image_download_layout);
        double size = ((int) (getTotalSize(provinceList.get(groupPosition)) / 1024.0 / 1024.0 * 100)) / 100.0;
        proviceMapSize.setText(String.valueOf(size) + " M");
        groupName.setText(provinceList.get(groupPosition).getProvinceName());
        proviceUnDownAllLayout.setVisibility(View.VISIBLE);
        proviceDownAllLayout.setVisibility(View.GONE);
        if (isOpen.length <= 0) {
            isOpen = new boolean[provinceList.size()];
        }
        if (isOpen[groupPosition]) {
            groupImage.setImageDrawable(mContext.getResources().getDrawable(
                    R.drawable.offlinearrow_down));
        } else {
            groupImage.setImageDrawable(mContext.getResources().getDrawable(
                    R.drawable.offlinearrow_right));
        }
        if (mContext != null && provinceList != null && provinceList.size() > 0 && provinceList.get(groupPosition) != null) {
            OfflineMapProvince province = provinceList.get(groupPosition);
            if (province != null && province.getAdCode() != -1) {
                ArrayList<OfflineMapCity> cityList = cityInMap.get(province.getAdCode(), null);
                boolean isFinish = true;
                boolean isDownLoading = false;
                for (OfflineMapCity city : cityList) {
                    if (city.mStatus != DownloadState.state_download_completed) {
                        if (city.mStatus == DownloadState.state_download_doing) {
                            isDownLoading = true;
                            if (!isFinish) break;
                        }
                        isFinish = false;
                    }
                }
                if (isFinish) {
                    proviceUnDownAllLayout.setVisibility(View.GONE);
                    proviceDownAllLayout.setVisibility(View.VISIBLE);
                } else {
                    if (!isDownLoading) {
                        proviceUnDownAllLayout.setEnabled(true);
                        download.setImageDrawable(mContext.getResources().getDrawable(R.drawable.offlinearrow_download));
                    } else {
                        proviceUnDownAllLayout.setEnabled(false);
                        download.setImageDrawable(mContext.getResources().getDrawable(R.drawable.offlinearrow_download));
                    }
                    proviceUnDownAllLayout.setVisibility(View.VISIBLE);
                    proviceDownAllLayout.setVisibility(View.GONE);
                }
            }
        }
        proviceUnDownAllLayout.setTag(groupPosition);
        proviceUnDownAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v);
            }
        });
        return convertView;
    }

        public Long getTotalSize(OfflineMapProvince offlineMapProvince) {
            Long mTotalSize = 0L;
            ArrayList<OfflineMapCity> mCitys = cityInMap.get(offlineMapProvince.getAdCode(), null);
            if (mCitys != null && mCitys.size() > 0) {
                for (OfflineMapCity city : mCitys)
                    mTotalSize += city.getMapSize();
            }
            return mTotalSize;
        }

    private void showDialog(View v) {
        if (dialog == null) {
            dialog = new PopupDialog(mFragment.getContext(), R.layout.app_download_popup_view).builder();
        }

        View view = dialog.getLayoutView();
        // 获取自定义Dialog布局中的控件
        TextView title = (TextView) view.findViewById(R.id.app_collect_popup_title);
        int position = -1;
        if (v.getTag() != null) {
            position = Integer.valueOf(v.getTag().toString());
            title.setText(provinceList.get(position).getProvinceName());
        }

        TextView del = (TextView) view.findViewById(R.id.app_collect_popup_del);
        del.setText("全部下载");
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
                    ItemClick(ITEM_CLICK_TYPE_DOWNLOAD_ALL);
                    break;
                case R.id.app_collect_popup_cancel:
                    ItemClick(null);
                    break;
            }
        }

        private void ItemClick(String type) {
            if (type != null) {
                mItemClickListener.onItemCliclk(type, mPosition);
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final OfflineAllCityViewHolder holder;
        final OfflineMapCity city = (OfflineMapCity) getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new OfflineAllCityViewHolder();
            convertView = (RelativeLayout) RelativeLayout.inflate(mContext, R.layout.offline_city_list_item_child, null);

            holder.cityListViewContainer = (RelativeLayout) convertView.findViewById(R.id.download_city_list_item_container);
            holder.tvCityName = (TextView) convertView.findViewById(R.id.tv_cityname);
            holder.tvMapSize = (TextView) convertView.findViewById(R.id.tv_map_size);
            holder.opreateStateImg = convertView.findViewById(R.id.img_operate);
            holder.tvOperator = (TextView) convertView.findViewById(R.id.tv_operate);
            holder.unDownLayout = convertView.findViewById(R.id.undown_layout);
            holder.opreateLayout = convertView.findViewById(R.id.operate_layout);
            convertView.setTag(holder);
        } else {
            holder = (OfflineAllCityViewHolder) convertView.getTag();
        }
        bindChildView(city, holder, groupPosition);
        return convertView;
    }

    private void bindChildView(final OfflineMapCity cityItem, final OfflineAllCityViewHolder holder, final int groupPosition) {
        // 绑定监听事件
        holder.unDownLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityItem == null) {
                    return;
                }
                int cityState = cityItem.mStatus;
                switch (cityState) {
                    // 开始和重试才允许点击，其他状态全部在下载管理中控制
                    case DownloadState.state_undownload:
                        if (NetworkUtil.checkNetwork(mContext) == 0) {
                            Toast.makeText(mContext, "网络异常，请检查网络后重试！", Toast.LENGTH_LONG).show();
                        } else {
                            // 需要优先判断下载基础包
                            if(isDebug) Log.i(TAG,"now click adcode:" + provinceList.get(groupPosition).getAdCode());
                            if(provinceList.get(groupPosition).getAdCode() != OfflineMapDownloadController.getBaseMapAdCode()){
                                downloadBasePkg();
                            }
                            cityItem.setProCode(provinceList.get(groupPosition).getAdCode());
                            Intent startIntent = new Intent(mContext, DownloadService.class);
                            startIntent.setAction(DownloadService.ACTION_START);
                            startIntent.putExtra("OfflineMapCity", cityItem);
                            mContext.startService(startIntent);
                            if(isDebug) Log.i(TAG,"click state_undownload:start DownloadService");
                        }
                        break;
                    case DownloadState.state_download_doing:
                    case DownloadState.state_download_waiting:
                    case DownloadState.state_download_pause:
                    case DownloadState.state_download_completed:
                        //无法点击
                        break;
                    // 开始和重试才允许点击，其他状态全部在下载管理中控制
                    case DownloadState.state_data_error:
                        if (NetworkUtil.checkNetwork(mContext) == 0) {
                            Toast.makeText(mContext, "网络异常，请检查网络后重试！", Toast.LENGTH_LONG).show();
                        } else {
                            cityItem.setProCode(provinceList.get(groupPosition).getAdCode());
                            Intent startIntent = new Intent(mContext, DownloadService.class);
                            startIntent.setAction(DownloadService.ACTION_ERROR);
                            startIntent.putExtra("OfflineMapCity", cityItem);
                            mContext.startService(startIntent);
                        }
                        break;
                }
            }
        });
        // 1、刷新地图名称和大小
        holder.tvCityName.setText(cityItem.getCityName());
        double size = ((int) (cityItem.getMapSize() / 1024.0 / 1024.0 * 100)) / 100.0;
        holder.tvMapSize.setText(String.valueOf(size) + " M");
        if (cityItem.getMapSize() != 0) {
            // 2、 刷新下载按钮状态
            holder.unDownLayout.setVisibility(View.GONE);
            holder.opreateLayout.setVisibility(View.GONE);
            holder.opreateStateImg.setImageResource(R.drawable.offline_state_undown);
            switch (cityItem.mStatus) {
                case DownloadState.state_undownload:
                    //holder.tvOperator.setText("下载");
                    holder.unDownLayout.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_download_doing:
                    holder.tvOperator.setText("下载中");
                    holder.opreateLayout.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_download_waiting:
                    holder.tvOperator.setText("等待");
                    holder.opreateLayout.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_download_pause:
                    holder.tvOperator.setText("已暂停");
                    holder.opreateLayout.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_download_completed:
                    holder.opreateStateImg.setImageResource(R.drawable.offline_state_down);
                    holder.tvOperator.setText("已下载");
                    holder.opreateLayout.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_data_error:
                    holder.tvOperator.setText("重试");
                    holder.opreateLayout.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_upgrade:
                    holder.tvOperator.setText("有更新");
                    holder.opreateLayout.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    public void downloadBasePkg() {
        if(mContext != null) {
            try {
                DownloadCity downloadCity = DownloadCityController.getInstance(mContext).getDownloadCityByCityId(OfflineMapDownloadController.getBaseMapAdCode());
                File file = new File(FileUtil.getAppSDCardMapPath(), EncryptUtil.getEncryptName(mContext, String.valueOf(OfflineMapDownloadController.getBaseMapAdCode())));
                // 从未下载 或者 不在下载列表中 或者 下载列表中但并未开始
                if (!file.exists() || downloadCity == null ||
                        (file.length() < downloadCity.getMapTotalSize() && downloadCity.getMapDownloadStatus() != DownloadState.state_download_doing)) {
                    Toast.makeText(mContext, "基础功能包是离线地图的基本依赖，需要优先下载", Toast.LENGTH_SHORT).show();
                    AllCity allCity = AllCityController.getInstance(mContext).getCityByAdCode(OfflineMapDownloadController.getBaseMapAdCode());
                    OfflineMapCity baseCity = new OfflineMapCity(allCity.getAdCode(), allCity.getCityName());
                    baseCity.setMapVersion(allCity.getMapVersion());
                    baseCity.setMapSize(allCity.getMapPkgSize());
                    baseCity.setProCode(OfflineMapDownloadController.getBaseMapProAdCode());// 插入基础功能包的proCode
                    Intent startIntent = new Intent(mContext, DownloadService.class);
                    startIntent.setAction(DownloadService.ACTION_START);
                    startIntent.putExtra("OfflineMapCity", baseCity);
                    mContext.startService(startIntent);
                    if(isDebug) Log.i(TAG,"downloadBasePkg start DownloadService");
                }
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        isOpen[groupPosition] = false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        isOpen[groupPosition] = true;
    }

    // 刷新下载进度 UI
    private long curTime = 0;

    public void updateProgress(OfflineMapCity fileBean) {
        if (fileBean.getProCode() != null) {
            for (OfflineMapCity data : cityInMap.get(fileBean.getProCode(), null)) {
                if (data.getAdCode().equals(fileBean.getAdCode())) {
                    data.setMapSize(fileBean.getMapSize());
                    data.setDownloadedSize(fileBean.getDownloadedSize());
                    data.mStatus = fileBean.mStatus;
                    if (fileBean.mStatus != DownloadState.state_download_completed && System.currentTimeMillis() - curTime > 1000) {
                        curTime = System.currentTimeMillis();
                        notifyDataSetChanged();
                    }
                    return;
                }
            }
        }
    }

    // 刷新下载错误 UI
    public void unStart(OfflineMapCity fileBean) {
        if (fileBean.getProCode() != null) {
            for (OfflineMapCity data : cityInMap.get(fileBean.getProCode(), null)) {
                if (data.getAdCode().equals(fileBean.getAdCode())) {
                    data.mStatus = DownloadState.state_undownload;
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    // 刷新等待下载 UI
    public void waiting(OfflineMapCity fileBean) {
        if (fileBean.getProCode() != null) {
            for (OfflineMapCity data : cityInMap.get(fileBean.getProCode(), null)) {
                if (data.getAdCode().equals(fileBean.getAdCode())) {
                    data.mStatus = DownloadState.state_download_waiting;
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    // 刷新开始下载 UI
    public void start(OfflineMapCity fileBean) {
        if (fileBean.getProCode() != null) {
            for (OfflineMapCity data : cityInMap.get(fileBean.getProCode(), null)) {
                if (data.getAdCode().equals(fileBean.getAdCode())) {
                    data.mStatus = DownloadState.state_download_doing;
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    // 刷新暂停下载 UI
    public void pause(OfflineMapCity fileBean) {
        if (fileBean.getProCode() != null) {
            for (OfflineMapCity data : cityInMap.get(fileBean.getProCode(), null)) {
                if (data.getAdCode().equals(fileBean.getAdCode())) {
                    data.mStatus = DownloadState.state_download_pause;
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    // 刷新下载结束 UI
    public void finished(OfflineMapCity fileBean) {
        if (fileBean.getProCode() != null) {
            for (OfflineMapCity data : cityInMap.get(fileBean.getProCode(), null)) {
                if (data.getAdCode().equals(fileBean.getAdCode())) {
                    data.mStatus = DownloadState.state_download_completed;
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    // 刷新下载错误 UI
    public void error(OfflineMapCity fileBean) {
        if (fileBean.getProCode() != null) {
            for (OfflineMapCity data : cityInMap.get(fileBean.getProCode(), null)) {
                if (data.getAdCode().equals(fileBean.getAdCode())) {
                    data.mStatus = DownloadState.state_data_error;
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }


    public class OfflineAllCityViewHolder {

        public RelativeLayout cityListViewContainer; // Container
        public TextView tvCityName; // 城市名字
        public TextView tvMapSize; // 地图下载状态图标和离线地图的大小

        public View unDownLayout;
        public View opreateLayout;

        public ImageView opreateStateImg;//状态
        public TextView tvOperator; // 操作按钮：下载、更新、暂停、重试、继续、 已下载
    }
}