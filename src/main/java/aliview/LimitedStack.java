package aliview;

import java.util.Stack;

import org.apache.log4j.Logger;

public class LimitedStack<T> {
	private static final Logger logger = Logger.getLogger(LimitedStack.class);
	Stack<T> stack;
	int maxSize;
	int pointer = -1;
	
	public LimitedStack(int maxSize){
		stack = new Stack<T>();
		this.maxSize = maxSize;
	}
	
	public T push(T obj){
		
		// remove everything between this and pointer
//		logger.info("pointer" + pointer);
//		logger.info("(stack.size() -1)" + (stack.size() -1));
		
		int diff = Math.abs(pointer - (stack.size()-1));
		
		T retVal = stack.push(obj);
		if(stack.size() > maxSize){
			stack.removeElementAt(0);
		}
		pointer = stack.size() - 1;
		return retVal;
	}
	
	public T pop(){
		T popObj = stack.pop();
		pointer = stack.size() - 1;
		return popObj;
	}
	
	public boolean isEmpty(){
		return stack.isEmpty();
	}
	
	public int size(){
		return stack.size();
	}
	
	private boolean rangeCheck(int index){
		if(index > -1 && index <= stack.size()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean hasPrevious(){
		return rangeCheck(pointer-1);
	}
	
	public boolean hasNext(){
		return rangeCheck(pointer+1);
	}
	
	public T getPreviousState() {
		pointer = pointer -1;
		return stack.elementAt(pointer);
	}
	
	public T getNextState() {
		pointer = pointer +1;
		return stack.elementAt(pointer);
	}

	public void clear() {
		stack.clear();
	}

}
