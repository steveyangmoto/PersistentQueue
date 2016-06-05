//MIT License
//
// Copyright (c) [2015] [Steve Yang]
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by SteveYang on 3/11/15.
 * a crash-resistent, and globally accessible queue for Android SDK, that holds String objects
 */
public class PersistentQueue {
    private static final String FILE_NAME = "PersistentQueue";
    private static PersistentQueue persistentQueue = null;
    private ArrayList<String> myQueue = null;
    
    private PersistentQueue() {
        myQueue = loadQueueData(FILE_NAME);
        if (myQueue == null) {
            myQueue = new ArrayList<String>();
        }
    }
    
    public static PersistentQueue getInstance() {
        if (persistentQueue == null) {
            persistentQueue = new PersistentQueue();
        }
        return persistentQueue;
    }
    
    /**
     * Adds an element to the head of the queue
     *
     * @param obj
     */
    public void enqueue(String obj) {
        synchronized (this) {
            myQueue.add(obj);
            persistQueueData(myQueue, FILE_NAME);
        }
    }
    
    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty
     */
    public String dequeue() {
        synchronized (this) {
            if (myQueue.size() > 0) {
                String element = myQueue.get(0);
                myQueue.remove(0);
                persistQueueData(myQueue, FILE_NAME);
                return element;
            }
        }
        return null;
    }
    
    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty
     */
    public String peek() {
        if (myQueue.size() > 0) {
            return myQueue.get(0);
        }
        return null;
    }
    
    /**
     * Retrieves, but does not remove, the element of this queue at the specified index,
     * or returns null if either this queue is empty or the index is out of bounds.
     *
     * @param index
     * @return the element of this queue at the specified index, or null if this queue is empty
     */
    public String peekAt(int index) {
        if (index >= 0 && index < myQueue.size()) {
            return myQueue.get(index);
        }
        return null;
    }
    
    /**
     * Inserts the specified object into the queue at the specified location.
     * The object is inserted before any previous element at the specified location.
     * If the location is equal to the size of this ArrayList, the object is added at the end.
     *
     * @param index
     * @param obj
     * @return true if the index to insert at in the queue is valid,
     * false if the index is negative or greater than the size of the queue
     */
    public boolean insertAt(int index, String obj) {
        synchronized (this) {
            if (index >= 0 && index <= myQueue.size()) {
                myQueue.add(index, obj);
                persistQueueData(myQueue, FILE_NAME);
                return true;
                
            }
        }
        return false;
    }
    
    /**
     * Removes the object at the specified location
     *
     * @param index
     * @return true if the index is valid in the queue
     * false if the index is negative or greater than the size of the queue
     */
    public boolean removeAt(int index) {
        synchronized (this) {
            if (index >= 0 && index < myQueue.size()) {
                myQueue.remove(index);
                persistQueueData(myQueue, FILE_NAME);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Retrieves the size of the queue
     *
     * @return size of the queue
     */
    public int getSize() {
        return myQueue.size();
    }
    
    private void persistQueueData(ArrayList<String> queue, String filePath) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)));
            oos.writeObject(queue);
            oos.close();
        } catch (IOException ioe) {
            return;
        }
    }
    
    private ArrayList<String> loadQueueData(String filePath) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filePath)));
            ArrayList<String> queue = (ArrayList<String>) ois.readObject();
            ois.close();
            return queue;
        } catch (Exception ex) {
            return null;
        }
    }
}
