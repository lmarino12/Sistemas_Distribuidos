/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente;

import java.util.HashSet;
import java.util.Random;
/**
 *
 * @author LAMG
 */
public class Funciones {
    
    public static boolean verificacion(Barco[][] mb, int f1, int c1, int barco_size, int vh){
        if(vh == 0){
            for (int i = 0; i < barco_size; i++) {
                if(mb[f1][c1+i]!=null ){
                    return true;
                }        
             }
            return false;
        }
        else{
            for (int i = 0; i < barco_size; i++) {
                if(mb[f1+i][c1]!=null ){
                    return true;
                }        
             }
            return false;
        }
    
    }
    
    public static void ubicarBarco(Barco b, Barco[][] mb){
        
        Random rand = new Random(); 
        
        //Se limita a que si es horizontal va a hacia la derecha
        //Y si es vertical va hacia abajo
        
        boolean flag1 = true;
        
        //
        while(flag1){
        
            int fila = rand.nextInt(6); //Posicion de indices
            int columna = rand.nextInt(7);  //Posicion de indices

            //System.out.println("Fila: "+ fila + " Columna: " + columna );
            

            if(b.getDireccion().equalsIgnoreCase("H")){
                //System.out.println("horizontal");
                if(columna+b.getSize() <= 7){
                    boolean ocupado = verificacion(mb,fila,columna,b.getSize(),0);
                    if(!ocupado){
                        for (int i = 0; i < b.getSize(); i++) {
                            mb[fila][columna+i] = b;
                            
                           
                        }
                        flag1=false;
                    }
                }
            }
            else{
                //System.out.println("vertical");
                if(fila+b.getSize()<= 6){
                    boolean ocupado = verificacion(mb,fila,columna,b.getSize(),1);
                    if(!ocupado){
                       for (int i = 0; i < b.getSize(); i++) {
                            mb[fila+i][columna] = b;
                            
                          
                        }
                        flag1=false;
                    }
                }       
            }
            
           
            
            
        }
    }
    
    public static void generarBarcos(Barco[][] matriz){
        Barco g1 = new Barco("H","G");
        Barco g2 = new Barco("V","G");
        Barco g3 = new Barco("H","G");
        
        Barco p1 = new Barco("V","P");
        Barco p2 = new Barco("H","P");
        
        
        ubicarBarco(g1,matriz);
        ubicarBarco(g2,matriz);
        ubicarBarco(g3,matriz);
        ubicarBarco(p1,matriz);
        ubicarBarco(p2,matriz);
    
    }
    
    public static void mostrarMatrices(Barco[][] m, int[][] m2 ){
        //Forma de mostrar la matriz por consola..... Esto se lo pueden reemplazar luego con un entorno grafico
        
        System.out.println("Mi matriz");
        
        for(int i = 0; i < m.length; i++){ 
            for(int j = 0; j < m[i].length; j++){ 
                if(m[i][j]==null){
                    System.out.print("0" + " ");
                }
                else{
                    System.out.print(m[i][j].getTipo() + " ");	// Imprime elemento
                }
            } 
            System.out.println();	// Imprime salto de línea 
        }
        
        System.out.println("Matriz Enemiga");
        
        for(int i = 0; i < m2.length; i++){ 
            for(int j = 0; j < m2[i].length; j++){ 
                    System.out.print(m2[i][j] + " ");	// Imprime elemento 
            } 
            System.out.println();	// Imprime salto de línea 
        }
    }
    
    public static String recibirAtaque(String[] arrAtack2, Barco[][] mm){
        String latack = "Resultado;";
        HashSet<Barco> hundidos = new HashSet<Barco>();
        int puntaje_t = 0;
        //Ataque
        for (int i = 0; i < 5; i++) {
            String[] coordenadas = arrAtack2[i].split(",");
            int f = Integer.parseInt(coordenadas[0]);
            int c = Integer.parseInt(coordenadas[1]);
                if(mm[f][c]!=null){
                    mm[f][c].perderVida();
                }
        }
        
        //Ponderacion de daños
        
        for (int i = 0; i < 5; i++) {
            
            
            String[] coordenadas = arrAtack2[i].split(",");
            int f = Integer.parseInt(coordenadas[0]);
            int c = Integer.parseInt(coordenadas[1]);

            int puntaje = -10;
            if(mm[f][c]!=null){
                puntaje = 10;
                if(mm[f][c].getVida()==0){
                    hundidos.add(mm[f][c]);
                    puntaje = 15;
                }
            }
            
            puntaje_t = puntaje_t + puntaje;
            latack = latack + arrAtack2[i] + "," + puntaje + ";";

            /*
            if(i<4){
                latack = latack + ";";
            }
            */
        }

        latack = latack + hundidos.size() + ";" + puntaje_t;
                    
        System.out.println(latack);
        
        //String de 7 .... 1ro Nombre resultado 2 al 6 doCoordenadas y porcentaje , el 7mo No de barcos hundidos
        return latack;
 
    }
    
    public static void actualizarME(String[] arrAtack2, int[][] me ){
        for (int i = 1; i < 6; i++){
            String[] coor = arrAtack2[i].split(",");
            int f2 =   Integer.parseInt(coor[0]);
            int c2 =   Integer.parseInt(coor[1]);

            int punt= Integer.parseInt(coor[2]);

            me[f2][c2] = punt;
                            
        }
    }
    
    
}