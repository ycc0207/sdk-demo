package com.epgis.offline.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.commons.utils.EncryptUtil;
import com.epgis.epgisapp.R;
import com.epgis.offline.DBException;
import com.epgis.offline.DownloadState;
import com.epgis.offline.OfflineMapCity;
import com.epgis.offline.controller.AllCityController;
import com.epgis.offline.controller.DownloadCityController;
import com.epgis.offline.controller.OfflineMapDownloadController;
import com.epgis.offline.db.AllCity;
import com.epgis.offline.db.DownloadCity;
import com.epgis.offline.service.DownloadService;
import com.epgis.offline.util.FileUtil;
import com.epgis.offline.util.NetworkUtil;

import java.io.File;
import java.util.List;

/**
 * Created by zhiwei on 2020/2/16.
 */

public class SearchCityListAdapter extends BaseAdapter {
	private List<OfflineMapCity> mapCityList;
	private LayoutInflater inflater;
	private Context mContext;

	public SearchCityListAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	public void setDataList(List<OfflineMapCity> offlineMapCityList){
		mapCityList = offlineMapCityList;
	}

	@Override
	public int getCount() {
		return mapCityList == null ? 0 : mapCityList.size();
	}

	@Override
	public Object getItem(int position) {
		return mapCityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView =  inflater.inflate(R.layout.offline_city_list_item_child, null);

			holder.cityListViewContainer = (RelativeLayout) convertView.findViewById(R.id.download_city_list_item_container);
			holder.tvCityName = (TextView) convertView.findViewById(R.id.tv_cityname);
			holder.tvMapSize = (TextView) convertView.findViewById(R.id.tv_map_size);
			holder.opreateStateImg = convertView.findViewById(R.id.img_operate);
			holder.tvOperator = (TextView) convertView.findViewById(R.id.tv_operate);
			holder.unDownLayout = convertView.findViewById(R.id.undown_layout);
			holder.opreateLayout = convertView.findViewById(R.id.operate_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		OfflineMapCity offlineMapCity = mapCityList.get(position);
		bindView(offlineMapCity, holder, position);
		return convertView;
	}

	private void bindView(OfflineMapCity cityItem, ViewHolder holder, int position){
		// ??????????????????
		holder.unDownLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cityItem == null) {
					return;
				}
				int cityState = cityItem.mStatus;
				switch (cityState) {
					// ???????????????????????????????????????????????????????????????????????????
					case DownloadState.state_undownload:
						if (NetworkUtil.checkNetwork(mContext) == 0) {
							Toast.makeText(mContext, "??????????????????????????????????????????", Toast.LENGTH_LONG).show();
						} else {
							// ?????????????????????????????????
							downloadBasePkg();
							//cityItem.setProCode(provinceList.get(groupPosition).getAdCode());
							Intent startIntent = new Intent(mContext, DownloadService.class);
							startIntent.setAction(DownloadService.ACTION_START);
							startIntent.putExtra("OfflineMapCity", cityItem);
							mContext.startService(startIntent);
						}
						break;
					case DownloadState.state_download_doing:
					case DownloadState.state_download_waiting:
					case DownloadState.state_download_pause:
					case DownloadState.state_download_completed:
						//????????????
						break;
					// ???????????????????????????????????????????????????????????????????????????
					case DownloadState.state_data_error:
						if (NetworkUtil.checkNetwork(mContext) == 0) {
							Toast.makeText(mContext, "??????????????????????????????????????????", Toast.LENGTH_LONG).show();
						} else {
							//cityItem.setProCode(provinceList.get(groupPosition).getAdCode());
							Intent startIntent = new Intent(mContext, DownloadService.class);
							startIntent.setAction(DownloadService.ACTION_ERROR);
							startIntent.putExtra("OfflineMapCity", cityItem);
							mContext.startService(startIntent);
						}
						break;
				}
			}
		});
		// 1??????????????????????????????
		holder.tvCityName.setText(cityItem.getCityName());
		double size = ((int) (cityItem.getMapSize() / 1024.0 / 1024.0 * 100)) / 100.0;
		holder.tvMapSize.setText(String.valueOf(size) + " M");
		if (cityItem.getMapSize() != 0) {
			// 2??? ????????????????????????
			holder.unDownLayout.setVisibility(View.GONE);
			holder.opreateLayout.setVisibility(View.GONE);
			holder.opreateStateImg.setImageResource(R.drawable.offline_state_undown);
			switch (cityItem.mStatus) {
				case DownloadState.state_undownload:
					//holder.tvOperator.setText("??????");
					holder.unDownLayout.setVisibility(View.VISIBLE);
					break;
				case DownloadState.state_download_doing:
					holder.tvOperator.setText("?????????");
					holder.opreateLayout.setVisibility(View.VISIBLE);
					break;
				case DownloadState.state_download_waiting:
					holder.tvOperator.setText("??????");
					holder.opreateLayout.setVisibility(View.VISIBLE);
					break;
				case DownloadState.state_download_pause:
					holder.tvOperator.setText("?????????");
					holder.opreateLayout.setVisibility(View.VISIBLE);
					break;
				case DownloadState.state_download_completed:
					holder.opreateStateImg.setImageResource(R.drawable.offline_state_down);
					holder.tvOperator.setText("?????????");
					holder.opreateLayout.setVisibility(View.VISIBLE);
					break;
				case DownloadState.state_data_error:
					holder.tvOperator.setText("??????");
					holder.opreateLayout.setVisibility(View.VISIBLE);
					break;
				case DownloadState.state_upgrade:
					holder.tvOperator.setText("?????????");
					holder.opreateLayout.setVisibility(View.VISIBLE);
					break;
				default:
					break;
			}
		}
	}

	public void downloadBasePkg() {
		if (mContext != null) {
			try {
				DownloadCity downloadCity = DownloadCityController.getInstance(mContext).getDownloadCityByCityId(OfflineMapDownloadController.getBaseMapAdCode());
				File file = new File(FileUtil.getAppSDCardMapPath(), EncryptUtil.getEncryptName(mContext, String.valueOf(OfflineMapDownloadController.getBaseMapAdCode())));
				// ???????????? ?????? ????????????????????? ?????? ??????????????????????????????
				if (!file.exists() || downloadCity == null ||
						(file.length() < downloadCity.getMapTotalSize() && downloadCity.getMapDownloadStatus() != DownloadState.state_download_doing)) {
					Toast.makeText(mContext, "??????????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
					AllCity allCity = AllCityController.getInstance(mContext).getCityByAdCode(OfflineMapDownloadController.getBaseMapAdCode());
					OfflineMapCity baseCity = new OfflineMapCity(allCity.getAdCode(), allCity.getCityName());
					baseCity.setMapVersion(allCity.getMapVersion());
					baseCity.setMapSize(allCity.getMapPkgSize());
					baseCity.setProCode(OfflineMapDownloadController.getBaseMapProAdCode());// ????????????????????????proCode
					Intent startIntent = new Intent(mContext, DownloadService.class);
					startIntent.setAction(DownloadService.ACTION_START);
					startIntent.putExtra("OfflineMapCity", baseCity);
					mContext.startService(startIntent);
				}
			} catch (DBException e) {
				e.printStackTrace();
			}
		}
	}

	// ?????????????????? UI
	public void unStart(OfflineMapCity fileBean) {
		if (fileBean.getProCode() != null) {
			for (OfflineMapCity data : mapCityList) {
				if (data.getAdCode().equals(fileBean.getAdCode())) {
					data.mStatus = DownloadState.state_undownload;
					notifyDataSetChanged();
					return;
				}
			}
		}
	}

	// ?????????????????? UI
	public void waiting(OfflineMapCity fileBean) {
		if (fileBean.getProCode() != null) {
			for (OfflineMapCity data : mapCityList) {
				if (data.getAdCode().equals(fileBean.getAdCode())) {
					data.mStatus = DownloadState.state_download_waiting;
					notifyDataSetChanged();
					return;
				}
			}
		}
	}

	// ?????????????????? UI
	public void start(OfflineMapCity fileBean) {
		if (fileBean.getProCode() != null) {
			for (OfflineMapCity data : mapCityList) {
				if (data.getAdCode().equals(fileBean.getAdCode())) {
					data.mStatus = DownloadState.state_download_doing;
					notifyDataSetChanged();
					return;
				}
			}
		}
	}

	// ?????????????????? UI
	public void pause(OfflineMapCity fileBean) {
		if (fileBean.getProCode() != null) {
			for (OfflineMapCity data : mapCityList) {
				if (data.getAdCode().equals(fileBean.getAdCode())) {
					data.mStatus = DownloadState.state_download_pause;
					notifyDataSetChanged();
					return;
				}
			}
		}
	}

	// ?????????????????? UI
	public void finished(OfflineMapCity fileBean) {
		if (fileBean.getProCode() != null) {
			for (OfflineMapCity data : mapCityList) {
				if (data.getAdCode().equals(fileBean.getAdCode())) {
					data.mStatus = DownloadState.state_download_completed;
					notifyDataSetChanged();
					return;
				}
			}
		}
	}

	// ?????????????????? UI
	public void error(OfflineMapCity fileBean) {
		if (fileBean.getProCode() != null) {
			for (OfflineMapCity data : mapCityList) {
				if (data.getAdCode().equals(fileBean.getAdCode())) {
					data.mStatus = DownloadState.state_data_error;
					notifyDataSetChanged();
					return;
				}
			}
		}
	}


	private class ViewHolder {
		public RelativeLayout cityListViewContainer; // Container
		public TextView tvCityName; // ????????????
		public TextView tvMapSize; // ????????????????????????????????????????????????

		public View unDownLayout;
		public View opreateLayout;

		public ImageView opreateStateImg;//??????
		public TextView tvOperator; // ???????????????????????????????????????????????????????????? ?????????
	}
}
