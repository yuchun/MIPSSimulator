
public class OpBREAK extends SpecialType{
	public OpBREAK(int word, int addr)
	{
		super(word, addr,Utils.InstType.InstType_ALU);
		setOpName("BREAK");
	}
	
	public String toString2()
	{
		return getOpName();
	}
	
}
