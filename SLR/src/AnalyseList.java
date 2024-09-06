
import java.io.*;
import java.lang.reflect.Array;
import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public  class AnalyseList{
	ArrayList<Production> productions; //产生式集
	ArrayList<String> terminals;//终结符集
	ArrayList<String> nonterminals;//非终结符集
	HashMap<String, ArrayList<String>> firsts; //first集
	HashMap<String, ArrayList<String>> follows; //follow集

	HashMap<Integer, ArrayList<Production>> projects; // 项目集 <项目集编号，项目集产生式>
	HashMap<Integer, HashMap<String, Action>> actionAndGoto;

	public  AnalyseList(){
		productions = new ArrayList<Production>();
		terminals = new ArrayList<String>();
		nonterminals = new ArrayList<String>();
		firsts = new HashMap<String, ArrayList<String>>();
		follows = new HashMap<String, ArrayList<String>>();
		projects = new HashMap<Integer, ArrayList<Production>>();
		actionAndGoto = new HashMap<Integer,HashMap<String,Action>>();
		setProductions();
		//showProduction();
		setNonTerminals();
		showNonTerminal();
		setTerminals();
		getFirst();
		showFirst();
		getFollow();
		showFollow();
		getProject();
		//Predict();
		//showProject();
		show();
	}
	public HashMap getactionAndGoto()
	{
		return actionAndGoto;
	}
	public ArrayList getproductions()
	{
		return productions;
	}
	public void setProductions(){
		String filePath = "grammar.txt";
		String encoding = "UTF-8";
		try {
			FileInputStream fis = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fis, encoding);
			BufferedReader br = new BufferedReader(isr);
			String line;
			String left;
			String right;
			Production production;
			while ((line = br.readLine()) != null) {
				left = line.split("->")[0].trim();
				right = line.split("->")[1].trim();
				production = new Production(left, right.split(" "));
				productions.add(production);
			}
			br.close();
			isr.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 获得非终结符集,获取每一个产生式的左部（能在左部一定是非终结符）
	public void setNonTerminals(){
		String filePath = "grammar.txt";
		String encoding = "UTF-8"; // 指定文件编码格式

		try {
			FileInputStream fis = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fis, encoding);
			BufferedReader br = new BufferedReader(isr);
			String line;
			String left;
			String right;
			Production production;
			while ((line=br.readLine())!=null) {
				left = line.split("->")[0].trim();
				if(nonterminals.contains(left)){
					continue;
				}
				else {
					nonterminals.add(left);
				}
			}
			br.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获得终结符集,依赖于获得产生式函数
	public void setTerminals(){
		// 遍历所有的产生式
		String[] rights;
		for (int i = 0; i < productions.size(); i++) {
			rights = productions.get(i).returnRights();
			// 从右侧寻找终结符
			for (int j = 0; j < rights.length; j++) {
				//空是否是终结符？
				//if(nonterminals.contains(rights[j])||rights[j].equals("Îµ")||terminals.contains(rights[j])){
				if(nonterminals.contains(rights[j])||terminals.contains(rights[j])){
						continue;
				}
					else {
						terminals.add(rights[j]);
					}
			}
		}
	}
	
	// 获取First集
	public void getFirst(){
		// 终结符全部求出first集
		ArrayList<String> first;
		for (int i = 0; i < terminals.size(); i++) {
			first = new ArrayList<String>();
			first.add(terminals.get(i));
			firsts.put(terminals.get(i), first);
		}
		// 给所有非终结符注册一下
		for (int i = 0; i < nonterminals.size(); i++) {
			first = new ArrayList<String>();
			firsts.put(nonterminals.get(i), first);
		}

		boolean flag;
		while (true) {
			flag = true;
			String left;
			String right;
			String[] rights;
			for (int i = 0; i < productions.size(); i++) {
				left = productions.get(i).returnLeft(); //String
				rights = productions.get(i).returnRights(); //String[]
				for (int j = 0; j < rights.length; j++) {
					right = rights[j]; //String
					// right是否存在，遇到空怎么办
					//if(!right.equals("ε")){
						//若一开始为first（right）为空，则不会进入该循环
						for (int l = 0; l < firsts.get(right).size(); l++) {
							if(firsts.get(left).contains(firsts.get(right).get(l))){
								continue;
							}
							else {
								firsts.get(left).add(firsts.get(right).get(l));
								flag=false;
							}
						}
					//}
					if (isCanBeNull(right)) {
						continue;
					}
					else {
						break;
					}
				}
			}
			if (flag==true) {
				break;
			}

		}
		// 非终结符的first集
	}
		
	// 获得Follow集
	public void getFollow() {
		// 所有非终结符的follow集初始化一下
		ArrayList<String> follow;
		for (int i = 0; i < nonterminals.size(); i++) {
			follow = new ArrayList<String>();
			follows.put(nonterminals.get(i), follow);
		}
		// 将$加入到follow(program')中
		//follows.get("program'").add("$");
		follows.get(productions.get(0).returnLeft()).add("$");
		boolean flag; //用于判断是否有新的符号加入follow
		boolean fab;
		while (true) {
			flag = true;
			// 循环，遍历所有产生式
			for (int i = 0; i < productions.size(); i++) {
				String left;
				String right;
				String[] rights;
				rights = productions.get(i).returnRights();//返回产生式右部
				for (int j = 0; j < rights.length; j++) {
					right = rights[j]; //当前字符
					// 是 非终结符
					if (nonterminals.contains(right)) {
						fab = true;
						//当前非终结符的下一个
						for (int k = j + 1; k < rights.length; k++) {

							// 查找first集 后一个符号的first集的每一个元素加入follow中
							for (int v = 0; v < firsts.get(rights[k]).size(); v++) {
								// 将后一个元素的first集加入到前一个元素的follow集中,其中空字符不必加入
								if (follows.get(right).contains(firsts.get(rights[k]).get(v))||firsts.get(rights[k]).get(v).equals("ε")) {
									continue;
								} else {
									follows.get(right).add(firsts.get(rights[k]).get(v));
									flag = false;
								}
							}
							//如果当前非终结符的下一个字符的first集可以是ε，那么需要把其下下个字符的first集也加入
							if (isCanBeNull(rights[k])) {
								continue;
							} else {
								fab = false;
								break;
							}
						}
						if (fab) {
							left = productions.get(i).returnLeft();
							for (int p = 0; p < follows.get(left).size(); p++) {
								if (follows.get(right).contains(follows.get(left).get(p))) {
									continue;
								} else {
									follows.get(right).add(follows.get(left).get(p));
									flag = false;
								}
							}
						}
					}

				}
			}
			if (flag == true) {
				break;
			}
		}
	}

	// 求项目集
//	projects = new HashMap<Integer, ArrayList<Production>>();
//	actionAndGoto = new HashMap<Integer,HashMap<String,Action>>();
	public void getProject() {
		//对ε进行处理
		for(int i = 0 ; i < productions.size(); i++){
			Production production = productions.get(i);
			String string = production.returnRights()[0];
			if(string.equals("ε")) {
				production.right[0]="";
				production.setIndex(production.index+1);
			}
		}
		//project的变量
		int count = 0;//项目集数
		int projectCount = 0;//目前总的项目集数
		int solveCount = 0;//正在处理的项目集，同时可以记录已经处理过的项目集数
		int flag = 1; //标记是否是第一次进入求解，第一次无需加入项目集等
		//先将增广文法的首个产生式加入项目集中
		ArrayList<Production> project = new ArrayList<Production>();//用于存储项目集
		project.add(productions.get(0));
		//队列，用来存储每个产生式需要求后继项的产生式
		Queue<Production> queue = new LinkedList<Production>();
		//先将第一个queue加入队列
		queue.add(productions.get(0).clone());
		//创建第一个项目集
		projects.put(count++,project);
		projectCount+=1;
		while (solveCount < projectCount){

			//projectCount-=1;
			if(count != 1)  {
				project = projects.get(solveCount);
				//将一个项目集的所有原始产生式加入到queue
				for(int i = 0 ; i < projects.get(solveCount).size() ; i++) {
					queue.offer(projects.get(solveCount).get(i).clone());
				}
			}
			//对一个项目集的求解
			while(queue.size()>0) {
				//Production production = queue.poll().clone();
				Production production = queue.poll();
				int index = production.getIndex(); // 获取.的位置

				//说明是归约式,没有下一个集，不做处理
				if(index == production.returnRights().length) {
					continue;
				}

				//拿到.后的字符
				String str = production.returnRights()[index];

//				//如果是空，实际上就是规约式，将index+1
//				if(str.equals("ε")) {
//					production.right[0] = "";
//					int myindex = production.index;
//					production.setIndex(myindex+1);
//				}
				//如果是空说明就是归约式
				if(production.right[0].equals("")) {
					continue;
				}

				//若是非终结符，找到其所有的产生式，clone后加入该项目集中
				if(nonterminals.contains(str)) {
					for(int i = 0 ; i < productions.size(); i++) {
						if(productions.get(i).returnLeft().equals(str)) {
							//TODO 让相同的产生式规则不重复加入---完成
							boolean myflag = true;
							for(int j = 0; j < project.size(); j++) {
								if(project.get(j).compare(productions.get(i))) {
									myflag = false;
								}
							}
							if(myflag) {
								project.add(productions.get(i).clone());
								queue.offer(productions.get(i).clone());
							}
						}
					}
				}
				//终结符没有更多产生式则不需处理
			}

			//存储可以转移的符号
			//HashMap<String, Integer> move = new HashMap<String , Integer>();

			boolean[] isVist = new boolean[project.size()]; //默认都为false;
			//对于每个project,开始生成下一个项目集时就可以进行action
			HashMap<String,Action> action = new HashMap<String,Action>();
			//因为衍生式不止一个，所以action要在for外创建
			//求解完一个项目集后，需要对其衍生式进行处理
			for(int i = 0 ; i < project.size(); i++) {
				//标记哪些产生式已经处理过
				if(isVist[i]) continue;
				isVist[i] = true;
				boolean isExist = false;
				Production production = project.get(i);

				//先查看是否已经存在该产生式(如果存在下一个产生式，那么说明一定不是归约项)
				for(int j = 0; j < projects.size() ; j++) { //项目集
					ArrayList<Production> tmp = projects.get(j);
					for(int k = 0 ; k < tmp.size(); k++) { //项目集内包含的产生式
						//.要后移一位
						if((production.getIndex()+1)==tmp.get(k).getIndex()&&production.returnLeft().equals(tmp.get(k).returnLeft())&&production.compareRights(tmp.get(k).returnRights())) {
							//TODO 如果找到了，就直接转移到该状态 --- 完成
							//此时接受的字符仍然为index
							int index = production.getIndex();
							String str = production.returnRights()[index];
							Action actiontmp = new Action(j); //跳转到该项目集
							//如果是非终结符，直接跳转至状态即可
							if(nonterminals.contains(str)) {
								actiontmp.setAction("");
							}
							action.put(str,actiontmp);
							isExist = true;
							break;
						}
					}
					if(isExist) break;
				}

				if(isExist) continue;

				//准备好新project
				ArrayList<Production> newProject = new ArrayList<Production>();
				int index = production.getIndex();
				//是否规约式,是规约式则不再移动.
				if(index == production.returnRights().length) {
					//如果是规约式，需要拿到left的follow集，将归约填到follow集中
					String left = production.returnLeft();
					//left的所有follow集
					ArrayList<String> arrayList = follows.get(left);
					int pindex = getProductionNum(production); //取得产生式集的编号
					for(int j = 0; j < arrayList.size(); j++) {
						Action actiontmp = new Action(pindex);
						actiontmp.setAction("r");
						action.put(arrayList.get(j),actiontmp);
					}
					continue;
				}
				production = production.clone();
				//将.后移一位
				production.setIndex(index+1);
				newProject.add(production);
				String str = production.returnRights()[index]; //拿到的是下一个字符的
				//寻找相同转移字符的产生式，需要将其放到同一个项目集中
				for(int j = 0; j < project.size(); j++) {
					//这时两者是同一产生式
					if(j==i) continue;
					Production production1 = project.get(j);
					int index1 = production1.getIndex();//.对应的下标就是产生式对应的下一个符号
					//如果发现是规约式，就无可能与其他非归约是同等
					if(index1 == production1.returnRights().length) {
						continue;
					}
					String str1 = production1.returnRights()[index1];
					//如果找到相同的，就直接放到相同项目集中
					if(str1.equals(str)) {
						isVist[j]=true;
						production1 = production1.clone();
						int tmp = production1.getIndex();
						production1.setIndex(tmp+1);
						newProject.add(production1);
					}
				}
				//找完后，加入新项目集中
				projects.put(count++,newProject);
				//此时str还是相同，也就是从该项目集接收str时，应该到达该新项目集
				Action actiontmp = new Action(count-1);
				//如果是非终结符，直接跳转至状态即可
				if(nonterminals.contains(str)) {
					actiontmp.setAction("");
				}
				action.put(str,actiontmp);
				projectCount+=1;

			}
			actionAndGoto.put(solveCount,action);
			//处理的项目计数+1
			solveCount++;
			//System.out.println(actionAndGoto);
		}

	}


	// 判断是否是终结符
	public boolean isTerminal(String symbol){
		if(terminals.contains(symbol))
			return true;
		else {
			return false;
		}
	}
	
	// 判断是否产生ε
	public boolean isCanBeNull(String symbol){
		String[] rights;
		for (int i = 0; i < productions.size(); i++) {
			// 找到产生式
			if (productions.get(i).returnLeft().equals(symbol)) {
				rights = productions.get(i).returnRights();
				if (rights[0].equals("ε")) {
					return true;
				}
			}
		}
		return false;
	}

	public int getProductionNum(Production production) {
		int len = productions.size();
		for(int i = 0 ; i < len ; i++) {
			Production compareProduction = productions.get(i);
			if(production.compareRights(compareProduction.returnRights())&&production.returnLeft().equals(compareProduction.returnLeft())) {
				return i;
			}
		}
		return -1;
	}

	public void show() {
		ArrayList<String> arrayList = new ArrayList<String>();
		for(int i = 0; i < terminals.size();i++) {
			arrayList.add(terminals.get(i));
		}
		for(int j = 0 ; j < nonterminals.size() ; j++) {
			arrayList.add(nonterminals.get(j));
		}
		arrayList.add("$");
		try{
			File writeName = new File("output.txt");
			writeName.createNewFile();
			FileWriter writer = new FileWriter(writeName);
			BufferedWriter out = new BufferedWriter(writer);
        for(int i = 0 ; i < actionAndGoto.size(); i++) {
            //System.out.println(i+":");
			out.write(i+":"+'\n');
            for(int j = 0 ; j < arrayList.size(); j++) {
                if(actionAndGoto.get(i).containsKey(arrayList.get(j))) {
					Action action = actionAndGoto.get(i).get(arrayList.get(j));
					String a=arrayList.get(j)+":"+action.getAction()+action.getToProject()+'\n';
					out.write(a);
                    }
                }
			out.write('\n');
            }
			out.write('\n');
			out.flush();
			out.close();
		}catch (IOException e) {
			e.printStackTrace();
		}



		//查看终结符first集
//		System.out.println("查看first集合");
//		for (int i = 0; i < terminals.size(); i++) {
//			System.out.print(firsts.get(terminals.get(i))+" ");
//
//			System.out.println();
//		}


	}
	public void showProduction() {
		//查看产生式
		//for(int i = 0 ; i < productions.size(); i++) {
			//for(int j = 0; j < productions.get(i).right.length; j++)
				//System.out.print(productions.get(i).right[j]+" ");
				//System.out.print(productions.get(i).returnLeft()+"->"+productions.get(i).returnRights()[j]+" ");
			//System.out.println();
		//}
		//System.out.println(productions.size());
	}

	public void showNonTerminal() {
				//查看非终结符
		//for (int i = 0; i < nonterminals.size(); i++) {
			//System.out.print(nonterminals.get(i)+" ");

			//System.out.println();
		//}
	}

	public void showTerminal() {
				//查看终结符
		System.out.println("终结符===============================");
		for (int i = 0; i < terminals.size(); i++) {
			System.out.print(terminals.get(i)+" ");

			System.out.println();
		}
	}

	public void showFirst() {
				//查看非终结符first集
		System.out.println("first集合==============================");
		for (int i = 0; i < nonterminals.size(); i++) {
			System.out.print(nonterminals.get(i)+"的first集:"+firsts.get(nonterminals.get(i))+" ");

			System.out.println();
		}
	}

	public void showFollow() {
				//非终结符follow集
		System.out.println("follow==============================");
		for (int i = 0; i < nonterminals.size(); i++) {
			System.out.print(nonterminals.get(i)+"的follow集:"+follows.get(nonterminals.get(i))+" ");

			System.out.println();
		}
	}

	public void showProject() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("zhuangtaiji.txt"));
			for(int j = 0 ; j < projects.size() ; j++ ) {
//				System.out.println("I"+j+":");
				bw.write("I"+j+":");
				bw.newLine();
				for(int i = 0 ; i < projects.get(j).size();i++){
					bw.write(projects.get(j).get(i).returnLeft()+"->"+projects.get(j).get(i).returnToString());
					bw.newLine();
//					System.out.println(projects.get(j).get(i).returnLeft()+"->"+projects.get(j).get(i).returnToString());
				}

				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
