package gamesuite.core.model.rules;

public class Result {
    public boolean passed;

    public Result(boolean passed) {
        this.passed = passed;
        
    }

    public boolean getPassed() { return this.passed; }
}
