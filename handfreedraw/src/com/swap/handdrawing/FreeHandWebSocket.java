package com.swap.handdrawing;



import java.util.ArrayList;

import android.os.Parcel;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServer.WebSocketRequestCallback;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

public class FreeHandWebSocket implements TouchEventInjection {

	private ArrayList<WebSocket> mSockets;
	private AsyncHttpServer mAsyncHttpServer;
	private WebSocketRequestCallback mWebSocketCallback;
	
	public void init(){
		 mSockets = new ArrayList<WebSocket>();
	     mAsyncHttpServer = new AsyncHttpServer();
	        mWebSocketCallback = new AsyncHttpServer.WebSocketRequestCallback() {
	            @Override
	            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
	                mSockets.add(webSocket);
	                webSocket.send("Welcome Client");
	                webSocket.setClosedCallback(new CompletedCallback() {
	                    public void onCompleted(Exception ex) {
	                        try {
	                            if (ex != null)
	                                Log.e("WebSocket", "Error");
	                        } finally {
	                            mSockets.remove(webSocket);
	                        }
	                    }
	                });
	                webSocket.setStringCallback(new WebSocket.StringCallback() {
	                    @Override
	                    public void onStringAvailable(final String s) {
	                        Log.d("SERVERTAG",s);
	                        
	                    }
	                });
	            }

				
	        };

	        mAsyncHttpServer.websocket("/",mWebSocketCallback);
	        mAsyncHttpServer.listen(7030);
	}
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		Log.d(FreeHandWebSocket.class.getName()+"before", event.toString());
		postEvent(ParcelableUtil.marshall(event));
//		
//		postEvent(event.writeToParcel(out, flags););
		return false;
	}

	private void postEvent(byte[] bytes){
		MotionEvent  event = ParcelableUtil.unmarshall(bytes, MotionEvent.CREATOR);
		Log.d(FreeHandWebSocket.class.getName()+"After", event.toString());
//		postEvent(event.toString());
		 for(WebSocket socket : mSockets) {
            socket.send(bytes);
        }
	}
	private void postEvent(String str){
		 for(WebSocket socket : mSockets) {
             socket.send(str);
         }
	}

}
