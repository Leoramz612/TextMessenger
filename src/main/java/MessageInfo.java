import java.io.Serializable;
import java.util.ArrayList;

public class MessageInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    boolean everyoneMessage = false;
    boolean groupMessage = false;
    boolean singleMessage = false;
    String message;
    int clientNum;

    int singleClient;
    ArrayList<Integer> groupClients = new ArrayList<>();

    ArrayList<Integer> clients = new ArrayList<>();

    boolean updateList = false;
    boolean updateSelected = false;

    MessageInfo(){

    }
}
