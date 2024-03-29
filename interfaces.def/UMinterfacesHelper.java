package interfacesDef;


/**
* com/librarymanagement/interfacesDef/UMinterfacesHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from UMinterfaces.idl
* Wednesday, 27 March, 2019 7:19:28 PM EDT
*/

abstract public class UMinterfacesHelper
{
  private static String  _id = "IDL:com/librarymanagement/interfacesDef/UMinterfaces:1.0";

  public static void insert (org.omg.CORBA.Any a, interfacesDef.UMinterfaces that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static interfacesDef.UMinterfaces extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (interfacesDef.UMinterfacesHelper.id (), "UMinterfaces");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static interfacesDef.UMinterfaces read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_UMinterfacesStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, interfacesDef.UMinterfaces value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static interfacesDef.UMinterfaces narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof interfacesDef.UMinterfaces)
      return (interfacesDef.UMinterfaces)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      interfacesDef._UMinterfacesStub stub = new interfacesDef._UMinterfacesStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static interfacesDef.UMinterfaces unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof interfacesDef.UMinterfaces)
      return (interfacesDef.UMinterfaces)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      interfacesDef._UMinterfacesStub stub = new interfacesDef._UMinterfacesStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
