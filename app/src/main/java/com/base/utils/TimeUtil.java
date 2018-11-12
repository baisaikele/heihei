package com.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.text.TextUtils;
import android.util.Log;

public class TimeUtil {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final SimpleDateFormat userFaceFormat = new SimpleDateFormat("MMdd");
    static final SimpleDateFormat userFaceFormat2 = new SimpleDateFormat("MM/dd");
    static final SimpleDateFormat formatDynamic1 = new SimpleDateFormat("yyyy.MM.dd");
    static final SimpleDateFormat formatDynamic2 = new SimpleDateFormat("HH:mm");

    static final SimpleDateFormat formatDynamic3 = new SimpleDateFormat("EEEE HH:mm");

    static final SimpleDateFormat formatDynamic4 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    static final SimpleDateFormat formatDynamic5 = new SimpleDateFormat("MM月dd日 HH:mm");

    static final SimpleDateFormat formatMessage1 = new SimpleDateFormat("EEEE");

    static final SimpleDateFormat formatMessage2 = new SimpleDateFormat("yy/MM/dd");

    static final SimpleDateFormat _formatContacts = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat formatContacts = new SimpleDateFormat("MM月dd日");
    public static final SimpleDateFormat formatBirthday = new SimpleDateFormat("yyyy年MM月dd日");
    public static final SimpleDateFormat formatMd = new SimpleDateFormat("MM月dd日");
    public static final SimpleDateFormat dateFormatDeadline = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat dateFormatorder = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String TAG = "TimeUtil";

    public static final SimpleDateFormat redDateFormat1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    public static String parseTimeMillis(String time) throws Exception {

        return String.valueOf(dateFormat.parse(time).getTime());
    }

    private final static long minute = 60 * 1000;// 1 minute
    private final static long hour = 60 * minute;// 1 hour
    private final static long day = 24 * hour;// 1 day
    private final static long week = 7 * day;// 1 week
    private final static long month = 31 * day;// 1 month
    private final static long year = 12 * month;// 1 year

    /**
     * return the description of time period
     * 
     * @author Cross
     * @param date
     * @return
     */
    public static String getTimeFormatText(long a) {
        if (a == 0) {
            return "";
        }
        Date date = new java.util.Date(a);

        long diff = System.currentTimeMillis() - a;
        long r = 0;
        /*
         * if (diff > year) { r = (diff / year); if(r == 1){ return r + "year ago"; } return r + " years ago"; } if (diff > month) { r = (diff / month); if(r == 1){ return r + "month ago"; } return r + " months ago"; }
         */
        if (diff > year || diff > month) {
            return formatDynamic4.format(date);
        }

        if (diff > week) {
            /*
             * r = (diff / week); if (r == 1) { return r + "星期前"; }
             */
            return formatDynamic4.format(date);
        }

        if (diff > day) {
            r = (diff / day);
            if (r == 1) {
                return "昨天  " + formatDynamic2.format(date);
            }
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            if (r == 1) {
                return r + "小时前";
            }
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            if (r == 1) {
                return r + "分钟前";
            }
            return r + "分钟前";
        }
        return " 刚刚";
    }

    /**
     * @method: getUserFaceCacheTime
     * @2013-3-11 下午2:26:35
     * @TODO: 得到头像缓存时间
     * @return
     * @throws Exception
     */
    public static String getUserFaceCacheTime() {
        return userFaceFormat.format(new Date());
    }

    public static String getUserFaceCacheTime(long time) {
        return userFaceFormat2.format(time);
    }

