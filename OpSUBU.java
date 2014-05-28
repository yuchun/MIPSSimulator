public class OpSUBU extends SpecialType{
	public OpSUBU(int word, int addr)
	{
		super(word, addr, Utils.InstType.InstType_ALU);
		setOpName("SUBU");
	}
	
	public String toString2()
	{
		return getOpName() + " " + "R" + getRd() + ", R" + getRs() + ", R" + getRt();
	}	
	public int getSource1(){return getRs();}
	public int getSource2(){return getRt();}
	public int getDest(){return getRd();}
	public int compute(int src1, int src2)
	{
		return src1 - src2;
	}
}
