# 0x01 you can hide but can't find me
这题还是蛮水的，简单考察了一下随机输入流的概念，RandomAccessFile就是这个，按我的理解，这玩意可以看成一个大型的byte数组，seek（int index）是调整它下标的方法，同时这个输入流既可以当输入也可以当输出，这里就只牵扯到输出，就是写入，具体的就看博客学习吧，
https://blog.csdn.net/qq_31615049/article/details/88562892

老规矩jeb先打开，看下主活动是个啥。
![avatar](https://img2020.cnblogs.com/blog/2021287/202010/2021287-20201031134042263-507628664.png)
然后发现按钮设置了一个点击事件，调用MakeThread.startWrite方法
跟进去一波
![avatar](https://img2020.cnblogs.com/blog/2021287/202010/2021287-20201031134535055-2137198530.png)
发现新建一个随机输入流的文件，然后接下来那么多add方法中的操作，其实就是往这个文件中写入对应的字符，不同的是，写入是有时间控制的
![avatar](https://img2020.cnblogs.com/blog/2021287/202010/2021287-20201031134816686-978268312.png)
 所以flag应该是根据时间顺序来拼接就好了，

Aol jsvjrdvyr ohz ybzalk Puav h zapmm tvklyu hya zahabl, Whpualk if uhabyl, svhaolk if aol Thzzlz, huk svclk if aol mld. Aol nlhyz zjylht pu h mhpslk ylcpchs: HOL17{IlaalyJyfwaZ4m3vyKpl}

弄出来是这个东西，quipquip安排上，一把梭就出来了

# 0x02 AES-Decrypt
这题感觉有点脑洞啊。。晕，不过感觉这题成功让我把jni又复习了一遍，惊奇的发现本质不就是反射那套东西吗，还算是有收获的，然后就是自己的arm汇编简直就是shit，动态只能源码级调，虽然这题还是蛮好用的233

jeb先打开，看看主要逻辑在哪
![avatar](https://img2020.cnblogs.com/blog/2021287/202010/2021287-20201031170121565-746171758.png)
发现将mContext对象传入一个native方法了，ida安排上，同时发现没有jni_onload方法，说明是静态注册的，直接搜Java_类名_native方法名，找到了主要逻辑
![avatar](https://img2020.cnblogs.com/blog/2021287/202010/2021287-20201031170635109-1457840664.png)
![avatar](https://img2020.cnblogs.com/blog/2021287/202010/2021287-20201031170429724-1129773799.png)
 这里其实挺操蛋的，本来java层几行可以解决的问题，在so层就得这么多行。上面一大堆，其实从后面可以看出DA3E0BB0这个函数其实就是做的一个base64加aes解密，这里我也没管，

就是黑盒来用，发现上面一大堆只是为了获取app的签名而已，由于这是java的数组，如果c++要使用需要转换成本地数组，转换后，作为aes的key，iv已经给了，对那个字符串进行解密

至于怎么看出DA3E09E4和58函数的作用的，第一个是发现输入的字符串后，会发生变化，于是跟进去，发现里面有base64字样，只能动调，发现就是base64，然后下一个函数

基本可以从题目中猜出来，一个key和一个iv，恰好符合，剩下其实我也迷茫了，后面看了wp才知道，是需要照猫画虎，将app显示的字符串，按同样的key和iv解密就好了

```
from Crypto.Cipher import AES
import base64
iv=[  0x99, 0x3F, 0x76, 0x06, 0xA7, 0x88, 0x1C, 0x67, 0x49, 0x27,
  0x66, 0x06, 0x8D, 0xE9, 0xD8, 0xAA
]
key=[
  0x00, 0xC2, 0xB2, 0x00, 0xCB, 0xAF, 0xED, 0x4E, 0x14, 0xB3,
  0x52, 0x93, 0x56, 0x36, 0xF6, 0x00, 0x65, 0x7C, 0x40, 0x6C,
  0xBB, 0x8F, 0x29, 0xC0, 0xA1, 0x4F, 0xBB, 0xEC, 0xB6, 0xAD,
  0x8E, 0x54
]
data="+NvwsZ48j3vyDIaMu6LrjnNn8/OAnexGUXn3POeavI8="
flag="T9WoXhrsQHgY3NLr8SwBbw=="
iv=''.join(chr(a) for a in iv)
key=''.join(chr(a) for a in key)
cipher=AES.new(key,AES.MODE_CBC,iv)
plain_data=cipher.decrypt(base64.b64decode(data))
print plain_data
cipher=AES.new(key,AES.MODE_CBC,iv)
plain_data=cipher.decrypt(base64.b64decode(flag))
print plain_data
```