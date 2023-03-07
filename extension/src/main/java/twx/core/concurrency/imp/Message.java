package twx.core.concurrency.imp;

import java.security.Provider.Service;

import org.json.JSONObject;

public class Message {
    private ActorImp        sender;
    private ActorImp        recipient;
    private String          service;
    private String          topic;
    private JSONObject	    body;

    public Message(final ActorImp sender, final ActorImp recipient, final JSONObject body ) {
        this.sender 	= sender;
        this.recipient  = recipient;
        this.service    = null;
        this.topic      = null;
        this.body 		= body;
    }
    
    public ActorImp getSender()     { return sender; }
    public boolean  hasRecipien()   { return recipient != null; }    
    public ActorImp getRecipient()  { return recipient; }
    
    public boolean  hasBody()   { return body != null; }
    public Object   getBody()   { return body; }

    public boolean  hasService()   { return service != null; }
    public Object   getService()   { return service; }

    public boolean  hasTopic()  { return topic != null; }
    public String   getTopic()  { return topic; }
    public void     setTopic(String topic) { 
        this.topic = topic;
    }
    
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

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("sender", this.sender.getName() );
        if( this.hasRecipien() )
            obj.put("recipient", this.recipient.getName() );
        if( this.hasService() )
            obj.put("topic", this.topic );
        if( this.hasTopic() )
            obj.put("topic", this.service );
        if( this.hasBody() )
            obj.put("body", this.body );
        
        return obj;
    }

}
