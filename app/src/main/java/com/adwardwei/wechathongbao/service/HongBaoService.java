package com.adwardwei.wechathongbao.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

/**
 * Created by adward.wei on 2018/1/2.
 */

public class HongBaoService extends AccessibilityService {

    //当前微信版本是:6.5.16

//    微信红包，"开"  sourceid: com.tencent.mm:id/brt
//    返回键  ：sourceid:com.tencent.mm:id/bph
//    聊天界面返回键：  sourceid： android:id/text1
//    红包详情返回键： sourceid :com.tencent.mm:id/hg

    public static final String TAG ="HongBaoService";
    public static final String HongBaoHint ="微信红包";
    public static final String GetHongBaoHint="领取红包";
    public static final String HongBaoChatClassName="com.tencent.mm.ui.LauncherUI";
    public static final String HongBaoClassName="com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f";//红包页面，开
    public static final String HongBaoDetailClassName="com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";//点击开之后，红包详情页面


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        switch(accessibilityEvent.getEventType()){

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.i(TAG,"window content changed.");
//                AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
//                parseAllNodes(nodeInfo);
                break;

            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className=accessibilityEvent.getClassName().toString();
                Log.i(TAG,"window state changed,current window classname:"+className);
                if(HongBaoChatClassName.equals(className)){
                    Log.i(TAG,"enter ChatActivity contains hongbao.");
                    huntHongbao();

                }else if(HongBaoClassName.equals(className)){
                    Log.i(TAG,"enter hongbao Activity.");
                    performClickById("com.tencent.mm:id/brt");

                }else if(HongBaoDetailClassName.equals(className)){
                    Log.i(TAG,"enter hongbao detail activity.");
                    performClickById("com.tencent.mm:id/hg");
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                }
                break;

            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> notificationTexts=accessibilityEvent.getText();
                if(notificationTexts!=null){
                    for(CharSequence text :notificationTexts){
                        if(text !=null && text.toString().contains(HongBaoHint)){
                            if(accessibilityEvent.getParcelableData() !=null &&
                                    accessibilityEvent.getParcelableData() instanceof Notification){
                                Log.i(TAG,"receiver hongbao message on notification.");
                                Notification hongbaoNotification =(Notification)accessibilityEvent.getParcelableData();
                                PendingIntent  pendingIntent =hongbaoNotification.contentIntent;
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;


        }
    }

    @Override
    public void onInterrupt() {

    }


    //获取红包
    public void huntHongbao(){
        AccessibilityNodeInfo rootNodeInfo =getRootInActiveWindow();
        parseNodes(rootNodeInfo);
    }

    //解析所有的nodes
    public void parseAllNodes(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null)
            return ;
        if(nodeInfo.getChildCount() ==0){
            if(nodeInfo.getText() !=null){
                Log.i(TAG,"node text:"+nodeInfo.getText()+",isClickable:"+nodeInfo.isClickable()+",parent isClickable:"+nodeInfo.getParent().isClickable());
            }
        }else{

            for(int i=0;i<nodeInfo.getChildCount();i++){
                parseAllNodes(nodeInfo.getChild(i));
            }
        }
    }

    public void parseNodes(AccessibilityNodeInfo nodeInfo){

        if(nodeInfo ==null)
            return;
        if(nodeInfo.getChildCount() ==0){
            if(nodeInfo.getText() !=null){
                if(GetHongBaoHint.equals(nodeInfo.getText().toString()) ){
                    if(nodeInfo.isClickable()){
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }else if(nodeInfo.getParent().isClickable()){
                        nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }else{
            for(int i=0;i<nodeInfo.getChildCount();i++){
                parseNodes(nodeInfo.getChild(i));
            }
        }
    }



    public void performClickById(String resourceId){
        AccessibilityNodeInfo rootNode=getRootInActiveWindow();

        List<AccessibilityNodeInfo> nodeInfos =rootNode.findAccessibilityNodeInfosByViewId(resourceId);
        for(AccessibilityNodeInfo nodeInfo :nodeInfos){
            if(nodeInfo.isClickable()){
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }else{
                nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }


}
