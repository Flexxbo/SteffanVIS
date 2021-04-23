import javax.jms.*;
import javax.naming.*;
import java.util.Properties;
import java.io.*;

public class Consumer implements MessageListener {
  private QueueConnectionFactory qFactory = null;
  private QueueConnection qConnect = null;
  private QueueSession qSession = null;
  private Queue rQueue = null;
  private QeueueReceiver qReceiver = null; 

  /* Constructor. Establish the Consumer */
  public Consumer (String broker, String username, String password) throws Exception{
     
    // Obtain a JNDI connection
    Properties env = new Properties();
    // ... specify the JNDI properties sprecific to the
    //     provider 
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
  public void onMessage (Message message){
    try {
      TextMessage textMessage = (TextMessage) message;
      String text = textMessage.getText();
      System.outprintln 
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
  public static void main(String argv[]) {
    String broker, username, password;
    if (argv.length == 3) {
      broker = argv[0];
      username = argv[1];
      password = argv[2];
    } else {
      return;
    }
    // Create Consumer
    Consumer consumer  = new Consumer 
                           (broker, username, password);
    System.out.println ("\Consumer started: \n");
    // Close connection
    consumer.close();
    }
}