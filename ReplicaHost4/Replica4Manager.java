package ReplicaHost4;

import java.io.IOException;
import java.net.*;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import PortInformation.FEPort;
import PortInformation.Replica;


public class Replica4Manager {

    public Logger logger;
    public String ReplicaId;
    public Integer SeqNumber;
    public Integer DeliveryNum;
    public Queue<MessageForReplica> holdBackQueue;//Queue for hold back
    public Queue<MessageForReplica> deliveryQueue;//Queue for delivery
    public Queue<MessageForReplica> backUpQueue;//Queue for backup

    
    public Replica4Manager(Logger logger) throws IOException {
    	
        Comparator<MessageForReplica> comparator = new Comparator<MessageForReplica>() {
            @Override
            //Compare two seqNumber to get difference between them. return a integer.
            public int compare(MessageForReplica o1, MessageForReplica o2) {
                return (Integer.valueOf(o1.getSeqId()) - Integer.valueOf(o2.getSeqId()));
            }
        };
        //Data initialize here
        this.logger = logger;ReplicaId = "4";DeliveryNum = 0;SeqNumber = 0;
        holdBackQueue = new PriorityQueue<MessageForReplica>(comparator);
        deliveryQueue = new PriorityQueue<MessageForReplica>(comparator);
        backUpQueue = new PriorityQueue<MessageForReplica>(comparator);
    }

