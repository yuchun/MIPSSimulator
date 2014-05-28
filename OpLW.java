
public class OpLW extends IType{
	public OpLW(int opcode, String name, int word, int addr)
	{
		super(opcode, name, word, addr, Utils.InstType.InstType_LD);
	}
	public String toString2()
	{
		return getOpName() + " " + "R" + getRt() + ", " + getImm() + "(R" + getRs() + ")";
	}
	public int getSource1(){return getRs();}
	public int getSource2(){return -1;}
	public int getDest(){return getRt();}
	public int compute(int src1, int src2)
	{
		return src1 + getImm();
	}
}
