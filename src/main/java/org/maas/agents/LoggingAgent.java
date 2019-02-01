package org.mas_maas.agents;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maas.GraphicsTest;
import org.maas.Objects.Bakery;
import org.maas.agents.BaseAgent;
import org.maas.utils.Time;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LoggingAgent extends BaseAgent {
    private Vector<Bakery> bakeries;
    private String scenarioPath;
    private AtomicInteger messageProcessing = new AtomicInteger(0);

    private static final String FILE_NAME = "mas_maas_log.csv";
    private StringBuffer stringBuffer = new StringBuffer();
    private FileChannel channel;
    private RandomAccessFile stream;
    // private Vector<Equipment> equipments;
    private Time endTime;

    protected void setup(){
        super.setup();

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            scenarioPath = (String) args[0];
            //String endTimeString = (String) args[1];
            //MetaInfo metaInfo = JSONConverter.parseMetaInfo(scenarioPath + "meta.json");

            String endTimeString = new String("01.02.00");
            endTime = new Time(endTimeString);
        } else {
            scenarioPath = "small";
            endTime = new Time(1,12,0);
        }

        this.register("LoggingAgent", "JADE-bakery");
        System.out.println("\n\n\nHello! " + getAID().getLocalName() + " is ready.\n\n\n\n");
        System.out.println("\n\n\nEnd time is:! " + endTime);

        addBehaviour(new timeTracker());
        addBehaviour(new ReceiveKneadingNotification());
        addBehaviour(new ReceivePreparationNotification());
        addBehaviour(new ReceiveDoughNotification());
        addBehaviour(new ReceiveBakingNotification());
        addBehaviour(new ReceiveBakingPreparationNotification());
        addBehaviour(new ReceiveCooling());


        try {
            stream = new RandomAccessFile(FILE_NAME, "rw");
            channel = stream.getChannel();

        } catch (FileNotFoundException e) {
            // TODO Let's hope we never get here :/
            e.printStackTrace();
        }

    }

    protected void appendToBuffer(String str)
    {
        System.out.println("APPEND TO BUFFER n\n\n\n\n\n" + str + "\n\n\n\n");
        String cleaned = processGetSender(str);
        //stringBuffer.append(str + '\n');
        //stringBuffer.append(baseAgent.getCurrentTime().toString() + ", ");
        Time curTime = baseAgent.getCurrentTime();
        System.out.println("Time update: " + (curTime.getMinute() + curTime.getHour() * 60 + curTime.getDay() * 3600));
        stringBuffer.append((curTime.getMinute() + curTime.getHour() * 60 + curTime.getDay() * 3600) + ", ");
        stringBuffer.append(cleaned + "\n");
    }

    protected void writeBuffer()
    {
        byte[] strBytes = stringBuffer.toString().getBytes();
        stringBuffer.setLength(0); // reset the buffer after a write
        ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
        buffer.put(strBytes);
        buffer.flip();

        try {
            channel.write(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void closeWriter()
    {
        try {
            stream.close();
            channel.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void takeDown() {
        System.out.println(getAID().getLocalName() + ": Terminating.");
        this.deRegister();
    }

    protected String processGetSender(String sender) {
        String cleanedString = "";

        sender = sender.trim();
        Pattern pattern = Pattern.compile(".*:name (.*?)_(.*?-\\d{3}).*");
        Matcher matcher = pattern.matcher(sender);
        if (matcher.find())
        {
            cleanedString = matcher.group(1) + ", " + matcher.group(2);
            System.out.println("\n\n\n|" + cleanedString + "|\n\n\n");

            // not all messages are the same so there is sometimes additional info we may want
            // we use the following to grab that extra info (e.g. which kneading machine was used)
            // could be more efficient...
            Pattern additionalInfoPattern = Pattern.compile(".*:name .*?_.*?_(.*?)@.*");
            Matcher additionalInfoMatcher = additionalInfoPattern.matcher(sender);
            if (additionalInfoMatcher.find())
            {
                cleanedString += ", " + additionalInfoMatcher.group(1);
            }
        }

        System.out.println("CLEANED STRING\n\n\n\n\n\n" + cleanedString + "\n\n\n\n");
        return cleanedString;
    }

    private class timeTracker extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            if (!baseAgent.getAllowAction()) {
                return;
            }

            // only advance if we aren't currently processing any messages
            if (messageProcessing.get() <= 0)
            {
                writeBuffer();

                Time curTime = baseAgent.getCurrentTime();
                if (curTime.equals(endTime))
                {
                    System.out.println("Trying to launch my boi");
                    closeWriter();
                    String[] args = new String[] {""};
                    GraphicsTest.main(args);
                    //System.out.println("I've called for a new thread");
                    //GraphicsTest graph = new GraphicsTest(); 
                    //Thread graphThread = new Thread(graph);
                    //graphThread.start();
                }
                baseAgent.finished();
            }
        }
    }

    private class ReceiveKneadingNotification extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            messageProcessing.incrementAndGet();

            MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId("kneading-notification"));
            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                appendToBuffer( msg.getSender() + ", " + msg.getContent());
                messageProcessing.decrementAndGet();

            } else {
                messageProcessing.decrementAndGet();
                block();
            }
        }
    }

    private class ReceivePreparationNotification extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            messageProcessing.incrementAndGet();

            MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId("preparation-notification"));
            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                appendToBuffer( msg.getSender() + ", " + msg.getContent());
                messageProcessing.decrementAndGet();

            } else {
                block();
                messageProcessing.decrementAndGet();
            }
        }

    }

    private class ReceiveDoughNotification extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            messageProcessing.incrementAndGet();

            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchConversationId("dough-Notification"));
            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                appendToBuffer( msg.getSender() + ", " + msg.getContent());
                messageProcessing.decrementAndGet();

            } else {
                messageProcessing.decrementAndGet();
                block();
            }
        }
    }

    private class ReceiveBakingNotification extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            messageProcessing.incrementAndGet();

            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchConversationId("baking-notification"));
            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                appendToBuffer( msg.getSender() + ", " + msg.getContent());
                messageProcessing.decrementAndGet();

            } else {
                messageProcessing.decrementAndGet();
                block();
            }
        }
    }

    private class ReceiveBakingPreparationNotification extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            messageProcessing.incrementAndGet();

            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchConversationId("preparationBaking-notification"));
            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                appendToBuffer( msg.getSender() + ", " + msg.getContent());
                messageProcessing.decrementAndGet();

            } else {
                messageProcessing.decrementAndGet();
                block();
            }
        }
    }

    private class ReceiveCooling extends CyclicBehaviour{
        private static final long serialVersionUID = 1L;

        public void action(){
            messageProcessing.incrementAndGet();

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
            ACLMessage msg = baseAgent.receive(mt);
            if (msg != null) {
                appendToBuffer( msg.getSender() + ", " + msg.getContent());
                messageProcessing.decrementAndGet();

            } else {
                messageProcessing.decrementAndGet();
                block();
            }

        }
    }
}
