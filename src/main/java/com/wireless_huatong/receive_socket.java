package com.wireless_huatong;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class receive_socket {  
	  
    public static void main(String[] args) {  
        receive_socket socketServer = new receive_socket();  
        socketServer.startServer();  
    }  
      
    public void startServer() {  
        ServerSocket serverSocket = null;  
        BufferedReader reader = null;  
        Socket socket = null;  
        try {  
            // �˿ں�ֻҪ����ͻ����  
            serverSocket = new ServerSocket(9999);  
            
            System.out.println("server started..");  
            // ��������״̬���ȴ��ͻ��˽���  
            socket = serverSocket.accept();              
            System.out.println("client " + socket.hashCode() + " connected");  
            // ��socket�ж�ȡ����  		
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
            String receiveMsg;  
            while ((receiveMsg = reader.readLine()) != null) { // ��"\n"����            	
            	List<String> list = new ArrayList<>();            	
            	for (String string : receiveMsg.split("'|'")) {
					list.add(string);
				}
                System.out.println(list);                
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                reader.close();  
                socket.close();  
                serverSocket.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }    
}  

