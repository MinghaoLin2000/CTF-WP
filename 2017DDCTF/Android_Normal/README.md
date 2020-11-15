蛮水的，jeb打开，发现输入的值，和一个无参数的native函数的返回值相等就corret，那就这不就easy吗，可惜我目前的frida native层还不会，不然直接主动调用一手，不就可以把结果dump出来吗2333.
这里讲下我动态调试的手法
1. jeb3打开，发现关键代码在native层，打开ida，把so文件放进去，至于选什么架构的，我优先选的是64位的，因为我调试的pixel是64位的，x86要用模拟器动调，有点麻烦
2. 把本地端口和手机端口联通一下，adb forward tcp:23946 tcp:23946，然后那边将androidserver64打开，这里先开启remote动调（主要是把ip固定上），这里会报错，因为so在这种情况下，无法独自运行，所以我们这里选择attach上去
3. 打开之后，发现是静态注册，静态注册就是指的native函数在so文件中，是以java_xxx包名_native函数名为function的名字，所以很好辨认，动态注册的话，还得找jninativemethod的结构体，jni_onload函数里面去找，扯远了
4. 然后发现里面要调了些别的库的函数，我果断动调，之后就是很老套的异或，之后在内存中，dump出flag。。。

flag:DDCTF-397a90a3267641658bbc975326700f4b@didichuxing.com