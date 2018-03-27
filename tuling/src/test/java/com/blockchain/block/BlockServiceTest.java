package com.blockchain.block;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import com.blockchain.model.*;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.blockchain.security.CryptoUtil;

/**
 * 区块链测试
 */
public class BlockServiceTest {
	private List<Block> blockchain;
	private List<Transaction> transactions;
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testBlockMine() throws Exception {
		/*创建区块链*/
		List<Block> blockchain = new ArrayList<Block>();
		/*创建创世区块，其结构是定死的，参数随便写，用不到*/
		Block firstBlock = new Block(1,System.currentTimeMillis(),new ArrayList<Transaction>(),1,"1","1");
		/*创世区块加人区块链中*/
		blockchain.add(firstBlock);
		/*打印区块链*/
		System.out.println(JSON.toJSONString(blockchain,true));
		/*接下来生成新的区块，即挖矿，把当前区块链中所有交易记录都记录到新的区块中来*/

		/*准备挖矿，挖矿之前，系统中已经有交易了，需要交易的集合，存储交易*/
		List<Transaction> transactions = new ArrayList<Transaction>();
		/*此时还没有 P2P 交易，创建假的交易，模拟网络中产生 3 笔交易，存到交易集合中*/
		Transaction tx1 = new Transaction();
		Transaction tx2 = new Transaction();

		/*将交易添加到要打包的交易集合 transactions 中*/
		transactions.add(tx1);
		transactions.add(tx2);

		/*首先生成 发起方和接收方 的钱包*/
		/*创建交易发起方钱包*/
		Wallet walletSender = Wallet.generateWallet();
		/*创建交易接收方钱包*/
		Wallet walletReciptent = Wallet.generateWallet();
		/*创建交易发起方                              前一次交易tx2的id 金额 签名          发送方的公钥地址*/
		TransactionInput txIn = new TransactionInput(tx2.getId(),10,null,walletSender.getPublicKey());
		/*创建交易接收方                                  金额     接收方钱包的公钥地址*/
		TransactionOutput txOut = new TransactionOutput(10,walletReciptent.getHashPubKey());
		/*创建交易tx3                     交易唯一编号       输入 输出*/
		Transaction tx3 = new Transaction(CryptoUtil.UUID(),txIn,txOut);
		/*指定tx2之前已经被打包进区块，也就是被记录进账本了*/
		tx3.sign(walletSender.getPrivateKey(),tx2);
		/*将交易tx3加人自己本地交易池中后，广播通知其他用户，可以挖矿了*/
		transactions.add(tx3);
		/*要开始挖矿，①要创建系统奖励交易*/
		Transaction sysTx = new Transaction();
		transactions.add(sysTx);
		/*②获取当前区块链里最后一个区块链*/
		Block lastBlock = blockchain.get(blockchain.size() - 1);
		/*③计算新区块 hash 值，hash = SHA256(最后一个区块的hash值 + 系统中所有交易记录信息 + 随机数)*/
		int nonce = 1;/*定义随机数，随机数的值任意定，这里定为1*/
		/*新区块 hash 值必须以 0或00或000或000...开头，否则，将随机数 nonce加1，再计算，依次循环，直到hash
    	*前面的0位数满足要求，才表示获取到合理hash
    	*/
		long start = System.currentTimeMillis();
		System.out.println("start at " + new Date(start));
		while (true){
			String hash = CryptoUtil.SHA256(lastBlock.getHash() + JSON.toJSONString(transactions) + nonce);
			/*④校验hash值是否有效*/
			if (hash.startsWith("0")){
				System.out.println("hash值正确：计算次数 nonce = " + nonce +  " + hash = "+ hash);
				/*⑤hash验证通过则创建新的区块，并打包系统所有交易信息，并将区块加人区块链*/
				Block newBlock = new Block(lastBlock.getIndex() + 1,System.currentTimeMillis(),transactions,1,lastBlock.getHash(),hash);
				/*新区块添加到区块链*/
				blockchain.add(newBlock);
				break;/*中断循环*/
			}
			//System.out.println("错误的 hash = " + hash);
			nonce += 1;
		}
		System.out.println("挖矿后的区块链：" + JSON.toJSONString(blockchain,true));
		long end = System.currentTimeMillis();
		System.out.println("end at " + new Date(end));
		System.out.println("how long : " + (end - start));
	}
	@Test
	public void testWallet(){
		Wallet wallet = Wallet.generateWallet();
		System.out.println("wallet = " + JSON.toJSONString(wallet,true));
	}

}
