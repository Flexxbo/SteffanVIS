import javax.jms.*;
import javax.naming.*;
import java.util.Properties;
import javax.jms.*;

public class MyProducer { //CurlyBrace missing
  private QueueConnectionFactory qFactory = null;
  private QueueConnection qConnect = null;
  private QueueSession qSession = null;
  private Queue sQueue = null;
  private QueueSender qSender = null; 

  /* Constructor. Establish the Producer */
  public MyProducer (String broker, String username, String password) throws Exception{    
    // Obtain a JNDI connection
    Properties env = new Properties();
    // ... specify the JNDI properties sprecific to the provider 

//hier fehlen noch die properties

    env.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
    env.setProperty(Context.PROVIDER_URL,"tcp://172.20.14.225:61616");
    env.setProperty("queue.hello","hello");
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
protected void SendMessage(String username) throws JMSException {
  // Create message
  TextMessage message = qSession.createTextMessage();
  // Set payload
  message.setText(username+" Hello");
  // Send Message 
  qSender.send(message);
 }

/* Close the JMS connection */
public void close() throws JMSException {
  qConnect.close();
}

/* Run the Producer */
public static void main(String argv[]) throws Exception{
  String broker, username, password;
  if (argv.length == 3) {
    broker = argv[0];
    username = argv[1];
    password = argv[2];
  } else {
    return;
  }
  // Create Producer
  MyProducer producer = new MyProducer(broker, username, password);
  producer.SendMessage(username);
  // Close connection
  producer.close();
  }
}