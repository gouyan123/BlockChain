package com.blockchain.p2pdemo;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * p2p服务端，用来被其他客户端连接的
 */
public class P2PServer {
	/*socket：server与client连接通道，将连接通道保存起来，用的时候再取出来*/
	private List<WebSocket> sockets = new ArrayList<WebSocket>();
	
	public List<WebSocket> getSockets() {
		return sockets;
	}
	
	public void initP2PServer(int port) {
		final WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {
			/*四个回调方法*/
			public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
				write(webSocket, "服务端连接成功 啦啦啦");
				System.out.println("socketClient = " + JSON.toJSONString(webSocket,true));
				sockets.add(webSocket);
			}

			public void onClose(WebSocket webSocket, int i, String s, boolean b) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				sockets.remove(webSocket);
			}

			public void onMessage(WebSocket webSocket, String msg) {
				System.out.println("接收到客户端消息：" + msg);
				write(webSocket, "服务器收到消息");
				//broatcast("服务器收到消息:" + msg);
			}

			public void onError(WebSocket webSocket, Exception e) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				sockets.remove(webSocket);
			}

			public void onStart() {

			}
		};
		socketServer.start();
		System.out.println("listening websocket p2p port on: " + port);
	}
	
	public void write(WebSocket ws, String message) {
		System.out.println("发送给" + ws.getRemoteSocketAddress().getPort() + "的p2p消息:" + message);
		ws.send(message);
	}
	
	public void broatcast(String message) {
		if (sockets.size() == 0) {
			return;
		}
		System.out.println("======广播消息开始：");
		for (WebSocket socket : sockets) {
			this.write(socket, message);
		}
		System.out.println("======广播消息结束");
	}

}
