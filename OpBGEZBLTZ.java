
public class OpBGEZBLTZ extends Instructions{
	public OpBGEZBLTZ(int word, int addr)
	{
		super(word, addr, Utils.InstType.InstType_BRANCH);
    	
    	short subopcode = (short) ((word >> Utils.RT_OFFSET) & Utils.RT_MASK);
    	rs = (word >> Utils.RS_OFFSET) & Utils.RS_MASK;
    	offset = (word >> Utils.IMMEDIATE_OFFSET) & Utils.IMMEDIATE_MASK;
    	switch(subopcode){
    	case Utils.SUBOPCODE_BGEZ:
    		opName = "BGEZ";
    		break;
    	case Utils.SUBOPCODE_BLTZ:
    		opName = "BLTZ";
    		break;
    	default:
    		opName = "";
    	}
	}
	
	public String getOpName()
	{
		return opName;
	}
	
	public int getOffset()
	{
		return offset;
	}
	
	public int getRs()
	{
		return rs;
	}

	public int getTargetAddr(int pc)
	{
		int offset =  getOffset()<<2;
		return pc + offset;
	}
	
	public String toString()
	{
    	return super.toString() + getAddr() + " " + toString2();
	}
	
	public String toString2()
	{
		return getOpName() + " R" + getRs() + ", #" + getOffset();
	}
	public int getSource1(){return getRs();}
	public int getSource2(){return -1;}
	public int getDest(){return -1;}
	
	public int compute(int src1, int src2)
	{
		if(opName.equals("BGEZ")){
			if(src1 >= 0)
				return 1;
			else
				return 0;
		}else{
			if(src1 < 0)
				return 1;
			else
				return 0;
		}
	}
	
	private String opName;
	private int offset;
	private int rs;
}
