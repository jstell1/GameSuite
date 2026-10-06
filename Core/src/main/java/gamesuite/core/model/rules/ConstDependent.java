package gamesuite.core.model.rules;

import java.util.List;
import java.util.Map;

public interface ConstDependent {
    public void addDependencies(Map<String, Constraint> dependencies);
    public List<String> getDependencyList();
}
