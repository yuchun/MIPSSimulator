
public class SpecialType extends Instructions{
	public SpecialType(int word, int addr, Utils.InstType type)
	{
		super(word, addr, type);

    	funct = (word >> Utils.FUNCT_OFFSET) & Utils.FUNCT_MASK;
    	shamt = (word >> Utils.SHAMT_OFFSET) & Utils.SHAMT_MASK;
    	rd = (word >> Utils.RD_OFFSET) & Utils.RD_MASK;
    	rt = (word >> Utils.RT_OFFSET) & Utils.RT_MASK;
    	rs = (word >> Utils.RS_OFFSET) & Utils.RS_MASK;	
	}
	
	public String getOpName()
	{
		return opName;
	}
	
	public void setOpName(String s)
	{
		opName = s;
	}
	
	public int getRt()
	{
		return rt;
	}
	
	public int getRs()
	{
		return rs;
	}
	
	public int getFunct()
	{
		return funct;
	}
	
	public int getShamt()
	{
		return shamt;
	}
	
	public int getRd()
	{
		return rd;
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
	private int funct;
	private int shamt;
	private int rd;

}
