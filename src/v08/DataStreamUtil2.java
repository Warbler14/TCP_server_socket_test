package v08;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class DataStreamUtil2 {
	
	public final static int BUFFER_SIZE = 1024;
	
	Socket socketFw;
	Socket socketBw;
	DataInputStream disFw;
	DataOutputStream dosFw;
	DataInputStream disBw;
	DataOutputStream dosBw;
	
	public DataStreamUtil2( Socket socketFw, Socket socketBw  ) throws Exception {
		super();
		this.socketFw = socketFw;
		this.socketBw = socketBw;
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
	
	public void sendAll( String message ) throws Exception {
		serveData( dosFw, message.getBytes() );
		serveData( dosBw, message.getBytes() );
	}
	
	public void serveData( DataOutputStream dos, byte [] serveData ) throws IOException {
		if( dos == null ){
			System.out.println("DataOutputStream is null");
			return;
		}
		
		if( serveData == null ){
			System.out.println("serveData is null");
			return;
		}
		
		dos.writeInt( serveData.length );
		dos.write(serveData);
		dos.flush();
	}
	
	
	public void sync() throws SocketException, IOException {
		//new OpenStream( this, 1 ).run();
		//new OpenStream( this, 0 ).run();
		
		
		if( disFw.available() != 0 && socketFw.isConnected() ){
			receiveAndServeData( disFw, dosBw , "FB");
		}else{
			System.out.println("disFw_dosBw do nothing");
		}
		
		if( disBw.available() != 0 && socketBw.isConnected() ){
			receiveAndServeData( disBw, dosFw , "BF");
		}else{
			System.out.println("disBw_dosFw do nothing");
		}
		
		if(socketFw.isClosed()){
			System.out.println("~R~FB !!!!!!!!!!!!!!!!" );
			throw new SocketException("~R~FB");
		}
		
		if( socketBw.isClosed() ){
			System.out.println("~R~BF !!!!!!!!!!!!!!!!" );
			throw new SocketException("~R~BF");
		}
		//receiveAndServeData( disBw, dosFw , 8);
		//receiveAndServeData( disFw, dosBw , 8);		
	}
	
	
	
	
	public void syncBF() throws Exception {
		receiveAndServeData( disBw, dosFw, "BF" );	
	}
	
	public void syncFB() throws Exception {
		receiveAndServeData( disFw, dosBw, "FB" );		
	}
	
	public void receiveAndServeData( DataInputStream dis, DataOutputStream dos, String mode ) throws SocketException {		
		if( dis == null ){
			System.out.println("DataInputStream is null");
		}
		
		if( dos == null ){
			System.out.println("DataOutputStream is null");
		}
		
		
		System.out.println(mode + "read start ");
		
		int totalLength = 0;
		try{
			totalLength = dis.readInt();			
			System.out.println( "totalLength " + totalLength);
			
		}catch( Exception e ){
			System.out.println("exception 1");
			throw new SocketException("~R~"+mode);
		}
		
		
		
		if( totalLength <= 0 ) return;
		
		byte[] buf = new byte[totalLength];
		
		try{
			dis.readFully(buf);
		}catch( Exception e ){
			System.out.println("exception 2");
			throw new SocketException("~R~"+mode);
		}
		System.out.println(mode + "read end " + totalLength);
		
		
		
		System.out.println(mode + "write start " + totalLength);
		try {
			dos.writeInt( totalLength );
			dos.write(buf);
			dos.flush();
		} catch (Exception e) {
			System.out.println("exception 3");
			throw new SocketException("~W~"+mode);
		}
		
		System.out.println(mode + "write end " + totalLength);
	}
	
	
	
	
	
	
	
	public class OpenStream implements Runnable{
		DataStreamUtil2 util;
		int mode = 0;
		
		
		public OpenStream(DataStreamUtil2 util, int mod){
			this.util = util;
			this.mode = mod;
		}
		
		
		@Override
		public void run() {
			try{
				switch( mode ){
				case 0 : util.syncBF(); break;
				case 1 : util.syncFB(); break;
				default : break;
				}
			
			}catch(Exception e){
				
			}
			
		}		
	}
}
