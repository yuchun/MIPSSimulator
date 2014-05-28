import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

public class MIPSsim {
    public static void main(String[] args)
    {
	   		System.out.println("MIPS simulation/disassembly test"); 
            MIPSsim sim = new MIPSsim();
            if(false == sim.parseOptions(args)){
                    sim.printHelp();
                    return;
            }
            if(false == sim.openFile())
            	return;
            try{
            	if(sim.operation.equals("dis")){
					System.out.println("test disassembly");
            		sim.disassembly();
				}else if(sim.operation.equals("sim")){
            		System.out.println("test simulation");
					sim.simulation();
				}
            }catch(IOException e){
            	System.out.println("ft, unexpected situation");	
            }
            sim.closeFile();
            System.out.println("Bye MIPSsim");
    }


    public MIPSsim(){
    	preQueue = new ArrayList<Instructions>();
    	iq = new InstructionQueue();
    	btBuf = new BranchTargetBuffer();
    	rf = new RegisterFile();
    	rstat = new RegisterStat();
    	rob = new ReorderBuffer();
    	rs = new ReservationStation();
    	ds = new DataSegment();
    	
    	currentLine = Utils.startCodeLine;
    	oldpc = newpc = Utils.startCodeLine;
    }
    
    public void printHelp()
    {
            System.out.println("MIPSsim imputFilename outputFilename [sim|dis] [-Tm:n]");
    }

    public boolean parseOptions(String[] args)
    {
            int size = args.length;
            if(size == 3 || size == 4){
                    inputFilename = args[0];
                    outputFilename = args[1];
                    operation = args[2];
                    if(!(operation.equals("dis") || operation.equals("sim")))
                    	return false;
                    if(size == 4){
                    	if(args[3].substring(0, new String("-T").length()).equals("-T")){
                    		try{
                    			startCycle = Integer.parseInt(args[3].substring(new String("-T").length(), args[3].indexOf(":")));
                    			endCycle = Integer.parseInt(args[3].substring(args[3].indexOf(":")+1));
                    		}catch(NumberFormatException e){
                    			return false;
                    		}
                    		return true;
                    	}else
                    		return false;
                    }
                    return true;
            }else
                    return false;
    };
    
    
    public boolean openFile()
    {
    	try{
    		inputFile = new FileInputStream(System.getProperty("user.dir")+"/"+inputFilename);
    		inputStream = new DataInputStream(inputFile);
    	}catch(FileNotFoundException e){
    		e.printStackTrace();
    		return false;
    	}
    	try{
    		outputWriter = new PrintWriter(outputFilename);
    	}catch(FileNotFoundException e){
    		
    		e.printStackTrace();
    		return false;
    	}
    
    	return true;
    }
    
