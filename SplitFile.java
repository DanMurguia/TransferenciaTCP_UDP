import java.util.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;

public class SplitFile {
 
    public static void main(String[] args) throws IOException {
        getSplitFile();
        String file = "./01. Stinkfist.mp3"; //File path
        RandomAccessFile raf = null;
        raf = new RandomAccessFile(new File(file), "r");
                 long length = raf.length();//The total length of the file
                 long maxSize = 400;//The length of the file after slicing
                 long count = length/maxSize; //Number of copies of file split
        merge("./Stinkrecons.mp3",file,count);
    }
 
    /**
           * File division method
     */
    public static void getSplitFile() {
        String file = "01. Stinkfist.mp3"; //File path
 
        RandomAccessFile raf = null;
        try {
                         //Get the target file and pre-allocate the space occupied by the file Create a file of the specified size on the disk r is read-only
            raf = new RandomAccessFile(new File(file), "r");
            long length = raf.length();//The total length of the file
            long maxSize = 400;//The length of the file after slicing
            // long count = length/maxSize; //The number of copies of the file split
            long count = length/maxSize; //Number of copies of file split
            long offSet = 0L;//Initialize the offset
            for (long i = 0; i <count; i++) {//The last piece is processed separately The count I calculated like this is the last piece removed 
                long begin = offSet;
                long end = (i + 1) * maxSize;
                offSet = getWrite(file, i, begin, end);
            }
            if (length - offSet > 0) {
                getWrite(file, count, offSet, length);
            }
 
        } catch (FileNotFoundException e) {
                         System.out.println("File not found");
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
    /**
           * Specify the boundary of each copy of the file and write it into a different file
           * @param file source file
           * @param i The order ID of the source file
           * @param begin start pointer position
           * @param end the position of the end pointer
     * @return long
     */
    public static long getWrite(String file,long i,long begin,long end){
        String a=file.split(".mp3")[0];
        long endPointer = 0L;
        byte[] data = null;
        try {
                         //Declare the file disk after the file is cut
            RandomAccessFile in = new RandomAccessFile(new File(file), "r");
                         //Define a readable, writable file and a binary file with the extension .tmp
            RandomAccessFile out = new RandomAccessFile(new File(a + "_" + i + ".tmp"), "rw");
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
                         //Declare the byte array of each file
            byte[] b = new byte[1024];
            int n = 0;
                         //Read the file byte stream from the specified position
            in.seek(begin);
                         //Determine the boundary of file stream reading
            while(in.getFilePointer() < end && (n = in.read(b)) != -1){
                if(in.getFilePointer() > end && in.getFilePointer() < 615420 ) {
                    System.out.println(in.getFilePointer());
                }
                                 //From the specified range of each file, write a different file
//                baos.write(b, 0, n);
                out.write(b, 0, n);
                
            }
            data = baos.toByteArray();
            String str = new String(data,"UTF-8");
                         //Define the pointer of the currently read file
            endPointer = in.getFilePointer();
                         //Close the input stream
            in.close();
                         //Close the output stream
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endPointer;
    }
    /**
           * File merger
           * @param file specifies the merge file
           * @param tempFile File name before split
           * @param count the number of files
     */
    public static void merge(String file,String tempFile,long count) {
        String a=tempFile.split(".mp3")[0];
        RandomAccessFile raf = null;
        try {
                         //Declare RandomAccessFile RandomAccessFile
            raf = new RandomAccessFile(new File(file), "rw");
                         //Start to merge files, corresponding to the sliced ​​binary file
            for (int i = 0; i < count+1; i++) {
                                 //Read the slice file
                RandomAccessFile reader = new RandomAccessFile(new File(a + "_" + i + ".tmp"), "r");
                byte[] b = new byte[1024];
                int n = 0;
                                 //Read first and write later
                                 while ((n = reader.read(b)) != -1) {//read
                                         raf.write(b, 0, n);//write
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}