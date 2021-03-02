/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pixi.jsprittest.listeners.ICalcEvents;
import com.pixi.jsprittest.service.CalculatorService;
import com.pixi.jsprittest.service.MqttService;
import com.pixi.jsprittest.service.TestService;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author grega
 */
@RestController
@RequestMapping("/api/test")
public class TestApi 
{
    @Autowired
    TestService service;
    
    @Autowired
    MqttService mqttcli;
    
    @Autowired 
    HttpServletRequest sr;
    
    @Autowired 
    CalculatorService  cs;
    
  
    ICalcEvents myeventListener;
    
   
    
    @GetMapping("/hvrp/keycloakinfo")
    public String getkcinfo() throws JsonProcessingException
    {
       RefreshableKeycloakSecurityContext scontext = (RefreshableKeycloakSecurityContext) sr.getAttribute(KeycloakSecurityContext.class.getName());
       if (scontext != null)
       {    
            String kcsub =  scontext.getToken().getSubject();
            String kcpus =  scontext.getToken().getPreferredUsername();
            return  "subject:"+kcsub+ " puname:"+kcpus;
       }
       else
       {
           return "RefreshableKeycloakSecurityContext null !";
       }    
    }    
    
    @RolesAllowed("heavy")
    @GetMapping("/heavy/hvrp/{numclients}")
    public  String doHVRP( @PathVariable int numclients) throws InterruptedException, ExecutionException
    {
     
        return service.execHVRP(numclients);
    }  
    
    @GetMapping("/hvrp/taskrunning")
    public int getRunningTasksSize()
    {
        return service.getUnfinishedTasksSize();
    }  
    
    @GetMapping("/hvrp/tasklist")
    public String getRunningTasks() throws JsonProcessingException
    {
        return service.getUnfinishedTasksLst();
    }  
    
    @GetMapping("/hvrp/cancel/{id}")
    public void getRunningTasks(@PathVariable  String id) throws JsonProcessingException
    {
         service.cancel(id);
    }
    
    @GetMapping("/hvrp/result/{id}")
    public String getResult(@PathVariable  String id) throws JsonProcessingException
    {
       return  service.result(id);
    }  
    
    @GetMapping("/hvrp/sumcalc/{a}/{b}")
    public int sum(@PathVariable  int a , @PathVariable  int b) throws JsonProcessingException
    {
       return  cs.sum(a, b);
    }  
    
    
    
    @GetMapping("/hvrp/mqtt/{nummsgs}")
    public String testMqtt(@PathVariable  int nummsgs) throws JsonProcessingException
    {
        int statsok = 0;
        int statfail = 0;
            
        try {
            
            mqttcli.connnect("tcp://192.168.0.140:1883", UUID.randomUUID().toString());
            for (int a = 0; a < nummsgs ; a++) 
            {
                try {
                    String uuid = UUID.randomUUID().toString();
                    mqttcli.subscribe(uuid);
                    mqttcli.sendMsg(uuid, "msg:"+uuid);
                    statsok++;
                    
                }
                catch (MqttException ex)
                {
                    Logger.getLogger(TestApi.class.getName()).log(Level.SEVERE, null, ex);
                    statfail ++;
                }
            }
            mqttcli.bye();
          
        } catch (MqttException ex) {
            Logger.getLogger(TestApi.class.getName()).log(Level.SEVERE, null, ex);
            statfail ++;
        }
        return "done ok" +  statsok + " !ok= "+ statfail;
    }
    
  
    
    @PostConstruct
    public void init()
    {
        Logger.getLogger(TestApi.class.getName()).info("Listener on  ! let test mqtt");
        try {
            mqttcli.subscribe("calculator");
        } catch (MqttException ex) {
            Logger.getLogger(TestApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        cs.setListener(new ICalcEvents() {
            @Override
            public void onSum(int result, int a, int b) 
            {
                try 
                {
                    Logger.getLogger(TestApi.class.getName()).info("on sum:"+" input a="+a + "  input b="+b + "  rezult="+result);
                    mqttcli.sendMsg("calculator", "on sum:"+" input a="+a + "  input b="+b + "  rezult="+result);
                } 
                catch (MqttException ex) 
                {
                    Logger.getLogger(TestApi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void numOfSumOperations(int c) 
            {
                 try 
                {
                    Logger.getLogger(TestApi.class.getName()).log(Level.INFO, "operation counter:{0}", c+"");
                    mqttcli.sendMsg("calculator", "operation counter:"+c);
                } 
                catch (MqttException ex) 
                {
                    Logger.getLogger(TestApi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }        
}
