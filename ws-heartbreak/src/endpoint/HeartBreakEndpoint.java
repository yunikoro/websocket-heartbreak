package endpoint;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/count")
public class HeartBreakEndpoint {
	
	private Timer timer;
	private volatile boolean isPong;
	private long startTime;
	
	@OnOpen
	public void start(Session localSession) {
		this.startTime = System.currentTimeMillis();
		
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					if(isPong) {
						System.out.println("Timer - Say hi");
						localSession.getBasicRemote().sendText("hi");
						isPong = false;
					} else {
						System.out.println("INFO - Check the time.");
						localSession.close();
						this.cancel();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					this.cancel();
				}
			}
			
		}, 2000, 2000);
		
	}
	
	@OnClose
	public void onClose() {
		long currTime = System.currentTimeMillis();
		System.out.println("OnClose - view time " + (currTime - this.startTime)/1000 + " sec.");
	}
	
	@OnMessage
	public void onMessage(String msg) {
		System.out.println("OnMessage - " + msg);
		if(msg.equals("yes")) {
			this.isPong = true;
		}
	}
	
	@OnError
	public void onError(Throwable t) throws Throwable {
		
	}
	
	public HeartBreakEndpoint() {
		this.timer = new Timer();
		this.isPong = true;
	}

}
