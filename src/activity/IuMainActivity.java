package activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android_serialport_api.sample.R;
import utils.ActivityManager;
import view.BannerLayout;

@SuppressLint("HandlerLeak")
public class IuMainActivity extends BaseAcitivity implements View.OnClickListener {

	private TextView mTime;
	private TextView shebei;
	private TextView tv_sofeware_version;
	private TextView tvCountdownTime;
	private TextView clockView;
	boolean isRunning = false;
	private static final String DATE_FORMAT = "%02d:%02d:%02d";
	protected static final String tag = "IuMainActivity";
	private BannerLayout bannerLayout;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_mains);
		initViews();
		initData();
	}

	private void initViews() {
		tv_sofeware_version = (TextView) findViewById(R.id.tv_sofeware_version);
		tvCountdownTime = (TextView) findViewById(R.id.tv_countdown_time_main_activity);
		bannerLayout = (BannerLayout) findViewById(R.id.banner1);
		clockView = (TextView) findViewById(R.id.clock);
		mTime = (TextView) findViewById(R.id.current_time);
		shebei = (TextView) findViewById(R.id.devices_num);
		
		shebei.setText("设备号:" + utils.Util.getMid());
		
		ImageView imageView1 = (ImageView) findViewById(R.id.iv_goodsdetils_shopping);
		ImageView ivRefundableBlueSeed = (ImageView) findViewById(R.id.iv_goodsdetils_take_goods);
		ImageView checkout = (ImageView) findViewById(R.id.iv_goodsdetils_return);
		imageView1.setOnClickListener(this);
		ivRefundableBlueSeed.setOnClickListener(this);
		checkout.setOnClickListener(this);

		tv_sofeware_version.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(IuMainActivity.this, "返回桌面", Toast.LENGTH_SHORT).show();
				Intent home = new Intent(Intent.ACTION_MAIN);
				home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				home.addCategory(Intent.CATEGORY_HOME);
				startActivity(home);
				return false;
			}
		});
	}

	private void initData() {
		if (!utils.NetworkUtils.isNetworkAvailable(IuMainActivity.this)) {
			Toast.makeText(IuMainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
		List<Integer> res = new ArrayList<Integer>();
		res.add(R.drawable.viewpage_1);
		res.add(R.drawable.viewpage_2);
		res.add(R.drawable.viewpage_3);
		List<String> titles = new ArrayList<String>();
		titles.add(" ");
		titles.add(" ");
		titles.add(" ");
		if (bannerLayout != null) {
			bannerLayout.setViewRes(res, titles);
		}
		ActivityManager.getInstance().addActivity(IuMainActivity.this);
	}

	// 获得当前年月日时分秒星期
	public String getDateTime() {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "日";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		return mYear + "-" + String.format("%02d", Integer.parseInt(mMonth)) + "-"
				+ String.format("%02d", Integer.parseInt(mDay)) + "" + "  " + "星期" + mWay + "  ";
	}

	protected void downloadApk(String url) {
		// apk下载链接地址,放置apk的所在路径

		// 1,判断sd卡是否可用,是否挂在上
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// 2,获取sd路径
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
					+ "linlinySellApp.apk";
			// 3,发送请求,获取apk,并且放置到指定路径
			HttpUtils httpUtils = new HttpUtils();
			// 4,发送请求,传递参数(下载地址,下载应用放置位置)
			httpUtils.download(url, path, new RequestCallBack<File>() {
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					// 下载成功(下载过后的放置在sd卡中apk)
					File file = responseInfo.result;
					// 提示用户安装
					installApk(file);
				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// 下载失败
				}

				// 刚刚开始下载方法
				@Override
				public void onStart() {
					super.onStart();
				}

				// 下载过程中的方法(下载apk总大小,当前的下载位置,是否正在下载)
				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					super.onLoading(total, current, isUploading);
				}
			});
		}
	}

	/**
	 * 安装对应apk
	 * 
	 * @param file
	 *            安装文件
	 */
	protected void installApk(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivityForResult(intent, 0);
	}

	@Override
	public void changeTvTime(int time) {
		mTime.setText(getDateTime());
		tvCountdownTime.setText(getTime() + "s");
		final Date d = new Date();
		clockView.setText(String.format(DATE_FORMAT, d.getHours(), d.getMinutes(), d.getSeconds()));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_goodsdetils_shopping:
			Intent intent = new Intent(IuMainActivity.this, GoodsDetailsActivity.class);
			intent.putExtra("title", "购      物");
			startActivity(intent);
			IuMainActivity.this.finish();
			break;
		case R.id.iv_goodsdetils_take_goods:
			Intent intent1 = new Intent(IuMainActivity.this, quhuoActitvty.class);
			intent1.putExtra("title", "取      货");
			startActivity(intent1);
			break;
		case R.id.iv_goodsdetils_return:
			Intent intent11 = new Intent(IuMainActivity.this, BasketMainActitvty.class);
			intent11.putExtra("title", "退      篮");
			startActivity(intent11);
			IuMainActivity.this.finish();
			break;
		default:
			break;
		}
	}

}
