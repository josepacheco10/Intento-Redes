/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java_video_stream;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author imran
 */
public class JavaClient {
    public static DatagramSocket ds;

    public static void main(String[] args) throws Exception {
        ds = new DatagramSocket();
        
        byte[] init = new byte[62000];
        init = "givedata".getBytes();
        
        // CAMBIOS
        
        //byte[] ipAddr = new byte[] { 127, 2, 2, 0 };
        // InetAddress addr = InetAddress.getByName("157.253.228.83");
        InetAddress addr = InetAddress.getByName("157.253.42.56");
        
        
     // CAMBIOS
        
        DatagramPacket dp = new DatagramPacket(init,init.length,addr,4321);
        
        ds.send(dp);
        
        DatagramPacket rcv = new DatagramPacket(init, init.length);
        
        ds.receive(rcv);
        System.out.println(new String(rcv.getData()));
        
        System.out.println(ds.getPort());
        Vidshow vd = new Vidshow();
        vd.start();
        
        String modifiedSentence;

        // InetAddress inetAddress = InetAddress.getByName("157.253.228.83");
        InetAddress inetAddress = InetAddress.getByName("157.253.42.56");
        
        
        //.getByName(String hostname); "CL11"
        System.out.println(inetAddress);

        Socket clientSocket = new Socket(inetAddress, 8082); //AQUI SE CAMBIO OTRA VEZ JIJIS
        DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToServer.writeBytes("Thanks man\n");

        CThread write = new CThread(inFromServer, outToServer, 0);
        CThread read = new CThread(inFromServer, outToServer, 1);

        write.join();
        read.join();
        clientSocket.close();
    }
}

class Vidshow extends Thread {

    static JFrame jf = new JFrame();
    public static JPanel jp = new JPanel(new GridLayout(2,1));
    
    // public static JPanel half = new JPanel(new GridLayout(3,1));
    JLabel jl = new JLabel();
    public static JTextArea ta,tb;
    
    byte[] rcvbyte = new byte[62000];
    
    DatagramPacket dp = new DatagramPacket(rcvbyte, rcvbyte.length);
    BufferedImage bf;
    ImageIcon imc;
    
    
    public Vidshow() throws Exception {
        //sc = mysoc;
        //sc.setTcpNoDelay(true);
    	jp.setSize(640, 960);
        jf.setSize(640, 960);
        jf.setTitle("Vista de Cliente");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setAlwaysOnTop(true);
        jf.setLayout(new BorderLayout());
        jf.setVisible(true);
        jl.setSize(640, 960);
        
        jp.add(jl);
        //jp.add(half);
        jf.add(jp);
        
        
        //JScrollPane jpane = new JScrollPane();
        //jpane.setSize(300, 200);
        //ta = new JTextArea();
        //tb = new JTextArea();
        //jpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //jpane.add(ta);
        //jpane.setViewportView(ta);
        //half.add(jpane);
        //half.add(tb);
        //ta.setText("Begins\n");   
    }

    @Override
    public void run() {

        try {
            System.out.println("got in");
            do {
                System.out.println("doing");
                System.out.println(JavaClient.ds.getPort());
                
                JavaClient.ds.receive(dp);
                System.out.println("received");
                ByteArrayInputStream bais = new ByteArrayInputStream(rcvbyte);
                
                bf = ImageIO.read(bais);

                if (bf != null) {
                    imc = new ImageIcon(bf);
                    jl.setIcon(imc);
                    Thread.sleep(15);
                }
                jf.revalidate();
                jf.repaint();
                

            } while (true);

        } catch (Exception e) {
            System.out.println("couldnt do it");
        }
    }
}

class CThread extends Thread {

    BufferedReader inFromServer;
    Button sender = new Button("Pause");
    Button play = new Button("Play");
    
    DataOutputStream outToServer;
    public static String sentence;
    int RW_Flag;

    public CThread(BufferedReader in, DataOutputStream out, int rwFlag) {
        
    	inFromServer = in;
        outToServer = out;
        RW_Flag = rwFlag;
        Canvas_Demo x = new Canvas_Demo();
        
      	 //Vidshow.half.add(sender);
         //Vidshow.half.add(play);
         
         sender.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
         	x.pausarVideo();
         }
       });
         
         
         play.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					x.playVideo();
				}
			});
        
        
        
        
        if(rwFlag == 0) {
        }
        start();
    }

    public void run() {
        String mysent;
        try {
            while (true) {
                if (RW_Flag == 0) {
                    if(sentence.length()>0)
                    {
                        Vidshow.jp.revalidate();
                        Vidshow.jp.repaint();
                        outToServer.writeBytes(sentence + '\n');
                        sentence = null;
                        Vidshow.tb.setText(null);
                    }
                } else if (RW_Flag == 1) {
                    mysent = inFromServer.readLine();
                    
//                    Vidshow.ta.append(mysent+"\n");
//                    Vidshow.ta.setCaretPosition(Vidshow.ta.getDocument().getLength());
//                    Vidshow.half.revalidate();
//                    Vidshow.half.repaint();
                    Vidshow.jp.revalidate();
                    Vidshow.jp.repaint();
                    
                    System.out.println("From : " + sentence);
                    sentence = null;
                }
            }
        } 
        
        catch (Exception e) {
        }
    }
}