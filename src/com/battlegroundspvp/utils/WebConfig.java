package com.battlegroundspvp.utils;
/* Created by GamerBah on 2/10/2018 */

public class WebConfig {

    public static String getMotd() {
        return HttpUtils.get(HttpUtils.PanelExtension.CFG_EDIT).get("motd");
    }

}
