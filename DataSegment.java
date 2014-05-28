import java.util.ArrayList;


public class DataSegment {

	public DataSegment()
	{
		dse = new ArrayList<DataSegmentElement>();
		for(int i = 0; i < size; i+=4)
			dse.add(new DataSegmentElement(baseAddr + i, 0, 0));
	}
	
	public String toString(int cycle)
	{
		String st = "Data Segment:\n";
		st += baseAddr + ":";
		for(DataSegmentElement d:dse){
			int val = d.getValue(cycle);
			/*
			st += ((val & 0xff000000)>>24) + "\t";
			st += ((val&0xff0000)>>16) + "\t";
			st += ((val&0xff00)>>8) + "\t";
			st += (val&0xff) + "\t";
			*/
			st += "\t" + val;
		}
		st += "\n";
		return st;
	}
	
	public int getData(int index, int cycle)
	{
		//System.out.println("getData: index-"+ index);
		return dse.get((index - baseAddr)/4).getValue(cycle);
	}
	
	public void setData(int index, int value, int cycle)
	{
		//System.out.println("index="+index+",value="+value);
		dse.get((index - baseAddr)/4).setValue(value,cycle);
	}
	
	private static final int size = 40;
	private static final int baseAddr = Utils.startDataLine;
	
	private ArrayList<DataSegmentElement> dse;
	
	public class DataSegmentElement{
		public DataSegmentElement(int a, int v, int cycle)
		{
			addr = a;
			oldValue = newValue = v;
			updateCycle = 0;
		}

		public int getAddr(){return addr;}
		public void setAddr(int a){addr = a;}
		public int getValue(int cycle){
			if(cycle > (updateCycle + 1))
				return newValue;
			else
				return oldValue;
		}
		public void setValue(int v, int cycle){
			oldValue = newValue;
			newValue = v;
			updateCycle = cycle;
		}
		public int getUpdateCycle(){return updateCycle;}
		public void setUpdateCycle(int u){updateCycle = u;}
		int addr;
		int newValue;
		int oldValue;
		int updateCycle = -1;
	}
}
