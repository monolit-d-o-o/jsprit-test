/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest.service;

import com.pixi.jsprittest.listeners.ICalcEvents;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author grega@monolit.si
 */
@Service
@Scope("singleton")
public class CalculatorService 
{
    ICalcEvents listener;
    private int sumcnt = 0;
    
    public CalculatorService()
    {
    
    }
    
    public void setListener( ICalcEvents listener)
    {
        this.listener = listener;
    }
    
    public int sum(int a , int b)
    {
        int r = b + a;
        sumcnt ++;
        if ( this.listener != null)
        {
            this.listener.onSum(r, a, b);
            this.listener.numOfSumOperations(sumcnt);
        }    
        return r;    
    }     
    
   
    
}
