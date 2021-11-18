package common.models;

import java.util.List;

public class Room {
    private List<ClientModel> clientModelList;
    private ClientModel owner;
    private String name;

    public Room(List<ClientModel> clientModelList, ClientModel owner, String name) {
        this.clientModelList = clientModelList;
        this.owner = owner;
        this.name = name;
    }

    public List<ClientModel> getClientList() {
        return clientModelList;
    }

    public ClientModel getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setClientList(List<ClientModel> clientModelList) {
        this.clientModelList = clientModelList;
    }

    public void setOwner(ClientModel owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void addClient(ClientModel clientModel){
        this.clientModelList.add(clientModel);
    }
}
