
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hendrik
 */
public class Neuron {
    
    public double weight;
    public double input;
    public Neuron()
    {
        weight =1;
        input=0;
    }
    
    public void setInputs(double input)
    {
        this.input += input; 
    }
    
    public void outputSet(double in)
    {
        input+= in;   
    }
    
    public double fireLinear()
    {
        
       return (input * weight);
    }
    
    public double fireSigmoid()
    {
        
       double tmp =((1-Math.exp(-2*(input*weight))) / (1+Math.exp(-2*(input*weight))));

        return tmp;
    }
    
    public double fireOutput()
    {
        if(input<0)
        {  
            return input*-1;
        }
        else
            return input;
    }
    
    public void clear()
    {
        input = 0;
        weight = 1; 
    }

    public void setWeight(double w)
    {
        weight = w;
    }
    
    public double biasFireLinear()
    {
        return (-1*weight);
    }

    public double biasFireSigmoid()
    {
        double tmp =((1-Math.exp(-2*(-1*weight))) / (1+Math.exp(-2*(-1*weight))));
        
        return tmp;
    }
    
    
   
}
