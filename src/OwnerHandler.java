import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class OwnerHandler extends ClientHandler {

    OwnerHandler(Socket socket, Database database, DataInputStream dis, DataOutputStream dos) {
        super(socket, database, dis, dos);
    }

    //list of commands:
    //login [phoneNumber] [password], response: {owner account object (COMPLETE)} or null
    //serialize [object type], response: id string
    //signup [phoneNumber] [password] {owner account} {restaurant object}
    //activeOrders, response: {json object with one field (activeOrders)}
    //deliver [order id] {owner account}
    //editFood [menu id] [food id] {food object}
    //addFood [menu id] [food id] {food object}
    //editRestaurant [restaurant id] {restaurant object}
    //editMenu [menu id] [menu object]
    //addImage [image id] [image file]
    //editImage [image id] [image file]
    //editComment [comment id] {comment object}


    @Override
    public void run() {

    }
}
