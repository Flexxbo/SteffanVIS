import javax.jms.*;
import javax.naming.*;
import java.util.Properties;
import java.io.*;

public class MyConsumer implements MessageListener {
  private QueueConnectionFactory qFactory = null;
  private QueueConnection qConnect = null;
  private QueueSession qSession = null;
  private Queue rQueue = null;
  private QueueReceiver qReceiver = null; 

  /* Constructor. Establish the Consumer */
  public MyConsumer (String broker, String username, String password) throws Exception{
     
    // Obtain a JNDI connection
    Properties env = new Properties();
    // ... specify the JNDI properties sprecific to the
    //     provider 
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
    rQueue = (Queue)jndi.lookup("hello");

    // Create a receiver
    qReceiver = qSession.createReceiver(rQueue);
     
   // set a JMS message listener
   qReceiver.setMessageListener(this);
    
    // Start the Connection
    qConnect.start();
  }

  /* Receive message from qReceiver */
  public void onMessage (Message message) {
    try {
      TextMessage textMessage = (TextMessage) message;
      String text = textMessage.getText();
      System.out.println 
         ("Message received – " + text + " from" +
            message.getJMSCorrelationID());
        } catch (java.lang.Exception rte) {
           rte.printStackTrace();
        }
  }

  /* Close the JMS connection */
  public void close() throws JMSException {
    qConnect.close();
  }

  /* Run the Consumer */
  public static void main(String argv[]) throws Exception {
    String broker, username, password;
    if (argv.length == 3) {
      broker = argv[0];
      username = argv[1];
      password = argv[2];
    } else {
      return;
    }
    // Create Consumer
    MyConsumer consumer  = new MyConsumer (broker, username, password);
    System.out.println ("consumer started: \n");
    // Close connection
    consumer.close();
    }
}