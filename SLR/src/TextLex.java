import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.DefaultTableModel;


public class TextLex{

    private String inputText;
   // private DefaultTableModel tbmodel_lex_result;
    private DefaultTableModel signTable;
    private DefaultTableModel errorList;
    private ArrayList<String> lex_result_stack;
    private ArrayList<HashMap<String, String>> lex_error_stack;
    private int textLength;
    private int rownumber=1;

    public ArrayList<Integer> rowNumber;

    public static ArrayList<String> symbolStack;

    public ArrayList<String> get_Lex_Result(){
        return lex_result_stack;
    }
    public ArrayList<HashMap<String, String>> get_Lex_Error() {
        // TODO Auto-generated constructor stub
        return lex_error_stack;
    }
    String[] keyWords = {"break","continue","void", "int", "long", "double","char","true","false","float", "else", "if", "return", "for", "goto", "short", "static", "while", "do"};

    //记录是否是数字
    ArrayList<String> number = new ArrayList<>();
    public TextLex(String text, DefaultTableModel signTable, DefaultTableModel tb_lex_error){
        this.inputText = text;
        this.signTable = signTable;
        this.errorList = tb_lex_error;
        this.symbolStack=symbolStack;
        textLength = text.length();
        symbolStack=new ArrayList<>();
        lex_result_stack = new ArrayList<String>();
        lex_error_stack = new ArrayList<HashMap<String, String>>();
        rowNumber = new ArrayList<Integer>();
    }
    //是否是字母
    public boolean isLetter(char c){
        if(((c<='z')&&(c>='a')) || ((c<='Z')&&(c>='A')) || (c=='_'))
            return true;
        return false;
    }
    //是否是数字
    public boolean isDigit(char c){
        if((c>='0')&&(c<='9'))
            return true;
        return false;
    }
    //是否是关键字
    public int checkKeyword(String t){
        for(int i = 0; i< keyWords.length; i++){
            if (t.equals(keyWords[i])) {
                return i;
            }
        }
        // 只是普通的标识符
        return -1;
    }

    // 处理整个字符串
    public void scanEntireText() throws IOException {
//        File file2 = new File("experiment1\\sample.out");
//        if (!file2.exists()){
//            file2.createNewFile();
//        }
        int i=0;
        char c;
        inputText = inputText +'\0'; //+终止符
        while(i< textLength){ //主处理程序
            c = inputText.charAt(i);
            //过滤空格和制表符
            if(c==' '||c=='\t')
                i++;
            else if (c=='\r'||c=='\n') { //有回车或换行，意味着有换行，要行数要+1，并且不做处理
                rownumber++;
                i++;
            }
            else
                i= scanSection(i);
        }
    }

