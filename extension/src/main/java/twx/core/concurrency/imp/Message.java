package twx.core.concurrency.imp;

public class Message {
	//! 
    private final AbstractActor sender;
    //!
    private final Object 		body;
    //!
    public Message(final AbstractActor sender, final Object body ) {
        this.sender 	= sender;
        this.body 		= body;
    }
    //!
    public AbstractActor getSender() {
        return sender;
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
    public boolean senderEquals(final AbstractActor otherActor) {
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
