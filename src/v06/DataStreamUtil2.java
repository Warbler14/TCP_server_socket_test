package v06;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class DataStreamUtil2 {
	
	public final static int BUFFER_SIZE = 1024;
	
	DataInputStream disFw;
	DataOutputStream dosFw;
	DataInputStream disBw;
	DataOutputStream dosBw;
	
	public DataStreamUtil2( Socket socketFw, Socket socketBw  ) throws Exception {
		super();
		this.disFw = new DataInputStream( socketFw.getInputStream() );
		this.dosFw = new DataOutputStream( socketFw.getOutputStream() );
		this.disBw = new DataInputStream( socketBw.getInputStream() );
		this.dosBw = new DataOutputStream( socketBw.getOutputStream() );
	}
	
	public void close() throws Exception {
		if( disFw != null ){
			dosFw.close();
		}
		
		if( disFw != null ){
			dosFw.close();
		}
		
		if( disBw != null ){
			disBw.close();
		}
		
		if( dosBw != null ){
			dosBw.close();
		}
	}
		
	public void sync() throws Exception {
		receiveAndServeData( disBw, dosFw );
		receiveAndServeData( disFw, dosBw );		
	}
	
	
	public void receiveAndServeData( DataInputStream dis, DataOutputStream dos ) throws SocketException {		
		if( dis == null ){
			System.out.println("DataInputStream is null");
		}
		
		if( dos == null ){
			System.out.println("DataOutputStream is null");
		}
		
		long sendByte = 0;
		System.out.println("read start");
		
		int totalLength = 0;
		try{
			totalLength = dis.readInt();
			System.out.println( "totalLength " + totalLength);
			
		}catch( Exception e ){
			throw new SocketException("~A~");
		}
		
		if( totalLength <= 0 ) return;
		 
		while(true) {
			byte[] buf = new byte[BUFFER_SIZE];
			
			try{
				sendByte += (long)dis.read(buf, 0, BUFFER_SIZE);
			}catch( Exception e ){
				throw new SocketException("~A~");
			}
			
			
			try {
				dos.write(buf);
				dos.flush();
			} catch (Exception e) {
				throw new SocketException("~B~");
			}
			
			
			if( sendByte >= totalLength ){
				break;
			}
			
			/*
			if( dis.available() == 0 ){
				break;
			}
			*/
			
		}//end while		
		
		System.out.println("read end " + sendByte);
	}
}
