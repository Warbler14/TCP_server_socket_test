package v04;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import v03.DataStreamUtil;

/**
 * 바이트 스트림 테스트
 * 
 * @since 2017.09.02
 * @author insung
 *
 */
public class Server04_00 {

	public static void main(String args[]) {
		ServerSocket serverSocket = null;

		try {
			// 서버소켓을 생성하고 5000번 포트와 결합(bind) 시킨다.
			int serverSocketPort = 5001;
			serverSocket = new ServerSocket(serverSocketPort);
			System.out.println(getTime() + " 서버가 준비되었습니다. port : " + serverSocketPort);

			while (true) {
				try {
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

					// 클라이언트 소켓으로 부터 받은 데이터를 출력한다.
					System.out.println("read data");
					long time01 = System.currentTimeMillis();
					String readData = waver.receiveData();

					// System.out.println("read finish");

					// System.out.println("클라이언트로부터 받은 메세지 : " + readData + " "
					// + readData.length() );

					long time02 = System.currentTimeMillis();

					// 클라이언트에 보낼 메시지 준비
					String sendData = readData;
					// System.out.println("클라이언트로 전송 할 메시지 : " + sendData + " "
					// + sendData.length() );

					// 클라이언트에 데이터를 보낸다.
					waver.serveData(sendData.getBytes());

					// System.out.println(getTime() + " 데이터를 전송했습니다." +
					// clientHostAddress);

					long time03 = System.currentTimeMillis();

					System.out.println(time02 - time01);
					System.out.println(time03 - time02);
					System.out.println(time03 - time01);

					// 스트림과 소켓을 달아준다.
					System.out.println("연결을 종료합니다. port : " + serverSocketPort);
					// waver.close();
					// socket.close();

				} catch (ConnectException ce) {
					System.out.println("ConnectException " + ce.getMessage());
				} catch (IOException ie) {
					System.out.println("IOException " + ie.getMessage());
				} catch (Exception e) {
					System.out.println("Exception " + e.getMessage());
				} // try - catch
			} // while

		} catch (IOException e) {
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
