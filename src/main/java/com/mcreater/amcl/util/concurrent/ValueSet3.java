package com.mcreater.amcl.util.concurrent;

public class ValueSet3<K1, K2, K3>{
    final K1 value1;
    final K2 value2;
    final K3 value3;
    public ValueSet3(K1 k1, K2 k2, K3 k3){
        value1 = k1;
        value2 = k2;
        value3 = k3;
    }

    public K1 getValue1() {
        return value1;
    }

    public K2 getValue2() {
        return value2;
    }

    public K3 getValue3() {
        return value3;
    }
}
