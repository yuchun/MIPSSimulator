
public class OpNOP extends SpecialType{
	public OpNOP(int word, int addr)
	{
		super(word, addr, Utils.InstType.InstType_ALU);
		setOpName("NOP");
	}
	
	public String toString2()
	{
		return getOpName();
	}
	public int compute(int src1, int src2)
	{
		return 1;
	}
}
