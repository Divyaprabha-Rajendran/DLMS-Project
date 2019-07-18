package interfacesDef;


/**
* com/librarymanagement/interfacesDef/UMinterfacesPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from UMinterfaces.idl
* Wednesday, 27 March, 2019 7:19:28 PM EDT
*/

public abstract class UMinterfacesPOA extends org.omg.PortableServer.Servant
 implements interfacesDef.UMinterfacesOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("addItem", new java.lang.Integer (0));
    _methods.put ("removeItem", new java.lang.Integer (1));
    _methods.put ("listItemAvailability", new java.lang.Integer (2));
    _methods.put ("borrowItem", new java.lang.Integer (3));
    _methods.put ("findItem", new java.lang.Integer (4));
    _methods.put ("returnItem", new java.lang.Integer (5));
    _methods.put ("addToWaitingList", new java.lang.Integer (6));
    _methods.put ("exchangeItem", new java.lang.Integer (7));
    _methods.put ("shutdown", new java.lang.Integer (8));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {

  //
       case 0:  // com/librarymanagement/interfacesDef/UMinterfaces/addItem
       {
         String managerID = in.read_string ();
         String itemID = in.read_string ();
         String itemName = in.read_string ();
         int quantity = in.read_long ();
         String $result = null;
         $result = this.addItem (managerID, itemID, itemName, quantity);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  //
       case 1:  // com/librarymanagement/interfacesDef/UMinterfaces/removeItem
       {
         String managerID = in.read_string ();
         String itemID = in.read_string ();
         int quantity = in.read_long ();
         String $result = null;
         $result = this.removeItem (managerID, itemID, quantity);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  //
       case 2:  // com/librarymanagement/interfacesDef/UMinterfaces/listItemAvailability
       {
         String managerID = in.read_string ();
         String $result = null;
         $result = this.listItemAvailability (managerID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  //
       case 3:  // com/librarymanagement/interfacesDef/UMinterfaces/borrowItem
       {
         String userID = in.read_string ();
         String itemID = in.read_string ();
         String $result = null;
         $result = this.borrowItem (userID, itemID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  //
       case 4:  // com/librarymanagement/interfacesDef/UMinterfaces/findItem
       {
         String userID = in.read_string ();
         String itemName = in.read_string ();
         String $result = null;
         $result = this.findItem (userID, itemName);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  //
       case 5:  // com/librarymanagement/interfacesDef/UMinterfaces/returnItem
       {
         String userID = in.read_string ();
         String itemID = in.read_string ();
         String $result = null;
         $result = this.returnItem (userID, itemID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  //
       case 6:  // com/librarymanagement/interfacesDef/UMinterfaces/addToWaitingList
       {
         String userID = in.read_string ();
         String itemID = in.read_string ();
         String $result = null;
         $result = this.addToWaitingList (userID, itemID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  //
       case 7:  // com/librarymanagement/interfacesDef/UMinterfaces/exchangeItem
       {
         String userID = in.read_string ();
         String newItemID = in.read_string ();
         String oldItemID = in.read_string ();
         String $result = null;
         $result = this.exchangeItem (userID, newItemID, oldItemID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  //
       case 8:  // com/librarymanagement/interfacesDef/UMinterfaces/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:com/librarymanagement/interfacesDef/UMinterfaces:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public UMinterfaces _this() 
  {
    return UMinterfacesHelper.narrow(
    super._this_object());
  }

  public UMinterfaces _this(org.omg.CORBA.ORB orb) 
  {
    return UMinterfacesHelper.narrow(
    super._this_object(orb));
  }


} // class UMinterfacesPOA
