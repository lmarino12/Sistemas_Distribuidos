/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vac_9
 */
import java.io.*;  // Imported because we need the InputStream and OuputStream classes
import java.net.*; // Imported because the Socket class is needed


public class Client {
      
    public static void main(String args[]) throws Exception {  
        
        
        
        
        Barco [][] m = new Barco[6][7];
        int [][] m2 = new int[6][7];
        
        
        Funciones.generarBarcos(m);
       
        //System.out.println("Ronda 0");
        
        //Funciones.mostrarMatrices(m, m2);
        
        // The default port     
        int clientport = 7777;
        String host = "localhost";
            
        if (args.length < 1) {
           System.out.println("Usage: UDPClient " + "Now using host = " + host + ", Port# = " + clientport);
        } 
        // Get the port number to use from the command line
        else {      
           //host = args[0];
           clientport = Integer.valueOf(args[0]).intValue();
           System.out.println("Usage: UDPClient " + "Now using host = " + host + ", Port# = " + clientport);
        } 
       
        // Get the IP address of the local machine - we will use this as the address to send the data to
        InetAddress ia = InetAddress.getByName(host);

        SenderThread sender = new SenderThread(ia, clientport,m,m2);
        sender.start();
        ReceiverThread receiver = new ReceiverThread(sender.getSocket(),m,m2,ia,clientport);
        receiver.start();
    }
}      
       
class SenderThread extends Thread {
    
    private InetAddress serverIPAddress;
    private DatagramSocket udpClientSocket;
    private boolean stopped = false;
    private int serverport;
    private Barco[][] mm;
    private int[][] me;

    public SenderThread(InetAddress address, int serverport, Barco[][]mm,int[][]me) throws SocketException {
        this.serverIPAddress = address;
        this.serverport = serverport;
        // Create client DatagramSocket
        this.udpClientSocket = new DatagramSocket();
        this.udpClientSocket.connect(serverIPAddress, serverport);
        
        this.mm = mm;
        this.me = me;
    }
    public void halt() {
        this.stopped = true;
    }
    public DatagramSocket getSocket() {
        return this.udpClientSocket;
    }

    public void run() {       
        try {         
            // Create input stream
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            while (true) 
            {
                if (stopped)
                    return;
                  
                // Message to send
                String clientMessage = inFromUser.readLine();
                  
                if (clientMessage.equals("."))
                    break;
                
                if (clientMessage.equalsIgnoreCase("Registrar")){
                    Funciones.mostrarMatrices(this.mm, this.me); 
                    // Create byte buffer to hold the message to send
                    byte[] sendData = new byte[1024];

                    // Put this message into our empty buffer/array of bytes
                    sendData = clientMessage.getBytes();

                    // Create a DatagramPacket with the data, IP address and port number
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverport);

                    //Me registro o envio un ataque
                    // Send the UDP packet to server
                    udpClientSocket.send(sendPacket);

                    Thread.yield();
                }
                
                else if(clientMessage.equalsIgnoreCase("Atacar")){
                    
                    String ataqueB = Funciones.generarAtaques(me);
                    
                    // Create byte buffer to hold the message to send
                    byte[] sendData = new byte[1024];

                    // Put this message into our empty buffer/array of bytes
                    sendData = ataqueB.getBytes();

                    // Create a DatagramPacket with the data, IP address and port number
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverport);

                    //Me registro o envio un ataque
                    // Send the UDP packet to server
                    udpClientSocket.send(sendPacket);
                    
                    Thread.yield();
                }
                else{
                    System.out.println("Comando invalido");
                }
                  
                
            }
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }
}   

class ReceiverThread extends Thread {
    
    private DatagramSocket udpClientSocket;
    private boolean stopped = false;
    private Barco[][] mm;
    private int[][] me;
    private InetAddress serverIPAddress;
    private int serverport;
     

    public ReceiverThread(DatagramSocket ds,Barco[][]mm,int[][]me,InetAddress address, int serverport) throws SocketException {
        this.udpClientSocket = ds;
        this.serverIPAddress = address;
        this.serverport = serverport;
        //Save matriz and matriz enemiga
        this.mm=mm;
        this.me=me;
    }

    public void halt() {
        this.stopped = true;
    }

    public void run() {
        
        // Create a byte buffer/array for the receive Datagram packet
        byte[] receiveData = new byte[1024];
        
        while (true) {            
            if (stopped)
            return;
            
            // Set up a DatagramPacket to receive the data into
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            
            try {
                // Receive a packet from the server (blocks until the packets are received)
                udpClientSocket.receive(receivePacket);
                
                // Extract the reply from the DatagramPacket      
                String serverReply =  new String(receivePacket.getData(), 0, receivePacket.getLength());
                
                // print to the screen
                System.out.println("UDPClient: Response from Server: \"" + serverReply + "\"\n");
                
                
                String[] arrAtack2 = serverReply.split(";");
                
                //Se ve quien empieza la partida...
                if(arrAtack2.length == 2){
                    if(arrAtack2[0].equalsIgnoreCase("Ronda")){
                        System.out.println("-------------------------------------------------");
                        System.out.println("Ronda "+arrAtack2[1]);
                        System.out.println("-------------------------------------------------");
                    }
                    else{
                        System.out.println(arrAtack2[1]);
                    }
                }
                
                //Recibo un ataque
                else if(arrAtack2.length == 5){
                    String latack = Funciones.recibirAtaque(arrAtack2, this.mm);
                        
                    // Create byte buffer to hold the message to send
                    byte[] sendData = new byte[1024];

                    // Put this message into our empty buffer/array of bytes
                    sendData = latack.getBytes();

                    // Create a DatagramPacket with the data, IP address and port number
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverport);

                    // Send the UDP packet to server
                    udpClientSocket.send(sendPacket);
                        
                        
                    System.out.println("Acabas de ser atacado...");    
                }
                
                //Veo los resultados de mi ataque
                else if(arrAtack2.length == 9){
                    Funciones.actualizarME(arrAtack2, this.me);
            
                    Funciones.mostrarMatrices(this.mm, this.me);
                    System.out.println("Puntaje del ataque realizado: "+ arrAtack2[7]);
                    System.out.println("Puntaje del ataque total: "+ arrAtack2[8]);
                    System.out.println("Tu turno ha terminado");
                }
                
                System.out.println("****************************\n");
                
                
                
                Thread.yield();
            } 
            catch (IOException ex) {
            System.err.println(ex);
            }
        }
    }
}
