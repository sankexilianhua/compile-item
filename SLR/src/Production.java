import java.util.ArrayList;
import java.util.HashMap;


// 产生式类
public class Production{
	String left; //产生式左部，为非终结符
	String[] right; //产生式右部，除去非终结符和空，其余为终结符

	int index; //用于记录.的位置
	int state; //记录是否是归约项 1为规约项，0为移入项

	//boolean originate; //记录是否是原始式

	HashMap<String, Integer> table; //action列

	public Production(String left, String[] right){
		this.left = left;
		this.right = right;
		this.index = 0; //初始时.的位置为前项
		this.state = 0; //初始时为移入项
		//this.originate = true;
	}

//	public void setOriginate(boolean originate){
//		this.originate = originate;
//	}

//	public boolean getOriginate() {
//		return this.originate;
//	}

	public void setIndex(int index){
		this.index = index;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getIndex() {
		return index;
	}

	public int getState() {
		return state;
	}
	public String[] returnRights(){
		return right;
	}
	
	public String returnLeft(){
		return left;
	}

	public String returnToString() {
		String str = "";
		int i;
		for(i = 0; i < right.length; i++) {
			if(i == index) {
				str += ". ";
				if(i!= right.length)
				str+=right[i]+" ";
			} else {
				str+=right[i]+" ";
			}
		}
		if(i==index) str+=" .";
		return str;
	}

	public Production clone() {
		Production tmp = new Production(this.left,this.right);
		tmp.index = this.index;
		return tmp;
	}

	public boolean compare(Production production) {
		if(production.getIndex()==this.index&& production.returnLeft().equals(this.left) && compareRights(production.returnRights())) {
			return true;
		}
		return false;
	}

	public boolean compareRights(String[] str) {
		int len = str.length;
		int mylen = this.right.length;
		if(len!=mylen) return false;
		for(int i = 0; i < len ; i++) {
			if(!str[i].equals(this.right[i])) return false;
		}
		return true;
	}
}
