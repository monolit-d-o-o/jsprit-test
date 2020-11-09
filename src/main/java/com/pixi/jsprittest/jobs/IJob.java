/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest.jobs;

import com.graphhopper.jsprit.core.algorithm.termination.PrematureAlgorithmTermination;

/**
 *
 * @author grega
 * @param <T>
 */
public interface IJob<T> extends PrematureAlgorithmTermination
{
     public String getTaskID();
     public T getResult();
     public void cancel();
}
