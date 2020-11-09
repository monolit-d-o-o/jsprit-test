/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest.jobs;

import com.graphhopper.jsprit.core.algorithm.SearchStrategy;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.termination.PrematureAlgorithmTermination;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author grega
 */
public class TestJob implements Callable<IJob> , IJob<String>
{
    //HVRP
    int numoflocations;
    String uid;
    VehicleRoutingProblemSolution best;
    boolean canceled = false;
     
    public TestJob(int numoflocations)
    {
        this.numoflocations = numoflocations;
    }        
    
    public String generateTaskID()
    {
       this.uid =  UUID.randomUUID().toString();
       return this.uid ;
    }        
    
    @Override
    public String getTaskID()
    {
      
       return this.uid ;
    }        
    
    
    @Override
    public IJob call() throws Exception 
    {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

  
        
        for (int a = 0; a<this.numoflocations ; a++)
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
        vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
        
        VehicleRoutingProblem vrp = vrpBuilder.build();
        

        //VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
         VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(vrp)
            .setProperty(Jsprit.Parameter.FAST_REGRET, "true")
            .setProperty(Jsprit.Parameter.THREADS, Runtime.getRuntime().availableProcessors()+"").buildAlgorithm();
       
        vra.addTerminationCriterion(this);
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

        this.best = Solutions.bestOf(solutions);
        return this;
    }

    @Override
    public String getResult() {
       return  this.best.toString();
    }

    @Override
    public boolean isPrematureBreak(SearchStrategy.DiscoveredSolution ds) {
        return this.canceled;
    }

    @Override
    public void cancel() {
       this.canceled = true;
    }

   
    
}
