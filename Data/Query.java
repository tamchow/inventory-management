package Data;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("unused")
public class Query {
    private ArrayList<String> lines;

    public Query() {
        lines = new ArrayList<>();
    }

    public void add(String line) {
        lines.add(line);
    }

    public void remove(String line) {
        lines.remove(line);
    }

    public String getLine(int idx) {
        return lines.get(idx);
    }

    public void setLine(int idx, String line) {
        lines.set(idx, line);
    }

    public String[] getQuery() {
        return lines.toArray(new String[lines.size()]);
    }

    public void setQuery(ArrayList<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    public void setQuery(String[] lines) {
        this.lines.clear();
        Collections.addAll(this.lines, lines);
    }
}