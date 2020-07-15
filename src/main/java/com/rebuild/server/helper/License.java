/*
Copyright (c) REBUILD <https://getrebuild.com/> and its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

package com.rebuild.server.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rebuild.server.Application;
import com.rebuild.server.helper.cache.CommonCache;
import com.rebuild.utils.CommonsUtils;
import com.rebuild.utils.JSONUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import java.util.UUID;

/**
 * 授权许可
 *
 * @author ZHAO
 * @since 2019-08-23
 */
public final class License {

    private static final String OSA_KEY = "IjkMHgq94T7s7WkP";

    /**
     * 授权码/SN
     *
     * @return
     */
    public static String SN() {
        String SN = SysConfiguration.get(ConfigurableItem.SN, false);
        if (SN == null) {
            SN = SysConfiguration.get(ConfigurableItem.SN, true);
        }

        if (SN == null) {
            try {
                String apiUrl = String.format("https://getrebuild.com/api/authority/new?ver=%s&k=%s", Application.VER, OSA_KEY);
                String result = CommonsUtils.get(apiUrl);

                if (JSONUtils.wellFormat(result)) {
                    JSONObject o = JSON.parseObject(result);
                    SN = o.getString("sn");
                    SysConfiguration.set(ConfigurableItem.SN, SN);
                }

            } catch (Exception ignored) {
                // UNCATCHABLE
            }
        }

        if (SN == null) {
            SN = String.format("ZR%d%s-%s",
                    Application.BUILD,
                    StringUtils.leftPad(Locale.getDefault().getCountry(), 3, "0"),
                    UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase());
            if (Application.serversReady()) {
                SysConfiguration.set(ConfigurableItem.SN, SN);
            }
        }

        return SN;
    }

    /**
     * 查询授权信息
     *
     * @return
     */
    public static JSON queryAuthority() {
        JSON result = siteApi("api/authority/query");
        if (result == null) {
            result = JSONUtils.toJSONObject(
                    new String[]{ "sn", "authType", "autoObject", "authExpires" },
                    new String[]{ SN(), "开源社区版", "GitHub", "无" });
        }
        return result;
    }

    /**
     * @param api
     * @return
     * @see #siteApi(String, boolean)
     */
    public static JSONObject siteApi(String api) {
        return siteApi(api, false);
    }

    /**
     * 调用 RB 官方服务 API
     *
     * @param api
     * @return
     */
    public static JSONObject siteApi(String api, boolean useCache) {
        String apiUrl = "https://getrebuild.com/" + api;
        apiUrl += (api.contains("\\?") ? "&" : "?") + "k=" + OSA_KEY + "&sn=" + SN();

        if (useCache) {
            Object o = Application.getCommonCache().getx(api);
            if (o != null) {
                return (JSONObject) o;
            }
        }

        try {
            String result = CommonsUtils.get(apiUrl);
            if (JSONUtils.wellFormat(result)) {
                JSONObject o = JSON.parseObject(result);
                Application.getCommonCache().putx(api, o, CommonCache.TS_HOUR * 2);
                return o;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
