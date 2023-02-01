/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente;

/**
 *
 * @author LAMG
 */
public class Barco {
    
    private String direccion;
    private String tipo;
    private int size;
    private int vida;

    public Barco(String direccion, String tipo) {
        this.direccion = direccion;
        this.tipo = tipo;
        
        if(this.tipo.equalsIgnoreCase("G")){
            this.size = 3;
            this.vida = 3;
        }
        else{
            this.size = 2;
            this.vida = 2;
        }
    }
    
   
    
    public void perderVida(){
        this.vida--;
    }
    
    public int getVida(){
        return vida;
    }
    
    public String getDireccion() {
        return direccion;
    }

    public String getTipo() {
        return tipo;
    }

    public int getSize() {
        return size;
    }
    
   
   

    @Override
    public String toString() {
        return "Barco{" + "direccion=" + direccion + ", tipo=" + tipo + ", size=" + size + '}';
    }
    
    
}