import java.util.*;

public class ReservationStation {

	public ReservationStation()
	{
		array = new ArrayList<RSElem>();	
	}
	
	public boolean available()
	{
		return !(array.size() >= maxsize);
	}

	
	public RSElem getAvailable()
	{
		if(available()){
			RSElem rse = new RSElem();
			array.add(rse);
			return rse;
		}
		return null;
	}
	
	public RSElem remove(int index)
	{
		return array.remove(index);
	}
	
	/*
	 * @return the index of the match instruction, -1 means not found
	 * */
	public int find(Instructions inst)
	{
		int index = -1;
		//How to identify the instruction?
		return index;
	}
	
	public void updateValue()
	{
		
	}
	
	public String toString()
	{
		String st = "RS:\n";
		for(RSElem e:array){
			st += "[" + e.getInst().toString2() + "] \n";
		}
		return st;
				
	}
	public int size()
	{
		return array.size();
	}
	
	public boolean dependency(int i, int cycle)
	{
		RSElem e = array.get(i);
		if(e.getInst().getInstType() == Utils.InstType.InstType_ST){
			if(e.getQj(cycle, false) != null)
				return true;
		}else if(e.getQj(cycle, false) != null || e.getQk(cycle, false) != null)
			return true;
		return false;
	}
	
	public int compute(int i, int cycle)
	{
		RSElem e = array.get(i);
		int dst = e.getInst().compute(e.getVj(cycle, false), e.getVk(cycle, false));
		if(e.getCompute() == -1)
			e.setCompute(dst);
//		if(e.getDest() != -1)
//			e.setDest(dst);
		return dst;
	}
	
	public RSElem getRSElem(int i)
	{
		return array.get(i);
	}
	
	private static final int maxsize = 8;
	private ArrayList<RSElem> array;
	public class RSElem{
		public RSElem()
		{
		}
		public String getName(){return name;}
		public void setName(String n){name = n;}
		public boolean getBusy(){return busy;}
		public void setBusy(boolean b){busy = b;}
		public String getOp(){return Op;}
		public void setOp(String q){Op = q;};
		public int getVj(int cycle, boolean forcenew){return Vj.getval(cycle, forcenew);}
		public void setVj(int v, int cycle){Vj.setval(new Integer(v), cycle);}
		public int getVk(int cycle, boolean forcenew){return Vk.getval(cycle, forcenew);}
		public void setVk(int v, int cycle){Vk.setval(new Integer(v), cycle);};
		public ReorderBuffer.ReorderBufferElement getQj(int cycle, boolean forcenew){return Qj.getval(cycle, forcenew);}
		public void setQj(ReorderBuffer.ReorderBufferElement q, int cycle){Qj.setval(q, cycle);}
		public ReorderBuffer.ReorderBufferElement getQk(int cycle, boolean forcenew){return Qk.getval(cycle, forcenew);}
		public void setQk(ReorderBuffer.ReorderBufferElement q, int cycle){Qk.setval(q, cycle);}
		public int getA(){return A;}
		public void setA(int a){A = a;};
		public ReorderBuffer.ReorderBufferElement getDest(int cycle, boolean forcenew)
		{
			return Dest.getval(cycle, forcenew);
		}
		public void setDest(ReorderBuffer.ReorderBufferElement d, int cycle){Dest.setval(d, cycle);}
		public Instructions getInst(){return inst;}
		public void setInst(Instructions i){inst = i;}
		
		public int getCompute(){return compute;}
		public void setCompute(int com){compute = com;}
		
		public int getLoadVal(){return loadVal;}
		public void setLoadVal(int val){loadVal = val;}
		
		public int getUpdateCycle(){return updateCycle;}
		public void setUpdateCycle(int u){updateCycle = u;}
		
		private String name;
		private boolean busy;
		private String Op;
		private Instructions inst;


		private int A;
		private int compute = -1;
		private int loadVal = -1;
		private int updateCycle = 0;
		
		private RSEValue<Integer> Vj = new RSEValue<Integer>(0, 0);
		private RSEValue<Integer> Vk = new RSEValue<Integer>(0, 0);
		private RSEValue<ReorderBuffer.ReorderBufferElement> Qj =
					new RSEValue<ReorderBuffer.ReorderBufferElement>(null, 0);
		private RSEValue<ReorderBuffer.ReorderBufferElement> Qk =
				new RSEValue<ReorderBuffer.ReorderBufferElement>(null, 0);
		private RSEValue<ReorderBuffer.ReorderBufferElement> Dest =
				new RSEValue<ReorderBuffer.ReorderBufferElement>(null, 0); //# of ROB
		public class RSEValue<T>{
			public RSEValue(T val, int cycle){
				oldval = val;
				newval = val;
				updateCycle = cycle;
			}
			public T getval(int cycle, boolean forcenew){
				if(forcenew || cycle > updateCycle)
					return newval;
				else
					return oldval;
			}

			public void setval(T val, int cycle){
				oldval = newval;
				newval = val;
				updateCycle = cycle;
			}
			private T oldval;
			private T newval;
			private int updateCycle;
		};
	}
}