    public int scanSection(int index) throws IOException {
        int i=index;
        char ch = inputText.charAt(i);
        String s="";
        // 第一个输入的字符是字母
        if (isLetter(ch)) {
            s = ""+ch;
            rowNumber.add(rownumber);
            return handleFirstLetter(i, s);
        }
        // 第一个是数字的话
        else if (isDigit(ch)) {
            s = ""+ch;
            rowNumber.add(rownumber);
            return handleFirstDigit(i, s);

        }
        // 既不是既不是数字也不是字母
        else {
            rowNumber.add(rownumber);
            s = ""+ch;
            switch (ch) {
//			case ' ':
//			case '\n':
//			case '\r':
//			case '\t':
//				return ++i;
                case '[':
                case ']':
                case '(':
                case ')':
                case '{':
                case '}':
                    printResult(s, "双界符");
                    return ++i;
                case ':':
                    if(inputText.charAt(i+1)==':'){
                        s = s+":";
                        printResult(s, "流标识符");
                        return i+2;
                    }
                    else {
                        printResult(s, "冒号");
                        return i+1;
                    }
                case ',':
                case '.':
                case ';':
                    printResult(s, "单界符");
                    return ++i;
                case '\\':
                    if(inputText.charAt(i+1)=='n'|| inputText.charAt(i+1)=='t'|| inputText.charAt(i+1)=='r'){
                        printResult(s+ inputText.charAt(i+1), "转义");
                        return i+2;
                    }
                case '\'':
                    // 判断是否为单字符，否则报错
                    return handleCharacter(i, s);
                case '\"':
                    // 判定字符串
                    return handleStringLiteral(i, s);
                case '+':
                    return handlePlusSign(i, s);
                case '-':
                    return handleMinusSign(i, s);
                //TODO
                case '*':
                    ch = inputText.charAt(++i);
                    if (ch=='=') {
                        // 输出运算符
                        s = s+ch;
                        printResult(s, "运算符");
                        return ++i;
                    }
                    else{
                        // 输出运算符
                        printResult(s, "运算符");
                        return i;
                    }
                    //TODO
                case '/':
                    if(inputText.charAt(i+1)=='=') {
                        // 输出运算符
                        s = s+ inputText.charAt(i+1);
                        printResult(s, "运算符");
                        return i+2;
                    }
                    else if(inputText.charAt(i+1)=='*'){
                        return handleMultilineComment(i, s);
                    }
                    else if (inputText.charAt(i+1)=='/') {
                        return handleSingleLineComment(i,s);
                    }else if (inputText.charAt(i+1)==' '|| inputText.charAt(i+1)=='\0'|| inputText.charAt(i+1)=='\n'|| inputText.charAt(i+1)=='\r'){
                        printResult(s, "运算符");
                        return ++i;
                    }
                case '!':
                    ch = inputText.charAt(++i);
                    if (ch=='=') {
                        // 输出运算符
                        s = s+ch;
                        printResult(s, "比较运算符");
                        return ++i;
                    }
                    else{
                        // 输出运算符
                        System.out.println(s);
                        printResult(s, "取反运算符");
                        return i;
                    }
                case '=':
                    ch = inputText.charAt(++i);
                    if (ch=='=') {
                        // 输出运算符
                        s = s+ch;
                        printResult(s, "比较运算符");
                        return ++i;
                    }
                    else{
                        // 输出运算符
                        printResult(s, "赋值运算符");
                        return i;
                    }
                case '|':
                    ch = inputText.charAt(++i);
                    if (ch=='|') {
                        // 输出运算符
                        s = s+ch;
                        printResult(s, "逻辑运算符");
                        return ++i;
                    }else {
                        printError(rownumber,s,"暂时无法识别的标识符");
                        return ++i;
                    }
                case '&':
                    ch = inputText.charAt(++i);
                    if (ch=='&') {
                        // 输出运算符
                        s = s+ch;
                        printResult(s, "逻辑运算符");
                        return ++i;
                    } else {
                        printResult(s,"取址运算符");
                        return ++i;
                    }
                case '>':
                    return handleGreaterSign(i, s);
                case '<':
                    return handleLesserSign(i, s);
                case '%':
                    ch = inputText.charAt(++i);
                    if (ch=='=') {
                        // 输出运算符
                        s = s+ch;
                        printResult(s, "运算符");
                        return ++i;
                    }
                    else if(ch=='s'||ch=='c'||ch=='d'||ch=='f'||ch=='l'){
                        // 输出类型标识符
                        s = s+ch;
                        printResult(s, "输出类型标识符");
                        return ++i;
                    }
                    else {
                        // 输出求余标识符
                        printResult(s, "求余标识符");
                        return i;
                    }
                case '#':
                    printResult(s, "预处理标识符");
                    return ++i;
                default:
                    // 输出暂时无法识别的字符,制表符也被当成了有问题的字符
                    printError(rownumber, s, "暂时无法识别的标识符");
                    return ++i;
            }
        }
    }

    public int handleFirstLetter(int indexStart, String str) throws IOException {
        int i=indexStart;
        String s = str;
        char ch= inputText.charAt(++i);
        while(isLetter(ch) || isDigit(ch)){
            s = s+ch;
            ch= inputText.charAt(++i);
        }
        int temp;
        // 到了结尾
        if((temp = checkKeyword(s))>=0){
            // 输出key
            printResult(s, "关键字");
            return i;

        }
        else {
            //TODO
            //标识符的分类
            if(ch == '[') printResult(s, "数组名");
            else if(ch == '(') printResult(s, "函数名");
                // 输出普通的标识符
            else{
                printResult(s, "变量名");
            }
            return i;
        }
    }

