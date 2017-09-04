package v07;

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
	
	
	public void sync() throws Exception {
		//new OpenStream( this, 1 ).run();
		//new OpenStream( this, 0 ).run();
		
		
		if( disBw.available() == 0 ){
			//disBw.wait();
			//dosFw.wait();
			receiveAndServeData( disFw, dosBw , 8);
			//disBw.notify();
			//dosFw.notify();
		}
		
		if( disFw.available() == 0 ){
			//disFw.wait();
			//dosBw.wait();
			receiveAndServeData( disBw, dosFw , 8);
			//disFw.notify();
			//dosBw.notify();
		}
		
		//receiveAndServeData( disBw, dosFw , 8);
		//receiveAndServeData( disFw, dosBw , 8);		
	}
	
	
	
	
	public void syncBF(int mode) throws Exception {
		receiveAndServeData( disBw, dosFw, mode );	
	}
	
	public void syncFB(int mode) throws Exception {
		receiveAndServeData( disFw, dosBw, mode );		
	}
	
	public void receiveAndServeData( DataInputStream dis, DataOutputStream dos, int mode ) throws SocketException {		
		if( dis == null ){
			System.out.println("DataInputStream is null");
		}
		
		if( dos == null ){
			System.out.println("DataOutputStream is null");
		}
		
		
		System.out.println("read start " + mode);
		
		int totalLength = 0;
		try{
			totalLength = dis.readInt();			
			System.out.println( "totalLength " + totalLength);
			
		}catch( Exception e ){
			System.out.println("exception 1");
			throw new SocketException("~A~");
		}
		
		if( totalLength <= 0 ) return;
		
		byte[] buf = new byte[totalLength];
		
		try{
			dis.readFully(buf);
		}catch( Exception e ){
			System.out.println("exception 2");
			throw new SocketException("~A~");
		}
		
		try {
			dos.writeInt( totalLength );
			dos.write(buf);
			dos.flush();
		} catch (Exception e) {
			System.out.println("exception 3");
			throw new SocketException("~B~");
		}
			
					
		
		System.out.println("read end " + totalLength);
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
				case 0 : util.syncBF( mode ); break;
				case 1 : util.syncFB( mode ); break;
				default : break;
				}
			
			}catch(Exception e){
				
			}
			
		}		
	}
}
