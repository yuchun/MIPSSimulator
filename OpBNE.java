
public class OpBNE extends IType{
	public OpBNE(int opcode, String name, int word, int addr)
	{
		super(opcode, name, word, addr, Utils.InstType.InstType_BRANCH);
	}
	
	public String toString2()
	{
    		return getOpName() + " " + "R" + getRs() + ", " + "R" + getRt() + ", " + "#" + (getImm()<<2);
    }
	public int getSource1(){return getRs();}
	public int getSource2(){return getRt();}
	public int getDest(){return -1;}
	public int compute(int src1, int src2)
	{
		if(src1 != src2)
			return 1;
		else
			return 0;
	}
	public int getTargetAddr(int pc)
	{
		int offset =  getImm()<<2;
		return pc + offset;
	}
}
