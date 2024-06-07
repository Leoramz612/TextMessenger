import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{

	int count = 1;

	MessageInfo intitalmessageInfo;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		  
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				intitalmessageInfo = new MessageInfo();
				intitalmessageInfo.clientNum = count;
				intitalmessageInfo.message = "Client has connected to server: " + "Client #" + intitalmessageInfo.clientNum;
				callback.accept(intitalmessageInfo);
				clients.add(c);
				c.start();
				
				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					MessageInfo errormessageInfo = new MessageInfo();
					errormessageInfo.message = "Server socket did not launch";
					callback.accept(errormessageInfo);
				}
			}//end of while
		}
	

		class ClientThread extends Thread implements Serializable{
			
		
			Socket connection;
			int innerCount;
			ObjectInputStream in;
			ObjectOutputStream out;

			boolean initial = true;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.innerCount = count;
			}
			
			public synchronized void updateAllClients(MessageInfo message) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
						 t.out.reset();
						 t.out.writeObject(message);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}

			public void updateGroupClients(MessageInfo message, ArrayList<Integer> groupClients) {
				for (ClientThread client : clients) {
					if (groupClients.contains(client.innerCount)) {
						try {
							client.out.reset();
							client.out.writeObject(message);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			public synchronized void updateSingleClient(MessageInfo message, int singleClient) {
				for (ClientThread client : clients) {
					if (singleClient == client.innerCount) {
						try {
							client.out.reset();
							client.out.writeObject(message);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			public synchronized void updateClientsList(MessageInfo message) {
				for (ClientThread t : clients) {
					try {
						t.out.reset();

						t.out.writeObject(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}



			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}

				intitalmessageInfo.message = "New client on server: Client #"+intitalmessageInfo.clientNum;
				updateAllClients(intitalmessageInfo);



				intitalmessageInfo.clients.clear();
				for (ClientThread client : clients) {
					intitalmessageInfo.clients.add(client.innerCount);
				}
				intitalmessageInfo.updateList = true;
				updateClientsList(intitalmessageInfo);
					
				 while(true) {
					    try {
					    	MessageInfo data = (MessageInfo) in.readObject();
							data.clientNum = innerCount;


					    	callback.accept(data);
							data.message = "Client #"+data.clientNum+" said: "+data.message;

							if(data.everyoneMessage){
								updateAllClients(data);
							}
							else if(data.groupMessage && !data.updateSelected){
								updateGroupClients(data,data.groupClients);
							}
							else if(data.singleMessage && !data.updateSelected){
								updateSingleClient(data,data.singleClient);
							}
							else if(data.updateSelected){
								updateSingleClient(data,data.clientNum);
							}

					    	
					    	}
					    catch(Exception e) {
							clients.remove(this);

							MessageInfo errormessageInfo = new MessageInfo();
							errormessageInfo.clientNum = innerCount;

							System.out.println("AH mann! Something went wrong with the socket from client: " + innerCount + "....closing down!  ¯\\_(ツ)_/¯");

							callback.accept(errormessageInfo);

							errormessageInfo.message = "Client #"+errormessageInfo.clientNum+" has left the server!";
					    	updateAllClients(errormessageInfo);

							errormessageInfo.updateList = true;
							errormessageInfo.clients.clear();
							for (ClientThread client : clients) {
								errormessageInfo.clients.add(client.innerCount);
							}
							updateClientsList(errormessageInfo);

					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
