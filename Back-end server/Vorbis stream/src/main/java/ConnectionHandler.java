import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by robin on 8/5/16.
 */
public class ConnectionHandler implements Runnable{
    private Socket socket;
    private String fileName;
    public ConnectionHandler(Socket socket, String fileName){
        this.socket=socket;
        this.fileName=fileName;
    }
    private int len;
    private byte[] dataArr=new byte[4096];
    public void run() {
        FileOutputStream fileOutputStream;
        VorbisOutputStream vorbisOutputStream=null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
            BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(fileOutputStream);

            vorbisOutputStream = new VorbisOutputStream(bufferedOutputStream);
            BufferedInputStream inFromClient = new BufferedInputStream(socket.getInputStream());
            int off=0;
            int count=0;

            while((len=inFromClient.read(dataArr,off,4096-off))!=-1){
                if((off+len)<4096){
                    off+=len;
                }else{
                    vorbisOutputStream.write(dataArr,0,off+len);
                    off=0;
                    System.out.println("Len "+len);
                    System.out.println("Count="+count);
                    if(vorbisOutputStream.isInitialized())
                        break;
                }

            }

            while((len=inFromClient.read(dataArr,0,4096))!=-1){
                if(len!=0)
                    vorbisOutputStream.write(dataArr,0,len);
                count+=len;
                System.out.println("Len "+len);
                System.out.println("Count="+count);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(vorbisOutputStream!=null) {
                vorbisOutputStream.flush();
                vorbisOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
