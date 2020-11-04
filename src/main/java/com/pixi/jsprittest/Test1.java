/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest;


import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.job.Service;

import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import java.util.ArrayList;



import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author grega
 */
public class Test1 
{
    //HVRP
     ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());  
     
     public  VehicleRoutingProblem.Builder vrpBuilder(int maxJobs)
     {
         VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

   
        
        for (int a = 0; a<maxJobs ; a++)
        {
            int randomNum1 = ThreadLocalRandom.current().nextInt(0, 500 + 1);
            int randomNum2 = ThreadLocalRandom.current().nextInt(0, 500 + 1);
            int randomNum3 = ThreadLocalRandom.current().nextInt(0, 30 + 1);
            
            vrpBuilder.addJob(Service.Builder.newInstance(a+"").addSizeDimension(0, randomNum3).setLocation(Location.newInstance(randomNum1, randomNum2)).build());
        }    
            

        //add vehicle - finite fleet
        //2xtype1
        VehicleType type1 = VehicleTypeImpl.Builder.newInstance("type_1").addCapacityDimension(0, 120).setCostPerDistance(1.0).build();
        VehicleImpl vehicle1_1 = VehicleImpl.Builder.newInstance("1_1").setStartLocation(Location.newInstance(40, 40)).setType(type1).build();
        vrpBuilder.addVehicle(vehicle1_1);
        VehicleImpl vehicle1_2 = VehicleImpl.Builder.newInstance("1_2").setStartLocation(Location.newInstance(40, 40)).setType(type1).build();
        vrpBuilder.addVehicle(vehicle1_2);
        //1xtype2
        VehicleType type2 = VehicleTypeImpl.Builder.newInstance("type_2").addCapacityDimension(0, 160).setCostPerDistance(1.1).build();
        VehicleImpl vehicle2_1 = VehicleImpl.Builder.newInstance("2_1").setStartLocation(Location.newInstance(40, 40)).setType(type2).build();
        vrpBuilder.addVehicle(vehicle2_1);
        //1xtype3
        VehicleType type3 = VehicleTypeImpl.Builder.newInstance("type_3").addCapacityDimension(0, 300).setCostPerDistance(1.3).build();
        VehicleImpl vehicle3_1 = VehicleImpl.Builder.newInstance("3_1").setStartLocation(Location.newInstance(40, 40)).setType(type3).build();
        vrpBuilder.addVehicle(vehicle3_1);

        //add penaltyVehicles to allow invalid solutions temporarily
//		vrpBuilder.addPenaltyVehicles(5, 1000);

        //set fleetsize finite
        vrpBuilder.setFleetSize(FleetSize.FINITE);
        return vrpBuilder;
     }        
     
     public int testCase (VehicleRoutingProblem.Builder vrpBuilder)
     {
        //build problem
        VehicleRoutingProblem vrp = vrpBuilder.build();

        //VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
         VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(vrp)
            .setProperty(Jsprit.Parameter.FAST_REGRET, "true")
            .setProperty(Jsprit.Parameter.THREADS, Runtime.getRuntime().availableProcessors()+"").buildAlgorithm();
       
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

        VehicleRoutingProblemSolution best = Solutions.bestOf(solutions);
        return vrp.getJobs().size();
        //SolutionPrinter.print(vrp, best, SolutionPrinter.Print.VERBOSE);
       
     }
    
     private void runSync( ArrayList<CTask> lt ) throws Exception
     {
         long s = System.currentTimeMillis();
      
         for (CTask t:lt)
         {
             t.call();
         }    
         
          long e = System.currentTimeMillis();
         long all = e - s;
         Logger.getLogger(Test1.class.getName()).info("DONE SYNC:"+lt.size()  + " time in ms = "+all + " sec ="+(all/1000));
     }        
     
     private void runASync( ArrayList<CTask> lt ) throws InterruptedException
     {
         long s = System.currentTimeMillis();
         List<Future<Integer>> le = executorService.invokeAll(lt);
         long e = System.currentTimeMillis();
         long all = e - s;
         Logger.getLogger(Test1.class.getName()).info("DONE ASYNC:"+le.size() + " time in ms = "+all + " sec ="+(all/1000));
         executorService.shutdown();
     }   
     
     public static void main(String[] args) 
     {

         Test1 t1 = new Test1();
         ArrayList<CTask> lt = new ArrayList<>();
         /*
         for (int t = 0; t<200; t++)
         {    
            VehicleRoutingProblem.Builder  b = t1.vrpBuilder(t);
            lt.add(new CTask(b, t1));
         }   
         */
         VehicleRoutingProblem.Builder  b1 = t1.vrpBuilder(500);
         lt.add(new CTask(b1, t1));
         VehicleRoutingProblem.Builder  b2 = t1.vrpBuilder(600);
         lt.add(new CTask(b2, t1));
         VehicleRoutingProblem.Builder  b3= t1.vrpBuilder(900);
         lt.add(new CTask(b3, t1));
         VehicleRoutingProblem.Builder  b4 = t1.vrpBuilder(522);
         lt.add(new CTask(b4, t1));
          VehicleRoutingProblem.Builder  b5= t1.vrpBuilder(522);
         lt.add(new CTask(b5, t1));
          VehicleRoutingProblem.Builder  b6 = t1.vrpBuilder(522);
         lt.add(new CTask(b6, t1));
          VehicleRoutingProblem.Builder  b7 = t1.vrpBuilder(256);
         lt.add(new CTask(b7, t1));
          VehicleRoutingProblem.Builder  b8 = t1.vrpBuilder(665);
         lt.add(new CTask(b8, t1));
         try 
         {
              t1.runSync(lt);
             t1.runASync(lt);
            
         }
         catch (InterruptedException ex) {
             Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
         } catch (Exception ex) {
             Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
         }
       

    }
     
     
}
