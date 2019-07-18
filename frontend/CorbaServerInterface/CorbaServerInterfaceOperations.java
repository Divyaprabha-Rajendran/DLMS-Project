package frontend.CorbaServerInterface;


/**
* CorbaServerInterface/CorbaServerInterfaceOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from CorbaServerInterface.idl
* Tuesday, 26 March, 2019 11:45:25 PM EDT
*/

public interface CorbaServerInterfaceOperations 
{
  String addItem (String userId, String itemId, String itemName, int quantity);
  String deleteItem (String userId, String itemId, int quantity);
  String listItemAvailability (String userId);
  String borrowItem (String userId, String itemId, int no_days);
  String findItem (String userId, String itemName);
  String returnItem (String userId, String itemId);
  String exchangeItems (String userId, String newItemId, String oldItemId);
} // interface CorbaServerInterfaceOperations
