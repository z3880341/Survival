package com.y.survival.survivalTools;

public interface SurvivalListener {
    void onStart();

    /**
     * 后台服务的心跳回调
     * id = 1 是双服务保活的第一服务心跳
     * id = 2 是双服务保活的第二服务心跳
     * id = 3 是音频服务保活的心跳
     * @param heartbeatId
     */
    void onHeartbeat(int heartbeatId);

    void onStop();

    void onDestroy();

    void onError(String reason);
}
