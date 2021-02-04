/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

/**
 *
 * @author grega@monolit.si
 */
@Service
public class MqttService implements MqttCallback  
{
    MqttClient client;
    public MqttService()
    {
        
       
   
    }        
    
    public void connnect(String mqtturl , String uiid) throws MqttException
    {
        if (client == null ||(  client != null && ! client.isConnected() ) )
        {    
            client = new MqttClient( mqtturl , uiid);
            client.connect();
            client.setCallback(MqttService.this);
        }
    }        
    
    public void subscribe(String topic) throws MqttException
    {
        client.subscribe(topic);
    }        
    
    public void sendMsg(String topic , String msg) throws MqttException 
    {
        MqttMessage message = new MqttMessage();
        message.setPayload(msg.getBytes());
        client.publish(topic, message);
    }        
    
    
    public void bye() throws MqttException 
    {
      
        if(client.isConnected())
                client.disconnect();
        client.close();
        
    }        
    
    
    @Override
    public void connectionLost(Throwable thrwbl) {
        try 
        {
          
            if(!client.isConnected())
                client.connect();
        } catch (MqttException ex) {
            Logger.getLogger(MqttService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void messageArrived(String string, MqttMessage mm) throws Exception {
          Logger.getLogger(MqttService.class.getName()).log(Level.INFO, string+":"+mm.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
       
    }
    
}
