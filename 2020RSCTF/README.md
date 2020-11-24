1. RC4
这题其实不难，怪自己对ida一些自定义的函数没有过于深度的了解，加上当时并不知道可以patch内存，只要change byte就可以，不然打表应该也是可以的。
加密过程比较简单，先是一个以encode这个字符串作为密钥的rc4加密，然后就是又经过一个加密函数，这个函数加密流程如下
```
unsigned __int64 __fastcall sub_400CE0(const char *a1, __int64 a2)
{
  const char *v2; // rbx
  unsigned __int64 result; // rax
  unsigned __int64 v4; // rdi
  int v5; // edx
  int v6; // ecx
  int v7; // ecx
  char v8; // r9
  int v9; // edx
  char v10; // r8
  bool v11; // sf
  unsigned __int8 v12; // of
  char v13; // cl
  char v14; // dl

  v2 = a1;
  result = (signed int)strlen(a1);
  if ( (_DWORD)result )
  {
    v4 = 0LL;
    do
    {
      v5 = v2[v4];
      if ( v2[v4] < 0 )
        v5 = 128 - v5;
      v6 = v5 + 15;
      if ( v5 >= 0 )
        v6 = v5;
      v7 = v6 >> 4;
      v8 = v7 + 55;
      v9 = v5 % 16;
      v10 = v7 + 48;
      v12 = __OFSUB__(v7, 10);
      v11 = v7 - 10 < 0;
      v13 = v9 + 48;
      if ( !(v11 ^ v12) )
        v10 = v8;
      v12 = __OFSUB__(v9, 10);
      v11 = v9 - 10 < 0;
      *(_BYTE *)(a2 + 2 * v4) = v10;
      v14 = v9 + 55;
      if ( v11 ^ v12 )
        v14 = v13;
      *(_BYTE *)(a2 + 2 * v4++ + 1) = v14;
    }
    while ( v4 < result );
  }
  return result;
}
```
这明显是不可能逆回去的，只能选择爆破，我卡在一个地方就是__OFSUB__这个地方，其实本质就是两个数字想减，判断是否溢出，溢出返回1，无溢出返回零。  
参考链接：https://blog.csdn.net/Kwansy/article/details/108691085
这里仔细查看其实可以发现，v12和v11那两句代码加上判断的if中的条件，不就是比较大于等于10还是小于10吗？  
所以直接把这段代码copy下来，微调一下，直接开始爆破就能出来了。
```
#include<stdio.h>
int main()
{
    int len=32;
    int v5;
    int v6;
    int v7;
    char v8;
    int v9;
    char v10;
    bool v11;
    char v13;
    char v14;
    char j;
    unsigned long long i=0;
    char a[0x30]={0};
    char res[]="AC6297BD8C53021894A5EADEA1FD9E8A0F0C7845A199FF1C0D8F970DB02B6802";
    int v4=0;
    do{
        for(j=0;j<=0xff;j++)
        {
            v5=j;
            if(j<0)
            {
                v5=128-v5;
            }
            v6=v5+15;
            if(v5>=0)
            v6=v5;
            v7=v6>>4;
            v8=v7+55;
            v9=v5%16;
            v10=v7+48;
            v11=v7<10;
            v13=v9+48;
            if(!v11)
            {
                v10=v8;
            }
            if(v10!=res[2*i])
            {
                continue;
            }
            v14=v9+55;
            v11=v9<10;
            if(v11)
            {
                v14=v13;
            }
            if(v14==res[2*i+1])
            {
                a[i]=j;
                printf("%02hhx",j);
                i++;
                break;
            }

        }
    }while(i<len);
}
```
解出来的东西就是一堆字符串的acsill码值的十六进制。直接用python的库解rc4，然后还可以直接转成字符串
```
from Crypto.Cipher import ARC4
import binascii
key = 'encode'
rc4 = ARC4.new(key)
res =
rc4.decrypt(binascii.unhexlify("d462e9c3f4530218ecdb96a2df83e2f60f0c7845dfe78
11c0df1e90dd02b6802"))
print res
```