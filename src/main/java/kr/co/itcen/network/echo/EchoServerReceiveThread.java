package kr.co.itcen.network.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class EchoServerReceiveThread extends Thread {
	private Socket socket;
	public EchoServerReceiveThread(Socket socket) {
		this.socket=socket;
	}
	@Override
	public void run() {
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
		EchoServer.log("Connected from client ["+inetRemoteSocketAddress.getAddress().getHostAddress()+":"+inetRemoteSocketAddress.getPort()+"]");
		try {
			//4.I/O Stream 생성
			BufferedReader br =new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);///버퍼가 있는 writer//true(flush옵션=버퍼를 한번 쓰면 바로 보내라)
			while(true) {
				//5.데이터 읽기(수신)
				String data =br.readLine();
				if(data==null) {
					EchoServer.log("closed by client");
					break;
				}
				EchoServer.log("recieve : "+data);
				//6.데이터 쓰기(송신)
				pw.println(data);
			}
		}catch(SocketException e){//소켓이 비정상적으로 종료가 되었을때
			EchoServer.log(" abnormal closed by client");
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			if(socket.isClosed()==false && socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
}
