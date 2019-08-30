package kr.co.itcen.network.echo;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
private static final int PORT = 8000;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			//1. 소켓생성
			serverSocket=new ServerSocket();
			InetAddress inetAddress = InetAddress.getLocalHost();
			String localHostAddress = inetAddress.getHostAddress();
			InetSocketAddress inetSocketAddress = new InetSocketAddress(localHostAddress,PORT);
			//2.bind
			serverSocket.bind(inetSocketAddress);
			log("binding "+inetAddress.getHostAddress()+":"+PORT);
			//3.accept
			while(true) {
				Socket socket=serverSocket.accept();
				new EchoServerReceiveThread(socket).start();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(serverSocket != null && serverSocket.isClosed()==false)
					serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void log(String log) {
		System.out.println("[Echo Server#"+Thread.currentThread().getId()+"] "+log);
	}
}
