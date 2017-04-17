/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package adaboost;

/**
 *
 * @author Monjura
 */
import java.util.*;

public class Node {
    double entropy;
    ArrayList data;
    int split_attr;	//attribute used to split data set
    int split_val; // the attribute-value that is used to divide the parent node
    int leaf;
    Node []children;
    Node parent;

    Node()
    {
        data = new ArrayList();
    }

}


