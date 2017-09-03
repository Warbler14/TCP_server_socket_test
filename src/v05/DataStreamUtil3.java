package v05;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DataStreamUtil3 {
	
	public final static int BUFFER_SIZE = 1024;
	
	DataInputStream disFw;
	DataOutputStream dosFw;
	DataInputStream disBw;
	DataOutputStream dosBw;
	
	public DataStreamUtil3( Socket socketFw, Socket socketBw  ) {
		super();
		try{
			this.disFw = new DataInputStream( socketFw.getInputStream() );
			this.dosFw = new DataOutputStream( socketFw.getOutputStream() );
			this.disBw = new DataInputStream( socketBw.getInputStream() );
			this.dosBw = new DataOutputStream( socketBw.getOutputStream() );
		}catch( Exception e ){
			System.out.println( e.getMessage() );
		}
	}
	
	public void close() {
		try{
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
		}catch( Exception e ){
			System.out.println( e.getMessage() );
		}
	}
		
	public void sync() throws IOException {
		receiveAndServeData( disBw, dosFw );
		receiveAndServeData( disFw, dosBw );		
	}
	
	
	public void receiveAndServeData( DataInputStream dis, DataOutputStream dos ) throws IOException {
		
		if( dis == null ){
			System.out.println("DataInputStream is null");
		}
		
		if( dos == null ){
			System.out.println("DataOutputStream is null");
		}
		
		byte[] buf = new byte[BUFFER_SIZE];

		System.out.println("read start");
		while(true) {
			
			dis.read(buf);
			
			dos.write(buf);
			dos.flush();
			
			
			if( dis.available() == 0 ){
				break;
			}
			
			
		}//end while
	}
}
