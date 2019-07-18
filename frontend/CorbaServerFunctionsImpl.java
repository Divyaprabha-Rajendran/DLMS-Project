package frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.omg.CORBA.ORB;

import frontend.CorbaServerInterface.*;

public class CorbaServerFunctionsImpl extends CorbaServerInterfacePOA
{
	
	private ORB orb;
	DatagramSocket FESocket = null;
	
	public CorbaServerFunctionsImpl()
	{
		try {
			FESocket = new DatagramSocket();
		} 
		catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	  public void setORB(ORB orb_val) {
	    orb = orb_val; 
	  }

	@Override
	public String addItem(String userId, String itemId, String itemName, int quantity) 
	{
		String request=userId+","+itemId+","+itemName+","+quantity;
		sendRequestToSequencer(request);
		return null;
	}

	@Override
	public String deleteItem(String userId, String itemId, int quantity) 
	{
		String request=userId+","+itemId+","+quantity;
		sendRequestToSequencer(request);
		return null;
	}

	@Override
	public String listItemAvailability(String userId) 
	{
		String request = userId;
		sendRequestToSequencer(request);
		return null;
	}

	@Override
	public String borrowItem(String userId, String itemId, int no_days) 
	{
		String request = userId+","+itemId+","+no_days;
		sendRequestToSequencer(request);
		return null;
	}

	@Override
	public String findItem(String userId, String itemName) 
	{
		String request=userId+","+itemName;
		sendRequestToSequencer(request);
		return null;
	}

	@Override
	public String returnItem(String userId, String itemId) 
	{
		String request=userId+","+itemId;
		sendRequestToSequencer(request);
		return null;
	}

	
	@Override
	public String exchangeItems(String userId, String newItemId, String oldItemId) 
	{
		String request=userId+","+newItemId+","+oldItemId;
		sendRequestToSequencer(request);
		return null;
	}

	public void sendRequestToSequencer(String request)
	{
		//byte[] buffer = new byte[1000];
		try {
			InetAddress SeqHost = InetAddress.getByName("localhost");
			DatagramPacket data_request = new DatagramPacket(request.getBytes(), request.length(),SeqHost,1111);
			FESocket.send(data_request);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
