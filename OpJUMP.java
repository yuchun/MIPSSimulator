
public class OpJUMP extends Instructions{
	public OpJUMP(int opcode, String name, int word, int addr)
	{
		super(word, addr, Utils.InstType.InstType_BRANCH);
		opName = name;
    	offset = (word >> Utils.OFFSET_OFFSET) & Utils.OFFSET_MASK;
	}
	
	public String getOpName()
	{
		return opName;
	}
	
	public int getOffset()
	{
		return offset;
	}
	
	public int getTargetAddr(int pc)
	{
		return getOffset()<<2;
	}
	
	public String toString()
	{
    	return super.toString() + getAddr() + " " + toString2();
	}
	
	public String toString2()
	{
    	return getOpName() + " #" + (getOffset()<<2);
	}
	
	public int compute(int src1, int src2)
	{
		return 1;
	}
	private String opName;
	private int offset;
}
