package twx.core.concurrency.imp;

public class Message {
	//! 
    private final ActorImp      sender;
	//! 
    private final ActorImp      recipient;
    //!
    private final Object 		body;
    //!
    public Message(final ActorImp sender, final ActorImp recipient, final Object body ) {
        this.sender 	= sender;
        this.recipient  = recipient;
        this.body 		= body;
    }
    //!
    public ActorImp getSender() {
        return sender;
    }
    //!
    public ActorImp getRecipient() {
        return recipient;
    }
    //!
    public Object getBody() {
        return body;
    }
    //!
    public boolean hasBody() {
        return body != null;
    }
    //!/
    public boolean senderEquals(final ActorImp otherActor) {
        if (sender == null) {
            return otherActor == null;
        }
        return sender.equals(otherActor);
    }
    //!
    @Override
    public String toString() {
        return String.format("%s --> %s", sender, body);
    }
}