    public int handleFirstDigit(int indexStart, String str) throws IOException {
        int i = indexStart;
        char ch = inputText.charAt(++i);
        String s = str;
        while(isDigit(ch)){
            s = s+ch;
            ch = inputText.charAt(++i);
        }
        if((inputText.charAt(i)==' ')||(inputText.charAt(i)=='\t')||(inputText.charAt(i)=='\n')||(inputText.charAt(i)=='\r')||(inputText.charAt(i)=='\0')||(inputText.charAt(i)=='}')||(inputText.charAt(i)==']')||(inputText.charAt(i)==')')||ch==';'||ch==','){
            // 到了结尾，输出数字
            number.add(s);
            printResult(s, "整数");
            return i;
        }
        else if (ch=='E'||ch=='e') {
            //TODO
            if (inputText.charAt(i+1)=='+'|| inputText.charAt(i+1)=='-') {
                s = s+ch;
                ch = inputText.charAt(++i);
                s = s+ch;
                ch = inputText.charAt(++i);
                while (isDigit(ch)) {
                    s = s+ch;
                    ch = inputText.charAt(++i);
                }
                //TODO
                if(ch=='\r'||ch=='\n'||ch==';'||ch=='\t'||ch==' '||ch==0||(ch=='}')||(ch==']')||(ch==')')||ch==','){
                    printResult(s, "科学计数");
                    return i;
                }
                else {
                    printError(i, s, "浮点数错误");
                    return i;
                }
            }
            else if (isDigit(inputText.charAt(i+1))) {
                s = s+ch;
                ch = inputText.charAt(++i);
                while (isDigit(ch)) {
                    s = s+ch;
                    ch = inputText.charAt(++i);
                }
                //TODO
                if(ch=='\r'||ch=='\n'||ch==';'||ch=='\t'||ch==' '||ch==0||(ch=='}')||(ch==']')||(ch==')')||ch==','){
                    printResult(s, "科学计数");
                    return i;
                }
                else {
                    printError(rownumber, s, "浮点数错误");
                    return i;
                }
            }
            else {
                printError(rownumber, s, "科学计数法错误");
                return ++i;
            }
        }

        // 浮点数判断
        else if (inputText.charAt(i)=='.'&&(isDigit(inputText.charAt(i+1)))) {
            s = s +'.';
            ch = inputText.charAt(++i);
            while (isDigit(ch)) {
                s = s+ch;
                ch = inputText.charAt(++i);
            }
            if (ch=='E'||ch=='e') {
                //TODO
                if (inputText.charAt(i+1)=='+'|| inputText.charAt(i+1)=='-') {
                    s = s+ch;
                    ch = inputText.charAt(++i);
                    s = s+ch;
                    ch = inputText.charAt(++i);
                    while (isDigit(ch)) {
                        s = s+ch;
                        ch = inputText.charAt(++i);
                    }
                    //TODO
                    if(ch=='\r'||ch=='\n'||ch==';'||ch=='\t'||ch==' '||ch=='\0'||(ch=='}')||(ch==']')||(ch==')')||ch==','){
                        printResult(s, "科学计数");
                        System.out.println(i);
                        return i;
                    }
                    else {
                        printError(i, s, "浮点数错误");
                        return i;
                    }
                }
                else if (isDigit(inputText.charAt(i+1))) {
                    s = s+ch;
                    ch = inputText.charAt(++i);
                    while (isDigit(ch)) {
                        s = s+ch;
                        ch = inputText.charAt(++i);
                    }
                    //TODO
                    if(ch=='\r'||ch=='\n'||ch==';'||ch=='\t'||ch==' '||ch=='\0'||(ch=='}')||(ch==']')||(ch==')')||ch==','){
                        printResult(s, "科学计数");
                        return ++i;
                    }
                    else {
                        printError(rownumber, s, "浮点数错误");
                        return i;
                    }
                }
                else {
                    printError(rownumber, s, "科学计数法错误");
                    return ++i;
                }
            }
            //TODO
            else if (ch=='\n'||ch=='\r'||ch=='\t'||ch==' '||ch=='\0'||ch!=','||ch!=';'||(ch=='}')||(ch==']')||(ch==')')||ch==',') {
                printResult(s, "浮点数");
                return i;
            }
            //TODO
            else if (ch=='+'||ch=='-'||ch=='*'||ch=='/'||ch=='\0'||(ch=='}')||(ch==']')||(ch==')')||ch==','||ch=='=') {
                printResult(s, "浮点数");
                return i;
            }
            else {
                while (ch!='\n'&&ch!='\t'&&ch!=' '&&ch!='\r'&&ch!='\0'&&ch!=';'&&ch!='.'&&ch!=',') {
                    s = s+ch;
                    ch = inputText.charAt(++i);
                }
                printError(rownumber, s, "不合法的字符");
                return i;
            }
        }
        else if (ch=='+'||ch=='-'||ch=='*'||ch=='/'||ch=='\0'||ch=='=') {
            printResult(s, "整数");
            return i;
        }
        else {
            do {
                ch = inputText.charAt(i++);
                s = s+ch;
            } while ((inputText.charAt(i)!=' ')&&(inputText.charAt(i)!='\t')&&(inputText.charAt(i)!='\n')&&(inputText.charAt(i)!='\r')&&(inputText.charAt(i)!='\0'));
            printError(rownumber, s, "错误的标识符");
            return i;
        }
    }
    public int handleCharacter(int indexStart, String str) throws IOException {
        String s = str;
        int i = indexStart;
        char ch = inputText.charAt(++i);
        while(ch!='\''){
            if (ch=='\r'||ch=='\n') {
                rownumber++;
            }
            else if(ch=='\0'){
                printError(rownumber, s, "单字符错误");
                return i;
            }
            s = s+ch;
            ch = inputText.charAt(++i);
        }
        s = s+ch;
        System.out.println(s);
        if (s.length()==3||s.equals("\'"+"\\"+"t"+"\'")||s.equals("\'"+"\\"+"n"+"\'")||s.equals("\'"+"\\"+"r"+"\'")) {
            printResult(s, "单字符");
        }
        else
            printError(rownumber, s, "字符溢出");
        return ++i;
    }

