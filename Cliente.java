import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;

class Cliente
{
  /**
           * Metodo de división de archivos
  **/
  public static void getSplitFile(String file, long maxSize, DataOutputStream salida) {
    RandomAccessFile raf = null;
    try {
                     //<<se obtiene el archivo
        raf = new RandomAccessFile(new File(file), "r");
        long length = raf.length();//El tamaño total del archivo
        long count = length/maxSize; //Numero de veces que se partirá el archivo
        long offSet = 0L;//Inicializa el offset
        salida.writeInt(file.length());
        salida.write(file.getBytes());
        salida.writeLong(length);
        salida.writeLong(maxSize);
        salida.writeLong(count);
        for (long i = 0; i <count; i++) {//La ultima parte del archivo se procesa por separado 
            long begin = offSet;
            long end = (i + 1) * maxSize;
            offSet = getWrite(file, i, begin, end, salida);
        }
        if (length - offSet > 0) {
            getWrite(file, count, offSet, length,salida);
        }

    } catch (FileNotFoundException e) { 
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public static long getWrite(String file,long i,long begin,long end, DataOutputStream salida){
    String a=file.split(".mp3")[0];
    long endPointer = 0L;
    byte[] data = null;
    try {
        RandomAccessFile in = new RandomAccessFile(new File(file), "r");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
                     //Declara el array de bytes que se van a leer
        byte[] b = new byte[1024];
        int n = 0;
                     //lee el stream del archivo en cierta posicion
        in.seek(begin);
                     //Se delimitan las secciones que va a leer
        while(in.getFilePointer() < end && (n = in.read(b)) != -1){
            if(in.getFilePointer() > end) {
                System.out.println(in.getFilePointer());
            }
            baos.write(b, 0, n);
            
        }
        data = baos.toByteArray();
        salida.write(data);                     
        endPointer = in.getFilePointer();   //Define el puntero final del archivo
        in.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return endPointer;
}



  public static void main(String[] args) throws Exception
  {
    Socket conexion = null;
    
    for(int i = 0; i<500000; i++){
      try
      {
    	  conexion = new Socket("localhost",50000);
          break;
      }
      catch (Exception e)
      {
        Thread.sleep(13000);
      }      
    }

    DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
    DataInputStream entrada = new DataInputStream(conexion.getInputStream());

    String file = "./01. Stinkfist.mp3"; 
    long maxSize = 400;

    getSplitFile(file, maxSize, salida);

    salida.close();
    entrada.close();
    conexion.close();    
  }
}