    public static String getChatTime(String time) {
        try {
            return dateFormat.format(Long.parseLong(time));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getDynamicTime(String time) {
        if (!StringUtils.StrTxt(time)) {
            return "";
        }
        Long _time;
        if (time.length() != 13) {
            _time = (long) (Double.parseDouble(time) * 1000);
        } else {
            try {
                _time = Long.valueOf(time);
            } catch (NumberFormatException e) {
                _time = (long) (Double.parseDouble(time) * 1000);
            }
        }
        if (System.currentTimeMillis() - _time < 43200000) {
            return formatDynamic2.format(_time);
        } else {
            return formatDynamic1.format(_time);
        }
    }

    public static String getFormatContactsTime(String time) {
        if ("0".equals(time)) {
            return "";
        }
        try {
            return formatContacts.format(_formatContacts.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 目标时间与当前时间做15天范围计算
     * 
     * @param time
     *            目标时间 当传来的时间为null时， 导致无法判断时间间隔时，返回32，以使大于15天的判断
     */
    public static Integer getNearbyBirthdayTime(String time) {
        try {
            Date now = new Date();
            time = Calendar.getInstance().get(Calendar.YEAR) + "年" + time;
            Date cDate = formatBirthday.parse(time);
            long timeSpan = (cDate.getTime() - now.getTime()) / (24 * 60 * 60 * 1000);
            if (timeSpan < 0)
                timeSpan += 365;
            return (int) timeSpan;
        } catch (ParseException e) {
        }
        return 32;
    }

    /**
     * format by pattern
     * 
     * @param obj
     * @return
     */
    public static String format(String pattern, Object obj) {
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat(pattern);
            if (obj instanceof String || obj instanceof Integer || obj instanceof Float)
                return sdf.format(Double.parseDouble(String.valueOf(obj)) * 1000);
            else if (obj instanceof Date)
                return sdf.format(obj);
            return sdf.format(0);
        } catch (NumberFormatException e) {
        }
        return "null";
    }

    /**
     * format by pattern 传过来的如果是string类型，要求是13位。
     * 
     * @param obj
     * @return
     */
    public static String format13count(String pattern, Object obj) {
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat(pattern);
            if (obj instanceof String || obj instanceof Integer || obj instanceof Float || obj instanceof Long)
                return sdf.format(Double.parseDouble(String.valueOf(obj)));
            else if (obj instanceof Date)
                return sdf.format(obj);
            return sdf.format(0);
        } catch (NumberFormatException e) {
        }
        return "null";
    }

    /**
     * format by pattern 传过来的如果是string类型，要求是13位。
     * 
     * @param obj
     * @return
     */
    public static String format13count(SimpleDateFormat sdf, Object obj) {
        try {
            if (obj instanceof String || obj instanceof Integer || obj instanceof Float || obj instanceof Long)
                return sdf.format(Double.parseDouble(String.valueOf(obj)));
            else if (obj instanceof Date)
                return sdf.format(obj);
            return sdf.format(0);
        } catch (NumberFormatException e) {
        }
        return "null";
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    public static String getDateforWeek(Date dt) {
        String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * @param dt
     * @return 1234567
     */
    public static int getWeekDayByDate(Date dt) {
        Integer[] weekDays = { 7, 1, 2, 3, 4, 5, 6 };
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 如果是聊天信息，时间展示有所不同；聊天类显示星期，非聊天不显示
     * 
     * @param time
     * @param chat
     * @return
     */
    public static String getFriendlyTime(String time, boolean chat) {
        try {
            if (!StringUtils.StrTxt(time)) {
                return "";
            }

            long ltime = StringUtils.getDToL(time) * 1000;// 毫秒//1422249824 1422249824000
            Date date = null;
            if (ltime > new Date().getTime()) {
                date = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
            } else {
                date = new Date(ltime);
            }
            String ftime = "";
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(date);
            // // 判断是否是同一天
            // String curDate = formatDynamic1.format(cal.getTime());
            // String paramDate = formatDynamic1.format(date);
            // if (curDate.equals(paramDate)) {
            // int hour = (int) ((cal.getTimeInMillis() - date.getTime()) /
            // 3600000);
            // if (hour == 0) {
            // long millis = Math.max((cal.getTimeInMillis() - date.getTime()) /
            // 60000,1);
            // if (millis < 5) {
            // ftime = "刚刚";
            // } else {
            // ftime = millis + "分钟前";
            // }
            // } else {
            // ftime = hour + "小时前";
            // }
            // return ftime;
            // }
            long lt = date.getTime() / 86400000;
            long ct = cal.getTimeInMillis() / 86400000;// 有误差，不知道起因
            int days = (int) (ct - lt);
            int curYear = cal.get(Calendar.YEAR);
            int dateYear = dateCal.get((Calendar.YEAR));
            int curDayInYear = cal.get(Calendar.DAY_OF_YEAR);
            int dateDayInYear = dateCal.get(Calendar.DAY_OF_YEAR);
            // 调节days为0或1的误差
            if (curYear == dateYear) // 同年则用日期来调相差天数，不用计算的方式
            {
                days = curDayInYear - dateDayInYear;
            }

            if (days == 0) {
                int hour = (int) ((cal.getTimeInMillis() - date.getTime()) / 3600000);
                long minutes = Math.max((cal.getTimeInMillis() - date.getTime()) / 60000, 1) % 60;
                if (hour == 0) {
                    if (minutes < 5) {
                        ftime = "刚刚";
                    } else {
                        ftime = minutes + "分钟前";
                    }
                } else {
                    if (minutes > 0)
                        ftime = hour + "小时" + minutes + "分钟前";
                    else ftime = hour + "小时前";
                }
            } else if (days == 1) {
                ftime = "昨天" + formatDynamic2.format(date);
            } else {
                if (chat) {
                    // 超过一年不显示时,分;聊天类信息展示星期几（同一年应该用年份来判断，不用365天）
                    ftime = formatMd.format(date) + " " + getWeekOfDate(date)
                            + (((curYear - dateYear) == 0) ? " " + formatDynamic2.format(date) : "");
                } else {
                    // 超过一年不显示时,分；展示类型信息不显示星期几（同一年应该用年份来判断，不用365天）
                    ftime = formatMd.format(date)
                            + (((curYear - dateYear) == 0) ? " " + formatDynamic2.format(date) : "");

                }
            }

            return ftime;
        } catch (Exception e) {
        }

        return "";
    }

    public static String getMdTime(String time) {
        try {
            if (!StringUtils.StrTxt(time)) {
                return "";
            }
            Long timeLong = StringUtils.removePointbig10000ToLong(time);// 将所有的时间都转为14位的Long类型，即毫秒的下一位
            time = timeLong + "";
            long ltime = Long.parseLong(time) / 10;// 毫秒
            Date date = null;
            if (ltime > new Date().getTime()) {
                date = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
            } else {
                date = new Date(ltime);
            }

            return formatMd.format(date);
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * 如果是聊天信息，时间展示有所不同；聊天类显示星期，非聊天不显示
     * 
     * @param time
     * @param chat
     * @return
     */
    public static String getFriendlyTimeDe10(String time, boolean chat) {
        try {
            if (!StringUtils.StrTxt(time)) {
                return "";
            }
            Long timeLong = StringUtils.removePointbig10000ToLong(time);// 将所有的时间都转为14位的Long类型，即毫秒的下一位
            time = timeLong + "";
            long ltime = Long.parseLong(time) / 10;// 毫秒
            Date date = null;
            if (ltime > new Date().getTime()) {
                date = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
            } else {
                date = new Date(ltime);
            }
            String ftime = "";
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(date);
            // // 判断是否是同一天
            // String curDate = formatDynamic1.format(cal.getTime());
            // String paramDate = formatDynamic1.format(date);
            // if (curDate.equals(paramDate)) {
            // int hour = (int) ((cal.getTimeInMillis() - date.getTime()) /
            // 3600000);
            // if (hour == 0) {
            // long millis = Math.max((cal.getTimeInMillis() - date.getTime()) /
            // 60000,1);
            // if (millis < 5) {
            // ftime = "刚刚";
            // } else {
            // ftime = millis + "分钟前";
            // }
            // } else {
            // ftime = hour + "小时前";
            // }
            // return ftime;
            // }
            long lt = date.getTime() / 86400000;
            long ct = cal.getTimeInMillis() / 86400000;// 有误差，不知道起因
            int days = (int) (ct - lt);
            int curYear = cal.get(Calendar.YEAR);
            int dateYear = dateCal.get((Calendar.YEAR));
            int curDayInYear = cal.get(Calendar.DAY_OF_YEAR);
            int dateDayInYear = dateCal.get(Calendar.DAY_OF_YEAR);
            // 调节days为0或1的误差
            if (curYear == dateYear) // 同年则用日期来调相差天数，不用计算的方式
            {
                days = curDayInYear - dateDayInYear;
            }

            if (days == 0) {
                int hour = (int) ((cal.getTimeInMillis() - date.getTime()) / 3600000);
                long minutes = Math.max((cal.getTimeInMillis() - date.getTime()) / 60000, 1) % 60;
                if (hour == 0) {
                    if (minutes < 5) {
                        ftime = "刚刚";
                    } else {
                        ftime = minutes + "分钟前";
                    }
                } else {
                    if (minutes > 0)
                        ftime = hour + "小时" + minutes + "分钟前";
                    else ftime = hour + "小时前";
                }
            } else if (days == 1) {
                ftime = "昨天" + formatDynamic2.format(date);
            } else {
                if (chat) {
                    // 超过一年不显示时,分;聊天类信息展示星期几（同一年应该用年份来判断，不用365天）
                    ftime = formatMd.format(date) + " " + getWeekOfDate(date)
                            + (((curYear - dateYear) == 0) ? " " + formatDynamic2.format(date) : "");
                } else {
                    // 超过一年不显示时,分；展示类型信息不显示星期几（同一年应该用年份来判断，不用365天）
                    ftime = formatMd.format(date)
                            + (((curYear - dateYear) == 0) ? " " + formatDynamic2.format(date) : "");

                }
            }

            return ftime;
        } catch (Exception e) {
        }

        return "";
    }

    public static String getTimeWithWeek(String time, boolean hasWeek) {
        try {
            if (!StringUtils.StrTxt(time)) {
                return "";
            }
            Long timeLong = StringUtils.removePointbig10000ToLong(time);// 将所有的时间都转为14位的Long类型，即毫秒的下一位
            time = timeLong + "";
            long ltime = Long.parseLong(time) / 10;// 毫秒
            Date date = new Date(ltime);
            String ftime = "";
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(date);
            long lt = date.getTime() / 86400000;
            long ct = cal.getTimeInMillis() / 86400000;// 有误差，不知道起因
            int days = (int) (ct - lt);
            int curYear = cal.get(Calendar.YEAR);
            int dateYear = dateCal.get((Calendar.YEAR));
            int curDayInYear = cal.get(Calendar.DAY_OF_YEAR);
            int dateDayInYear = dateCal.get(Calendar.DAY_OF_YEAR);
            Log.e("jiyu", "days:  " + days);
            // 调节days为0或1的误差
            if (curYear == dateYear) // 同年则用日期来调相差天数，不用计算的方式
            {
                days = curDayInYear - dateDayInYear;
            }
            if (hasWeek) {
                if (curYear == dateYear) {
                    ftime = formatMd.format(date) + " " + "(" + getDateforWeek(date) + ")"
                            + (((curYear - dateYear) == 0) ? " " + formatDynamic2.format(date) : "");
                } else {
                    ftime = formatBirthday.format(date) + " " + "(" + getDateforWeek(date) + ")"
                            + (((curYear - dateYear) == 0) ? " " + formatDynamic2.format(date) : "");
                }

            } else if (days == 0) {
                int hour = (int) ((cal.getTimeInMillis() - date.getTime()) / 3600000);
                long minutes = Math.max((cal.getTimeInMillis() - date.getTime()) / 60000, 1) % 60;
                if (hour == 0) {
                    if (minutes < 5) {
                        ftime = "刚刚";
                    } else {
                        ftime = minutes + "分钟前";
                    }
                } else {
                    ftime = formatDynamic2.format(date);
                }
            } else if (days == 1) {
                ftime = "昨天";
            } else if (days == 2) {
                ftime = "前天";
            } else {
                // ftime = formatDynamic4.format(date);
                ftime = formatMd.format(date);
                // + (((curYear - dateYear) == 0) ? " "
                // + formatDynamic2.format(date) : "");
            }
            return ftime;
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * 聊天对话页面的时间
     * 
     * @param time
     * @return
     */
    public static String getFriendlyTimeDe10ForChat(String time) {
        try {
            if (TextUtils.isEmpty(time)) {
                return "";
            }
            // 将所有的时间都转为14位的Long类型，即毫秒的下一位
            Long timeLong = StringUtils.removePointbig10000ToLong(time);
            // time =String.valueOf(timeLong);
            long sTime = timeLong / 10;// 毫秒

            Date sDate = null;
            if (sTime > new Date().getTime()) {
                sDate = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
            } else {
                sDate = new Date(sTime);
            }
            String resultTime = "";
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(new Date(System.currentTimeMillis()));
            cCal.set(Calendar.HOUR_OF_DAY, 0);
            cCal.set(Calendar.MINUTE, 0);
            cCal.set(Calendar.SECOND, 0);
            cCal.set(Calendar.MILLISECOND, 0);

            Calendar sCal = Calendar.getInstance();
            sCal.setTime(sDate);

            if ((sCal.getTimeInMillis() - cCal.getTimeInMillis()) > 0
                    && (sCal.getTimeInMillis() - cCal.getTimeInMillis()) <= 86400000) {
                // 24小时内
                resultTime = formatDynamic2.format(sCal.getTimeInMillis());
            } else if ((cCal.getTimeInMillis() - sCal.getTimeInMillis()) <= (86400000)) {
                // 昨天
                resultTime = "昨天 " + formatDynamic2.format(sCal.getTimeInMillis());
            } else if ((cCal.getTimeInMillis() - sCal.getTimeInMillis()) <= (86400000 * 6)) {
                // 一周内
                resultTime = formatDynamic3.format(sCal.getTimeInMillis());
            } else {
                // 全部日期 几年几月几日 几时几分 （24）
                resultTime = formatDynamic4.format(sCal.getTimeInMillis());
            }

            return resultTime;
        } catch (Exception e) {
        }

        return "";
    }

    public static String getNotifyTime(String timestr) {
        double time = Double.valueOf(timestr);
        long sTime = (long) time;
        sTime = sTime * 1000l;

        Date sDate = null;
        if (sTime > new Date().getTime()) {
            sDate = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
        } else {
            sDate = new Date(sTime);
        }
        String resultTime = "";
        Calendar cCal = Calendar.getInstance();
        cCal.setTime(new Date(System.currentTimeMillis()));
        cCal.set(Calendar.HOUR_OF_DAY, 0);
        cCal.set(Calendar.MINUTE, 0);
        cCal.set(Calendar.SECOND, 0);
        cCal.set(Calendar.MILLISECOND, 0);

        Calendar sCal = Calendar.getInstance();
        sCal.setTime(sDate);

        if ((sCal.getTimeInMillis() - cCal.getTimeInMillis()) > 0
                && (sCal.getTimeInMillis() - cCal.getTimeInMillis()) <= 86400000) {
            // 24小时内
            resultTime = formatDynamic2.format(sCal.getTimeInMillis());
        } else if ((cCal.getTimeInMillis() - sCal.getTimeInMillis()) <= (86400000)) {
            // 昨天
            resultTime = "昨天 " + formatDynamic2.format(sCal.getTimeInMillis());
        } else if ((cCal.getTimeInMillis() - sCal.getTimeInMillis()) <= (86400000 * 6)) {
            // 一周内
            resultTime = formatDynamic3.format(sCal.getTimeInMillis());
        } else {
            // 全部日期 几年几月几日 几时几分 （24）
            resultTime = formatDynamic4.format(sCal.getTimeInMillis());
        }

        return resultTime;
    }

    /**
     * 聊天列表的时间显示
     * 
     * @param time
     * @return
     */
    public static String getFriendlyTimeDe10ForChatList(String time) {
        try {
            if (TextUtils.isEmpty(time)) {
                return "";
            }
            // 将所有的时间都转为14位的Long类型，即毫秒的下一位
            Long timeLong = StringUtils.removePointbig10000ToLong(time);
            // time =String.valueOf(timeLong);
            long sTime = timeLong / 10;// 毫秒

            Date sDate = null;
            if (sTime > new Date().getTime()) {
                sDate = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
            } else {
                sDate = new Date(sTime);
            }
            String resultTime = "";
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(new Date(System.currentTimeMillis()));
            cCal.set(Calendar.HOUR_OF_DAY, 0);
            cCal.set(Calendar.MINUTE, 0);
            cCal.set(Calendar.SECOND, 0);
            cCal.set(Calendar.MILLISECOND, 0);

            Calendar sCal = Calendar.getInstance();
            sCal.setTime(sDate);

            if ((sCal.getTimeInMillis() - cCal.getTimeInMillis()) > 0
                    && (sCal.getTimeInMillis() - cCal.getTimeInMillis()) <= 86400000) {
                // 24小时内
                resultTime = formatDynamic2.format(sCal.getTimeInMillis());
            } else if ((cCal.getTimeInMillis() - sCal.getTimeInMillis()) <= (86400000)) {
                // 昨天
                resultTime = "昨天";
            } else if ((cCal.getTimeInMillis() - sCal.getTimeInMillis()) <= (86400000 * 6)) {
                // 一周内
                resultTime = formatMessage1.format(sCal.getTimeInMillis());
            } else {
                // 全部日期 15/11/10
                resultTime = formatMessage2.format(sCal.getTimeInMillis());
            }

            return resultTime;
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * 群发消息时时间显示规则
     * 
     * @param time
     * @return
     */
    public static String getQunfaFriendlyTime(long time) {
        long ltime = Long.valueOf(time) * 1000;// 毫秒
        Date date = null;
        if (ltime > new Date().getTime()) {
            date = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
        } else {
            date = new Date(ltime);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        int curYear = cal.get(Calendar.YEAR);
        int dateYear = dateCal.get((Calendar.YEAR));
        String ftime = "";
        // 今年不显示年份
        if (curYear - dateYear == 0) {
            ftime = formatMd.format(date) + " " + getWeekOfDate(date) + " " + formatDynamic2.format(date);
        } else {
            ftime = formatBirthday.format(date) + " " + getWeekOfDate(date) + " " + formatDynamic2.format(date);
        }
        return ftime;
    }

    /**
     * 日期转时间 用法：date2Long("2008-07-10 19:20:00")
     * 
     * @param date
     * @return
     */
    public static String date2Long(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date toDate = simpleDateFormat.parse(date);
            return toDate.getTime() + "";
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取星期几
     * 
     * @param date
     *            Date类型
     */
    public static String getDayOfWeek(Date date) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * 获取星期几
     * 
     * @param date
     *            Long类型
     */
    public static String getDayOfWeek(long date) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * 发布日期
     * 
     * @param time
     * @return
     */
    public static String getPublishTime(long time) {
        long ltime = Long.valueOf(time * 1000L);// 毫秒
        Date date = null;
        if (ltime > new Date().getTime()) {
            date = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
        } else {
            date = new Date(ltime);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        int curYear = cal.get(Calendar.YEAR);
        int dateYear = dateCal.get((Calendar.YEAR));
        String ftime = "";
        // 今年不显示年份
        if (curYear - dateYear == 0) {
            ftime = formatMd.format(date);
        } else {
            ftime = formatBirthday.format(date);
        }
        return ftime;
    }

    /**
     * 发布日期
     * 
     * @param time
     * @return
     */
    public static String getTrystPublishTime(long time) {
        long ltime = Long.valueOf(time * 1000L);// 毫秒
        Date date = null;
        if (ltime > new Date().getTime()) {
            date = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
        } else {
            date = new Date(ltime);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        int curYear = cal.get(Calendar.YEAR);
        int dateYear = dateCal.get((Calendar.YEAR));
        String ftime = "";
        // 今年不显示年份
        if (curYear - dateYear == 0) {
            ftime = formatDynamic5.format(date);
        } else {
            ftime = formatDynamic4.format(date);
        }
        return ftime;
    }

    public static String getTrystApplyTime(long startTime, long endTime) {
        if (isSameDay(startTime, endTime)) {
            return formatDynamic5.format(new Date(startTime * 1000L)) + "-"
                    + formatDynamic2.format(new Date(endTime * 1000L));
        }

        return formatContacts.format(new Date(startTime * 1000L)) + "-"
                + formatContacts.format(new Date(endTime * 1000L));
    }

    /**
     * 红包领取日期
     * 
     * @param time
     * @return
     */
    public static String getRedpackageTime(long time) {
        long ltime = Long.valueOf(time * 1000L);// 毫秒
        Date date = null;
        if (ltime > new Date().getTime()) {
            date = new Date(System.currentTimeMillis());// 服务器时间大于当前时间的异常情形使用当下时间
        } else {
            date = new Date(ltime);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        int curYear = cal.get(Calendar.YEAR);
        int dateYear = dateCal.get((Calendar.YEAR));
        String ftime = "";
        // // 今年不显示年份
        // if (curYear - dateYear == 0) {
        // ftime = formatDynamic5.format(date);
        // } else {
        // ftime = redDateFormat.format(date);
        // }
        ftime = redDateFormat1.format(date);
        return ftime;
    }

    /**
     * 判断是两个日期是不是同一天
     * 
     * @param ti
     * @return
     */
    public static boolean isSameDay(long ti_a, long ti_b) {
        ti_a = StringUtils.removePointbig10000ToLong("" + ti_a) / 10;
        ti_b = StringUtils.removePointbig10000ToLong("" + ti_b) / 10;
        Calendar a = new GregorianCalendar();
        Calendar b = new GregorianCalendar();
        a.setTime(new Date(ti_a));
        b.setTime(new Date(ti_b));
        if (a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                && a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是两个日期是不是同一年
     * 
     * @param ti
     * @return
     */
    public static boolean isSameYear(long ti_a, long ti_b) {
        ti_a = StringUtils.removePointbig10000ToLong("" + ti_a) / 10;
        ti_b = StringUtils.removePointbig10000ToLong("" + ti_b) / 10;
        Calendar a = new GregorianCalendar();
        Calendar b = new GregorianCalendar();
        a.setTime(new Date(ti_a));
        b.setTime(new Date(ti_b));
        if (a.get(Calendar.YEAR) == b.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }

    public static String getHHmm(long time) {
        try {
            time = StringUtils.removePointbig10000ToLong("" + time) / 10;
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFormatTime(String time, SimpleDateFormat dataformat) {
        try {
            long _time = StringUtils.removePointbig10000ToLong(time) / 10;
            Date date = new Date(_time);
            return dataformat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取指定日期的年份
     * 
     * @param p_date
     *            util.Date日期
     * @return int 年份
     */
    public static int getYearOfDate(java.util.Date p_date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(p_date);
        return c.get(java.util.Calendar.YEAR);
    }

    /**
     * 获取指定日期的月份
     * 
     * @param p_date
     *            util.Date日期
     * @return int 月份
     */
    public static int getMonthOfDate(java.util.Date p_date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(p_date);
        return c.get(java.util.Calendar.MONTH) + 1;
    }

    /**
     * 获取指定日期的日份
     * 
     * @param p_date
     *            util.Date日期
     * @return int 日份
     */
    public static int getDayOfDate(java.util.Date p_date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(p_date);
        return c.get(java.util.Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取字符日期一个月的天数
     * 
     * @param p_date
     * @return 天数
     */
    public static long getDayOfMonth(Date p_date) throws ParseException {
        int year = getYearOfDate(p_date);
        int month = getMonthOfDate(p_date) - 1;
        int day = getDayOfDate(p_date);
        Calendar l_calendar = new GregorianCalendar(year, month, day);
        return l_calendar.getActualMaximum(l_calendar.DAY_OF_MONTH);
    }

    /**
     * 获取某年某月的天数
     * 
     * @param year
     * @param month
     *            从1开始
     * @return 天数
     */
    public static int getDayOfMonth(int year, int month) {
        // Calendar对象默认一月为0
        Calendar l_calendar = new GregorianCalendar(year, month - 1, 1);
        return l_calendar.getActualMaximum(l_calendar.DAY_OF_MONTH);
    }

    public static String formatHM(long time) {
        return formatDynamic2.format(new Date(time * 1000L));
    }

    /**
     * 订单时间
     * 
     * @param time
     * @return
     */
    public static String formatTrystOrder(long time) {
        return dateFormatorder.format(new Date(time));
    }

    /**
     * 动态时间格式化 (*年)*月*日 星期*
     * 
     * @param time
     *            毫秒
     * @return
     */
    public static String getDynamicTime(long time) {
        String ret = "";
        Date date = new Date(time);
        boolean issameyear = isSameYear(time, System.currentTimeMillis());
        if (issameyear) {
            ret = formatMd.format(date);
        } else {
            ret = formatBirthday.format(date);
        }
        String week = getWeekOfDate(date);

        return ret + " " + week;
    }

    public static String formatPlayTime(int duration) {
        duration = duration / 1000;
        int hour = duration / (60 * 60);
        int minu = duration / 60 % 60;
        int second = duration % 60;
        String hourStr = hour < 10 ? "0" + hour : "" + hour;
        String minuStr = minu < 10 ? "0" + minu : "" + minu;
        String secondStr = second < 10 ? "0" + second : "" + second;
        return hourStr + ":" + minuStr + ":" + secondStr;
    }

}
