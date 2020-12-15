package app.expert.models.call;

public enum CallType {
    
    INCOMING(true),
    OUTGOING(false);
    
    boolean value;
    
    private CallType(boolean value) {
        this.value = value;
    }

    public String getKey() {
        return getKey();
    }

    public boolean getValue() {
        return this.value;
    }
}
