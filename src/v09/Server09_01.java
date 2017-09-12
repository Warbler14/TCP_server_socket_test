package v09;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 바이트 스트림 테스트
 * 
 * @since 2017.09.02
 * @author insung
 *
 */
public class Server09_01 {

	public static void main(String args[]) {
		ServerSocket serverSocket = null;

		try {
			// 서버소켓을 생성하고 5000번 포트와 결합(bind) 시킨다.
			int localSocketPort = 5000;
			serverSocket = new ServerSocket(localSocketPort);
			System.out.println(getTime() + " 서버가 준비되었습니다." + localSocketPort);
			
			String serverIP = "127.0.0.1"; // 127.0.0.1 & localhost 본인
			System.out.println("서버에 연결중입니다. 서버 IP : " + serverIP);
			
			// 소켓을 생성하여 연결을 요청한다.
			int homeSocketPort = 5001;
			Socket socketFw = new Socket(serverIP, homeSocketPort);
			
			// 소켓의 입/출력스트림을 얻는다.
			System.out.println(getTime() + " 홈 서버에 연결 완료 " + homeSocketPort);
			
			
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			
			System.out.println(getTime() + " 연결요청을 기다립니다.");
			// 서버소켓은 클라이언트의 연결요청이 올 때까지 실행을 멈추고 계속 기다린다.
			// 클라이언트의 연결요청이 오면 클라이언트 소켓과 통신할 새로운 소켓을 생성한다.
			Socket socketBw = serverSocket.accept();
			
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			
			
			DataStreamUtil2 waver = new DataStreamUtil2( socketFw, socketBw );
			
			
			
			waver.sendAll("start");
			
			int runCount = 0;
			
			while (true) {
				try {
					
					waver.sync();


					
					if( runCount++ > 3 ){
						runCount = 0;
						waver.sendAll("heartbeat");
					}
					
					
					Thread.sleep(100);	//cpu 과점유 방지
					
				} catch (SocketException se) {
					System.out.println("!!SocketException " + se.getMessage() );
					String exceptionCode = se.getMessage();
					if("~R~BF".equals(exceptionCode)){
						System.out.println( "client side offline");
					}else if ("~W~BF".equals(exceptionCode)){
						System.out.println( "client side offline");
					}else if ("~R~FB".equals(exceptionCode)){
						System.out.println( "server side offline");
					}else if ("~W~FB".equals(exceptionCode)){
						System.out.println( "server side offline");
					}
					
					break;
				} catch (IOException ie) {
					System.out.println("!!IOException " + ie.getMessage() );
					break;
				} catch (Exception e) {
					System.out.println("!!Exception " + e.getMessage() );

					String exceptionCode = e.getMessage();
					if("~R~BF".equals(exceptionCode)){
						System.out.println( "client side offline");
					}else if ("~W~BF".equals(exceptionCode)){
						System.out.println( "client side offline");
					}else if ("~R~FB".equals(exceptionCode)){
						System.out.println( "server side offline");
					}else if ("~W~FB".equals(exceptionCode)){
						System.out.println( "server side offline");
					}
					break;
				} // try - catch
			} // while
			
			System.out.println("연결을 종료합니다." );
			waver.close();
			socketFw.close();
			socketBw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null && !serverSocket.isClosed()) {
				try {

					if (serverSocket != null && !serverSocket.isClosed()) {

					}
					serverSocket.close();
				} catch (Exception e) {
				}

			}

		}

	}

	
	static String getTime() {
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	} // getTime

}
