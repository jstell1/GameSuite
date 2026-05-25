package checkers.model.Rules;

public class Rule {
    Constraint[] constraints;
    Effect[] effects;

    public Rule(Constraint[] constraints, Effect[] effects) {
        this.constraints = constraints;
        this.effects = effects;
    }
}
