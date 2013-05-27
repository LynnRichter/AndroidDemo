package com.lynnrichter.tools;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;



/**
 * Created by lynn on 13-5-24.
 */
public class AsyncImageLoader {

    private final HashMap<String ,SoftReference<Drawable>>imageCaches;
    private final Context mcontext;

    public AsyncImageLoader(Context context)
    {
        mcontext=context;
        imageCaches=new HashMap<String, SoftReference<Drawable>>();
    }

    public Drawable loadImage(final String ImageUrl,final ImageView imageview,final ILoadImageCallBack callBack)
    {
        //
        if(imageCaches.containsKey(ImageUrl))
        {
            SoftReference<Drawable> softReference =imageCaches.get(ImageUrl);
            Drawable drawable=softReference.get();
            if(null!=drawable)
            {
                return drawable;
            }
        }
        final Handler handler=new Handler() {
           @Override
		public void handleMessage(Message msg)
           {

               callBack.onObtainDrawable((Drawable)msg.obj,imageview);
           }
        };
        new Thread()
        {
            @Override
			public void run()
            {


                try
                {
                    Drawable drawable=Drawable.createFromStream(new URL(ImageUrl).openStream(), "logo.png");
                    imageCaches.put(ImageUrl, new SoftReference<Drawable>(drawable));
                    Message msg=handler.obtainMessage(0, drawable);
                    handler.sendMessage(msg);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Message msg=new Message();
                    msg.obj=null;
                    handler.sendMessage(msg);

                }

            }
        }.start();
        return null;
    }

    public interface  ILoadImageCallBack
    {
        public void onObtainDrawable(Drawable drawable,ImageView imageView);
    }



}

