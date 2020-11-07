package com.example.babyapp;

public class Base64Encode {
    private String table="ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()abcdefghijklmnopqrstuvwxyz+/";
    private String cipher="";
    private String plain;
    public Base64Encode(String plain){
        this.plain=plain;
    }
    public String getCipher(){
        return this.cipher;
    }
    public void encode(){
        int le=plain.length();
        if (le %3 ==0){
            for(int i=0;i<le;i=i+3){
                this.cipher+=table.charAt((int)plain.charAt(i)>>2);
                this.cipher+=table.charAt((((int)plain.charAt(i)&3)<<4)+((int)plain.charAt(i+1)>>4));
                this.cipher+=table.charAt((((int)plain.charAt(i+1)&0xf)<<2)+((int)plain.charAt(i+2)>>6));
                this.cipher+=table.charAt((int)plain.charAt(i+2)&0x3f);
            }
        }
        else{
            if(le%3==1){
                for(int i=0;i<le-3;i=i+3){
                    this.cipher+=table.charAt((int)plain.charAt(i)>>2);
                    this.cipher+=table.charAt((((int)plain.charAt(i)&3)<<4)+((int)plain.charAt(i+1)>>4));
                    this.cipher+=table.charAt((((int)plain.charAt(i+1)&0xf)<<2)+((int)plain.charAt(i+2)>>6));
                    this.cipher+=table.charAt((int)plain.charAt(i+2)&0x3f);
                }
                this.cipher+=table.charAt((int)plain.charAt(le-1)>>2);
                this.cipher+=table.charAt(((int)plain.charAt(le-1)&3));
                this.cipher+="==";
            }
            else{
                for(int i=0;i<le-3;i=i+3){
                    this.cipher+=table.charAt((int)plain.charAt(i)>>2);
                    this.cipher+=table.charAt((((int)plain.charAt(i)&3)<<4)+((int)plain.charAt(i+1)>>4));
                    this.cipher+=table.charAt((((int)plain.charAt(i+1)&0xf)<<2)+((int)plain.charAt(i+2)>>6));
                    this.cipher+=table.charAt((int)plain.charAt(i+2)&0x3f);
                }
                this.cipher+=table.charAt((int)plain.charAt(le-2)>>2);
                this.cipher+=table.charAt((((int)plain.charAt(le-2)&3)<<4)+((int)plain.charAt(le-1)>>4));
                this.cipher+=table.charAt((int)plain.charAt(le-1)&0xf);
                this.cipher+='=';
            }
        }
    }

}
