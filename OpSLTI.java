
public class OpSLTI extends IType{
	public OpSLTI(int opcode, String name, int word, int addr)
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
		if(src1 < getImm())
			return 1;
		else
			return 0;
	}
}
