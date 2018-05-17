from util import entropy, information_gain, partition_classes
from collections import Counter

def partition(x, y, split_val):
    current_y = [[],[]]
    if str(x[0]).isdigit():
        for index, i in enumerate(x):
            if i <= split_val:
                current_y[0].append(y[index])
            else:
                current_y[1].append(y[index])
    else:
        for index, i in enumerate(x):
            if i == split_val:
                current_y[0].append(y[index])
            else:
                current_y[1].append(y[index])
    return current_y


def recursive(X,y):
    if y.count(y[0]) == len(y):
        return y[0]
    
    MaxIG = 0
    
    for i in range(len(X[0])):        
        column = [x[i] for x in X]
        for value in set(column):            
            current_y = partition(column, y, value)
            current_IG = information_gain(y, current_y)
                
            if current_IG > MaxIG:
                MaxIG = current_IG
                split_attribute = i
                split_value = value

    if MaxIG == 0:
        a = Counter(y)
        top = a.most_common(1)
        return top[0][0]
    
    X_left, X_right, y_left, y_right = partition_classes(X, y, split_attribute, split_value)
    
    return {(split_attribute, split_value):[recursive(X_left, y_left), recursive(X_right, y_right)]}

def classfication(tree, record):
    split_attr, split_val = tree.keys()[0]
    x = record[split_attr]
    if str(x).isdigit():
        if record[split_attr] <= split_val:
            label = 0  
        else:
            label = 1
    else:
        if record[split_attr] == split_val:
            label = 0  
        else:
            label = 1
            
    if isinstance(tree.values()[0][label], dict):
        return classfication(tree.values()[0][label], record)
    else:
        return tree.values()[0][label]

class DecisionTree(object):
    def __init__(self):
        # Initializing the tree as an empty dictionary or list, as preferred
        #self.tree = []
        self.tree = {}
        pass
        
    
    def learn(self, X, y):
        # TODO: Train the decision tree (self.tree) using the the sample X and labels y
        # You will have to make use of the functions in utils.py to train the tree
        
        # One possible way of implementing the tree:
        #    Each node in self.tree could be in the form of a dictionary:
        #       https://docs.python.org/2/library/stdtypes.html#mapping-types-dict
        #    For example, a non-leaf node with two children can have a 'left' key and  a 
        #    'right' key. You can add more keys which might help in classification
        #    (eg. split attribute and split value) 
        self.tree = recursive(X,y)
        pass


    def classify(self, record):
        # TODO: classify the record using self.tree and return the predicted label
        return classfication(self.tree, record)
        pass