    private void sendToReplicaAndReturnRes (MessageForReplica msg) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        String ms = msg.getCity() + ":" + msg.getMessage();
        byte[] data = ms.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, ReplicaPort.REPLICA_PORT.port);

        DatagramSocket socket = new DatagramSocket(RMPortInfo.RM_PORT_INFO.execMsgToRplc);

        socket.send(sendPacket);

        byte[] recvData = new byte[1024];
        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);

        TimerForSleep timer = new TimerForSleep(socket);
        Thread thread = new Thread(timer);
        thread.start();
        try{
            socket.receive(recvPacket);
            String receiveMessage = new String(recvPacket.getData(), 0, recvPacket.getLength());
            packageMsgAndSendToFE(socket, msg.getFEHostAddress(), receiveMessage);
        } catch (SocketException e) {
            logger.info("TIME OUT:Replica_" + ReplicaId + " Not Reply");
        }
    }

    private void packageMsgAndSendToFE (DatagramSocket socket, String feHostAddress, String receiveMessage) throws IOException {
        InetAddress address = InetAddress.getByName(feHostAddress);
        String msg = this.ReplicaId + ":" + receiveMessage;
        byte[] data = msg.getBytes(("UTF-8"));
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address , FEPort.FE_PORT.FEPort);

        socket.send(sendPacket);
        socket.close();
    }

    private MessageForReplica parseToMessage(String receiveMessage) {
        String[] message = receiveMessage.split(":");
        String seqId = message[0];
        String FEHostAddress = message[1];
        String city = message[2];
        String msg = message[3];
        return new MessageForReplica(seqId,FEHostAddress,city,msg);
    }

    private void listenCrash(int port){
        logger.info("Crash Listener Start");
        try {
            DatagramSocket socket = new DatagramSocket(port);

            while (true){
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);

                int count = 0;
                while (count < 3){
                    socket.receive(packet);
                    logger.info("Recv Crash Message" + count);
                    count ++;
                }
                this.logger.info("Replica_" + ReplicaId + " Crash");

                restartReplica();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restartReplica() throws IOException {
        Runnable replica4 = () -> {
            try {
                Replica4.main(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(replica4);
        thread.start();
    }

    public void startRMListener(int RMPort) throws Exception {
        DatagramSocket socket = new DatagramSocket(RMPort);
        DatagramPacket packet = null;
        byte[] data = null;
        logger.info("Replica4 Manager Start");
        while(true)
        {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);

            String receiveMessage = new String(packet.getData(), 0, packet.getLength());


            if (receiveMessage.indexOf(FailureEnum.SoftWareFailure.toString()) != -1){
            	
                //TODO:log software failure
            } else if (receiveMessage.indexOf(FailureEnum.ServerCrash.toString()) != -1){
              String crashServerNum = receiveMessage.split("-")[0];
              logger.info("RM_" + crashServerNum + " ServerCrash");
              logger.info("RM_crashServerNum" + crashServerNum + " ServerCrash");
              TellServerCrash(7004);
              
              if (!crashServerNum.equals(this.ReplicaId)){
                  Thread checkThread = new Thread(new CheckAlive(crashServerNum));
                  checkThread.start();
              }

          } else {
              MessageForReplica recvMsg = parseToMessage(receiveMessage);
              moveToHoldBackQueue(recvMsg);
          }
      }
  }

  private void TellServerCrash(int CrashPort) throws IOException {
      InetAddress address = InetAddress.getByName("localhost");
      byte[] data = "SoftwareFailure".getBytes();
      DatagramPacket packet = new DatagramPacket(data, 0 , data.length , address , CrashPort);
      DatagramSocket socket = new DatagramSocket();
      socket.send(packet);
      socket.close();
  }
    //Send software fail msg "SoftwareFailure"
    private void TellSoftWareFail(int softwarePort) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        byte[] data = "SoftwareFailure".getBytes();
        DatagramPacket packet = new DatagramPacket(data, 0 , data.length , address , softwarePort);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
    }

    //move to hold back queue
    private void moveToHoldBackQueue(MessageForReplica recvMsg) throws IOException {
        if(!holdBackQueue.contains(recvMsg)){
            this.holdBackQueue.offer(recvMsg);
            moveToDeliveryQueue();
        } else {
        	//            reject();
        }
    }
    
    private void moveToDeliveryQueue() throws IOException {
        MessageForReplica message = this.holdBackQueue.peek();
        if (message == null) return;
        if (Integer.valueOf(message.getSeqId()) == this.SeqNumber && !this.deliveryQueue.contains(message)){
            MessageForReplica msg = this.holdBackQueue.poll();
            this.deliveryQueue.offer(msg);
            this.SeqNumber ++;
            executeMessage();
            moveToDeliveryQueue();
        }
    }

    private void executeMessage() throws IOException {
        MessageForReplica msg = this.deliveryQueue.peek();
        if(msg != null && SeqNumber >= DeliveryNum && Integer.valueOf(msg.getSeqId()) == DeliveryNum){
            //Execute message
            msg = this.deliveryQueue.poll();

            sendToReplicaAndReturnRes(msg);

            backUpQueue.offer(msg);
            DeliveryNum ++;

            //Then recursive call to check weather deliveryQueue still have message to run.
            executeMessage();
        }
    }

    private void listenBackUp(int backUpPort){
        logger.info("BackUp Listener Start");
        try {
            DatagramSocket socket = new DatagramSocket(backUpPort);
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);

            while (true) {
                socket.receive(packet);
                String msg = new String(packet.getData(),0 , packet.getLength());

                Comparator<MessageForReplica> comparator = new Comparator<MessageForReplica>() {
                    @Override
                    public int compare(MessageForReplica o1, MessageForReplica o2) {
                        return (Integer.valueOf(o1.getSeqId()) - Integer.valueOf(o2.getSeqId()));
                    }

                };
                Queue<MessageForReplica> q = new PriorityQueue<>(comparator);

                if (msg.equals("BackUp")){
                    while (!backUpQueue.isEmpty()){

                        MessageForReplica message = backUpQueue.poll();
                        q.offer(message);

                        String ms = message.getCity() + ":" + message.getMessage();

                        byte[] senddata = ms.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, packet.getAddress(), ReplicaPort.REPLICA_PORT.backUpPort);

                        socket.send(sendPacket);

                        byte[] buffer = new byte[1024];
                        DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);

                        socket.receive(packet1);

                        System.out.println(new String(packet1.getData(),0,data.length));
                    }
                }

                logger.info("BackUp End");
                backUpQueue = q;
            }


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Logger replicaLogger = Logger.getLogger("RM_4.log");
        replicaLogger.setLevel(Level.ALL);

        //Replica4
        FileHandler FileHandler = new FileHandler("RM_4" + ".log");
        FileHandler.setFormatter(new LoggerFormatter());
        replicaLogger.addHandler(FileHandler);

        Replica4Manager RM = new Replica4Manager(replicaLogger);
        
        //Run listener for Task,Crash and backup for MTL/TOR/OTW servers.
        Runnable RMListenerTask = () -> {
            try {
                RM.startRMListener(Replica.REPLICA.replica4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        /*
        Runnable CrashListener = () -> {
            RM.listenCrash(RMPortInfo.RM_PORT_INFO.recvCrash);
        };*/

        Runnable BackUpListener = () -> {
            RM.listenBackUp(RMPortInfo.RM_PORT_INFO.backUp);
        };

        Thread t1 = new Thread(RMListenerTask);
        //Thread t2 = new Thread(CrashListener);
        Thread t3 = new Thread(BackUpListener);

        t1.start();
       // t2.start();
        t3.start();
    }

}


enum ReplicaPort {
    REPLICA_PORT;
    final int port = 4444;
    final int backUpPort = 8084;
    final int softwareFail = 8884;
}

enum RMPortInfo{
    RM_PORT_INFO;
    final int recvMsg = 6004;
    final int execMsgToRplc = 2004;
    final int backUp = 5004;
    final int recvCrash = 7004;
}