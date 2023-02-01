/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vac_9
 */
import java.net.*; // Imported because the Socket class is needed
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Server {	
    
	private static HashSet<Integer> portSet = new HashSet<Integer>();
        
        public static void enviarM(String mensaje_ganador, String mensaje_perdedor,int ganador, int perdedor, InetAddress clientIP,DatagramSocket udpServerSocket) throws Exception{
            enviar_mg(mensaje_ganador,ganador,clientIP,udpServerSocket);
            enviar_mg(mensaje_perdedor,perdedor,clientIP,udpServerSocket);
        }
        
        public static void enviar_mg(String mensaje,int puerto, InetAddress clientIP,DatagramSocket udpServerSocket) throws Exception{
            byte[] mensajeE  = new byte[1024];
            mensajeE = mensaje.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(mensajeE, mensajeE.length, clientIP, puerto);
            udpServerSocket.send(sendPacket);
        }
	
	public static void main(String args[]) throws Exception {
	    
	   // The default port     
        int serverport = 7777;        
           
        if (args.length < 1) {
            System.out.println("Usage: UDPServer " + "Now using Port# = " + serverport);
        } 
        // Get the port number & host to use from the command line
        else {            
            serverport = Integer.valueOf(args[0]).intValue();
            System.out.println("Usage: UDPServer " + "Now using Port# = " + serverport);
        }
	    
	    // Open a new datagram socket on the specified port
	    DatagramSocket udpServerSocket = new DatagramSocket(serverport);        
	        
	    System.out.println("Server started...");
            System.out.println("****************************\n");
            
            HashMap<Integer, int[]> jugadores = new HashMap<Integer, int[]>();
            int n_jug=0;
            int jugadas=0;
            int ronda=1;
            
            int turno_jug = -1;
	    while(true)
		{
			// Create byte buffers to hold the messages to send and receive
			byte[] receiveData = new byte[1024];          
			  
			// Create an empty DatagramPacket packet
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			  
			// Block until there is a packet to receive, then receive it  (into our empty packet)
			udpServerSocket.receive(receivePacket);           
			  
			// Extract the message from the packet and make it into a string, then trim off any end characters
			String clientMessage = (new String(receivePacket.getData())).trim();
			  
			// Print some status messages
			System.out.println("Client Connected - Socket Address: " + receivePacket.getSocketAddress());
			System.out.println("Client message: \"" + clientMessage + "\"");          
			  
			// Get the IP address and the the port number which the received connection came from
			InetAddress clientIP = receivePacket.getAddress();           
			
			// Print out status message
			System.out.println("Client IP Address & Hostname: " + clientIP + ", " + clientIP.getHostName() + "\n");
			            
			// Get the port number which the recieved connection came from
			int clientport = receivePacket.getPort();
			  
			portSet.add(clientport);
			  
			
                        if(clientMessage.equalsIgnoreCase("Registrar")){
                            
                            n_jug++;
                            if(n_jug==1){
                                
                                //Indice 0 representa turnos; 1 Inicia primero, 0 Inicia segundo
                                //Indice 1 representa el n_de barcos destruidos
                                //Indice 2 representa el puntaje acumulado por ronda
                                //Indice 3 representa si es jugador 1 o jugador 2
                                Random rand = new Random(); 
                                
                                int turno = rand.nextInt(2);
                                
                                int [] j1 = {turno,0,0,1};
                                jugadores.put(clientport, j1);
                                turno_jug = turno;
                            }
                            else{
                                
                                int turno;
                                
                                if(turno_jug==0){
                                    turno=1;
                                }
                                else{
                                    turno=0;
                                }
                                
                                
                                int [] j2 = {turno,0,0,2};
                                jugadores.put(clientport, j2);
                                
                                 
                                //Aqui se modifica cuando sea lo del orden aleatorio
                                for(Integer port : portSet) 
                                        {
                                                if(port != clientport) 
                                                {
                                                    enviar_mg("Inicio;Tu inicias la partida",port,clientIP,udpServerSocket);
                                                    enviar_mg("Inicio;Tu oponenete inicia la partida",clientport,clientIP,udpServerSocket);
                                                    enviar_mg("Ronda;"+ronda,port,clientIP,udpServerSocket);
                                                    enviar_mg("Ronda;"+ronda,clientport,clientIP,udpServerSocket);
                                                }
                                        }
                                
                            }
                            System.out.println("El jugador " + n_jug + " ha sido registrado");
                            System.out.println("****************************\n");
                            
                            //Envio mensaje de confirmacion de registro mas su numero de jugador
                            
                            String conf = "Se ha registrado al juego como jugador " + n_jug;
                            enviar_mg(conf,clientport,clientIP,udpServerSocket);
                            
                            //En caso de estar los dos jugadores el servidor; dira quien inicia la partida
                            if(n_jug==2){
                                for(Integer port : portSet) 
                                    {
                                        if(jugadores.get(port)[0]==1){
                                            System.out.println("El jugador " + jugadores.get(port)[3] + " iniciara la partida");
                                        }
                                    
                                    }
                                
                            }
                            
                            
                        }
                        else{
                            
                            //El juego ya tiene a los dos jugadores registrados
                            if(n_jug==2){
                                
                                int jugador_id = jugadores.get(clientport)[3];
                                
                                if(jugadores.get(clientport)[0]==0){
                                    
                                    
                                    String respuestaAt = clientMessage.split(";")[0];
                                    if(respuestaAt.equalsIgnoreCase("Resultado")){
                                        // Response message
                                        int b_destruidos = Integer.parseInt(clientMessage.split(";")[6]);
                                        int puntaje_at = Integer.parseInt(clientMessage.split(";")[7]); //modificar
                                        
                                        
                                        
                                        String returnMessage = clientMessage.toUpperCase();          

                                        
                                        
                                        int b_hundidos = 0; //ponder la correspondiente
            

                                        for(Integer port : portSet) 
                                        {
                                                if(port != clientport) 
                                                {
                                                        
                                                        
                                                        int []jg_info = jugadores.get(port);
                                                        jg_info[0]= 0;
                                                        
                                                        //Actualizar barcos hundidos en el correspondiente jugador
                                                        jg_info[1]= jg_info[1] + b_destruidos;
                                                         
                                                        //Actualiza puntaje
                                                        jg_info[2]= jg_info[2] + puntaje_at;
                                                        jugadores.put(port,jg_info);
                                                        
                                                        b_hundidos = jugadores.get(port)[1];
                                                        
                                                        //
                                                        returnMessage = returnMessage + ";" + jg_info[2];
                                                        //
                                                        enviar_mg(returnMessage,port,clientIP,udpServerSocket);
                                                        
                                                        System.out.println("- Resultado del ataque realizado por jugador " + jugadores.get(port)[3] + " -");
                                                        System.out.println(returnMessage);
                                                        System.out.println("****************************\n");
                                                        
                                                        
                                                        
                                                }
                                        }
                                        
                                        if(jugadas!=16){
                                            enviar_mg("Turno;Ahora es tu turno",clientport,clientIP,udpServerSocket);
                                        }
                                        
                                        
                                        
                                        int []jg_info = jugadores.get(clientport);
                                        jg_info[0]= 1;
                                        jugadores.put(clientport,jg_info);
                                        
                                        //Aqui debe ir lo del barcos destruidos
                                        
                                        
                                        if(b_hundidos == 5){
                                            String returnMessage3 = "Has ganado por haber undido todos los barcos enemigos";
                                            String returnMessage4 = "Has perdido porque han hundido todos tus barcos";
                                            //Mandar el correspondiente mensaje al ganador usando su puerto
                                                              
                                            for(Integer port : portSet) 
                                            {
                                                 
                                                if(port != clientport){
                                                    enviarM(returnMessage3,returnMessage4,port,clientport,clientIP,udpServerSocket);
                                                    System.out.println("El jugador " + jugadores.get(port)[3] +" ha ganado por haber hundido todos los barcos del jugador " + jugador_id);
                                                    System.out.println("****************************\n");
                                                }
                                                
                                                
                                                
                                            }
                                        }
                                        
                                        
                                        

                                        //Aqui ver lo del numero de rondas......
                                        else if(jugadas==16){
                                            
                                            String returnMessage3 = "Has ganado por tu puntaje";
                                            String returnMessage4 = "Has perdido por tu puntaje";
                                            String returnMessage5 = "Has empatado por tu puntaje";

                         
                                            //Verificar quien gano usando el puntaje
                                            for(Integer port : portSet) 
                                            {
                                                 if(port != clientport){
                                                     
                                                     if(jugadores.get(port)[2]>jugadores.get(clientport)[2]){

                                                        // Create a DatagramPacket to send, using the buffer, the clients IP address, and the clients port                                   
                                                        enviarM(returnMessage3,returnMessage4,port,clientport,clientIP,udpServerSocket);
                                                        System.out.println("El jugador " + jugadores.get(port)[3] +" ha ganado por haber tenido mayor puntacion que el jugador " + jugador_id);
                                                        
                                                     }
                                                     else if (jugadores.get(port)[2]<jugadores.get(clientport)[2]){
                                                        enviarM(returnMessage3,returnMessage4,clientport,port,clientIP,udpServerSocket);
                                                        System.out.println("El jugador " + jugador_id +" ha ganado por haber tenido mayor puntacion que el jugador " + jugadores.get(port)[3]);
                                                        
                                                     }
                                                     else{
                                                        enviarM(returnMessage5,returnMessage5,port,clientport,clientIP,udpServerSocket);
                                                        System.out.println("El jugador " + jugador_id +" y el jugador " + jugadores.get(port)[3] + " han empatado por tener la misma puntuacion");
                                                        
                                                     }
                                                     
                                                     System.out.println("Puntuacion Jugador "+ jugador_id + ": " + jugadores.get(clientport)[2]);
                                                     System.out.println("Puntuacion Jugador "+ jugadores.get(port)[3] + ": " + jugadores.get(port)[2]);
                                                     System.out.println("****************************\n");
                                                    
                                                }
                                            }
                                            
                                            
                                            
                                            
                                            
                                            
                                        }
                                        else if(jugadas%2==0){
                                            ronda++;
                                            for(Integer port : portSet) 
                                            {
                                                if(port != clientport) 
                                                {
                                                    enviar_mg("Ronda;"+ronda,port,clientIP,udpServerSocket);
                                                    enviar_mg("Ronda;"+ronda,clientport,clientIP,udpServerSocket);
                                                }
                                            }
                                        }
                                        
                                    }
                                    else{
                                        
                                        String msg = "El jugador " + jugador_id + " ha intentado atacar antes de su turno";
                                        
                                        System.out.println(msg);
                                        System.out.println("****************************\n");
                                        String returnMessage = "Todavia no es tu turno";
                                         // Create an empty buffer/array of bytes to send back 
                                        enviar_mg(returnMessage,clientport,clientIP,udpServerSocket);
                                    }
                                    
                                    
                                }
                                else{
                                
                                    // Response message	
                                    jugadas++;
                                    String returnMessage = clientMessage.toUpperCase();          

                                    

                                    for(Integer port : portSet) 
                                    {
                                            if(port != clientport) 
                                            {
                                                    enviar_mg(returnMessage,port,clientIP,udpServerSocket);
                                                    System.out.println("- Juagador " + jugadores.get(port)[3] + " ha recibido un ataque de jugador " + jugador_id + " -");
                                                    System.out.println("* El ataque *");
                                                    System.out.println(returnMessage);
                                                    System.out.println("****************************\n");
                                            }
                                    }
                                
                                
                                }
                                
                                
                                
                                
                                
                            }
                            //El juego no tiene a los dos jugadores registrados
                            else{
                                String returnMessage = "No puedes empezar a atacar. O no estas registrado en el juego o falta un segundo jugador de que se registre";
                                // Create an empty buffer/array of bytes to send back 
                                enviar_mg(returnMessage,clientport,clientIP,udpServerSocket);
                                System.out.println("Alguien intento realizar un ataque pero no pudo, ya sea porque no esta registrado o porque falta un segundo jugador a registrar");
                                System.out.println("****************************\n");
                            }
                            
                        }
        }
    }
}
