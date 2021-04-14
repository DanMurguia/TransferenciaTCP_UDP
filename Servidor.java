import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.ByteArrayOutputStream;

class Servidor
{
  // lee del DataInputStream todos los bytes requeridos

  static void read(DataInputStream f,byte[] b,int posicion,int longitud) throws Exception
  {
    while (longitud > 0)
    {
      int n = f.read(b,posicion,longitud);
      posicion += n;
      longitud -= n;
    }
  }

  static class Worker extends Thread
  {
    Socket conexion;

    Worker(Socket conexion)
    {
      this.conexion = conexion;
    }

    public void run()
    {
      RandomAccessFile raf = null;
      try
      {
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        String fname;
	// recibe el tamaños de cada corte y el numero de cortes que se hicieron
        int namelen = entrada.readInt();
        byte[] buffernl = new byte[namelen];
        read(entrada,buffernl,0,namelen);
        fname = new String(buffernl,"UTF-8");
        Long length = entrada.readLong();
        Long maxSize = entrada.readLong();
        Long count = entrada.readLong();
        Long offSet=0L;
        raf = new RandomAccessFile(new File(fname+"_recons.mp3"),"rw");
        for (int i = 0;i<count; i++) {
            byte[]buffer = new byte[1024];
            read(entrada,buffer,0,maxSize.intValue());
            raf.write(buffer, 0,maxSize.intValue());
            offSet = (i+1) * maxSize;

        }
        if (length - offSet > 0) {
          byte[]buffer = new byte[1024];
          read(entrada,buffer,0,maxSize.intValue());
        }
      

        salida.close();
        entrada.close();
        conexion.close();
      }
      catch (Exception e)
      {
        System.err.println(e.getMessage());
      }finally {
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
  }

  public static void main(String[] args) throws Exception
  {
    ServerSocket servidor = new ServerSocket(50000);

    for (int i = 0; i<500000; i++)
    {
      Socket conexion = servidor.accept();
      Worker w = new Worker(conexion);
      w.start();
      //w.join();
    }
  }
}