package pers.zlf.plugin.util;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.pojo.ZenTaoData;
import pers.zlf.plugin.pojo.ZenTaoDataDetail;
import pers.zlf.plugin.pojo.response.ZenTaoResult;
import pers.zlf.plugin.pojo.response.ZenTaoSessionInfo;
import pers.zlf.plugin.pojo.response.ZenTaoUserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 禅道工具
 *
 * @author zhanglinfeng
 * @date create in 2025/3/20 10:21
 */
public class ZenTaoUtil {
    private static final String SESSION_URL = "/api-getsessionid.json";
    private static final String LOGIN_URL = "/user-login-%s.json";
    private static final String TASK_URL = "/my-task-assignedTo-id_desc-1000-1000-1.json";
    private static final String BUG_URL = "/my-bug-assignedTo-id_desc-1000-1000-1.json";

    /**
     * 获取待处理的任务和bug列表
     *
     * @param url      禅道地址
     * @param account  禅道账号
     * @param password 禅道密码
     * @return ZenTaoData
     * @throws Exception 异常
     */
    public static ZenTaoData getTaskAndBugList(String url, String account, String password) throws Exception {
        ZenTaoSessionInfo sessionInfo = login(url, account, password);
        Map<String, String> headerMap = new HashMap<>(4);
        headerMap.put(sessionInfo.getSessionName(), sessionInfo.getToken());
        headerMap.put("Cookie", sessionInfo.getSessionName() + Common.EQUAL_SIGN + sessionInfo.getToken() + Common.SEMICOLON);
        //获取BUG
        ZenTaoResult bugResult = HttpUtil.get(url + BUG_URL, headerMap, ZenTaoResult.class);
        if (bugResult.getResponseCode() != 200) {
            throw new Exception(Message.ZENTAO_REQUEST_BUG_FAILED + bugResult.getResponseCode());
        }
        if (!Common.SUCCESS.equals(bugResult.getStatus())) {
            throw new Exception(bugResult.getReason());
        }
        ZenTaoData data = JsonUtil.toObject(bugResult.getData(), ZenTaoData.class);
        List<ZenTaoDataDetail> bugs = data.getBugs();
        //获取任务
        ZenTaoResult taskResult = HttpUtil.get(url + TASK_URL, headerMap, ZenTaoResult.class);
        if (taskResult.getResponseCode() != 200) {
            throw new Exception(Message.ZENTAO_REQUEST_TASK_FAILED + taskResult.getResponseCode());
        }
        if (!Common.SUCCESS.equals(taskResult.getStatus())) {
            throw new Exception(taskResult.getReason());
        }
        data = JsonUtil.toObject(taskResult.getData(), ZenTaoData.class);
        data.setBugs(bugs);
        return data;
    }

    /**
     * 禅道登录
     *
     * @param url      禅道地址
     * @param account  禅道账号
     * @param password 禅道密码
     * @return ZenTaoSessionInfo
     * @throws Exception 异常
     */
    private static ZenTaoSessionInfo login(String url, String account, String password) throws Exception {
        ZenTaoResult sessionResult = HttpUtil.get(url + SESSION_URL, ZenTaoResult.class);
        if (sessionResult.getResponseCode() != 200) {
            throw new Exception(Message.ZENTAO_REQUEST_SESSION_FAILED + sessionResult.getResponseCode());
        }
        if (!Common.SUCCESS.equals(sessionResult.getStatus())) {
            throw new Exception(sessionResult.getReason());
        }
        ZenTaoSessionInfo sessionInfo = JsonUtil.toObject(sessionResult.getData(), ZenTaoSessionInfo.class);
        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("account", account);
        paramMap.put("password", password);
        ZenTaoUserInfo userInfo = HttpUtil.postForm(url + String.format(LOGIN_URL, sessionInfo.getSessionID()), paramMap, ZenTaoUserInfo.class);
        if (userInfo.getResponseCode() != 200) {
            throw new Exception(Message.ZENTAO_LOGIN_FAILED + userInfo.getResponseCode());
        }
        if (!Common.SUCCESS.equals(userInfo.getStatus())) {
            throw new Exception(userInfo.getReason());
        }
        sessionInfo.setToken(userInfo.getUser().getToken());
        return sessionInfo;
    }
}