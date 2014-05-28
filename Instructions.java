import java.math.BigInteger;


public class Instructions {
	Instructions(int word, int addr, Utils.InstType type)
	{
		bytes = word;
		address = addr;
		instType = type;
	}
	public String toString()
	{
    	int opcode, rs, rt, rd, shamt, funct;
    	String retStr="";
    	opcode = (bytes >> Utils.OPCODE_OFFSET) & Utils.OPCODE_MASK;
    	rt = (bytes >> Utils.RT_OFFSET) & Utils.RT_MASK;
    	rs = (bytes >> Utils.RS_OFFSET) & Utils.RS_MASK;
    	rd = (bytes >> Utils.RD_OFFSET) & Utils.RD_MASK;
    	shamt = (bytes >> Utils.SHAMT_OFFSET) & Utils.SHAMT_MASK;
    	funct = (bytes >> Utils.FUNCT_OFFSET) & Utils.FUNCT_MASK;
    	retStr = String.format("%06d ", new BigInteger(Integer.toBinaryString(opcode)));
    	retStr += String.format("%05d ", new BigInteger(Integer.toBinaryString(rs)));
    	retStr += String.format("%05d ", new BigInteger(Integer.toBinaryString(rt)));
    	retStr += String.format("%05d ", new BigInteger(Integer.toBinaryString(rd)));
    	retStr += String.format("%05d ", new BigInteger(Integer.toBinaryString(shamt)));
    	retStr += String.format("%06d ", new BigInteger(Integer.toBinaryString(funct)));
    	return retStr;
	}
	public String toString2()
	{
		return "";
	}
	public int getAddr()
	{
		return address;
	}
	public void setInstType(Utils.InstType type)
	{
		instType = type;
	}
	public Utils.InstType getInstType()
	{
		return instType;
	}
	public String getOpName()
	{
		return "Default";
	}
	public int compute(int src1, int src2){return -1;}
	
	public int getRt(){return -1;}
	public void setRt(int r){}
	public int getRs(){return -1;}
	public void setRs(int r){}
	public int getImm(){return -1;}
	public int getFunct(){return -1;}
	public int getShamt(){return -1;}
	public int getRd(){return -1;}
	public void setRd(int r){}
	public int getSource1(){return -1;}
	public void setSource1(int s){}
	public int getSource2(){return -1;}
	public void setSource2(int s){}
	public int getDest(){return -1;}
	public void setDest(int d){}
	public int getPredicted(){return predicted;}
	public void setPredicted(int p){predicted = p;}
	public int getTargetAddr(int pc){return taddr;}
	
	private int bytes;
	private int address;
	private Utils.InstType instType;
	private int predicted = -1;	//for branch instruction, whether it is predicted right or wrong,
							//1 for right, 0 for wrong
	private int taddr=-1;//for branch instruction, the computed target address.
}
