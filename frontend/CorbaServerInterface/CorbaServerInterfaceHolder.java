package frontend.CorbaServerInterface;

/**
* CorbaServerInterface/CorbaServerInterfaceHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from CorbaServerInterface.idl
* Tuesday, 26 March, 2019 11:45:25 PM EDT
*/

public final class CorbaServerInterfaceHolder implements org.omg.CORBA.portable.Streamable
{
  public CorbaServerInterface value = null;

  public CorbaServerInterfaceHolder ()
  {
  }

  public CorbaServerInterfaceHolder (CorbaServerInterface initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = CorbaServerInterfaceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    CorbaServerInterfaceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return CorbaServerInterfaceHelper.type ();
  }

}
