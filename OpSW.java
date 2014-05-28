
public class OpSW extends IType{
	public OpSW(int opcode, String name, int word, int addr)
	{
		super(opcode, name, word, addr, Utils.InstType.InstType_ST);
	}
	
	public String toString2()
	{
		return getOpName() + " " + "R" + getRt() + ", " + getImm() + "(R" + getRs() + ")";
	}
	public int getSource1(){return getRs();}
	public int getSource2(){return getRt();}
	public int getDest(){return -1;}
	public int compute(int src1, int src2)
	{
		//System.out.println("SW: src1="+src1+" src2="+src2);
		return src1 + getImm();
	}
}