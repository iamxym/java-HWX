package javahwx;
import java.net.*;
import java.io.*;
import java.util.*;
public class GameProtocol{
    protected InetAddress otherIP;
    protected int otherPort;
    DatagramSocket datagramSocket;
    DatagramPacket receivePacket,sendPacket;
    public GameProtocol(String ip,int port){}
    public GameProtocol(){}
    public static String getIP(){return getLocalIP().toString();}
    public int getPort(){return datagramSocket.getLocalPort();}
    public static String getLocalIP() {
        List<String> ipList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
			if (ip.equals("127.0.0.1")||ip.equals("127.0.1.1"));
                        else ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return (String)ipList.get(0);
    }
    public DatagramPacket receive(byte[] buf)
    {
        try{
            receivePacket=new DatagramPacket(buf,buf.length);//用于接收数据
            datagramSocket.receive(receivePacket);
        }
        catch (IOException e){e.printStackTrace();}
        return receivePacket;
    }
    public void send(ByteArrayOutputStream baos)
    {
        try{
            byte sendBuffer[]=baos.toByteArray();  
            DatagramPacket sendPacket=new DatagramPacket(sendBuffer,sendBuffer.length,otherIP,otherPort);//用于发送数据
            datagramSocket.send(sendPacket);
        }catch (IOException e){e.printStackTrace();}
    }
    public void close()
    {
        datagramSocket.close();
    }
    public void init(){}
}
class GameServer extends GameProtocol{
    public GameServer()
    {
        try{
            //InetAddress ip=InetAddress.getByAddress(clientIP);
            datagramSocket=new DatagramSocket(0);
            //receiveSocket=new DatagramSocket(port,ip);
            //serverPort=sendSocket.getPort();
            //InetAddress serverIp = InetAddress.getLocalHost();
            //System.out.println("Server IP:" + getLocalIP().toString()); 
            //System.out.println("Server Port:"+datagramSocket.getLocalPort());

        }
        catch (IOException e){e.printStackTrace();}
    }
    @Override
    public void init()//初始化用于获得client的IP和port
    {
        byte buf[]=new byte[25];
        try{
            receivePacket=new DatagramPacket(buf,buf.length);//用于接收数据
            datagramSocket.receive(receivePacket);
            otherIP=receivePacket.getAddress();
            otherPort=receivePacket.getPort();
            //System.out.println("otherIP="+otherIP+"otherPort"+otherPort);
            //System.out.println("client says "+new String(buf,0,receivePacket.getLength()));
        }
        catch (IOException e){e.printStackTrace();}
    }
}
class GameClient extends GameProtocol{
    public GameClient(String ip,int port)
    {
        try{
            
            otherIP=InetAddress.getByName(ip);
            
            otherPort=port;
            //System.out.println("serverIP:"+otherIP.toString());
            //System.out.println("serverPort:"+otherPort);
            datagramSocket=new DatagramSocket();//这里和Server端不一样
        }
        catch (IOException e){e.printStackTrace();}
    }
    @Override
    public void init()//初始化用于获得client的IP和port，其中
    {
        try{
            String str=new String("Hello World!");
            byte sendBuffer[]=str.getBytes();
            DatagramPacket sendPacket=new DatagramPacket(sendBuffer,sendBuffer.length,otherIP,otherPort);//用于发送数据
            datagramSocket.send(sendPacket);
        }
        catch (IOException e){e.printStackTrace();}
    }
}
