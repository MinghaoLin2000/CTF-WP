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
