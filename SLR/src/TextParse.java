import javax.swing.table.DefaultTableModel;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextParse{
	ArrayList<Production> productions; //产生式集
	HashMap<String, String> predictmap;
	ArrayList<String> input_cache;
	ArrayList<String> ac;
	private DefaultTableModel process;
	private DefaultTableModel analyse;
	ArrayList<Integer> deduce_str;
	private DefaultTableModel tbmodel_lex_result;
	private DefaultTableModel errorlist;
	HashMap<Integer, HashMap<String, Action>> ActionAndGoto;
	Boolean first=true;
	HashMap<String,Integer> arrayMap;
	Stack<Attribute> valueStack = new Stack<>();

	private static int tNumber = 0;

	HashMap<String,String> top;

	private ArrayList<String> symbolStack;
	private ArrayList<String> signStack;
	public ArrayList<Integer> rowNumber;
	private Integer nextInh=100;
	private TreeMap<Integer, String> resultMap;

	public TextParse(ArrayList<String> input_cache, DefaultTableModel tbmodel_lex_resultm, HashMap actionAndGoto, ArrayList<Production> production, ArrayList<Integer> rowNumber, DefaultTableModel process, DefaultTableModel analyse, DefaultTableModel errorlist, ArrayList<String> symbolStack){
		this.symbolStack=symbolStack;
		arrayMap=new HashMap<>();
		signStack = new ArrayList<>();
		top = new HashMap<>();
		ActionAndGoto = actionAndGoto;
		predictmap = new HashMap<String, String>();
		this.analyse=analyse;
		this.input_cache = input_cache;
		this.process=process;
		this.errorlist=errorlist;
		deduce_str = new ArrayList<Integer>();
		this.tbmodel_lex_result = tbmodel_lex_result;
		productions=production;
		this.resultMap = new TreeMap<>();
		this.rowNumber = rowNumber;
		for(int i = 0 ; i < rowNumber.size(); i++) {
			System.out.println(rowNumber.get(i));
		}
		tNumber = 0;
		ac=new ArrayList<String>();
	}
	
	// 句法分析
	public void Parsing(){
		// 初始符号压入栈
		error();
		deduce_str.add(0);
		System.out.println(input_cache);
		input_cache.add("$");
		symbolStack.add("$");
		Collections.reverse(input_cache);
		Collections.reverse(symbolStack);
		ArrayList<String> stack=new ArrayList<String>();
		stack.add("$");
		signStack.add("$");
		int rowcount = 0;
		while (input_cache.size()!=0) {
//			if (stack.size()!=signStack.size()){
//				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			}
			Action action=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(input_cache.get(input_cache.size()-1));

			errorRecovery(action,rowcount,stack);

			action=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(input_cache.get(input_cache.size()-1));
			System.out.println("状态:"+deduce_str);
			System.out.println("token符号:"+signStack);
			System.out.println("符号:"+stack);
			System.out.println("输入:"+input_cache);
			System.out.println("token:"+symbolStack);
			System.out.println("Value栈:"+valueStack);
			System.out.println("Value栈大小:"+valueStack.size());
//			System.out.println(input_cache.size());
//			System.out.println(symbolStack.size());
			System.out.println(action.getAction()+action.getToProject());
//			System.out.println();
			String stack1=new String();
			String action1=new String();
			stack1=turntostring(stack);
			if(action.getAction()=="s"||action.getAction()=="")
			{
				action1="Shift to"+action.getToProject();
				deduce_str.add(action.getToProject());
				stack.add(input_cache.get(input_cache.size()-1));
				signStack.add(symbolStack.get(symbolStack.size()-1));
				input_cache.remove(input_cache.size()-1);
				symbolStack.remove(symbolStack.size()-1);
				rowcount++;
			}
			else if(action.getAction()=="r")
			{
				int num = action.getToProject();
				executeSemanticActions(num,rowcount);
				if(action.getToProject()==0&&stack.size()==2)
				{
					action1="acc";
					process.addRow(new String[]{stack1,action1});
					System.out.println("acc");
					break;
				}
				if(action.getToProject()==4)
				{
					action1="Reduce by "+productions.get(action.getToProject()).left+"->ε";
					stack.add("decls");
					signStack.add("decls");
					Action a=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(stack.get(stack.size()-1));
					deduce_str.add(a.getToProject());
				}
				else if(action.getToProject()==11)
				{
					action1="Reduce by "+productions.get(action.getToProject()).left+"->ε";
					stack.add("stmts");
					signStack.add("stmts");
					Action a=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(stack.get(stack.size()-1));
					deduce_str.add(a.getToProject());
				}
				else if (action.getToProject()==27){
					action1="Reduce by "+productions.get(action.getToProject()).left+"->ε";
					stack.add("M");
					signStack.add("M");
					Action a=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(stack.get(stack.size()-1));
					deduce_str.add(a.getToProject());
				}
				else if (action.getToProject()==35){
					action1="Reduce by "+productions.get(action.getToProject()).left+"->ε";
					stack.add("K");
					signStack.add("K");
					Action a=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(stack.get(stack.size()-1));
					deduce_str.add(a.getToProject());
				}
				else if (action.getToProject()==33){
					action1="Reduce by "+productions.get(action.getToProject()).left+"->ε";
					stack.add("L");
					signStack.add("L");
					Action a=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(stack.get(stack.size()-1));
					deduce_str.add(a.getToProject());
				}
				else if (action.getToProject()==28){
					action1="Reduce by "+productions.get(action.getToProject()).left+"->ε";
					stack.add("N");
					signStack.add("N");
					Action a=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(stack.get(stack.size()-1));
					deduce_str.add(a.getToProject());
				}
				else {
					action1="Reduce by "+productions.get(action.getToProject()).left+"->"+ Arrays.toString(productions.get(action.getToProject()).right);

					int b=productions.get(action.getToProject()).right.length;
//					System.out.println("=================");
//					System.out.println(b);
					while(b>0)
					{
						deduce_str.remove(deduce_str.size()-1);
						stack.remove(stack.size()-1);
						signStack.remove(signStack.size()-1);
						b=b-1;
					}
					stack.add(productions.get(action.getToProject()).left);
					signStack.add(productions.get(action.getToProject()).left);
//					System.out.println(deduce_str.get(deduce_str.size()-1));
					Action a=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(stack.get(stack.size()-1));
					deduce_str.add(a.getToProject());
//					System.out.println("====================归约后============================");
//					System.out.println("状态:"+deduce_str);
//					System.out.println("token符号:"+signStack);
//					System.out.println("符号:"+stack);
//					System.out.println("输入:"+input_cache);
//					System.out.println("token:"+symbolStack);
//					System.out.println("Value栈:"+valueStack);
//					System.out.println("Value栈大小:"+valueStack.size());
//					System.out.println("====================归约后============================");
				}
			}
			process.addRow(new String[]{stack1,action1});
		}
		optimize(resultMap);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("result.txt"));
			Set<Map.Entry<Integer, String>> entries = resultMap.entrySet();
			for (Map.Entry<Integer,String> entry:entries){
				bw.write(entry.getKey()+":"+"   "+entry.getValue());
				bw.newLine();
				analyse.addRow(new String[]{entry.getKey().toString(),entry.getValue()});
			}
			bw.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println(top);
		System.out.println(resultMap);
	}

	private void optimize(TreeMap<Integer, String> resultMap) {
		int size = resultMap.size();
//		System.out.println("=================");
//		System.out.println(resultMap.get(100));
//		System.out.println("=================");
		size+=100;
		String regex = "^goto.*";//以goto开头的正则式
		Pattern pattern = Pattern.compile(regex);
		for(int i = 100; i < size-1 ; i++) {
			//若出现两个goto，只要其中有一个是前面有跳转的就不优化，否则有冗余
			String str1 = resultMap.get(i);
			String str2 = resultMap.get(i+1);
			Matcher matcher1 = pattern.matcher(str1);
			Matcher matcher2 = pattern.matcher(str2);
			if(matcher1.find() && matcher2.find()) {
				//检测是否有goto跳转到此处的
				String index2 = String.valueOf(i+1);
				String indexreg2 = ".*goto\\s+"+index2+".*";
				Pattern indexp2 = Pattern.compile(indexreg2);
				boolean flag = false;
				for(int l = 101; l < i; l++) {
					Matcher indexm2 = indexp2.matcher(resultMap.get(l));
					if(indexm2.find()) {
						flag = true;
						break;
					}
				}
				if(flag) continue;
//				System.out.println("===========");
//				System.out.println(str1+"  "+str2);
//				System.out.println("===========");
				resultMap.remove(i+1);
				//对每个i+1之后的key都降1，对于原来goto中有跳转到i+1的地方跳转数值-1
				for(int j = i+1; j < size-1; j++) {
					String num = String.valueOf(j);
					//寻找带有goto 该编号的条目，并replace
					String numregex = ".*goto\\s+.*";//含有goto的语句
					Pattern numpattern = Pattern.compile(numregex);
					for(int k = 100; k < size-1 ; k++) {
						String str = resultMap.get(k);
						if(str == null) continue;
						Matcher nummatcher = numpattern.matcher(str);
						if(nummatcher.find()) {
							if(str.contains(num)) {
								System.out.println("===========");
								System.out.println(num+":"+str);

								String newstr = str.replace(num,String.valueOf(j-1));
								System.out.println("new"+":"+newstr);
								System.out.println("===========");
								resultMap.remove(k);
								resultMap.put(k,newstr);
							}
						}
					}
					resultMap.put(j,resultMap.get(j+1));
				}
				resultMap.remove(size-1);
				size-=1;
				i-=1;
			}

		}
	}

	private void executeSemanticActions(int num,int rowcount) {
		Attribute attribute = new Attribute();

		if(num == 5){ // decl → type id;
			top.put(signStack.get(signStack.size()-2),valueStack.peek().getType() );
			gen(signStack.get(signStack.size()-2)+": "+valueStack.pop().getType());
		}
		else if(num == 1){// program -> basic id ( ) block
			gen("return");
		}else if(num == 2){//block   -> { decls stmts }
			/*
			                block.nextlist=stmts.nextlist;
                block.breaklist=stmts.breaklist;
                block.continuelist=stmts.continuelist;
			backpatch(stmts.nextlist, nextinstr);
			 */
			attribute.setNextlist(valueStack.peek().getNextlist());
			attribute.setBreakList(valueStack.peek().getBreakList());
			attribute.setContinueList(valueStack.peek().getContinueList());
			backpatch(valueStack.peek().getNextlist(), nextInh);
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 6) {
			/*
			type     → type1[num] {
                type.type = array( num.lexval, type1.type);
                type.width = num.val * type1.width;
                }
			 */
			attribute.setType("array( "+signStack.get(signStack.size()-2)+", "+valueStack.peek().getType()+" )");
			attribute.setWidth(Integer.parseInt(signStack.get(signStack.size()-2))*valueStack.peek().getWidth());
			valueStack.pop();
			valueStack.push(attribute);

		} else if(num == 7) {// type → basic
			attribute.setWidth(valueStack.peek().getWidth());
			attribute.setType(valueStack.peek().getType());
			valueStack.pop();
			valueStack.push(attribute);
		}
		else if(num == 8) { // basic → float
			attribute.setType("float");
			attribute.setWidth(8);
			valueStack.push(attribute);
		}
		else if(num == 9) { // basic → int
			attribute.setType("int");
			attribute.setWidth(8);
			valueStack.push(attribute);
		}
		else if(num == 10) {// stmts → stmts1 M stmt
/*
                backpatch(stmts1.nextlist, M.instr);
                stmts.nextlist=stmt.nextlist;
                stmts.breaklist=merge(stmts1.breaklist, stmt.breaklist);
                stmts.continuelist=merge(stmts1.continuelist, stmt.continuelist);
 */
			backpatch(valueStack.get(valueStack.size()-3).getNextlist(),valueStack.get(valueStack.size()-2).getInstr());
			attribute.setNextlist(valueStack.peek().getNextlist());
			attribute.setBreakList(merge(valueStack.get(valueStack.size()-3).getBreakList(),valueStack.get(valueStack.size()-1).getBreakList()));
			attribute.setContinueList(merge(valueStack.get(valueStack.size()-3).getContinueList(),valueStack.get(valueStack.size()-1).getContinueList()));
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			System.out.println(attribute.getNextlist());
			valueStack.push(attribute);
		}
		else if(num == 11) { // stmts → ε
				/*
				{
                stmts.nextlist=null;
                stmts.breaklist=null;
                stmts.continuelist=null;
                }
				 */
			attribute.setContinueList(new ArrayList<>());
			attribute.setNextlist(new ArrayList<>());
			attribute.setBreakList(new ArrayList<>());
			valueStack.push(attribute);
		}
		else if(num == 12) {// stmt → id = expr;
/*
                stmt.nextlist=null;
                stmt.breaklist=null;
                stmt.continuelist= null;
                gen(top.get(id.lexeme) '=' expr.addr;
 */
			if (checkError(signStack.get(signStack.size()-4))){

				if (valueStack.peek().getType().equals(top.get(signStack.get(signStack.size()-4)))){
					gen(signStack.get(signStack.size()-4)+"="+valueStack.pop().getAddr());
					valueStack.push(attribute);
				}else {
					gen("t"+tNumber+"= ("+top.get(signStack.get(signStack.size()-4))+")"+valueStack.pop().getAddr());
					gen(signStack.get(signStack.size()-4)+"="+"t"+tNumber);
					tNumber++;
					valueStack.push(attribute);
				}
			}else {
				System.out.println(signStack.get(signStack.size()-4)+"没有定义");
				String a= rowNumber.get(rowcount-1)+"";
				errorlist.addRow(new String[]{ a,signStack.get(signStack.size()-4)+"没有定义"});
				error_write(a,signStack.get(signStack.size()-4)+"没有定义");
				if (valueStack.peek().getType().equals(valueStack.get(valueStack.size()-2).getType())){
					gen(signStack.get(signStack.size()-4)+"="+valueStack.pop().getAddr());
					valueStack.push(attribute);
				}
			}


		}
		else if(num == 13) {// stmt → loc = expr;
			/*
			                gen(loc.array.base '[' loc.addr ']' '=' expr.addr;
			 */
			gen(valueStack.get(valueStack.size()-2).getArrayName()+"["+valueStack.get(valueStack.size()-2).getAddr()+"]"+"="+valueStack.get(valueStack.size()-1).getAddr());
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 14) {
			/*
			expr     → loc {
                expr.addr=new Temp();
                gen(expr.addr '=' loc.array.base '[' loc.addr ']';
                }
			 */
			attribute.setAddr("t"+tNumber);
			tNumber++;
			gen(attribute.getAddr()+"="+valueStack.peek().getArrayName()+"["+valueStack.peek().getAddr()+"]");
			valueStack.pop();
			valueStack.push(attribute);
		} else if(num == 15) { // loc → loc1 [expr]
/*
{
                loc.array = loc1.array;
                loc.type = loc1.type.elem;
                t = new Temp();
                loc.addr = new Temp();
                gen(t '=' expr '*' loc.type.width);
                gen(loc.addr '=' loc.array.addr '+' t);
                }
 */
			attribute.setArray(valueStack.get(valueStack.size()-2).getArray());
			attribute.setType(valueStack.get(valueStack.size()-2).getType());
			int t = tNumber;
			gen("t"+t+"="+valueStack.peek().getAddr()+"*"+valueStack.get(valueStack.size()-2).getWidth());
			tNumber++;
			attribute.setAddr("t"+tNumber);
			tNumber++;
			gen(attribute.getAddr()+"="+"a"+"t"+t);
			valueStack.pop();
			valueStack.pop();

			valueStack.push(attribute);
		}
		else if(num == 16){ //loc -> id[expr]
/*
                loc.array = top.get(id.lexeme);
                loc.type = loc.array.type.elem;
                loc.addr = new Temp();
                gen(loc.addr '=' expr '*' loc.type.width);
 */

			if (top.get(signStack.get(signStack.size()-4))!=null){
				if (top.get(signStack.get(signStack.size()-4)).contains("int")){
					attribute.setType("int");
					attribute.setWidth(4);
				} else if (top.get(signStack.get(signStack.size() - 4)).contains("float")) {
					attribute.setType("float");
					attribute.setWidth(4);
				}
			}else {
				String a= rowNumber.get(rowcount-1)+"";
				errorlist.addRow(new String[]{ a,"数组"+signStack.get(signStack.size()-4)+"没有被定义"});
				error_write(a,"数组"+signStack.get(signStack.size()-4)+"没有被定义");
				System.out.println("数组"+signStack.get(signStack.size()-4)+"没有被定义");
			}
			attribute.setArrayName(signStack.get(signStack.size()-4));
			attribute.setArray(signStack.get(signStack.size()-2));
			attribute.setAddr("t"+tNumber);
			tNumber++;
			gen(attribute.getAddr()+"="+valueStack.get(valueStack.size()-1).getAddr()+"*"+attribute.getWidth());
			valueStack.pop();
			valueStack.push(attribute);
		}
		else if(num == 17) {// stmt → if (bool) M stmt1
				/*
				{
                backpatch(bool.truelist, M.instr);
				stmt.nextlist=merge(bool.falselist, stmt1.nextlist);
				stmt.breaklist=stmt1.breaklist;
				stmt.continuelist=stmt1.continuelist;
				}
				 */
//			backpatch(valueStack.get(valueStack.size()-3).getTrueList(),valueStack.get(valueStack.size()-2).getInstr());
//			attribute.setNextlist(merge(valueStack.get(valueStack.size()-3).getFalseList(),valueStack.get(valueStack.size()-1).getNextlist()));
//			System.out.println(attribute.getNextlist());
//			attribute.setBreakList(valueStack.get(valueStack.size()-1).getBreakList());
//			attribute.setContinueList(valueStack.get(valueStack.size()-1).getContinueList());
//			valueStack.pop();
//			valueStack.pop();
//			valueStack.pop();
//			valueStack.push(attribute);
		}
		else if(num == 18) {// stmt → if (bool) M1 stmt1 N else M2 stmt2
			/*
			{
				backpatch(bool.truelist, M1.instr);
				backpatch(bool.falselist, M2.instr);
				temp=merge(stmt1.nextlist, N.nextlist);
				stmt.nextlist=merge(temp, stmt2.nextlist);
				stmt.breaklist=merge(stmt1.breaklist, stmt2.breaklist);
				stmt.continuelist=merge(stmt1.continuelist, stmt2.continuelist);
				}
			 */
//			backpatch(valueStack.get(valueStack.size()-6).getTrueList(),valueStack.get(valueStack.size()-5).getInstr());
//			backpatch(valueStack.get(valueStack.size()-6).getFalseList(),valueStack.get(valueStack.size()-2).getInstr());
//			List<Integer> temp = merge(valueStack.get(valueStack.size() - 4).getNextlist(), valueStack.get(valueStack.size() - 3).getNextlist());
//			attribute.setNextlist(merge(temp,valueStack.get(valueStack.size() - 1).getNextlist()));
//			attribute.setBreakList(merge(valueStack.get(valueStack.size()-4).getBreakList(),valueStack.get(valueStack.size()-1).getBreakList()));
//			attribute.setContinueList(merge(valueStack.get(valueStack.size()-4).getContinueList(),valueStack.get(valueStack.size()-1).getContinueList()));
//			valueStack.pop();
//			valueStack.pop();
//			valueStack.pop();
//			valueStack.pop();
//			valueStack.pop();
//			valueStack.pop();
//			valueStack.push(attribute);
		} else if (num == 19) {
			backpatch(valueStack.get(valueStack.size()-6).getFalseList(),valueStack.get(valueStack.size()-2).getInstr());
			backpatch(valueStack.get(valueStack.size()-6).getTrueList(),valueStack.get(valueStack.size()-5).getInstr());
			List<Integer> merge = merge(valueStack.get(valueStack.size() - 4).getNextlist(), valueStack.get(valueStack.size() - 3).getNextlist());
			attribute.setNextlist(merge(merge,valueStack.peek().getNextlist()));
			attribute.setBreakList(merge(valueStack.get(valueStack.size()-4).getBreakList(),valueStack.peek().getBreakList()));
			attribute.setContinueList(merge(valueStack.get(valueStack.size()-4).getContinueList(),valueStack.peek().getContinueList()));

			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 20) {
/*
Stmt_Open -> if ( Bool ) M Stmt1
    {backpatch(Bool.truelist, M.instr)
    Stmt_Open.nextlist = merge(Bool.falselist, Stmt1.nextlist)
    Stmt_Open.breaklist = Stmt1.breaklist
    Stmt_Open.continuelist = Stmt1.continuelist}
 */

			backpatch(valueStack.get(valueStack.size()-3).getTrueList(),valueStack.get(valueStack.size()-2).getInstr());
			attribute.setNextlist(merge(valueStack.get(valueStack.size()-3).getFalseList(),valueStack.peek().getNextlist()));
			attribute.setBreakList(valueStack.peek().getBreakList());
			attribute.setContinueList(valueStack.peek().getContinueList());
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 21) {
			/*
			Stmt_Open -> if ( Bool ) M1 Stmt_Closed1 N else M2 Stmt_Open2
    {backpatch(Bool.truelist, M1.instr)
    backpatch(Bool.falselist, M2.instr)
    temp = merge(Stmt_Closed1.nextlist, N.nextlist)
    Stmt_Open.nextlist = merge(temp, Stmt_Open2.nextlist)
    Stmt_Open.breaklist = merge(Stmt_Closed1.breaklist, Stmt_Open2.breaklist)
    Stmt_Open.continuelist = merge(Stmt_Closed1.continuelist, Stmt_Open2.continuelist)}
			 */
			backpatch(valueStack.get(valueStack.size()-6).getFalseList(),valueStack.get(valueStack.size()-2).getInstr());
			backpatch(valueStack.get(valueStack.size()-6).getTrueList(),valueStack.get(valueStack.size()-5).getInstr());
			List<Integer> merge = merge(valueStack.get(valueStack.size() - 4).getNextlist(), valueStack.get(valueStack.size() - 3).getNextlist());
			attribute.setNextlist(merge(merge,valueStack.peek().getNextlist()));
			attribute.setBreakList(merge(valueStack.get(valueStack.size()-4).getBreakList(),valueStack.peek().getBreakList()));
			attribute.setBreakList(merge(valueStack.get(valueStack.size()-4).getContinueList(),valueStack.peek().getContinueList()));

			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		} else if(num == 22) {


			backpatch(valueStack.peek().getTrueList(), valueStack.get(valueStack.size()-4).getInstr());
			backpatch(valueStack.get(valueStack.size()-3).getTrueList(),valueStack.get(valueStack.size()-2).getInstr());
			backpatch(valueStack.peek().getContinueList(),valueStack.get(valueStack.size()-4).getInstr());
			attribute.setNextlist(merge(valueStack.get(valueStack.size()-3).getFalseList(),valueStack.peek().getBreakList()));
			gen("goto "+valueStack.get(valueStack.size()-4).getInstr());
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		}
		else if(num == 23) {// stmt → do stmt while (bool);
			/*
			stmt     → do M1 stmt1 while M2 ( bool ) ; {
                backpatch(bool.truelist, M1.instr);
                backpatch(stmt1.nextlist, M2.instr);
                backpatch(stmt1.continuelist, M1.instr);
                stmt.nextlist=merge(bool.falselist, stmt1.breaklist);
                stmt.breaklist=null;
                stmt.continuelist=null;
                gen('goto' M1.instr);
                }
			 */
			backpatch(valueStack.peek().getTrueList(),valueStack.get(valueStack.size()-4).getInstr());
			backpatch(valueStack.get(valueStack.size()-3).getNextlist(),valueStack.get(valueStack.size()-2).getInstr());
			backpatch(valueStack.get(valueStack.size()-3).getContinueList(),valueStack.get(valueStack.size()-4).getInstr());
			attribute.setNextlist(merge(valueStack.peek().getFalseList(),valueStack.get(valueStack.size()-3).getBreakList()));
			gen("goto ");
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		}
		else if(num == 24) {// stmt → break;
			/*
			                stmt.nextlist=null;
                stmt.breaklist=makelist(nextinstr);
                stmt.continuelist=null;
                gen('goto_____');
			 */
			attribute.getBreakList().add(nextInh);
			gen("goto ");
			valueStack.push(attribute);
		} else if (num == 25) {
			/*
			stmt     → continue ;{
                stmt.nextlist=null;
                stmt.breaklist=null;
                stmt.continuelist=makelist(nextinstr);
                gen('goto_____');
                }
			 */
			attribute.getContinueList().add(nextInh);
			gen("goto ");
			valueStack.push(attribute);
		} else if(num == 26) {// stmt → block

		} else if (num == 27) {
			attribute.setInstr(nextInh);
			valueStack.push(attribute);
		} else if (num == 28){
			attribute.setNextlist(new ArrayList<>());
			attribute.getNextlist().add(nextInh);
			valueStack.push(attribute);
			gen("goto ");
		}else if (num == 29) {
			attribute.setTrueList(new ArrayList<>());
			attribute.setFalseList(new ArrayList<>());
			attribute.getTrueList().add(nextInh);
			attribute.getFalseList().add(nextInh+1);

			gen("if "+valueStack.get(valueStack.size()-3).getAddr()+valueStack.get(valueStack.size()-2).getOp()+valueStack.get(valueStack.size()-1).getAddr()+" goto "+" ");
			gen("goto ");
			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 30) {// bool -> true
			/*
			 {bool.truelist=makelist(nextinstr); gen('goto_____');}
			 */
			attribute.getTrueList().add(nextInh);
			gen("goto ");
			valueStack.push(attribute);
		}else if (num == 31) {// bool -> false
			/*
			 {bool.falselist=makelist(nextinstr); gen('goto_____');}
			 */
			attribute.getFalseList().add(nextInh);
			gen("goto ");
			valueStack.push(attribute);
		} else if (num == 32) {// bool     → bool1 || L bool2
/*
                    backpatch( bool1.falselist, L.instr );
                    bool.truelist = merge( bool1.truelist, bool2.truelist );
                    bool.falselist = bool2.falselist ;
 */
			backpatch(valueStack.get(valueStack.size()-3).getFalseList(),valueStack.get(valueStack.size()-2).getInstr());
			attribute.setTrueList(merge(valueStack.get(valueStack.size()-3).getTrueList(),valueStack.peek().getTrueList()));
			attribute.setFalseList(valueStack.peek().getFalseList());

			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 33) {
			attribute.setInstr(nextInh);
			valueStack.push(attribute);
		} else if (num == 34) {
			/*
			bool     → bool1 && K bool2 {
                    backpatch( bool1.truelist, K.instr );
                    bool.truelist = bool2.truelist;
                    bool.falselist = merge( bool1.falselist, bool2.falselist );
                }
			 */

			backpatch(valueStack.get(valueStack.size()-3).getTrueList(),valueStack.get(valueStack.size()-2).getInstr());
			attribute.setFalseList(merge(valueStack.get(valueStack.size()-3).getFalseList(),valueStack.peek().getFalseList()));
			attribute.setTrueList(valueStack.peek().getTrueList());

			valueStack.pop();
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 35) {
			attribute.setInstr(nextInh);
			valueStack.push(attribute);
		} else if (num == 36) {
			/*
			bool     → ! bool1 {
                bool.truelist = bool1.falselist;
                bool.falselist = bool1.truelist;
                }
			 */
			attribute.setTrueList(valueStack.peek().getFalseList());
			attribute.setFalseList(valueStack.peek().getTrueList());
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 37) {
			/*
			bool     → (bool1) {
                bool.truelist = bool1.truelist;
                bool.falselist = bool1.falselist;
                }
			 */
			attribute.setTrueList(valueStack.peek().getTrueList());
			attribute.setFalseList(valueStack.peek().getFalseList());
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 38) {
			attribute.setOp("<");
			valueStack.push(attribute);
		} else if (num == 39) {
			attribute.setOp(">");
			valueStack.push(attribute);
		} else if (num == 40) {
			attribute.setOp("==");
			valueStack.push(attribute);
		} else if (num == 41) {
			attribute.setOp("!=");
			valueStack.push(attribute);
		} else if (num == 42) {
			attribute.setOp("<=");
			valueStack.push(attribute);
		} else if (num == 43) {
			attribute.setOp(">=");
			valueStack.push(attribute);
		} else if (num == 44) {

			if (typeChange()){
				attribute.setType("float");
			}else {
				attribute.setType(valueStack.peek().getType());
			}
			attribute.setAddr("t"+ tNumber);
			tNumber++;
			gen(attribute.getAddr()+"="+valueStack.get(valueStack.size()-2).getAddr()+"+"+valueStack.get(valueStack.size()-1).getAddr());

			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		}else if (num == 45) {
			if (typeChange()){
				attribute.setType("float");
			}else {
				attribute.setType(valueStack.peek().getType());
			}
			attribute.setAddr("t"+ tNumber);
			tNumber++;
			gen(attribute.getAddr()+"="+valueStack.get(valueStack.size()-2).getAddr()+"-"+valueStack.get(valueStack.size()-1).getAddr());
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		} else if (num == 47) {
			if (typeChange()){
				attribute.setType("float");
			}else {
				attribute.setType(valueStack.peek().getType());
			}
			attribute.setAddr("t"+ tNumber);
			tNumber++;
			gen(attribute.getAddr()+"="+valueStack.get(valueStack.size()-2).getAddr()+"*"+valueStack.get(valueStack.size()-1).getAddr());
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		}else if (num == 48) {
			if (typeChange()){
				attribute.setType("float");
			}else {
				attribute.setType(valueStack.peek().getType());
			}
			attribute.setAddr("t"+ tNumber);
			tNumber++;
			gen(attribute.getAddr()+"="+valueStack.get(valueStack.size()-2).getAddr()+"/"+valueStack.get(valueStack.size()-1).getAddr());
			valueStack.pop();
			valueStack.pop();
			valueStack.push(attribute);
		}else if (num == 50) {
			attribute.setAddr("t"+ tNumber);
			tNumber++;
			gen(attribute.getAddr()+"="+"-"+valueStack.get(valueStack.size()-1).getAddr());
			valueStack.pop();
			valueStack.push(attribute);
		}else if (num == 51) {
			attribute.setAddr(valueStack.get(valueStack.size()-1).getAddr());
			valueStack.pop();
			valueStack.push(attribute);
		}else if (num == 52) {
			if (checkError(signStack.get(signStack.size()-1))){
				attribute.setType(top.get(signStack.get(signStack.size()-1)));
			}else {
				String a= rowNumber.get(rowcount-1)+"";
				error_write(a,signStack.get(signStack.size()-1)+"没有定义");
				errorlist.addRow(new String[]{ a,signStack.get(signStack.size()-1)+"没有定义"});
				System.out.println(signStack.get(signStack.size()-1)+"没有定义");
			}
			attribute.setAddr(signStack.get(signStack.size()-1));
//			valueStack.pop();
			valueStack.push(attribute);
		}else if (num == 53) {
			if (signStack.get(signStack.size()-1).contains(".")){
				attribute.setType("float");
			}else{
				attribute.setType("int");
			}
			attribute.setAddr(signStack.get(signStack.size()-1));
//			valueStack.pop();
			valueStack.push(attribute);
		}


	}

	private boolean checkError(String s) {

		if (top.containsKey(s)){
			return true;
		}
		return false;
	}

	private boolean typeChange() {
		if (valueStack.peek().getType()==null||valueStack.get(valueStack.size() - 2).getType()==null){
			return false;
		}
		if (!valueStack.peek().getType().equals(valueStack.get(valueStack.size() - 2).getType())){
			if (valueStack.peek().getType().equals("int")){
				gen("t"+tNumber+" = (float)"+valueStack.peek().getAddr());
				valueStack.peek().setAddr("t"+tNumber);
				valueStack.peek().setType("float");
				tNumber++;

			}else {
				gen("t"+tNumber+" = (float)"+valueStack.get(valueStack.size() - 2).getAddr());
				valueStack.get(valueStack.size() - 2).setAddr("t"+tNumber);
				valueStack.get(valueStack.size() - 2).setType("float");
				tNumber++;
			}
			return true;
		}
		return false;
	}

	private void backpatch(List<Integer> nextlist, Integer instr) {
		nextlist = new ArrayList<>(new HashSet<>(nextlist));
		nextlist.forEach(integer -> {
			String s = resultMap.get(integer)+instr;
			resultMap.put(integer,s);
		});
		System.out.println(resultMap);
	}

	private List<Integer> merge(List<Integer> list1,List<Integer> list2){
		list1.addAll(list2);
		return list1;
	}
	private void gen(String s) {
		System.out.println("========================");
		System.out.println(nextInh+":"+"   "+s);
		resultMap.put(nextInh,s);
		System.out.println("========================");
		nextInh++;
	}
	public void error_write(String line,String error)
	{
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("error.txt",true));
			bw.write(line+":"+"   "+error);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void errorRecovery(Action action, int rowcount, ArrayList<String> stack) {
		//错误处理
		int rownumber = -1;
		while(action == null) {
			boolean flag = false;
			if(input_cache.size()==1) {
				if(rownumber != rowNumber.get(rowcount-1)){
					flag = true;
					String a= rowNumber.get(rowcount-1)+1+"";
					if(first){
						first=false;
						a=rowNumber.get(rowcount-1)+"";
					}
					errorlist.addRow(new String[]{ a,"因括号闭合出错缺少定义"});
					error_write(a,"因括号闭合出错缺少定义");
					System.out.println("语法分析出错在第" + rowNumber.get(rowcount - 1) + "行出现error,原因可能是括号闭合出错");
				}
			} else {
				if (rownumber != rowNumber.get(rowcount - 1)) {
					String a= rowNumber.get(rowcount-1)+1+"";
					if(first){
						first=false;
						a=rowNumber.get(rowcount-1)+"";
					}
					errorlist.addRow(new String[]{ a,"因缺少某些符号或遇到了某些错误符号缺少定义"});
					error_write(a,"因缺少某些符号或遇到了某些错误符号缺少定义");
					System.out.println("语法分析出错在第" + rowNumber.get(rowcount - 1) + "行出现error,原因可能是缺少某些符号或遇到了某些错误符号");
				}
			}
			if(flag) {
				input_cache.add("}");
				symbolStack.add("}");
			}
			while(!stack.get(stack.size()-1).equals("{")) {
				if(!(stack.size()==1)) {
					stack.remove(stack.size() - 1);
					signStack.remove(signStack.size()-1);
				}
				if(!(deduce_str.size()==1)) deduce_str.remove(deduce_str.size()-1);
			}
			action=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(input_cache.get(input_cache.size()-1));
			if(action == null) {
				input_cache.remove(input_cache.size() - 1);
				symbolStack.remove(symbolStack.size()-1);
				action = (ActionAndGoto.get(deduce_str.get(deduce_str.size() - 1))).get(input_cache.get(input_cache.size() - 1));
//					if(rownumber != rowNumber.get(rowcount-1)){
//						rownumber = rowNumber.get(rowcount-1);
//						System.out.println("语法分析出错在第" + rowNumber.get(rowcount - 1) + "行出现error,原因可能是出现不希望遇到的符号:"+"\""+str+"\"");
//					}
				action=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(input_cache.get(input_cache.size()-1));
			}
			action=(ActionAndGoto.get(deduce_str.get(deduce_str.size()-1))).get(input_cache.get(input_cache.size()-1));
//				if(rownumber != rowNumber.get(rowcount-1)) {
//					rownumber = rowNumber.get(rowcount-1);
//					System.out.println("语法分析出错在第"+rowNumber.get(rowcount-1)+"行出现error,原因可能是缺失某个符号！");
//				}
		}
	}
	public void error(){
		ActionAndGoto.get(92).get("expr").setAction("");
		ActionAndGoto.get(92).get("expr").setToPorject(54);
		ActionAndGoto.get(91).get("expr").setAction("");
		ActionAndGoto.get(91).get("expr").setToPorject(54);
		ActionAndGoto.get(58).get("expr").setAction("");
		ActionAndGoto.get(58).get("expr").setToPorject(67);

	}
	public String turntostring(ArrayList a)
	{
		String b=new String();
		b="";
		for(int i=0;i<a.size();i++)
		{
			String c=a.get(i)+" ";
			b=b+c;
		}
		//System.out.println(b);
		return b;
	}

}

