import javax.jms.*;
import javax.naming.*;
import java.util.Properties;

public class Producer
  private QueueConnectionFactory qFactory = null;
  private QueueConnection qConnect = null;
  private QueueSession qSession = null;
  private Queue sQueue = null;
  private QeueueSender qSender = null; 

  /* Constructor. Establish the Producer */
  public Producer (String broker, String username, String password) throws Exception{    
    // Obtain a JNDI connection
    Properties env = new Properties();
    // ... specify the JNDI properties sprecific to the provider 
    InitialContext jndi = new InitialContext(env);
    
    // Look up a JMS QueueConnectionFactory
    qFactory = 
      (QueueConnectionFactory)jndi.lookup(broker);

    // Create a JMS QueueConnection object
    qConnect = 
      qFactory.createQueueConnection(username,password);

    // Create one JMS QueueSession object
    qSession = qConnect.createQueueSession
                             (false,
                              Session.AUTO_ACKNOWLEDGE);
    
    // Look up for a JMS Queue hello
    sQueue = (Queue)jndi.lookup("hello");

    // Create a sender
    qSender = qSession.createSender(sQueue);

    // Start the Connection
    qConnect.start();
  }

/* Create and send message using qSender */
protected void SendMessage() throws JMSException {
  // Create message
  TextMessage message = qSession.createTextMessage();
  // Set payload
  Message.setText(username+" Hello");
  // Send Message 
  qSender.send(message);
 }

/* Close the JMS connection */
public void close() throws JMSException {
  qConnect.close();
}

/* Run the Producer */
public static void main(String argv[]) {
  String broker, username, password;
  if (argv.length == 3) {
    broker = argv[0];
    username = argv[1];
    password = argv[2];
  } else {
    return;
  }
  // Create Producer
  Producer producer = new Producer 
                         (broker, username, password);
  SendMessage();
  // Close connection
  producer.close();
  }
}