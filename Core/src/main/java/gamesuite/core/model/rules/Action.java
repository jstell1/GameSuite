package gamesuite.core.model.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Action {

    private String name;
    private String type;
    private Constraint[] constraints;
    private Effect[] effects;
    private String[] negateList;

    public Action(Constraint[] constraints, Effect[] effects) {
        this.constraints = constraints;
        this.effects = effects;
    }

    public void setNegateList(String[] list) { this.negateList = list; }

    public void setName(String name) { this.name = name; }

    public void setType(String type) { this.type = type; }

    public String getName() { return this.name; }

    public String getType() { return this.type; }

    public List<Constraint> getConstraints() {
        return Arrays.asList(this.constraints);
    }

    public List<Effect> getEffects() {
        return Arrays.asList(this.effects);
    }
}
