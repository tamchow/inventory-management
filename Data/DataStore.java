package Data;

import java.util.ArrayList;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DataStore {
    public final ArrayList<Database> tables;
    public final ArrayList<Query> queries;

    public DataStore() {
        tables = new ArrayList<>();
        queries = new ArrayList<>();
    }
}