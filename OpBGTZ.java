
public class OpBGTZ extends IType{
	public OpBGTZ(int opcode, String name, int word, int addr)
	{
		super(opcode, name, word, addr, Utils.InstType.InstType_BRANCH);
	}
	
	public String toString2()
	{
    	return getOpName() + " " + "R" + getRs() + ", " + "#" + (getImm()<<2);
    }
	public int getSource1(){return getRs();}
	public int getSource2(){return -1;}
	public int getDest(){return -1;}
	public int compute(int src1, int src2)
	{
		if(src1 > 0)
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
