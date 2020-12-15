package app.expert.db.statics.client_type;

public enum ClientTypeContainer {
    
    PHY(ClientType.builder().code("PHY").name("Физ.лицо").description("Тут описание физ лица").build()),
    JUR(ClientType.builder().code("JUR").name("Юр.лицо").description("Тут описание юр лица").build());
    
    private ClientType clientType;
    
    ClientTypeContainer(ClientType clientType) {
        this.clientType = clientType;
    }
}   
