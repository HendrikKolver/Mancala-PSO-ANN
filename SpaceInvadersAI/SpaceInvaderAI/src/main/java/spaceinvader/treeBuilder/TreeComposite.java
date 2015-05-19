package spaceinvader.treeBuilder;

import java.util.Random;
import spaceinvader.neuralNetwork.NeuralNetwork;

/**
 *
 * @author Hendrik Kolver
 */
public class TreeComposite extends TreeInterface {       
     
    public TreeComposite(NeuralNetwork n)
    {
       evaluation = n;
       children = null;
       next = null;
       nodeScore = 0;
    }
     
    public TreeInterface getNext()
    {
        return next;
    }
     
    public void setNext(TreeInterface newNode)
    {
        next = newNode;
    }
     
    public void addChild(TreeInterface node)
    {

        if(children == null)
       {
           children = node;
           children.nodeDepth = nodeDepth+1;
       }
       else
       {
           TreeInterface tmpNode = children;

           while(tmpNode.next != null)
           {
               tmpNode = tmpNode.next;
           }
           tmpNode.next = node; 
           node.nodeDepth = nodeDepth+1;
       }
    }

     public void evaluateMyself(int gPlayer)
     {
         //TODO Simple evaluation function  
     }

    @Override
    boolean isGameOver() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
         
}