    // 单行注释处理
    public int handleSingleLineComment(int indexStart, String str) throws IOException {
        String s = str;
        int i = indexStart;
        char ch = inputText.charAt(++i);
        while (ch!='\r'&&ch!='\n'&&ch!='\0') {
            s = s+ch;
            ch = inputText.charAt(++i);
        }
        printResult(s, "单行注释");
        return i;
    }

    // 字符串处理
    public int handleStringLiteral(int indexStart, String str) throws IOException {
        String s = str;
        int i=indexStart;
        char ch = inputText.charAt(++i);
        while(ch!='"'){
            if (ch=='\r'||ch=='\n') {
                rownumber++;
            }
            else if(ch=='\0'){
                printError(rownumber, s, "字符串没有闭合");
                return i;
            }
            s = s+ch;
            ch = inputText.charAt(++i);
        }
        s = s+ch;
        printResult(s, "字符串");
        return ++i;
    }

    public int handlePlusSign(int indexStart, String str) throws IOException {
        int i=indexStart;
        char ch = inputText.charAt(++i);
        String s = str;
        if (ch=='+'){
            // 输出运算符
            s = s+ch;
            printResult(s, "运算符");
            return ++i;
        }

        else if(ch=='='){
            s = s+ch;
            // 输出运算符
            printResult(s, "运算符");
            return ++i;
        }
        else{
            // 输出运算符
            printResult(s, "运算符");
            return i;
        }
    }

