package com.lynnrichter.hellolynnrichter;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;

import java.util.Date;


/**
 * Created by Lynnrichter on 13-7-8.
 */
public class HandleService extends Service {

    /**
     * 变量声明
     * */
    private static final String TAG="MessageService";
    private static final int KUKA=0;
    private Looper looper;
    private ServiceHandler handler;

    private final class ServiceHandler extends Handler
    {
        public ServiceHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case KUKA:
                    Log.i(TAG,"The obj field of msg:"+msg.obj);
                    break;
                default:
                    break;
            }
            stopSelf(msg.arg1);
        }


    }
    @Override
    public void onCreate()
    {
        Log.i(TAG,"MessageService -->onCreate()");
        /**默认情况下Service是运行在主线程中，而服务一般又十分耗费时间
         * 如果放在主线程中，将会影响与用户的交互，因此把Service放在一个单独的线程中执行
        */
        HandlerThread thread=new HandlerThread("messagedemoThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        /**获取当前线程的looper对象*/
        looper=thread.getLooper();
        /**创建Handler对象,把looper传过来，使得Handler，looper，messageQueue三者建立联系*/
        handler=new ServiceHandler(looper);

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startID)
    {
        Log.i(TAG,"MessageService-->onStartCommand()");
        /**从消息池中获取一个Message实例*/
        Message msg=handler.obtainMessage();
        /**arg1保存线程的ID，在handleMessage()方法中，通过stopSelf(startID)方法来终止服务*/
        msg.arg1=startID;
        /**msg的标志*/
        msg.what=KUKA;
        Date date=new Date();
        msg.obj=date;
        handler.sendMessage(msg);
        return START_STICKY;

    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG,"MessageService-->onDestroy()");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
