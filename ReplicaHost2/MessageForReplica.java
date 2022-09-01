package ReplicaHost2;

public class MessageForReplica {
    public String seqId;
    public String FEHostAddress;
    public String city;
    public String message;

    public MessageForReplica() {
    }

    public MessageForReplica(String seqId, String FEHostAddress, String city, String message) {
        this.seqId = seqId;
        this.FEHostAddress = FEHostAddress;
        this.city = city;
        this.message = message;
    }

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    public String getFEHostAddress() {
        return FEHostAddress;
    }

    public void setFEHostAddress(String FEHostAddress) {
        this.FEHostAddress = FEHostAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
