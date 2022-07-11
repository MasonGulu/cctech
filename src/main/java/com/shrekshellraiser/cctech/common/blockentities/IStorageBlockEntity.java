package com.shrekshellraiser.cctech.common.blockentities;

public interface IStorageBlockEntity {
    String readChar();
    boolean seekRel(int offset);
    boolean seekAbs(int offset);
    boolean writeChar(char ch);
}
