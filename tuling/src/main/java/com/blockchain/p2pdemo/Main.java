package com.blockchain.p2pdemo;

/**
 * 区块链节点启动入口
 */
public class Main {
	public static void main(String[] args) {
		/*每个用户都是服务端，又可以连接其他服务端*/
		P2PServer p2pServer = new P2PServer();/*作为服务端*/
		P2PClient p2pClient = new P2PClient();/*连接其他服务端*/
		int p2pPort = Integer.valueOf(args[0]);
		// 启动p2p服务端
		p2pServer.initP2PServer(p2pPort);
		if (args.length == 2 && args[1] != null) {
			// 作为p2p客户端连接p2p服务端
			p2pClient.connectToPeer(args[1]);
		}
	}
}
