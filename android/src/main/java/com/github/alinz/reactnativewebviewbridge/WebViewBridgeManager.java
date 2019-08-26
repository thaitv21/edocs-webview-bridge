package com.github.alinz.reactnativewebviewbridge;

import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.webview.ReactWebViewManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class WebViewBridgeManager extends ReactWebViewManager {
    private static final String REACT_CLASS = "RCTWebViewBridge";

    public static final int COMMAND_SEND_TO_BRIDGE = 101;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public
    @Nullable
    Map<String, Integer> getCommandsMap() {
        Map<String, Integer> commandsMap = super.getCommandsMap();

        commandsMap.put("sendToBridge", COMMAND_SEND_TO_BRIDGE);

        return commandsMap;
    }

    @Override
    protected WebView createViewInstance(ThemedReactContext reactContext) {
        WebView root = super.createViewInstance(reactContext);
        root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addJavascriptInterface(new JavascriptBridge(root), "WebViewBridge");
//        String html = "<h2 style=\"font-weight: 600; font-size: 24px; text-align: center\">A330</h2><h2 style=\"font-weight: 600; font-size: 24px; text-align: center\">AIRCRAFT SCHEMATIC MANUAL</h2><h2 style=\"font-size: 24px; text-align: center\">ASM</h2><h2 style=\"font-size: 24px; text-align: center\">HVN</h2><h2 style=\"font-size: 24px; text-align: center\">Revision date: April 01, 2019 </h2><h2 style=\"font-size: 24px; text-align: center\">Revision number: 30</h2><h4 style=\"text-decoration: underline\">&copy; AIRBUS S.A.S. 2005. All rights reserved.</h4><p>2005</p><p>AIRBUS S.A.S.</p><p>Customer Services<br/>Technical Data Support and Services<br/>31707 Blagnac Cedex<br/>FRANCE<br/></p><p>The content of this document is the property of Airbus.<br/>It is supplied in confidence and commercial security on its contents must be maintained.<br/>It must not be used for any purpose other than that for which it is supplied, nor may information contained in it be disclosed to unauthorized persons.<br/>It must not be reproduced in whole or in part without permission in writing from the owners of the copyright. Requests for reproduction of any data in this document and the media authorized for it must be addressed to Airbus.<br/></p>";
//        root.loadData(html, "text/html", "utf-8");
//        root.getSettings().setDomStorageEnabled(true);
        root.getSettings().setJavaScriptEnabled(true);
        root.setWebChromeClient(new WebChromeClient() {

        });
        return root;
    }

    @Override
    public void receiveCommand(WebView root, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);

        switch (commandId) {
            case COMMAND_SEND_TO_BRIDGE:
                sendToBridge(root, args.getString(0));
                break;
            default:
                //do nothing!!!!
        }
    }

    private void sendToBridge(WebView root, String message) {
        String script = "WebViewBridge.onMessage('" + message + "');";
        WebViewBridgeManager.evaluateJavascript(root, script);
    }

    static private void evaluateJavascript(WebView root, String javascript) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            root.evaluateJavascript(javascript, null);
        } else {
            root.loadUrl("javascript:" + javascript);
        }
    }

    @ReactProp(name = "allowFileAccessFromFileURLs")
    public void setAllowFileAccessFromFileURLs(WebView root, boolean allows) {
        root.getSettings().setAllowFileAccessFromFileURLs(allows);
    }

    @ReactProp(name = "allowUniversalAccessFromFileURLs")
    public void setAllowUniversalAccessFromFileURLs(WebView root, boolean allows) {
        root.getSettings().setAllowUniversalAccessFromFileURLs(allows);
    }

    @ReactProp(name = "enableJQuery")
    public void setEnableJQuery(WebView root, boolean enable) {
        root.addJavascriptInterface(new JQueryInterface(root), "android");
        root.addJavascriptInterface(new BootstrapInterface(root), "bootstrap");
    }
}