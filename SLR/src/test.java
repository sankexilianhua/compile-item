import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
         String[] str1 = {"E"};
         String[] str2 = {"E"};
        //if(str1.equals("ε")) System.out.println("εandyes");
//        Production production = new Production("dsaf",str1);
//        Production tmp = production.clone();
//        tmp.setIndex(10);
        //System.out.println(production.getIndex());
        //System.out.println(tmp.getIndex());
//        Production production = new Production("S",str1);
//        Production production1 = new Production("S",str2);
//        if(production.compare(production1)) System.out.println("yes");
          AnalyseList  atest = new AnalyseList();
          atest.getactionAndGoto();
          String regex = "^goto.*";
          Pattern p = Pattern.compile(regex);
          String data = "if i<j goto 100";
          Matcher matcher = p.matcher(data);
          if(matcher.find())
          {
              System.out.println(matcher.group());
          }else {
              System.out.println("nothing");
          }
          //Action action = new Action(10);
          //System.out.println(action.getToProject());
//        System.out.println(action.getAction());
//        action.setAction("r");
//        System.out.println(action.getAction());
    }
}
