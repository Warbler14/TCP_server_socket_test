package v09;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 바이트 스트림 테스트
 * 
 * @since 2017.09.02
 * @author insung
 *
 */
public class Server09_00 {

	public static void main(String args[]) {
		ServerSocket serverSocket = null;

		try {
			// 서버소켓을 생성하고 5000번 포트와 결합(bind) 시킨다.
			int serverSocketPort = 5001;
			serverSocket = new ServerSocket(serverSocketPort);
			System.out.println(getTime() + " 서버가 준비되었습니다. port : " + serverSocketPort);

			System.out.println(getTime() + " 연결요청을 기다립니다.");
			// 서버소켓은 클라이언트의 연결요청이 올 때까지 실행을 멈추고 계속 기다린다.
			// 클라이언트의 연결요청이 오면 클라이언트 소켓과 통신할 새로운 소켓을 생성한다.
			Socket socket = serverSocket.accept();
			socket.getInetAddress();
			
			InetAddress clientInetAddress = socket.getInetAddress();
			String clientHostAddress = clientInetAddress.getHostAddress();
			System.out.println(getTime() + clientHostAddress + " 로부터 연결요청이 들어왔습니다.");

			// 클라이언트의 입출력 소켓을 얻는다.
			DataStreamUtil waver = new DataStreamUtil(socket);
			
			System.out.println("======================READY======================");
			
			while(true){
				String dataRead = waver.receiveData();
				if("start".equals( dataRead)){
					break;
				}
				
				Thread.sleep(100);	//cpu 과점유 방지
			}
			
			System.out.println("======================START======================");
			
			for (int i = 0; i < 300; i++) {
				
				
				// 소켓으로 부터 받은 데이터를 출력한다.
				String dataRead = waver.receiveData();
				System.out.println("서버로부터 받은 메세지 : " + dataRead );
				
				
				if(dataRead != null && "heartbeat".equals(dataRead)){
					System.out.println("!!! read heart beat !!!");
					
				}else{
					
					
						// 서버에 보낼 메시지 준비
						String sendData = null;
						sendData = getTime() + message01( 1 );
						
						byte [] sendByteData = sendData.getBytes();
						System.out.println("size " + sendData.length() + " " + sendByteData.length);
						System.out.println("서버로 전송 할 메시지 : " + sendData );
			
						// 서버로 데이터를 전송한다.				
						waver.serveData( sendByteData );
						
						//System.out.println("send finish");
					
					
				}
				
				
				
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					// TODO: handle exception
				}
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			}//end for
 
			// 스트림과 소켓을 달아준다.
			System.out.println("연결을 종료합니다. port : " + serverSocketPort  );
			waver.close();
			socket.close();
			
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
	
	private static String message01( int max){
		String result = "";
		String code = "";
		for (int i = 0; i < 256; i++) {
			code += String.valueOf('A');
		}
		
		//System.out.println( "code : " + code + "\n length " + code.length());
		
		for (int i = 0; i < max;i++) {
			result += code;
		}
		//System.out.println( result.length() );
		return result;
	}
	
	static String getTime() {
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	} // getTime

}
