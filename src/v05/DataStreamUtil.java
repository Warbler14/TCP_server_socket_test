package v05;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DataStreamUtil {
	
	public final static int BUFFER_SIZE = 1024;
	
	DataInputStream dis;
	DataOutputStream dos;
	
	public DataStreamUtil( DataInputStream dis, DataOutputStream dos ) {
		super();
		this.dis = dis;
		this.dos = dos;
	}
	
	public DataStreamUtil( InputStream is, OutputStream os ) {
		super();
		this.dis = new DataInputStream( is );
		this.dos = new DataOutputStream( os );
	}
	
	public DataStreamUtil( Socket socket ) throws Exception {
		super();
		this.dis = new DataInputStream( socket.getInputStream() );
		this.dos = new DataOutputStream( socket.getOutputStream() );
	}

	public void close() throws Exception {
		if( this.dis != null ){
			this.dis.close();
		}
		
		if( this.dos != null ){
			this.dos.close();
		}
	}
	
	public void serveData( byte [] serveData ) throws IOException {
		if( this.dos == null ){
			System.out.println("DataOutputStream is null");
			return;
		}
		
		if( serveData == null ){
			System.out.println("serveData is null");
			return;
		}
		
		this.dos.write(serveData);
		this.dos.flush();
	}
	
	public String receiveData() throws IOException {
		if( this.dis == null ){
			System.out.println("DataInputStream is null");
			return null;
		}
		
		byte[] buf = new byte[BUFFER_SIZE];
		StringBuffer strbuf = new StringBuffer(BUFFER_SIZE);

		int read = 0;
		int runCount = 0;
		System.out.println("read start");
		while(true) {
			
			read = this.dis.read(buf);
			
			strbuf.append(new String(buf, 0, read));
			System.out.println("[" + runCount + "] buff : " + read + ", total : " + strbuf.length());
			runCount++;
			if( this.dis.available() == 0 ){
				break;
			}
		}
		System.out.println("read finish");
		return new String(strbuf);
	}
}