    public boolean closeFile()
    {
    	outputWriter.close();
    	try {
    		inputStream.close();
			inputFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return true;
    }
    
    public void disassembly() throws IOException
    {
    	int readWord;

    	preReadInstruction();
    	for( Instructions inst : preQueue){
    		outputWriter.println(inst.toString());
    	}
    	while(true){
    		try{
    			readWord = inputStream.readInt();
    		}catch(EOFException e){
    			return;
    		}
    		String outWord = parseData(readWord);
    		outputWriter.println(outWord);
    		currentLine+=4;
    	}
    	
    }
    
    public void simulation() throws IOException
    {
    	preReadInstruction();
    	boolean verbose = false;
    	/*
    	for( Instructions inst : preQueue){
    		System.out.println(inst.toString2());
    	}
    	*/
    	do{
    		cycle++;
    		if(verbose == false && startCycle <= cycle)
    			verbose = true;
    		if(verbose == true && endCycle > 0 && endCycle < cycle)
    			verbose = false;
    		if(verbose)outputWriter./*System.out.*/println("Cycle " + "<" + cycle + ">:");
    		cleaning();
    		
    		InstructionCommit();
    		InstructionWriteback();
 
    		InstructionExecute();
    		InstructionIssue();
    		InstructionFetch();
	
    		if(verbose)outputWriter./*System.out.*/print(iq.toString());
    		if(verbose)outputWriter./*System.out.*/print(rs.toString());
    		if(verbose)outputWriter./*System.out.*/print(rob.toString());
    		if(verbose)outputWriter./*System.out.*/print(btBuf.toString(cycle));
    		if(verbose)outputWriter./*System.out.*/print(rf.toString(cycle));
    		if(verbose)outputWriter./*System.out.*/print(ds.toString(cycle));  
    		
			/*
			 * Since the update occurs at the end of a cycle, I add this function after all the pipeline functions to avoid the update of one stage 
			 * inteferes its previous stage. The purpose of cleaning function is the same. 
			 * 
			 * */
    		InstructionExecute2();
    		if(rs.size() == 0 && rob.getSize() == 0 && cycle > 1)
    			stop = true;
    	}while(!stop);
    	
    }
    	
    private void preReadInstruction() throws IOException
    {
    	int readWord;
    	Instructions inst = null;
    	
    	while(true){
    		try{
    			readWord = inputStream.readInt();
    		}catch(EOFException e){
    			return;
    		}
    		inst = parseInstruction(readWord);
    		if(inst == null)
    			break;
    		preQueue.add(inst);
    		currentLine+=4;
    		if(inst instanceof OpBREAK){	//code section end
    			break;
    		}
    	}   
    }
    
    private String parseData(int word)
    {
    	String st = String.format("%032d ", new BigInteger(Integer.toBinaryString(word)));
    	st += currentLine + " " + String.format("%d", word);
    	return st;
    }
    
    private Instructions parseInstruction(int word)
    {
    	int opcode;
    	opcode = (word >> Utils.OPCODE_OFFSET) & Utils.OPCODE_MASK;
    	Instructions inst = null;
    	switch(opcode){
    	case Utils.OPCODE_ADDI:
    		inst = new OpADDI(opcode, "ADDI", word, currentLine);
    		break;
    	case Utils.OPCODE_ADDIU:
    		inst = new OpADDIU(opcode, "ADDIU", word, currentLine);
    		break;
    	case Utils.OPCODE_SW:
    		inst = new OpSW(opcode, "SW", word, currentLine);
    		break;
    	case Utils.OPCODE_LW:
    		inst = new OpLW(opcode, "LW", word, currentLine);
    		break;
    	case Utils.OPCODE_SPECIAL:
    		inst = parseSpecialType(word, currentLine);
    		break;
    	case Utils.OPCODE_J:
    		inst = new OpJUMP(opcode, "J", word, currentLine);
    		break;
    	case Utils.OPCODE_BEQ:
    		inst = new OpBEQ(opcode, "BEQ", word, currentLine);
    		break;
    	case Utils.OPCODE_BNE:
    		inst = new OpBNE(opcode, "BNE", word, currentLine);
    		break;
    	case Utils.OPCODE_REGIMM:
    		inst = new OpBGEZBLTZ(word, currentLine);
    		break;
    	case Utils.OPCODE_BGTZ:
    		inst = new OpBGTZ(opcode, "BGTZ", word, currentLine);
    		break;
    	case Utils.OPCODE_BLEZ:
    		inst = new OpBLEZ(opcode, "BLEZ", word, currentLine);
    		break;
    	case Utils.OPCODE_SLTI:
    		inst = new OpSLTI(opcode, "SLTI", word, currentLine);
    		break;
    	default:
    		break;
    	}
    	return inst;
    }
    
    private Instructions parseSpecialType(int word, int addr)
    {
    	short funct = (short) ((word >> Utils.FUNCT_OFFSET) & Utils.FUNCT_MASK);
    	Instructions inst = null;
    	switch(funct){
    	case Utils.FUNCT_BREAK:
    		inst = new OpBREAK(word, addr);
    		endLine = addr;
    		break;
    	case Utils.FUNCT_SLT:
    		inst = new OpSLT(word, addr);
    		break;
    	case Utils.FUNCT_SLTU:
    		inst = new OpSLTU(word, addr);
    		break;
    	case Utils.FUNCT_NOP:
    		if(word == 0){//NOP
        		inst = new OpNOP(word, addr);
        		break;
    		}else{ // SLL
        		inst = new OpSLL(word, addr);
        		break;
    		}
    	case Utils.FUNCT_SRL:		
    		inst = new OpSRL(word, addr);
    		break;
    	case Utils.FUNCT_SRA:
    		inst = new OpSRA(word, addr);
    		break;
    	case Utils.FUNCT_SUB:
    		inst = new OpSUB(word, addr);
    		break;
    	case Utils.FUNCT_SUBU:
    		inst = new OpSUBU(word, addr);
    		break;
    	case Utils.FUNCT_ADD:
    		inst = new OpADD(word, addr);
    		break;
    	case Utils.FUNCT_ADDU:
    		inst = new OpADDU(word, addr);
    		break;
    	case Utils.FUNCT_AND:
    		inst = new OpAND(word, addr);
    		break;
    	case Utils.FUNCT_OR:
    		inst = new OpOR(word, addr);
    		break;
    	case Utils.FUNCT_XOR:
    		inst = new OpXOR(word, addr);
    		break;
    	case Utils.FUNCT_NOR:
    		inst = new OpNOR(word, addr);
    		break;
    	default:
    		inst = null;
    	}
    	return inst;
    }

    private Instructions InstructionFetch()
    {
    	if(getpc(cycle) > endLine)
    		return null;
    	Instructions inst = preQueue.get((getpc(cycle)-Utils.startCodeLine)/4);
    	if(inst == null){
    		System.out.println("instruction fetch: all done");
    		return inst;
    	}
    	iq.enQueue(inst);
    	if(inst.getInstType() == Utils.InstType.InstType_BRANCH){
    		if(btBuf.predict(getpc(cycle), cycle, false) == Utils.PredictType.Predict_TRUE){
    			setpc(btBuf.getTargetAddr(getpc(cycle)), cycle);
    			inst.setPredicted(1);
    		}else{
    			setpc(getpc(cycle) + 4, cycle);
    			Utils.PredictType p = btBuf.predict(getpc(cycle), cycle, false);
    			//if(inst.getOpName().equals("J"))
    			//	p = Utils.PredictType.Predict_TRUE;
    			btBuf.insert(inst.getAddr(), inst.getTargetAddr(getnewpc()), p, cycle); /* temporary value for target address and prediction, to be update
    			 										  * after Instruction Execution stage.
    			 										  */
    			inst.setPredicted(0);

    		}
    	}else
    		setpc(getpc(cycle) + 4, cycle);
    	return inst;
    }
    
    private boolean InstructionIssue()
    {
    	Instructions inst;
  	
    	if(rob.available()){
    		inst = iq.getFirst(); 
    		if(inst != null && (inst.getOpName().equals("NOP") || inst.getOpName().equals("BREAK"))){
    			inst = iq.deQueue();
    			int robidx = rob.getAvailable();
        		ReorderBuffer.ReorderBufferElement robb = rob.getROB(robidx);
            	robb.setInst(inst);
            	robb.setReady(true);
        		return true;
    		}
    	}
    	
    	if(rob.available() && rs.available()){

    		inst = iq.deQueue();
        	if(inst == null)
        		return false;
    		int robidx = rob.getAvailable();
    		ReorderBuffer.ReorderBufferElement robb = rob.getROB(robidx);
    		ReservationStation.RSElem rser = rs.getAvailable();
 
        	if(inst.getSource1() != -1){      		
        		RegisterStat.RegStatElement rses1 = rstat.getRegStat(inst.getSource1());
        		if(true == rses1.getBusy()){
        			ReorderBuffer.ReorderBufferElement h = rses1.getReorder();
        		
        			if(h.getReady() == true){
        				rser.setVj(h.getValue(), cycle);
        				rser.setQj(null, cycle);
        			}else
        				rser.setQj(h, cycle);
        		}else{
        			rser.setVj(rf.getReg(inst.getSource1(), cycle), cycle);
        			rser.setQj(null, cycle);
        		}
        	}
        	if(inst.getSource2() != -1){       		
        		RegisterStat.RegStatElement rses2 = rstat.getRegStat(inst.getSource2());
        		if(true == rses2.getBusy()){
        			ReorderBuffer.ReorderBufferElement h = rses2.getReorder(); 			
        			if(true == h.getReady()){
        				rser.setVk(h.getValue(), cycle);
        				rser.setQk(null, cycle);
        			}else{
        				rser.setQk(h, cycle);
        			}
        		}else{	    			
        			rser.setVk(rf.getReg(inst.getSource2(), cycle), cycle);
        			rser.setQk(null, cycle);
        		}
        	}
        	if(inst.getDest() != -1){
        		RegisterStat.RegStatElement rsed = rstat.getRegStat(inst.getDest());
        		rsed.setReorder(robb);
        		rsed.setBusy(true);
        		robb.setDest(inst.getDest());
        	}
        	if(inst.getImm() != -1){
        		rser.setA(inst.getImm());
        	}
        	rser.setInst(inst);
        	rser.setDest(robb, cycle);
        	rser.setBusy(true);
        	robb.setInst(inst);
        	robb.setReady(false);
    //if(inst.getInstType()==Utils.InstType.InstType_ST)
    //	System.out.println("issue: getVj="+rser.getVj(cycle, false)+" getVk="+rser.getVk(cycle, false)
    //			+" getImm="+rser.getA()+" qj="+rser.getQj(cycle, false)+" qk="+rser.getQk(cycle, false));
        	return true;
    	}
    	return false;
    }

    private void InstructionExecute()
    {
    	for(int i = 0; i < rs.size(); i++){
    		ReservationStation.RSElem aa = rs.getRSElem(i);
    		if(!rs.dependency(i, cycle) && rs.getRSElem(i).getCompute() == -1){
    			ReservationStation.RSElem rse = rs.getRSElem(i);
    			Instructions inst = rse.getInst();
    			
    			if(inst.getInstType() == Utils.InstType.InstType_LD 
    					|| inst.getInstType() == Utils.InstType.InstType_ST){
    				//check "all load/store ahead of this load have their addresses ready in the ROB" 
    			}
    			
    			int dst = rs.compute(i, cycle);
    			
    			if(inst.getInstType() == Utils.InstType.InstType_BRANCH){
					Utils.PredictType p;
					if(dst == 1)
						p = Utils.PredictType.Predict_TRUE;
					else 
						p = Utils.PredictType.Predict_FALSE;
    				BranchProcess bp = new BranchProcess(rse, p);
    				branches.add(bp);
    			//break;
    			}else if(inst.getOpName().equals("SW")){
    				//by pass write back
    				//write address into rob
    				//mark it ready
    				ReorderBuffer.ReorderBufferElement robe =rse.getDest(cycle, false);
    				rse.setBusy(false);
    				/*
    				for(int j = 0; j < rs.size(); j++){
    					ReservationStation.RSElem rsej = rs.getRSElem(j);
    					if(rsej.getQj(cycle) == rse.getDest(cycle)){
    						rsej.setVj(rse.getCompute(), cycle);
    						rsej.setQj(null, cycle);
    					}
    					if(rsej.getQk(cycle) == rse.getDest(cycle)){
    						rsej.setVk(rse.getCompute(), cycle);
    						rsej.setQk(null, cycle);
    					}
    				}*/
    				robe.setDest(rse.getCompute());
    				//System.out.println("InstructionExecute:rse-"+rse.getCompute());
    				//System.out.println("InstructionExecute:"+robe.getDest());
    				if(robe.getValue() == -1 && rse.getQk(cycle, false) == null)
    					robe.setValue(rse.getVk(cycle, false));
    				if(robe.getValue() != -1)
    					robe.setReady(true);	
    			}else if(inst.getOpName().equals("NOP")){
    				System.out.println("shouldn't come here");
    			}else if(inst.getOpName().equals("BREAK")){
    				System.out.println("shouldn't come here");
    			}
    		}
    	}
    	//in the end: if the executed instruction is a branch instruction, resolve btb
    }

    private void InstructionExecute2()
    {
		if(branches.size() == 0)
			return;
		
		while(!branches.isEmpty()){
			BranchProcess bp = branches.remove(0);
			ReservationStation.RSElem rse = bp.rse;
			Utils.PredictType p = bp.predict;
			Instructions inst = rse.getInst();
				
			ReorderBuffer.ReorderBufferElement robe = rse.getDest(cycle, true);

			if(p == btBuf.predict(inst.getAddr(), cycle, true) 
				|| (p == Utils.PredictType.Predict_FALSE 
					&& btBuf.predict(inst.getAddr(), cycle, true) == Utils.PredictType.Predict_NOTSET)){ //output == predict
				robe.setState("Commit");
				robe.setReady(true);
				btBuf.updateValue(inst.getAddr(), btBuf.getTargetAddr(inst.getAddr()), p, cycle);
			}else{
				int j;
				
				j = rob.getIdx(rse.getDest(cycle, true));
				while(j < rob.getSize() - 1){			
					for(int k = rs.size() - 1; k >= 0; k--){
						ReservationStation.RSElem rse1 = rs.getRSElem(k);
						if(rob.getIdx(rse1.getDest(cycle, true)) == rob.getSize() - 1){
							rs.remove(k);
							break;
						}
					}
					rob.remove(rob.getSize() - 1); // all instructions after the branch in the ROB are cancelled 
				}
				/*also remove the instructions after the branch in the reservation station?*/
				
				while(iq.size() > 0)
					iq.deQueue();
				
				robe.setState("Commit");
				robe.setReady(true);
				if(p == Utils.PredictType.Predict_TRUE && btBuf.predict(inst.getAddr(), cycle, true) != Utils.PredictType.Predict_TRUE){ //I think this branch includes "J **" operation
					int taddr = btBuf.getTargetAddr(inst.getAddr());
					setpc(taddr, cycle);
					btBuf.updateValue(inst.getAddr(), taddr, Utils.PredictType.Predict_TRUE, cycle);
				}else{
					int taddr = btBuf.getTargetAddr(inst.getAddr());
					setpc(inst.getAddr() + 4, cycle);
					btBuf.updateValue(inst.getAddr(), taddr, Utils.PredictType.Predict_TRUE, cycle);
				}
			}
			rse.setBusy(false);

    }
    }
    
    private void InstructionWriteback()
    {
    	for(int i = 0; i < rs.size(); i++){
    		ReservationStation.RSElem rse = rs.getRSElem(i);
    		Instructions inst = rse.getInst();
    		if(rse.getCompute() != -1){ //finish execution and get result
    			if(inst.getInstType() == Utils.InstType.InstType_LD){
    				if(rse.getLoadVal() == -1){
	    				//stage 1. check early store
	    				boolean conflict = false;
	    				for(int j = 0; j< rob.getIdx(rse.getDest(cycle, false)); j++){
	    					ReorderBuffer.ReorderBufferElement robe = rob.getROB(j);
	    					if(robe.getInst().getInstType() == Utils.InstType.InstType_ST 
	    							&& robe.getDest() == rse.getCompute())
	    						//exsits previous uncompleted store with same address, stall
	    						//We stall it just by moving to next instruction
	    						conflict = true;
	    						break; 
	    				}
	    				if(conflict)
	    					continue;
    				//		   and then memory access
	    				rse.setLoadVal(ds.getData(rse.getCompute(), cycle));
	    				rse.setCompute(rse.getLoadVal());
    				}else{
    				//stage 2. broadcast to cdb
    					broadcastCDB(rse);
    				}
    			}else if(inst.getInstType() == Utils.InstType.InstType_ST){
    				//store by pass this stage
    			}else{ //general instructionsw
    				broadcastCDB(rse);
    			}
    			//break; // only writeback one instruction
    		}
    	}
    }
    
    private void InstructionCommit()
    {
    	if(rob.getSize() > 0){
	    	ReorderBuffer.ReorderBufferElement robe = rob.getROB(0);
	    	if(robe.getReady()){
	    		Instructions inst = robe.getInst();
	    		if(inst.getInstType() == Utils.InstType.InstType_BRANCH){
	    			
	    		}else if(inst.getOpName() == "NOP"){
	    			
	    		}else if(inst.getOpName().equals("BREAK")){
	    		}else if(inst.getInstType() == Utils.InstType.InstType_ST){
	    			//System.out.println("**************** st: "+robe.getDest()+" "+robe.getValue());
	    			ds.setData(robe.getDest(), robe.getValue(), cycle);
	    		}else{
	    			//regular instructions, move store to InstructionCommit2
	    			rf.setReg(robe.getDest(), robe.getValue(), cycle);
	    		}
	    		rob.getROB(0).setState("Committed");
	    	}else{
	    	}
    	}

    	return;
    }
    
    private void cleaning()
    {
    	for(int i = rs.size() - 1; i >= 0; i--){
    		ReservationStation.RSElem rse = rs.getRSElem(i);
    		if(!rse.getBusy())
    			rs.remove(i);
    	}
    	
    	if(rob.getSize() > 0){
    		if(rob.getROB(0).getState().equals("Committed")){
    			if(rob.getROB(0).getInst().getInstType() != Utils.InstType.InstType_ST
    					&& rob.getROB(0).getInst().getInstType() != Utils.InstType.InstType_BRANCH
    					&& !rob.getROB(0).getInst().getOpName().equals("NOP")
    					&& !rob.getROB(0).getInst().getOpName().equals("BREAK")){
    				rstat.getRegStat(rob.getROB(0).getDest()).setReorder(null);
    				rstat.getRegStat(rob.getROB(0).getDest()).setBusy(false);
	    		}
    			rob.remove(0);
    			/*                           n  nb
    			for(int i = 0; i < rs.size(); i++){
    				rs.getRSElem(i).setDest(rs.getRSElem(i).getDest() - 1);
    				
    				rs.getRSElem(i).setQj(rs.getRSElem(i).getQj() - 1);
    				rs.getRSElem(i).setQk(rs.getRSElem(i).getQk() - 1);
    			}
    			*/
    		}
    	}
    	
    }
    
    private void broadcastCDB(ReservationStation.RSElem rse)
    {
		ReorderBuffer.ReorderBufferElement robe = rse.getDest(cycle, false);
		rse.setBusy(false);
		for(int j = 0; j < rs.size(); j++){
			ReservationStation.RSElem rsej = rs.getRSElem(j);
			
			if(rsej.getQj(cycle, false) == rse.getDest(cycle, false)){
			
				rsej.setVj(rse.getCompute(), cycle);
				rsej.setQj(null, cycle);
			}
			if(rsej.getQk(cycle, false) == rse.getDest(cycle, false)){
				rsej.setVk(rse.getCompute(), cycle);
				rsej.setQk(null, cycle);
			}
		}
			
		robe.setValue(rse.getCompute());
		robe.setReady(true);
		//cannot inform store instructions by update values in ReservationStation, 
		//query ROB for instructions who need it.
		for(int k = 0; k <rob.getSize(); k++){
			Instructions in = rob.getROB(k).getInst();
			if(in.getInstType() == Utils.InstType.InstType_ST && in.getSource2() == rse.getInst().getDest())
			{
				rob.getROB(k).setValue(rse.getCompute());
				if(rob.getROB(k).getDest() != -1)
					rob.getROB(k).setReady(true);
			}
		}
    }
    
    private void setpc(int pc, int cycle)
    {
    	oldpc = newpc;
    	newpc = pc;
    	updatecycle = cycle;
    }
    
    private int getpc(int cycle)
    {
    	if(cycle > updatecycle)
    		return newpc;
    	else
    		return oldpc;
    }
    private int getnewpc()
    {
    	return newpc;
    }
    
    private class BranchProcess{
    	BranchProcess(ReservationStation.RSElem r, Utils.PredictType p){
    		rse = r;
    		predict = p;
    	}
    	public ReservationStation.RSElem rse;
    	public Utils.PredictType predict;
    };
    private ArrayList<BranchProcess> branches = new ArrayList<BranchProcess>();
    
    private int cycle = 0;
    private int oldpc;
    private int newpc;
    private int updatecycle = 0;
 
    
    private ArrayList<Instructions> preQueue;
    private InstructionQueue iq;
    private BranchTargetBuffer btBuf;
    private RegisterFile rf;
    private RegisterStat rstat;
    private ReorderBuffer rob;
    private ReservationStation rs;
    private DataSegment ds;
    
    private String inputFilename = "", outputFilename = "", operation = "";
    private FileInputStream inputFile;
    private DataInputStream inputStream;
    private PrintWriter outputWriter;
    private int startCycle = 0, endCycle = 0;
    private int currentLine;
    private int endLine = 99999999;
    private boolean stop = false;

}
