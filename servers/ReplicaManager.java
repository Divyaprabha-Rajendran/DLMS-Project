package servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.spi.DirStateFactory.Result;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import entities.Item;
import entities.User;
import frontend.CorbaServerFunctionsImpl;
import frontend.CorbaServerInterface.*;
import interfacesDef.UMinterfacesHelper;

public class ReplicaManager 
{
	MulticastSocket aSocket = null;
	public final String replicaId="1";
	
	public LinkedList ConcordiaBackup;
	public LinkedList McGillBackup;
	public LinkedList MontrealBackup;
	
	LinkedHashMap<Integer , String > requestMap;
	
	static int seqNum;
	
	boolean SWFailureFlag;
	boolean CrashFailureFlag;
	int SWFailureCnt;
	
    boolean serverRunning;
	
	public ReplicaManager()
	{
		try {
			seqNum=0;
			ConcordiaBackup = new LinkedList<>();
			McGillBackup = new LinkedList<>();
			MontrealBackup =  new LinkedList<>();
			requestMap=new LinkedHashMap<Integer, String >();
			aSocket = new MulticastSocket(1313);
			aSocket.joinGroup(InetAddress.getByName("230.1.1.5"));	
			System.out.println("Server Started............");
			initializeServers();
			System.out.println("Server Initialized.........");
		}
		catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	
	public void initializeServers() throws SecurityException, IOException
	{
		ConcordiaServer.InitializeConcordiaServer();
		McGillServer.InitializeMcGillServer();
		MontrealServer.InitializeMontrealServer();
		
		System.out.println("All servers are running..");
		
		this.serverRunning=true;
	}
	
	/*public void updateConcordiaBackup()
	{
		if(ConcordiaBackup.size()>0)
			ConcordiaBackup.clear();
		
		ConcordiaBackup.add(ConcordiaServer.userList);
		ConcordiaBackup.add(ConcordiaServer.itemList);
		ConcordiaBackup.add(ConcordiaServer.waitList);
		
	}
	
	public void updateMcGillBackup()
	{
		if(McGillBackup.size()>0)
			McGillBackup.clear();
		
		McGillBackup.add(McGillServer.userList);
		McGillBackup.add(McGillServer.itemList);
		McGillBackup.add(McGillServer.waitList);
	}
	
	public void updateMontrealBackup()
	{
		if(MontrealBackup.size()>0)
			MontrealBackup.clear();
		
		MontrealBackup.add(MontrealServer.userList);
		MontrealBackup.add(MontrealServer.itemList);
		MontrealBackup.add(MontrealServer.waitList);
	}*/
	
	public void shutDownAllServers()
	{
		System.out.println("Shutting Concordia server... ");
		ConcordiaServer.shutdown();
		System.out.println("Shutting McGill server... ");
		McGillServer.shutdown();
		System.out.println("Shutting Montreal server... ");
		MontrealServer.shutdown();
		this.serverRunning=false;
	}
	
	public void restartAllServers() throws SecurityException, IOException, InterruptedException
	{
		initializeServers();
	
		
		for (String request : requestMap.values())
		{
			IdentifyRequest(request);
		}
		
		System.out.println("Concordia server restarted...");
			
		System.out.println("McGill server restarted...");
	
		System.out.println("Montreal server restarted...");
	}
	
	
	/*public boolean checkSequence(String request)
	{
		String req[]=request.split(",");
		int curr_seq=Integer.parseInt(req[req.length-1]);
		int expecting_seq=seqNum+1;
		if (curr_seq==expecting_seq)
		{
			System.out.println("Requests are in order..current sequence number "+seqNum);
			seqNum=seqNum+1;
			requestMap.put(Integer.parseInt(req[req.length-1]), request);
			return true;
		}
		return false;
	}*/
	
	public String IdentifyRequest(String request) throws InterruptedException, IOException
	{
		String result="";
		String req[]=request.split(",");
		//String id_pos = req[1].charAt(3)+"".toUpperCase();
					switch(Integer.parseInt(req[0]))
			{
			case 1: 
					{
						if (serverRunning)
						{
						if(req[1].toUpperCase().contains("CON") )
							result=ConManagerAddItem(req[1], req[2], req[3], Integer.parseInt(req[4]));
						else if(req[1].toUpperCase().contains("MCG"))
							result=McGManagerAddItem(req[1], req[2], req[3], Integer.parseInt(req[4]));
						else if(req[1].toUpperCase().contains("MON"))
							result=MonManagerAddItem(req[1], req[2], req[3], Integer.parseInt(req[4]));
						}
						break;
			         }
			case 2: 
					{
						if (serverRunning)
						{
						if(req[1].toUpperCase().contains("CON"))
							result=ConManagerRemoveItem(req[1], req[2], Integer.parseInt(req[3]));
						else if(req[1].toUpperCase().contains("MCG"))
							result=McGManagerRemoveItem(req[1], req[2], Integer.parseInt(req[3]));
						else if(req[1].toUpperCase().contains("MON"))
							result=MonManagerRemoveItem(req[1], req[2], Integer.parseInt(req[3]));
						
						}
						break;
			        }
			case 3: 
					{
						if (serverRunning)
						{
						if(req[1].toUpperCase().contains("CON"))
							result=ConManagerListItemAvailability(req[1]);
						else if(req[1].toUpperCase().contains("MCG"))
							result=McGManagerListItemAvailability(req[1]);
						else if(req[1].toUpperCase().contains("MON"))
							result=MonManagerListItemAvailability(req[1]);
						
						}
						break;
			        }

			case 4: 
					 {
						 if (serverRunning)
						 {
							 System.out.println("inside borrow.."+serverRunning);
						 	if(req[1].toUpperCase().contains("CON"))
						 		result=ConUserBorrowItem(req[1], req[2]);
							else if(req[1].toUpperCase().contains("MCG"))
								result=McGUserBorrowItem(req[1], req[2]);
							else if(req[1].toUpperCase().contains("MON"))
								result=MonUserborrowItem(req[1], req[2]);
						 }
						 System.out.println("outside borrow.."+serverRunning);
						 	break;
			         }
			case 5: 
					{
						if(serverRunning)
						{
						if(req[1].toUpperCase().contains("CON"))
							result=ConUserReturnItem(req[1], req[2]);
						else if(req[1].toUpperCase().contains("MCG"))
							result=McGUserReturnItem(req[1], req[2]);
						else if(req[1].toUpperCase().contains("MON"))
							result=MonUserReturnItem(req[1], req[2]);
						}
						break;
			        }
			case 6: 
					{
						if(serverRunning)
						{
						if(req[1].toUpperCase().contains("CON"))
							result=ConUserFindItem(req[1], req[2]);
						else if(req[1].toUpperCase().contains("MCG"))
							result=McGUserFindItem(req[1], req[2]);
						else if(req[1].toUpperCase().contains("MON"))
							result=MonUserFindItem(req[1], req[2]);
						}
						break;
			        }
			case 7:
					{
						if (serverRunning)
						{
						if(req[1].toUpperCase().contains("CON"))
							result=ConUserExchangeItems(req[3], req[2], req[1]);
						else if(req[1].toUpperCase().contains("MCG"))
							result=McGUserExchangeItems(req[3], req[2], req[1]);
						else if(req[1].toUpperCase().contains("MON"))
							result=MonUserExchangeItems(req[3], req[2], req[1]);
						}
						break;
					}
			case 8:
			{
				result="waitingList#success";
			}
				
			case 9:
				{
					if(req[1].equals(constants.SW_FAILURE))
					{
						if(replicaId.equals(req[2]))
						{
							this.SWFailureCnt++;
							if(this.SWFailureCnt == 3)
							{
								this.SWFailureCnt = 0;
								this.SWFailureFlag = true;
							}
						}
						else
						{
							System.out.println("Replica " + req[2] + " has encountered a s/w failure");
						} 
					}
					else if(req[1].equals(constants.CRASH_FAILURE))
					{
						if(replicaId.equals(req[2]))
						{
							if(!this.CrashFailureFlag) //first time when this flag is false then it will do crash
							{
								System.out.println("failure = true");
								this.CrashFailureFlag = true;
								this.shutDownAllServers();
							}
							else //for the next req. it will restart all servers
							{
								System.out.println("failure = false");
								this.restartAllServers();
								this.CrashFailureFlag = false;
							}
							
						}
						else
						{
							System.out.println("Replica " + req[2] + " has encountered a crash failure");
						}
					}
					break;
				}
			case 10:
			{
				if(this.replicaId.equals(req[1]))
				{
					if(!this.CrashFailureFlag) //first time when this flag is false then it will do crash
					{
						this.CrashFailureFlag = true;
						this.shutDownAllServers();
						//return result;
					}
				}
				break;
			}	
		
		}
				return result;
	}
	
	public String ConManagerAddItem(String managerId, String itemID, String itemName, int quantity) {
		String result="";
		try {
			
		if (ConcordiaServer.itemList.containsKey(itemID))
		{
			Item curr_book = ConcordiaServer.itemList.get(itemID);
			int curr_quantity=curr_book.getQuantity();
			curr_quantity=curr_quantity+quantity;
			curr_book.setQuantity(curr_quantity);
			System.out.println(curr_book.getItemId()+" is updated to "+curr_book.getQuantity()+" successfully by "+ managerId);
			ConcordiaServer.logger.info(curr_book.getItemId()+" is updated to "+curr_book.getQuantity()+" successfully by "+ managerId);
			if (ConcordiaServer.waitList.size()>0 && ConcordiaServer.waitList.containsKey(itemID))
			{
				for (int i=0;i<quantity;i++)
				{
					if(ConcordiaServer.waitList.get(itemID).size()>0)
					{
						ConcordiaServer.adjustWaitlist(itemID);
					}
				}
			}
			result="Request success: "+itemID+"#success";
		}
		else
		{
			System.out.println(itemID+" not found");
			ConcordiaServer.itemList.put(itemID, new Item(itemID, itemName, quantity));
			System.out.println(itemID+" is added to Concordia library with a quantity of "+quantity+" successfully");
			result="Request success: "+itemID+"#success";
		}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
	
		return result;
	}

	
	public String ConManagerRemoveItem(String managerId, String itemID, int Quantity) {

		String result="";
		if (ConcordiaServer.itemList.containsKey(itemID))
		{
			Item curr_book = ConcordiaServer.itemList.get(itemID);
			int curr_quantity=curr_book.getQuantity();
			if(curr_quantity==Quantity)
			{
				ConcordiaServer.itemList.remove(itemID);
				System.out.println(itemID+" is successfully deleted by "+managerId);
				ConcordiaServer.logger.info(itemID+" is successfully deleted by "+managerId);
				for (Entry<String, User> entry : ConcordiaServer.userList.entrySet())
				{
					String key = entry.getKey().toString();
					User curr_user = entry.getValue();
					if (curr_user.getItems().contains(itemID))
					{
						HashSet<String> temp = curr_user.getItems();
						temp.remove(itemID);
						curr_user.setItems(temp);
					}
				}
	         result="Request success "+itemID+"#success";
			}
			else
			{
				curr_quantity=curr_quantity-Quantity;
				curr_book.setQuantity(curr_quantity);
				System.out.println(curr_book.getItemId()+" is reduced to "+curr_book.getQuantity()+" successfully by "+ managerId);
				ConcordiaServer.logger.info(curr_book.getItemId()+" is reduced to "+curr_book.getQuantity()+" successfully by "+ managerId);
				result="Request success "+itemID+"#success";
			}
		}
		else
		{
			System.out.println("Book not found "+itemID);
			ConcordiaServer.logger.info("Book not found "+itemID);
			result="Request failure "+itemID+"#failure";
		}
	
		return result;
	}

	
	public String ConManagerListItemAvailability(String userId) {
		String print_list="";
		System.out.println("listing available books...");
		for (Entry<String, Item> entry : ConcordiaServer.itemList.entrySet())
		{
		String key = entry.getKey().toString();
		Item curr_item = entry.getValue();
		print_list=print_list+(curr_item.getItemId()+"     "+curr_item.getItemName()+"    "+curr_item.getQuantity());
		}
		System.out.println(print_list);
		return print_list+"#success";
	}

	public String McGManagerAddItem(String managerId, String itemID, String itemName, int quantity) {
		String result="";
		try {
		if (McGillServer.itemList.containsKey(itemID))
		{
			Item curr_book = McGillServer.itemList.get(itemID);
			int curr_quantity=curr_book.getQuantity();
			curr_quantity=curr_quantity+quantity;
			curr_book.setQuantity(curr_quantity);
			System.out.println(curr_book.getItemId()+" is updated to "+curr_book.getQuantity()+" successfully by "+ managerId);
			McGillServer.logger.info(curr_book.getItemId()+" is updated to "+curr_book.getQuantity()+" successfully by "+ managerId);
			if (McGillServer.waitList.size()>0 && McGillServer.waitList.containsKey(itemID))
			{
				for (int i=0;i<quantity;i++)
				{
					if(McGillServer.waitList.get(itemID).size()>0)
					{
						McGillServer.adjustWaitlist(itemID);
					}
				}
			}
			result="Request success: "+itemID+"#success";
		}
		else
		{
			System.out.println(itemID+" not found");
			McGillServer.itemList.put(itemID, new Item(itemID, itemName, quantity));
			System.out.println(itemID+" is added to Concordia library with a quantity of "+quantity+" successfully");
			result="Request success: "+itemID+"#success";	
		}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
	
		return result;
	}

	
	public String McGManagerRemoveItem(String managerId, String itemID, int Quantity) {

		String result="";
		if (McGillServer.itemList.containsKey(itemID))
		{
			Item curr_book = McGillServer.itemList.get(itemID);
			int curr_quantity=curr_book.getQuantity();
			if(curr_quantity==Quantity)
			{
				McGillServer.itemList.remove(itemID);
				System.out.println(itemID+" is successfully deleted by "+managerId);
				McGillServer.logger.info(itemID+" is successfully deleted by "+managerId);
				for (Entry<String, User> entry : McGillServer.userList.entrySet())
				{
					String key = entry.getKey().toString();
					User curr_user = entry.getValue();
					if (curr_user.getItems().contains(itemID))
					{
						HashSet<String> temp = curr_user.getItems();
						temp.remove(itemID);
						curr_user.setItems(temp);
					}
				}
	
			}
			else
			{
				curr_quantity=curr_quantity-Quantity;
				curr_book.setQuantity(curr_quantity);
				System.out.println(curr_book.getItemId()+" is reduced to "+curr_book.getQuantity()+" successfully by "+ managerId);
				McGillServer.logger.info(curr_book.getItemId()+" is reduced to "+curr_book.getQuantity()+" successfully by "+ managerId);
			}
			result="Request success "+itemID+"#success";
		}
		else
		{
			System.out.println("Book not found "+itemID);
			McGillServer.logger.info("Book not found "+itemID);
			result="Request failure "+itemID+"#failure";
		}
	
		return result;
	}

	
	public String McGManagerListItemAvailability(String userId) {
		String print_list="";
		System.out.println("listing available books...");
		for (Entry<String, Item> entry : McGillServer.itemList.entrySet())
		{
		String key = entry.getKey().toString();
		Item curr_item = entry.getValue();
		print_list=print_list+(curr_item.getItemId()+"     "+curr_item.getItemName()+"    "+curr_item.getQuantity());
		}
		System.out.println(print_list);
		return print_list+"#success";
	}

	public String MonManagerAddItem(String managerId, String itemID, String itemName, int quantity) {
		String result="";
		try {
		if (MontrealServer.itemList.containsKey(itemID))
		{
			Item curr_book = MontrealServer.itemList.get(itemID);
			int curr_quantity=curr_book.getQuantity();
			curr_quantity=curr_quantity+quantity;
			curr_book.setQuantity(curr_quantity);
			System.out.println(curr_book.getItemId()+" is updated to "+curr_book.getQuantity()+" successfully by "+ managerId);
			MontrealServer.logger.info(curr_book.getItemId()+" is updated to "+curr_book.getQuantity()+" successfully by "+ managerId);
			if (MontrealServer.waitList.size()>0 && MontrealServer.waitList.containsKey(itemID))
			{
				for (int i=0;i<quantity;i++)
				{
					if(MontrealServer.waitList.get(itemID).size()>0)
					{
						MontrealServer.adjustWaitlist(itemID);
					}
				}
			}
			result="Request success: "+itemID+"#success";
		}
		else
		{
			System.out.println(itemID+" not found");
			MontrealServer.itemList.put(itemID, new Item(itemID, itemName, quantity));
			System.out.println(itemID+" is added to Concordia library with a quantity of "+quantity+" successfully");
			result="Request success: "+itemID+"#success";
		}
		
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
	
		return result;
	}

	
	public String MonManagerRemoveItem(String managerId, String itemID, int Quantity) {

		String result="";
		if (MontrealServer.itemList.containsKey(itemID))
		{
			Item curr_book = MontrealServer.itemList.get(itemID);
			int curr_quantity=curr_book.getQuantity();
			if(curr_quantity==Quantity)
			{
				MontrealServer.itemList.remove(itemID);
				System.out.println(itemID+" is successfully deleted by "+managerId);
				MontrealServer.logger.info(itemID+" is successfully deleted by "+managerId);
				for (Entry<String, User> entry : MontrealServer.userList.entrySet())
				{
					String key = entry.getKey().toString();
					User curr_user = entry.getValue();
					if (curr_user.getItems().contains(itemID))
					{
						HashSet<String> temp = curr_user.getItems();
						temp.remove(itemID);
						curr_user.setItems(temp);
					}
				}
	
			}
			else
			{
				curr_quantity=curr_quantity-Quantity;
				curr_book.setQuantity(curr_quantity);
				System.out.println(curr_book.getItemId()+" is reduced to "+curr_book.getQuantity()+" successfully by "+ managerId);
				MontrealServer.logger.info(curr_book.getItemId()+" is reduced to "+curr_book.getQuantity()+" successfully by "+ managerId);
			}
			result="Request success: "+itemID+"#success";
		}
		else
		{
			System.out.println("Book not found "+itemID);
			MontrealServer.logger.info("Book not found "+itemID);
			result="Request failure: "+itemID+"#failure";
		}
	
		return result;
	}

	public String MonManagerListItemAvailability(String userId) {
		String print_list="";
		System.out.println("listing available books...");
		for (Entry<String, Item> entry : MontrealServer.itemList.entrySet())
		{
		String key = entry.getKey().toString();
		Item curr_item = entry.getValue();
		print_list=print_list+(curr_item.getItemId()+"     "+curr_item.getItemName()+"    "+curr_item.getQuantity());
		}
		System.out.println(print_list);
		return print_list+"#success";
	}

	public String ConUserBorrowItem(String userId, String itemId ) throws InterruptedException 
	{
		String result="";
		int no_days=0;
		try 
		{
			result = ConcordiaServer.lendBooks(userId.toUpperCase(), itemId.toUpperCase(), no_days);
		} 
		 catch (IOException e) 
		 {	
			e.printStackTrace();
		 } 
		 catch (InterruptedException e) 
		 {
			e.printStackTrace();
		 }
		 System.out.println("request forwarded to concordia");
		 Thread.sleep(250);
		 //System.out.println(result +" Books you own.."+ConcordiaServer.userList.get(userId).getItems());
		 if(ConcordiaServer.userList.get(userId).getItems().contains(itemId))
			{
				result="Borrow success: "+itemId+" Books you own.."+ConcordiaServer.userList.get(userId).getItems()+"#success";
			}
			else
				result="Borrow failure: "+itemId+" Books you own.."+ConcordiaServer.userList.get(userId).getItems()+"#failure";
		return result;
	}

	
	public String ConUserFindItem(String userId, String itemName) {
		String all_data="";
		try {
			all_data=ConcordiaServer.FindItem(itemName);
		} 
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		if (all_data.trim().contains(itemName))
			all_data=all_data+"#success";
		else
			all_data=all_data+"#failure";
		return all_data;
	}

	
	public String ConUserReturnItem(String userId, String itemId) throws InterruptedException {
		String result="";
		try {
			if(ConcordiaServer.userList.containsKey(userId) && ConcordiaServer.userList.get(userId).getItems().contains(itemId))
				result=ConcordiaServer.returnBooks(userId.toUpperCase(), itemId.toUpperCase());
			else
			{
				result="Return failure "+itemId+"#failure";
				return result;
			}
		} 
		catch (InterruptedException | IOException e) 
		{
		e.printStackTrace();
		}
		Thread.sleep(250);
		if(!(ConcordiaServer.userList.get(userId).getItems().contains(itemId)))
		{
			result="Return success: "+itemId+" Books you own.."+ConcordiaServer.userList.get(userId).getItems()+"#success";
		}
		else
			result="Return failure: "+itemId+" Books you own.."+ConcordiaServer.userList.get(userId).getItems()+"#failure";
		return result ;
	}

	
	public String ConUserPrintItems(String userId) {
		HashSet<String> items=new HashSet<String>();
		String items_list=new String();
		items=ConcordiaServer.userList.get(userId.toUpperCase()).getItems();
		for (String item : items)
		{
			items_list=items_list+item+",";
		}
		return items_list;
	}

	
	public String ConUserExchangeItems(String oldItemId, String newItemId,String userId ) throws InterruptedException, IOException {

		String result="none in  exchange";
		oldItemId=oldItemId.toUpperCase();
		newItemId=newItemId.toUpperCase();
		userId=userId.toUpperCase();
		
		
		if(ConcordiaServer.userList.containsKey(userId) && ConcordiaServer.userList.get(userId).getItems().contains(oldItemId) && (!ConcordiaServer.userList.get(userId).getItems().contains(newItemId)))
		{
			User user = ConcordiaServer.userList.get(userId);
			
			switch(newItemId.substring(0,3))
			{
			case "CON":
			{
				if(ConcordiaServer.itemList.containsKey(newItemId))
				{
				Item item = ConcordiaServer.itemList.get(newItemId);
				if (item.getQuantity()>0)
				{
				item.setQuantity(item.getQuantity()-1);
				HashSet items=user.getItems();
				items.add(newItemId);
				user.setItems(items);
				}
				else
				{
					ConcordiaServer.addToWaitlist(newItemId, userId);
				}
				}
				break;
			}
			case "MCG":
			{
				if(McGillServer.itemList.containsKey(newItemId))
				{
				Item item = McGillServer.itemList.get(newItemId);
				if (item.getQuantity()>0)
				{
				item.setQuantity(item.getQuantity()-1);
				HashSet items=user.getItems();
				items.add(newItemId);
				user.setItems(items);
				}
				else
				{
					McGillServer.addToWaitlist(newItemId, userId);
				}
				}
				break;
			}
			case "MON":
			{
				if(MontrealServer.itemList.containsKey(newItemId))
				{
				Item item = MontrealServer.itemList.get(newItemId);
				if (item.getQuantity()>0)
				{
				item.setQuantity(item.getQuantity()-1);
				HashSet items=user.getItems();
				items.add(newItemId);
				user.setItems(items);
				}
				else
				{
					MontrealServer.addToWaitlist(newItemId, userId);
				}
				}
				break;
			}
			}
			
			
			switch(oldItemId.substring(0, 3))
			{
			case "CON":
			{
				Item item = ConcordiaServer.itemList.get(oldItemId);
				item.setQuantity(item.getQuantity()+1);
				HashSet items=user.getItems();
				items.remove(oldItemId);
				user.setItems(items);
				ConcordiaServer.adjustWaitlist(oldItemId);
				break;
			}
			case "MCG":
			{
				Item item = McGillServer.itemList.get(oldItemId);
				item.setQuantity(item.getQuantity()+1);
				HashSet items=user.getItems();
				items.remove(oldItemId);
				user.setItems(items);
				McGillServer.adjustWaitlist(oldItemId);
				break;
			}
			case "MON":
			{
				Item item = MontrealServer.itemList.get(oldItemId);
				item.setQuantity(item.getQuantity()+1);
				HashSet items=user.getItems();
				items.remove(oldItemId);
				user.setItems(items);
				MontrealServer.adjustWaitlist(oldItemId);
				break;
			}
			}
			
			if (user.getItems().contains(newItemId) && (!user.getItems().contains(oldItemId)))
				result="Exchange success: Books you own..."+user.getItems().toString()+"#success";
			else
				result="Exchange failure: Books you own..."+user.getItems().toString()+"#failure";
		}
		
		else
		{
			result="Exchange failure"+"#failure";
		}
		

		/*try {
			result = ConcordiaServer.exchangeItems(oldItemId, newItemId, userId);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}*/
		
		//Thread.sleep(2500);
		
		
		
		return result ;
	
	}

	public String McGUserBorrowItem(String userId, String itemId) throws InterruptedException 
	{
		String result="";
		int no_days =0;
		try 
		{
			result = McGillServer.lendBooks(userId.toUpperCase(), itemId.toUpperCase(), no_days);
		} 
		 catch (IOException e) 
		 {	
			e.printStackTrace();
		 } 
		 System.out.println("request forwarded to mcgill");
		 Thread.sleep(250);
		 if(McGillServer.userList.get(userId).getItems().contains(itemId))
			{
				result="Borrow success: "+itemId+" Books you own.."+McGillServer.userList.get(userId).getItems()+"#success";
			}
			else
				result="Borrow failure: "+itemId+" Books you own.."+McGillServer.userList.get(userId).getItems()+"#failure";
		 return result; 
	}

	
	public String McGUserFindItem(String userId, String itemName) {
		String all_data="";
		try {
			all_data=McGillServer.FindItem(itemName);
		} 
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		if (all_data.trim().contains(itemName))
			all_data=all_data+"#success";
		else
			all_data=all_data+"#failure";
		return all_data;
	}

	
	public String McGUserReturnItem(String userId, String itemId) throws InterruptedException {
		String result="";
		try {
			if(McGillServer.userList.containsKey(userId)  && McGillServer.userList.get(userId).getItems().contains(itemId))
				result=McGillServer.returnBooks(userId.toUpperCase(), itemId.toUpperCase());
			else
			{
				result="Return failure: "+itemId+"#failure";
				return result;
			}
		} 
		catch (InterruptedException | IOException e) 
		{
		e.printStackTrace();
		}
		Thread.sleep(250);
		if(!(McGillServer.userList.get(userId).getItems().contains(itemId)))
		{
			result="Return success: "+itemId+" Books you own.."+McGillServer.userList.get(userId).getItems()+"#success";
		}
		else
			result="Return failure: "+itemId+" Books you own.."+McGillServer.userList.get(userId).getItems()+"#failure";
	 return result; 
	}

	
	public String McGUserPrintItems(String userId) {
		HashSet<String> items=new HashSet<String>();
		String items_list=new String();
		items=McGillServer.userList.get(userId.toUpperCase()).getItems();
		for (String item : items)
		{
			items_list=items_list+item+",";
		}
		return items_list;
	}

	public String McGUserExchangeItems(String oldItemId, String newItemId,String userId ) throws InterruptedException, IOException {

		String result="none in  exchange";
		oldItemId=oldItemId.toUpperCase();
		newItemId=newItemId.toUpperCase();
		userId=userId.toUpperCase();
		//
		
		if(McGillServer.userList.containsKey(userId)  && McGillServer.userList.get(userId).getItems().contains(oldItemId) && (!McGillServer.userList.get(userId).getItems().contains(newItemId)))
		{
			User user = McGillServer.userList.get(userId);
			
			switch(newItemId.substring(0,3))
			{
			case "CON":
			{
				if(ConcordiaServer.itemList.containsKey(newItemId))
				{
				Item item = ConcordiaServer.itemList.get(newItemId);
				if (item.getQuantity()>0)
				{
				item.setQuantity(item.getQuantity()-1);
				HashSet items=user.getItems();
				items.add(newItemId);
				user.setItems(items);
				}
				else
				{
					ConcordiaServer.addToWaitlist(newItemId, userId);
				}
				}
				break;
			}
			case "MCG":
			{
				if(McGillServer.itemList.containsKey(newItemId))
				{
				Item item = McGillServer.itemList.get(newItemId);
				if (item.getQuantity()>0)
				{
				item.setQuantity(item.getQuantity()-1);
				HashSet items=user.getItems();
				items.add(newItemId);
				user.setItems(items);
				}
				else
				{
					McGillServer.addToWaitlist(newItemId, userId);
				}
				}
				break;
			}
			case "MON":
			{
				if(MontrealServer.itemList.containsKey(newItemId))
				{
				Item item = MontrealServer.itemList.get(newItemId);
				if (item.getQuantity()>0)
				{
				item.setQuantity(item.getQuantity()-1);
				HashSet items=user.getItems();
				items.add(newItemId);
				user.setItems(items);
				}
				else
				{
					MontrealServer.addToWaitlist(newItemId, userId);
				}
				}
				break;
			}
			}
			
			
			switch(oldItemId.substring(0, 3))
			{
			case "CON":
			{
				Item item = ConcordiaServer.itemList.get(oldItemId);
				item.setQuantity(item.getQuantity()+1);
				HashSet items=user.getItems();
				items.remove(oldItemId);
				user.setItems(items);
				ConcordiaServer.adjustWaitlist(oldItemId);
				break;
			}
			case "MCG":
			{
				Item item = McGillServer.itemList.get(oldItemId);
				item.setQuantity(item.getQuantity()+1);
				HashSet items=user.getItems();
				items.remove(oldItemId);
				user.setItems(items);
				McGillServer.adjustWaitlist(oldItemId);
				break;
			}
			case "MON":
			{
				Item item = MontrealServer.itemList.get(oldItemId);
				item.setQuantity(item.getQuantity()+1);
				HashSet items=user.getItems();
				items.remove(oldItemId);
				user.setItems(items);
				MontrealServer.adjustWaitlist(oldItemId);
				break;
			}
			}
			
			if (user.getItems().contains(newItemId) && (!user.getItems().contains(oldItemId)))
				result="Exchange success: Books you own..."+user.getItems().toString()+"#success";
			else
				result="Exchange failure: Books you own..."+user.getItems().toString()+"#failure";
		}

		else
		{
			result="Exchange failure"+"#failure";
		}
		
		/*try {
			result = McGillServer.exchangeItems(oldItemId, newItemId, userId);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}*/
		
		//Thread.sleep(1000);
		
		
		
		return result ;
	
	}
	
	public String MonUserborrowItem(String userId, String itemId) throws InterruptedException 
	{
		String result="";
		int no_days = 0;
		try 
		{
			result = MontrealServer.lendBooks(userId.toUpperCase(), itemId.toUpperCase(), no_days);
		} 
		 catch (IOException e) 
		 {	
			e.printStackTrace();
		 } 
		 System.out.println("request forwarded to mcgill");
		 Thread.sleep(250);
		 if(MontrealServer.userList.get(userId).getItems().contains(itemId))
			{
				result="Borrow success: "+itemId+" Books you own.."+MontrealServer.userList.get(userId).getItems()+"#success";
			}
			else
				result="Borrow failure: "+itemId+" Books you own.."+MontrealServer.userList.get(userId).getItems()+"#failure";
		 return result; 
	}

	
	public String MonUserFindItem(String userId, String itemName) {
		String all_data="";
		try {
			all_data=MontrealServer.FindItem(itemName);
		} 
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		if (all_data.trim().contains(itemName))
			all_data=all_data+"#success";
		else
			all_data=all_data+"#failure";
		return all_data;
	}

	
	public String MonUserReturnItem(String userId, String itemId) throws InterruptedException {
		String result="";
		try {
			if(MontrealServer.userList.containsKey(userId) && MontrealServer.userList.get(userId).getItems().contains(itemId))
				result=MontrealServer.returnBooks(userId.toUpperCase(), itemId.toUpperCase());
			else
			{
				result="Return failure: "+itemId+"#failure";
				return result;
			}
				
		} 
		catch (InterruptedException | IOException e) 
		{
		e.printStackTrace();
		}
		Thread.sleep(250);
		if(!(MontrealServer.userList.get(userId).getItems().contains(itemId)))
		{
			result="Return success: "+itemId+" Books you own.."+MontrealServer.userList.get(userId).getItems()+"#success";
		}
		else
			result="Return failure: "+itemId+" Books you own.."+MontrealServer.userList.get(userId).getItems()+"#failure";
	 return result; 
	}

	
	public String MonUserPrintItems(String userId) {
		HashSet<String> items=new HashSet<String>();
		String items_list=new String();
		items=MontrealServer.userList.get(userId.toUpperCase()).getItems();
		for (String item : items)
		{
			items_list=items_list+item+",";
		}
		return items_list;
	}

	
	public String MonUserExchangeItems(String oldItemId, String newItemId,String userId ) throws InterruptedException, IOException {
		String result="none in  exchange";
		oldItemId=oldItemId.toUpperCase();
		newItemId=newItemId.toUpperCase();
		userId=userId.toUpperCase();
		
		
		if(MontrealServer.userList.containsKey(userId) && MontrealServer.userList.get(userId).getItems().contains(oldItemId) && (!MontrealServer.userList.get(userId).getItems().contains(newItemId)))
		{
			User user = MontrealServer.userList.get(userId);
			
			switch(newItemId.substring(0,3))
			{
			case "CON":
			{
				if(ConcordiaServer.itemList.containsKey(newItemId))
				{
				Item item = ConcordiaServer.itemList.get(newItemId);
				if (item.getQuantity()>0)
				{
				item.setQuantity(item.getQuantity()-1);
				HashSet items=user.getItems();
				items.add(newItemId);
				user.setItems(items);
				}
				else
				{
					ConcordiaServer.addToWaitlist(newItemId, userId);
				}
				}
				break;
			}
			case "MCG":
			{
				if(McGillServer.itemList.containsKey(newItemId))
				{
				Item item = McGillServer.itemList.get(newItemId);
				if (item.getQuantity()>0)
				{
				item.setQuantity(item.getQuantity()-1);
				HashSet items=user.getItems();
				items.add(newItemId);
				user.setItems(items);
				}
				else
				{
					McGillServer.addToWaitlist(newItemId, userId);
				}
				}
				break;
			}
			case "MON":
			{
				if(MontrealServer.itemList.containsKey(newItemId))
				{
				Item item = MontrealServer.itemList.get(newItemId);
				if (item.getQuantity()>0)
				{
				item.setQuantity(item.getQuantity()-1);
				HashSet items=user.getItems();
				items.add(newItemId);
				user.setItems(items);
				}
				else
				{
					MontrealServer.addToWaitlist(newItemId, userId);
				}
				}
				break;
			}
			}
			
			
			switch(oldItemId.substring(0, 3))
			{
			case "CON":
			{
				Item item = ConcordiaServer.itemList.get(oldItemId);
				item.setQuantity(item.getQuantity()+1);
				HashSet items=user.getItems();
				items.remove(oldItemId);
				user.setItems(items);
				ConcordiaServer.adjustWaitlist(oldItemId);
				break;
			}
			case "MCG":
			{
				Item item = McGillServer.itemList.get(oldItemId);
				item.setQuantity(item.getQuantity()+1);
				HashSet items=user.getItems();
				items.remove(oldItemId);
				user.setItems(items);
				McGillServer.adjustWaitlist(oldItemId);
				break;
			}
			case "MON":
			{
				Item item = MontrealServer.itemList.get(oldItemId);
				item.setQuantity(item.getQuantity()+1);
				HashSet items=user.getItems();
				items.remove(oldItemId);
				user.setItems(items);
				MontrealServer.adjustWaitlist(oldItemId);
				break;
			}
			}
			if (user.getItems().contains(newItemId) && (!user.getItems().contains(oldItemId)))
				result="Exchange success: Books you own..."+user.getItems().toString()+"#success";
			else
				result="Exchange failure: Books you own..."+user.getItems().toString()+"#failure";
		}

		else
		{
			result="Exchange failure "+"#failure";
		}
		
		/*try {
			result = MontrealServer.exchangeItems(oldItemId, newItemId, userId);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}*/
		
		//Thread.sleep(250);
		
		
		
		return result ;
	}


	
	
public static void main(String args[]) throws InterruptedException, SecurityException, IOException
{
	/*ReplicaManager rm = new ReplicaManager();
	ConcordiaServer.userList.put("CONUABCD", new User("CONU1010"));
	McGillServer.userList.put("MCGUABCD", new User("MCGU1010"));
	MontrealServer.userList.put("MONUABCD", new User("MONU1010"));
	System.out.println(ConcordiaServer.userList.toString());
	System.out.println(McGillServer.userList.toString());
	System.out.println(MontrealServer.userList.toString());
	rm.updateConcordiaBackup();
	System.out.println(rm.ConcordiaBackup.size());
	rm.updateMcGillBackup();
	System.out.println(rm.McGillBackup.size());
	rm.updateMontrealBackup();
	System.out.println(rm.MontrealBackup.size());
	
	ConcordiaServer.userList.put("CONUefgh", new User("CONU1010"));
	McGillServer.userList.put("MCGUefgh", new User("MCGU1010"));
	MontrealServer.userList.put("MONUefgh", new User("MONU1010"));
	
	rm.shutDownAllServers();
	rm.restartAllServers();
	System.out.println(ConcordiaServer.userList.keySet());
	System.out.println(McGillServer.userList.keySet());
	System.out.println(MontrealServer.userList.keySet());*/
	
	try {
		ReplicaManager rm = new ReplicaManager();
		while (true) {
			byte[] buffer = new byte[1000];
			System.out.println("receive request created..");
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			rm.aSocket.receive(request);
			System.out.println(new String(request.getData()));
			String request_msg=new String(request.getData());
			String [] req=request_msg.trim().split(",");
			String reply_msg="";
			if ((req[req.length-1].equals("FL")))
			{
				reply_msg = rm.IdentifyRequest(request_msg);
			}
			else
			{	
				rm.requestMap.put(Integer.parseInt(req[req.length-1]), request_msg);
				reply_msg = rm.IdentifyRequest(request_msg);
				if(!(reply_msg.equals("")))
				{
				reply_msg=rm.replicaId+"#"+reply_msg;
				System.out.println(reply_msg);
				DatagramPacket reply = new DatagramPacket(reply_msg.getBytes(), reply_msg.length(), request.getAddress(),
						1319);
				
				rm.aSocket.send(reply);
				reply_msg="";
				}
			}
		}

	} catch (SocketException e) {
		System.out.println("Socket: " + e.getMessage());
	} catch (IOException e) {
		System.out.println("IO: " + e.getMessage());
	}
    
}
}
