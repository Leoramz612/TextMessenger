import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{

	
	Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private Consumer<Serializable> callback;
	private Consumer<Serializable> callback2;
	
	Client(Consumer<Serializable> call,Consumer<Serializable> call2){
	
		callback = call;
		callback2 = call2;
	}
	
	public void run() {
		
		try {
		socketClient= new Socket("127.0.0.1",5555);
	    out = new ObjectOutputStream(socketClient.getOutputStream());
	    in = new ObjectInputStream(socketClient.getInputStream());
	    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}
		
		while(true) {
			 
			try {
			MessageInfo message = (MessageInfo) in.readObject();


			if(message.updateList){
				callback2.accept(message);
			}
			else{
				callback.accept(message);
			}

			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
    }
	
	public void send(MessageInfo data) {
		
		try {
			out.reset();
			out.writeObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
