package pers.zlf.plugin.util;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2024/6/18 15:49
 */
public class CronUtil {

    /**
     * 判断是否是cron表达式
     *
     * @param cronStr  cron表达式
     * @param cronType cron类型
     * @return true 是
     */
    public static boolean isCron(String cronStr, CronType cronType) {
        if (StringUtil.isEmpty(cronStr)) {
            return false;
        }
        try {
            CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(cronType));
            cronParser.parse(cronStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 返回最近的执行时间
     *
     * @param cronStr  cron表达式
     * @param cronType cron类型
     * @param count    次数
     * @return List
     */
    public static List<String> getExecutionTimeList(String cronStr, CronType cronType, int count) {
        CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(cronType));
        Cron cron = cronParser.parse(cronStr);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime now = ZonedDateTime.now();
        List<String> cronList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DDHHMMSS);
        for (int i = 1; i <= count; i++) {
            now = executionTime.nextExecution(now).get();
            cronList.add(formatter.format(now));
        }
        return cronList;
    }

}
