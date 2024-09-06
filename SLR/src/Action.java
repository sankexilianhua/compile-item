import java.util.HashMap;

public class Action {
    int toProject;
    String action;
    //默认下其为移入
    public Action(int toProject) {
        this.toProject = toProject;
        action = "s";
    }

    public int getToProject(){
        return this.toProject;
    }

    public void setToPorject(int toProject) {
        this.toProject = toProject;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

