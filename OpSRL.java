public class OpSRL extends SpecialType{
	public OpSRL(int word, int addr)
	{
		super(word, addr, Utils.InstType.InstType_ALU);
		setOpName("SRL");
	}
	
	public String toString2()
	{
		return getOpName() + " " + "R" + getRd() + ", R" + getRt() + ", #" + getShamt();
	}
	public int getSource1(){return -1;}
	public int getSource2(){return getRt();}
	public int getDest(){return getRd();}
	public int compute(int src1, int src2)
	{
		return src2 >> getShamt();
	}
}
