package com.ez.widget.filter.slide;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.ez.widget.R;
import com.ez.widget.alert.QzBaseSlideFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * @author : ezhuwx
 * Describe :侧滑时间选择组件
 * Designed on 2021/9/28 0028
 * E-mail : ezhuwx@163.com
 * Update on 16:47 by ezhuwx
 */
public class QzSlideDatePicker extends QzBaseSlideFragment {
    private final SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy", Locale.getDefault());
    private final SimpleDateFormat FORMAT_MONTH = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat FORMAT_HOUR = new SimpleDateFormat("yyyy-MM-dd HH", Locale.getDefault());
    private final SimpleDateFormat FORMAT_SECOND = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat FORMAT_MINUTE = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    LinearLayout startTimeLl;
    LinearLayout startTimeTitleLl;
    LinearLayout endTimeLl;
    TextView startTimeTv;
    TextView endTimeTv;
    TextView confirmTv;
    private OnTimePickFinishedListener pickFinishedListener;
    /**
     * 是否正确选择了时间
     */
    private boolean isSelectedRight = true;
    /**
     * 时间格式
     */
    private SimpleDateFormat format;
    /**
     * 开始时间选择器
     */
    private TimePickerView startTimePicker;
    /**
     * 截止时间选择器
     */
    private TimePickerView endTimePicker;
    /**
     * 是否格式化到秒
     */
    private boolean isFormatWithSecond;
    /**
     * 是否格式化到分
     */
    private boolean isFormatWithMinute;
    /**
     * 是否仅格式化到日
     */
    private boolean isFormatWithDay;
    /**
     * 是否格式化到月
     */
    private boolean isFormatWithMonth;
    /**
     * 是否格式化到月
     */
    private boolean isFormatWithYear;
    /**
     * 是否只显示截止时间
     */
    private boolean isOnlyShowEndTime;
    /**
     * 选择的开始时间
     */
    private String startTime;
    /**
     * 选择的截止时间
     */
    private String endTime;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_common_slide_date_picker;
    }

    @Override
    public void bindView(View v) {
        startTimeLl = v.findViewById(R.id.start_time_ll);
        startTimeTitleLl = v.findViewById(R.id.start_time_title_ll);
        endTimeLl = v.findViewById(R.id.end_time_ll);
        startTimeTv = v.findViewById(R.id.start_time_tv);
        endTimeTv = v.findViewById(R.id.end_time_tv);
        confirmTv = v.findViewById(R.id.confirm_tv);
        confirmTv.setOnClickListener(this::onClick);
        //日期格式
        format = isFormatWithSecond ? FORMAT_SECOND
                : isFormatWithMinute ? FORMAT_MINUTE
                : isFormatWithDay ? FORMAT_DAY
                : isFormatWithMonth ? FORMAT_MONTH
                : isFormatWithYear ? FORMAT_YEAR
                : FORMAT_HOUR;
        //是否只显示截止时间
        if (!isOnlyShowEndTime) {
            //开始时间初始化
            startTimePicker = getTimePicker(startTime, startTimeLl);
            startTimePicker.setKeyBackCancelable(false);
            startTimePicker.show(startTimeTv, false);
        } else {
            startTimeTitleLl.setVisibility(View.GONE);
            startTimeLl.setVisibility(View.GONE);
        }
        //结束时间初始化
        endTimePicker = getTimePicker(endTime, endTimeLl);
        endTimePicker.setKeyBackCancelable(false);
        endTimePicker.show(endTimeTv, false);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.confirm_tv) {
            if (pickFinishedListener != null) {
                //若开始时间显示则返回
                if (!isOnlyShowEndTime) {
                    startTimePicker.returnData();
                }
                //截止时间返回
                endTimePicker.returnData();
                //回调
                if (isSelectedRight) {
                    pickFinishedListener.onPickFinished(startTime, endTime);
                    dismiss();
                } else {
                    //重置选择成功标志
                    isSelectedRight = true;
                }
            }
        }
    }

    /**
     * 时间控件初始化
     */
    private TimePickerView getTimePicker(String currentTime, ViewGroup decorView) {
        //当前选中时间
        Calendar selectedCl = Calendar.getInstance();
        try {
            Date selectedDate;
            if (currentTime != null) {
                selectedDate = format.parse(currentTime);
            } else {
                selectedDate = new Date();
            }
            assert selectedDate != null;
            selectedCl.setTime(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //日期范围开始时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2015, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(new Date());
        //时间选择器
        return new TimePickerBuilder(requireContext(), this::onTimePicked)
                .setType(new boolean[]{true, !isFormatWithYear, !isFormatWithYear && !isFormatWithMonth,
                        !isFormatWithYear && !isFormatWithMonth && !isFormatWithDay,
                        isFormatWithMinute || isFormatWithSecond, isFormatWithSecond})
                .setLabel("年", "月", "日",
                        "时", "分", "秒")
                .isCenterLabel(true)
                .setDividerColor(requireContext().getResources().getColor(R.color.line_gray))
                .setBgColor(Color.parseColor("#F5F5F5"))
                .setContentTextSize(14)
                .setDate(selectedCl)
                .setOutSideCancelable(false)
                .setLayoutRes(R.layout.view_time_picker, v -> {

                })
                .setRangDate(startDate, endDate)
                .setDecorView(decorView)
                .build();
    }

    /**
     * 时间选择回调
     */
    private void onTimePicked(Date date, View view) {
        if (view == startTimeTv) {
            startTime = format.format(date.getTime());
        } else {
            if (isOnlyShowEndTime) {
                endTime = format.format(date);
            } else {
                try {
                    //开始时间
                    Date parse = format.parse(startTime);
                    long timeBegin = parse != null ? parse.getTime() : 0;
                    //结束时间
                    long timeEnd = date.getTime();
                    //截止时间必须大于开始时间
                    if (timeEnd > timeBegin) {
                        endTime = format.format(date);
                    } else {
                        isSelectedRight = false;
                        Toast.makeText(getContext(), getString(R.string.tip_end_time_mistake), Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    isSelectedRight = false;
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 设置默认选中时间
     *
     * @param startTime 开始时间
     * @param endTime   截止时间
     */
    public QzSlideDatePicker setCurrentDate(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        isOnlyShowEndTime = false;
        return this;
    }

    /**
     * 仅显示截止时间
     *
     * @param endTime 截止时间
     */
    public QzSlideDatePicker onlyShowEndTime(String endTime) {
        this.endTime = endTime;
        isOnlyShowEndTime = true;
        return this;
    }

    /**
     * 选择完成监听
     */
    public QzSlideDatePicker addPickFinishedListener(OnTimePickFinishedListener pickFinishedListener) {
        this.pickFinishedListener = pickFinishedListener;
        return this;
    }

    /**
     * 格式化到秒（默认到小时）
     */
    public QzSlideDatePicker formatWithSecond() {
        isFormatWithSecond = true;
        isFormatWithMinute = false;
        isFormatWithDay = false;
        isFormatWithMonth = false;
        isFormatWithYear = false;
        return this;
    }
    /**
     * 格式化到分（默认到小时）
     */
    public QzSlideDatePicker formatWithMinute() {
        isFormatWithSecond = false;
        isFormatWithMinute = true;
        isFormatWithDay = false;
        isFormatWithMonth = false;
        isFormatWithYear = false;
        return this;
    }

    /**
     * 格式化到日（默认到小时）
     */
    public QzSlideDatePicker formatWithDay() {
        isFormatWithSecond = false;
        isFormatWithMinute = false;
        isFormatWithDay = true;
        isFormatWithMonth = false;
        isFormatWithYear = false;
        return this;
    }

    /**
     * 格式化到月（默认到小时）
     */
    public QzSlideDatePicker formatWithMonth() {
        isFormatWithSecond = false;
        isFormatWithMinute = false;
        isFormatWithDay = false;
        isFormatWithMonth = true;
        isFormatWithYear = false;
        return this;
    }
    /**
     * 格式化到年（默认到小时）
     */
    public QzSlideDatePicker formatWithYear() {
        isFormatWithSecond = false;
        isFormatWithMinute = false;
        isFormatWithDay = false;
        isFormatWithMonth = false;
        isFormatWithYear = true;
        return this;
    }
    public interface OnTimePickFinishedListener {
        /**
         * 选择完成
         *
         * @param startTime 开始时间
         * @param endTime   结束时间
         */
        void onPickFinished(@Nullable String startTime, String endTime);
    }
}
