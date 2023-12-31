package de.quoss.java.cache.parameter;

public class Parameter {

    private String type;

    private String name;

    private String data0;

    private String data1;

    private String data2;

    private String data3;

    Parameter(final String type, final String name, final String data0, final String data1, final String data2, final String data3) {
        this.type = type;
        this.name = name;
        this.data0 = data0;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }
    
    public String getData0() {
        return data0;
    }

    public String getData1() {
        return data1;
    }

    public String getData2() {
        return data2;
    }

    public String getData3() {
        return data3;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
