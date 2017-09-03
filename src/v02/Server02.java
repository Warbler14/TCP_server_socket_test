package v02;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 에코 형상으로 수정
 * @since 2017.09.02
 * @author insung
 *
 */
public class Server02 {
	
	public static void main(String args[]){
		ServerSocket serverSocket = null;
	     
	     try {
	         // 서버소켓을 생성하고 5000번 포트와 결합(bind) 시킨다.
	         serverSocket = new ServerSocket(5000);
	         System.out.println(getTime() + " 서버가 준비되었습니다.");
		     
		     while (true) {
		         try {
		        	 
		        	 
		        	 
		             System.out.println(getTime() + " 연결요청을 기다립니다.");
		             // 서버소켓은 클라이언트의 연결요청이 올 때까지 실행을 멈추고 계속 기다린다.
		             // 클라이언트의 연결요청이 오면 클라이언트 소켓과 통신할 새로운 소켓을 생성한다.
		             Socket socket = serverSocket.accept();
		             
		             socket.getInetAddress();
		             
		             InetAddress clientInetAddress = socket.getInetAddress();
		             String clientHostAddress = clientInetAddress.getHostAddress();
		             System.out.println(getTime() + clientInetAddress + " 로부터 연결요청이 들어왔습니다.");
		              
		             // 클라이언트의 입출력 소켓을 얻는다.
		             DataInputStream dis = new DataInputStream(socket.getInputStream());
		             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		             
		             // 클라이언트 소켓으로 부터 받은 데이터를 출력한다.
		             String readData = dis.readUTF();
		             System.out.println("클라이언트로부터 받은 메세지 : " + readData);   
		             
		             // 클라이언트에 보낼 메시지 준비
		             String sendData = readData + " read ok, time: " + getTime();
		             System.out.println("클라이언트로 전송 할 메시지 : " + sendData);
		             
		             // 클라이언트에 데이터를 보낸다.
		             dos.writeUTF(sendData);
		             System.out.println(getTime() + " 데이터를 전송했습니다." + clientHostAddress);
		             
		             
		             
		             // 스트림과 소켓을 달아준다.
		             System.out.println("연결을 종료합니다." + clientHostAddress);
		             dos.close();
		             socket.close();
		         } catch (IOException e) {
		             e.printStackTrace();
		         } // try - catch
		     } // while
		
	     } catch (IOException e) {
	         e.printStackTrace();
	     }finally{
	    	 if( serverSocket != null && !serverSocket.isClosed() ){
	    		 try{
	    			 
	    			 if( serverSocket != null && !serverSocket.isClosed() ){
	    				 
	    			 }
	    			 serverSocket.close();
	    		 }catch(Exception e){}
	    		 
	    	 }
	    	 
	     }
	     
	}
	
	static String getTime() {
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	} // getTime

	 


}
