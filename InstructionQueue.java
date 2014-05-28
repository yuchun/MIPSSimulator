import java.util.*;

public class InstructionQueue {

	public InstructionQueue()
	{
		queue = new LinkedList<Instructions>();
	}
	
	public void enQueue(Instructions inst)
	{
		queue.offer(inst);
	}
	
	public Instructions deQueue()
	{
		return queue.poll();
	}
	
	public int size()
	{
		return queue.size();
	}
	
	public Instructions getFirst()
	{
		if(!queue.isEmpty())
			return queue.getFirst();
		return null;
	}
	
	public String toString()
	{
		String str = "IQ:\n";
		int size = queue.size();
		for(int i = 0; i < size; i++){
			str += "[" + queue.get(i).toString2() + "] ";
			str += "\n";
		}
		return str;
	}
	
	private LinkedList<Instructions> queue;
}
