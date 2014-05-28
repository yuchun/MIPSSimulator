public class IType extends Instructions{
	public IType(int opcode, String name, int word, int addr, Utils.InstType type)
	{
		super(word, addr, type);
		opName = name;
    	rt = (short) ((word >> Utils.RT_OFFSET) & Utils.RT_MASK);
    	rs = (short) ((word >> Utils.RS_OFFSET) & Utils.RS_MASK);
    	imm = (short) ((word >> Utils.IMMEDIATE_OFFSET) & Utils.IMMEDIATE_MASK);
	}
	
	public String getOpName()
	{
		return opName;
	}
	
	public int getRt()
	{
		return rt;
	}
	
	public int getRs()
	{
		return rs;
	}
	
	public int getImm()
	{
		return imm;
	}
	
	public String toString()
	{
    	return super.toString() + getAddr() + " " + toString2();
	}
	
	public String toString2()
	{
		return "";
	}
	
	private String opName;
	private int rt;
	private int rs;
	private int imm;
}
