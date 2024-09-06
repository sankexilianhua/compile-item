import java.util.ArrayList;
import java.util.List;

public class Attribute {

    private List<String> nextList;

    private List<Integer> trueList;

    private List<Integer> falseList;
    private List<Integer> breakList;

    private Integer lineWith;

    public Integer getLineWith() {
        return lineWith;
    }

    public void setLineWith(Integer lineWith) {
        this.lineWith = lineWith;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "nextList=" + nextList +
                ", trueList=" + trueList +
                ", falseList=" + falseList +
                ", breakList=" + breakList +
                ", continueList=" + continueList +
                ", op='" + op + '\'' +
                ", arrayName='" + arrayName + '\'' +
                ", array='" + array + '\'' +
                ", width=" + width +
                ", type='" + type + '\'' +
                ", addr='" + addr + '\'' +
                ", instr=" + instr +
                ", nextlist=" + nextlist +
                '}';
    }

    public List<Integer> getContinueList() {
        return continueList;
    }

    public void setContinueList(List<Integer> continueList) {
        this.continueList = continueList;
    }

    private List<Integer> continueList;

    private String op;

    private String arrayName;

    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    private String array;

    public String getArray() {
        return array;
    }

    public void setArray(String array) {
        this.array = array;
    }

    public List<Integer> getBreakList() {
        return breakList;
    }

    public void setBreakList(List<Integer> breakList) {
        this.breakList = breakList;
    }

    private Integer width;

    private String type;

    private String addr;

    private Integer instr;

    private List<Integer> nextlist;

    public List<String> getNextList() {
        return nextList;
    }

    public void setNextList(List<String> nextList) {
        this.nextList = nextList;
    }

    public List<Integer> getTrueList() {
        return trueList;
    }

    public void setTrueList(List<Integer> trueList) {
        this.trueList = trueList;
    }

    public List<Integer> getFalseList() {
        return falseList;
    }

    public void setFalseList(List<Integer> falseList) {
        this.falseList = falseList;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Integer getInstr() {
        return instr;
    }

    public void setInstr(Integer instr) {
        this.instr = instr;
    }

    public List<Integer> getNextlist() {
        return nextlist;
    }

    public void setNextlist(List<Integer> nextlist) {
        this.nextlist = nextlist;
    }

    public Attribute(List<String> nextList, List<Integer> trueList, List<Integer> falseList, String op, Integer width, String type, String addr, Integer instr, List<Integer> nextlist) {
        this.nextList = nextList;
        this.trueList = trueList;
        this.falseList = falseList;
        this.op = op;
        this.width = width;
        this.type = type;
        this.addr = addr;
        this.instr = instr;
        this.nextlist = nextlist;
    }

    public Attribute() {
        this.nextList = new ArrayList<>();
        this.trueList = new ArrayList<>();
        this.falseList =new ArrayList<>();
        this.nextlist = new ArrayList<>();
        this.breakList = new ArrayList<>();
        this.continueList = new ArrayList<>();
    }
}