    // 处理注释,没有考虑不闭合的情况
    public int handleMultilineComment(int indexStart, String str) throws IOException {
        int i = indexStart;
        char ch= inputText.charAt(++i);
        String s = str+ch;
        ch = inputText.charAt(++i);
        while (ch!='*' || ((i+1)< textLength) && inputText.charAt(i+1)!='/') {
            if (ch=='\r'||ch=='\n') {
                rownumber++;
            }
            else if (ch=='\0') {
                printError(rownumber, s, "注释没有闭合");
                return i;
            }else {
                s = s+ch;
            }
            ch = inputText.charAt(++i);
        }
        s = s+"*/";
        printResult(s, "注释");
        return i+2;
    }

    // 处理减号
    public int handleMinusSign(int indexStart, String str) throws IOException {
        int i=indexStart;
        char ch = inputText.charAt(++i);
        String s = str;
        if (ch=='-'){
            s = s+ch;
            // 输出运算符
            printResult(s, "运算符");
            return ++i;
        }

        else if(ch=='='){
            s = s+ch;
            // 输出运算符
            printResult(s, "运算符");
            return ++i;
        }
        else{
            // 输出运算符
            printResult(s, "运算符");
            return i;
        }
    }

    public int handleGreaterSign(int indexStart, String str) throws IOException {
        int i=indexStart;
        char ch = inputText.charAt(++i);
        String s = str;
        if (ch=='='){
            s = s+ch;
            // 输出运算符
            printResult(s, "比较运算符");
            return ++i;
        }

        else if(ch=='>'){
            s = s+ch;
            // 输出运算符
            printResult(s, "运算符");
            return ++i;
        }
        else{
            // 输出运算符
            printResult(s, "比较运算符");
            return i;
        }
    }

    public int handleLesserSign(int indexStart, String str) throws IOException {
        int i=indexStart;
        String s = str;
        char ch = inputText.charAt(++i);
        if (ch=='='){
            s = s+ch;
            // 输出运算符
            printResult(s, "比较运算符");
            return ++i;
        }

        else if(ch=='<'){
            s = s+ch;
            // 输出运算符
            printResult(s, "运算符");
            return ++i;
        }
        else{
            // 输出运算符
            printResult(s, "比较运算符");
            return i;
        }
    }

    // 打印结果
    public void printResult(String rs_value, String rs_name){
        symbolStack.add(rs_value);
//
        if(rs_name.equals("数组名")||rs_name.equals("函数名")||rs_name.equals("变量名")){
            lex_result_stack.add("id");
            signTable.addRow(new String[]{"id", rs_value});
        }
        else if(rs_name.equals("整数")){
            lex_result_stack.add("num");
            signTable.addRow(new String[]{"num", rs_value});
        }
        else if (rs_name.equals("科学计数")||rs_name.equals("浮点数")) {
            lex_result_stack.add("num");
            signTable.addRow(new String[]{"num", rs_value});
        }
        else if(rs_name.equals("单字符")){
            lex_result_stack.add("char");
            signTable.addRow(new String[]{"char", rs_value});
        }
        else if(rs_name.equals("字符串")){
            lex_result_stack.add("str");
            signTable.addRow(new String[]{"str", rs_value});
        }
        else {
            if (rs_value.equals("true")||rs_value.equals("false"))
            {
                lex_result_stack.add(rs_value);
                signTable.addRow(new String[]{rs_name, rs_value});
            }
            else if(rs_value.equals("break")) {
                lex_result_stack.add("break");
                signTable.addRow(new String[]{rs_name, rs_value});
            }
            else {
                lex_result_stack.add(rs_value);
                signTable.addRow(new String[]{rs_name, rs_value});
            }
        }

    }

    // 打印错误信息
    public void printError(int row_num, String rs_value, String rs_name) {
//		tbmodel_lex_error.addRow(new String[]{row_num+"", rs_value, rs_name});
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("row_num", row_num+"");
        hashMap.put("rs_value", rs_value+"");
        hashMap.put("rs_name", rs_name+"");
        lex_error_stack.add(hashMap);
        signTable.addRow(new String[]{"ERROR，"+rs_name, rs_value});
    }


    public ArrayList<String> getSymbolStack() {
        return symbolStack;
    }
}