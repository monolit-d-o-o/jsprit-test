/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest.listeners;

/**
 *
 * @author grega@monolit.si
 */
public interface ICalcEvents 
{
        public void onSum( int result , int a , int b );
        public void numOfSumOperations( int c);
}
