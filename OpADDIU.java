public class OpADDIU extends IType{
	public OpADDIU(int opcode, String name, int word, int addr)
	{
		super(opcode, name, word, addr, Utils.InstType.InstType_ALU);
	}
	
	public String toString2(){
		return getOpName() + " " + "R" + getRt() + ", R" + getRs() + ", #" + getImm();
	}
	public int getSource1(){return getRs();}
	public int getSource2(){return -1;}
	public int getDest(){return getRt();}
	public int compute(int src1, int src2)
	{
		return src1 + getImm();
	}
}