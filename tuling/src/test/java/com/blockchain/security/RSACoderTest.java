package com.blockchain.security;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;
import com.blockchain.security.RSACoder;

public class RSACoderTest {
	private String publicKey;
	private String privateKey;

	@Before
	public void setUp() throws Exception {
		/*自定义RSACoder类的initKey方法，跟进去，就是获取一个map，里面封装了公钥，私钥*/
		Map<String, Object> keyMap = RSACoder.initKey();
		System.out.println(JSON.toJSONString(keyMap,true));
		this.publicKey = RSACoder.getPublicKey(keyMap);
		this.privateKey = RSACoder.getPrivateKey(keyMap);
		System.err.println("公钥: \n\r" + publicKey);
		System.err.println("私钥： \n\r" + privateKey);
	}
	/*非对称加密*/
	@Test
	public void testEncrypt() throws Exception {
		System.out.println("公钥加密-私钥解密");
		/*需要加密内容*/
		String inputStr = "abc";
		/*加密内容字符串转换为字节数组*/
		byte[] data = inputStr.getBytes();
		/*通过公钥，对内容进行加密，得到密文*/
		byte[] encodedRSACoder = RSACoder.encryptByPublicKey(data,this.publicKey);
		System.out.println("发送方加密后密文 = " + new String(encodedRSACoder));
		byte[] decodedRSACoder = RSACoder.decryptByPrivateKey(encodedRSACoder,this.privateKey);
		System.out.println("解密方解密后内容 = " + new String(decodedRSACoder));
	}

	@Test
	public void testSign() throws Exception {
		System.out.println("发送方生成公钥私钥对：公钥给别人，私钥给自己；发送方加密，接收方解密；");
		/*签名内容*/
		String inputStr = "sign";
		byte[] data = inputStr.getBytes();
		/*发送方用私钥对签名进行加密*/
		String sign = RSACoder.sign(data,this.privateKey);
		System.out.println(JSON.toJSONString(sign,true));
		/*接收方用接收方方公钥验证签名*/
		boolean status = RSACoder.verify(data,this.publicKey,sign);
		System.out.println(JSON.toJSONString(status,true));
	}

}
