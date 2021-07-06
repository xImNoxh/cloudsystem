package de.polocloud.database.emurations;

public enum DataType {

    TEXT("text"),
    VARCHAR("varchar", 64),
    LONG("bigint"),
    INT("int");

    private final String sqlTag;
    private int length;

    DataType(String sqlTag) {
        this.sqlTag = sqlTag;
    }

    DataType(String sqlTag, int length){
        this.sqlTag = sqlTag;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public DataType setLength(int length) {
        this.length = length;
        return this;
    }

    public String getSqlTag() {
        if(this == DataType.VARCHAR) return sqlTag + "(" + length + ")";
        else return sqlTag;
    }


}
