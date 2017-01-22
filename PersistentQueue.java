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

import android.content.Context;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SteveYang on 3/11/15.
 * a crash-resistent, and globally accessible queue for Android SDK, that holds any serializable objects
 */
public class PersistentQueue<T extends Serializable> {
    private static final String TAG = "PersistentQueue";
    private ArrayList<T> myQueue = null;
    private Context context;
    private String fileName;

    public PersistentQueue(Context context,String fileName) {
        this.context = context;
        this.fileName = fileName;
        myQueue = loadQueueData(fileName);
        if (myQueue == null) {
            myQueue = new ArrayList<T>();
        }
    }

    /**
     * Adds an element to the head of the queue
     *
     * @param obj
     */
    public void enqueue(T obj) {
        synchronized (this) {
            myQueue.add(obj);
            persistQueueData(myQueue, fileName);
        }
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty
     */
    public T dequeue() {
        synchronized (this) {
            if (myQueue.size() > 0) {
                T element = myQueue.get(0);
                myQueue.remove(0);
                persistQueueData(myQueue, fileName);
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
    public T peek() {
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
    public T peekAt(int index) {
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
    public boolean insertAt(int index, T obj) {
        synchronized (this) {
            if (index >= 0 && index <= myQueue.size()) {
                myQueue.add(index, obj);
                persistQueueData(myQueue, fileName);
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
                persistQueueData(myQueue, fileName);
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

    private void persistQueueData(ArrayList<T> queue, String filePath) {
        ObjectOutputStream oos = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filePath, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(new BufferedOutputStream(fileOutputStream));
            oos.writeObject(queue);
        } catch (IOException ioe) {
            Log.e(TAG, "exception: " + ioe.getMessage());
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception ignore) {
            }
        }
    }

    private ArrayList<T> loadQueueData(String filePath) {
        ObjectInputStream ois = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(filePath);
            ois = new ObjectInputStream(new BufferedInputStream(fileInputStream));
            ArrayList<T> queue = (ArrayList<T>) ois.readObject();
            ois.close();
            return queue;
        } catch (Exception ex) {
            Log.e(TAG, "exception: " + ex.getMessage());
            return null;
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception ignore) {
            }
        }
    }
}
