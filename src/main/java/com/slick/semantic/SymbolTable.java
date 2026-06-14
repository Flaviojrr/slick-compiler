package com.slick.semantic;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    Map<String, Symbol> table = new HashMap<>();

    public void define (Symbol s){
        table.put(s.name, s);
    }
    public Symbol resolve(String name){
        if(contains(name)) return table.get(name);
        return null;
    }
    public boolean contains(String name){
        if(table.containsKey(name)) return true;
        return false;
    }
}
