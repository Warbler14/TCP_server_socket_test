package v07;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

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
		
		this.dos.writeInt( serveData.length );
		this.dos.write(serveData);
		this.dos.flush();
		System.out.println("serve ok" + serveData.length);
	}
	
	public String receiveData() throws IOException {
		if( this.dis == null ){
			System.out.println("DataInputStream is null");
			return null;
		}
		
		int totalLength = 0;
		try{
			System.out.println("~~~~~~~~~~GET TOT LENGTH~~~~~~~~~~");
			totalLength = dis.readInt();
			System.out.println( "totalLength " + totalLength);
			
		}catch( Exception e ){
			throw new IOException("~A~");
		}
		
		if( totalLength <= 0 ) return null;
		
		byte[] buf = new byte[totalLength];
		StringBuffer strbuf = new StringBuffer(totalLength);

		System.out.println("read start");		
			
		this.dis.readFully(buf);
			
		strbuf.append(new String(buf, 0, totalLength));
		System.out.println("total : " + strbuf.length());
			
		
		System.out.println("read finish");
		return new String(strbuf);
	}
}
