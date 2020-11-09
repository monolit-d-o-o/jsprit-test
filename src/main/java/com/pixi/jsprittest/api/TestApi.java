/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pixi.jsprittest.service.TestService;
import java.util.concurrent.ExecutionException;
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
    
    @GetMapping("/hvrp/{numclients}")
    public  String doHVRP(@PathVariable int numclients) throws InterruptedException, ExecutionException
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
}
