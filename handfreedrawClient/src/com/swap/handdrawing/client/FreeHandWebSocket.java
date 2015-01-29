package com.swap.handdrawing.client;



import android.util.Log;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;


public class FreeHandWebSocket  {

	
	private OnTouchListener mTouchListner;
	private AsyncHttpClient mAsyncHttpClient;
	
	public void init(String streamUrl){
        AsyncHttpClient.WebSocketConnectCallback mWebSocketConnectCallback = new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                webSocket.send("Hello Server");
                webSocket.setDataCallback(new DataCallback() {
					
					@Override
					public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
						MotionEvent  event = ParcelableUtil.unmarshall(bb.getAllByteArray(), MotionEvent.CREATOR);
						Log.d("CLIENTTAG",event.toString());
						mTouchListner.onTouch(null, event);
						
					}
				});
                
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(final String s) {
                        Log.d("CLIENTTAG",s);
                       
                       
                    }
                });
            }
        };
        mAsyncHttpClient = AsyncHttpClient.getDefaultInstance();
        mAsyncHttpClient.websocket(streamUrl, null, mWebSocketConnectCallback);

	}
	
	
   public void setTouchListner(OnTouchListener listner){
	   mTouchListner=listner;
   }
	

}
