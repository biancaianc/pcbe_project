package common.models;

import java.util.List;

public class Room {
    private List<Client> clientList;
    private Client owner;
    private String name;

    public Room(List<Client> clientList, Client owner, String name) {
        this.clientList = clientList;
        this.owner = owner;
        this.name = name;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public Client getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void addClient(Client client){
        this.clientList.add(client);
    }
}
