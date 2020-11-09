/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.pixi.jsprittest.jobs.IJob;
import com.pixi.jsprittest.jobs.TestJob;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import org.springframework.stereotype.Service;

/**
 *
 * @author grega
 */
@Service
@Scope("singleton")
public class TestService 
{
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor executor;
    private final BlockingQueue<Future<IJob> > tasks = new ArrayBlockingQueue<>(100);
    private final HashMap<String,IJob > tasksMap = new HashMap<>();
    private final HashMap<String,IJob > tasksMapResults = new HashMap<>();
    //private final HashMap<IJob> tasks = new ArrayBlockingQueue<>(100);
    
    
    public TestService()
    {
        
    }        
    
    @PostConstruct
    public void init() {
       Logger.getLogger(TestService.class.getName()).info("task queue started --");
       executor.execute(() -> {
            while (true) 
            {
                try
                {
                    Future<IJob> j = tasks.take();
                    if (! j.isCancelled())
                    {
                         IJob jDone = j.get();
                         tasksMapResults.put(jDone.getTaskID(), jDone);
                         tasksMap.remove(jDone.getTaskID()); 
                         
                    }    
                   
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(TestService.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
    }
    
    public String execHVRP(int numlocations) throws InterruptedException, ExecutionException 
    {
        if (tasks.size() > executor.getCorePoolSize())
        {
            return "Max task on server exeeded :"+executor.getCorePoolSize()+"<"+tasks.size() ;
          
        }
        TestJob j = new TestJob(numlocations);
        String jobID = j.generateTaskID();
        Future<IJob> f = executor.submit(j);
        
        tasks.put(f);
        tasksMap.put(jobID, j);
        return jobID;
    }    

    public int getUnfinishedTasksSize()
    {
        return tasksMap.size();
    }
    
    public String getUnfinishedTasksLst() throws JsonProcessingException
    {
        return  new ObjectMapper().writeValueAsString(tasksMap);
    }
    
    public void cancel(String id) 
    {
        Logger.getLogger(TestService.class.getName()).info("try cancel:"+id);
        IJob f = tasksMap.get(id);
        if (f != null)
        {    
            tasksMap.remove(id);
            f.cancel();
            Logger.getLogger(TestService.class.getName()).info("done cancel:"+id);
        }    
    }
    
    
    public String result(String id) throws JsonProcessingException 
    {
        IJob f = tasksMapResults.get(id);
        if (f != null)
        {    
            return  new ObjectMapper().writeValueAsString(f.getResult());
        }
        return "Still processin :"+id;
    }
    
    
}
