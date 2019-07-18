package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import entities.User;
import frontend.CorbaServerInterface.*;
import interfacesDef.UMinterfaces;
import interfacesDef.UMinterfacesHelper;



public class ClientInterface 
{
	
	
	public static void DisplayMessages(HashMap<String, User> map)
	{
		for (Entry<String, User> entry :map.entrySet())
		{
			String key = entry.getKey().toString();
			User curr_item = entry.getValue();
			System.out.println(curr_item.getUserId()+"--"+curr_item.getBorrowBooks()+"--"+curr_item.getOwnBooks());
		}
	}
	public static void DisplayData(String data_list,String delimiter)
	{
		
		String[] dataList=data_list.split(delimiter);
		for (String str : dataList)
		{
			System.out.println(str);
		}
		
	}
	
	
	public void userOperations(UMinterfaces userObj,String userId) throws IOException, InterruptedException
	{
		BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));

		String choice="";
		do
		{
			System.out.println("1...Find books accross libraries");
			System.out.println("2...Borrow book");
			System.out.println("3...Return book");
			System.out.println("4...Exchange books");
			System.out.println("Enter the option...");
			int input=Integer.parseInt(scan.readLine());
			
			switch(input)
			{
			case 1: 
			{
				System.out.println("Enter the book name to find");
				String itemName=scan.readLine().toUpperCase();
				String data=userObj.findItem(userId, itemName);
				DisplayData(data,"and");
				break;
			}
			case 2:
			{
				System.out.println("Enter the book id");
				String itemId=scan.readLine().toUpperCase();
				//System.out.println("Enter the number of days");
				//int no_days=Integer.parseInt(scan.readLine());
				String result=userObj.borrowItem(userId, itemId);				
				//TimeUnit.MILLISECONDS.sleep(1000);
				System.out.println(result);
				System.out.println("Control back to client");
				//System.out.println("Books you own.."+UserObj.printItems(userId));
				break;
			}
			case 3:
			{
				System.out.println("Enter the book id");
				String itemId=scan.readLine().toUpperCase();
				String result=userObj.returnItem(userId, itemId);
				System.out.println(result);
				//TimeUnit.MILLISECONDS.sleep(1000);
				//System.out.println("Books you own.."+UserObj.printItems(userId));
				break;
			}
			case 4:
			{
				System.out.println("Enter the book to be returned");
				String oldbook=scan.readLine().toUpperCase();
				System.out.println("Enter the new book");
				String newbook=scan.readLine().toUpperCase();
				String result=userObj.exchangeItem(oldbook, newbook, userId.toUpperCase());
				TimeUnit.MILLISECONDS.sleep(1000);			
				System.out.println(result);
				break;
			}
			default: System.out.println("Invalid choice...");
			}
			System.out.println("Do you want to contionue(y/n)..");
			choice=scan.readLine();
		}while(choice.equalsIgnoreCase("y"));
	}
	
	public void managerOperations(UMinterfaces ManagerObj,String userId) throws InterruptedException, IOException
	{
		BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			System.out.println("1...Add Books");
			System.out.println("2...Remove books");
			System.out.println("3...List books in library");
			System.out.println("Enter the option...");
			int input=Integer.parseInt(scan.readLine());
			
			switch(input)
			{
			case 1:
			{
				System.out.println("Enter the book id");
				String itemId=scan.readLine();
				System.out.println("Enter the book name");
				String itemName=scan.readLine();
				System.out.println("Enter the quantity");
				int quantity = Integer.parseInt(scan.readLine());
				if (quantity>0) {
					String result=ManagerObj.addItem(userId.toUpperCase(), itemId.toUpperCase(), itemName, quantity);
					System.out.println(result);
				}
				else
					System.out.println("Enter valid quantity greater than Zero.");
				break;
			}
			case 2:
			{
				System.out.println("Enter the book id");
				String itemId=scan.readLine();
				System.out.println("Enter the quantity");
				int quantity = Integer.parseInt(scan.readLine());
				if (quantity>0)
				{
					String result=ManagerObj.removeItem(userId.toUpperCase(), itemId.toUpperCase(), quantity);
					System.out.println(result);
				}
				else
					System.out.println("Enter valid quantity greater than Zero.");
				break;
			}
			case 3:
			{
				String data_list=ManagerObj.listItemAvailability(userId.toUpperCase());
				DisplayData(data_list,"and");
				break;
			}
			default: System.out.println("Invalid option...");
			}
		}
	}
	
	public static void main(String args[])
	{
		try 
		{
			ClientInterface cliUI = new ClientInterface();
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object ncref = orb.resolve_initial_references("NameService");
			NamingContextExt objncref = NamingContextExtHelper.narrow(ncref);
			
			UMinterfaces  UserObj;
			
			System.out.println("Client Ready...");
			Scanner scan=new Scanner(System.in);
			System.out.println("Please enter the UserID or ManagerId");
			String id=scan.nextLine();
			String id_pos = id.charAt(3)+"".toUpperCase();
			String identification = id.substring(0, 3).toUpperCase();
			
			if (id_pos.equalsIgnoreCase("U"))
			{
				UserObj = UMinterfacesHelper.narrow(objncref.resolve_str("FrontEnd"));
				cliUI.userOperations(UserObj, id);
			}
			else if (id_pos.equalsIgnoreCase("M"))
			{
				UserObj = UMinterfacesHelper.narrow(objncref.resolve_str("FrontEnd"));
				cliUI.managerOperations(UserObj, id);
			}
			else
			{
				System.out.println("Not a valid user...");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
