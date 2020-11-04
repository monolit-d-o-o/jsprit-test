/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest;

import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import java.util.concurrent.Callable;

/**
 *
 * @author grega
 */
public class CTask implements Callable<Integer> 
{
    VehicleRoutingProblem.Builder vrpBuilder;
    Test1 t;
    public CTask(VehicleRoutingProblem.Builder vrpBuilder , Test1 t)
    {
        this.vrpBuilder = vrpBuilder;
        this.t = t;
    }     
    
    
    @Override 
    public Integer call() throws Exception {
      return t.testCase(this.vrpBuilder);
    }
    
}
