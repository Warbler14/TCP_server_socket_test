package v10;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server10 {
	
	public static void main(String args[]){
		ServerSocket serverSocket = null;
		Socket socket = null;
		OutputStream out = null;
		DataOutputStream dos = null;
		InputStream in = null;
		DataInputStream  dis = null;
	     
	     try {
	         // 서버소켓을 생성하고 5000번 포트와 결합(bind) 시킨다.
	         serverSocket = new ServerSocket(5000);
	         serverSocket.setSoTimeout(5000);
	         System.out.println(getTime() + " 서버가 준비되었습니다.");
	         
	         //========================================
		     for (int i = 0; i < 10; i++) {
	             //========================================	             
	             try{
	            	 System.out.println(getTime() + " 연결요청을 기다립니다.");
	            	 // 서버소켓은 클라이언트의 연결요청이 올 때까지 실행을 멈추고 계속 기다린다.
	            	 // 클라이언트의 연결요청이 오면 클라이언트 소켓과 통신할 새로운 소켓을 생성한다.
	            	 socket = serverSocket.accept();
	            	 System.out.println(getTime() + socket.getInetAddress() + " 로부터 연결요청이 들어왔습니다.");
	            	 System.out.println("접속 대기 해제");
		    		 break;
		    		 
	             }catch( Exception e){
	            	 //System.out.println(e.getMessage(); );
	            	 e.printStackTrace();
	             }
	             
	             //========================================
		         
 		     } // for     
             //========================================
		     
             
             if( socket != null && socket.isConnected() ){
            	 printSocketInfo( socket , "socket active" );
            	 
            	 // 소켓의 출력스트림을 얻는다.
            	 out = socket.getOutputStream();
            	 dos = new DataOutputStream(out);
            	 in = socket.getInputStream();
            	 dis = new DataInputStream(in);
            	 
            	 Server10 thisClass = new Server10();
            	 ExecutorService executorService = Executors.newSingleThreadExecutor();

            	 DataRead dataRead = new DataRead( thisClass, dis );
            	 Future<?> future = executorService.submit( dataRead );
            	 
            	//===============================
            	for (int i = 0; i < 20; i++) {
					
            		int testData = 100 + i;
            		byte[] sendData = thisClass.intToByteArray( testData );
            		try{
            			if(i< 20){
	            			dos.write(sendData);
	            			dos.flush();
	            			System.out.println(getTime() + " 데이터를 전송했습니다." + testData);
            			}
            			
            			if( !future.isDone() ){
            				System.out.println("thread is alive");
            			}else{
            				System.out.println("thread not alive");
            				break;
            			}
            			
            			Thread.sleep(1000);
            		}catch( Exception e ){
            			System.out.println("st write Exception");
            			e.printStackTrace();
            			System.out.println("ed write Exception");
            		}
            		//========================================
				}//end for
            	
            	
            	//========================================
            	System.out.println("!!! start shutdown thread");
            	dataRead.close();
            	executorService.shutdown();
            	System.out.println("!!! end shutdown thread");
             }             
             //========================================
             
		             
		             
	     } catch (SocketException se) {
	    	 System.out.println("SocketException");
	         se.printStackTrace();		
	     } catch (IOException e) {
	         e.printStackTrace();
	     } catch (Exception e) {
	            e.printStackTrace();
	     }finally{
	    	 // 스트림과 소켓을 달아준다.
	    	 if( dis != null ){
	    		 try {
	    			 dis.close();
				} catch (Exception e2) {
					System.out.println( e2.getMessage() );
				}
	    	 }
	    	 if( dos != null ){
	    		 try {
	    			 dos.close();
				} catch (Exception e2) {
					System.out.println( e2.getMessage() );
				}
	    	 }
	    	 if( socket != null ){
	    		 try {
	    			 socket.close();
				} catch (Exception e2) {
					System.out.println( e2.getMessage() );
				}
	    	 }
	    	 if( serverSocket != null ){
	    		 try{
	    			 serverSocket.close();
	    		 }catch(Exception e){}	    		 
	    	 }
	    	 
	    	 //printSocketInfo( socket , "after close" );
	    	 
	    	 System.out.println("end of line");
	     }
	     
	}
	
	
	static class DataRead extends Thread {
		
		private DataInputStream dis = null;
		private Server10 server = null;
		private boolean isClose = false;
		
		public DataRead( Server10 server, DataInputStream dis ){
			this.server = server;
			this.dis = dis;
		}
		
		@Override
	    public void run() {
			
			byte [] buff = new byte[1024];

	        // 원격 소켓(remote socket)에 데이터를 보낸다.
	        while(true){
	            try {
	            	if( isClose ){
	            		break;
	            	}
	            	
	            	System.out.println("block to read");
	            	int readSize = dis.read(buff);
	            	
	            	if( readSize < 0 ){
	            		System.out.println("connection closed");
	            		throw new SocketException("stream off line");
	            	}
	            	
	            	int readData = server.byteArrayToInt(buff);
	            	
	            	// 소켓으로 부터 받은 데이터를 출력한다.
	            	System.out.println("받은 메세지 : " + readData + ", length : " + readSize);
				
	            } catch (SocketException e) {
	            	System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
					System.out.println( e.getMessage() );
					System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
					break;
				} catch (Exception e) {
					System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
					System.out.println( e.getMessage() );
					System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
					break;
				}
	        }//end while
	        System.out.println("end thread");	        
	    }
		
		public void close(){
			isClose = true;
		}
		
	}
	
	
	
	public byte[] intToByteArray(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte)(value >> 24);
		byteArray[1] = (byte)(value >> 16);
		byteArray[2] = (byte)(value >> 8);
		byteArray[3] = (byte)(value);
		return byteArray;
	}
	
	public  int byteArrayToInt(byte bytes[]) {
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
	} 


	public static void printSocketInfo( Socket socket , String group){
		/*
		getKeepAlive false
		getLocalAddress /127.0.0.1
		getLocalPort 5000
		getOOBInline false
		getPort 51784
		getReceiveBufferSize 8192
		getReuseAddress false
		getSendBufferSize 64512
		getSoLinger -1
		getSoTimeout 0
		getTcpNoDelay false
		getTrafficClass 0
		isBound true
		isClosed false
		isConnected true
		isInputShutdown false
		isOutputShutdown false
		hashCode 407906873
		toString Socket[addr=/127.0.0.1,port=51784,localport=5000]	
		*/
		System.out.println("==============" + group + " start ==============");
		
		try {
			System.out.println( "getKeepAlive " + socket.getKeepAlive() );
			/*
			 boolean java.net.Socket.getKeepAlive() throws SocketException


			 Tests if SO_KEEPALIVE is enabled.

			 Returns:
			 a boolean indicating whether or not SO_KEEPALIVE is enabled.
			 Throws:
			 SocketException - if there is an error in the underlying protocol, such as a TCP error.
			 Since:
			 1.3
			 See Also:
			 setKeepAlive(boolean)
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "getLocalAddress " + socket.getLocalAddress() );
			/*
			 InetAddress java.net.Socket.getLocalAddress()


			 Gets the local address to which the socket is bound.

			 Returns:
			 the local address to which the socket is bound, or the wildcard address if the socket is closed or not bound yet.
			 Since:
			 JDK1.1
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "getLocalPort " + socket.getLocalPort() );
			/*
			 int java.net.Socket.getLocalPort()


			 Returns the local port number to which this socket is bound. 

			 If the socket was bound prior to being closed, then this method will continue to return the local port number after the socket is closed.

			 Returns:
			 the local port number to which this socket is bound or -1 if the socket is not bound yet.
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "getOOBInline " + socket.getOOBInline() );
			/*
			 boolean java.net.Socket.getOOBInline() throws SocketException


			 Tests if OOBINLINE is enabled.

			 Returns:
			 a boolean indicating whether or not OOBINLINE is enabled.
			 Throws:
			 SocketException - if there is an error in the underlying protocol, such as a TCP error.
			 Since:
			 1.4
			 See Also:
			 setOOBInline(boolean)
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}			
		try {
			System.out.println( "getPort " + socket.getPort() );
			/*
			 int java.net.Socket.getPort()


			 Returns the remote port number to which this socket is connected. 

			 If the socket was connected prior to being closed, then this method will continue to return the connected port number after the socket is closed.

			 Returns:
			 the remote port number to which this socket is connected, or 0 if the socket is not connected yet.
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}			
		try {
			System.out.println( "getReceiveBufferSize " + socket.getReceiveBufferSize() );
			/*
			 int java.net.Socket.getReceiveBufferSize() throws SocketException


			 Gets the value of the SO_RCVBUF option for this Socket, that is the buffer size used by the platform for input on this Socket.

			 Returns:
			 the value of the SO_RCVBUF option for this Socket.
			 Throws:
			 SocketException - if there is an error in the underlying protocol, such as a TCP error.
			 Since:
			 1.2
			 See Also:
			 setReceiveBufferSize(int)
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "getReuseAddress " + socket.getReuseAddress() );
			/*
			 boolean java.net.Socket.getReuseAddress() throws SocketException


			 Tests if SO_REUSEADDR is enabled.

			 Returns:
			 a boolean indicating whether or not SO_REUSEADDR is enabled.
			 Throws:
			 SocketException - if there is an error in the underlying protocol, such as a TCP error.
			 Since:
			 1.4
			 See Also:
			 setReuseAddress(boolean)
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "getSendBufferSize " + socket.getSendBufferSize() );
			/*
			 int java.net.Socket.getSendBufferSize() throws SocketException


			 Get value of the SO_SNDBUF option for this Socket, that is the buffer size used by the platform for output on this Socket.

			 Returns:
			 the value of the SO_SNDBUF option for this Socket.
			 Throws:
			 SocketException - if there is an error in the underlying protocol, such as a TCP error.
			 Since:
			 1.2
			 See Also:
			 setSendBufferSize(int)
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "getSoLinger " + socket.getSoLinger() );
			/*
			 int java.net.Socket.getSoLinger() throws SocketException


			 Returns setting for SO_LINGER. -1 returns implies that the option is disabled. The setting only affects socket close.

			 Returns:
			 the setting for SO_LINGER.
			 Throws:
			 SocketException - if there is an error in the underlying protocol, such as a TCP error.
			 Since:
			 JDK1.1
			 See Also:
			 setSoLinger(boolean, int)
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "getSoTimeout " + socket.getSoTimeout() );
			/*
			 int java.net.Socket.getSoTimeout() throws SocketException


			 Returns setting for SO_TIMEOUT. 0 returns implies that the option is disabled (i.e., timeout of infinity).

			 Returns:
			 the setting for SO_TIMEOUT
			 Throws:
			 SocketException - if there is an error in the underlying protocol, such as a TCP error.
			 Since:
			 JDK1.1
			 See Also:
			 setSoTimeout(int)
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "getTcpNoDelay " + socket.getTcpNoDelay() );
			/*
			 boolean java.net.Socket.getTcpNoDelay() throws SocketException


			 Tests if TCP_NODELAY is enabled.

			 Returns:
			 a boolean indicating whether or not TCP_NODELAY is enabled.
			 Throws:
			 SocketException - if there is an error in the underlying protocol, such as a TCP error.
			 Since:
			 JDK1.1
			 See Also:
			 setTcpNoDelay(boolean)
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "getTrafficClass " + socket.getTrafficClass() );
			/*
			 int java.net.Socket.getTrafficClass() throws SocketException


			 Gets traffic class or type-of-service in the IP header for packets sent from this Socket 

			 As the underlying network implementation may ignore the traffic class or type-of-service set using setTrafficClass(int) this method may return a different value than was previously set using the setTrafficClass(int) method on this Socket.

			 Returns:
			 the traffic class or type-of-service already set
			 Throws:
			 SocketException - if there is an error obtaining the traffic class or type-of-service value.
			 Since:
			 1.4
			 See Also:
			 setTrafficClass(int)
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "isBound " + socket.isBound() );
			/*
			 boolean java.net.Socket.isBound()


			 Returns the binding state of the socket. 

			 Note: Closing a socket doesn't clear its binding state, which means this method will return true for a closed socket (see isClosed()) if it was successfuly bound prior to being closed.

			 Returns:
			 true if the socket was successfuly bound to an address
			 Since:
			 1.4
			 See Also:
			 bind
			*/				
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "isClosed " + socket.isClosed() );
			/*
			 boolean java.net.Socket.isClosed()


			 Returns the closed state of the socket.

			 Returns:
			 true if the socket has been closed
			 Since:
			 1.4
			 See Also:
			 close
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "isConnected " + socket.isConnected() );
			/*
			 boolean java.net.Socket.isConnected()


			 Returns the connection state of the socket. 

			 Note: Closing a socket doesn't clear its connection state, which means this method will return true for a closed socket (see isClosed()) if it was successfuly connected prior to being closed.

			 Returns:
			 true if the socket was successfuly connected to a server
			 Since:
			 1.4
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "isInputShutdown " + socket.isInputShutdown() );
			/*
			 boolean java.net.Socket.isInputShutdown()


			 Returns whether the read-half of the socket connection is closed.

			 Returns:
			 true if the input of the socket has been shutdown
			 Since:
			 1.4
			 See Also:
			 shutdownInput
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "isOutputShutdown " + socket.isOutputShutdown() );
			/*
			 boolean java.net.Socket.isOutputShutdown()


			 Returns whether the write-half of the socket connection is closed.

			 Returns:
			 true if the output of the socket has been shutdown
			 Since:
			 1.4
			 See Also:
			 shutdownOutput
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println( "hashCode " + socket.hashCode() );
			/*
			 int java.lang.Object.hashCode()


			 Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by java.util.HashMap. 

			 The general contract of hashCode is: 

			 Whenever it is invoked on the same object more than once during an execution of a Java application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of an application to another execution of the same application. 
			 If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result. 
			 It is not required that if two objects are unequal according to the java.lang.Object.equals(java.lang.Object) method, then calling the hashCode method on each of the two objects must produce distinct integer results. However, the programmer should be aware that producing distinct integer results for unequal objects may improve the performance of hash tables. 
			 As much as is reasonably practical, the hashCode method defined by class Object does return distinct integers for distinct objects. (This is typically implemented by converting the internal address of the object into an integer, but this implementation technique is not required by the JavaTM programming language.)

			 Returns:
			 a hash code value for this object.
			 See Also:
			 java.lang.Object.equals(java.lang.Object)
			 java.lang.System.identityHashCode
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println( "toString " + socket.toString() );
			/*
			 String java.net.Socket.toString()


			 Converts this socket to a String.

			 Overrides: toString() in Object
			 Returns:
			 a string representation of this socket.
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("==============" + group + " end ==============");
			
	}
	
	static String getTime() {
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	} // getTime

	 


}
