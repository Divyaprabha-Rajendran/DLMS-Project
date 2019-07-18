package frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Sequencer 
{
	MulticastSocket seqSocket = null;
	public final int seqPort=5555;
	public static int seqNum=1;
	public Sequencer() 
	{
		try {
			seqSocket = new MulticastSocket(seqPort);
			seqSocket.joinGroup(InetAddress.getByName("230.1.1.5-->first system"));
			seqSocket.joinGroup(InetAddress.getByName("230.1.1.5-->second system"));
			seqSocket.joinGroup(InetAddress.getByName("230.1.1.5-->third system"));
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendRequest(String req) 
	{
		
		try {
			DatagramPacket first_request = new DatagramPacket(req.getBytes(), req.length(), InetAddress.getByName("230.1.1.5-->first system"),1111);
			seqSocket.send(first_request);
			DatagramPacket second_request = new DatagramPacket(req.getBytes(), req.length(), InetAddress.getByName("230.1.1.5-->second system"),1111);
			seqSocket.send(second_request);
			DatagramPacket third_request = new DatagramPacket(req.getBytes(), req.length(), InetAddress.getByName("230.1.1.5-->third system"),1111);
			seqSocket.send(third_request);
			//such this three requests to be sent
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void receiveRequest()
	{
		while (true) {
			byte[] buffer = new byte[1000];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			try {
				seqSocket.receive(request);
				String req=request.getData().toString();
				req=req+" "+seqNum;
				sendRequest(req);
				seqNum=seqNum+1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
