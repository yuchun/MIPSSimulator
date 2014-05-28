import java.util.*;

public class BranchTargetBuffer {
	public BranchTargetBuffer()
	{
		btbs = new BtbSet[sets];
		for(int i = 0; i < sets; i++)
			btbs[i] = new BtbSet(way);
	}
	
	public Utils.PredictType predict(int addr, int cycle, boolean forcenew)
	{
		//Utils.PredictType pre = Utils.PredictType.Predict_NOTSET;
		int offset = (addr/sets)%sets;
		
		return btbs[offset].predict(addr, cycle, forcenew);
	}
	
	public boolean insert(int addr, int taddr, Utils.PredictType pre, int cycle)
	{
		int offset = (addr/sets)%sets;
		return btbs[offset].insert(addr, taddr, pre, cycle);
	}
	
	public boolean updateValue(int addr, int taddr, Utils.PredictType pre, int cycle)
	{
		int offset = (addr/sets)%sets; 
		return btbs[offset].updateValue(addr, taddr, pre, cycle);
	}
	
	public int getTargetAddr(int addr)
	{
		int offset = (addr/sets)%sets;
		return btbs[offset].getTargetAddr(addr);
	}

	public String toString(int cycle)
	{
		String st = "BTB:\n";
		//int j = 0;
		for(int i = 0; i < btbs.length; i++){
			for(int k = 0; k < btbs[i].getElem().size(); k++){
				int addr = btbs[i].getElem().get(k).getInstAddr();
				int taddr = btbs[i].getElem().get(k).getTargetAddr();
				Utils.PredictType pre = btbs[i].getElem().get(k).getPredict(cycle);
				st += "[Set " + (i) + "]:<" + addr + "," + taddr + ",";
				if(pre == Utils.PredictType.Predict_NOTSET)
					st += "NotSet>\n";
				else if (pre == Utils.PredictType.Predict_FALSE)
					st += "0>\n";
				else
					st += "1>\n";
			}
		}
		return st;
	}


	private final int size = 16;
	private final int way = 4;
	private final int sets = size/way;
	
	private BtbSet[] btbs;
	
	public class BtbSet
	{
		public BtbSet(int size)
		{
			elems = new ArrayList<BtbElem>();
			setSize = size;  
		}
		
		public Utils.PredictType predict(int addr, int cycle, boolean forcenew)
		{
			Utils.PredictType pre = Utils.PredictType.Predict_NOTSET;
			BtbElem be;
			for(int i = 0; i < elems.size(); i++){
				if(addr == elems.get(i).getInstAddr()){
					if(forcenew)
						pre = elems.get(i).getNewPredict();
					else
						pre = elems.get(i).getPredict(cycle);
					/*the purpose of the following two codes is to move the visited branch to the end, so as to LRU*/
					if(i < elems.size() - 1){
						be = elems.remove(i);
						elems.add(be);
					}
				}
			}
			return pre;
		}
		
		public boolean insert(int addr, int taddr, Utils.PredictType pre, int cycle)
		{
			for(int i = 0; i < elems.size(); i++){
				if(addr == elems.get(i).getInstAddr()){
					elems.get(i).setTargetAddr(taddr);
					elems.get(i).setPredict(pre, cycle);
					return true;
				}
			}
			BtbElem be = new BtbElem(addr, taddr, pre);
			if(elems.size() == setSize) //already full
				elems.remove(0); //remove the first element, due to LRU
			return elems.add(be);
		}
		
		public boolean updateValue(int addr, int taddr, Utils.PredictType pre, int cycle)
		{
			BtbElem be;
			for(int i = 0; i < elems.size(); i++){
				if(addr == elems.get(i).getInstAddr()){	//found
					/*update the value of the visited branch and move it to the end, so as to LRU*/
					elems.get(i).setTargetAddr(taddr);
					elems.get(i).setPredict(pre, cycle);
					if(i < elems.size() - 1){
						be = elems.remove(i);
						return elems.add(be);
					}else
						return true;
				}
			}
			return false;
		}
		
		public int getTargetAddr(int addr)
		{
			int tad = -1;
			BtbElem be;
			for(int i = 0; i < elems.size(); i++){
				if(addr == elems.get(i).getInstAddr()){	//found
					tad = elems.get(i).getTargetAddr();
					if(i < elems.size() - 1){
						be = elems.remove(i);
						elems.add(be);
					}
				}
			}
			return tad;
		}
		
		public ArrayList<BtbElem> getElem()
		{
			return elems;
		}
		
		
		private int setSize; 
		private ArrayList<BtbElem> elems;
	}
	
	public class BtbElem implements Cloneable{
		public BtbElem(int addr, int taddr, Utils.PredictType pred)
		{
			instAddr = addr;
			targetAddr = taddr;
			oldpredict = newpredict = pred;
			updateCycle = 0;
		}
		public BtbElem()
		{
			this(0, 0, Utils.PredictType.Predict_NOTSET);
		}
		public BtbElem clone ()
		{
			try {
				return (BtbElem)super.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		public int getInstAddr()
		{
			return instAddr;
		}
		public void setInstAddr(int addr)
		{
			instAddr = addr;
		}
		public void setTargetAddr(int addr)
		{
			targetAddr = addr;
		}
		public int getTargetAddr()
		{
			return targetAddr;
		}
		public Utils.PredictType getPredict(int cycle)
		{
			if(cycle > updateCycle)
				return newpredict;
			else
				return oldpredict;
		}
		public Utils.PredictType getNewPredict()
		{
			return newpredict;
		}
		public void setPredict(Utils.PredictType pred, int cycle)
		{
			oldpredict = newpredict;
			newpredict = pred;
			updateCycle = cycle;
		}
		int instAddr = 0;
		int targetAddr = 0;
		Utils.PredictType oldpredict = Utils.PredictType.Predict_NOTSET;	
		Utils.PredictType newpredict = Utils.PredictType.Predict_NOTSET;	
		int updateCycle = -1;
		Instructions instruction;
	};

}
