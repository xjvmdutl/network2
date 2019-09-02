package http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;

public class RequestHandler extends Thread {
	private static String documentRoot ="";
	static {//로딩될떄 세팅
		documentRoot=RequestHandler.class.getClass().getResource("/webapp").getPath();//리소스위치의 경로가 나온다. 
	}
	private Socket socket;
	
	public RequestHandler( Socket socket ) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			// get IOStream
			OutputStream outputStream = socket.getOutputStream();//이미지 텍스트 모두받아 byte로 받은것 이다.
			BufferedReader br =new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			
			String request=null;
			while(true) {
				String line=br.readLine();
				//브라우저가 연결을 끊으면... 
				if(line==null)
					break;
				//header만 읽음
				if("".equals(line))
					break;
				if(request==null) {
					request=line;
					break;
				}
			}
			consoleLog(request);
			
			
			// logging Remote Host IP Address & Port
			InetSocketAddress inetSocketAddress = ( InetSocketAddress )socket.getRemoteSocketAddress();
			consoleLog( "connected from " + inetSocketAddress.getAddress().getHostAddress() + ":" + inetSocketAddress.getPort() );
			
			String[] tokens = request.split(" ");//GET + "200 OK\r\n"
			if("GET".equals(tokens[0])) {
				consoleLog("request:"+request);
				responseStaticResource(outputStream,tokens[1],tokens[2]);
			}else {//POST,PUT,DELETE 명령은 무시
				consoleLog("bad request:"+request);			
				response400Error(outputStream,tokens[2]);//과제//HTTP/1.1 400 BadRequest \n
			}
			// 예제 응답입니다.
			// 서버 시작과 테스트를 마친 후, 주석 처리 합니다.
			//outputStream.write( "HTTP/1.1 200 OK\r\n".getBytes( "UTF-8" ) );
			//outputStream.write( "Content-Type:text/html; charset=utf-8\r\n".getBytes( "UTF-8" ) );
			//outputStream.write( "\r\n".getBytes() );
			//outputStream.write( "<h1>이 페이지가 잘 보이면 실습과제 SimpleHttpServer를 시작할 준비가 된 것입니다.</h1>".getBytes( "UTF-8" ) );

		} catch( Exception ex ) {
			consoleLog( "error:" + ex );
		} finally {
			// clean-up
			try{
				if( socket != null && socket.isClosed() == false ) {
					socket.close();
				}
				
			} catch( IOException ex ) {
				consoleLog( "error:" + ex );
			}
		}			
	}

	public void consoleLog( String message ) {
		System.out.println( "[RequestHandler#" + getId() + "] " + message );
	}
	private void responseStaticResource(OutputStream outputStream,String url,String protocol)throws IOException  {
		if("/".equals(url))// /로 들어오면 index.html을 보내주겠다.
			url="/index.html";
		File file = new File("documentRoot"+url);
		if(file.exists()==false) {
			consoleLog("File Not Found : "+url);
			response404Error(outputStream,protocol);//HTTP/1.1 404 FileNotFound \r\n
			//error에 있다.
			return;
		}
		//nio
		byte[] body = Files.readAllBytes(file.toPath());//파일 내용을 한번에 읽는다
		String contentType = Files.probeContentType(file.toPath());
		//응답
		outputStream.write( (protocol + "200 OK\r\n").getBytes( "UTF-8" ) );
		outputStream.write( ("Content-Type:"+contentType+"; charset=utf-8\r\n").getBytes( "UTF-8" ) );
		outputStream.write( "\r\n".getBytes() );
		outputStream.write( body );
	}
	private void response404Error(OutputStream outputStream,String protocol) throws IOException{
		
		File file = new File(documentRoot+"/error/404.html");
		byte[] body = Files.readAllBytes(file.toPath());//파일 내용을 한번에 읽는다
		outputStream.write( (protocol + "404 FileNotFound \r\n").getBytes( "UTF-8" ) );
		outputStream.write( "\r\n".getBytes() );
		outputStream.write( body );
	}
	private void response400Error(OutputStream outputStream,String protocol) throws IOException{
		File file = new File(documentRoot+"/error/400.html");
		byte[] body = Files.readAllBytes(file.toPath());//파일 내용을 한번에 읽는다
		outputStream.write( (protocol + "400 BadRequest \r\n").getBytes( "UTF-8" ) );
		outputStream.write( "\r\n".getBytes() );
		outputStream.write( body );
	}
}
