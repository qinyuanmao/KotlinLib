package com.wisesoft.android.kotlinlib.view.verifyCodeView.sms;

/**
 * 接收短信监听回调接口
 */
public interface ReceiveSmsMessageListener {

    void onReceive(String smsSender, String smsBody);

}
