/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hendrik
 */
public class NeuralNetwork {
    Neuron inputs[];
    Neuron outputs[];
    Neuron hidden[];
    double weights[];
    int outputCount;
    Neuron inputBias;
    Neuron hiddenBias;
    int sigmoid;
    
    
    public NeuralNetwork(int inputs, int outputs, int hidden, int sigmoid)
    {
        
        outputCount = (int)(hidden/outputs);
        
        this.inputs = new Neuron[inputs];
        for(int x=0;x<inputs;x++)
           this.inputs[x] = new Neuron(); 
        
        this.outputs = new Neuron[outputs];
        for(int x=0;x<outputs;x++)
           this.outputs[x] = new Neuron(); 
        
        this.hidden = new Neuron[hidden];
        for(int x=0;x<hidden;x++)
           this.hidden[x] = new Neuron();
        
        weights = new double[getConnections()];
        inputBias = new Neuron();
        hiddenBias = new Neuron();
        this.sigmoid = sigmoid;
    }
    
    
    public void updateWeights(double pos[])
    {
        for(int x=0;x<getConnections();x++)
        {
            weights[x] = pos[x];
            
        } 
    }
    
    public Neuron[] calculate(double inputValues[])
    {
       //linear calculation 
      if(sigmoid == 1)
      {
         int weightCounter=0;
        //input stage
        
        for(int x=0; x< inputs.length;x++)
        { 
            //set inputs
          inputs[x].setInputs(inputValues[x]);
        }
        
        //hidden stage
        for(int x=0; x<inputs.length;x++)
        {
            for(int y=0;y< hidden.length;y++)
            {
                //set weight for connection
                inputs[x].setWeight(weights[weightCounter]);
                //calculate activation and fire
                double inputVal = (inputs[x].fireLinear());
                //add value to hidden layer
                hidden[y].setInputs(inputVal);
                weightCounter++;
            }
            //clear that input values when done
            inputs[x].clear();
        }
        
        //input bias 
        for(int y=0;y< hidden.length;y++)
            {
                //set weight for bias connection 
                inputBias.setWeight(weights[weightCounter]);
                double inputVal = (inputBias.biasFireLinear());
               //add bias to hidden unit
                hidden[y].setInputs(inputVal);
                weightCounter++;
                //clear weight from bias
                inputBias.clear();
            }
        
        //output stage

            for(int x=0;x<hidden.length;x++)
            {
                for(int y=0; y<outputs.length;y++)
                {
                    //set weight for connection
                    hidden[x].setWeight(weights[weightCounter]);
                    //calculate activation for hidden unit and fire
                    double inputVal = (hidden[x].fireLinear());
                    //add value to output layer
                    outputs[y].outputSet(inputVal);
                    weightCounter++;  
                }
                //clear values from that hidden unit
                hidden[x].clear();
            } 

        //hidden bias
        for(int y=0;y< outputs.length;y++)
            {
                //set weight for bias unit
                hiddenBias.setWeight(weights[weightCounter]);
                //calculate activation and fire
                double inputVal = (hiddenBias.biasFireLinear()); 
                //add value to output layer
                outputs[y].outputSet(inputVal);
                weightCounter++;
                //clear values from bias unit
                hiddenBias.clear();
            }
       
      }
      else
      {
         int weightCounter=0;
        //input stage
        
        for(int x=0; x< inputs.length;x++)
        { 
            //set inputs
          inputs[x].setInputs(inputValues[x]);
        }
        
        //hidden stage
        for(int x=0; x<inputs.length;x++)
        {
            for(int y=0;y< hidden.length;y++)
            {
                //set weight for connection
                inputs[x].setWeight(weights[weightCounter]);
                //calculate activation and fire
                double inputVal = (inputs[x].fireSigmoid());
                //add value to hidden layer
                hidden[y].setInputs(inputVal);
                weightCounter++;
            }
            //clear that input values when done
            inputs[x].clear();
        }
        
        //input bias 
        for(int y=0;y< hidden.length;y++)
            {
                //set weight for bias connection 
                inputBias.setWeight(weights[weightCounter]);
                double inputVal = (inputBias.biasFireSigmoid());
               //add bias to hidden unit
                hidden[y].setInputs(inputVal);
                weightCounter++;
                //clear weight from bias
                inputBias.clear();
            }
        
        //output stage

            for(int x=0;x<hidden.length;x++)
            {
                for(int y=0; y<outputs.length;y++)
                {
                    //set weight for connection
                    hidden[x].setWeight(weights[weightCounter]);
                    //calculate activation for hidden unit and fire
                    double inputVal = (hidden[x].fireSigmoid());
                    //add value to output layer
                    outputs[y].outputSet(inputVal);
                    weightCounter++;  
                }
                //clear values from that hidden unit
                hidden[x].clear();
            } 

        //hidden bias
        for(int y=0;y< outputs.length;y++)
            {
                //set weight for bias unit
                hiddenBias.setWeight(weights[weightCounter]);
                //calculate activation and fire
                double inputVal = (hiddenBias.biasFireSigmoid()); 
                //add value to output layer
                outputs[y].outputSet(inputVal);
                weightCounter++;
                //clear values from bias unit
                hiddenBias.clear();
            }
      }
        return outputs;
    }
    
    public int getConnections()
    {
        int tmpCounter = 0;
        //input stage
        
        tmpCounter= ((inputs.length+1)*hidden.length);
        tmpCounter+= ((hidden.length+1)*outputs.length);
        //output stage
        
        
       
        return tmpCounter;
    }
}
